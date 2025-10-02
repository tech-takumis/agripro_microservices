<template>
  <AuthenticatedLayout 
    :navigation="adminNavigation" 
    role-title="Admin Portal"
    page-title="All Users"
  >
    <template #header>
      <div class="flex items-center justify-between">
        <div>
          <h1 class="text-2xl font-bold text-gray-900">All Users</h1>
          <p class="text-gray-600">Manage system users and their access</p>
        </div>
        <div class="flex items-center space-x-2">
          <button
            @click="refreshUsers"
            :disabled="userStore.isLoading"
            class="bg-gray-100 hover:bg-gray-200 text-gray-700 px-4 py-2 rounded-md text-sm font-medium transition-colors disabled:opacity-50"
          >
            <RefreshCw :class="{ 'animate-spin': userStore.isLoading }" class="h-4 w-4 mr-2" />
            Refresh
          </button>
          <router-link 
            to="/admin/staff/register"
            class="bg-purple-600 hover:bg-purple-700 text-white px-4 py-2 rounded-md text-sm font-medium transition-colors"
          >
            <UserPlus class="h-4 w-4 mr-2" />
            Add New User
          </router-link>
        </div>
      </div>
    </template>

    <!-- Users Content -->
    <div class="space-y-6">
      <!-- Search and Filter -->
      <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-4">
        <div class="flex flex-col sm:flex-row gap-4">
          <div class="flex-1">
            <div class="relative">
              <Search class="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-gray-400" />
              <input
                v-model="searchTerm"
                type="text"
                placeholder="Search users by name, email, or phone..."
                class="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500"
                @input="handleSearch"
              />
            </div>
          </div>
          <div class="flex gap-2">
            <select
              v-model="selectedRole"
              @change="handleRoleFilter"
              class="px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500"
            >
              <option value="">All Roles</option>
              <option value="ADMIN">Admin</option>
              <option value="Regional Field Offices">Regional Field Offices</option>
              <option value="Regional Rice Focal Person">Regional Rice Focal Person</option>
              <option value="Provincial Rice Program Coordinator">Provincial Rice Program Coordinator</option>
              <option value="Municipal Agriculturists">Municipal Agriculturists</option>
              <option value="Rice Specialists">Rice Specialists</option>
              <option value="Agricultural Extension Workers">Agricultural Extension Workers</option>
            </select>
          </div>
        </div>
      </div>

      <!-- Loading State -->
      <div v-if="userStore.isLoading && userStore.allUsers.length === 0" class="bg-white rounded-lg shadow-sm border border-gray-200">
        <div class="p-6">
          <div class="text-center py-12">
            <Loader2 class="mx-auto h-12 w-12 text-purple-600 animate-spin" />
            <h3 class="mt-2 text-sm font-medium text-gray-900">Loading Users</h3>
            <p class="mt-1 text-sm text-gray-500">Please wait while we fetch all users...</p>
          </div>
        </div>
      </div>

      <!-- Error State -->
      <div v-else-if="userStore.getError" class="bg-white rounded-lg shadow-sm border border-red-200">
        <div class="p-6">
          <div class="text-center py-12">
            <AlertCircle class="mx-auto h-12 w-12 text-red-500" />
            <h3 class="mt-2 text-sm font-medium text-red-900">Error Loading Users</h3>
            <p class="mt-1 text-sm text-red-600">{{ userStore.getError }}</p>
            <button
              @click="refreshUsers"
              class="mt-4 bg-red-600 hover:bg-red-700 text-white px-4 py-2 rounded-md text-sm font-medium transition-colors"
            >
              Try Again
            </button>
          </div>
        </div>
      </div>

      <!-- Users Table -->
      <div v-else-if="filteredUsers.length > 0" class="bg-white rounded-lg shadow-sm border border-gray-200 overflow-hidden">
        <div class="px-6 py-4 border-b border-gray-200">
          <div class="flex items-center justify-between">
            <h2 class="text-lg font-semibold text-gray-900">
              System Users ({{ filteredUsers.length }})
            </h2>
            <div class="text-sm text-gray-500">
              Total: {{ userStore.allUsers.length }} users
            </div>
          </div>
        </div>
        
        <div class="overflow-x-auto">
          <table class="min-w-full divide-y divide-gray-200">
            <thead class="bg-gray-50">
              <tr>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  User
                </th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Contact Information
                </th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Address
                </th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Roles
                </th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Actions
                </th>
              </tr>
            </thead>
            <tbody class="bg-white divide-y divide-gray-200">
              <tr v-for="agriculture in filteredUsers" :key="agriculture.id" class="hover:bg-gray-50">
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="flex items-center">
                    <div class="flex-shrink-0 h-10 w-10">
                      <div class="h-10 w-10 rounded-full bg-purple-100 flex items-center justify-center">
                        <span class="text-sm font-medium text-purple-600">
                          {{ getUserInitials(agriculture) }}
                        </span>
                      </div>
                    </div>
                    <div class="ml-4">
                      <div class="text-sm font-medium text-gray-900">
                        {{ getUserFullName(agriculture) }}
                      </div>
                      <div class="text-sm text-gray-500">
                        @{{ agriculture.username }}
                      </div>
                    </div>
                  </div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="text-sm text-gray-900">
                    <div class="flex items-center mb-1">
                      <Mail class="h-4 w-4 text-gray-400 mr-2" />
                      {{ agriculture.email || 'No email' }}
                    </div>
                    <div class="flex items-center">
                      <Phone class="h-4 w-4 text-gray-400 mr-2" />
                      {{ agriculture.phoneNumber || 'No phone' }}
                    </div>
                  </div>
                </td>
                <td class="px-6 py-4">
                  <div class="text-sm text-gray-900 max-w-xs">
                    <div class="flex items-start">
                      <MapPin class="h-4 w-4 text-gray-400 mr-2 mt-0.5 flex-shrink-0" />
                      <span class="break-words">{{ agriculture.address || 'No address provided' }}</span>
                    </div>
                  </div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="flex flex-wrap gap-1">
                    <span
                      v-for="role in agriculture.roles || []"
                      :key="role.id"
                      class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-purple-100 text-purple-800"
                    >
                      {{ role.name }}
                    </span>
                    <span v-if="!agriculture.roles || agriculture.roles.length === 0" class="text-sm text-gray-500">
                      No roles assigned
                    </span>
                  </div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                  <div class="flex items-center space-x-2">
                    <button
                      @click="viewUser(agriculture)"
                      class="text-purple-600 hover:text-purple-900 transition-colors"
                      title="View User"
                    >
                      <Eye class="h-4 w-4" />
                    </button>
                    <button
                      @click="editUser(agriculture)"
                      class="text-blue-600 hover:text-blue-900 transition-colors"
                      title="Edit User"
                    >
                      <Edit class="h-4 w-4" />
                    </button>
                    <button
                      @click="deleteUser(agriculture)"
                      class="text-red-600 hover:text-red-900 transition-colors"
                      title="Delete User"
                    >
                      <Trash2 class="h-4 w-4" />
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- Pagination -->
        <div v-if="userStore.getPagination.totalPages > 1" class="px-6 py-4 border-t border-gray-200">
          <div class="flex items-center justify-between">
            <div class="text-sm text-gray-700">
              Showing {{ ((userStore.getPagination.currentPage - 1) * userStore.getPagination.itemsPerPage) + 1 }} 
              to {{ Math.min(userStore.getPagination.currentPage * userStore.getPagination.itemsPerPage, userStore.getPagination.totalItems) }} 
              of {{ userStore.getPagination.totalItems }} results
            </div>
            <div class="flex space-x-2">
              <button
                @click="changePage(userStore.getPagination.currentPage - 1)"
                :disabled="userStore.getPagination.currentPage <= 1"
                class="px-3 py-1 border border-gray-300 rounded-md text-sm disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50"
              >
                Previous
              </button>
              <button
                @click="changePage(userStore.getPagination.currentPage + 1)"
                :disabled="userStore.getPagination.currentPage >= userStore.getPagination.totalPages"
                class="px-3 py-1 border border-gray-300 rounded-md text-sm disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50"
              >
                Next
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Empty State -->
      <div v-else class="bg-white rounded-lg shadow-sm border border-gray-200">
        <div class="p-6">
          <div class="text-center py-12">
            <Users class="mx-auto h-12 w-12 text-gray-400" />
            <h3 class="mt-2 text-sm font-medium text-gray-900">No Users Found</h3>
            <p class="mt-1 text-sm text-gray-500">
              {{ searchTerm || selectedRole ? 'No users match your search criteria.' : 'No users have been added to the system yet.' }}
            </p>

          </div>
        </div>
      </div>
    </div>
  </AuthenticatedLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
    Users, UserPlus, RefreshCw, Search, Loader2, AlertCircle,
    Mail, Phone, MapPin, Eye, Edit, Trash2
} from 'lucide-vue-next'
import AuthenticatedLayout from '@/layouts/AuthenticatedLayout.vue'
import { useUserStore } from '@/stores/agriculture'
import { ADMIN_NAVIGATION } from '@/lib/constants'

const router = useRouter()
const userStore = useUserStore()
const adminNavigation = ADMIN_NAVIGATION

// Reactive variables
const searchTerm = ref('')
const selectedRole = ref('')

// Computed properties
const filteredUsers = computed(() => {
    let users = userStore.allUsers

    // Filter by search term
    if (searchTerm.value) {
        const search = searchTerm.value.toLowerCase()
        users = users.filter(agriculture => {
            const fullName = getUserFullName(agriculture).toLowerCase()
            const email = (agriculture.email || '').toLowerCase()
            const phone = (agriculture.phoneNumber || '').toLowerCase()
            const username = (agriculture.username || '').toLowerCase()

            return fullName.includes(search) ||
                email.includes(search) ||
                phone.includes(search) ||
                username.includes(search)
        })
    }

    // Filter by role
    if (selectedRole.value) {
        users = users.filter(agriculture =>
            agriculture.roles && agriculture.roles.some(role => role.name === selectedRole.value)
        )
    }

    return users
})

// Helper functions
const getUserFullName = (agriculture) => {
    if (agriculture.firstName && agriculture.lastName) {
        return `${agriculture.firstName} ${agriculture.lastName}`
    }
    return agriculture.username || 'Unknown User'
}

const getUserInitials = (agriculture) => {
    const fullName = getUserFullName(agriculture)
    if (fullName === 'Unknown User') return 'U'

    return fullName
        .split(' ')
        .map(name => name[0])
        .join('')
        .toUpperCase()
        .substring(0, 2)
}

// Data fetching functions
const fetchUsers = async () => {
    try {
        await userStore.fetchUsers()
    } catch (error) {
        console.error('Failed to fetch users:', error)
    }
}

const refreshUsers = async () => {
    userStore.clearError()
    await fetchUsers()
}

// Search and filter handlers
const handleSearch = () => {
    // Search is handled reactively by the computed property
}

const handleRoleFilter = () => {
    // Role filtering is handled reactively by the computed property
}

// Pagination functions
const changePage = async (page) => {
    if (page >= 1 && page <= userStore.getPagination.totalPages) {
        await userStore.fetchUsers({ page })
    }
}

// User management functions
const viewUser = (agriculture) => {
    // Navigate to agriculture detail page or open modal
    router.push(`/admin/users/${agriculture.id}`)
}

const editUser = (agriculture) => {
    // Navigate to agriculture edit page or open modal
    router.push(`/admin/users/${agriculture.id}/edit`)
}

const deleteUser = async (agriculture) => {
    if (confirm(`Are you sure you want to delete agriculture "${getUserFullName(agriculture)}"? This action cannot be undone.`)) {
        try {
            const result = await userStore.deleteUser(agriculture.id)
            if (result.success) {
                alert('User deleted successfully!')
                // Refresh the users list
                await refreshUsers()
            } else {
                alert(`Failed to delete agriculture: ${result.error}`)
            }
        } catch (error) {
            console.error('Error deleting agriculture:', error)
            alert('An error occurred while deleting the agriculture.')
        }
    }
}

// Lifecycle hooks
onMounted(async () => {
    // Fetch users when component mounts
    await fetchUsers()
})
</script>
