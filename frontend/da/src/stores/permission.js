import { defineStore } from 'pinia';
import axios from '@/lib/axios';

export const usePermissionStore = defineStore('permission', {
    state: () => ({
        permissions: [],
        loading: false,
        error: null,
        initialized: false
    }),

    getters: {
        allPermissions: state => state.permissions,
        isLoading: state => state.loading,
        getError: state => state.error,

        // Get permission by name (case insensitive)
        getPermissionByName: state => name => {
            return state.permissions.find(
                permission => permission.name.toUpperCase() === name.toUpperCase()
            );
        },

        // Check if permission exists
        hasPermission: state => name => {
            return state.permissions.some(
                permission => permission.name.toUpperCase() === name.toUpperCase()
            );
        }
    },

    actions: {
        async fetchPermissions() {
            if (this.initialized) return;

            this.loading = true;
            this.error = null;

            try {
                const response = await axios.get('/api/v1/agriculture/permissions');
                // Normalize all permission names to uppercase when storing
                this.permissions = response.data.map(permission => ({
                    ...permission,
                    name: permission.name.toUpperCase()
                }));
                this.initialized = true;
                console.log('Permissions fetched and normalized:', this.permissions);
                return { success: true, data: this.permissions };
            } catch (error) {
                this.error = error.response?.data?.message || 'Failed to fetch permissions';
                console.error('Error fetching permissions:', error);
                return { success: false, error: this.error };
            } finally {
                this.loading = false;
            }
        },

        // Add a new permission (admin only)
        async addPermission(permissionData) {
            this.loading = true;
            this.error = null;

            try {
                const response = await axios.post('/api/v1/agriculture/permissions', {
                    ...permissionData,
                    name: permissionData.name.toUpperCase()
                });
                this.permissions.push(response.data);
                return { success: true, data: response.data };
            } catch (error) {
                this.error = error.response?.data?.message || 'Failed to add permission';
                return { success: false, error: this.error };
            } finally {
                this.loading = false;
            }
        },

        // Reset store
        $reset() {
            this.permissions = [];
            this.loading = false;
            this.error = null;
            this.initialized = false;
        }
    }
});