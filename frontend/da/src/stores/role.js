import { defineStore } from "pinia"
import axios from "@/lib/axios"
import { ref, computed } from 'vue'

export const useRoleStore = defineStore("rolePermission", () => {
    // State
    const roles = ref([])
    const loading = ref(false)
    const error = ref(null)
    const initialized = ref(false)

    // Getters as computed
    const allRoles = computed(() => roles.value)
    const isLoading = computed(() => loading.value)
    const getError = computed(() => error.value)

    // Methods that were getters with parameters
    const getRoleByName = (name) =>
        roles.value.find(role => role.name.toUpperCase() === name.toUpperCase())

    const getRoleById = (id) =>
        roles.value.find(role => role.id === id)

    const getRolePermissions = (roleName) => {
        const role = roles.value.find(r => r.name.toUpperCase() === roleName.toUpperCase())
        return role?.permissions || []
    }

    // Actions as functions
    async function fetchRoles() {
        if (initialized.value) return

        loading.value = true
        error.value = null

        try {
            const response = await axios.get("/api/v1/agriculture/roles")
            // Normalize all role names and their permissions to uppercase
            roles.value = response.data.map(role => ({
                ...role,
                name: role.name.toUpperCase(),
                permissions: (role.permissions || []).map(perm => ({
                    ...perm,
                    name: perm.name.toUpperCase()
                }))
            }))
            initialized.value = true
            console.log("Roles fetched and normalized:", roles.value)
            return { success: true, data: roles.value }
        } catch (err) {
            error.value = err.response?.data?.message || "Failed to fetch roles"
            console.error("Error fetching roles:", err)
            return { success: false, error: error.value }
        } finally {
            loading.value = false
        }
    }

    async function createRole(roleData) {
        loading.value = true
        error.value = null

        try {
            const payload = {
                name: roleData.name,
                permissionIds: roleData.permissionIds
            }

            console.log("Creating role with payload:", JSON.stringify(payload, null, 2))

            const response = await axios.post("/api/v1/agriculture/roles", payload)
            roles.value.push(response.data)

            console.log("Role created successfully:", response.data)
            return { success: true, data: response.data }
        } catch (err) {
            error.value = err.response?.data?.message || err.message
            console.error("Error creating role:", err.response?.data || err.message)
            return { success: false, error: error.value }
        } finally {
            loading.value = false
        }
    }

    async function updateRole(roleId, roleData) {
        loading.value = true
        error.value = null

        try {
            const response = await axios.put(`/api/v1/agriculture/roles/${roleId}`, roleData)
            const index = roles.value.findIndex(role => role.id === roleId)
            if (index !== -1) {
                roles.value[index] = response.data
            }
            return { success: true, data: response.data }
        } catch (err) {
            error.value = err.response?.data?.message || err.message
            console.error("Error updating role:", err)
            return { success: false, error: error.value }
        } finally {
            loading.value = false
        }
    }

    async function deleteRole(roleId) {
        loading.value = true
        error.value = null

        try {
            await axios.delete(`/api/v1/agriculture/roles/${roleId}`)
            roles.value = roles.value.filter(role => role.id !== roleId)
            return { success: true }
        } catch (err) {
            error.value = err.response?.data?.message || err.message
            console.error("Error deleting role:", err)
            return { success: false, error: error.value }
        } finally {
            loading.value = false
        }
    }

    return {
        // State
        roles,
        loading,
        error,
        initialized,

        // Getters
        allRoles,
        isLoading,
        getError,

        // Methods
        getRoleByName,
        getRoleById,
        getRolePermissions,

        // Actions
        fetchRoles,
        createRole,
        updateRole,
        deleteRole
    }
})
