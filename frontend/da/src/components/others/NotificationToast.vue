<template>
    <Teleport to="body">
        <div class="fixed top-4 right-4 z-50 space-y-2 max-w-sm">
            <TransitionGroup
                name="notification"
                tag="div"
                class="space-y-2">
                <div
                    v-for="notification in visibleNotifications"
                    :key="notification.id"
                    :class="notificationClasses(notification.type)"
                    class="flex items-start p-4 rounded-lg shadow-lg backdrop-blur-sm border transform transition-all duration-300 ease-in-out">
                    <!-- Icon -->
                    <div class="flex-shrink-0 mr-3">
                        <component
                            :is="getIcon(notification.type)"
                            :class="getIconClasses(notification.type)"
                            class="h-5 w-5" />
                    </div>
                    
                    <!-- Message Content -->
                    <div class="flex-1 min-w-0">
                        <p class="text-sm font-medium text-gray-900">
                            {{ notification.message }}
                        </p>
                    </div>
                    
                    <!-- Close Button -->
                    <button
                        class="flex-shrink-0 ml-2 text-gray-400 hover:text-gray-600 transition-colors"
                        @click="notificationStore.removeNotification(notification.id)">
                        <X class="h-4 w-4" />
                    </button>
                </div>
            </TransitionGroup>
        </div>
    </Teleport>
</template>

<script setup>
import { computed } from 'vue'
import { X, CheckCircle, XCircle, AlertTriangle, Info } from 'lucide-vue-next'
import { useNotificationStore } from '@/stores/notification'

const notificationStore = useNotificationStore()

const visibleNotifications = computed(() => 
    notificationStore.notifications.filter(n => n.visible)
)

const notificationClasses = (type) => {
    const baseClasses = 'border-l-4'
    
    switch (type) {
        case 'success':
            return `${baseClasses} bg-green-50 border-green-400 text-green-800`
        case 'error':
            return `${baseClasses} bg-red-50 border-red-400 text-red-800`
        case 'warning':
            return `${baseClasses} bg-yellow-50 border-yellow-400 text-yellow-800`
        case 'info':
            return `${baseClasses} bg-blue-50 border-blue-400 text-blue-800`
        default:
            return `${baseClasses} bg-gray-50 border-gray-400 text-gray-800`
    }
}

const getIcon = (type) => {
    switch (type) {
        case 'success':
            return CheckCircle
        case 'error':
            return XCircle
        case 'warning':
            return AlertTriangle
        case 'info':
            return Info
        default:
            return Info
    }
}

const getIconClasses = (type) => {
    switch (type) {
        case 'success':
            return 'text-green-600'
        case 'error':
            return 'text-red-600'
        case 'warning':
            return 'text-yellow-600'
        case 'info':
            return 'text-blue-600'
        default:
            return 'text-gray-600'
    }
}


</script>

<style scoped>
.notification-enter-active,
.notification-leave-active {
    transition: all 0.3s ease;
}

.notification-enter-from {
    opacity: 0;
    transform: translateX(100%) scale(0.8);
}

.notification-leave-to {
    opacity: 0;
    transform: translateX(100%) scale(0.8);
}

.notification-move {
    transition: transform 0.3s ease;
}
</style>
