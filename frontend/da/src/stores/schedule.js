import axios from '@/lib/axios'
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useScheduleStore = defineStore('schedule', {
    state: () => ({
        schedule: [],
        loading: false,
        error: null
    }),
    getters: {
        allSchedules: (state) => state.schedule,
        isLoading: (state) => state.loading,
        getError: (state) => state.error,
        getPagination: (state) => state.pagination,
        schedulesByType: (state) => (type) => {
            return state.schedule.filter(schedule => schedule.type === type)
        },
        schedulesByPriority: (state) => (priority) => {
            return state.schedule.filter(schedule => schedule.priority === priority)
        },
        meetingSchedules: (state) => {
            return state.schedule.filter(schedule => schedule.type === 'MEETING')
        },
        visitSchedules: (state) => {
            return state.schedule.filter(schedule => schedule.type === 'VISIT')
        },
        highPrioritySchedules: (state) => {
            return state.schedule.filter(schedule => schedule.priority === 'HIGH')
        },
        mediumPrioritySchedules: (state) => {
            return state.schedule.filter(schedule => schedule.priority === 'MEDIUM')
        },
        lowPrioritySchedules: (state) => {
            return state.schedule.filter(schedule => schedule.priority === 'LOW')
        },
        upcomingSchedules: (state) => {
            const now = new Date()
            return state.schedule.filter(schedule => new Date(schedule.scheduleDate) > now)
        },
        todaySchedules: (state) => {
            const today = new Date()
            return state.schedule.filter(schedule => new Date(schedule.scheduleDate) === today)
        },
        schedulesByLocation: (state) => (location) => {
            return state.schedule.filter(schedule =>
                schedule.metaData?.location?.toLowerCase().includes(location.toLowerCase())
            )
        },
        schedulesByFarmer: (state) => (farmerName) => {
            return state.schedule.filter(schedule =>
                schedule.metaData?.farmerName?.toLowerCase().includes(farmerName.toLowerCase())
            )
        },
        getScheduleById: (state) => (id) => {
            return state.schedule.find(schedule => schedule.id === id)
        },
        recentSchedules: (state) => state.schedule.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt)).slice(0, 10)
    },

    actions: {
        async fetchSchedules() {
            this.loading = true
            this.error = null

            try {
                const response = await axios.get("/api/v1/schedules");
                this.schedule = response.data
            } catch (err) {
                this.error = err.response?.data?.message || err.message || 'Failed to fetch schedules'
                console.error('Error fetching schedules:', err)
            } finally {
                this.loading = false
            }
        },

        async createSchedule(scheduleData) {
            this.loading = true
            this.error = null

            try {
                const response = await axios.post("/api/v1/schedules", scheduleData);
                this.schedule.push(response.data)
                return response.data
            } catch (err) {
                this.error = err.response?.data?.message || err.message || 'Failed to create schedule'
                console.error('Error creating schedule:', err)
                throw err
            } finally {
                this.loading = false
            }
        },

        async updateSchedule(id, scheduleData) {
            this.loading = true
            this.error = null

            try {
                const response = await axios.put(`/api/v1/schedules/${id}`, scheduleData);
                const index = this.schedule.findIndex(schedule => schedule.id === id)
                if (index !== -1) {
                    this.schedule[index] = response.data
                }
                return response.data
            } catch (err) {
                this.error = err.response?.data?.message || err.message || 'Failed to update schedule'
                console.error('Error updating schedule:', err)
                throw err
            } finally {
                this.loading = false
            }
        },

        async deleteSchedule(id) {
            this.loading = true
            this.error = null

            try {
                await axios.delete(`/api/v1/schedules/${id}`);
                const index = this.schedule.findIndex(schedule => schedule.id === id)
                if (index !== -1) {
                    this.schedule.splice(index, 1)
                }
                return true
            } catch (err) {
                this.error = err.response?.data?.message || err.message || 'Failed to delete schedule'
                console.error('Error deleting schedule:', err)
                throw err
            } finally {
                this.loading = false
            }
        },

        async updateSchedulePriority(id, priority) {
            return this.updateSchedule(id, { priority })
        },

        async updateScheduleStatus(id, status) {
            return this.updateSchedule(id, { status })
        },

        clearError() {
            this.error = null
        },

        $reset() {
            this.schedule = []
            this.loading = false
            this.error = null
        }
    }
})

