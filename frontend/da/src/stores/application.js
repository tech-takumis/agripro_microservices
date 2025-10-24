import { defineStore } from "pinia"
import { ref, computed } from "vue"
import axios from "@/lib/axios"

export const useApplicationStore = defineStore("application", () => {
    // State
    const availableFieldTypes = ref([])
    const applications = ref([])
    const sections = ref([])

    // Getter: always returns an array
    const allApplications = computed(() => applications.value || [])

    // Actions
    async function fetchFieldTypes() {
        try {
            const response = await axios.get("/api/v1/applications/fields/types")
            availableFieldTypes.value = response.data
            return { success: true, data: response.data }
        } catch (error) {
            console.error("Error fetching field types:", error.response?.data || error.message)
            return { success: false, error: error.response?.data || error.message }
        }
    }

    async function createInsuranceApplication(applicationData) {
        try {
            console.log("Creating insurance application:", JSON.stringify(applicationData, null, 2))
            const response = await axios.post("/api/v1/applications", applicationData)
            console.log("Application created successfully:", response.data)
            return { success: true, data: response.data }
        } catch (error) {
            console.error("Error creating application:", error.response?.data || error.message)
            return { success: false, error: error.response?.data || error.message }
        }
    }

    async function fetchApplications() {
        try {
            const response = await axios.get("/api/v1/applications")
            applications.value = response.data
            return { success: true, data: response.data }
        } catch (error) {
            console.error("Error fetching applications:", error.response?.data || error.message)
            return { success: false, error: error.response?.data || error.message }
        }
    }

    async function fetchApplicationById(id) {
        try {
            const response = await axios.get(`/api/v1/applications/${id}`)
            return { success: true, data: response.data }
        } catch (error) {
            console.error("Error fetching application:", error.response?.data || error.message)
            return { success: false, error: error.response?.data || error.message }
        }
    }

    async function updateApplication(id, applicationData) {
        try {
            const response = await axios.put(`/api/v1/applications/${id}`, applicationData)
            // Update the application in the store
            const index = applications.value.findIndex((app) => app.id === id)
            if (index !== -1) {
                applications.value[index] = response.data
            }
            return { success: true, data: response.data }
        } catch (error) {
            console.error("Error updating application:", error.response?.data || error.message)
            return { success: false, error: error.response?.data || error.message }
        }
    }

    async function deleteApplication(id) {
        try {
            await axios.delete(`/api/v1/applications/${id}`)
            // Remove the application from the store
            applications.value = applications.value.filter((app) => app.id !== id)
            return { success: true }
        } catch (error) {
            console.error("Error deleting application:", error.response?.data || error.message)
            return { success: false, error: error.response?.data || error.message }
        }
    }

    async function fetchAgricultureApplications() {
        try {
            const response = await axios.get("/api/v1/applications/agriculture")
            applications.value = response.data
            return { success: true, data: response.data }
        } catch (error) {
            console.error("Error fetching agriculture applications:", error.response?.data || error.message)
            return { success: false, error: error.response?.data || error.message }
        }
    }

    // Expose state and actions
    return {
        availableFieldTypes,
        applications,
        sections,
        allApplications, // <-- expose getter
        fetchFieldTypes,
        createInsuranceApplication,
        fetchApplications,
        fetchApplicationById,
        updateApplication,
        deleteApplication,
        fetchAgricultureApplications,
    }
})
