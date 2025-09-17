<template>
    <div v-if="componentToRender">
      <component
        :is="componentToRender"
        v-bind="$attrs"
      />
    </div>
    <div v-else-if="showFallback" class="text-center py-4">
      <p class="text-gray-500">No component available for your role.</p>
    </div>
  </template>
  
  <script setup>
  import { computed } from 'vue'
  import { useAuthStore } from '@/stores/auth'
  
  const authStore = useAuthStore()
  
  const props = defineProps({
    components: {
      type: Object,
      required: true
    },
    showFallback: {
      type: Boolean,
      default: false
    }
  })
  
  const componentToRender = computed(() => {
    const userRoles = authStore.userRoles.map(role => role.slug)
    
    // Find the first matching component based on user roles
    for (const role of userRoles) {
      if (props.components[role]) {
        return props.components[role]
      }
    }
    
    // Return default component if available
    return props.components.default || null
  })
  </script>
  