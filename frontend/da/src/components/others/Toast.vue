<template>
    <Teleport to="body">
      <div class="fixed top-4 right-4 z-50 space-y-2">
        <TransitionGroup
          enter-active-class="transition-all duration-300"
          enter-from-class="opacity-0 translate-x-full"
          enter-to-class="opacity-100 translate-x-0"
          leave-active-class="transition-all duration-300"
          leave-from-class="opacity-100 translate-x-0"
          leave-to-class="opacity-0 translate-x-full"
        >
          <div
            v-for="toast in toasts"
            :key="toast.id"
            class="flex items-center p-4 rounded-lg shadow-lg max-w-sm"
            :class="getToastClass(toast.type)"
          >
            <div class="flex-shrink-0">
              <component :is="getToastIcon(toast.type)" class="h-5 w-5" />
            </div>
            <div class="ml-3 flex-1">
              <p class="text-sm font-medium">{{ toast.title }}</p>
              <p v-if="toast.message" class="text-sm opacity-90 mt-1">{{ toast.message }}</p>
            </div>
            <button
              @click="removeToast(toast.id)"
              class="ml-4 flex-shrink-0 opacity-70 hover:opacity-100 transition-opacity"
            >
              <X class="h-4 w-4" />
            </button>
          </div>
        </TransitionGroup>
      </div>
    </Teleport>
  </template>
  
  <script setup>
  import { ref } from 'vue'
  import { CheckCircle, AlertCircle, AlertTriangle, Info, X } from 'lucide-vue-next'
  
  const toasts = ref([])
  
  const getToastClass = (type) => {
    const classes = {
      success: 'bg-success-50 text-success-800 border border-success-200',
      error: 'bg-danger-50 text-danger-800 border border-danger-200',
      warning: 'bg-warning-50 text-warning-800 border border-warning-200',
      info: 'bg-primary-50 text-primary-800 border border-primary-200'
    }
    return classes[type] || classes.info
  }
  
  const getToastIcon = (type) => {
    const icons = {
      success: CheckCircle,
      error: AlertCircle,
      warning: AlertTriangle,
      info: Info
    }
    return icons[type] || icons.info
  }
  
  const addToast = (toast) => {
    const id = Date.now() + Math.random()
    const newToast = {
      id,
      type: 'info',
      duration: 5000,
      ...toast
    }
    
    toasts.value.push(newToast)
    
    if (newToast.duration > 0) {
      setTimeout(() => {
        removeToast(id)
      }, newToast.duration)
    }
    
    return id
  }
  
  const removeToast = (id) => {
    const index = toasts.value.findIndex(toast => toast.id === id)
    if (index > -1) {
      toasts.value.splice(index, 1)
    }
  }
  
  const clearToasts = () => {
    toasts.value = []
  }
  
  // Expose methods for external use
  defineExpose({
    addToast,
    removeToast,
    clearToasts,
    success: (title, message) => addToast({ type: 'success', title, message }),
    error: (title, message) => addToast({ type: 'error', title, message }),
    warning: (title, message) => addToast({ type: 'warning', title, message }),
    info: (title, message) => addToast({ type: 'info', title, message })
  })
  </script>
  