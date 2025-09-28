<template>
    <div class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
        <div class="bg-white rounded-lg shadow-xl max-w-2xl w-full mx-4 max-h-[90vh] overflow-y-auto">
            Modal Header
            <div class="flex items-center justify-between p-6 border-b border-gray-200">
                <h2 class="text-xl font-semibold text-gray-900">Filter Applications</h2>
                <button
                    @click="$emit('close')"
                    class="text-gray-400 hover:text-gray-600 transition-colors"
                >
                    <XIcon class="w-6 h-6" />
                </button>
            </div>

            Filter Form
            <div class="p-6 space-y-6">
                Status Filter
                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-2">Status</label>
                    <select
                        v-model="localFilters.status"
                        class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                    >
                        <option value="">All Statuses</option>
                        <option v-for="status in availableOptions.statuses" :key="status" :value="status">
                            {{ status }}
                        </option>
                    </select>
                </div>

                Crop Type Filter
                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-2">Crop Type</label>
                    <select
                        v-model="localFilters.cropType"
                        class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                    >
                        <option value="">All Crop Types</option>
                        <option v-for="crop in availableOptions.cropTypes" :key="crop" :value="crop">
                            {{ crop }}
                        </option>
                    </select>
                </div>

                Coverage Amount Range
                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-2">Coverage Amount Range</label>
                    <div class="grid grid-cols-2 gap-4">
                        <input
                            v-model.number="localFilters.minCoverage"
                            type="number"
                            placeholder="Min Amount"
                            class="px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                        />
                        <input
                            v-model.number="localFilters.maxCoverage"
                            type="number"
                            placeholder="Max Amount"
                            class="px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                        />
                    </div>
                </div>

                Date Range
                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-2">Submission Date Range</label>
                    <div class="grid grid-cols-2 gap-4">
                        <input
                            v-model="localFilters.startDate"
                            type="date"
                            class="px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                        />
                        <input
                            v-model="localFilters.endDate"
                            type="date"
                            class="px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                        />
                    </div>
                </div>

                Search by Name
                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-2">Search by Name</label>
                    <input
                        v-model="localFilters.searchName"
                        type="text"
                        placeholder="Enter applicant name..."
                        class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                    />
                </div>
            </div>

            Modal Footer
            <div class="flex items-center justify-between p-6 border-t border-gray-200">
                <button
                    @click="clearFilters"
                    class="px-4 py-2 text-gray-600 hover:text-gray-800 transition-colors"
                >
                    Clear All Filters
                </button>
                <div class="flex gap-3">
                    <button
                        @click="$emit('close')"
                        class="px-4 py-2 border border-gray-300 text-gray-700 rounded-md hover:bg-gray-50 transition-colors"
                    >
                        Cancel
                    </button>
                    <button
                        @click="applyFilters"
                        class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors"
                    >
                        Apply Filters
                    </button>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { XIcon } from 'lucide-vue-next'

// Props
const props = defineProps({
    filters: {
        type: Object,
        required: true
    },
    availableOptions: {
        type: Object,
        required: true
    }
})

// Emits
const emit = defineEmits(['update:filters', 'close'])

// Local state for form
const localFilters = ref({})

// Methods
const clearFilters = () => {
    localFilters.value = {
        status: '',
        cropType: '',
        minCoverage: null,
        maxCoverage: null,
        startDate: '',
        endDate: '',
        searchName: ''
    }
}

const applyFilters = () => {
    emit('update:filters', { ...localFilters.value })
    emit('close')
}

// Initialize local filters
onMounted(() => {
    localFilters.value = { ...props.filters }
})
</script>