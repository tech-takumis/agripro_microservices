import { defineStore } from 'pinia';
import axios from '@/lib/axios';
import { ref, computed } from 'vue';

export const usePermissionStore = defineStore('permission', () => {
    // State
    const permissions = ref([]);
    const loading = ref(false);
    const error = ref(null);
    const initialized = ref(false);

    // Getters as computed
    const allPermissions = computed(() => permissions.value);
    const isLoading = computed(() => loading.value);
    const getError = computed(() => error.value);

    // Methods that were getters with parameters
    const getPermissionByName = (name) => {
        return permissions.value.find(
            permission => permission.name.toUpperCase() === name.toUpperCase()
        );
    };

    const hasPermission = (name) => {
        return permissions.value.some(
            permission => permission.name.toUpperCase() === name.toUpperCase()
        );
    };

    // Actions as functions
    async function fetchPermissions() {
        if (initialized.value) return;

        loading.value = true;
        error.value = null;

        try {
            const response = await axios.get('/api/v1/agriculture/permissions');
            // Normalize all permission names to uppercase when storing
            permissions.value = response.data.map(permission => ({
                ...permission,
                name: permission.name.toUpperCase()
            }));
            initialized.value = true;
            console.log('Permissions fetched and normalized:', permissions.value);
            return { success: true, data: permissions.value };
        } catch (err) {
            error.value = err.response?.data?.message || 'Failed to fetch permissions';
            console.error('Error fetching permissions:', err);
            return { success: false, error: error.value };
        } finally {
            loading.value = false;
        }
    }

    async function addPermission(permissionData) {
        loading.value = true;
        error.value = null;

        try {
            const response = await axios.post('/api/v1/agriculture/permissions', {
                ...permissionData,
                name: permissionData.name.toUpperCase()
            });
            permissions.value.push(response.data);
            return { success: true, data: response.data };
        } catch (err) {
            error.value = err.response?.data?.message || 'Failed to add permission';
            return { success: false, error: error.value };
        } finally {
            loading.value = false;
        }
    }

    function reset() {
        permissions.value = [];
        loading.value = false;
        error.value = null;
        initialized.value = false;
    }

    return {
        // State
        permissions,
        loading,
        error,
        initialized,

        // Getters
        allPermissions,
        isLoading,
        getError,

        // Methods
        getPermissionByName,
        hasPermission,

        // Actions
        fetchPermissions,
        addPermission,
        reset
    };
});