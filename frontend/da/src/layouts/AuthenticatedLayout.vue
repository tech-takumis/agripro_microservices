<template>
    <div class="flex h-screen overflow-hidden bg-gray-100 print:h-auto print:bg-white">
        <!-- Desktop Sidebar -->
        <div class="hidden md:flex md:w-64 flex-shrink-0 md:flex-col print:hidden">
            <div class="flex flex-col h-full bg-white border-r border-gray-200">
                <!-- Main Navigation -->
                <div class="flex-1 overflow-y-auto overflow-x-hidden">
                    <SidebarNavigation
                        :navigation="navigation"
                        :role-title="roleTitle"
                        :user-full-name="authStore.userFullName"
                        :user-email="authStore.userEmail"
                        :user-initials="userInitials"
                        @help-support="handleHelpSupport" />
                </div>

                <!-- Bottom Section with Logout -->
                <div class="flex-shrink-0">
                    <div class="px-3 py-4">
                        <div class="space-y-3">
                            <div class="border-t border-gray-200"></div>
                            <button
                                @click="handleLogout"
                                class="flex items-center justify-between w-full px-4 py-2.5 text-sm font-medium text-red-600 hover:bg-red-50 rounded-lg group transition-colors"
                            >
                                <div class="flex items-center">
                                    <LogOut class="w-5 h-5 mr-3" />
                                    <span>Sign Out</span>
                                </div>
                                <ArrowRight class="w-4 h-4 opacity-0 group-hover:opacity-100 transition-opacity" />
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Mobile Sidebar -->
        <div
            v-if="sidebarOpen"
            class="fixed inset-0 z-40 md:hidden print:hidden">
            <div
                class="fixed inset-0 bg-gray-600 bg-opacity-75"
                @click="sidebarOpen = false"></div>
            <div class="relative flex flex-col h-full w-[280px] max-w-[95vw] bg-white">
                <div class="absolute top-0 right-0 -mr-12 pt-2">
                    <button
                        @click="sidebarOpen = false"
                        class="ml-1 flex items-center justify-center h-10 w-10 rounded-full focus:outline-none focus:ring-2 focus:ring-inset focus:ring-white">
                        <X class="h-6 w-6 text-white" />
                        <span class="sr-only">Close sidebar</span>
                    </button>
                </div>

                <!-- Mobile Navigation Content -->
                <div class="flex-1 overflow-y-auto overflow-x-hidden">
                    <SidebarNavigation
                        :navigation="navigation"
                        :role-title="roleTitle"
                        :user-full-name="authStore.userFullName"
                        :user-email="authStore.userEmail"
                        :user-initials="userInitials"
                        @help-support="handleHelpSupport" />
                </div>

                <!-- Mobile Bottom Section with Logout -->
                <div class="flex-shrink-0">
                    <div class="px-3 py-4">
                        <div class="space-y-3">
                            <div class="border-t border-gray-200"></div>
                            <button
                                @click="handleLogout"
                                class="flex items-center justify-between w-full px-4 py-2.5 text-sm font-medium text-red-600 hover:bg-red-50 rounded-lg group transition-colors"
                            >
                                <div class="flex items-center">
                                    <LogOut class="w-5 h-5 mr-3" />
                                    <span>Sign Out</span>
                                </div>
                                <ArrowRight class="w-4 h-4 opacity-0 group-hover:opacity-100 transition-opacity" />
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Main content area -->
        <div class="flex flex-col flex-1 w-0 overflow-hidden">
            <!-- Top bar for mobile -->
            <div class="md:hidden bg-white border-b border-gray-200 px-4 py-2 flex items-center justify-between shadow-sm print:hidden">
                <button
                    @click="sidebarOpen = true"
                    class="p-2 rounded-md text-gray-400 hover:text-gray-600 hover:bg-gray-100"
                    aria-label="Open sidebar">
                    <Menu class="h-6 w-6" />
                </button>
                <h1 class="text-lg font-semibold text-gray-900 truncate">
                    {{ pageTitle }}
                </h1>
                <div class="w-10"></div>
            </div>

            <!-- Page header -->
            <header
                v-if="$slots.header"
                class="bg-white shadow-sm border-b border-gray-200 hidden md:block print:hidden">
                <div class="px-4 py-4 sm:px-6 lg:px-8 max-w-full overflow-hidden">
                    <slot name="header" />
                </div>
            </header>

            <!-- Mobile header -->
            <header
                v-if="$slots.header"
                class="bg-white shadow-sm border-b border-gray-200 md:hidden print:hidden">
                <div class="px-4 py-4 sm:px-6 lg:px-8 max-w-full overflow-hidden">
                    <slot name="header" />
                </div>
            </header>

            <!-- Main content -->
            <main class="flex-1 relative overflow-y-auto overflow-x-hidden bg-gray-50 print:overflow-visible print:bg-white">
                <div class="p-4 sm:p-6 lg:p-8 print:p-0 max-w-full">
                    <slot />
                </div>
            </main>
        </div>
    </div>
</template>

<script setup>
import { ref, computed , onMounted, onBeforeUnmount} from 'vue'
import { Menu, X, LogOut, ArrowRight } from 'lucide-vue-next'
import {useAuthStore} from '@/stores/auth'
import {useWebSocketStore} from '@/stores/websocket'
import SidebarNavigation from '@/components/layouts/SidebarNavigation.vue'

const authStore = useAuthStore()
const wsStore = useWebSocketStore()
const { disconnect,connect } = wsStore

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
  const name = authStore.userFullName
  if (!name) return ''
  return name.split(' ').map(n => n[0]).join('').toUpperCase()
})

const handleHelpSupport = (action) => {
  switch (action) {
    case 'faq':
      // Navigate to FAQ page or show FAQ modal
      console.log('Navigate to FAQ page')
      break
    case 'user-guide':
      // Navigate to user guide page or show user guide modal
      console.log('Navigate to user guide page')
      break
    case 'contact':
      // Navigate to contact form or show contact modal
      console.log('Navigate to contact form')
      break
    default:
      console.log('Unknown help support action:', action)
  }
}

// Handle logout
const handleLogout = async () => {
  // Perform logout logic, e.g., call an API, clear tokens, etc.
  console.log('Logout clicked')

  // Disconnect WebSocket before logout
  await disconnect()

  authStore.logout() // Assuming you have a logout action in your auth store
}

onMounted(async () => {
    try {
        if (!wsStore.connected) {
            console.log('[App] 🔌 Connecting WebSocket...')
            await wsStore.connect()
            console.log('[App] ✅ WebSocket connected globally')
        }
    } catch (err) {
        console.error('[App] ❌ Failed to connect WebSocket:', err)
    }
})
</script>
<style>
@media print {
    .authenticated-layout,
    .page-content {
        display: block !important;
    }
}
</style>
