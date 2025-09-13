import { defineStore } from "pinia"
import axios from "@/lib/axios"

export const useRolePermissionStore = defineStore("rolePermission", {
  state: () => ({
    roles: [],
    permissions: [],
    loading: false,
    error: null,
  }),

  getters: {
    // Get all available permissions
    availablePermissions: (state) => state.permissions,
    
    // Get all roles
    allRoles: (state) => state.roles,
    
    // Get role by ID
    getRoleById: (state) => (id) => {
      return state.roles.find(role => role.id === id)
    },
    
    // Get role by name
    getRoleByName: (state) => (name) => {
      return state.roles.find(role => role.name === name)
    },
    
    // Get permission by ID
    getPermissionById: (state) => (id) => {
      return state.permissions.find(permission => permission.id === id)
    },
    
    // Get permission by name
    getPermissionByName: (state) => (name) => {
      return state.permissions.find(permission => permission.name === name)
    },
    
    // Get permissions for a specific role
    getRolePermissions: (state) => (roleId) => {
      const role = state.roles.find(role => role.id === roleId)
      return role ? role.permissions || [] : []
    },
    
    // Check if role has specific permission
    roleHasPermission: (state) => (roleId, permissionName) => {
      const role = state.roles.find(role => role.id === roleId)
      if (!role || !role.permissions) return false
      return role.permissions.some(permission => permission.name === permissionName)
    },
    
    // Group permissions by category (based on prefix)
    groupedPermissions: (state) => {
      const grouped = {}
      state.permissions.forEach(permission => {
        const category = permission.name.split('_')[1] || 'Other'
        if (!grouped[category]) {
          grouped[category] = []
        }
        grouped[category].push(permission)
      })
      return grouped
    },
    
    // Get roles with their permission counts
    rolesWithPermissionCounts: (state) => {
      return state.roles.map(role => ({
        ...role,
        permissionCount: role.permissions ? role.permissions.length : 0
      }))
    },
  },

  actions: {
    // Fetch all permissions
    async fetchPermissions() {
      try {
        this.loading = true
        this.error = null
        
        const response = await axios.get("/api/v1/permissions", {
          headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json',
            'X-Tenant-ID': 'pcic'
          },
          withCredentials: true,
        })
        this.permissions = response.data
        
        console.log("Permissions fetched successfully:", this.permissions)
        return { success: true, data: response.data }
      } catch (error) {
        this.error = error.response?.data?.message || error.message
        console.error("Error fetching permissions:", error.response?.data || error.message)
        return { success: false, error: this.error }
      } finally {
        this.loading = false
      }
    },

    // Fetch all roles
    async fetchRoles() {
      try {
        this.loading = true
        this.error = null
        
        const response = await axios.get("/api/v1/roles", {
          headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json',
            'X-Tenant-ID': 'pcic'
          },
          withCredentials: true,
        })
        this.roles = response.data
        
        console.log("Roles fetched successfully:", this.roles)
        return { success: true, data: response.data }
      } catch (error) {
        this.error = error.response?.data?.message || error.message
        console.error("Error fetching roles:", error.response?.data || error.message)
        return { success: false, error: this.error }
      } finally {
        this.loading = false
      }
    },

    // Create a new role
    async createRole(roleData) {
      try {
        this.loading = true
        this.error = null
        
        const payload = {
          name: roleData.name,
          permissionIds: roleData.permissionIds || []
        }
        
        console.log("Creating role with payload:", JSON.stringify(payload, null, 2))
        
        const response = await axios.post("/api/v1/roles", payload, {
          headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json',
            'X-Tenant-ID': 'pcic'
          },
          withCredentials: true,
        })
        
        // Add the new role to the store
        this.roles.push(response.data)
        
        console.log("Role created successfully:", response.data)
        return { success: true, data: response.data }
      } catch (error) {
        this.error = error.response?.data?.message || error.message
        console.error("Error creating role:", error.response?.data || error.message)
        return { success: false, error: this.error }
      } finally {
        this.loading = false
      }
    },

    // Update an existing role
    async updateRole(roleId, roleData) {
      try {
        this.loading = true
        this.error = null
        
        const payload = {
          name: roleData.name,
          permissionIds: roleData.permissionIds || []
        }
        
        console.log("Updating role with payload:", JSON.stringify(payload, null, 2))
        
        const response = await axios.put(`/api/v1/roles/${roleId}`, payload, {
          headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json',
            'X-Tenant-ID': 'pcic'
          },
          withCredentials: true,
        })
        
        // Update the role in the store
        const index = this.roles.findIndex(role => role.id === roleId)
        if (index !== -1) {
          this.roles[index] = response.data
        }
        
        console.log("Role updated successfully:", response.data)
        return { success: true, data: response.data }
      } catch (error) {
        this.error = error.response?.data?.message || error.message
        console.error("Error updating role:", error.response?.data || error.message)
        return { success: false, error: this.error }
      } finally {
        this.loading = false
      }
    },

    // Delete a role
    async deleteRole(roleId) {
      try {
        this.loading = true
        this.error = null
        
        await axios.delete(`/api/v1/roles/${roleId}`, {
          headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json',
            'X-Tenant-ID': 'pcic'
          },
          withCredentials: true,
        })
        
        // Remove the role from the store
        this.roles = this.roles.filter(role => role.id !== roleId)
        
        console.log("Role deleted successfully")
        return { success: true }
      } catch (error) {
        this.error = error.response?.data?.message || error.message
        console.error("Error deleting role:", error.response?.data || error.message)
        return { success: false, error: this.error }
      } finally {
        this.loading = false
      }
    },

    // Assign permissions to a role
    async assignPermissionsToRole(roleId, permissionIds) {
      try {
        this.loading = true
        this.error = null
        
        const payload = { permissionIds }
        
        const response = await axios.post(`/api/v1/roles/${roleId}/permissions`, payload, {
          headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json',
            'X-Tenant-ID': 'pcic'
          },
          withCredentials: true,
        })
        
        // Update the role in the store
        const index = this.roles.findIndex(role => role.id === roleId)
        if (index !== -1) {
          this.roles[index] = response.data
        }
        
        console.log("Permissions assigned successfully:", response.data)
        return { success: true, data: response.data }
      } catch (error) {
        this.error = error.response?.data?.message || error.message
        console.error("Error assigning permissions:", error.response?.data || error.message)
        return { success: false, error: this.error }
      } finally {
        this.loading = false
      }
    },

    // Remove permissions from a role
    async removePermissionsFromRole(roleId, permissionIds) {
      try {
        this.loading = true
        this.error = null
        
        const payload = { permissionIds }
        
        const response = await axios.delete(`/api/v1/roles/${roleId}/permissions`, {
          headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json',
            'X-Tenant-ID': 'pcic'
          },
          withCredentials: true,
          data: payload
        })
        
        // Update the role in the store
        const index = this.roles.findIndex(role => role.id === roleId)
        if (index !== -1) {
          this.roles[index] = response.data
        }
        
        console.log("Permissions removed successfully:", response.data)
        return { success: true, data: response.data }
      } catch (error) {
        this.error = error.response?.data?.message || error.message
        console.error("Error removing permissions:", error.response?.data || error.message)
        return { success: false, error: this.error }
      } finally {
        this.loading = false
      }
    },

    // Get role details with permissions
    async getRoleDetails(roleId) {
      try {
        this.loading = true
        this.error = null
        
        const response = await axios.get(`/api/v1/roles/${roleId}`, {
          headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json',
            'X-Tenant-ID': 'pcic'
          },
          withCredentials: true,
        })
        
        console.log("Role details fetched:", response.data)
        return { success: true, data: response.data }
      } catch (error) {
        this.error = error.response?.data?.message || error.message
        console.error("Error fetching role details:", error.response?.data || error.message)
        return { success: false, error: this.error }
      } finally {
        this.loading = false
      }
    },

    // Create a new permission
    async createPermission(permissionData) {
      try {
        this.loading = true
        this.error = null
        
        const payload = {
          name: permissionData.name,
          description: permissionData.description
        }
        
        console.log("Creating permission with payload:", JSON.stringify(payload, null, 2))
        
        const response = await axios.post("/api/v1/permissions", payload, {
          headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json',
            'X-Tenant-ID': 'pcic'
          },
          withCredentials: true,
        })
        
        // Add the new permission to the store
        this.permissions.push(response.data)
        
        console.log("Permission created successfully:", response.data)
        return { success: true, data: response.data }
      } catch (error) {
        this.error = error.response?.data?.message || error.message
        console.error("Error creating permission:", error.response?.data || error.message)
        return { success: false, error: this.error }
      } finally {
        this.loading = false
      }
    },

    // Update an existing permission
    async updatePermission(permissionId, permissionData) {
      try {
        this.loading = true
        this.error = null
        
        const payload = {
          name: permissionData.name,
          description: permissionData.description
        }
        
        console.log("Updating permission with payload:", JSON.stringify(payload, null, 2))
        
        const response = await axios.put(`/api/v1/permissions/${permissionId}`, payload, {
          headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json',
            'X-Tenant-ID': 'pcic'
          },
          withCredentials: true,
        })
        
        // Update the permission in the store
        const index = this.permissions.findIndex(permission => permission.id === permissionId)
        if (index !== -1) {
          this.permissions[index] = response.data
        }
        
        console.log("Permission updated successfully:", response.data)
        return { success: true, data: response.data }
      } catch (error) {
        this.error = error.response?.data?.message || error.message
        console.error("Error updating permission:", error.response?.data || error.message)
        return { success: false, error: this.error }
      } finally {
        this.loading = false
      }
    },

    // Delete a permission
    async deletePermission(permissionId) {
      try {
        this.loading = true
        this.error = null
        
        await axios.delete(`/api/v1/permissions/${permissionId}`, {
          headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json',
            'X-Tenant-ID': 'pcic'
          },
          withCredentials: true,
        })
        
        // Remove the permission from the store
        this.permissions = this.permissions.filter(permission => permission.id !== permissionId)
        
        // Also remove from all roles that have this permission
        this.roles = this.roles.map(role => ({
          ...role,
          permissions: role.permissions ? role.permissions.filter(p => p.id !== permissionId) : []
        }))
        
        console.log("Permission deleted successfully")
        return { success: true }
      } catch (error) {
        this.error = error.response?.data?.message || error.message
        console.error("Error deleting permission:", error.response?.data || error.message)
        return { success: false, error: this.error }
      } finally {
        this.loading = false
      }
    },

    // Initialize store (fetch both roles and permissions)
    async initialize() {
      try {
        await Promise.all([
          this.fetchRoles(),
          this.fetchPermissions()
        ])
        return { success: true }
      } catch (error) {
        console.error("Error initializing role permission store:", error)
        return { success: false, error: error.message }
      }
    },

    // Clear error state
    clearError() {
      this.error = null
    },

    // Reset store state
    $reset() {
      this.roles = []
      this.permissions = []
      this.loading = false
      this.error = null
    }
  },
})
