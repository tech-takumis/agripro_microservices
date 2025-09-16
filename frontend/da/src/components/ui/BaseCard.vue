<template>
    <div :class="cardClasses">
        <div v-if="$slots.header" :class="headerClasses">
            <slot name="header" />
        </div>

        <div :class="contentClasses">
            <slot />
        </div>

        <div v-if="$slots.footer" :class="footerClasses">
            <slot name="footer" />
        </div>
    </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
    variant: {
        type: String,
        default: 'default',
        validator: (value) => ['default', 'bordered', 'elevated', 'flat'].includes(value)
    },
    padding: {
        type: String,
        default: 'normal',
        validator: (value) => ['none', 'small', 'normal', 'large'].includes(value)
    },
    hover: {
        type: Boolean,
        default: false
    }
})

const cardClasses = computed(() => [
    'bg-white rounded-lg overflow-hidden',
    {
        // Variants
        'border border-gray-200': props.variant === 'default' || props.variant === 'bordered',
        'shadow-sm': props.variant === 'default',
        'shadow-md': props.variant === 'elevated',
        'shadow-none border-0': props.variant === 'flat',

        // Hover effect
        'hover:shadow-md hover:border-gray-300 transition-all duration-200': props.hover,
    }
])

const headerClasses = computed(() => [
    'border-b border-gray-200',
    {
        'p-3': props.padding === 'small',
        'p-4': props.padding === 'normal',
        'p-6': props.padding === 'large',
    }
])

const contentClasses = computed(() => [
    {
        'p-0': props.padding === 'none',
        'p-3': props.padding === 'small',
        'p-4': props.padding === 'normal',
        'p-6': props.padding === 'large',
    }
])

const footerClasses = computed(() => [
    'border-t border-gray-200 bg-gray-50',
    {
        'p-3': props.padding === 'small',
        'p-4': props.padding === 'normal',
        'p-6': props.padding === 'large',
    }
])
</script>
