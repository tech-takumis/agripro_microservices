import { defineStore } from 'pinia'
import { ref } from 'vue'
import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client'
import { useMessageStore } from '@/stores/message'

export const useWebSocketStore = defineStore('websocket', () => {
  const stompClient = ref(null)
  const connected = ref(false)
  const lastMessage = ref(null)
  const manualDisconnect = ref(false)
  const messageStore = useMessageStore()
  let reconnectAttempts = 0
  const MAX_RECONNECT_ATTEMPTS = 5
  let reconnectTimeout = null
  let connectionPromise = null
  let subscription = null

  const subscribeToPrivateMessages = () => {
    const topic = '/user/queue/private.messages'
    if (!stompClient.value?.active) return
    if (subscription) subscription.unsubscribe()

    subscription = stompClient.value.subscribe(topic, (message) => {
      console.log('[WebSocket] 📨 Message received:', message.body)
      lastMessage.value = message.body
      try {
        const data = JSON.parse(message.body)
        messageStore.addIncomingMessage(data)
      } catch (err) {
        console.error('[WebSocket] Error parsing message:', err)
      }
    })
    console.log(`[WebSocket] ✅ Subscribed to ${topic}`)
  }

  const connect = async () => {
    if (connectionPromise) return connectionPromise
    if (stompClient.value?.active && connected.value) return Promise.resolve()

    connectionPromise = new Promise((resolve, reject) => {
      const token = localStorage.getItem('webSocketToken')
      if (!token) {
        console.error('[WebSocket] 🚫 Missing webSocketToken')
        reject('Token missing')
        return
      }

      const client = new Client({
        brokerURL: `ws://localhost:9001/ws?token=${token}`,
          connectHeaders: {},
        debug: (str) => {
          if (str.includes('ERROR') || str.includes('CONNECT')) console.log('[WebSocket Debug]', str)
        },
        reconnectDelay: 0, // manual reconnect
        heartbeatIncoming: 4000,
        heartbeatOutgoing: 4000,

        onConnect: () => {
          connected.value = true
          reconnectAttempts = 0
          console.log('[WebSocket] ✅ Connected to server')
          subscribeToPrivateMessages()
          connectionPromise = null
          resolve()
        },

        onStompError: (frame) => {
          console.error('[WebSocket] ❌ STOMP error:', frame.headers?.message)
          connected.value = false
          connectionPromise = null
          scheduleReconnect()
          reject(new Error(frame.headers?.message))
        },

        onWebSocketError: (event) => {
          console.error('[WebSocket] ❌ WebSocket error:', event)
          connected.value = false
          connectionPromise = null
          scheduleReconnect()
        },

        onDisconnect: () => {
          connected.value = false
          connectionPromise = null
          console.warn('[WebSocket] 🔌 Disconnected')
          if (manualDisconnect.value) {
            manualDisconnect.value = false
            return
          }
          scheduleReconnect()
        }
      })

      client.activate()
      stompClient.value = client
    })

    return connectionPromise
  }

  const scheduleReconnect = () => {
    if (reconnectAttempts >= MAX_RECONNECT_ATTEMPTS) {
      console.warn('[WebSocket] ❌ Max reconnect attempts reached')
      return
    }
    reconnectAttempts++
    const delay = 10_000
    console.log(`[WebSocket] ⏳ Reconnect attempt ${reconnectAttempts}/${MAX_RECONNECT_ATTEMPTS} in ${delay}ms`)
    reconnectTimeout = setTimeout(() => connect(), delay)
  }

  const disconnect = () => {
    if (stompClient.value?.active) {
      manualDisconnect.value = true
      if (subscription) subscription.unsubscribe()
      stompClient.value.deactivate()
      connected.value = false
      console.log('[WebSocket] 🔌 Manual disconnect')
    }
  }

  const sendMessage = (destination, body) => {
    if (!connected.value || !stompClient.value?.active) {
      console.error('[WebSocket] ⚠️ Cannot send: not connected')
      return
    }
    stompClient.value.publish({ destination, body: JSON.stringify(body) })
    console.log(`[WebSocket] 📤 Sent to ${destination}`)
  }

  return { stompClient, connected, lastMessage, connect, disconnect, sendMessage }
})
