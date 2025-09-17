<template>
    <div v-if="hasAccess">
      <slot />
    </div>
    <div v-else-if="showFallback" class="text-center py-8">
      <div class="text-gray-400 mb-2">
        <Lock class="h-12 w-12 mx-auto" />
      </div>
      <p class="text-gray-600">You don't have permission to access this content.</p>
    </div>
  </template>
  
  <script setup>
  import { computed } from 'vue'
  import { Lock } from 'lucide-vue-next'
  import { useAuthStore } from '@/stores/auth'
  
  const authStore = useAuthStore()
  
  const props = defineProps({
    permissions: {
      type: Array,
      default: () => []
    },
    roles: {
      type: Array,
      default: () => []
    },
    requireAll: {
      type: Boolean,
      default: false
    },
    showFallback: {
      type: Boolean,
      default: false
    }
  })
  
  const hasAccess = computed(() => {
    // If no permissions or roles specified, allow access
    if (props.permissions.length === 0 && props.roles.length === 0) {
      return true
    }
    
    let hasPermission = true
    let hasRole = true
    
    // Check permissions
    if (props.permissions.length > 0) {
      if (props.requireAll) {
        hasPermission = props.permissions.every(permission => 
          authStore.hasPermission(permission)
        )
      } else {
        hasPermission = props.permissions.some(permission => 
          authStore.hasPermission(permission)
        )
      }
    }
    
    // Check roles
    if (props.roles.length > 0) {
      if (props.requireAll) {
        hasRole = props.roles.every(role => 
          authStore.hasRole(role)
        )
      } else {
        hasRole = props.roles.some(role => 
          authStore.hasRole(role)
        )
      }
    }
    
    // Return based on requirements
    if (props.permissions.length > 0 && props.roles.length > 0) {
      return props.requireAll ? (hasPermission && hasRole) : (hasPermission || hasRole)
    }
    
    return props.permissions.length > 0 ? hasPermission : hasRole
  })
  </script>
  