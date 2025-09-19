<template>
    <div class="flex items-center justify-center" :class="containerClass">
      <div class="relative">
        <div
          class="animate-spin rounded-full border-4 border-gray-200"
          :class="[sizeClass, colorClass]"
        ></div>
        <div
          class="absolute top-0 left-0 animate-spin rounded-full border-4 border-transparent border-t-current"
          :class="sizeClass"
        ></div>
      </div>
      <span v-if="text" class="ml-3 text-sm font-medium text-gray-600">{{ text }}</span>
    </div>
  </template>
  
  <script setup>
  import { computed } from 'vue'
  
  const props = defineProps({
    size: {
      type: String,
      default: 'md',
      validator: (value) => ['sm', 'md', 'lg', 'xl'].includes(value)
    },
    color: {
      type: String,
      default: 'primary',
      validator: (value) => ['primary', 'secondary', 'success', 'warning', 'danger'].includes(value)
    },
    text: {
      type: String,
      default: ''
    },
    fullScreen: {
      type: Boolean,
      default: false
    }
  })
  
  const sizeClass = computed(() => {
    const sizes = {
      sm: 'h-4 w-4',
      md: 'h-8 w-8',
      lg: 'h-12 w-12',
      xl: 'h-16 w-16'
    }
    return sizes[props.size]
  })
  
  const colorClass = computed(() => {
    const colors = {
      primary: 'text-primary-600',
      secondary: 'text-gray-600',
      success: 'text-success-600',
      warning: 'text-warning-600',
      danger: 'text-danger-600'
    }
    return colors[props.color]
  })
  
  const containerClass = computed(() => {
    return props.fullScreen ? 'min-h-screen' : 'py-4'
  })
  </script>
  