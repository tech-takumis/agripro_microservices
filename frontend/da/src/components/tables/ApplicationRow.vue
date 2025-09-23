<template>
    <tr class="hover:bg-gray-50 transition-colors">
        Selection Checkbox
        <td class="px-6 py-4 whitespace-nowrap">
            <input
                type="checkbox"
                :checked="isSelected"
                @change="$emit('toggleSelect', application.id)"
                class="w-4 h-4 text-blue-600 rounded border-gray-300 focus:ring-blue-500"
            />
        </td>

        Applicant Info
        <td class="px-6 py-4 whitespace-nowrap">
            <div class="flex items-center">
                <div class="flex-shrink-0 h-10 w-10">
                    <div class="h-10 w-10 rounded-full bg-blue-100 flex items-center justify-center">
                        <UserIcon class="w-5 h-5 text-blue-600" />
                    </div>
                </div>
                <div class="ml-4">
                    <div class="text-sm font-medium text-gray-900">
                        {{ fullName }}
                    </div>
                    <div class="text-sm text-gray-500">
                        {{ application.dynamicFields.cell_phone_number }}
                    </div>
                </div>
            </div>
        </td>

        Crop Details
        <td class="px-6 py-4 whitespace-nowrap">
            <div class="text-sm text-gray-900">{{ application.dynamicFields.crop_type }}</div>
            <div class="text-sm text-gray-500">{{ application.dynamicFields.cover_type }}</div>
        </td>

        Coverage
        <td class="px-6 py-4 whitespace-nowrap">
            <div class="text-sm text-gray-900">₱{{ formatCurrency(application.dynamicFields.amount_of_cover) }}</div>
            <div class="text-sm text-gray-500">Premium: ₱{{ formatCurrency(application.dynamicFields.premium) }}</div>
        </td>

        Status
        <td class="px-6 py-4 whitespace-nowrap">
            <StatusBadge :status="application.status" />
        </td>

        Submitted Date
        <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
            {{ formatDate(application.submittedAt) }}
        </td>

        Actions
        <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
            <button
                @click="$emit('viewDetails')"
                class="text-blue-600 hover:text-blue-900 transition-colors"
            >
                View Details
            </button>
        </td>
    </tr>
</template>

<script setup>
import { computed } from 'vue'
import { UserIcon } from 'lucide-vue-next'
import StatusBadge from '../others/StatusBadge.vue'

// Props
const props = defineProps({
    application: {
        type: Object,
        required: true
    },
    isSelected: {
        type: Boolean,
        default: false
    }
})

// Emits
defineEmits(['toggleSelect', 'viewDetails'])

// Computed
const fullName = computed(() => {
    const { first_name, middle_name, last_name } = props.application.dynamicFields
    return `${first_name} ${middle_name} ${last_name}`.trim()
})

// Utility functions
const formatCurrency = (amount) => {
    return new Intl.NumberFormat('en-PH').format(amount)
}

const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString('en-PH', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
    })
}
</script>