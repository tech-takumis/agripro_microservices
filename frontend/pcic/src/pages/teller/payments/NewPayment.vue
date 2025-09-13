<template>
  <SidebarLayout 
    :navigation="tellerNavigation" 
    role-title="Teller Portal"
    page-title="New Payment"
  >
    <template #header>
      <div class="flex items-center justify-between">
        <div>
          <h1 class="text-2xl font-bold text-gray-900">New Payment</h1>
          <p class="text-gray-600">Process customer premium payments</p>
        </div>
        <router-link 
          to="/teller/dashboard"
          class="inline-flex items-center px-4 py-2 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50"
        >
          <ArrowLeft class="h-4 w-4 mr-2" />
          Back to Dashboard
        </router-link>
      </div>
    </template>

    <div class="max-w-4xl mx-auto">
      <div class="bg-white rounded-lg shadow-sm border border-gray-200">
        <div class="px-6 py-4 border-b border-gray-200">
          <h2 class="text-lg font-semibold text-gray-900">Payment Information</h2>
        </div>
        
        <form @submit.prevent="processPayment" class="p-6 space-y-6">
          <!-- Customer Information -->
          <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">
                Policy Number
              </label>
              <input
                v-model="form.policyNumber"
                type="text"
                required
                class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-green-500 focus:border-green-500"
                placeholder="Enter policy number"
              />
            </div>
            
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">
                Customer Name
              </label>
              <input
                v-model="form.customerName"
                type="text"
                required
                class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-green-500 focus:border-green-500"
                placeholder="Enter customer name"
              />
            </div>
          </div>

          <!-- Payment Details -->
          <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">
                Payment Amount
              </label>
              <div class="relative">
                <span class="absolute left-3 top-2 text-gray-500">₱</span>
                <input
                  v-model="form.amount"
                  type="number"
                  step="0.01"
                  required
                  class="w-full pl-8 pr-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-green-500 focus:border-green-500"
                  placeholder="0.00"
                />
              </div>
            </div>
            
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">
                Payment Method
              </label>
              <select
                v-model="form.paymentMethod"
                required
                class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-green-500 focus:border-green-500"
              >
                <option value="">Select payment method</option>
                <option value="cash">Cash</option>
                <option value="check">Check</option>
                <option value="bank_transfer">Bank Transfer</option>
              </select>
            </div>
          </div>

          <!-- Additional Information -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">
              Notes (Optional)
            </label>
            <textarea
              v-model="form.notes"
              rows="3"
              class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-green-500 focus:border-green-500"
              placeholder="Add any additional notes..."
            ></textarea>
          </div>

          <!-- Action Buttons -->
          <div class="flex justify-end space-x-4 pt-6 border-t border-gray-200">
            <button
              type="button"
              @click="resetForm"
              class="px-4 py-2 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50"
            >
              Reset
            </button>
            <button
              type="submit"
              :disabled="processing"
              class="px-6 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-green-600 hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-green-500 disabled:opacity-50"
            >
              <Loader2 v-if="processing" class="animate-spin h-4 w-4 mr-2" />
              {{ processing ? 'Processing...' : 'Process Payment' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </SidebarLayout>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowLeft, Loader2 } from 'lucide-vue-next'
import SidebarLayout from '@/layouts/SidebarLayout.vue'

const router = useRouter()

// Same navigation as dashboard
const tellerNavigation = [
  // ... same navigation items
]

const form = ref({
  policyNumber: '',
  customerName: '',
  amount: '',
  paymentMethod: '',
  notes: ''
})

const processing = ref(false)

const processPayment = async () => {
  processing.value = true
  
  try {
    // Simulate API call
    await new Promise(resolve => setTimeout(resolve, 2000))
    
    // Show success message and redirect
    alert('Payment processed successfully!')
    router.push('/teller/dashboard')
  } catch (error) {
    alert('Error processing payment. Please try again.')
  } finally {
    processing.value = false
  }
}

const resetForm = () => {
  form.value = {
    policyNumber: '',
    customerName: '',
    amount: '',
    paymentMethod: '',
    notes: ''
  }
}
</script>