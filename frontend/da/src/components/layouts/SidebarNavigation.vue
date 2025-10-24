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
      <h1 class="mt-4 text-base font-bold text-gray-900 tracking-tight text-center">
        Department of Agriculture
      </h1>

      <!-- Role Badge -->
      <span class="mt-3 text-xs font-semibold text-gray-700 bg-green-100 px-4 py-1.5 rounded-full shadow-sm transition-colors duration-300 hover:bg-green-200">
        {{ roleTitle }}
      </span>
    </div>

    <!-- Navigation -->
    <nav class="flex-1 px-1 py-5 space-y-2 overflow-y-auto custom-scrollbar">
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
          <transition name="slide">
            <div 
              v-show="expandedGroups.includes(item.title)"
              class="mt-1 space-y-1 pl-4"
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
          </transition>
        </div>
      </template>
    </nav>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { ChevronDown } from 'lucide-vue-next'
import {
  ADMIN_NAVIGATION,
  MUNICIPAL_AGRICULTURIST_NAVIGATION,
  AGRICULTURAL_EXTENSION_WORKER_NAVIGATION
} from '@/lib/navigation'

const route = useRoute()
const auth = useAuthStore()
const expandedGroups = ref([])

// Toggle sidebar group open/close
const toggleGroup = (groupTitle) => {
  const index = expandedGroups.value.indexOf(groupTitle)
  if (index === -1) {
    expandedGroups.value.push(groupTitle)
  } else {
    expandedGroups.value.splice(index, 1)
  }
}

// Detect active route (works for both parent + child)
const isActive = (to) => {
  if (!to || !route) return false
  if (to.name === route.name) return true
  return route.path.startsWith(to.path)
}

// Get navigation items based on user's role
const filteredNavigation = computed(() => {
  const roles = auth.userRoles
  if (roles.includes('ADMIN')) return ADMIN_NAVIGATION
  if (roles.includes('MUNICIPAL AGRICULTURISTS')) return MUNICIPAL_AGRICULTURIST_NAVIGATION
  if (roles.includes('AGRICULTURAL EXTENSION WORKERS')) return AGRICULTURAL_EXTENSION_WORKER_NAVIGATION
  return []
})

// Format role title
const roleTitle = computed(() => {
  if (!auth.userData.roles || auth.userData.roles.length === 0) return ''
  return auth.userData.roles[0].name
    .split('_')
    .map(word => word.charAt(0).toUpperCase() + word.slice(1).toLowerCase())
    .join(' ')
})

// Auto-expand group if a child route is active
const autoExpandActiveGroup = () => {
  const currentPath = route.path
  filteredNavigation.value.forEach(item => {
    if (item.children && item.children.some(child => child.to.path === currentPath)) {
      if (!expandedGroups.value.includes(item.title)) {
        expandedGroups.value.push(item.title)
      }
    }
  })
}

// Persist expanded groups in localStorage
watch(expandedGroups, (newVal) => {
  localStorage.setItem('expandedGroups', JSON.stringify(newVal))
}, { deep: true })

onMounted(() => {
  // Restore previously expanded groups
  const saved = localStorage.getItem('expandedGroups')
  if (saved) expandedGroups.value = JSON.parse(saved)

  // Auto-expand group that contains the active route
  autoExpandActiveGroup()
})

// Watch for route changes and keep expanded group open
watch(() => route.path, () => {
  autoExpandActiveGroup()
})
</script>

<style scoped>
/* Smooth slide transition for submenu */
.slide-enter-active,
.slide-leave-active {
  transition: all 0.25s ease;
}
.slide-enter-from,
.slide-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}

/* Clean custom scrollbar */
.custom-scrollbar::-webkit-scrollbar {
  width: 6px;
}

.custom-scrollbar::-webkit-scrollbar-thumb {
  background-color: transparent;
  border-radius: 8px;
  transition: background-color 0.3s;
}

.custom-scrollbar:hover::-webkit-scrollbar-thumb {
  background-color: rgba(156, 163, 175, 0.5); /* gray-400 */
}

.custom-scrollbar::-webkit-scrollbar-track {
  background: transparent;
}

/* Firefox support */
.custom-scrollbar {
  scrollbar-width: thin;
  scrollbar-color: transparent transparent;
}

.custom-scrollbar:hover {
  scrollbar-color: rgba(156, 163, 175, 0.5) transparent;
}
</style>
