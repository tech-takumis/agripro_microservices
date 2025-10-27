import axios from '../lib/axios'
import { ref } from 'vue'

export function useVerificationStore() {
  const isForwarding = ref(false)
  const error = ref(null)
  const basePath = ref("/api/v1/agriculture/verification")

  async function forwardApplicationToPCIC(applicationIds) {
    isForwarding.value = true
    error.value = null
    try {
      await axios.post(`${basePath.value}/forwards`, applicationIds)
      isForwarding.value = false
      return true
    } catch (e) {
      error.value = e
      isForwarding.value = false
      return false
    }
  }

  return {
    isForwarding,
    error,
    forwardApplicationToPCIC
  }
}
