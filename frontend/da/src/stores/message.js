import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import axios from '@/lib/axios'
import { useWebSocket } from '@/composables/useWebSocket'
import { useAuthStore } from '@/stores/auth'

export const useMessageStore = defineStore('message', () => {
    // State
    const messages = ref([])
    const isLoadingMessages = ref(false)
    const currentSubscription = ref(null)
    const ws = useWebSocket()
    const authStore = useAuthStore()

    // Getters
    const getMessagesByUser = computed(() => (userId) => {
        return messages.value.filter(msg =>
            msg.senderId === userId || msg.receiverId === userId
        ).sort((a, b) => new Date(a.timestamp) - new Date(b.timestamp))
    })

    // Actions
    const fetchMessages = async (currentUserId, selectedUserId = null) => {
        try {
            isLoadingMessages.value = true
            const response = await axios.get(`/api/v1/chat/${currentUserId}/messages`)
            const allMessages = response.data

            // Map all messages with required properties
            const mappedMessages = allMessages.map(msg => ({
                id: msg.messageId || Date.now() + Math.random(),
                text: msg.text,
                senderId: msg.senderId,
                receiverId: msg.receiverId,
                timestamp: msg.sentAt,
                type: msg.type,
                isOwn: msg.senderId === currentUserId,
                attachments: msg.attachments || [] // This now contains array of { documentId, url }
            }))

            // If selectedUserId is provided, filter messages
            messages.value = selectedUserId
                ? mappedMessages.filter(msg =>
                    (msg.senderId === currentUserId && msg.receiverId === selectedUserId) ||
                    (msg.senderId === selectedUserId && msg.receiverId === currentUserId)
                  )
                : mappedMessages

            return messages.value
        } catch (error) {
            console.error('Error fetching messages:', error)
            throw error
        } finally {
            isLoadingMessages.value = false
        }
    }

    const sendMessage = async (messageData) => {
        try {
            if (!ws.stompClient.value?.active) {
                console.log('[MessageStore] WebSocket not connected, attempting to connect...')
                await ws.connect()
                await ws.waitForConnection()
            }

            // Send message to backend without senderId
            ws.stompClient.value.publish({
                destination: '/app/private.chat',
                body: JSON.stringify(messageData)
            })

            // Add message to local state with attachments
            messages.value.push({
                id: Date.now(),
                text: messageData.text,
                senderId: authStore.userId,
                receiverId: messageData.receiverId,
                timestamp: messageData.sentAt,
                type: messageData.type,
                isOwn: true,
                attachments: messageData.attachments
            })

            return true
        } catch (error) {
            console.error('[MessageStore] Failed to send message:', error)
            throw error
        }
    }

    const subscribeToUserMessages = async (currentUserId, selectedUserId) => {
        try {
            if (!ws.stompClient.value?.active) {
                await ws.connect()
                await ws.waitForConnection()
            }

            if (currentSubscription.value) {
                currentSubscription.value.unsubscribe()
            }

            currentSubscription.value = ws.stompClient.value.subscribe(
                `/user/${currentUserId}/queue/private`,
                async (message) => {
                    try {
                        const data = JSON.parse(message.body)
                        console.log('[MessageStore] Received message:', data)

                        if (data.senderId === selectedUserId) {
                            messages.value.push({
                                id: Date.now(),
                                text: data.text,
                                senderId: data.senderId,
                                receiverId: data.receiverId,
                                timestamp: data.sentAt,
                                type: data.type,
                                isOwn: false,
                                attachments: data.attachments // Already contains { documentId, url }
                            })
                        }
                    } catch (error) {
                        console.error('[MessageStore] Error handling message:', error)
                    }
                }
            )

            console.log('[MessageStore] Subscribed to messages for user:', selectedUserId)
        } catch (error) {
            console.error('[MessageStore] Failed to subscribe to messages:', error)
            throw error
        }
    }

    const cleanup = () => {
        if (currentSubscription.value) {
            currentSubscription.value.unsubscribe()
            currentSubscription.value = null
        }
        messages.value = []
    }

    return {
        // State
        messages,
        isLoadingMessages,
        // Getters
        getMessagesByUser,
        // Actions
        fetchMessages,
        sendMessage,
        subscribeToUserMessages,
        cleanup
    }
})
