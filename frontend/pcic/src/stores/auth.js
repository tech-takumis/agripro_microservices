import axios from "@/lib/axios"
import { defineStore, acceptHMRUpdate } from "pinia"
import { ref, computed } from "vue"

export const useAuthStore = defineStore("auth", () => {
    // State
    const userData = ref({})
    const availableRoles = ref([])
    const availablePermissions = ref([])
    const roleHierarchy = ref({})
    const permissionMap = ref({})
    const isInitialized = ref(false)

    // Getters
    const isAuthenticate = computed(() => Object.values(userData.value).length > 0)
    const userRoles = computed(() => userData.value?.roles?.map(r => r.name) || [])
    const userPermissions = computed(() => {
        if (!userData.value?.roles) return []
        const perms = []
        userData.value.roles.forEach(role => {
            if (role.permissions) {
                perms.push(...role.permissions.map(p => p.name))
            }
        })
        return [...new Set(perms)]
    })
    const userPrimaryRole = computed(() => userData.value?.roles?.[0]?.name || null)

    // Actions
    async function fetchRolesAndPermissions() {
        try {
            const [rolesResponse, permissionsResponse] = await Promise.all([
                axios.get("/api/v1/roles"),
                axios.get("/api/v1/permissions")
            ])
            availableRoles.value = rolesResponse.data
            availablePermissions.value = permissionsResponse.data
            buildRoleHierarchy()
            buildPermissionMap()
        } catch (error) {
            console.error("Error fetching roles and permissions:", error)
            throw error
        }
    }

    function buildRoleHierarchy() {
        const hierarchy = {}
        availableRoles.value.forEach((role, index) => {
            hierarchy[role.name] = availableRoles.value.length - index
        })
        roleHierarchy.value = hierarchy
    }

    function buildPermissionMap() {
        const map = {}
        availablePermissions.value.forEach(permission => {
            map[permission.name] = permission
        })
        permissionMap.value = map
    }

    async function login(form, setErrors, processing) {
        try {
            processing.value = true
            const loginResponse = await
                axios.post("/api/v1/pcic/auth/login",form.value)
            // Handle new user response structure
            const { websocketToken, user } = loginResponse.data
            if (!user || !websocketToken) {
                setErrors.value = ["Invalid login response."]
                return
            }
            // Store websocket token for future websocket connection
            localStorage.setItem("websocketToken", websocketToken)
            userData.value = user

            // Validate at least one role
            if (!user.roles || user.roles.length === 0) {
                setErrors.value = ["Access denied: No valid role assigned."]
                await logout()
                return
            }

            // Redirect to defaultRoute of the first role
            const defaultRoute = user.roles[0]?.defaultRoute || "/"
            this.router.push(defaultRoute)
        } catch (error) {
            if (error.response && error.response.status === 422) {
                setErrors.value = Object.values(error.response.data.errors).flat()
            } else if (error.response && error.response.status === 401) {
                setErrors.value = ["Invalid username or password."]
            } else {
                setErrors.value = ["Login failed. Please try again."]
            }
        } finally {
            processing.value = false
        }
    }

    // Used for browser refresh to fetch authenticated user
    async function getAuthenticated() {
        try {
            const response = await axios.get("/api/v1/pcic/auth/me")
            userData.value = response.data.user || response.data
        } catch (error) {
            $reset()
            // Redirect to login page if error occurs
            if (this.router) {
                this.router.push({ name: "login" })
            }
            throw error
        }
    }

    function getRedirectPath() {
        // Use defaultRoute of the first role if available
        const roles = userData.value?.roles || []
        if (roles.length === 0) return { name: "login" }
        const defaultRoute = roles[0]?.defaultRoute
        if (defaultRoute) return defaultRoute
        return { name: "login" }
    }

    async function logout() {
        await axios
            .post("/api/v1/pcic/auth/logout")
            .then(() => {
                $reset()
                localStorage.removeItem("websocketToken")
                this.router.push({ name: "login" })
            })
            .catch((error) => {
                if (error.response?.status !== 422) throw error
            })
    }

    function $reset() {
        userData.value = {}
        availableRoles.value = []
        availablePermissions.value = []
        roleHierarchy.value = {}
        permissionMap.value = {}
        isInitialized.value = false
        localStorage.removeItem("websocketToken")
    }

    return {
        // state
        userData,
        availableRoles,
        availablePermissions,
        roleHierarchy,
        permissionMap,
        isInitialized,
        // getters
        isAuthenticate,
        userRoles,
        userPermissions,
        userPrimaryRole,
        // actions
        fetchRolesAndPermissions,
        buildRoleHierarchy,
        buildPermissionMap,
        login,
        getAuthenticated,
        getRedirectPath,
        logout,
        $reset,
    }
})

if (import.meta.hot) {
    import.meta.hot.accept(acceptHMRUpdate(useAuthStore, import.meta.hot))
}
