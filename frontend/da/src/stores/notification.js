import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useNotificationStore = defineStore('notification', () => {
    const notifications = ref([])
    const nextId = ref(0)

    const addNotification = (type, message, duration = 5000) => {
        const id = nextId.value++
        const notification = {
            id,
            type, // 'success', 'error', 'info', 'warning'
            message,
            duration,
            visible: true,
            timestamp: Date.now()
        }

        notifications.value.push(notification)

        // Auto remove notification after duration
        if (duration > 0) {
            setTimeout(() => {
                removeNotification(id)
            }, duration)
        }

        return id
    }

    const removeNotification = (id) => {
        const index = notifications.value.findIndex(n => n.id === id)
        if (index > -1) {
            notifications.value[index].visible = false
            // Remove from array after animation
            setTimeout(() => {
                notifications.value.splice(index, 1)
            }, 300)
        }
    }

    const clearAllNotifications = () => {
        notifications.value.forEach(n => n.visible = false)
        setTimeout(() => {
            notifications.value.length = 0
        }, 300)
    }

    // Convenience methods
    const showSuccess = (message, duration = 5000) => {
        return addNotification('success', message, duration)
    }

    const showError = (message, duration = 7000) => {
        return addNotification('error', message, duration)
    }

    const showInfo = (message, duration = 5000) => {
        return addNotification('info', message, duration)
    }

    const showWarning = (message, duration = 6000) => {
        return addNotification('warning', message, duration)
    }

    return {
        notifications,
        addNotification,
        removeNotification,
        clearAllNotifications,
        showSuccess,
        showError,
        showInfo,
        showWarning
    }
})
