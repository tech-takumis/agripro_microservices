import { defineStore } from "pinia"
import axios from "@/lib/axios"

export const useRoleStore = defineStore("rolePermission", {
  state: () => ({
    roles: [],
    loading: false,
    error: null,
  }),

  getters: {
    // Get all roles
    allRoles: (state) => state.roles,
    
    // Get role by ID
    getRoleById: (state) => (id) => {
      return state.roles.find(role => role.id === id)
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
  },

  actions: {
    // Fetch all roles
    async fetchRoles() {
      try {
        this.loading = true
        this.error = null
        
        const response = await axios.get("/api/v1/roles")
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
        
        const response = await axios.post("/api/v1/roles", payload)
        
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
      this.loading = true;
      this.error = null;
      
      try {
        const payload = {
          name: roleData.name,
          permissionIds: roleData.permissionIds || []
        };
        
        const response = await axios.put(`/api/v1roles/${roleId}`, payload);
        
        // Update the role in the roles array
        const index = this.roles.findIndex(role => role.id === roleId);
        if (index !== -1) {
          this.roles[index] = response.data;
        }
        
        // Update currentRole if it's the one being updated
        if (this.currentRole?.id === roleId) {
          this.currentRole = response.data;
        }
        
        return { success: true, data: response.data };
      } catch (error) {
        this.error = error.response?.data?.message || error.message;
        console.error(`Error updating role ${roleId}:`, error);
        return { success: false, error: this.error };
      } finally {
        this.loading = false;
      }
    },

    async deleteRole(id) {
      this.loading = true;
      this.error = null;
      
      try {
        await axios.delete(`/api/v1/roles/${id}`);
        
        const index = this.roles.findIndex(role => role.id === id);
        if (index !== -1) {
          this.roles.splice(index, 1);
        }
        
        if (this.currentRole?.id === id) {
          this.currentRole = null;
        }
        
        return { success: true };
      } catch (error) {
        this.error = error.response?.data?.message || error.message;
        console.error(`Error deleting role ${id}:`, error);
        return { success: false, error: this.error };
      } finally {
        this.loading = false;
      }
    },
    
    setCurrentRole(role) {
      this.currentRole = role;
    },
    
    clearCurrentRole() {
      this.currentRole = null;
    },
    
    clearError() {
      this.error = null;
    },
    
    $reset() {
      this.roles = [];
      this.loading = false;
      this.error = null;
      this.currentRole = null;
    }
  },
  
  persist: true
});
