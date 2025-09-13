<template>
  <div class="flex h-screen bg-gray-100">
    <!-- Desktop Sidebar -->
    <div class="hidden md:flex md:w-64 md:flex-col">
      <SidebarNavigation
        :navigation="navigation"
        :role-title="roleTitle"
        :user-full-name="store.userFullName"
        :user-email="store.userEmail"
        :user-initials="userInitials"
        @logout="handleLogout"
      />
    </div>

    <!-- Mobile Sidebar (Off-canvas) -->
    <div v-if="sidebarOpen" class="fixed inset-0 z-40 md:hidden">
      <div class="fixed inset-0 bg-gray-600 bg-opacity-75" @click="sidebarOpen = false"></div>
      <div class="relative flex-1 flex flex-col max-w-xs w-full bg-white">
        <div class="absolute top-0 right-0 -mr-12 pt-2">
          <button
            @click="sidebarOpen = false"
            class="ml-1 flex items-center justify-center h-10 w-10 rounded-full focus:outline-none focus:ring-2 focus:ring-inset focus:ring-white"
          >
            <X class="h-6 w-6 text-white" />
            <span class="sr-only">Close sidebar</span>
          </button>
        </div>
        <SidebarNavigation
          :navigation="navigation"
          :role-title="roleTitle"
          :user-full-name="store.userFullName"
          :user-email="store.userEmail"
          :user-initials="userInitials"
          @logout="handleLogout"
        />
      </div>
    </div>

    <!-- Main content area -->
    <div class="flex flex-col flex-1 overflow-hidden">
      <!-- Top bar for mobile (visible on small screens) -->
      <div class="md:hidden bg-white border-b border-gray-200 px-4 py-2 flex items-center justify-between shadow-sm">
        <button
          @click="sidebarOpen = true"
          class="p-2 rounded-md text-gray-400 hover:text-gray-600 hover:bg-gray-100"
          aria-label="Open sidebar"
        >
          <Menu class="h-6 w-6" />
        </button>
        <h1 class="text-lg font-semibold text-gray-900">{{ pageTitle }}</h1>
        <div class="w-10"></div> <!-- Spacer for centering -->
      </div>

      <!-- Page header (visible on all screens, but mobile has its own top bar) -->
      <header v-if="$slots.header" class="bg-white shadow-sm border-b border-gray-200 hidden md:block">
        <div class="px-4 py-4 sm:px-6 lg:px-8">
          <slot name="header" />
        </div>
      </header>
      <!-- Mobile header for consistency with desktop header slot -->
      <header v-if="$slots.header" class="bg-white shadow-sm border-b border-gray-200 md:hidden">
        <div class="px-4 py-4 sm:px-6 lg:px-8">
          <slot name="header" />
        </div>
      </header>


      <!-- Main content area -->
      <main class="flex-1 overflow-y-auto bg-gray-50">
        <div class="p-4 sm:p-6 lg:p-8">
          <slot />
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { Menu, X } from 'lucide-vue-next'
import { useUserStore } from '@/stores/user'
import SidebarNavigation from '@/components/layouts/SidebarNavigation.vue' // Import the new component

const store = useUserStore()

const props = defineProps({
  navigation: {
    type: Array,
    default: () => []
  },
  roleTitle: {
    type: String,
    default: 'Staff Portal'
  },
  pageTitle: {
    type: String,
    default: 'Dashboard'
  }
})

const sidebarOpen = ref(false)

const userInitials = computed(() => {
  const name = store.userFullName
  if (!name) return ''
  return name.split(' ').map(n => n[0]).join('').toUpperCase()
})

const handleLogout = () => {
  store.logout()
}
</script>
