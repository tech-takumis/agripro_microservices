import axios from '@/lib/axios'
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useScheduleStore = defineStore('schedule', () => {
    // State
    const schedule = ref([])
    const loading = ref(false)
    const error = ref(null)

    // Getters as computed
    const allSchedules = computed(() => schedule.value)
    const isLoading = computed(() => loading.value)
    const getError = computed(() => error.value)

    const meetingSchedules = computed(() =>
        schedule.value.filter(s => s.type === 'MEETING')
    )

    const visitSchedules = computed(() =>
        schedule.value.filter(s => s.type === 'VISIT')
    )

    const highPrioritySchedules = computed(() =>
        schedule.value.filter(s => s.priority === 'HIGH')
    )

    const mediumPrioritySchedules = computed(() =>
        schedule.value.filter(s => s.priority === 'MEDIUM')
    )

    const lowPrioritySchedules = computed(() =>
        schedule.value.filter(s => s.priority === 'LOW')
    )

    const upcomingSchedules = computed(() => {
        const now = new Date()
        return schedule.value.filter(s => new Date(s.scheduleDate) > now)
    })

    const todaySchedules = computed(() => {
        const today = new Date()
        return schedule.value.filter(s => new Date(s.scheduleDate) === today)
    })

    const recentSchedules = computed(() =>
        schedule.value
            .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
            .slice(0, 10)
    )

    // Methods that were getters with parameters
    const schedulesByType = (type) =>
        schedule.value.filter(s => s.type === type)

    const schedulesByPriority = (priority) =>
        schedule.value.filter(s => s.priority === priority)

    const schedulesByLocation = (location) =>
        schedule.value.filter(s =>
            s.metaData?.location?.toLowerCase().includes(location.toLowerCase())
        )

    const schedulesByFarmer = (farmerName) =>
        schedule.value.filter(s =>
            s.metaData?.farmerName?.toLowerCase().includes(farmerName.toLowerCase())
        )

    const getScheduleById = (id) =>
        schedule.value.find(s => s.id === id)

    // Actions as functions
    async function fetchSchedules() {
        loading.value = true
        error.value = null

        try {
            const response = await axios.get("/api/v1/schedules")
            schedule.value = response.data
        } catch (err) {
            error.value = err.response?.data?.message || err.message || 'Failed to fetch schedules'
            console.error('Error fetching schedules:', err)
        } finally {
            loading.value = false
        }
    }

    async function createSchedule(scheduleData) {
        loading.value = true
        error.value = null

        try {
            const response = await axios.post("/api/v1/schedules", scheduleData)
            schedule.value.push(response.data)
            return response.data
        } catch (err) {
            error.value = err.response?.data?.message || err.message || 'Failed to create schedule'
            console.error('Error creating schedule:', err)
            throw err
        } finally {
            loading.value = false
        }
    }

    async function updateSchedule(id, scheduleData) {
        loading.value = true
        error.value = null

        try {
            const response = await axios.put(`/api/v1/schedules/${id}`, scheduleData)
            const index = schedule.value.findIndex(s => s.id === id)
            if (index !== -1) {
                schedule.value[index] = response.data
            }
            return response.data
        } catch (err) {
            error.value = err.response?.data?.message || err.message || 'Failed to update schedule'
            console.error('Error updating schedule:', err)
            throw err
        } finally {
            loading.value = false
        }
    }

    async function deleteSchedule(id) {
        loading.value = true
        error.value = null

        try {
            await axios.delete(`/api/v1/schedules/${id}`)
            schedule.value = schedule.value.filter(s => s.id !== id)
        } catch (err) {
            error.value = err.response?.data?.message || err.message || 'Failed to delete schedule'
            console.error('Error deleting schedule:', err)
            throw err
        } finally {
            loading.value = false
        }
    }

    return {
        // State
        schedule,
        loading,
        error,

        // Getters
        allSchedules,
        isLoading,
        getError,
        meetingSchedules,
        visitSchedules,
        highPrioritySchedules,
        mediumPrioritySchedules,
        lowPrioritySchedules,
        upcomingSchedules,
        todaySchedules,
        recentSchedules,

        // Methods
        schedulesByType,
        schedulesByPriority,
        schedulesByLocation,
        schedulesByFarmer,
        getScheduleById,

        // Actions
        fetchSchedules,
        createSchedule,
        updateSchedule,
        deleteSchedule
    }
})
