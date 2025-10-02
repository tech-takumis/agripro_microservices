<template>
    <div v-if="isOpen" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
        <div class="bg-white rounded-lg shadow-xl max-w-4xl w-full mx-4 max-h-[90vh] overflow-hidden">
            Header
            <div class="flex items-center justify-between p-6 border-b">
                <h2 class="text-xl font-semibold text-gray-900">
                    Application Details - {{ application?.dynamicFields?.full_name || 'N/A' }}
                </h2>
                <button
                    @click="$emit('close')"
                    class="text-gray-400 hover:text-gray-600 transition-colors"
                >
                    <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                    </svg>
                </button>
            </div>

            Content
            <div class="p-6 overflow-y-auto max-h-[calc(90vh-120px)]">
                <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
                    Personal Information
                    <div class="space-y-4">
                        <h3 class="text-lg font-medium text-gray-900 border-b pb-2">Personal Information</h3>
                        <div class="grid grid-cols-2 gap-4 text-sm">
                            <div>
                                <label class="font-medium text-gray-700">Full Name:</label>
                                <p class="text-gray-900">{{ application?.dynamicFields?.full_name || 'N/A' }}</p>
                            </div>
                            <div>
                                <label class="font-medium text-gray-700">Age:</label>
                                <p class="text-gray-900">{{ application?.dynamicFields?.age || 'N/A' }}</p>
                            </div>
                            <div>
                                <label class="font-medium text-gray-700">Sex:</label>
                                <p class="text-gray-900">{{ application?.dynamicFields?.sex || 'N/A' }}</p>
                            </div>
                            <div>
                                <label class="font-medium text-gray-700">Civil Status:</label>
                                <p class="text-gray-900">{{ application?.dynamicFields?.civil_status || 'N/A' }}</p>
                            </div>
                            <div class="col-span-2">
                                <label class="font-medium text-gray-700">Address:</label>
                                <p class="text-gray-900">{{ formatAddress(application?.dynamicFields) }}</p>
                            </div>
                            <div>
                                <label class="font-medium text-gray-700">Phone:</label>
                                <p class="text-gray-900">{{ application?.dynamicFields?.phone_number || 'N/A' }}</p>
                            </div>
                            <div>
                                <label class="font-medium text-gray-700">Email:</label>
                                <p class="text-gray-900">{{ application?.dynamicFields?.email_address || 'N/A' }}</p>
                            </div>
                        </div>
                    </div>

                    Application Info
                    <div class="space-y-4">
                        <h3 class="text-lg font-medium text-gray-900 border-b pb-2">Application Information</h3>
                        <div class="grid grid-cols-2 gap-4 text-sm">
                            <div>
                                <label class="font-medium text-gray-700">Application ID:</label>
                                <p class="text-gray-900 font-mono">{{ application?.id || 'N/A' }}</p>
                            </div>
                            <div>
                                <label class="font-medium text-gray-700">Status:</label>
                                <StatusBadge :status="application?.status" />
                            </div>
                            <div>
                                <label class="font-medium text-gray-700">Submitted:</label>
                                <p class="text-gray-900">{{ formatDate(application?.submittedAt) }}</p>
                            </div>
                            <div>
                                <label class="font-medium text-gray-700">Last Updated:</label>
                                <p class="text-gray-900">{{ formatDate(application?.updatedAt) }}</p>
                            </div>
                            <div>
                                <label class="font-medium text-gray-700">Version:</label>
                                <p class="text-gray-900">{{ application?.version || 'N/A' }}</p>
                            </div>
                        </div>
                    </div>

                    Crop Information
                    <div class="space-y-4">
                        <h3 class="text-lg font-medium text-gray-900 border-b pb-2">Crop Information</h3>
                        <div class="grid grid-cols-2 gap-4 text-sm">
                            <div>
                                <label class="font-medium text-gray-700">Crop Type:</label>
                                <p class="text-gray-900">{{ application?.dynamicFields?.crop_type || 'N/A' }}</p>
                            </div>
                            <div>
                                <label class="font-medium text-gray-700">Variety:</label>
                                <p class="text-gray-900">{{ application?.dynamicFields?.variety || 'N/A' }}</p>
                            </div>
                            <div>
                                <label class="font-medium text-gray-700">Area (hectares):</label>
                                <p class="text-gray-900">{{ application?.dynamicFields?.area_hectares || 'N/A' }}</p>
                            </div>
                            <div>
                                <label class="font-medium text-gray-700">Planting Date:</label>
                                <p class="text-gray-900">{{ formatDate(application?.dynamicFields?.planting_date) }}</p>
                            </div>
                            <div>
                                <label class="font-medium text-gray-700">Expected Harvest:</label>
                                <p class="text-gray-900">{{ formatDate(application?.dynamicFields?.expected_harvest_date) }}</p>
                            </div>
                            <div>
                                <label class="font-medium text-gray-700">Irrigation Source:</label>
                                <p class="text-gray-900">{{ application?.dynamicFields?.irrigation_source || 'N/A' }}</p>
                            </div>
                        </div>
                    </div>

                    Insurance Details
                    <div class="space-y-4">
                        <h3 class="text-lg font-medium text-gray-900 border-b pb-2">Insurance Details</h3>
                        <div class="grid grid-cols-2 gap-4 text-sm">
                            <div>
                                <label class="font-medium text-gray-700">Premium Amount:</label>
                                <p class="text-gray-900 font-semibold">₱{{ formatCurrency(application?.dynamicFields?.premium_amount) }}</p>
                            </div>
                            <div>
                                <label class="font-medium text-gray-700">Coverage Amount:</label>
                                <p class="text-gray-900 font-semibold">₱{{ formatCurrency(application?.dynamicFields?.coverage_amount) }}</p>
                            </div>
                            <div class="col-span-2">
                                <label class="font-medium text-gray-700">Primary Beneficiary:</label>
                                <p class="text-gray-900">{{ application?.dynamicFields?.primary_beneficiary || 'N/A' }}</p>
                            </div>
                            <div class="col-span-2">
                                <label class="font-medium text-gray-700">Secondary Beneficiary:</label>
                                <p class="text-gray-900">{{ application?.dynamicFields?.secondary_beneficiary || 'N/A' }}</p>
                            </div>
                        </div>
                    </div>

                    Location Details
                    <div class="space-y-4 lg:col-span-2">
                        <h3 class="text-lg font-medium text-gray-900 border-b pb-2">Farm Location</h3>
                        <div class="grid grid-cols-2 lg:grid-cols-4 gap-4 text-sm">
                            <div>
                                <label class="font-medium text-gray-700">Lot Number:</label>
                                <p class="text-gray-900">{{ application?.dynamicFields?.lot_number || 'N/A' }}</p>
                            </div>
                            <div>
                                <label class="font-medium text-gray-700">Survey Number:</label>
                                <p class="text-gray-900">{{ application?.dynamicFields?.survey_number || 'N/A' }}</p>
                            </div>
                            <div>
                                <label class="font-medium text-gray-700">Soil Type:</label>
                                <p class="text-gray-900">{{ application?.dynamicFields?.soil_type || 'N/A' }}</p>
                            </div>
                            <div>
                                <label class="font-medium text-gray-700">Topography:</label>
                                <p class="text-gray-900">{{ application?.dynamicFields?.topography || 'N/A' }}</p>
                            </div>
                        </div>

                        Boundaries
                        <div class="mt-4">
                            <label class="font-medium text-gray-700">Lot Boundaries:</label>
                            <div class="grid grid-cols-2 gap-2 mt-2 text-sm">
                                <div><span class="font-medium">North:</span> {{ application?.dynamicFields?.north_boundary || 'N/A' }}</div>
                                <div><span class="font-medium">South:</span> {{ application?.dynamicFields?.south_boundary || 'N/A' }}</div>
                                <div><span class="font-medium">East:</span> {{ application?.dynamicFields?.east_boundary || 'N/A' }}</div>
                                <div><span class="font-medium">West:</span> {{ application?.dynamicFields?.west_boundary || 'N/A' }}</div>
                            </div>
                        </div>
                    </div>

                    Certifications
                    <div class="space-y-4 lg:col-span-2">
                        <h3 class="text-lg font-medium text-gray-900 border-b pb-2">Certifications & Documents</h3>
                        <div class="grid grid-cols-2 lg:grid-cols-3 gap-4 text-sm">
                            <div>
                                <label class="font-medium text-gray-700">RSBSA Number:</label>
                                <p class="text-gray-900 font-mono">{{ application?.dynamicFields?.rsbsa_number || 'N/A' }}</p>
                            </div>
                            <div>
                                <label class="font-medium text-gray-700">RSBSA Date:</label>
                                <p class="text-gray-900">{{ formatDate(application?.dynamicFields?.rsbsa_date) }}</p>
                            </div>
                            <div>
                                <label class="font-medium text-gray-700">CTC Number:</label>
                                <p class="text-gray-900 font-mono">{{ application?.dynamicFields?.ctc_number || 'N/A' }}</p>
                            </div>
                            <div>
                                <label class="font-medium text-gray-700">CTC Date:</label>
                                <p class="text-gray-900">{{ formatDate(application?.dynamicFields?.ctc_date) }}</p>
                            </div>
                            <div>
                                <label class="font-medium text-gray-700">Valid ID:</label>
                                <p class="text-gray-900">{{ application?.dynamicFields?.valid_id || 'N/A' }}</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            Footer
            <div class="flex justify-end gap-3 p-6 border-t bg-gray-50">
                <button
                    @click="$emit('close')"
                    class="px-4 py-2 text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50 transition-colors"
                >
                    Close
                </button>
                <button
                    @click="$emit('edit', application)"
                    class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors"
                >
                    Edit Application
                </button>
            </div>
        </div>
    </div>
</template>

<script setup>
import { computed } from 'vue'
import StatusBadge from '../others/StatusBadge.vue'

const props = defineProps({
    isOpen: {
        type: Boolean,
        default: false
    },
    application: {
        type: Object,
        default: null
    }
})

const emit = defineEmits(['close', 'edit'])

const formatDate = (dateString) => {
    if (!dateString) return 'N/A'
    return new Date(dateString).toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
    })
}

const formatCurrency = (amount) => {
    if (!amount) return '0.00'
    return parseFloat(amount).toLocaleString('en-US', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    })
}

const formatAddress = (fields) => {
    if (!fields) return 'N/A'
    const parts = [
        fields.house_number,
        fields.street,
        fields.barangay,
        fields.municipality,
        fields.province
    ].filter(Boolean)
    return parts.length > 0 ? parts.join(', ') : 'N/A'
}
</script>