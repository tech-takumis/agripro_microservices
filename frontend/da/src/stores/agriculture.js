import { defineStore } from 'pinia'
import axios from '@/lib/axios'

export const useAgricultureStore = defineStore('agriculture', {
    state: () => ({
        agricultureUsers: [],
        currentAgricultureUser: null,
        loading: false,
        error: null,
        page: 0,
        size: 10,
        totalPages: 0,
        totalElements: 0,
        search: '',
        effectivePermissions: {},
    }),
    getters: {
        allAgricultureUsers: state => state.agricultureUsers,
        getAgricultureUserById: state => id => state.agricultureUsers.find(user => user.id === id),
        isLoading: state => state.loading,
        getError: state => state.error,
        getPagination: state => ({
            page: state.page,
            size: state.size,
            totalPages: state.totalPages,
            totalElements: state.totalElements,
        }),
        getEffectivePermissions: state => userId => state.effectivePermissions[userId] || new Set(),
    },
    actions: {
        async fetchAgricultureUsers({ search = '', page = 0, size = 10 } = {}) {
            this.loading = true
            this.error = null
            try {
                const response = await axios.get('/api/v1/agriculture', {
                    params: { search, page, size }
                })
                this.agricultureUsers = response.data.content
                this.page = response.data.number
                this.size = response.data.size
                this.totalPages = response.data.totalPages
                this.totalElements = response.data.totalElements
                this.search = search
                return { success: true, data: this.agricultureUsers }
            } catch (error) {
                this.error = error.response?.data?.message || 'Failed to fetch agriculture users'
                return { success: false, error: this.error }
            } finally {
                this.loading = false
            }
        },
        async fetchAgricultureUserById(id) {
            this.loading = true
            this.error = null
            try {
                const response = await axios.get(`/api/v1/agriculture/${id}`)
                this.currentAgricultureUser = response.data
                // Update user in the array if exists
                const index = this.agricultureUsers.findIndex(user => user.id === id)
                if (index !== -1) {
                    this.agricultureUsers[index] = response.data
                }
                return { success: true, data: response.data }
            } catch (error) {
                this.error = error.response?.data?.message || 'Failed to fetch agriculture user'
                return { success: false, error: this.error }
            } finally {
                this.loading = false
            }
        },
        async deleteAgricultureUser(id) {
            this.loading = true
            this.error = null
            try {
                await axios.delete(`/api/v1/agriculture/${id}`)
                this.agricultureUsers = this.agricultureUsers.filter(user => user.id !== id)
                return { success: true }
            } catch (error) {
                this.error = error.response?.data?.message || 'Failed to delete agriculture user'
                return { success: false, error: this.error }
            } finally {
                this.loading = false
            }
        },
        async assignDirectPermission(userId, permissionId) {
            this.loading = true
            this.error = null
            try {
                await axios.post(`/api/v1/agriculture/${userId}/permissions/${permissionId}`)
                return { success: true }
            } catch (error) {
                this.error = error.response?.data?.message || 'Failed to assign permission'
                return { success: false, error: this.error }
            } finally {
                this.loading = false
            }
        },
        async fetchEffectivePermissions(userId) {
            this.loading = true
            this.error = null
            try {
                const response = await axios.get(`/api/v1/agriculture/${userId}/effective-permissions`)
                this.effectivePermissions[userId] = new Set(response.data)
                return { success: true, data: response.data }
            } catch (error) {
                this.error = error.response?.data?.message || 'Failed to fetch effective permissions'
                return { success: false, error: this.error }
            } finally {
                this.loading = false
            }
        },
        clearError() {
            this.error = null
        },
        clearCurrentAgricultureUser() {
            this.currentAgricultureUser = null
        },
        $reset() {
            this.agricultureUsers = []
            this.currentAgricultureUser = null
            this.loading = false
            this.error = null
            this.page = 0
            this.size = 10
            this.totalPages = 0
            this.totalElements = 0
            this.search = ''
            this.effectivePermissions = {}
        },
    },
})