import { defineStore } from 'pinia'
import axios from '@/lib/axios'
import { ref, computed } from 'vue'

export const useFarmerStore = defineStore('farmers', () => {
    // State
    const farmers = ref([])
    const currentFarmer = ref(null)
    const loading = ref(false)
    const error = ref(null)

    // Getters as computed
    const allFarmers = computed(() => farmers.value)
    const totalFarmer = computed(() => farmers.value.length)
    const isLoading = computed(() => loading.value)
    const getError = computed(() => error.value)

    // Methods that were getters with parameters
    const getFarmerById = (id) =>
        farmers.value.find(farmer => farmer.id === id)

    // Actions as functions
    async function fetchFarmers() {
        loading.value = true
        error.value = null

        try {
            const response = await axios.get('/api/v1/farmer')
            farmers.value = response.data.map(farmer => ({
                ...farmer,
                name: `${farmer.firstName} ${farmer.lastName}`.trim(),
            }))
            console.log('Fetched farmers:', farmers.value)

            return { success: true, data: farmers.value }
        } catch (err) {
            error.value = err.response?.data?.message || 'Failed to fetch farmers'
            return { success: false, error: error.value }
        } finally {
            loading.value = false
        }
    }

    async function fetchFarmerById(id) {
        loading.value = true
        error.value = null

        try {
            const response = await axios.get(`/api/v1/farmer/${id}`)
            currentFarmer.value = response.data
            // Update farmer in the farmers array if it exists
            const index = farmers.value.findIndex(farmer => farmer.id === id)
            if (index !== -1) {
                farmers.value[index] = response.data
            }
            return { success: true, data: response.data }
        } catch (err) {
            error.value = err.response?.data?.message || 'Failed to fetch farmer'
            return { success: false, error: error.value }
        } finally {
            loading.value = false
        }
    }

    function clearError() {
        error.value = null
    }

    function clearCurrentFarmer() {
        currentFarmer.value = null
    }

    function reset() {
        farmers.value = []
        currentFarmer.value = null
        loading.value = false
        error.value = null
    }

    return {
        // State
        farmers,
        currentFarmer,
        loading,
        error,

        // Getters
        allFarmers,
        totalFarmer,
        isLoading,
        getError,

        // Methods
        getFarmerById,

        // Actions
        fetchFarmers,
        fetchFarmerById,
        clearError,
        clearCurrentFarmer,
        reset
    }
})
