import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useWebSocketStore = defineStore('websocket', () => {
    const connected = ref(false)
    const lastMessage = ref(null)

    function setConnected(value) {
        connected.value = value
    }

    function setLastMessage(msg) {
        lastMessage.value = msg
    }

    return { connected, lastMessage, setConnected, setLastMessage }
})
