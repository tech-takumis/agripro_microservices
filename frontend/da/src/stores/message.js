import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import axios from '@/lib/axios'
import { useAuthStore } from '@/stores/auth'
import { useWebSocketStore } from '@/stores/websocket' 

export const useMessageStore = defineStore('message', () => {
    const messages = ref([])
    const isLoadingMessages = ref(false)
    const ws = useWebSocketStore() 

    const getMessagesByUser = computed(() => (userId) => {
        return messages.value
            .filter(msg => msg.senderId === userId || msg.receiverId === userId)
            .sort((a, b) => new Date(a.sentAt) - new Date(b.sentAt))
    })

    const fetchMessages = async (currentUserId, selectedUserId = null) => {
        try {
            isLoadingMessages.value = true
            const { data } = await axios.get(`/api/v1/chat/${currentUserId}/messages`)
            const mapped = data.map(msg => ({
                id: msg.messageId || Date.now() + Math.random(),
                text: msg.text,
                senderId: msg.senderId,
                receiverId: msg.receiverId,
                timestamp: msg.sentAt,
                type: msg.type,
                isOwn: msg.senderId === currentUserId,
                attachments: msg.attachments || []
            }))
            messages.value = selectedUserId
                ? mapped.filter(m =>
                    (m.senderId === currentUserId && m.receiverId === selectedUserId) ||
                    (m.senderId === selectedUserId && m.receiverId === currentUserId)
                )
                : mapped
            console.log('[MessageStore] Fetched messages:', messages.value.length)
        } catch (e) {
            console.error('[MessageStore] Error fetching messages:', e)
        } finally {
            isLoadingMessages.value = false
        }
    }

    const sendMessage = async (messageData) => {
        console.log('[MessageStore] sendMessage called with data:', {
            ...messageData,
            files: messageData.files ? `Array(${messageData.files.length})` : 'none',
            textLength: messageData.text?.length || 0
        })
        
        try {
            const auth = useAuthStore()
            console.log('[MessageStore] Auth store user ID:', auth.userId)
            
            const msg = {
                senderId: auth.userId,
                receiverId: messageData.receiverId,
                conversationId: messageData.conversationId || null,
                text: messageData.text || '', // Allow empty text
                type: 'FARMER_AGRICULTURE',
                sentAt: messageData.sentAt || new Date().toISOString()
            }
            console.log('[MessageStore] Prepared message object:', { ...msg, text: msg.text?.substring(0, 50) + (msg.text?.length > 50 ? '...' : '') })

            const formData = new FormData()
            
            // 1. Append the message as a JSON string
            const messageBlob = new Blob([JSON.stringify(msg)], { type: 'application/json' })
            formData.append('message', messageBlob)
            
            // 2. Append conversationType as a string (not as a Blob)
            formData.append('conversationType', 'FARMER_AGRICULTURE')
            
            // 3. Append files if any
            if (messageData.files && messageData.files.length > 0) {
                console.log(`[MessageStore] Attaching ${messageData.files.length} files`)
                messageData.files.forEach((file, index) => {
                    formData.append('attachments', file)
                    console.log(`[MessageStore] Added file ${index + 1}:`, file.name, `(${file.size} bytes, ${file.type})`)
                })
            }
            
            // Log FormData contents for debugging
            console.log('[MessageStore] FormData entries:')
            for (let [key, value] of formData.entries()) {
                if (value instanceof Blob) {
                    console.log(`  ${key}: Blob(${value.size} bytes, ${value.type})`)
                } else {
                    console.log(`  ${key}:`, value)
                }
            }
            
            const config = {
                headers: { 
                    'Content-Type': 'multipart/form-data',
                    'X-Requested-With': 'XMLHttpRequest'
                },
                withCredentials: true 
            }
            
            console.log('[MessageStore] Sending message with config:', config)
            console.log('[MessageStore] Request payload:', {
                message: msg,
                conversationType: 'FARMER_AGRICULTURE',
                hasFiles: messageData.files && messageData.files.length > 0
            })
            
            try {
                const response = await axios.post('/api/v1/chat', formData, config)
                console.log('[MessageStore] ✅ Message sent successfully', response.data)
                return response.data
            } catch (error) {
                console.error('[MessageStore] Detailed error:', {
                    message: error.message,
                    status: error.response?.status,
                    statusText: error.response?.statusText,
                    data: error.response?.data,
                    request: {
                        method: error.config?.method,
                        url: error.config?.url,
                        headers: error.config?.headers,
                        data: error.config?.data
                    }
                })
                throw error
            }
        } catch (err) {
            console.error('[MessageStore] Failed to send message:', {
                message: err.message,
                status: err.response?.status,
                statusText: err.response?.statusText,
                data: err.response?.data,
                headers: err.response?.headers
            })
            
            // If 403, the user might need to re-authenticate
            if (err.response?.status === 403) {
                const authStore = useAuthStore()
                console.warn('[MessageStore] Access denied. User might need to re-authenticate.')
                // Optionally trigger a re-authentication flow
                // await authStore.logout()
            }
            
            // Re-throw the error to be handled by the component
            throw err
        }
    }

    const addIncomingMessage = (data) => {
        const currentUserId = useAuthStore().userId

        // Map attachmentResponses to attachments format for UI consistency
        const attachments = data.attachmentResponses ?
            data.attachmentResponses.map(attachment => ({
                attachmentId: attachment.attachmentId,
                documentId: attachment.documentId,
                url: attachment.url
            })) : []

        const newMsg = {
            id: data.messageId,
            text: data.text,
            senderId: data.senderId,
            receiverId: data.receiverId,
            timestamp: data.timestamp || new Date().toISOString(),
            type: data.conversationType || data.type,
            isOwn: data.senderId === currentUserId,
            attachments: attachments
        }

        messages.value.push(newMsg)
        console.log('[MessageStore] 📨 Added incoming message:', {
            ...newMsg,
            attachments: attachments.length > 0 ? `${attachments.length} attachment(s)` : 'none'
        })
    }

    return {
        messages,
        isLoadingMessages,
        getMessagesByUser,
        fetchMessages,
        sendMessage,
        addIncomingMessage
    }
})
