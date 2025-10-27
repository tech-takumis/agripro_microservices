import {defineStore} from 'pinia'
import { computed, ref } from 'vue'
import axios from '@/lib/axios'

export const useApplicationStore = defineStore('application', () => {
    const applications = ref([])
    const loading = ref(false)
    const error = ref(null)
    const basePath = ref('/api/v1/applications')


    const allApplications = computed(() => applications.value || [])
    const isLoading = computed(() => loading.value)
    const errors = computed(() => errors.value)


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
            const response = await axios.get("/api/v1/applications/provider/Agriculture")
            applications.value = response.data
            return { success: true, data: response.data }
        } catch (error) {
            console.error("Error fetching agriculture applications:", error.response?.data || error.message)
            return { success: false, error: error.response?.data || error.message }
        }
    }

    async function fetchVerificationApplication() {
        try {
            loading.value = true
            error.value = null
            const response = await axios.get(
                '/api/v1/agriculture/verification',
            )
            applications.value = response.data
            return { success: true, data: response.data }
        } catch (error) {
            console.error(
                'Error fetching verification applications:',
                error.response?.data || error.message,
            )
            loading.value = false
            error.response?.data || error.message
            return {
                success: false,
                error: error.response?.data || error.message,
            }
        } finally {
            loading.value = false
            error.value = null
        }
    }


    const fetchApplicationByBatches = async (batchId) => {
        try{
            loading.value = true
            error.value = null
            const response = await axios.get(`/api/v1/applications/batch/id/${batchId}`)

            if(response.status === 200){
                console.log("Fetched applications by batch id:", response.data)
                applications.value = response.data
                return { success: true, data: response.data }
            }else{
                return { success: false, error: response.data }
            }
        }catch (error){
            error.value = error.message
            loading.value = false
        }finally {
            loading.value = false
        }
    }

    return {
        applications,
        loading,
        error,
        basePath,

        allApplications,
        isLoading,
        errors,

        fetchApplicationByBatches,
        fetchAgricultureApplications,
        createInsuranceApplication,
        fetchApplications,
        fetchVerificationApplication,
        fetchApplicationById,
        updateApplication,
        deleteApplication,
    }
})

export const useApplicationTypeStore = defineStore('applicationType', () => {
    const applicationTypes = ref([])
    const loading = ref(false)
    const error = ref(null)
    const basePath = ref('/api/v1/application/types')

    const allApplicationTypes = computed(() => applicationTypes.value || [])
    const isLoading = computed(() => loading.value)
    const errors = computed(() => errors.value)

    const createApplicationType = async (applicationType) => {
        try {
            const response = await axios.post(basePath.value, applicationType)
            applicationTypes.value.push(response.data)
            return { success: true, data: response.data }
        } catch (error) {
            console.error("Error creating application type:", error.response?.data || error.message)
            return { success: false, error: error.response?.data || error.message }
        }
    }

    const fetchApplicationTypes = async () => {
        try {
            const response = await axios.get(`${basePath.value}/provider/Agriculture`)
            applicationTypes.value = response.data
            return { success: true, data: response.data }
        } catch (error) {
            console.error("Error fetching application types:", error.response?.data || error.message)
            return { success: false, error: error.response?.data || error.message }
        }
    }

    const updateApplicationType = async (id, applicationType) => {
        try {
            const response = await axios.put(`${basePath.value}/${id}`, applicationType)
            const index = applicationTypes.value.findIndex(type => type.id === id)
            if (index !== -1) {
                applicationTypes.value[index] = response.data
            }
            return { success: true, data: response.data }
        } catch (error) {
            console.error("Error updating application type:", error.response?.data || error.message)
        }
    }

    const deleteApplicationType = async (id) => {
        try {
            await axios.delete(`${basePath.value}/${id}`)
            applicationTypes.value = applicationTypes.value.filter(type => type.id !== id)
            return { success: true }
        } catch (error) {
            console.error("Error deleting application type:", error.response?.data || error.message)
        }
    }


    return {
        applicationTypes,
        loading,
        error,
        allApplicationTypes,
        isLoading,
        errors,
        createApplicationType,
        fetchApplicationTypes,
        updateApplicationType,
        deleteApplicationType,
    }


})


export const useApplicationFieldStore = defineStore('applicationField', () => {
    const applicationFields = ref([])
    const loading = ref(false)
    const error = ref(null)
    const basePath = ref('/api/v1/application/fields')

    const allApplicationFields = computed(() => applicationFields.value || [])
    const isLoading = computed(() => loading.value)
    const errors = computed(() => errors.value)

    const fetchApplicationFields = async () => {
        try {
            const response = await axios.get(basePath.value)
            applicationFields.value = response.data
            return { success: true, data: response.data }
        } catch (error) {
            console.error("Error fetching application fields:", error.response?.data || error.message)
            return { success: false, error: error.response?.data || error.message }
        }
    }


    return {
        applicationFields,
        loading,
        error,
        basePath,
        allApplicationFields,
        isLoading,
        errors,
        fetchApplicationFields,
    }
})

export const useApplicationBatchStore = defineStore('applicationBatch', () => {
    const batches = ref([])
    const loading = ref(false)
    const error = ref(null)
    const basePath = ref('/api/v1/batches')

    const allApplicationBatches = computed(() => batches.value || [])
    const isLoading = computed(() => loading.value)
    const errors = computed(() => errors.value)

    const fetchApplicationBatches = async () => {
        try {
            const response = await axios.get(
                `${basePath.value}/provider/Agriculture`,
            )
            batches.value = response.data
            return { success: true, data: response.data }
        } catch (error) {
            console.error(
                'Error fetching application batches:',
                error.response?.data || error.message,
            )
            return {
                success: false,
                error: error.response?.data || error.message,
            }
        }
    }

    const createApplicationBatch = async batch => {
        try {
            const response = await axios.post(basePath.value, batch)
            batches.value.push(response.data)
            return { success: true, data: response.data }
        } catch (error) {
            console.error(
                'Error creating application batch:',
                error.response?.data || error.message,
            )
        }
    }

    const updateApplicationBatch = async (id, batch) => {
        try {
            const response = await axios.put(`${basePath.value}/${id}`, batch)
            const index = batches.value.findIndex(b => b.id === id)
            if (index !== -1) {
                batches.value[index] = response.data
            }
            return { success: true, data: response.data }
        } catch (error) {
            console.error(
                'Error updating application batch:',
                error.response?.data || error.message,
            )
        }
    }

    const deleteApplicationBatch = async id => {
        try {
            await axios.delete(`${basePath.value}/${id}`)
            batches.value = batches.value.filter(b => b.id !== id)
            return { success: true }
        } catch (error) {
            console.error(
                'Error deleting application batch:',
                error.response?.data || error.message,
            )
        }
    }

    return {
        batches,
        loading,
        error,
        basePath,
        allApplicationBatches,
        isLoading,
        errors,
        fetchApplicationBatches,
        createApplicationBatch,
        updateApplicationBatch,
    }
})
