<template>
  <div class="flex flex-col h-full w-full bg-green-600">
    <!-- Logo / Header -->
    <div class="flex flex-col items-center py-4 px-4 bg-green-600 border-b border-green-700 flex-shrink-0">
      <!-- Logo -->
      <img
        src="@/assets/pcic.svg"
        alt="PCIC Logo"
        class="h-12 w-10 mb-2"
      />

      <!-- Title -->
      <h1 class="text-2xl font-bold text-white leading-tight">PCIC</h1>

      <!-- Role Badge -->
      <span
        class="inline-block bg-yellow-100 text-yellow-800 text-xs font-semibold px-3 py-1 rounded-full mt-2 mb-1"
      >
        {{ roleTitle }}
      </span>
    </div>

    <!-- Navigation -->
    <nav class="flex-1 px-3 py-4 space-y-1 bg-green-600 overflow-y-auto min-h-0">
      <template v-for="item in navigation" :key="item.name || item.title">
        <!-- Single Navigation Item -->
        <router-link
          v-if="!item.children && item.to"
          :to="item.to"
          :class="[
            isActive(item.to)
              ? 'bg-white text-green-700 font-semibold shadow-sm'
              : 'text-white hover:bg-green-700 hover:text-white',
            'group flex items-center px-3 py-2.5 text-sm rounded-md transition-colors duration-200'
          ]"
        >
          <component 
            :is="item.icon" 
            :class="[
              isActive(item.to) 
                ? 'text-green-700' 
                : 'text-white group-hover:text-yellow-300',
              'mr-3 h-5 w-5 transition-colors duration-200'
            ]" 
          />
          {{ item.title || item.name }}
        </router-link>
        <span v-else-if="!item.children && !item.to" class="group flex items-center px-3 py-2.5 text-sm rounded-md transition-colors duration-200 text-green-100 opacity-50 cursor-not-allowed">
          <component :is="item.icon" class="mr-3 h-5 w-5 text-green-200" />
          {{ item.title || item.name }}
        </span>

        <!-- Navigation Group with Children -->
        <div v-else>
          <button
            @click="toggleGroup(item.title || item.name)"
            :class="[
              'text-white hover:bg-green-700 hover:text-white',
              'group w-full flex items-center justify-between px-3 py-2.5 text-sm rounded-md transition-colors duration-200'
            ]"
          >
            <div class="flex items-center">
              <component 
                :is="item.icon" 
                class="text-white group-hover:text-yellow-300 mr-3 h-5 w-5 transition-colors duration-200" 
              />
              {{ item.title || item.name }}
            </div>
            <ChevronDown 
              :class="[
                expandedGroups.includes(item.title || item.name) ? 'rotate-180 text-white' : 'text-white',
                'h-4 w-4 transition-transform duration-200'
              ]"
            />
          </button>
          <!-- Submenu with Tree Lines -->
          <div
            v-show="expandedGroups.includes(item.title || item.name)"
            class="mt-1 pl-2 border-l-2 border-green-700"
          >
            <template v-for="child in getChildren(item)" :key="child.name || child.title">
              <router-link
                v-if="child.to"
                :to="child.to"
                :class="[
                  isActive(child.to)
                    ? 'bg-white text-green-700 font-semibold shadow-sm'
                    : 'text-white hover:bg-green-700 hover:text-yellow-300',
                  'group relative flex items-center pl-6 pr-3 py-2.5 text-sm rounded-md transition-colors duration-200'
                ]"
              >
                <span
                  :class="[
                    'absolute -left-2 top-1/2 -translate-y-1/2 w-2 h-0.5 rounded-full',
                    isActive(child.to) 
                      ? 'bg-green-700' 
                      : 'bg-green-700 group-hover:bg-green-400'
                  ]"
                ></span>
                {{ child.title || child.name }}
              </router-link>
              <span v-else class="group relative flex items-center pl-6 pr-3 py-2.5 text-sm rounded-md transition-colors duration-200 text-green-100 opacity-50 cursor-not-allowed">
                {{ child.title || child.name }}
              </span>
            </template>
          </div>
        </div>
      </template>
    </nav>

    <!-- User Profile Section -->
    <div class="flex-shrink-0 border-t border-green-700 px-4 py-4 bg-green-600">
      <button
        @click="$emit('logout')"
        class="group flex w-full items-center justify-center px-4 py-2.5 text-sm font-semibold bg-white text-red-600 rounded-lg transition-all duration-200 hover:bg-red-500 hover:text-white hover:shadow-md active:scale-95"
        title="Logout"
      >
        <LogOut class="h-4 w-4 mr-2" />
        <span>Logout</span>
      </button>
    </div>
  </div>
</template>


<script setup>
import { ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { ChevronDown, LogOut } from 'lucide-vue-next'

const route = useRoute()

const props = defineProps({
  navigation: {
    type: Array,
    default: () => []
  },
  roleTitle: {
    type: String,
    default: 'Staff Portal'
  },
  userFullName: {
    type: String,
    default: ''
  },
  userEmail: {
    type: String,
    default: ''
  },
  userInitials: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['logout'])

const expandedGroups = ref([])

// Enhanced route matching: checks exact match or nested route
const isActive = (href) => {
  if (!href) return false
  const currentPath = route.path
  const targetPath = href
  
  // Exact match
  if (currentPath === targetPath) return true
  
  // Nested route match (e.g., /dashboard matches /dashboard/overview)
  if (currentPath.startsWith(targetPath + '/')) return true
  
  // Handle root path
  if (targetPath === '/' && currentPath === '/') return true
  
  return false
}

// Check if any child route is active (for auto-expanding parent groups)
const hasActiveChild = (item) => {
  if (!item.children || !Array.isArray(item.children)) return false
  return item.children.some(child => child.to && isActive(child.to))
}

// Auto-expand groups that have active children
watch(() => route.path, () => {
  props.navigation.forEach(item => {
    if (item.children && hasActiveChild(item)) {
      const groupName = item.title || item.name
      if (!expandedGroups.value.includes(groupName)) {
        expandedGroups.value.push(groupName)
      }
    }
  })
}, { immediate: true })

const toggleGroup = (groupName) => {
  const index = expandedGroups.value.indexOf(groupName)
  if (index > -1) {
    expandedGroups.value.splice(index, 1)
  } else {
    expandedGroups.value.push(groupName)
  }
}

function getChildren(item) {
  return Array.isArray(item.children) ? item.children : []
}
</script>
