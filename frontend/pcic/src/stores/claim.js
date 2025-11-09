import {defineStore} from 'pinia'
import {ref,computed} from 'vue'
import axios from '@/lib/axios'


export const useClaimStore = defineStore("claimStore",() => {
    const claims = ref([])
    const loading = ref(false);
    const error = ref(null)
    const basePath = ref("/api/v1/claims")

    const allClaims = computed(() => claims.value);
    const isLoading = computed(() => loading.value)
    const hasError = computed(() => error.value !== null)

    const fetchAllClaims = async () => {
        try{
            loading.value = true
            error.value = null

            const response = await axios.get(basePath.value);
            if(response.status === 200){
                claims.value = response.data
                return {success: "true", message: "Successfully fetch all claims", data: response.data}
            }
            return {success: "false",message: response.data.message, data: null};

        }catch (error){
            console.log("Failed to fetch all claims");
            error.value = error.data.message;
            loading.value = false
            return {success: "false", message: error.value, data: null}
        }finally {
            loading.value = false
            error.value = null
        }
    }

    const createClaim = async (data) => {
        try {
            loading.value = true
            error.value = null

            const response = await axios.post(basePath.value, data)

            if (response.status === 201) {
                claims.value.push(response.data)
                return {
                    success: 'true',
                    message: 'Successfully create claim',
                    data: response.data,
                }
            }

            return {
                success: 'false',
                message: response.data.message,
                data: null,
            }
        } catch (error) {
            console.log('Failed to create claim')
            error.value = error.data.message
            loading.value = false
            return { success: 'false', message: error.value, data: null }
        } finally {
            loading.value = false
            error.value = null
        }
    }

    const updateClaim = async (id, data) => {
        try{
            loading.value = true
            error.value = null

            const response = await axios.put(`${basePath.value}/${id}`, data);
            if(response.status === 200){
                const idx = claims.value.findIndex(claim => claim.id === id)
                if(idx !== -1){
                    claims.value[idx] = response.data
                }
                return {success: "true", message: "Successfully update claim", data: response.data}
            }
            return {success: "false",message: response.data.message, data: null};
        }catch (error){
            console.log("Failed to update claim");
            error.value = error.data.message;
            loading.value = false
            return {success: "false", message: error.value, data: null}
        }finally {
            loading.value = false
            error.value = null
        }
    }

    const deleteClaim = async (id) => {
        try{
            loading.value = true
            error.value = null

            const response = await axios.delete(`${basePath.value}/${id}`);
            if(response.status === 200){
                const idx = claims.value.findIndex(claim => claim.id === id)
                if(idx !== -1){
                    claims.value.splice(idx, 1)
                }
                return {success: "true", message: "Successfully delete claim", data: null}
            }

            return {success: "false",message: response.data.message, data: null};
        }catch (error){
            console.log("Failed to delete claim");
            error.value = error.data.message;
            loading.value = false
            return {success: "false", message: error.value, data: null}
        }finally {
            loading.value = false
            error.value = null
        }
    }

    return {
        allClaims,
        isLoading,
        hasError,
        fetchAllClaims,
        createClaim,
        updateClaim,
        deleteClaim,
    }
})
