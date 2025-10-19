import { defineStore } from "pinia"
import axios from "@/lib/axios"
import { ref } from "vue"

export const useDashboardStore = defineStore("dashboard", () => {
    // State
    const municipalDashboard = ref({
        dashboardId: null,
        activePrograms: 0,
        programs: [],
        transactions: [],
    })
    const loading = ref(false)
    const error = ref(null)

    // Actions as functions
    async function fetchMunicipalDashboard() {
        loading.value = true
        error.value = null

        try {
            const response = await axios.get("/api/v1/dashboard/municipal-agriculturists")
            municipalDashboard.value = response.data
            return response.data
        } catch (err) {
            console.error("Failed to fetch dashboard data:", err)
            error.value = err.response?.data?.message || "Failed to fetch dashboard data"
            throw err
        } finally {
            loading.value = false
        }
    }

    // Utility functions
    function formatCurrency(amount) {
        return (amount / 1000).toFixed(0) // Convert to K format
    }

    function getTransactionStatusClass(status) {
        switch (status) {
            case "COMPLETED":
                return "bg-green-100 text-green-800"
            case "PENDING":
                return "bg-yellow-100 text-yellow-800"
            case "PROCESSING":
                return "bg-blue-100 text-blue-800"
            default:
                return "bg-gray-100 text-gray-800"
        }
    }

    return {
        // State
        municipalDashboard,
        loading,
        error,

        // Actions
        fetchMunicipalDashboard,

        // Utility functions
        formatCurrency,
        getTransactionStatusClass,
    }
})
