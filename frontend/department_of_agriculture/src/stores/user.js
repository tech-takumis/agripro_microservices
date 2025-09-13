import axios from "@/lib/axios"
import { defineStore, acceptHMRUpdate } from "pinia"
import { useRolePermissionStore } from "./rolePermission"

export const useUserStore = defineStore("users", {
  state: () => ({
    userData: {
      userId: null,
      username: "",
      firstName: "",
      lastName: "",
      email: "",
      phoneNumber: "",
      address: "",
      roles: [],
      permission: []
    },
    rbacConfig: {
      roleCategories: {},
      roleRedirects: {},
      featurePermissions: {}
    },
    loading: false,
    error: null
  }),

  getters: {
    isAuthenticated: (state) => !!state.userData.userId,
    hasVerified: (state) => !!state.userData.email_verified_at,
    fullName: (state) => `${state.userData.firstName} ${state.userData.lastName}`.trim(),
    userEmail: (state) => state.userData.email,
    userRoles: (state) => state.userData.roles,
    userPermissions: (state) => state.userData.permission,
    isAdmin: (state) => state.userData.roles.includes("ADMIN"),
    isUnderwriter: (state) => state.userData.roles.includes("UNDERWRITER"),
    isClaimProcessor: (state) => state.userData.roles.includes("CLAIM PROCESSOR"),
    isTeller: (state) => state.userData.roles.includes("TELLER"),
    isManagement: (state) => state.userData.roles.includes("MANAGEMENT"),

    roleCategory: (state) => {
      const primaryRole = state.userData.roles[0]
      return state.rbacConfig.roleCategories[primaryRole] || state.rbacConfig.roleCategories.default || "Staff"
    },

    hasPermission: (state) => (permissionName) => {
      const rolePermissionStore = useRolePermissionStore()
      const userPermissions = state.userData.permission
      const rolePermissions = state.userData.roles
        .flatMap(role => rolePermissionStore.getRolePermissions(role))
        .map(p => p.name)
      return [...userPermissions.map(p => p.name), ...rolePermissions].includes(permissionName)
    },

    userDisplayInfo: (state) => ({
      userId: state.userData.userId,
      username: state.userData.username,
      fullName: `${state.userData.firstName} ${state.userData.lastName}`.trim(),
      email: state.userData.email,
      phoneNumber: state.userData.phoneNumber,
      address: state.userData.address,
      roles: state.userData.roles,
      primaryRole: state.userData.roles[0] || null
    })
  },

  actions: {
    // Fetch RBAC configuration
    async fetchRbacConfig() {
      try {
        this.loading = true
        this.error = null
        const response = await axios.get("/api/v1/rbac-config", {
          headers: { "X-Tenant-ID": "agriculture" }
        })
        this.rbacConfig = response.data
        console.log("RBAC config fetched:", this.rbacConfig)
        return { success: true, data: this.rbacConfig }
      } catch (error) {
        this.error = error.response?.data?.message || error.message
        console.error("Error fetching RBAC config:", this.error)
        return { success: false, error: this.error }
      } finally {
        this.loading = false
      }
    },

    // Fetch authenticated user data
    async fetchUserData() {
      try {
        this.loading = true
        this.error = null
        const response = await axios.get("/api/v1/auth/me", {
          headers: { "X-Tenant-ID": "agriculture" }
        })
        this.userData = {
          userId: response.data.userId,
          username: response.data.username,
          firstName: response.data.firstName,
          lastName: response.data.lastName,
          email: response.data.email,
          phoneNumber: response.data.phoneNumber,
          address: response.data.address,
          roles: response.data.roles,
          permission: response.data.permission
        }
        // Initialize role/permission store and RBAC config
        const rolePermissionStore = useRolePermissionStore()
        await Promise.all([
          rolePermissionStore.initialize(),
          this.fetchRbacConfig()
        ])
        console.log("User data fetched:", this.userData)
        return { success: true, data: this.userData }
      } catch (error) {
        this.error = error.response?.data?.message || error.message
        console.error("Error fetching user data:", this.error)
        return { success: false, error: this.error }
      } finally {
        this.loading = false
      }
    },

    // Login user
    async login(form, setErrors, processing) {
      try {
        processing.value = true
        this.error = null
        await axios.post("/api/v1/auth/login", form.value, {
          headers: { "X-Tenant-ID": "agriculture" }
        })
        const result = await this.fetchUserData()
        if (!result.success) {
          setErrors.value = ["Failed to fetch user data."]
          return
        }
        const validRoles = Object.keys(this.rbacConfig.roleCategories).filter(r => r !== 'default')
        if (!this.userData.roles.some(role => validRoles.includes(role))) {
          console.error("Access denied: No valid roles", this.userData.roles)
          setErrors.value = ["Access denied: Valid staff role required."]
          await this.logout()
          return
        }
        const redirectPath = this.getRedirectPath()
        this.router.push(redirectPath)
      } catch (error) {
        this.error = error.response?.data?.message || error.message
        console.error("Login error:", this.error)
        if (error.response?.status === 422) {
          setErrors.value = Object.values(error.response.data.errors).flat()
        } else if (error.response?.status === 401) {
          setErrors.value = ["Invalid username or password."]
        } else {
          setErrors.value = ["Login failed. Please try again."]
        }
      } finally {
        processing.value = false
      }
    },

    getRedirectPath() {
      const primaryRole = this.userData.roles[0]
      return this.rbacConfig.roleRedirects[primaryRole] || this.rbacConfig.roleRedirects.default
    },

    hasRole(role) {
      return this.userData.roles.includes(role)
    },

    hasAnyRole(roles) {
      return Array.isArray(roles) && roles.some(role => this.userData.roles.includes(role))
    },

    hasAllRoles(roles) {
      return Array.isArray(roles) && roles.every(role => this.userData.roles.includes(role))
    },

    canAccess(feature) {
      const requiredPermissions = this.rbacConfig.featurePermissions[feature] || []
      return requiredPermissions.some(perm => this.hasPermission(perm))
    },

    async registerStaff(staffData) {
      try {
        this.loading = true
        this.error = null
        const response = await axios.post("/api/v1/staffs", staffData, {
          headers: { "X-Tenant-ID": "agriculture" }
        })
        console.log("Staff registered:", response.data)
        return { success: true, data: response.data }
      } catch (error) {
        this.error = error.response?.data?.message || error.message
        console.error("Error registering staff:", this.error)
        return { success: false, error: this.error }
      } finally {
        this.loading = false
      }
    },

    async forgotPassword(form, setStatus, setErrors, processing) {
      try {
        processing.value = true
        this.error = null
        const response = await axios.post("/api/v1/forgot-password", form.value, {
          headers: { "X-Tenant-ID": "agriculture" }
        })
        setStatus.value = response.data.status
        return { success: true }
      } catch (error) {
        this.error = error.response?.data?.message || error.message
        console.error("Error in forgot password:", this.error)
        if (error.response?.status === 422) {
          setErrors.value = Object.values(error.response.data.errors).flat()
        } else {
          setErrors.value = ["Failed to send password reset link."]
        }
        return { success: false, error: this.error }
      } finally {
        processing.value = false
      }
    },

    async resetPassword(form, setErrors, processing) {
      try {
        processing.value = true
        this.error = null
        const response = await axios.post("/api/v1/reset-password", form.value, {
          headers: { "X-Tenant-ID": "agriculture" }
        })
        this.router.push("/login?reset=" + btoa(response.data.status))
        return { success: true }
      } catch (error) {
        this.error = error.response?.data?.message || error.message
        console.error("Error resetting password:", this.error)
        if (error.response?.status === 422) {
          setErrors.value = Object.values(error.response.data.errors).flat()
        } else {
          setErrors.value = ["Failed to reset password."]
        }
        return { success: false, error: this.error }
      } finally {
        processing.value = false
      }
    },

    async resendEmailVerification(setStatus, processing) {
      try {
        processing.value = true
        this.error = null
        const response = await axios.post("/api/v1/email/verification-notification", {}, {
          headers: { "X-Tenant-ID": "agriculture" }
        })
        setStatus.value = response.data.status
        return { success: true }
      } catch (error) {
        this.error = error.response?.data?.message || error.message
        console.error("Error resending verification email:", this.error)
        return { success: false, error: this.error }
      } finally {
        processing.value = false
      }
    },

    async logout() {
      try {
        await axios.post("/api/v1/logout", {}, {
          headers: { "X-Tenant-ID": "agriculture" }
        })
        this.$reset()
        this.router.push({ name: "login" })
        return { success: true }
      } catch (error) {
        this.error = error.response?.data?.message || error.message
        console.error("Error logging out:", this.error)
        return { success: false, error: this.error }
      }
    },

    $reset() {
      this.userData = {
        userId: null,
        username: "",
        firstName: "",
        lastName: "",
        email: "",
        phoneNumber: "",
        address: "",
        roles: [],
        permission: []
      }
      this.rbacConfig = {
        roleCategories: {},
        roleRedirects: {},
        featurePermissions: {}
      }
      this.loading = false
      this.error = null
    }
  },

  persist: true
})

if (import.meta.hot) {
  import.meta.hot.accept(acceptHMRUpdate(useUserStore, import.meta.hot))
}