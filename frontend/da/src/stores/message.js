import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import axios from '@/lib/axios'
import { useWebSocket } from '@/composables/useWebSocket'
import { useDocumentStore } from './document'

export const useMessageStore = defineStore('message', () => {
    // State
    const messages = ref([])
    const isLoadingMessages = ref(false)
    const currentSubscription = ref(null)
    const ws = useWebSocket()

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
                id: msg.id || Date.now() + Math.random(),
                text: msg.text,
                senderId: msg.senderId,
                receiverId: msg.receiverId,
                timestamp: msg.sentAt,
                type: msg.type,
                isOwn: msg.senderId === currentUserId,
                attachments: msg.attachments || []
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
            if (!ws.stompClient.value?.connected) {
                console.log('[MessageStore] WebSocket not connected, attempting to connect...')
                await ws.connect()
            }

            // Get preview URLs for attachments if they exist
            let previewUrls = []
            if (messageData.attachments?.length > 0) {
                try {
                    const previewResponses = await Promise.all(
                        messageData.attachments.map(docId =>
                            axios.get(`/api/v1/documents/${docId}/view`)
                        )
                    )
                    previewUrls = previewResponses.map(response => response.data)
                } catch (error) {
                    console.error('[MessageStore] Failed to fetch preview URLs:', error)
                }
            }

            // Send message to backend with document IDs
            ws.stompClient.value.publish({
                destination: '/app/private.chat',
                body: JSON.stringify(messageData)
            })

            // Add message to local state with preview URLs for display
            messages.value.push({
                id: Date.now(),
                text: messageData.text,
                senderId: messageData.senderId,
                receiverId: messageData.receiverId,
                timestamp: messageData.sentAt,
                type: messageData.type,
                isOwn: true,
                attachments: previewUrls
            })

            return true
        } catch (error) {
            console.error('[MessageStore] Failed to send message:', error)
            throw error
        }
    }

    const subscribeToUserMessages = async (currentUserId, selectedUserId) => {
        try {
            if (!ws.stompClient.value?.connected) {
                await ws.connect()
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
                            // Get preview URLs for attachments if they exist
                            let previewUrls = []
                            if (data.attachments?.length > 0) {
                                try {
                                    const previewResponses = await Promise.all(
                                        data.attachments.map(docId =>
                                            axios.get(`/api/v1/documents/${docId}/preview`)
                                        )
                                    )
                                    previewUrls = previewResponses.map(response => response.data.previewUrl)
                                } catch (error) {
                                    console.error('[MessageStore] Failed to fetch preview URLs:', error)
                                }
                            }

                            messages.value.push({
                                id: Date.now(),
                                text: data.text,
                                senderId: data.senderId,
                                receiverId: data.receiverId,
                                timestamp: data.sentAt,
                                type: data.type,
                                isOwn: false,
                                attachments: previewUrls
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
