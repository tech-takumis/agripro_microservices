<template>
    <button
        :type="type"
        :disabled="disabled || loading"
        :class="buttonClasses"
        @click="$emit('click', $event)"
    >
        <slot name="icon" />
        <Loader2 v-if="loading" class="animate-spin h-4 w-4" />
        <slot />
    </button>
</template>

<script setup>
import { computed } from 'vue'
import { Loader2 } from 'lucide-vue-next'

const props = defineProps({
    variant: {
        type: String,
        default: 'primary',
        validator: (value) => ['primary', 'secondary', 'outline', 'ghost', 'danger'].includes(value)
    },
    size: {
        type: String,
        default: 'medium',
        validator: (value) => ['small', 'medium', 'large'].includes(value)
    },
    type: {
        type: String,
        default: 'button',
        validator: (value) => ['button', 'submit', 'reset'].includes(value)
    },
    disabled: {
        type: Boolean,
        default: false
    },
    loading: {
        type: Boolean,
        default: false
    },
    fullWidth: {
        type: Boolean,
        default: false
    }
})

defineEmits(['click'])

const buttonClasses = computed(() => [
    'inline-flex items-center justify-center font-medium rounded-md transition-colors focus:outline-none focus:ring-2 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed',

    // Sizes
    {
        'px-3 py-1.5 text-sm gap-1.5': props.size === 'small',
        'px-4 py-2 text-sm gap-2': props.size === 'medium',
        'px-6 py-3 text-base gap-2': props.size === 'large',
    },

    // Variants
    {
        'bg-blue-600 text-white hover:bg-blue-700 focus:ring-blue-500': props.variant === 'primary',
        'bg-gray-600 text-white hover:bg-gray-700 focus:ring-gray-500': props.variant === 'secondary',
        'border border-gray-300 bg-white text-gray-700 hover:bg-gray-50 focus:ring-blue-500': props.variant === 'outline',
        'text-gray-700 hover:bg-gray-100 focus:ring-gray-500': props.variant === 'ghost',
        'bg-red-600 text-white hover:bg-red-700 focus:ring-red-500': props.variant === 'danger',
    },

    // Full width
    {
        'w-full': props.fullWidth
    }
])
</script>
