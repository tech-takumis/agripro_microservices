<template>
  <div v-if="isOpen" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
    <div class="bg-white rounded-lg shadow-xl max-w-md w-full mx-4">
      <!-- Modal Header -->
      <div class="flex items-center justify-between p-6 border-b border-gray-200">
        <h2 class="text-xl font-semibold text-gray-900">Filter Options</h2>
        <button
          @click="$emit('close')"
          class="text-gray-400 hover:text-gray-600 transition-colors"
        >
          <X class="w-6 h-6" />
        </button>
      </div>

      <!-- Filter Form -->
      <div class="p-6 space-y-4">
        <!-- Layout Filter -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">Layout Type</label>
          <select
            v-model="localFilters.layout"
            class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
          >
            <option value="">All Layouts</option>
            <option value="single-step">Single Step</option>
            <option value="two-step">Two Step</option>
            <option value="multi-step">Multi Step</option>
          </select>
        </div>

        <!-- Section Count Filter -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">Number of Sections</label>
          <select
            v-model="localFilters.sectionCount"
            class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
          >
            <option value="">Any</option>
            <option value="1">1 Section</option>
            <option value="2">2 Sections</option>
            <option value="3">3+ Sections</option>
          </select>
        </div>

        <!-- Field Count Filter -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">Total Fields</label>
          <select
            v-model="localFilters.fieldCount"
            class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
          >
            <option value="">Any</option>
            <option value="1-5">1-5 Fields</option>
            <option value="6-10">6-10 Fields</option>
            <option value="11+">11+ Fields</option>
          </select>
        </div>

        <!-- Sort By -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">Sort By</label>
          <select
            v-model="localFilters.sortBy"
            class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
          >
            <option value="name">Name (A-Z)</option>
            <option value="name-desc">Name (Z-A)</option>
            <option value="sections">Section Count</option>
            <option value="fields">Field Count</option>
            <option value="recent">Recently Created</option>
          </select>
        </div>
      </div>

      <!-- Modal Footer -->
      <div class="flex items-center justify-between p-6 border-t border-gray-200">
        <button
          @click="handleClear"
          class="px-4 py-2 text-sm text-gray-600 hover:text-gray-800 transition-colors"
        >
          Clear All
        </button>
        <div class="flex gap-3">
          <button
            @click="$emit('close')"
            class="px-4 py-2 border border-gray-300 text-sm text-gray-700 rounded-md hover:bg-gray-50 transition-colors"
          >
            Cancel
          </button>
          <button
            @click="handleApply"
            class="px-4 py-2 bg-indigo-600 text-sm text-white rounded-md hover:bg-indigo-700 transition-colors"
          >
            Apply Filters
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { X } from 'lucide-vue-next'

// Props
const props = defineProps({
  isOpen: {
    type: Boolean,
    default: false
  },
  selectedLayout: {
    type: String,
    default: ''
  },
  selectedSectionCount: {
    type: String,
    default: ''
  },
  selectedFieldCount: {
    type: String,
    default: ''
  },
  sortBy: {
    type: String,
    default: 'name'
  }
})

// Emits
const emit = defineEmits(['close', 'apply', 'clear'])

// Local state for form
const localFilters = ref({
  layout: props.selectedLayout,
  sectionCount: props.selectedSectionCount,
  fieldCount: props.selectedFieldCount,
  sortBy: props.sortBy
})

// Watch props to update local filters when they change
watch(() => props.isOpen, (newVal) => {
  if (newVal) {
    localFilters.value = {
      layout: props.selectedLayout,
      sectionCount: props.selectedSectionCount,
      fieldCount: props.selectedFieldCount,
      sortBy: props.sortBy
    }
  }
})

// Methods
const handleClear = () => {
  localFilters.value = {
    layout: '',
    sectionCount: '',
    fieldCount: '',
    sortBy: 'name'
  }
  emit('clear')
}

const handleApply = () => {
  emit('apply', { ...localFilters.value })
}
</script>