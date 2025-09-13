import { defineStore } from "pinia"
import axios from "@/lib/axios"

export const useRolePermissionStore = defineStore("rolePermission", {
  state: () => ({
    roles: [],
    authorities: [],
    loading: false,
    error: null,
  }),

  getters: {
    // Get all available permissions/authorities
    availableAuthorities: (state) => state.authorities,
    
    // Get all roles
    allRoles: (state) => state.roles,
    
    // Get role by ID
    getRoleById: (state) => (id) => {
      return state.roles.find(role => role.id === id)
    },
    
    // Get authority by ID
    getAuthorityById: (state) => (id) => {
      return state.authorities.find(authority => authority.id === id)
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
    
    // Group authorities by category (based on prefix)
    groupedAuthorities: (state) => {
      const grouped = {}
      state.authorities.forEach(authority => {
        const category = authority.name.split('_')[1] || 'Other'
        if (!grouped[category]) {
          grouped[category] = []
        }
        grouped[category].push(authority)
      })
      return grouped
    },
  },

  actions: {
    // Fetch all authorities/permissions
    async fetchAuthorities() {
      try {
        this.loading = true
        this.error = null
        
        const response = await axios.get("/authorities  ")
        this.authorities = response.data
        
        console.log("Authorities fetched successfully:", this.authorities)
        return { success: true, data: response.data }
      } catch (error) {
        this.error = error.response?.data?.message || error.message
        console.error("Error fetching authorities:", error.response?.data || error.message)
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
        
        const response = await axios.get("/roles")
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
          permissionIds: roleData.permissionIds
        }
        
        console.log("Creating role with payload:", JSON.stringify(payload, null, 2))
        
        const response = await axios.post("/roles", payload)
        
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
          permissionIds: roleData.permissionIds
        }
        
        console.log("Updating role with payload:", JSON.stringify(payload, null, 2))
        
        const response = await axios.put(`/roles/${roleId}`, payload)
        
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
        
        await axios.delete(`/roles/${roleId}`)
        
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
        
        const response = await axios.post(`/roles/${roleId}/permissions`, payload)
        
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
        
        const response = await axios.delete(`/roles/${roleId}/permissions`, {
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
        
        const response = await axios.get(`/roles/${roleId}`)
        
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

    // Initialize store (fetch both roles and authorities)
    async initialize() {
      try {
        await Promise.all([
          this.fetchRoles(),
          this.fetchAuthorities()
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
      this.authorities = []
      this.loading = false
      this.error = null
    }
  },
})
