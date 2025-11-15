<template>
  <div class="h-screen bg-white flex flex-col">
    <!-- Mobile Sidebar (Off-canvas) -->
    <div v-if="sidebarOpen" class="fixed inset-0 z-40 md:hidden">
      <!-- Overlay -->
      <div
        class="fixed inset-0 bg-gray-600 bg-opacity-75"
        @click="sidebarOpen = false"
      ></div>

      <!-- Sidebar -->
      <div class="relative flex flex-col w-64 max-w-xs bg-white h-full rounded-lg">
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
          :user-full-name="store.userData?.fullName"
          :user-email="store.userData?.email"
          :user-initials="userInitials"
          @logout="handleLogout"
        />
      </div>
    </div>

    <!-- Main Container -->
    <div class="flex-1 flex flex-col md:flex-row overflow-hidden px-1 md:px-2 py-2 md:py-3 gap-2 md:gap-3 rounded-lg">
      <!-- Sidebar: Tight spacing, closer to screen edge -->
      <aside
        class="hidden md:flex md:flex-col md:h-full bg-white rounded-lg md:w-72 overflow-hidden"
      >

        <SidebarNavigation
          :navigation="navigation"
          :role-title="roleTitle"
          :user-full-name="store.userData?.fullName"
          :user-email="store.userData?.email"
          :user-initials="userInitials"
          @logout="handleLogout"
        />
      </aside>

      <!-- Dashboard: Slight gap to right side -->
      <main
        class="flex-1 flex flex-col bg-white rounded-lg overflow-hidden"
      >
        <!-- Mobile Top Bar -->
        <div
          class="md:hidden bg-white border-b border-gray-200 px-4 py-3 flex items-center justify-between"
        >
          <button
            @click="sidebarOpen = true"
            class="p-2 rounded-md text-gray-400 hover:text-gray-600 hover:bg-gray-100"
            aria-label="Open sidebar"
          >
            <Menu class="h-6 w-6" />
          </button>
          <h1 class="text-lg font-semibold text-gray-900">{{ pageTitle }}</h1>
          <div class="w-10"></div>
        </div>

        <!-- Page Header -->
        <header
          v-if="$slots.header"
          class="bg-gray-100 border-b border-gray-200 flex-shrink-0"
        >
          <div class="px-6 py-5">
            <slot name="header" />
          </div>
        </header>

        <!-- Dashboard Content -->
        <div class="flex-1 overflow-y-auto bg-gray-100">
          <div class="px-6 py-6">
            <slot />
          </div>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { Menu, X } from 'lucide-vue-next'
import { useAuthStore } from '@/stores/auth'
import SidebarNavigation from '@/components/layouts/SidebarNavigation.vue'

const store = useAuthStore()

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
  const name = store.userData?.fullName
  if (!name) return ''
  return name.split(' ').map(n => n[0]).join('').toUpperCase()
})

const handleLogout = () => {
  store.logout()
}
</script>
