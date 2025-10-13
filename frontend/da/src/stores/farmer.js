import { defineStore } from 'pinia'
import axios from '@/lib/axios'

export const useFarmerStore = defineStore('farmers', {
    state: () => ({
        farmers: [],
        currentFarmer: null,
        loading: false,
        error: null,
    }),

    getters: {
        // Get all farmers
        allFarmers: state => state.farmers,

        // Get farmer by ID
        getFarmerById: state => id => state.farmers.find(farmer => farmer.id === id),

        // Get loading state
        isLoading: state => state.loading,

        // Get error state
        getError: state => state.error,
    },

    actions: {
        // Fetch all farmers
        async fetchFarmers() {
            this.loading = true
            this.error = null
            try {
                const response = await axios.get('/api/v1/farmer')
                this.farmers = response.data
                return { success: true, data: this.farmers }
            } catch (error) {
                this.error = error.response?.data?.message || 'Failed to fetch farmers'
                return { success: false, error: this.error }
            } finally {
                this.loading = false
            }
        },

        // Fetch single farmer by ID
        async fetchFarmerById(id) {
            this.loading = true
            this.error = null
            try {
                const response = await axios.get(`/api/v1/farmer/${id}`)
                this.currentFarmer = response.data
                // Update farmer in the farmers array if it exists
                const index = this.farmers.findIndex(farmer => farmer.id === id)
                if (index !== -1) {
                    this.farmers[index] = response.data
                }
                return { success: true, data: response.data }
            } catch (error) {
                this.error = error.response?.data?.message || 'Failed to fetch farmer'
                return { success: false, error: this.error }
            } finally {
                this.loading = false
            }
        },

        // Clear error state
        clearError() {
            this.error = null
        },

        // Clear current farmer
        clearCurrentFarmer() {
            this.currentFarmer = null
        },

        // Reset store state
        $reset() {
            this.farmers = []
            this.currentFarmer = null
            this.loading = false
            this.error = null
        },
    },
})
