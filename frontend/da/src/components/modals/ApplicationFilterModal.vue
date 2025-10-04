<template>
    <div
        v-if="show"
        class="fixed inset-0 z-50 overflow-y-auto"
        aria-labelledby="modal-title"
        role="dialog"
        aria-modal="true"
    >
        <div class="flex items-end justify-center min-h-screen pt-4 px-4 pb-20 text-center sm:block sm:p-0">
            <div
                class="fixed inset-0 bg-gray-500 bg-opacity-75 transition-opacity"
                @click="closeModal"
            ></div>
            <div class="inline-block align-bottom bg-white rounded-lg text-left overflow-hidden shadow-xl transform transition-all sm:my-8 sm:align-middle sm:max-w-lg sm:w-full">
                <div class="bg-white px-4 pt-5 pb-4 sm:p-6 sm:pb-4">
                    <div class="flex items-center justify-between mb-4">
                        <h3 class="text-lg font-medium text-gray-900" id="modal-title">
                            Filter Applications
                        </h3>
                        <button
                            @click="closeModal"
                            class="text-gray-400 hover:text-gray-500 focus:outline-none"
                        >
                            <X class="h-6 w-6" />
                        </button>
                    </div>

                    <div class="space-y-4">
                        <div>
                            <label for="crop-type" class="block text-sm font-medium text-gray-700 mb-1">
                                Crop Type
                            </label>
                            <select
                                id="crop-type"
                                v-model="localFilters.cropType"
                                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                            >
                                <option value="">All Crops</option>
                                <option value="Rice">Rice</option>
                                <option value="Corn">Corn</option>
                                <option value="Vegetables">Vegetables</option>
                            </select>
                        </div>

                        <div>
                            <label for="cover-type" class="block text-sm font-medium text-gray-700 mb-1">
                                Cover Type
                            </label>
                            <select
                                id="cover-type"
                                v-model="localFilters.coverType"
                                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                            >
                                <option value="">All Types</option>
                                <option value="Multi-Risk">Multi-Risk</option>
                                <option value="Natural Disaster">Natural Disaster</option>
                                <option value="Crop Loss">Crop Loss</option>
                            </select>
                        </div>

                        <div>
                            <label for="location" class="block text-sm font-medium text-gray-700 mb-1">
                                Location
                            </label>
                            <input
                                id="location"
                                v-model="localFilters.location"
                                type="text"
                                placeholder="Search by barangay, city, or province"
                                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                            />
                        </div>

                        <div class="grid grid-cols-2 gap-4">
                            <div>
                                <label for="date-from" class="block text-sm font-medium text-gray-700 mb-1">
                                    Date From
                                </label>
                                <input
                                    id="date-from"
                                    v-model="localFilters.dateFrom"
                                    type="date"
                                    class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                                />
                            </div>
                            <div>
                                <label for="date-to" class="block text-sm font-medium text-gray-700 mb-1">
                                    Date To
                                </label>
                                <input
                                    id="date-to"
                                    v-model="localFilters.dateTo"
                                    type="date"
                                    class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                                />
                            </div>
                        </div>

                        <div class="grid grid-cols-2 gap-4">
                            <div>
                                <label for="amount-min" class="block text-sm font-medium text-gray-700 mb-1">
                                    Min Amount
                                </label>
                                <input
                                    id="amount-min"
                                    v-model="localFilters.amountMin"
                                    type="number"
                                    placeholder="0"
                                    class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                                />
                            </div>
                            <div>
                                <label for="amount-max" class="block text-sm font-medium text-gray-700 mb-1">
                                    Max Amount
                                </label>
                                <input
                                    id="amount-max"
                                    v-model="localFilters.amountMax"
                                    type="number"
                                    placeholder="1000000"
                                    class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                                />
                            </div>
                        </div>
                    </div>
                </div>

                <div class="bg-gray-50 px-4 py-3 sm:px-6 sm:flex sm:flex-row-reverse gap-3">
                    <button
                        @click="handleApply"
                        class="w-full inline-flex justify-center rounded-lg border border-transparent shadow-sm px-4 py-2 bg-blue-600 text-base font-medium text-white hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 sm:ml-3 sm:w-auto sm:text-sm"
                    >
                        Apply Filters
                    </button>
                    <button
                        @click="handleReset"
                        class="mt-3 w-full inline-flex justify-center rounded-lg border border-gray-300 shadow-sm px-4 py-2 bg-white text-base font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 sm:mt-0 sm:w-auto sm:text-sm"
                    >
                        Reset
                    </button>
                </div>
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
