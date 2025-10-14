import { defineStore } from 'pinia'
import axios from '@/lib/axios'

export const useDashboardStore = defineStore('dashboard', {
    state: () => ({
        municipalDashboard: {
            dashboardId: null,
            activePrograms: 0,
            programs: [],
            transactions: []
        },
        loading: false,
        error: null
    }),

    actions: {
        async fetchMunicipalDashboard() {
            this.loading = true
            this.error = null

            try {
                const response = await axios.get('/api/v1/dashboard/municipal-agriculturists')
                this.municipalDashboard = response.data
                return response.data
            } catch (error) {
                console.error('Failed to fetch dashboard data:', error)
                this.error = error.response?.data?.message || 'Failed to fetch dashboard data'
                throw error
            } finally {
                this.loading = false
            }
        },

        // Format currency for display
        formatCurrency(amount) {
            return (amount / 1000).toFixed(0) // Convert to K format
        },

        // Get status class for transactions
        getTransactionStatusClass(status) {
            switch (status) {
                case 'COMPLETED':
                    return 'bg-green-100 text-green-800'
                case 'PENDING':
                    return 'bg-yellow-100 text-yellow-800'
                case 'PROCESSING':
                    return 'bg-blue-100 text-blue-800'
                default:
                    return 'bg-gray-100 text-gray-800'
            }
        }
    }
})
