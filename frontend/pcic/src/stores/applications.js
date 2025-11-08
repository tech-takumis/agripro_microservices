import {defineStore} from 'pinia'
import {ref,computed} from 'vue'
import axios from '@/lib/axios'


export const useApplicationStore = defineStore("applicationStore",() => {
    const applications = ref([])
    const loading = ref(false);
    const error = ref(null)
    const basePath = ref("/api/v1/applications")


    const allApplications = computed(() => applications.value);
    const isLoading = computed(() => loading.value)
    const hasError = computed(() => error.value);


    const fetchAllApplications = async () => {
        try{
            loading.value = true
            error.value = null

            const response = await axios.get(basePath.value);

            if(response.status === 200){
                applications.value = response.data
                return {success: "true", message: "Successfully fetch all application"}
            }

            return {success: "false",message: response.data.message};
        }catch (error){
            console.log("Failed to fetch all applications");
            error.value = error.data.message;
            loading.value = false
            return {success: "false", message: error.value}
        }finally {
            loading.value = false
            error.value = null
        }

    }

});