<template>
  <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6
              hover:shadow-md hover:border-indigo-300 transition-all duration-200 cursor-pointer">
    <div class="flex items-center justify-between">
      <div class="flex items-center min-w-0">
        <div :class="[
          'p-3 rounded-lg flex-shrink-0',
          iconBgColor
        ]">
          <component :is="icon" :class="['h-6 w-6', iconColor]" />
        </div>
        <div class="ml-4 min-w-0 flex-1">
          <h3 class="text-lg font-semibold text-gray-900 truncate">{{ title }}</h3>
          <p class="text-sm text-gray-500 truncate">{{ description }}</p>
        </div>
      </div>
      <div class="text-right flex-shrink-0 ml-4">
        <p class="text-2xl font-bold text-gray-900 truncate">{{ value }}</p>
        <p v-if="change" :class="[
          'text-sm font-medium truncate',
          changeColor
        ]">
          {{ change }}
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  title: String,
  description: String,
  value: [String, Number],
  change: String,
  icon: [Object, Function],
  variant: {
    type: String,
    default: 'default'
  }
})

const iconBgColor = computed(() => {
  const variants = {
    primary: 'bg-indigo-100',
    success: 'bg-green-100',
    warning: 'bg-yellow-100',
    danger: 'bg-red-100',
    info: 'bg-blue-100',
    default: 'bg-gray-100'
  }
  return variants[props.variant] || variants.default
})

const iconColor = computed(() => {
  const variants = {
    primary: 'text-indigo-600',
    success: 'text-green-600',
    warning: 'text-yellow-600',
    danger: 'text-red-600',
    info: 'text-blue-600',
    default: 'text-gray-600'
  }
  return variants[props.variant] || variants.default
})

const changeColor = computed(() => {
  if (!props.change) return ''
  return props.change.startsWith('+') ? 'text-green-600' : 'text-red-600'
})
</script>
