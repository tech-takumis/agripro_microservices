<script setup>
import { ref, computed, onMounted } from 'vue'
import AuthenticatedLayout from '@/layouts/AuthenticatedLayout.vue'
import { MUNICIPAL_AGRICULTURIST_NAVIGATION } from '@/lib/navigation'
import { useRouter } from 'vue-router'
import { useFarmerStore } from '@/stores/farmer'
import { Search, Printer, ArrowLeft, Check, X } from 'lucide-vue-next'

const navigation = MUNICIPAL_AGRICULTURIST_NAVIGATION
const router = useRouter()
const farmerStore = useFarmerStore()

// State
const selectedFarmers = ref([])
const searchQuery = ref('')
const showPrintPreview = ref(false)

// Voucher Information
const voucherInfo = ref({
  title: '',
  voucherType: 'Seeds',
  totalBags: '',
  issueDate: new Date().toISOString().split('T')[0],
  expiryDate: '',
  referenceNumber: ''
})

// Computed
const filteredFarmers = computed(() => {
  if (!searchQuery.value) return farmerStore.allFarmers
  const query = searchQuery.value.toLowerCase()
  return farmerStore.allFarmers.filter(farmer => 
    farmer.name?.toLowerCase().includes(query) ||
    farmer.firstName?.toLowerCase().includes(query) ||
    farmer.lastName?.toLowerCase().includes(query)
  )
})

const isFormValid = computed(() => {
  return voucherInfo.value.title && 
         voucherInfo.value.voucherType && 
         voucherInfo.value.totalBags && 
         selectedFarmers.value.length > 0
})

const voucherTypes = [
  'Seeds',
  'Fertilizer',
  'Equipment'
]

// Methods
const toggleFarmerSelection = (farmer) => {
  const index = selectedFarmers.value.findIndex(f => f.id === farmer.id)
  if (index > -1) {
    selectedFarmers.value.splice(index, 1)
  } else {
    selectedFarmers.value.push(farmer)
  }
}

const isFarmerSelected = (farmer) => {
  return selectedFarmers.value.some(f => f.id === farmer.id)
}

const selectAllFiltered = () => {
  filteredFarmers.value.forEach(farmer => {
    if (!isFarmerSelected(farmer)) {
      selectedFarmers.value.push(farmer)
    }
  })
}

const clearSelection = () => {
  selectedFarmers.value = []
}

const handlePrint = () => {
  showPrintPreview.value = true
  setTimeout(() => {
    window.print()
  }, 100)
}

const handleBack = () => {
  router.push({'name': "agriculturist-dashboard"})
}

const resetForm = () => {
  voucherInfo.value = {
    title: '',
    voucherType: 'Seeds',
    totalBags: '',
    issueDate: new Date().toISOString().split('T')[0],
    expiryDate: '',
    referenceNumber: ''
  }
  selectedFarmers.value = []
  showPrintPreview.value = false
}

// Generate reference number
const generateReferenceNumber = () => {
  const date = new Date()
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const random = Math.floor(Math.random() * 10000).toString().padStart(4, '0')
  voucherInfo.value.referenceNumber = `VCH-${year}${month}-${random}`
}

onMounted(async () => {
  await farmerStore.fetchFarmers()
  generateReferenceNumber()
})
</script>

<template>
  <AuthenticatedLayout
    :navigation="navigation"
    role-title="Municipal Agriculturist"
    page-title="Create Voucher">
    
    <!-- Main Content - Hidden when printing -->
    <div class="space-y-6 overflow-y-auto h-full print:hidden">
      <!-- Header -->
      <div class="flex items-center justify-between">
        <div>
          <h1 class="text-2xl font-bold text-gray-900">Create Voucher</h1>
          <p class="text-gray-600 mt-1">Select farmers and generate vouchers for distribution</p>
        </div>
        <button
          @click="handleBack"
          class="flex items-center gap-2 px-4 py-2 text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors">
          <ArrowLeft class="w-4 h-4" />
          Back
        </button>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <!-- Left Column: Farmer Selection -->
        <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <div class="flex items-center justify-between mb-4">
            <h2 class="text-lg font-semibold text-gray-900">Select Farmers</h2>
            <span class="text-sm text-gray-600">{{ selectedFarmers.length }} selected</span>
          </div>

          <!-- Search -->
          <div class="relative mb-4">
            <Search class="absolute left-3 top-1/2 transform -translate-y-1/2 w-5 h-5 text-gray-400" />
            <input
              v-model="searchQuery"
              type="text"
              placeholder="Search farmers..."
              class="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent"
            />
          </div>

          <!-- Action Buttons -->
          <div class="flex gap-2 mb-4">
            <button
              @click="selectAllFiltered"
              class="flex-1 px-3 py-2 text-sm text-green-700 bg-green-50 border border-green-200 rounded-lg hover:bg-green-100 transition-colors">
              Select All
            </button>
            <button
              @click="clearSelection"
              class="flex-1 px-3 py-2 text-sm text-red-700 bg-red-50 border border-red-200 rounded-lg hover:bg-red-100 transition-colors">
              Clear Selection
            </button>
          </div>

          <!-- Farmers List -->
          <div class="border border-gray-200 rounded-lg max-h-96 overflow-y-auto">
            <div
              v-for="farmer in filteredFarmers"
              :key="farmer.id"
              @click="toggleFarmerSelection(farmer)"
              class="flex items-center justify-between p-3 hover:bg-gray-50 cursor-pointer border-b border-gray-100 last:border-b-0 transition-colors">
              <div class="flex items-center gap-3">
                <div
                  class="w-5 h-5 rounded border-2 flex items-center justify-center transition-colors"
                  :class="isFarmerSelected(farmer) ? 'bg-green-600 border-green-600' : 'border-gray-300'">
                  <Check v-if="isFarmerSelected(farmer)" class="w-4 h-4 text-white" />
                </div>
                <div>
                  <p class="font-medium text-gray-900">{{ farmer.name }}</p>
                </div>
              </div>
            </div>
            <div v-if="filteredFarmers.length === 0" class="p-8 text-center text-gray-500">
              No farmers found
            </div>
          </div>
        </div>

        <!-- Right Column: Voucher Information -->
        <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <h2 class="text-lg font-semibold text-gray-900 mb-4">Voucher Information</h2>

          <div class="space-y-4">
            <!-- Title -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Voucher Title *</label>
              <input
                v-model="voucherInfo.title"
                type="text"
                placeholder="e.g., Rice Seeds Distribution 2024"
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent"
              />
            </div>

            <!-- Voucher Type -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Voucher Type *</label>
              <select
                v-model="voucherInfo.voucherType"
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent">
                <option v-for="type in voucherTypes" :key="type" :value="type">{{ type }}</option>
              </select>
            </div>

            <!-- Total Bags -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Total Bags *</label>
              <input
                v-model="voucherInfo.totalBags"
                type="number"
                min="1"
                placeholder="e.g., 5"
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent"
              />
            </div>

            <!-- Reference Number -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Reference Number</label>
              <div class="flex gap-2">
                <input
                  v-model="voucherInfo.referenceNumber"
                  type="text"
                  readonly
                  class="flex-1 px-3 py-2 border border-gray-300 rounded-lg bg-gray-50"
                />
                <button
                  @click="generateReferenceNumber"
                  class="px-3 py-2 text-sm text-green-700 bg-green-50 border border-green-200 rounded-lg hover:bg-green-100 transition-colors">
                  Generate
                </button>
              </div>
            </div>

            <!-- Dates -->
            <div class="grid grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Issue Date</label>
                <input
                  v-model="voucherInfo.issueDate"
                  type="date"
                  class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent"
                />
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Expiry Date</label>
                <input
                  v-model="voucherInfo.expiryDate"
                  type="date"
                  class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent"
                />
              </div>
            </div>

          </div>
        </div>
      </div>

      <!-- Action Buttons -->
      <div class="flex items-center justify-end gap-4 pb-6">
        <button
          @click="resetForm"
          class="px-6 py-2.5 text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors">
          Reset
        </button>
        <button
          @click="handlePrint"
          :disabled="!isFormValid"
          class="flex items-center gap-2 px-6 py-2.5 text-white bg-green-600 rounded-lg hover:bg-green-700 transition-colors disabled:bg-gray-300 disabled:cursor-not-allowed">
          <Printer class="w-4 h-4" />
          Print Vouchers
        </button>
      </div>
    </div>

    <!-- Print Preview - Two Column Layout -->
    <div v-if="showPrintPreview" class="hidden print:block">
      <div class="grid grid-cols-2 gap-6 p-6">
        <div
          v-for="(farmer, index) in selectedFarmers"
          :key="farmer.id"
          class="border-4 border-green-700 rounded-xl p-6 break-inside-avoid bg-gradient-to-br from-green-50 to-white shadow-lg"
          :class="{ 'page-break-before': index % 4 === 0 && index !== 0 }">
          
          <!-- Decorative Top Border -->
          <div class="border-b-4 border-green-600 pb-4 mb-4">
            <!-- Logo/Header Section -->
            <div class="text-center">
              <div class="inline-block bg-green-700 text-white px-6 py-2 rounded-full mb-2">
                <h2 class="text-base font-bold tracking-wide">BAYUGAN CITY</h2>
              </div>
              <h3 class="text-xl font-bold text-green-800 mb-1">AGRICULTURE OFFICE</h3>
              <div class="bg-green-600 h-1 w-24 mx-auto rounded-full mb-2"></div>
              <p class="text-sm font-semibold text-gray-700 uppercase tracking-wide">{{ voucherInfo.voucherType }} Distribution Voucher</p>
            </div>
          </div>

          <!-- Voucher Details -->
          <div class="space-y-3">
            <!-- Reference Number -->
            <div class="bg-white border-2 border-green-200 rounded-lg p-3">
              <p class="text-xs text-gray-600 font-medium mb-1">Reference Number</p>
              <p class="text-sm font-bold text-green-800">{{ voucherInfo.referenceNumber }}-{{ String(index + 1).padStart(3, '0') }}</p>
            </div>
            
            <!-- Program Title -->
            <div class="bg-green-100 border-2 border-green-300 rounded-lg p-3">
              <p class="text-xs text-gray-600 font-medium mb-1">Program</p>
              <p class="text-base font-bold text-green-900">{{ voucherInfo.title }}</p>
            </div>

            <!-- Beneficiary Information -->
            <div class="bg-white border-2 border-green-200 rounded-lg p-3">
              <p class="text-xs text-gray-600 font-medium mb-2">Beneficiary</p>
              <p class="text-lg font-bold text-gray-900">{{ farmer.name }}</p>
            </div>

            <!-- Total Bags -->
            <div class="bg-gradient-to-r from-green-600 to-green-700 text-white rounded-lg p-4 text-center">
              <p class="text-xs font-medium mb-1 opacity-90">Total Bags</p>
              <p class="text-3xl font-bold">{{ voucherInfo.totalBags }}</p>
              <p class="text-xs mt-1 opacity-90">{{ voucherInfo.voucherType }}</p>
            </div>

            <!-- Dates -->
            <div class="grid grid-cols-2 gap-2">
              <div class="bg-white border border-green-200 rounded-lg p-2 text-center">
                <p class="text-xs text-gray-600 font-medium">Issue Date</p>
                <p class="text-sm font-bold text-gray-900">{{ new Date(voucherInfo.issueDate).toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' }) }}</p>
              </div>
              <div v-if="voucherInfo.expiryDate" class="bg-white border border-green-200 rounded-lg p-2 text-center">
                <p class="text-xs text-gray-600 font-medium">Expiry Date</p>
                <p class="text-sm font-bold text-gray-900">{{ new Date(voucherInfo.expiryDate).toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' }) }}</p>
              </div>
            </div>
          </div>

          <!-- Signature Section -->
          <div class="mt-5 pt-4 border-t-2 border-green-600">
            <div class="grid grid-cols-2 gap-4">
              <div class="text-center">
                <div class="border-b-2 border-gray-800 mb-2 pb-10"></div>
                <p class="text-xs font-bold text-gray-900">Beneficiary Signature</p>
                <p class="text-xs text-gray-600 mt-1">Date: __________</p>
              </div>
              <div class="text-center">
                <div class="border-b-2 border-gray-800 mb-2 pb-10"></div>
                <p class="text-xs font-bold text-gray-900">Authorized Signature</p>
                <p class="text-xs text-gray-600 mt-1">Municipal Agriculturist</p>
              </div>
            </div>
          </div>

          <!-- Footer Note -->
          <div class="mt-3 text-center">
            <p class="text-xs text-gray-500 italic">This voucher is non-transferable and must be claimed by the beneficiary.</p>
          </div>
        </div>
      </div>
    </div>
  </AuthenticatedLayout>
</template>

<style scoped>
@media print {
  @page {
    size: 8.5in 13in; /* Bond paper size */
    margin: 0.5in;
  }
  
  .page-break-before {
    page-break-before: always;
  }
  
  .break-inside-avoid {
    break-inside: avoid;
  }
}
</style>
