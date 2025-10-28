import axios from '../lib/axios'
import { ref,computed } from 'vue'

export function useVerificationStore() {
  const applications = ref([])
  const isForwarding = ref(false)
  const loading = ref(false)
  const errors = ref([])
  const basePath = ref("/api/v1/verification")

  const allApplications = computed(() => applications.value || [])
  const isLoading = computed(() => loading.value)
  const errorsList = computed(() => errors.value || [])
  const isForwardingStatus = computed(() => isForwarding.value)

  async function forwardApplicationToPCIC(applicationIds) {
    isForwarding.value = true
    errors.value = null
    try {
      await axios.post(`${basePath.value}/forwards`, applicationIds)
      isForwarding.value = false
      return true
    } catch (e) {
      errors.value = e
      isForwarding.value = false
      return false
    }
  }

  const fetchApplications = async () => {
    try {
      const response = await axios.get(basePath.value)
      applications.value = response.data
      return { success: true, data: response.data }
    } catch (e) {
      console.error("Error fetching verification applications:", e.response?.data || e.message)
      errors.value = e.error
      return { success: false, error: e.response?.data || e.message }
    }
  }

  const fetchApplicationByBatchId = async (batchId) => {
      try{
        loading.value = true
        errors.value = null
        const response = await axios.get(`${basePath.value}/${batchId}/submissions`)
        applications.value = response.data
        return { success: true, data: response.data }
      }catch(err){
        loading.value = false
        errors.value = err.error
        console.error("Error fetching applications by batch ID:", err.response?.data || err.message)
        return { success: false, error: err.response?.data || err.message }
      }finally {
        loading.value = false
        errors.value = null
      }
  }

  return {
    applications,
    allApplications,
    isLoading,
    errorsList,
    isForwardingStatus,
    isForwarding,
    errors,
    forwardApplicationToPCIC,
    fetchApplications,
    fetchApplicationByBatchId
  }
}
