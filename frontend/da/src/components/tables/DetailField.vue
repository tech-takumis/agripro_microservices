<template>
    <div :class="className">
        <dt class="text-sm font-medium text-gray-500">{{ label }}</dt>
        <dd class="mt-1 text-sm text-gray-900">{{ displayValue }}</dd>
    </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
    label: {
        type: String,
        required: true
    },
    value: {
        type: [String, Number, Boolean],
        default: null
    },
    class: {
        type: String,
        default: ''
    }
})

const displayValue = computed(() => {
    if (props.value === null || props.value === undefined || props.value === '') {
        return 'N/A'
    }
    return props.value
})

const className = computed(() => {
    return props.class || ''
})
</script>
