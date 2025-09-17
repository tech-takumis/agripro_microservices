<template>
    <div class="card">
      <div class="flex items-center">
        <div class="p-2 rounded-lg" :class="iconBgClass">
          <component :is="icon" class="h-6 w-6" :class="iconColorClass" />
        </div>
        <div class="ml-4 flex-1">
          <p class="text-sm font-medium text-gray-600">{{ label }}</p>
          <div class="flex items-baseline">
            <p class="text-2xl font-bold text-gray-900">{{ formattedValue }}</p>
            <div v-if="change !== undefined" class="ml-2 flex items-center">
              <component
                :is="changeIcon"
                class="h-4 w-4"
                :class="changeColorClass"
              />
              <span class="text-sm font-medium" :class="changeColorClass">
                {{ Math.abs(change) }}%
              </span>
            </div>
          </div>
          <p v-if="subtitle" class="text-xs text-gray-500 mt-1">{{ subtitle }}</p>
        </div>
      </div>
      
      <!-- Loading State -->
      <div v-if="loading" class="absolute inset-0 bg-white bg-opacity-75 flex items-center justify-center rounded-lg">
        <LoadingSpinner size="sm" />
      </div>
    </div>
  </template>
  
  <script setup>
  import { computed } from 'vue'
  import { TrendingUp, TrendingDown } from 'lucide-vue-next'
  import LoadingSpinner from './LoadingSpinner.vue'
  
  const props = defineProps({
    label: {
      type: String,
      required: true
    },
    value: {
      type: [Number, String],
      required: true
    },
    icon: {
      type: Object,
      required: true
    },
    color: {
      type: String,
      default: 'primary',
      validator: (value) => ['primary', 'success', 'warning', 'danger', 'info'].includes(value)
    },
    change: {
      type: Number,
      default: undefined
    },
    subtitle: {
      type: String,
      default: ''
    },
    formatter: {
      type: Function,
      default: null
    },
    loading: {
      type: Boolean,
      default: false
    }
  })
  
  const formattedValue = computed(() => {
    if (props.formatter) {
      return props.formatter(props.value)
    }
    
    if (typeof props.value === 'number') {
      return props.value.toLocaleString()
    }
    
    return props.value
  })
  
  const iconBgClass = computed(() => {
    const classes = {
      primary: 'bg-primary-100',
      success: 'bg-success-100',
      warning: 'bg-warning-100',
      danger: 'bg-danger-100',
      info: 'bg-blue-100'
    }
    return classes[props.color]
  })
  
  const iconColorClass = computed(() => {
    const classes = {
      primary: 'text-primary-600',
      success: 'text-success-600',
      warning: 'text-warning-600',
      danger: 'text-danger-600',
      info: 'text-blue-600'
    }
    return classes[props.color]
  })
  
  const changeIcon = computed(() => {
    return props.change >= 0 ? TrendingUp : TrendingDown
  })
  
  const changeColorClass = computed(() => {
    return props.change >= 0 ? 'text-success-600' : 'text-danger-600'
  })
  </script>
  