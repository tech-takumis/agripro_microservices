import {defineStore} from 'pinia'
import { computed, ref } from 'vue'
import axios from '@/lib/axios'

export const useTransactionStore = defineStore('transaction', () =>  {
    // State
    const transactions = ref([])
    const loading = ref(false)
    const error = ref(null)


    // Getters as computed
    const allTransactions = computed(() => transactions.value)
    const isLoading = computed(() => loading.value)
    const getError = computed(() => error.value)

    // Actions as functions

    async function fetchTransactions() {
        loading.value = true
        error.value = null

        try {
            const response = await axios.get('/api/v1/transaction')
            transactions.value = response.data
            console.log('Fetched transactions:', transactions.value)
            return { success: true, data: transactions.value }
        }catch (error){
            console.error('Error fetching transactions:', error)
            return { success: false, error: error.message }
        }finally {
            loading.value = false
            console.log('Loading state:', loading.value)
        }

    }

    return {
        transactions,
        loading,
        error,
        allTransactions,
        isLoading,
        getError,
        fetchTransactions
    }
})