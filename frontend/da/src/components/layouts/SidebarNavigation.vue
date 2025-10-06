<template>
  <div class="flex flex-col w-64 h-screen bg-gray-50 border-r border-gray-200 shadow-sm">
    <!-- Logo + Title Section -->
  <div class="flex flex-col items-center px-6 pt-8 pb-5 bg-gradient-to-b from-gray-50 to-white border-b border-gray-200 shadow-sm">
    <!-- Logo -->
    <div class="relative group">
      <img 
        src="@/assets/da_image.png" 
        alt="DA Logo" 
        class="h-14 w-auto transition-transform duration-300 group-hover:scale-105 group-hover:shadow-md rounded-md"
      />
      <div class="absolute inset-0 bg-gray-900 bg-opacity-0 group-hover:bg-opacity-5 transition-all duration-300 rounded-md"></div>
    </div>

    <!-- Department Name -->
    <h1 class="mt-4 text-base font-bold text-black-900 tracking-tight text-center">
      Department of Agriculture
    </h1>

    <!-- Role Badge -->
    <span class="mt-3 text-xs font-semibold text-gray-700 bg-green-100 px-4 py-1.5 rounded-full shadow-sm transition-colors duration-300 hover:bg-green-200">
      {{ roleTitle }}
    </span>
  </div>

    <!-- Navigation -->
    <nav class="flex-1 px-3 py-6 space-y-2 overflow-y-auto">
      <template v-for="item in filteredNavigation" :key="item.title">
        <!-- Single Navigation Item -->
        <router-link
          v-if="!item.children"
          :to="item.to"
          :class="[
            isActive(item.to)
              ? 'bg-green-600 text-white shadow-md'
              : 'text-gray-700 hover:bg-green-100 hover:text-green-800',
            'group flex items-center px-4 py-2.5 text-sm font-medium rounded-lg transition-all duration-300'
          ]"
        >
          <component 
            :is="item.icon" 
            :class="[
              isActive(item.to) ? 'text-white' : 'text-gray-500 group-hover:text-green-700',
              'mr-3 h-5 w-5 transition-colors duration-300'
            ]" 
          />
          <span class="truncate">{{ item.title }}</span>
        </router-link>

        <!-- Navigation Group with Children -->
        <div v-else>
          <button
            @click="toggleGroup(item.title)"
            :class="[
              'text-gray-700 hover:bg-green-100 hover:text-green-800',
              'group w-full flex items-center justify-between px-4 py-2.5 text-sm font-medium rounded-lg transition-all duration-300'
            ]"
          >
            <div class="flex items-center">
              <component 
                :is="item.icon" 
                class="text-gray-500 group-hover:text-green-700 mr-3 h-5 w-5 transition-colors duration-300" 
              />
              <span class="truncate">{{ item.title }}</span>
            </div>
            <ChevronDown 
              :class="[
                expandedGroups.includes(item.title) ? 'rotate-180 text-green-700' : 'text-gray-500',
                'h-4 w-4 transition-transform duration-300'
              ]"
            />
          </button>
          
          <!-- Submenu -->
          <div 
            v-show="expandedGroups.includes(item.title)"
            class="mt-1 space-y-1 pl-4"
            v-animate="{ animation: 'slideDown' }"
          >
            <router-link
              v-for="child in item.children"
              :key="child.title"
              :to="child.to"
              :class="[
                isActive(child.to)
                  ? 'bg-green-50 text-green-800 border-l-4 border-green-600'
                  : 'text-gray-600 hover:bg-green-50 hover:text-green-800',
                'group flex items-center pl-7 pr-4 py-2 text-sm font-medium rounded-md transition-all duration-300'
              ]"
            >
              <span class="truncate">{{ child.title }}</span>
            </router-link>
          </div>
        </div>
      </template>
    </nav>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'
import { ChevronDown, HelpCircle, BookOpen, Mail } from 'lucide-vue-next'
import { useAuthStore } from '@/stores/auth'

const route = useRoute()
const authStore = useAuthStore()

const props = defineProps({
  navigation: {
    type: Array,
    default: () => []
  },
  roleTitle: {
    type: String,
    default: 'Staff Portal'
  }
})

const emit = defineEmits(['help-support'])

const expandedGroups = ref([])

const supportItems = [
  { title: 'FAQs', icon: HelpCircle, action: () => emit('help-support', 'faq') },
  { title: 'User Guide', icon: BookOpen, action: () => emit('help-support', 'user-guide') },
  { title: 'Contact Support', icon: Mail, action: () => emit('help-support', 'contact') }
]

const filteredNavigation = computed(() => {
  if (!props.navigation || props.navigation.length === 0) return []

  return props.navigation.filter(item => {
    if (!item.children && !item.permission) return true
    if (!item.children && item.permission) {
      return authStore.hasPermission(item.permission)
    }
    if (item.children) {
      const filteredChildren = item.children.filter(child => {
        if (!child.permission) return true
        return authStore.hasPermission(child.permission)
      })
      return filteredChildren.length > 0
    }
    return true
  }).map(item => {
    if (item.children) {
      return {
        ...item,
        children: item.children.filter(child => {
          if (!child.permission) return true
          return authStore.hasPermission(child.permission)
        })
      }
    }
    return item
  })
})

const isActive = (routeObj) => {
  if (!routeObj) return false
  if (typeof routeObj === 'object' && routeObj.name) {
    return route.name === routeObj.name
  }
  if (typeof routeObj === 'string') {
    return route.path === routeObj || route.path.startsWith(routeObj + '/')
  }
  return false
}

const toggleGroup = (groupName) => {
  const index = expandedGroups.value.indexOf(groupName)
  if (index > -1) {
    expandedGroups.value.splice(index, 1)
  } else {
    expandedGroups.value.push(groupName)
  }
}
</script>

<style scoped>
/* Custom animation for submenu slide down */
@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

[v-animate] {
  animation: slideDown 0.3s ease-out;
}
</style>