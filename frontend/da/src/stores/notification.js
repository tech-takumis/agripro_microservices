import {defineStore } from 'pinia'
import { ref, computed } from 'vue'
import axios  from '@/lib/axios'


export const useNotificationStore = defineStore('notification', () => {
    const notifications = ref([])
    const loading =  ref([])
    const error = ref([])


    const isLoading = computed(() => loading.value)
    const isError = computed(() => error.value)
    const allNotifications = computed(() => notifications.value)    
    const unreadCount = computed(() => notifications.value.filter(n => !n.read).length)
    const hasUnread = computed(() => unreadCount.value > 0)
    const lastNotification = computed(() => notifications.value[notifications.value.length - 1])
    const hasNotifications = computed(() => notifications.value.length > 0)
    const hasNewNotifications = computed(() => notifications.value.some(n => n.new))

    const fetchNotification = async () => {
        try{
            loading.value = true
            const response = await axios.get('/api/v1/notifications')
            notifications.value = response.data

            return {status: true, data: response.data}
        }catch(error){
            error.value = error.response?.data?.message || error.message || 'Failed to fetch notifications'
            return {status: false, error: error.response?.data?.message || error.message || 'Failed to fetch notifications'}
        }finally{
            loading.value = false
        }
    }
    const addIncomingNotification = (notification) => {
        notifications.value.unshift(notification)
    }

    

    return {
        isLoading,
        isError,
        allNotifications,
        unreadCount,
        hasUnread,
        lastNotification,
        hasNotifications,
        hasNewNotifications,
        fetchNotification,
        addIncomingNotification
    }
})