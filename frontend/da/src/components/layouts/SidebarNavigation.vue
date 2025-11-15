<template>
    <!-- Outer Container Wrapper -->
    <div class="h-screen flex items-start justify-start py-4 px-2 bg-white">
        <!-- Sidebar Container with white background -->
        <div
            class="flex flex-col w-72 h-full bg-gray-100 rounded-lg overflow-hidden border border-gray-200 shadow-sm">
            <!-- Header Section with reduced margins -->
            <div
                class="flex flex-col items-center px-4 pt-4 pb-4 border-b border-gray-200">
                <img
                    src="@/assets/da_image.png"
                    alt="DA Logo"
                    class="h-12 w-auto rounded-lg transition-transform duration-300 hover:scale-110" />

                <h1
                    class="mt-2 text-lg font-semibold text-gray-800 tracking-wide text-center">
                    Bayugan City Agriculture
                </h1>

                <span
                    class="mt-2 text-xs font-semibold bg-green-100 text-gray-700 px-4 py-1.5 rounded-full transition duration-300 hover:bg-green-100">
                    {{ roleTitle }}
                </span>
            </div>

            <!-- Navigation Section -->
            <nav
                class="flex-1 px-3 py-4 space-y-1 overflow-y-auto custom-scrollbar">
                <template v-for="item in filteredNavigation" :key="item.title">
                    <!-- Single Menu Item -->
                    <router-link
                        v-if="!item.children"
                        :to="item.to"
                        :class="[
                            isActive(item.to)
                                ? 'bg-green-600 text-white'
                                : 'text-gray-700 hover:bg-green-50',
                            'group flex items-center px-4 py-3 text-sm font-medium rounded-lg transition-all duration-200',
                        ]">
                        <component
                            :is="item.icon"
                            :class="[
                                isActive(item.to)
                                    ? 'text-yellow-300'
                                    : 'text-green-600 group-hover:text-green-600',
                                'h-5 w-5 mr-3 transition-colors duration-200',
                            ]" />

                        <span class="truncate">
                            {{ item.title }}
                        </span>
                    </router-link>

                    <!-- Group Menu Item -->
                    <div v-else>
                        <button
                            @click="toggleGroup(item.title)"
                            :class="[
                                expandedGroups.includes(item.title)
                                    ? 'text-gray-700'
                                    : 'text-gray-700 hover:bg-green-50',
                                'w-full flex items-center justify-between px-4 py-3 text-sm font-medium rounded-lg transition-all duration-200',
                            ]">
                            <div class="flex items-center">
                                <component
                                    :is="item.icon"
                                    :class="[
                                        expandedGroups.includes(item.title)
                                            ? 'text-yellow-400'
                                            : 'text-green-600 group-hover:text-green-600',
                                        'h-5 w-5 mr-3 transition-colors duration-200',
                                    ]" />

                                <span class="truncate">
                                    {{ item.title }}
                                </span>
                            </div>

                            <ChevronDown
                                :class="[
                                    expandedGroups.includes(item.title)
                                        ? 'rotate-180 text-gray-700'
                                        : 'text-gray-500',
                                    'h-4 w-4 transition-transform duration-300',
                                ]" />
                        </button>

                        <!-- Submenu -->
                        <transition name="slide">
                            <div
                                v-show="expandedGroups.includes(item.title)"
                                class="pl-3 mt-1 space-y-1 border-l border-gray-200">
                                <router-link
                                    v-for="child in item.children"
                                    :key="child.title"
                                    :to="child.to"
                                    :class="[
                                        isActive(child.to)
                                            ? 'bg-green-600 text-yellow-300 border-l-4 border-yellow-300 font-semibold'
                                            : 'text-gray-600 hover:text-yellow-400',
                                        'flex items-center pl-7 pr-4 py-2 text-sm font-medium rounded-md transition-all duration-200',
                                    ]">
                                    <span class="truncate">
                                        {{ child.title }}
                                    </span>
                                </router-link>
                            </div>
                        </transition>
                    </div>
                </template>
            </nav>

            <!-- Sign Out Section -->
            <div
                class="flex-shrink-0 border-t border-gray-200 px-4 py-3 bg-gray-100 border-none">
                <button
                    @click="handleLogout"
                    class="group flex items-center justify-between w-full px-4 py-3 text-sm font-semibold bg-white text-red-600 rounded-lg border-2 border-gray-200 transition-all duration-200 hover:bg-red-500 hover:border-red-500 hover:text-white hover:shadow-md active:scale-95">
                    <div class="flex items-center">
                        <LogOut
                            class="w-5 h-5 mr-3 transition-colors duration-200" />
                        <span>Sign Out</span>
                    </div>
                </button>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useWebSocketStore } from '@/stores/websocket'
import { ChevronDown, LogOut, ArrowRight } from 'lucide-vue-next'
import {
    ADMIN_NAVIGATION,
    MUNICIPAL_AGRICULTURIST_NAVIGATION,
    AGRICULTURAL_EXTENSION_WORKER_NAVIGATION,
} from '@/lib/navigation'

const route = useRoute()
const auth = useAuthStore()
const wsStore = useWebSocketStore()
const { disconnect } = wsStore
const expandedGroups = ref([])

/* Toggle sidebar group open/close */
const toggleGroup = groupTitle => {
    const index = expandedGroups.value.indexOf(groupTitle)
    if (index === -1) {
        expandedGroups.value.push(groupTitle)
    } else {
        expandedGroups.value.splice(index, 1)
    }
}

/* Detect active route */
const isActive = to => {
    if (!to || !route) return false
    if (to.name === route.name) return true
    return route.path.startsWith(to.path)
}

/* Get navigation items based on user role */
const filteredNavigation = computed(() => {
    const roles = auth.userRoles
    if (roles.includes('ADMIN')) return ADMIN_NAVIGATION
    if (roles.includes('MUNICIPAL AGRICULTURISTS'))
        return MUNICIPAL_AGRICULTURIST_NAVIGATION
    if (roles.includes('AGRICULTURAL EXTENSION WORKERS'))
        return AGRICULTURAL_EXTENSION_WORKER_NAVIGATION
    return []
})

/* Format role title */
const roleTitle = computed(() => {
    if (!auth.userData.roles || auth.userData.roles.length === 0) return ''
    return auth.userData.roles[0].name
        .split('_')
        .map(word => word.charAt(0).toUpperCase() + word.slice(1).toLowerCase())
        .join(' ')
})

/* Auto-expand group if active child route exists */
const autoExpandActiveGroup = () => {
    const currentPath = route.path
    filteredNavigation.value.forEach(item => {
        if (
            item.children &&
            item.children.some(child => child.to.path === currentPath)
        ) {
            if (!expandedGroups.value.includes(item.title)) {
                expandedGroups.value.push(item.title)
            }
        }
    })
}
// Handle logout
const handleLogout = async () => {
    console.log('Logout clicked')
    try {
        await disconnect()
    } catch (error) {
        console.error('Error disconnecting WebSocket:', error)
    }
    auth.logout()
}

/* Persist expanded groups */
watch(
    expandedGroups,
    newVal => {
        localStorage.setItem('expandedGroups', JSON.stringify(newVal))
    },
    { deep: true },
)

onMounted(() => {
    const saved = localStorage.getItem('expandedGroups')
    if (saved) expandedGroups.value = JSON.parse(saved)
})

let shouldSkipNextAutoExpand = true

watch(
    () => route.path,
    () => {
        if (shouldSkipNextAutoExpand) {
            shouldSkipNextAutoExpand = false
            return
        }
        autoExpandActiveGroup()
    },
)
</script>

<style scoped>
/* Submenu transitions */
.slide-enter-active,
.slide-leave-active {
    transition: opacity 0.25s ease, transform 0.25s ease, max-height 0.25s ease;
    overflow: hidden;
}
.slide-enter-from,
.slide-leave-to {
    opacity: 0;
    transform: translateY(-6px);
    max-height: 0;
}
.slide-enter-to,
.slide-leave-from {
    opacity: 1;
    transform: translateY(0);
    max-height: 500px;
}

/* Reduced motion support */
@media (prefers-reduced-motion: reduce) {
    .slide-enter-active,
    .slide-leave-active {
        transition: none;
    }
}

/* Scrollbar */
.custom-scrollbar {
    overflow-y: auto;
    scrollbar-width: thin;
    scrollbar-color: rgba(0, 0, 0, 0.2) transparent;
}

.custom-scrollbar::-webkit-scrollbar {
    width: 6px;
}
.custom-scrollbar::-webkit-scrollbar-track {
    background: transparent;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
    background-color: rgba(0, 0, 0, 0.2);
    border-radius: 10px;
}
.custom-scrollbar::-webkit-scrollbar-thumb:hover {
    background-color: rgba(0, 0, 0, 0.4);
}

/* Hide scrollbar when not overflowing */
.custom-scrollbar:has(> :not(:only-child)):not(
        :hover
    )::-webkit-scrollbar-thumb {
    background-color: transparent;
}
</style>
