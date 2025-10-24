import {defineStore} from 'pinia'
import { computed, ref } from 'vue'
import axios from '@/lib/axios'


export const useBatchStore = defineStore('batch', () => {
    const batch = ref(null)
    const loading = ref(false)
    const error = ref(null)
    const basePath = ref('/api/v1/batches')

    // getter
    const allBatch = computed(() => batch.value || [])
    const isLoading = computed(() => isLoading.value || false)


    // actions
    const fetchBatch = async () => {
        try{
            loading.value = true
            error.value = null
            const response = await  axios.get(basePath.value,)

            if(response.status === 200){
                batch.value = response.data
                return { success: true, data: response.data }
            }else{
                return { success: false, error: response.data }
            }
        }catch (error){
            console.error('Error fetching batch:', error)
            error.value = error.message
            loading.value = false
            return { success: false, error: error.value }
        }finally {
            loading.value = false
        }
    }

    return {
        allBatch,
        isLoading,
        fetchBatch,
    }
})