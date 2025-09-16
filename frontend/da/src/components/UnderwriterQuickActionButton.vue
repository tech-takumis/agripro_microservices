<template>
  <button
    @click="$emit('click')"
    :class="[
      'flex flex-col items-center justify-center p-6 bg-white rounded-lg shadow-sm border border-gray-200 hover:shadow-md hover:border-indigo-300 transition-all duration-200',
      'focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2'
    ]"
  >
    <div :class="[
      'p-3 rounded-lg mb-3',
      iconBgColor
    ]">
      <component :is="icon" :class="['h-8 w-8', iconColor]" />
    </div>
    <h3 class="text-sm font-semibold text-gray-900 text-center">{{ title }}</h3>
    <p class="text-xs text-gray-500 text-center mt-1">{{ description }}</p>
  </button>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  title: String,
  description: String,
  icon: [Object, Function],
  variant: {
    type: String,
    default: 'default'
  }
})

defineEmits(['click'])

const iconBgColor = computed(() => {
  const variants = {
    primary: 'bg-indigo-100',
    secondary: 'bg-blue-100',
    warning: 'bg-yellow-100',
    danger: 'bg-red-100',
    default: 'bg-gray-100'
  }
  return variants[props.variant] || variants.default
})

const iconColor = computed(() => {
  const variants = {
    primary: 'text-indigo-600',
    secondary: 'text-blue-600',
    warning: 'text-yellow-600',
    danger: 'text-red-600',
    default: 'text-gray-600'
  }
  return variants[props.variant] || variants.default
})
</script>
