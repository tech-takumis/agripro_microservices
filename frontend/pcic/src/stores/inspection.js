import {defineStore} from 'pinia'
import {ref,computed} from 'vue'
import axios from '@/lib/axios'


export const useInspectionStore = defineStore('inspection', () => {
    const inspections = ref([])
    const loading = ref(false)
    const error = ref(null)
    const baseUrl = ref("/api/inspections")

    const isLoading = computed(() => loading.value)
    const errorMessage = computed(() => error.value)
    const getInspections = computed(() => inspections.value)


    const fetchInspections = async () => {
        try{
            loading.value = true
            error.value = false

            const response = await axios.get(baseUrl.value)

            inspections.value = response.data
            return { success: true, data: response.data }
        }catch (error) {
            console.error("Error fetching inspections:", error.response?.data || error.message)
            error.value = error.response?.data || error.message
            return { success: false, error: error.response?.data || error.message }
        }finally {
            loading.value = false
            error.value = null
        }
    }


    return {
        isLoading,
        errorMessage,
        fetchInspections
    }
})
