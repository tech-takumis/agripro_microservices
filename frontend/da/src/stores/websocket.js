import { defineStore } from 'pinia'
import { ref } from 'vue'
import { Client } from '@stomp/stompjs'
import { useMessageStore } from '@/stores/message'
import { useNotificationStore } from './notification'

export const useWebSocketStore = defineStore('websocket', () => {
  const stompClient = ref(null)
  const connected = ref(false)
  const lastMessage = ref(null)
  const manualDisconnect = ref(false)
  const messageStore = useMessageStore()
  const notificationStore = useNotificationStore()
  let reconnectAttempts = 0
  const MAX_RECONNECT_ATTEMPTS = 5
  let reconnectTimeout = null
  let connectionPromise = null
  let privateMessageSub = null
  let notificationSub = null

  const subscribeToPrivateMessages = () => {
  const topic = '/user/queue/private.messages'
  if (!stompClient.value?.active) return
  if (privateMessageSub) privateMessageSub.unsubscribe()

  privateMessageSub = stompClient.value.subscribe(topic, (message) => {
    console.log('[WebSocket] 📨 Private message:', message.body)
    lastMessage.value = message.body
    try {
      const data = JSON.parse(message.body)

      // Log attachment information if present
      if (data.attachmentResponses && data.attachmentResponses.length > 0) {
        console.log(`[WebSocket] 📎 Message has ${data.attachmentResponses.length} attachment(s):`,
          data.attachmentResponses.map(att => ({ documentId: att.documentId, url: att.url }))
        )
      }

      messageStore.addIncomingMessage(data)
    } catch (err) {
      console.error('[WebSocket] Error parsing private message:', err)
    }
  })
  console.log(`[WebSocket] ✅ Subscribed to ${topic}`)
}

const subscribeToApplicationNotification = () => {
  const topic = '/user/queue/application.notifications'
  if (!stompClient.value?.active) return
  if (notificationSub) notificationSub.unsubscribe()

  notificationSub = stompClient.value.subscribe(topic, (message) => {
    console.log('[WebSocket] 📨 Notification:', message.body)
    try {
      const data = JSON.parse(message.body)
      notificationStore.addIncomingNotifications(data)
    } catch (err) {
      console.error('[WebSocket] Error parsing notification:', err)
    }
  })
  console.log(`[WebSocket] ✅ Subscribed to ${topic}`)
}

const disconnect = () => {
  if (reconnectTimeout) clearTimeout(reconnectTimeout)
  if (stompClient.value?.active) {
    manualDisconnect.value = true
    privateMessageSub?.unsubscribe()
    notificationSub?.unsubscribe()
    stompClient.value.deactivate()
    connected.value = false
    console.log('[WebSocket] 🔌 Manual disconnect')
  }
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
          subscribeToApplicationNotification()
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
        onWebSocketClose: () => {
          console.warn('[WebSocket] 🔌 WebSocket closed')
          connected.value = false
          connectionPromise = null
          scheduleReconnect()
        },
        onWebSocketOpen: () => {
          console.log('[WebSocket] ✅ WebSocket opened')
          connected.value = true
          connectionPromise = null
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


  const sendMessage = (destination, body) => {
    if (!connected.value || !stompClient.value?.active) {
      console.error('[WebSocket] ⚠️ Cannot send: not connected')
      return
    }
    stompClient.value.publish({ destination, body: JSON.stringify(body) })
    console.log(`[WebSocket] 📤 Sent to ${destination}`)
  }


  return { stompClient, connected, lastMessage, connect, disconnect, subscribeToApplicationNotification, sendMessage }
})
