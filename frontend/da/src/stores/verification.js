import { defineStore } from 'pinia'
import axios from '@/lib/axios'
import { ref, computed } from 'vue'

export const useVerificationStore = defineStore('verification', () => {
    // State
    const verifications = ref([])
    const loading = ref(false)
    const error = ref(null)

    // Getters as computed
    const allVerifications = computed(() => verifications.value)
    const isLoading = computed(() => loading.value)
    const getError = computed(() => error.value)
    const pendingVerifications = computed(() =>
        verifications.value.filter((v) => v.status === 'PENDING')
    )
    const approvedVerifications = computed(() =>
        verifications.value.filter((v) => v.status === 'APPROVED')
    )
    const rejectedVerifications = computed(() =>
        verifications.value.filter((v) => v.status === 'REJECTED')
    )
    const recentVerifications = computed(() =>
        verifications.value
            .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
            .slice(0, 10)
    )
    const verificationsStats = computed(() => {
        const total = verifications.value.length
        const pending = verifications.value.filter((v) => v.status === 'PENDING').length
        const approved = verifications.value.filter((v) => v.status === 'APPROVED').length
        const rejected = verifications.value.filter((v) => v.status === 'REJECTED').length
        const byType = verifications.value.reduce((acc, v) => {
            acc[v.type] = (acc[v.type] || 0) + 1
            return acc
        }, {})
        return { total, pending, approved, rejected, byType }
    })

    // Methods that were getters with params
    const verificationsByType = (type) =>
        verifications.value.filter((v) => v.type === type)

    // Actions as functions
    async function fetchVerifications() {
        loading.value = true
        error.value = null

        try {
            const response = await axios.get('/verifications')
            verifications.value = response.data
        } catch (err) {
            error.value = err.response?.data?.message || err.message || 'Failed to fetch verifications'
            console.error('Error fetching verifications:', err)
        } finally {
            loading.value = false
        }
    }

    async function createVerification(verificationData) {
        loading.value = true
        error.value = null

        try {
            const response = await axios.post('/verifications', verificationData)
            verifications.value.push(response.data)
            return response.data
        } catch (err) {
            error.value = err.response?.data?.message || err.message || 'Failed to create verification'
            console.error('Error creating verification:', err)
        } finally {
            loading.value = false
        }
    }

    async function updateVerification(id, verificationData) {
        loading.value = true
        error.value = null

        try {
            const response = await axios.put(`/verifications/${id}`, verificationData)
            const index = verifications.value.findIndex((v) => v.id === id)
            if (index !== -1) {
                verifications.value[index] = response.data
            }
            return response.data
        } catch (err) {
            error.value = err.response?.data?.message || err.message || 'Failed to update verification'
            console.error('Error updating verification:', err)
        } finally {
            loading.value = false
        }
    }

    async function deleteVerification(id) {
        loading.value = true
        error.value = null

        try {
            await axios.delete(`/verifications/${id}`)
            verifications.value = verifications.value.filter((v) => v.id !== id)
        } catch (err) {
            error.value = err.response?.data?.message || err.message || 'Failed to delete verification'
            console.error('Error deleting verification:', err)
        } finally {
            loading.value = false
        }
    }

    return {
        // State
        verifications,
        loading,
        error,

        // Getters
        allVerifications,
        isLoading,
        getError,
        pendingVerifications,
        approvedVerifications,
        rejectedVerifications,
        recentVerifications,
        verificationsStats,

        // Methods
        verificationsByType,
        fetchVerifications,
        createVerification,
        updateVerification,
        deleteVerification
    }
})