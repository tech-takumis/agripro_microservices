import {defineStore} from 'pinia'
import { computed, ref } from 'vue'
import axios from 'axios'

export const useNotificationStore = defineStore('notification', () => {
    const notifications = ref([])
    const loading = ref(false)
    const error = ref(null)

    // Getter
    const allNotifications = computed(() => notifications.value)
    const isLoading = computed(() => loading.value)
    const hasError = computed(() => error.value !== null)

    //Actions
    const fetchNotifications = async () => {
        loading.value = true
        error.value = null
        try {
            const response = await axios.get('/api/v1/notification')
            notifications.value = response.data
            console.log('Fetched notifications:', notifications.value)
            return { success: true, data: notifications.value }
        }catch (error){
            console.error('Error fetching notifications:', error)
            return { success: false, error: error.message }
        }finally {
            loading.value = false
        }
    }

    const fetchUserNotifications = async (username) => {
        try{
            loading.value = true
            error.value = null

            const response = await axios.get(`/api/v1/notification/user/${username}`)
            notifications.value = response.data
            console.log(`Fetched notifications for user ${username}:`, notifications.value)
            return { success: true, data: notifications.value }
        }catch (error){
            console.error('Error fetching user notifications:', error)
            error.value = error.message
            return { success: false, error: error.message }
        }finally {
            loading.value = false
            error.value = null
        }
    }

    const addIncomingNotifications = (notifications) => {
        notifications.value.unshift(...notifications)
        console.log('New notifications added:', notifications)
    }

    return {
        allNotifications,
        isLoading,
        hasError,
        addIncomingNotifications,
        fetchNotifications
    }
})