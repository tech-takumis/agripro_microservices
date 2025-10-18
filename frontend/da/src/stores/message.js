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
        try {
            const auth = useAuthStore()
            const msg = {
                senderId: auth.userId,
                receiverId: messageData.receiverId,
                text: messageData.text,
                type: messageData.type || 'FARMER_AGRICULTURE',
                attachments: messageData.attachments || [],
                sentAt: new Date().toISOString()
            }
            console.log('[MessageStore] Sending message:', msg)
            ws.sendMessage('/app/private.chat', msg)
            console.log('[MessageStore] ✅ Message sent successfully')
        } catch (err) {
            console.error('[MessageStore] Failed to send message:', err)
        }
    }

    const addIncomingMessage = (data) => {
        const currentUserId = useAuthStore().userId
        const newMsg = {
            id: data.messageId,
            text: data.text,
            senderId: data.senderId,
            receiverId: data.receiverId,
            timestamp: data.sentAt,
            type: data.type,
            isOwn: data.senderId === currentUserId,
            attachments: data.attachments || []
        }
        messages.value.push(newMsg)
        console.log('[MessageStore] 📨 Added incoming message:', newMsg)
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
