<script setup>
import { ref, computed, onMounted } from 'vue'
import AuthenticatedLayout from '@/layouts/AuthenticatedLayout.vue'
import { MUNICIPAL_AGRICULTURIST_NAVIGATION } from '@/lib/navigation'
import { useRouter } from 'vue-router'
import { useFarmerStore } from '@/stores/farmer'
import { Search, Printer, RotateCcw, Check } from 'lucide-vue-next'

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
    page-title="Create Voucher"
  >
    <!-- ===== MAIN CONTENT (Visible on screen, hidden when printing) ===== -->
    <div class="p-4 sm:p-6 min-h-screen flex flex-col space-y-6 print:hidden">

      <!-- Header Section -->
      <div class="flex flex-col sm:flex-row sm:justify-between items-start sm:items-center border-b border-gray-200 pb-3">
        <div class="flex items-center gap-2">
          <FileText class="w-6 h-6 text-green-600" />
          <h1 class="text-2xl font-semibold text-gray-900 leading-tight">
            Create Voucher
          </h1>
        </div>

        <div class="flex items-center gap-3 mt-3 sm:mt-0">
          <button
            @click="resetForm"
            class="flex items-center gap-2 px-4 py-2 text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors shadow-sm"
          >
            <RotateCcw class="w-4 h-4 text-gray-500" />
            <span>Reset</span>
          </button>

          <button
            @click="handlePrint"
            :disabled="!isFormValid"
            class="flex items-center gap-2 px-5 py-2 text-white bg-green-600 rounded-lg hover:bg-green-700 transition-colors disabled:bg-gray-300 disabled:cursor-not-allowed shadow-sm"
          >
            <Printer class="w-4 h-4" />
            <span>Print Vouchers</span>
          </button>
        </div>
      </div>

      <!-- ===== Two-Column Layout ===== -->
      <div class="grid grid-cols-1 xl:grid-cols-2 gap-6 flex-grow">
        <!-- === Farmer Selection === -->
        <div class="bg-white border border-gray-200 rounded-2xl shadow-sm p-6 flex flex-col h-[550px]">
          <div class="flex items-center justify-between mb-4">
            <h2 class="text-lg font-semibold text-gray-900 flex items-center gap-2">
              <User class="w-5 h-5 text-green-600" />
              Select Farmers
            </h2>
            <span class="text-sm text-gray-600">{{ selectedFarmers.length }} selected</span>
          </div>

          <div class="relative mb-4">
            <Search class="absolute left-3 top-1/2 transform -translate-y-1/2 w-5 h-5 text-gray-400" />
            <input
              v-model="searchQuery"
              type="text"
              placeholder="Search farmers..."
              class="w-full pl-10 pr-4 py-2.5 border border-gray-300 rounded-xl focus:border-green-400 focus:ring-2 focus:ring-green-300 transition duration-200"
            />
          </div>

          <div class="flex gap-3 mb-4">
            <button
              @click="selectAllFiltered"
              class="flex-1 py-2 text-sm text-white bg-green-600 border border-green-200 rounded-xl hover:bg-green-700 transition-colors"
            >
              Select All
            </button>
            <button
              @click="clearSelection"
              class="flex-1 py-2 text-sm text-white bg-red-500 border border-red-200 rounded-xl hover:bg-red-600 transition-colors"
            >
              Clear
            </button>
          </div>

          <div class="border border-gray-200 rounded-xl flex-1 overflow-y-auto divide-y divide-gray-100">
            <div
              v-for="farmer in filteredFarmers"
              :key="farmer.id"
              @click="toggleFarmerSelection(farmer)"
              class="flex items-center justify-between p-3 hover:bg-green-50 cursor-pointer transition-colors"
            >
              <div class="flex items-center gap-3">
                <div
                  class="w-5 h-5 rounded-md border-2 flex items-center justify-center transition-all"
                  :class="isFarmerSelected(farmer) ? 'bg-green-600 border-green-600' : 'border-gray-300'"
                >
                  <Check v-if="isFarmerSelected(farmer)" class="w-3.5 h-3.5 text-white" />
                </div>
                <p class="font-medium text-gray-900">{{ farmer.name }}</p>
              </div>
            </div>

            <div v-if="filteredFarmers.length === 0" class="p-6 text-center text-gray-500 text-sm">
              No farmers found
            </div>
          </div>
        </div>

<!-- === Voucher Information === -->
<div class="bg-white border border-gray-200 rounded-2xl shadow-sm p-6 flex flex-col h-[550px]">
  <!-- Header -->
  <div class="flex items-center gap-2 mb-5">
    <FileText class="w-5 h-5 text-green-600" />
    <h2 class="text-lg font-semibold text-gray-900">Voucher Information</h2>
  </div>

  <!-- Scrollable Form -->
  <div class="flex-1 space-y-5 pr-1"> <!-- Removed overflow-y-auto -->
    <!-- Voucher Title -->
    <div>
      <label class="block text-sm font-medium text-gray-700 mb-1">
        Voucher Title <span class="text-red-500">*</span>
      </label>
      <input
        v-model="voucherInfo.title"
        type="text"
        placeholder="e.g., Rice Seeds Distribution 2024"
        class="w-full px-3 py-2.5 border border-gray-300 rounded-xl 
               focus:outline-none focus:border-green-400 
               focus:ring-2 focus:ring-green-300 
               transition duration-200"
      />
    </div>

    <!-- Voucher Type -->
    <div>
      <label class="block text-sm font-medium text-gray-700 mb-1">
        Voucher Type <span class="text-red-500">*</span>
      </label>
      <select
        v-model="voucherInfo.voucherType"
        class="w-full px-3 py-2.5 border border-gray-300 rounded-xl 
               focus:outline-none focus:border-green-400 
               focus:ring-2 focus:ring-green-300 
               transition duration-200 bg-white"
      >
        <option v-for="type in voucherTypes" :key="type" :value="type">
          {{ type }}
        </option>
      </select>
    </div>

    <!-- Total Bags -->
    <div>
      <label class="block text-sm font-medium text-gray-700 mb-1">
        Total Bags <span class="text-red-500">*</span>
      </label>
      <input
        v-model="voucherInfo.totalBags"
        type="number"
        min="1"
        placeholder="e.g., 5"
        class="w-full px-3 py-2.5 border border-gray-300 rounded-xl 
               focus:outline-none focus:border-green-400 
               focus:ring-2 focus:ring-green-300 
               transition duration-200"
      />
    </div>

    <!-- Reference Number -->
    <div>
      <label class="block text-sm font-medium text-gray-700 mb-1">
        Reference Number
      </label>
      <div class="flex gap-2">
        <input
          v-model="voucherInfo.referenceNumber"
          type="text"
          readonly
          class="flex-1 px-3 py-2.5 border border-gray-300 rounded-xl 
                 bg-gray-50 text-gray-700 cursor-not-allowed"
        />
        <button
          @click="generateReferenceNumber"
          class="px-3 py-2.5 text-sm font-medium text-white bg-green-600 
                 border border-green-200 rounded-xl hover:bg-green-700 
                 transition-colors"
        >
          Generate
        </button>
      </div>
    </div>

    <!-- Issue & Expiry Dates -->
    <div class="grid grid-cols-2 gap-4">
      <div>
        <label class="block text-sm font-medium text-gray-700 mb-1">
          Issue Date
        </label>
        <input
          v-model="voucherInfo.issueDate"
          type="date"
          class="w-full px-3 py-2.5 border border-gray-300 rounded-xl 
                 focus:outline-none focus:border-green-400 
                 focus:ring-2 focus:ring-green-300 
                 transition duration-200"
        />
      </div>
      <div>
        <label class="block text-sm font-medium text-gray-700 mb-1">
          Expiry Date
        </label>
        <input
          v-model="voucherInfo.expiryDate"
          type="date"
          class="w-full px-3 py-2.5 border border-gray-300 rounded-xl 
                 focus:outline-none focus:border-green-400 
                 focus:ring-2 focus:ring-green-300 
                 transition duration-200"
        />
      </div>
    </div>
  </div>
</div>


      </div>
    </div>

    <!-- ===== PRINT PREVIEW (Visible only on print) ===== -->
    <div v-if="showPrintPreview" class="print:block hidden">
      <div class="grid grid-cols-2 gap-6 p-6 print:p-4">
        <div
          v-for="(farmer, index) in selectedFarmers"
          :key="farmer.id"
          class="border-4 border-green-700 rounded-2xl p-6 bg-white break-inside-avoid shadow-md"
        >
          <div class="border-b-4 border-green-600 pb-3 mb-4 text-center">
            <div class="inline-block bg-green-700 text-white px-5 py-1.5 rounded-full mb-2">
              <h2 class="text-sm font-bold tracking-wide">BAYUGAN CITY</h2>
            </div>
            <h3 class="text-lg font-bold text-green-800">AGRICULTURE OFFICE</h3>
            <p class="text-xs font-semibold text-gray-700 mt-1 uppercase tracking-wide">
              {{ voucherInfo.voucherType }} Distribution Voucher
            </p>
          </div>

          <div class="space-y-3">
            <div class="border-2 border-green-200 rounded-lg p-3">
              <p class="text-xs text-gray-600 font-medium">Reference No.</p>
              <p class="text-sm font-bold text-green-800">
                {{ voucherInfo.referenceNumber }}-{{ String(index + 1).padStart(3, '0') }}
              </p>
            </div>

            <div class="bg-green-100 border-2 border-green-300 rounded-lg p-3">
              <p class="text-xs text-gray-600 font-medium">Program</p>
              <p class="text-base font-bold text-green-900">{{ voucherInfo.title }}</p>
            </div>

            <div class="border-2 border-green-200 rounded-lg p-3">
              <p class="text-xs text-gray-600 font-medium">Beneficiary</p>
              <p class="text-lg font-bold text-gray-900">{{ farmer.name }}</p>
            </div>

            <div class="bg-gradient-to-r from-green-600 to-green-700 text-white rounded-lg p-4 text-center">
              <p class="text-xs opacity-90">Total Bags</p>
              <p class="text-3xl font-bold">{{ voucherInfo.totalBags }}</p>
              <p class="text-xs opacity-90">{{ voucherInfo.voucherType }}</p>
            </div>
          </div>

          <div class="mt-5 pt-4 border-t-2 border-green-600 grid grid-cols-2 gap-4 text-center">
            <div>
              <div class="border-b-2 border-gray-800 mb-2 pb-10"></div>
              <p class="text-xs font-bold text-gray-900">Beneficiary Signature</p>
            </div>
            <div>
              <div class="border-b-2 border-gray-800 mb-2 pb-10"></div>
              <p class="text-xs font-bold text-gray-900">Authorized Signature</p>
            </div>
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
