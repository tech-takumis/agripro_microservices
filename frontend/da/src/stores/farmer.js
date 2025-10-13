import { defineStore } from 'pinia'
import axios from '@/lib/axios'

export const useUserStore = defineStore('users', {
    state: () => ({
        users: [],
        currentUser: null,
        loading: false,
        error: null,
        pagination: {
            currentPage: 1,
            totalPages: 1,
            totalItems: 0,
            itemsPerPage: 10,
        },
    }),

    getters: {
        // Get all users
        allUsers: state => state.users,

        // Get user by ID
        getUserById: state => id => state.users.find(user => user.id === id),

        // Get users by role
        getUsersByRole: state => roleName =>
            state.users.filter(
                user =>
                    user.roles &&
                    user.roles.some(role => role.name === roleName),
            ),

        // Get loading state
        isLoading: state => state.loading,

        // Get error state
        getError: state => state.error,

        // Get pagination info
        getPagination: state => state.pagination,

        // Check if user has specific role
        userHasRole: state => (userId, roleName) => {
            const user = state.users.find(u => u.id === userId)
            return (
                user &&
                user.roles &&
                user.roles.some(role => role.name === roleName)
            )
        },

        // Get user permissions
        getUserPermissions: state => userId => {
            const user = state.users.find(u => u.id === userId)
            if (!user || !user.roles) return []

            const permissions = []
            user.roles.forEach(role => {
                if (role.permissions) {
                    role.permissions.forEach(permission => {
                        if (!permissions.find(p => p.id === permission.id)) {
                            permissions.push(permission)
                        }
                    })
                }
            })
            return permissions
        },
    },

    actions: {
        // Fetch all users with optional pagination and filters
        async fetchUsers(params = {}) {
            this.loading = true
            this.error = null

            try {
                const queryParams = new URLSearchParams({
                    page: params.page || 1,
                    size: params.size || 10,
                    ...params.filters,
                })

                const response = await axios.get(`/api/v1/users?${queryParams}`)

                if (response.data.content) {
                    // Handle paginated response
                    this.users = response.data.content
                    this.pagination = {
                        currentPage: response.data.number + 1,
                        totalPages: response.data.totalPages,
                        totalItems: response.data.totalElements,
                        itemsPerPage: response.data.size,
                    }
                } else {
                    // Handle simple array response
                    this.users = response.data
                }

                console.log('Users fetched successfully:', this.users)
                return { success: true, data: this.users }
            } catch (error) {
                console.error('Failed to fetch users:', error)
                this.error =
                    error.response?.data?.message || 'Failed to fetch users'
                return { success: false, error: this.error }
            } finally {
                this.loading = false
            }
        },

        // Fetch single user by ID
        async fetchUserById(id) {
            this.loading = true
            this.error = null

            try {
                const response = await axios.get(`/api/v1/users/${id}`)

                this.currentUser = response.data

                // Update user in the users array if it exists
                const index = this.users.findIndex(user => user.id === id)
                if (index !== -1) {
                    this.users[index] = response.data
                }

                console.log('User fetched successfully:', response.data)
                return { success: true, data: response.data }
            } catch (error) {
                console.error('Failed to fetch user:', error)
                this.error =
                    error.response?.data?.message || 'Failed to fetch user'
                return { success: false, error: this.error }
            } finally {
                this.loading = false
            }
        },

        // Update existing user
        async updateUser(id, userData) {
            this.loading = true
            this.error = null

            try {
                const response = await axios.put(
                    `/api/v1/users/${id}`,
                    userData,
                )

                // Update user in the users array
                const index = this.users.findIndex(user => user.id === id)
                if (index !== -1) {
                    this.users[index] = response.data
                }

                // Update current user if it's the same
                if (this.currentUser && this.currentUser.id === id) {
                    this.currentUser = response.data
                }

                console.log('User updated successfully:', response.data)
                return { success: true, data: response.data }
            } catch (error) {
                console.error('Failed to update user:', error)
                this.error =
                    error.response?.data?.message || 'Failed to update user'
                return {
                    success: false,
                    error: this.error,
                    details: error.response?.data,
                }
            } finally {
                this.loading = false
            }
        },

        // Delete user
        async deleteUser(id) {
            this.loading = true
            this.error = null

            try {
                await axios.delete(`/api/v1/users/${id}`, {
                    headers: {
                        'Content-Type': 'application/json',
                        Accept: 'application/json',
                    },
                    withCredentials: true,
                })

                // Remove user from the users array
                this.users = this.users.filter(user => user.id !== id)

                // Clear current user if it's the deleted user
                if (this.currentUser && this.currentUser.id === id) {
                    this.currentUser = null
                }

                console.log('User deleted successfully')
                return { success: true }
            } catch (error) {
                console.error('Failed to delete user:', error)
                this.error =
                    error.response?.data?.message || 'Failed to delete user'
                return { success: false, error: this.error }
            } finally {
                this.loading = false
            }
        },

        // Register new staff (alias for createUser with specific formatting)
        async registerStaff(staffData) {
            // Format the data according to the expected structure
            const formattedData = {
                username:
                    staffData.email?.split('@')[0] ||
                    staffData.firstname?.toLowerCase(),
                firstName: staffData.firstname,
                lastName: staffData.lastname,
                email: staffData.email,
                phoneNumber: staffData.phoneNumber,
                address: staffData.address,
                position: staffData.position,
                location: staffData.location,
                roleId: staffData.roleId,
            }

            return await this.createUser(formattedData)
        },

        // Assign role to user
        async assignRole(userId, roleId) {
            this.loading = true
            this.error = null

            try {
                const response = await axios.post(
                    `/api/v1/users/${userId}/roles/${roleId}`,
                    {},
                    {
                        headers: {
                            'Content-Type': 'application/json',
                            Accept: 'application/json',
                            'X-Tenant-ID': 'agriculture',
                        },
                        withCredentials: true,
                    },
                )

                // Refresh user data
                await this.fetchUserById(userId)

                console.log('Role assigned successfully')
                return { success: true, data: response.data }
            } catch (error) {
                console.error('Failed to assign role:', error)
                this.error =
                    error.response?.data?.message || 'Failed to assign role'
                return { success: false, error: this.error }
            } finally {
                this.loading = false
            }
        },

        // Remove role from user
        async removeRole(userId, roleId) {
            this.loading = true
            this.error = null

            try {
                await axios.delete(`/api/v1/users/${userId}/roles/${roleId}`)

                // Refresh user data
                await this.fetchUserById(userId)

                console.log('Role removed successfully')
                return { success: true }
            } catch (error) {
                console.error('Failed to remove role:', error)
                this.error =
                    error.response?.data?.message || 'Failed to remove role'
                return { success: false, error: this.error }
            } finally {
                this.loading = false
            }
        },

        // Search users
        async searchUsers(searchTerm, filters = {}) {
            this.loading = true
            this.error = null

            try {
                const queryParams = new URLSearchParams({
                    search: searchTerm,
                    ...filters,
                })

                const response = await axios.get(
                    `/api/v1/users/search?${queryParams}`,
                )

                console.log('Users search completed:', response.data)
                return { success: true, data: response.data }
            } catch (error) {
                console.error('Failed to search users:', error)
                this.error =
                    error.response?.data?.message || 'Failed to search users'
                return { success: false, error: this.error }
            } finally {
                this.loading = false
            }
        },

        // Clear error state
        clearError() {
            this.error = null
        },

        // Clear current user
        clearCurrentUser() {
            this.currentUser = null
        },

        // Reset store state
        $reset() {
            this.users = []
            this.currentUser = null
            this.loading = false
            this.error = null
            this.pagination = {
                currentPage: 1,
                totalPages: 1,
                totalItems: 0,
                itemsPerPage: 10,
            }
        },
    },
})
