import { defineStore } from "pinia"
import axios from "@/lib/axios"

export const useRoleStore = defineStore("rolePermission", {
  state: () => ({
    roles: [],
    loading: false,
    error: null,
    initialized: false
  }),

  getters: {
    // Get all roles
    allRoles: (state) => state.roles,
    isLoading: (state) => state.loading,
    getError: (state) => state.error,

    // Get role by name (case insensitive)
    getRoleByName: (state) => (name) => {
      return state.roles.find(
        role => role.name.toUpperCase() === name.toUpperCase()
      );
    },

    // Get role by ID
    getRoleById: (state) => (id) => {
      return state.roles.find(role => role.id === id)
    },

    // Get permissions for a specific role
    getRolePermissions: (state) => (roleName) => {
      const role = state.roles.find(
        r => r.name.toUpperCase() === roleName.toUpperCase()
      );
      return role?.permissions || [];
    }
  },

  actions: {
    // Fetch all roles
    async fetchRoles() {
      if (this.initialized) return;

      this.loading = true
      this.error = null

      try {
        const response = await axios.get("/api/v1/agriculture/roles")
        // Normalize all role names and their permissions to uppercase
        this.roles = response.data.map(role => ({
          ...role,
          name: role.name.toUpperCase(),
          permissions: (role.permissions || []).map(perm => ({
            ...perm,
            name: perm.name.toUpperCase()
          }))
        }))
        this.initialized = true
        console.log("Roles fetched and normalized:", this.roles)
        return { success: true, data: this.roles }
      } catch (error) {
        this.error = error.response?.data?.message || "Failed to fetch roles"
        console.error("Error fetching roles:", error)
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
        
        const response = await axios.post("/api/v1/agriculture/roles", payload)
        
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
        
        const response = await axios.put(`/api/v1/agriculture/roles/${roleId}`, payload);
        
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
        await axios.delete(`/api/v1/agriculture/roles/${id}`);
        
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
    
    // Check if role exists (case insensitive)
    hasRole(roleName) {
      return this.roles.some(
        role => role.name.toUpperCase() === roleName.toUpperCase()
      );
    },

    // Check if role has specific permission (case insensitive)
    roleHasPermission(roleName, permissionName) {
      const role = this.roles.find(
        r => r.name.toUpperCase() === roleName.toUpperCase()
      );
      return role?.permissions.some(
        p => p.name.toUpperCase() === permissionName.toUpperCase()
      );
    },

    // Reset store
    $reset() {
      this.roles = [];
      this.loading = false;
      this.error = null;
      this.initialized = false;
    }
  },
  
  persist: true
});
