import { defineStore } from 'pinia';
import axios from '@/lib/axios';

export const usePermissionStore = defineStore('permission', {
    state: () => ({
        permissions: [],
        loading: false,
        error: null,
        currentPermission: null,
    }),

    getters: {
        allPermissions: (state) => state.permissions,
        isLoading: (state) => state.loading,
        getError: (state) => state.error,
        getCurrentPermission: (state) => state.currentPermission,

        getPermissionById: (state) => (id) => {
            return state.permissions.find(permission => permission.id === id);
        },

        getPermissionBySlug: (state) => (slug) => {
            return state.permissions.find(permission => permission.slug === slug);
        }
    },

    actions: {
        async fetchPermissions() {
            this.loading = true;
            this.error = null;

            try {
                const response = await axios.get('/api/v1/agriculture/permissions');
                this.permissions = response.data;
                return { success: true, data: response.data };
            } catch (error) {
                this.error = error.response?.data?.message || error.message;
                console.error('Error fetching permissions:', error);
                return { success: false, error: this.error };
            } finally {
                this.loading = false;
            }
        },

        async fetchPermissionById(id) {
            this.loading = true;
            this.error = null;

            try {
                const response = await axios.get(`/api/v1/agriculture/permissions/${id}`);
                this.currentPermission = response.data;
                return { success: true, data: response.data };
            } catch (error) {
                this.error = error.response?.data?.message || error.message;
                console.error(`Error fetching permission ${id}:`, error);
                return { success: false, error: this.error };
            } finally {
                this.loading = false;
            }
        },

        async createPermission(permissionData) {
            this.loading = true;
            this.error = null;

            try {
                const response = await axios.post('/api/v1/agriculture/permissions', {
                    name: permissionData.name,
                    description: permissionData.description
                });

                this.permissions.push(response.data);
                return { success: true, data: response.data };
            } catch (error) {
                this.error = error.response?.data?.message || error.message;
                console.error('Error creating permission:', error);
                return { success: false, error: this.error };
            } finally {
                this.loading = false;
            }
        },

        async updatePermission({ id, ...updateData }) {
            this.loading = true;
            this.error = null;

            try {
                const response = await axios.put(`/api/v1/agriculture/permissions/${id}`, {
                    name: updateData.name,
                    description: updateData.description
                });

                const index = this.permissions.findIndex(p => p.id === id);
                if (index !== -1) {
                    this.permissions[index] = response.data;
                }

                if (this.currentPermission?.id === id) {
                    this.currentPermission = response.data;
                }

                return { success: true, data: response.data };
            } catch (error) {
                this.error = error.response?.data?.message || error.message;
                console.error(`Error updating permission ${id}:`, error);
                return { success: false, error: this.error };
            } finally {
                this.loading = false;
            }
        },

        async deletePermission(id) {
            this.loading = true;
            this.error = null;

            try {
                await axios.delete(`/api/v1/agriculture/permissions/${id}`);

                const index = this.permissions.findIndex(p => p.id === id);
                if (index !== -1) {
                    this.permissions.splice(index, 1);
                }

                if (this.currentPermission?.id === id) {
                    this.currentPermission = null;
                }

                return { success: true };
            } catch (error) {
                this.error = error.response?.data?.message || error.message;
                console.error(`Error deleting permission ${id}:`, error);
                return { success: false, error: this.error };
            } finally {
                this.loading = false;
            }
        },

        setCurrentPermission(permission) {
            this.currentPermission = permission;
        },

        clearCurrentPermission() {
            this.currentPermission = null;
        },

        clearError() {
            this.error = null;
        },

        $reset() {
            this.permissions = [];
            this.loading = false;
            this.error = null;
            this.currentPermission = null;
        }
    },

    persist: true
});