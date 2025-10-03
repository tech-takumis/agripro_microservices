import { defineStore } from 'pinia'
import axios from '@/lib/axios'

export const useVerificationStore = defineStore('verification', {
  state: () => ({
    verifications: [],
    loading: false,
    error: null,
  }),
  getters: {
    allVerifications: (state) => state.verifications,
    isLoading: (state) => state.loading,
    getError: (state) => state.error,
    pendingVerifications: (state) =>
      state.verifications.filter((v) => v.status === 'PENDING'),
    approvedVerifications: (state) =>
      state.verifications.filter((v) => v.status === 'APPROVED'),
    rejectedVerifications: (state) =>
      state.verifications.filter((v) => v.status === 'REJECTED'),
    verificationsByType: (state) => (type) =>
      state.verifications.filter((v) => v.type === type),
    recentVerifications: (state) =>
      state.verifications
        .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
        .slice(0, 10),
    verificationsStats: (state) => {
      const total = state.verifications.length
      const pending = state.verifications.filter((v) => v.status === 'PENDING')
        .length
      const approved = state.verifications.filter((v) => v.status === 'APPROVED')
        .length
      const rejected = state.verifications.filter((v) => v.status === 'REJECTED')
        .length
      const byType = state.verifications.reduce((acc, v) => {
        acc[v.type] = (acc[v.type] || 0) + 1
        return acc
      }, {})
      return { total, pending, approved, rejected, byType }
    },
  },
  actions: {
    async fetchVerifications() {
      this.loading = true
      this.error = null

      try {
        const response = await axios.get('/verifications')
        this.verifications = response.data
      } catch (error) {
        this.error =
          error.response?.data?.message || error.message || 'Failed to fetch verifications'
        console.error('Error fetching verifications:', error)
      } finally {
        this.loading = false
      }
    },

    async createVerification(verificationData) {
      this.loading = true
      this.error = null

      try {
        const response = await axios.post('/verifications', verificationData)
        this.verifications.push(response.data)
        return response.data
      } catch (error) {
        this.error =
          error.response?.data?.message || error.message || 'Failed to create verification'
        console.error('Error creating verification:', error)
      } finally {
        this.loading = false
      }
    },

    async updateVerification(id, verificationData) {
      this.loading = true
      this.error = null

      try {
        const response = await axios.put(`/verifications/${id}`, verificationData)
        const index = this.verifications.findIndex((v) => v.id === id)
        if (index !== -1) {
          this.verifications[index] = response.data
        }
        return response.data
      } catch (error) {
        this.error =
          error.response?.data?.message || error.message || 'Failed to update verification'
        console.error('Error updating verification:', error)
      } finally {
        this.loading = false
      }
    },

    async deleteVerification(id) {
      this.loading = true
      this.error = null

      try {
        await axios.delete(`/verifications/${id}`)
        this.verifications = this.verifications.filter((v) => v.id !== id)
      } catch (error) {
        this.error =
          error.response?.data?.message || error.message || 'Failed to delete verification'
        console.error('Error deleting verification:', error)
      } finally {
        this.loading = false
      }
    },
  },
})