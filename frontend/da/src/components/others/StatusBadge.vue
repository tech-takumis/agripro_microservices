<template>
  <span :class="badgeClasses" class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium">
    {{ status }}
  </span>
</template>

<script setup>
import { computed } from 'vue'

// Props
const props = defineProps({
    status: {
        type: String,
        required: true
    }
})

// Computed
const badgeClasses = computed(() => {
    const baseClasses = 'inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium'

    switch (props.status.toLowerCase()) {
        case 'submitted':
            return 'bg-blue-100 text-blue-800'
        case 'approved':
            return 'bg-green-100 text-green-800'
        case 'rejected':
            return 'bg-red-100 text-red-800'
        case 'pending':
            return 'bg-yellow-100 text-yellow-800'
        default:
            return 'bg-gray-100 text-gray-800'
    }
})
</script>