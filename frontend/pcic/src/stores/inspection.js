import {defineStore} from 'pinia'
import {ref,computed} from 'vue'
import axios from '@/lib/axios'


export const useInspectionStore = defineStore('inspection', () => {
    const inspections = ref([])
    const loading = ref(false)
    const schedule = ref(null)
    const error = ref(null)
    const baseUrl = ref("/api/inspections")

    const isLoading = computed(() => loading.value)
    const errorMessage = computed(() => error.value)
    const getInspectionSchedule = computed(() => schedule.value)
    const allInspections = computed(() => inspections.value)


    const fetchInspections = async () => {
        try{
            loading.value = true
            error.value = false

            const response = await axios.get(baseUrl.value)

            inspections.value = response.data
            return { success: true, message:"Application fetch successfully", data: response.data }
        }catch (error) {
            console.error("Error fetching inspections:", error.response?.data || error.message)
            error.value = error.response?.message
            return { success: false, error: error.value, message: "Failed to fetch the applications" }
        }finally {
            loading.value = false
            error.value = null
        }
    }

    const createInspection = async (data) => {
        try{
            loading.value = true
            error.value = false

            const response = await axios.post(baseUrl.value, data)

            if(response.status === 201){
                schedule.value = response.data
                return { success: true, data: response.data, message: "Inspection created successfully" }
            }
            return { success: false, error: response.data.message, message: "Failed to create the inspection" }
        }catch (error) {
            console.error("Error fetching inspections:", error.response?.data || error.message)
            error.value = error.response?.message
            return { success: false, error: error.value, message: "Failed to create the inspection, error occurred" }
        }finally {
            loading.value = false
            error.value = null
        }
    }

    const updateInspection = async (id, data) => {
        try{
            loading.value = true
            error.value = false

            const response = await axios.put(`${baseUrl.value}/${id}`, data)
            if(response.status === 200){
                return { success: true, message: "Inspection updated successfully" , data: response.data }
            }

            return { success: false, error: response.data.message, message: "Failed to update the inspection" }
        }catch (error) {
            console.error("Error fetching inspections:", error.response?.data || error.message)
            error.value = error.response?.message
            return { success: false, error: error.value , message: "Failed to update the inspection, error occurred" }
        }finally {
            loading.value = false
            error.value = null
        }
    }

    const deleteInspection = async (id) => {
        try{
            loading.value = true
            error.value = false

            const response = await axios.delete(`${baseUrl.value}/${id}`)
            if(response.status === 200){
                return { success: true, message: "Inspection deleted successfully" }
            }
            return { success: false, error: response.data.message, message: "Failed to delete the inspection" }

        }catch (error) {
            console.error("Error fetching inspections:", error.response?.data || error.message)
            error.value = error.response?.message
            return { success: false, error: error.value, message: "Failed to delete the inspection" }
        }finally {
            loading.value = false
            error.value = null
        }
    }

    return {
        isLoading,
        errorMessage,
        allInspections,
        getInspectionSchedule,
        fetchInspections,
        createInspection,
        updateInspection,
        deleteInspection,
    }
})
