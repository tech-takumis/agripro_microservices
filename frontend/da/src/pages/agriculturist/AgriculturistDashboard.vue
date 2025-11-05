<template>
    <AuthenticatedLayout
        :navigation="navigation"
        role-title="Municipal Agriculturist"
        :page-title="'Municipal Dashboard'">
        <!-- Flex container for navbar and scrollable content -->
        <div class="flex flex-col h-full">
            <!-- Fixed Navbar (does not scroll) -->
            <nav
                class="flex-shrink-0 bg-transparent border-0 shadow-none px-2 lg:px-4 py-3 mb-4 flex items-center justify-between">
                <h4 class="text-3xl font-bold text-gray-700">Dashboard</h4>
                <!-- Right Side: Notifications + Profile -->
                <div class="flex items-center space-x-6">
                    <!-- Notifications -->
                    <div class="relative notifications-dropdown">
                        <button
                            class="relative p-2 text-gray-600 hover:text-gray-900 hover:bg-gray-50 rounded-lg transition-colors"
                            @click="toggleNotificationsDropdown">
                            <Bell class="h-5 w-5" />
                            <!-- Notification Badge -->
                            <span
                                class="absolute -top-1 -right-1 h-5 w-5 bg-red-500 text-white text-xs rounded-full flex items-center justify-center">
                                {{ notificationCount }}
                            </span>
                        </button>

                        <!-- Notifications Dropdown with Smooth Transition -->
                        <transition
                            enter-active-class="transition ease-out duration-300"
                            enter-from-class="opacity-0 -translate-y-2"
                            enter-to-class="opacity-100 translate-y-0"
                            leave-active-class="transition ease-in duration-200"
                            leave-from-class="opacity-100 translate-y-0"
                            leave-to-class="opacity-0 -translate-y-2">
                            <div
                                v-show="showNotificationsDropdown"
                                class="absolute right-0 top-10 w-[26rem] bg-white rounded-2xl shadow-xl border border-gray-200 z-50">
                                <!-- Header -->
                                <div
                                    class="p-4 border-b border-gray-200 flex justify-between items-center bg-gray-50 rounded-t-2xl">
                                    <h3
                                        class="text-lg font-semibold text-gray-900 flex items-center space-x-2">
                                        <Bell class="h-5 w-5 text-green-600" />
                                        <span>Notifications</span>
                                    </h3>
                                    <button
                                        class="text-sm text-green-600 hover:text-green-700 font-medium transition-colors"
                                        @click="markAllAsRead">
                                        Mark all as read
                                    </button>
                                </div>

                                <!-- Notification List -->
                                <div class="max-h-[26rem] overflow-y-auto">
                                    <div
                                        v-for="notification in notifications"
                                        :key="notification.id"
                                        class="p-4 border-b border-gray-100 hover:bg-green-50 transition cursor-pointer flex space-x-4">
                                        <!-- Icon / Status -->
                                        <div class="flex-shrink-0">
                                            <div
                                                class="h-9 w-9 flex items-center justify-center rounded-full"
                                                :class="
                                                    notification.read
                                                        ? 'bg-gray-100'
                                                        : 'bg-green-100'
                                                ">
                                                <Bell
                                                    class="h-4 w-4 text-green-600" />
                                            </div>
                                        </div>

                                        <!-- Message -->
                                        <div class="flex-1">
                                            <div
                                                class="flex justify-between items-center">
                                                <p
                                                    class="font-medium text-gray-900 text-sm">
                                                    {{ notification.title }}
                                                </p>
                                                <span
                                                    class="text-xs text-gray-400"
                                                    >{{
                                                        notification.time
                                                    }}</span
                                                >
                                            </div>
                                            <p
                                                class="text-sm text-gray-600 mt-1">
                                                {{ notification.message }}
                                            </p>
                                            <div
                                                v-if="!notification.read"
                                                class="mt-1 inline-block bg-green-600 text-white text-[10px] px-2 py-0.5 rounded-full">
                                                New
                                            </div>
                                        </div>
                                    </div>

                                    <!-- Empty State -->
                                    <div
                                        v-if="notifications.length === 0"
                                        class="p-8 text-center text-gray-500">
                                        <Bell
                                            class="h-12 w-12 text-gray-300 mx-auto mb-3" />
                                        <p class="text-base font-medium">
                                            No new notifications
                                        </p>
                                        <p class="text-sm text-gray-400">
                                            You're all caught up!
                                        </p>
                                    </div>
                                </div>

                                <!-- Footer -->
                                <div
                                    class="p-3 border-t border-gray-200 bg-gray-50 rounded-b-2xl">
                                    <button
                                        class="w-full text-center text-sm text-green-700 hover:text-green-800 font-medium transition"
                                        @click="viewAllNotifications">
                                        View all notifications
                                    </button>
                                </div>
                            </div>
                        </transition>
                    </div>

                    <!-- Profile Dropdown -->
                    <div class="relative select-none">
                        <!-- Profile Button -->
                        <button
                            class="flex items-center space-x-3 p-2 rounded-lg hover:bg-gray-50 transition-colors"
                            @click.stop="
                                showProfileDropdown = !showProfileDropdown
                            ">
                            <div
                                class="h-8 w-8 rounded-full bg-green-600 flex items-center justify-center">
                                <span
                                    class="text-sm font-semibold text-white"
                                    >{{ userInitials }}</span
                                >
                            </div>
                            <div class="hidden md:block text-left">
                                <p class="text-sm font-medium text-gray-900">
                                    {{ userFullName }}
                                </p>
                                <p class="text-xs text-gray-500">
                                    {{ userEmail }}
                                </p>
                            </div>
                            <ChevronDown
                                class="h-4 w-4 text-gray-400 transition-transform duration-300"
                                :class="{
                                    'rotate-180': showProfileDropdown,
                                }" />
                        </button>

                        <!-- Dropdown Menu with Animation -->
                        <transition
                            enter-active-class="transition ease-out duration-200"
                            enter-from-class="opacity-0 scale-95 -translate-y-2"
                            enter-to-class="opacity-100 scale-100 translate-y-0"
                            leave-active-class="transition ease-in duration-150"
                            leave-from-class="opacity-100 scale-100 translate-y-0"
                            leave-to-class="opacity-0 scale-95 -translate-y-2">
                            <div
                                v-if="showProfileDropdown"
                                class="absolute right-0 mt-2 w-52 bg-white rounded-xl shadow-lg border border-gray-100 z-50 overflow-hidden">
                                <div class="py-2">
                                    <!-- Profile -->
                                    <button
                                        class="flex items-center w-full px-4 py-2 text-sm text-gray-700 hover:bg-green-50 hover:text-green-700 transition"
                                        @click="handleProfileClick">
                                        <User
                                            class="h-4 w-4 mr-3 text-gray-400" />
                                        View Profile
                                    </button>

                                    <!-- Settings -->
                                    <button
                                        class="flex items-center w-full px-4 py-2 text-sm text-gray-700 hover:bg-green-50 hover:text-green-700 transition"
                                        @click="handleSettingsClick">
                                        <Settings
                                            class="h-4 w-4 mr-3 text-gray-400" />
                                        Settings
                                    </button>

                                    <!-- Preferences -->
                                    <button
                                        class="flex items-center w-full px-4 py-2 text-sm text-gray-700 hover:bg-green-50 hover:text-green-700 transition"
                                        @click="handlePreferencesClick">
                                        <Sliders
                                            class="h-4 w-4 mr-3 text-gray-400" />
                                        Preferences
                                    </button>

                                    <!-- Divider -->
                                    <div
                                        class="border-t border-gray-100 my-1"></div>

                                    <!-- Logout -->
                                    <button
                                        class="flex items-center w-full px-4 py-2 text-sm text-red-600 hover:bg-red-50 transition"
                                        @click="handleLogoutClick">
                                        <Briefcase
                                            class="h-4 w-4 mr-3 text-red-500" />
                                        Logout
                                    </button>
                                </div>
                            </div>
                        </transition>
                    </div>
                </div>
            </nav>

            <!-- Content Area (fixed height, no scroll at this level) -->
            <div class="flex-1 flex flex-col min-h-0">
                <div
                    class="w-full space-y-6 pb-6 overflow-hidden flex flex-col">
                    <!-- Stats Grid (fixed, no scroll) -->
                    <div
                        class="flex-shrink-0 grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
                        <!-- Local Farmers -->
                        <div
                            class="bg-white rounded-xl shadow-sm border border-gray-200 hover:shadow-md transition-all duration-200">
                            <div class="p-5">
                                <div class="flex items-center justify-between">
                                    <p
                                        class="text-2xs font-medium text-green-700">
                                        Local Farmers
                                    </p>
                                    <div
                                        :class="[
                                            farmersChange > 0
                                                ? 'text-green-700 bg-green-50'
                                                : farmersChange < 0
                                                ? 'text-red-700 bg-red-50'
                                                : 'text-gray-700 bg-gray-50',
                                            'inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium',
                                        ]">
                                        <ArrowUpRight
                                            v-if="farmersChange > 0"
                                            class="h-3.5 w-3.5 mr-1" />
                                        <ArrowDownRight
                                            v-else-if="farmersChange < 0"
                                            class="h-3.5 w-3.5 mr-1" />
                                        {{
                                            Math.abs(farmersChange).toFixed(1)
                                        }}%
                                    </div>
                                </div>
                                <div
                                    class="mt-2 flex items-center justify-between">
                                    <div class="p-2 rounded-lg bg-green-50">
                                        <Sprout
                                            class="h-6 w-6 text-green-600" />
                                    </div>
                                    <p
                                        class="text-3xl font-semibold text-gray-900">
                                        {{
                                            stats.localFarmers.toLocaleString()
                                        }}
                                    </p>
                                </div>
                            </div>
                        </div>

                        <!-- Disbursements -->
                        <div
                            class="bg-white rounded-xl shadow-sm border border-gray-200 hover:shadow-md transition-all duration-200">
                            <div class="p-5">
                                <div class="flex items-center justify-between">
                                    <p
                                        class="text-2xs font-medium text-yellow-500">
                                        Disbursements
                                    </p>
                                    <div
                                        :class="[
                                            disbursementsChange > 0
                                                ? 'text-yellow-600 bg-yellow-50'
                                                : disbursementsChange < 0
                                                ? 'text-red-700 bg-red-50'
                                                : 'text-gray-700 bg-gray-50',
                                            'inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium',
                                        ]">
                                        <ArrowUpRight
                                            v-if="disbursementsChange > 0"
                                            class="h-3.5 w-3.5 mr-1" />
                                        <ArrowDownRight
                                            v-else-if="disbursementsChange < 0"
                                            class="h-3.5 w-3.5 mr-1" />
                                        {{
                                            Math.abs(
                                                disbursementsChange,
                                            ).toFixed(1)
                                        }}%
                                    </div>
                                </div>
                                <div
                                    class="mt-2 flex items-center justify-between">
                                    <div class="p-2 rounded-lg bg-yellow-50">
                                        <TrendingUp
                                            class="h-6 w-6 text-yellow-500" />
                                    </div>
                                    <p
                                        class="text-3xl font-semibold text-gray-900">
                                        ₱{{
                                            stats.disbursements.toLocaleString()
                                        }}K
                                    </p>
                                </div>
                            </div>
                        </div>

                        <!-- Active Programs -->
                        <div
                            class="bg-green-600 rounded-xl shadow-sm border border-gray-200 hover:shadow-md transition-all duration-200">
                            <div class="p-5">
                                <div class="flex items-center justify-between">
                                    <p class="text-2xs font-medium text-white">
                                        Active Programs
                                    </p>
                                    <div
                                        :class="[
                                            programsChange > 0
                                                ? 'text-green-700 bg-green-50'
                                                : programsChange < 0
                                                ? 'text-red-700 bg-red-50'
                                                : 'text-gray-800 bg-gray-50',
                                            'inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium bg-green-600',
                                        ]">
                                        <ArrowUpRight
                                            v-if="programsChange > 0"
                                            class="h-3.5 w-3.5 mr-1" />
                                        <ArrowDownRight
                                            v-else-if="programsChange < 0"
                                            class="h-3.5 w-3.5 mr-1" />
                                        {{
                                            Math.abs(programsChange).toFixed(1)
                                        }}%
                                    </div>
                                </div>
                                <div
                                    class="mt-2 flex items-center justify-between">
                                    <div class="p-2 rounded-lg bg-green-600">
                                        <Users
                                            class="h-7 w-7 text-green-800 bg-green-600" />
                                    </div>
                                    <p
                                        class="text-3xl font-semibold text-white">
                                        {{ stats.activePrograms }}
                                    </p>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- New 12-column grid layout with Posts (col-8) and Financial/Program (col-4) -->
                    <!-- Posts has independent scroll, Financial/Program share unified scroll -->
                    <div
                        class="flex-1 grid grid-cols-1 lg:grid-cols-12 gap-6 min-h-0">
                        <!-- Posts Card - 8 columns with independent scroll -->
                        <div class="lg:col-span-8 flex flex-col min-h-0">
                            <div
                                class="h-full overflow-y-auto overflow-x-hidden pr-2 custom-scrollbar">
                                <PostCard :posts="posts" />
                            </div>
                        </div>

                        <!-- Financial & Program Cards - 4 columns with unified scroll container -->
                        <div class="lg:col-span-4 flex flex-col min-h-0">
                            <div
                                class="h-full overflow-y-auto overflow-x-hidden pr-2 custom-scrollbar">
                                <div class="space-y-6">
                                    <!-- Latest Transactions -->
                                    <FinancialCard />

                                    <!-- Latest Programs -->
                                    <ProgramCard />
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </AuthenticatedLayout>
</template>

<script setup>
import { ref, onMounted, computed, onUnmounted } from 'vue'
import {
    Sprout,
    CreditCard,
    TrendingUp,
    Users,
    ChevronDown,
    User,
    Settings,
    Sliders,
    ArrowUpRight,
    ArrowDownRight,
    Bell,
    Briefcase,
} from 'lucide-vue-next'
import AuthenticatedLayout from '@/layouts/AuthenticatedLayout.vue'
import PermissionGuard from '@/components/others/PermissionGuard.vue'
import PostCard from '@/components/cards/PostCard.vue'
import FinancialCard from '@/components/cards/FinancialCard.vue'
import ProgramCard from '@/components/cards/ProgramCard.vue'
import { MUNICIPAL_AGRICULTURIST_NAVIGATION } from '@/lib/navigation'
import { useAuthStore } from '@/stores/auth'
import { useDashboardStore } from '@/stores/dashboard'
import { usePostStore } from '@/stores/post'

const postStore = usePostStore()
const posts = computed(() => postStore.posts)
const navigation = MUNICIPAL_AGRICULTURIST_NAVIGATION
const authStore = useAuthStore()
const dashboardStore = useDashboardStore()

// Profile and notification states
const showProfileDropdown = ref(false)
const showFiltersDropdown = ref(false)
const showNotificationsDropdown = ref(false)
const notificationCount = ref(3)

// User data from auth store
const userFullName = computed(() => authStore.userFullName)
const userEmail = computed(() => authStore.userEmail)
const userInitials = computed(() => {
    const name = userFullName.value
    return name
        .split(' ')
        .map(n => n[0])
        .join('')
        .toUpperCase()
        .slice(0, 2)
})

// Dashboard data
const stats = computed(() => ({
    localFarmers: 1250, // This could come from another API endpoint if needed
    disbursements: dashboardStore.formatCurrency(
        dashboardStore.municipalDashboard.transactions
            ?.filter(t => t.type === 'EXPENSE')
            ?.reduce((sum, t) => sum + t.amount, 0) || 0,
    ),
    activePrograms: dashboardStore.municipalDashboard.activePrograms || 0,
}))

// Trend calculations (you might want to fetch historical data from API if needed)
const farmersTrend = ref([
    900, 920, 915, 930, 945, 950, 960, 980, 990, 1005, 1010, 1025,
])
const disbursementsTrend = computed(() => {
    const transactions = dashboardStore.municipalDashboard.transactions || []
    return transactions.map(t => t.amount / 1000)
})
const programsTrend = computed(() => {
    const count = dashboardStore.municipalDashboard.activePrograms
    return Array(12).fill(count)
})

// Notifications data
const notifications = ref([
    {
        id: 1,
        title: 'New Claim Submitted',
        message:
            'Farmer Juan Dela Cruz submitted a new insurance claim for rice crop damage',
        time: '2 minutes ago',
        read: false,
    },
    {
        id: 2,
        title: 'Program Update',
        message: 'Seed Distribution Program has reached 75% completion',
        time: '1 hour ago',
        read: false,
    },
    {
        id: 3,
        title: 'Budget Alert',
        message: 'Fertilizer Subsidy budget is running low (15% remaining)',
        time: '3 hours ago',
        read: true,
    },
])

const getTransactionStatusClass = status => {
    switch (status) {
        case 'Completed':
            return 'bg-green-100 text-green-800'
        case 'Pending':
            return 'bg-yellow-100 text-yellow-800'
        case 'Processing':
            return 'bg-blue-100 text-blue-800'
        default:
            return 'bg-gray-100 text-gray-800'
    }
}

// Profile dropdown methods
const toggleProfileDropdown = () => {
    showProfileDropdown.value = !showProfileDropdown.value
}

// Filters dropdown methods
const toggleFiltersDropdown = () => {
    showFiltersDropdown.value = !showFiltersDropdown.value
    // Close notifications when opening filters
    if (showFiltersDropdown.value) {
        showNotificationsDropdown.value = false
    }
}

// Notifications dropdown methods
const toggleNotificationsDropdown = () => {
    showNotificationsDropdown.value = !showNotificationsDropdown.value
    // Close filters when opening notifications
    if (showNotificationsDropdown.value) {
        showFiltersDropdown.value = false
    }
}

const markAllAsRead = () => {
    notifications.value = notifications.value.map(n => ({ ...n, read: true }))
    notificationCount.value = 0
}

const viewAllNotifications = () => {
    showNotificationsDropdown.value = false
    console.log('Navigate to all notifications page')
}

const handleProfileClick = () => {
    showProfileDropdown.value = false
    // Navigate to profile page or show profile modal
    console.log('Navigate to profile page')
}

const handleSettingsClick = () => {
    showProfileDropdown.value = false
    // Navigate to settings page
    console.log('Navigate to settings page')
}

const handlePreferencesClick = () => {
    showProfileDropdown.value = false
    // Navigate to preferences page
    console.log('Navigate to preferences page')
}

const handleLogoutClick = () => {
    showProfileDropdown.value = false
    // Handle logout
    authStore.logout()
}

// Click outside handler for dropdowns
const handleClickOutside = event => {
    const profileDropdownEl = document.querySelector('.profile-dropdown')
    const filtersDropdownEl = document.querySelector('.filters-dropdown')
    const notificationsDropdownEl = document.querySelector(
        '.notifications-dropdown',
    )

    const clickedInsideProfile = profileDropdownEl?.contains(event.target)
    const clickedInsideFilters = filtersDropdownEl?.contains(event.target)
    const clickedInsideNotifications = notificationsDropdownEl?.contains(
        event.target,
    )

    if (!clickedInsideProfile && showProfileDropdown.value) {
        showProfileDropdown.value = false
    }
    if (!clickedInsideFilters && showFiltersDropdown.value) {
        showFiltersDropdown.value = false
    }
    if (!clickedInsideNotifications && showNotificationsDropdown.value) {
        showNotificationsDropdown.value = false
    }
}

onMounted(async () => {
    // Add click outside listener
    document.addEventListener('click', handleClickOutside)

    // Load dashboard data
    try {
        await dashboardStore.fetchMunicipalDashboard()
        await postStore.fetchPosts()
    } catch (error) {
        console.error('Failed to load dashboard data:', error)
    }
})

onUnmounted(() => {
    // Remove click outside listener
    document.removeEventListener('click', handleClickOutside)
})

// ----- Trends & Sparklines -----
// Example trend data for the last 12 periods (e.g., weeks)

const computePercentChange = arr => {
    if (!arr || arr.length < 2) return 0
    const prev = arr[arr.length - 2]
    const curr = arr[arr.length - 1]
    if (prev === 0) return 0
    return ((curr - prev) / prev) * 100
}

const farmersChange = computed(() => computePercentChange(farmersTrend.value))
const disbursementsChange = computed(() =>
    computePercentChange(disbursementsTrend.value),
)
const programsChange = computed(() => computePercentChange(programsTrend.value))

// Generate polyline points for a 120x32 viewBox sparkline
const generateSparklinePoints = arr => {
    if (!arr || arr.length === 0) return ''
    const width = 120
    const height = 32
    const max = Math.max(...arr)
    const min = Math.min(...arr)
    const range = max - min || 1
    const stepX = width / Math.max(1, arr.length - 1)
    const points = arr.map((v, i) => {
        const x = Math.round(i * stepX)
        const y = Math.round(height - ((v - min) / range) * height)
        return `${x},${y}`
    })
    return points.join(' ')
}
</script>

<style scoped>
/* Custom scrollbar styling */
.custom-scrollbar::-webkit-scrollbar {
    width: 6px;
}

.custom-scrollbar::-webkit-scrollbar-track {
    background: #f1f5f9;
    border-radius: 10px;
}

.custom-scrollbar::-webkit-scrollbar-thumb {
    background: #cbd5e1;
    border-radius: 10px;
}

.custom-scrollbar::-webkit-scrollbar-thumb:hover {
    background: #94a3b8;
}

/* Firefox scrollbar */
.custom-scrollbar {
    scrollbar-width: thin;
    scrollbar-color: #cbd5e1 #f1f5f9;
}
</style>
