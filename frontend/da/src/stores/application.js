import { defineStore } from "pinia"
import axios from "@/lib/axios"

export const useApplicationStore = defineStore("application", {
  state: () => ({
    availableFieldTypes: [],
    applications: [],
    sections: [],
  }),

  actions: {
    async fetchFieldTypes() {
      try {
        const response = await axios.get("/fields/types")
        this.availableFieldTypes = response.data
        return { success: true, data: response.data }
      } catch (error) {
        console.error("Error fetching field types:", error.response?.data || error.message)
        return { success: false, error: error.response?.data || error.message }
      }
    },

    async createInsuranceApplication(applicationData) {
      try {
        console.log("Creating insurance application:", JSON.stringify(applicationData, null, 2))

        const response = await axios.post("/insurances", applicationData)

        console.log("Application created successfully:", response.data)
        return { success: true, data: response.data }
      } catch (error) {
        console.error("Error creating application:", error.response?.data || error.message)
        return { success: false, error: error.response?.data || error.message }
      }
    },

    async fetchApplications() {
      try {
        const response = await axios.get("/insurances")
        this.applications = response.data
        return { success: true, data: response.data }
      } catch (error) {
        console.error("Error fetching applications:", error.response?.data || error.message)
        return { success: false, error: error.response?.data || error.message }
      }
    },

    async fetchApplicationById(id) {
      try {
        const response = await axios.get(`/insurances/${id}`)
        return { success: true, data: response.data }
      } catch (error) {
        console.error("Error fetching application:", error.response?.data || error.message)
        return { success: false, error: error.response?.data || error.message }
      }
    },

    async updateApplication(id, applicationData) {
      try {
        const response = await axios.put(`/insurances/${id}`, applicationData)
        
        // Update the application in the store
        const index = this.applications.findIndex(app => app.id === id)
        if (index !== -1) {
          this.applications[index] = response.data
        }
        
        return { success: true, data: response.data }
      } catch (error) {
        console.error("Error updating application:", error.response?.data || error.message)
        return { success: false, error: error.response?.data || error.message }
      }
    },

    async deleteApplication(id) {
      try {
        await axios.delete(`/insurances/${id}`)
        
        // Remove the application from the store
        this.applications = this.applications.filter(app => app.id !== id)
        
        return { success: true }
      } catch (error) {
        console.error("Error deleting application:", error.response?.data || error.message)
        return { success: false, error: error.response?.data || error.message }
      }
    },
  },
})
