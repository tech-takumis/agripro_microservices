<template>
  <div
    v-if="show"
    class="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm transition-all"
    aria-labelledby="modal-title"
    role="dialog"
    aria-modal="true"
  >
    <div
      class="bg-white rounded-2xl shadow-2xl w-full max-w-md mx-4 overflow-hidden transform transition-all"
    >
      <!-- Header -->
      <div class="flex items-center justify-between px-6 py-4 border-b border-gray-200">
        <h3 class="text-lg font-semibold text-gray-800">Filter Applications</h3>
        <button
          @click="closeModal"
          class="text-gray-400 hover:text-gray-600 transition"
        >
          <X class="h-5 w-5" />
        </button>
      </div>

      <!-- Body -->
      <div class="px-6 py-5 space-y-4">
        <!-- Crop Type -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">Crop Type</label>
          <select
            v-model="localFilters.cropType"
            class="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-400 focus:border-green-400 transition"
          >
            <option value="">All Crops</option>
            <option value="Rice">Rice</option>
            <option value="Corn">Corn</option>
            <option value="Vegetables">Vegetables</option>
          </select>
        </div>

        <!-- Cover Type -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">Cover Type</label>
          <select
            v-model="localFilters.coverType"
            class="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-400 focus:border-green-400 transition"
          >
            <option value="">All Types</option>
            <option value="Multi-Risk">Multi-Risk</option>
            <option value="Natural Disaster">Natural Disaster</option>
            <option value="Crop Loss">Crop Loss</option>
          </select>
        </div>

        <!-- Location -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">Location</label>
          <input
            v-model="localFilters.location"
            type="text"
            placeholder="Search by barangay, city, or province"
            class="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-400 focus:border-green-400 transition"
          />
        </div>

        <!-- Dates -->
        <div class="grid grid-cols-2 gap-3">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Date From</label>
            <input
              v-model="localFilters.dateFrom"
              type="date"
              class="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-400 focus:border-green-400 transition"
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Date To</label>
            <input
              v-model="localFilters.dateTo"
              type="date"
              class="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-400 focus:border-green-400 transition"
            />
          </div>
        </div>

        <!-- Amount -->
        <div class="grid grid-cols-2 gap-3">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Min Amount</label>
            <input
              v-model="localFilters.amountMin"
              type="number"
              placeholder="0"
              class="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-400 focus:border-green-400 transition"
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Max Amount</label>
            <input
              v-model="localFilters.amountMax"
              type="number"
              placeholder="1000000"
              class="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-400 focus:border-green-400 transition"
            />
          </div>
        </div>
      </div>

      <!-- Footer -->
      <div class="bg-gray-50 px-6 py-4 flex justify-end gap-3 border-t border-gray-200">
        <button
          @click="handleReset"
          class="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-lg shadow-sm hover:bg-gray-100 transition"
        >
          Reset
        </button>
        <button
          @click="handleApply"
          class="px-4 py-2 text-sm font-medium text-white bg-green-600 rounded-lg shadow-sm hover:bg-green-700 focus:ring-2 focus:ring-offset-2 focus:ring-green-500 transition"
        >
          Apply Filters
        </button>
      </div>
    </div>
  </div>
</template>


<script setup>
import { ref, watch } from 'vue'
import { X } from 'lucide-vue-next'

const props = defineProps({
    show: {
        type: Boolean,
        required: true
    },
    filters: {
        type: Object,
        required: true
    }
})

const emit = defineEmits(['update:show', 'apply-filters', 'reset-filters'])

const localFilters = ref({ ...props.filters })

// Watch for external filter changes
watch(() => props.filters, (newFilters) => {
    localFilters.value = { ...newFilters }
}, { deep: true })

const closeModal = () => {
    emit('update:show', false)
}

const handleApply = () => {
    emit('apply-filters', localFilters.value)
}

const handleReset = () => {
    localFilters.value = {
        cropType: '',
        coverType: '',
        location: '',
        dateFrom: '',
        dateTo: '',
        amountMin: '',
        amountMax: ''
    }
    emit('reset-filters')
}
</script>
