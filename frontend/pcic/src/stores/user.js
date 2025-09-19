import axios from "@/lib/axios"
import { defineStore, acceptHMRUpdate } from "pinia"

export const useUserStore = defineStore("users", {
  state: () => ({
    userData: {},
    availableRoles: [],
    availablePermissions: [],
    roleHierarchy: {},
    permissionMap: {},
    isInitialized: false,
  }),
  
  getters: {
    isAuthenticate: (state) => Object.values(state.userData).length > 0,
    hasVerified: (state) =>
      Object.keys(state.userData).length > 0 ? state.userData.email_verified_at !== null : false,

    // User basic info
    userFullName: (state) => state.userData?.firstName && state.userData?.lastName 
      ? `${state.userData.firstName} ${state.userData.lastName}` 
      : state.userData?.username || "",
    userEmail: (state) => state.userData?.email || "",
    userRoles: (state) => state.userData?.roles || [], // Returns array of role names
    userPermissions: (state) => state.userData?.permission || [], // Returns array of permission names
    userPrimaryRole: (state) => {
      // Get the highest priority role for display purposes
      const roles = state.userData?.roles || []
      if (roles.length === 0) return null
      
      // Use dynamic role hierarchy from state
      return roles.reduce((highest, role) => {
        const currentPriority = state.roleHierarchy[role] || 0
        const highestPriority = state.roleHierarchy[highest] || 0
        return currentPriority > highestPriority ? role : highest
      }, roles[0])
    },
    userPhoneNumber: (state) => state.userData?.phoneNumber || "",
    userAddress: (state) => state.userData?.address || "",
    userId: (state) => state.userData?.userId || "",

    // Dynamic role-specific checks
    checkRole: (state) => (roleName) => (state.userData?.roles || []).includes(roleName),
    
    // Permission-specific checks
    checkPermission: (state) => (permissionName) => (state.userData?.permission || []).includes(permissionName),

    // Dynamic permission level checks based on role hierarchy
    hasPermissionLevel: (state) => (requiredLevel) => {
      const userRoles = state.userData?.roles || []
      if (userRoles.length === 0) return false
      
      const userMaxLevel = Math.max(...userRoles.map(role => state.roleHierarchy[role] || 0))
      return userMaxLevel >= requiredLevel
    },

    // Check if user is valid PCIC staff (has at least one valid role from backend)
    isValidStaff: (state) => {
      const userRoles = state.userData?.roles || []
      const validRoles = state.availableRoles.map(role => role.name)
      console.log("Checking roles:", userRoles, "Valid roles:", validRoles)
      return userRoles.some((role) => validRoles.includes(role))
    },

    // Get user display info
    userDisplayInfo: (state) => {
      if (!state.userData || Object.keys(state.userData).length === 0) return null

      return {
        userId: state.userData.userId,
        username: state.userData.username,
        name: state.userData.firstName && state.userData.lastName 
          ? `${state.userData.firstName} ${state.userData.lastName}` 
          : state.userData.username,
        firstName: state.userData.firstName,
        lastName: state.userData.lastName,
        email: state.userData.email,
        phoneNumber: state.userData.phoneNumber,
        address: state.userData.address,
        roles: state.userData.roles,
        permissions: state.userData.permission,
        primaryRole: state.userData.roles?.[0] || null,
      }
    },

    // Get available roles from backend
    getAvailableRoles: (state) => state.availableRoles,
    
    // Get available permissions from backend
    getAvailablePermissions: (state) => state.availablePermissions,
    
    // Get role hierarchy from backend
    getRoleHierarchy: (state) => state.roleHierarchy,
  },

  actions: {
    // Fetch roles and permissions from backend
    async fetchRolesAndPermissions() {
      try {
        const [rolesResponse, permissionsResponse] = await Promise.all([
          axios.get("/api/v1/roles"),
          axios.get("/api/v1/permissions")
        ])

        this.availableRoles = rolesResponse.data
        this.availablePermissions = permissionsResponse.data

        // Build role hierarchy based on available roles
        this.buildRoleHierarchy()
        
        // Build permission map for easy lookup
        this.buildPermissionMap()

        console.log("Roles and permissions loaded:", {
          roles: this.availableRoles,
          permissions: this.availablePermissions
        })
      } catch (error) {
        console.error("Error fetching roles and permissions:", error)
        throw error
      }
    },

    // Build role hierarchy dynamically
    buildRoleHierarchy() {
      const hierarchy = {}
      this.availableRoles.forEach((role, index) => {
        // Assign priority based on order in array (can be customized)
        hierarchy[role.name] = this.availableRoles.length - index
      })
      this.roleHierarchy = hierarchy
    },

    // Build permission map for easy lookup
    buildPermissionMap() {
      const map = {}
      this.availablePermissions.forEach(permission => {
        map[permission.name] = permission
      })
      this.permissionMap = map
    },

    async getData() {
      try {
        // Only fetch roles and permissions if we don't have them
        if (this.availableRoles.length === 0) {
          await this.fetchRolesAndPermissions()
        }

        // Then fetch user data
        const response = await axios.get("/api/v1/auth/me", {
          headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json',
            'X-Tenant-ID': 'pcic'
          },
          withCredentials: true,
        })
        this.userData = response.data

        console.log("User data received:", this.userData)

        // Validate that user has at least one valid role from backend
        const validRoles = this.availableRoles.map(role => role.name)
        const userRoles = this.userData?.roles || []
        const hasValidRole = userRoles.some((role) => validRoles.includes(role))

        if (!hasValidRole) {
          console.error("No valid roles detected:", userRoles)
          throw new Error("Access denied: Valid PCIC staff role required")
        }

        console.log("Role validation passed for roles:", userRoles)
      } catch (error) {
        if (error.response?.status === 409) {
          this.router.push("/verify-email")
        } else {
          console.error("getData error:", error)
          throw error
        }
      }
    },

    async login(form, setErrors, processing) {
      try {
        processing.value = true

        // First login
        await axios.post("/api/v1/auth/login",
            form.value,{
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
                'X-Tenant-ID': 'pcic'
            },
            withCredentials: true,
            })

        // Fetch roles and permissions first
        await this.fetchRolesAndPermissions()

        // Then fetch the authenticated user
        const response = await axios.get("/api/v1/auth/me",{
          headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json',
            'X-Tenant-ID': 'pcic'
          },
          withCredentials: true,
        })

        if (response.status === 200) {
          this.userData = response.data

          console.log("Login successful, user data:", this.userData)

          // Validate PCIC staff access using dynamic roles
          const validRoles = this.availableRoles.map(role => role.name)
          const userRoles = this.userData?.roles || []
          const hasValidRole = userRoles.some((role) => validRoles.includes(role))

          if (!hasValidRole) {
            console.error("Access denied for roles:", userRoles)
            setErrors.value = ["Access denied: This portal is for PCIC staff members only."]
            await this.logout()
            return
          }

          console.log("Access granted for roles:", userRoles)

          // Role-based redirect
          const redirectPath = this.getRedirectPath()
          console.log("Redirecting to:", redirectPath)
          this.router.push(redirectPath)
        }
      } catch (error) {
        console.error("Login error:", error)
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
    },

    // Get redirect path based on user's highest priority role (dynamic)
    getRedirectPath() {
      const roles = this.userData?.roles || []
      if (roles.length === 0) return { name: "login" }

      // Get the highest priority role based on dynamic hierarchy
      const highestRole = roles.reduce((highest, role) => {
        const currentPriority = this.roleHierarchy[role] || 0
        const highestPriority = this.roleHierarchy[highest] || 0
        return currentPriority > highestPriority ? role : highest
      }, roles[0])

      // Dynamic route mapping based on role name
      const routeMap = {
        "ADMIN": { name: "admin-dashboard" },
        "MANAGEMENT": { name: "management-dashboard" },
        "UNDERWRITER": { name: "underwriter-dashboard" },
        "CLAIM_PROCESSOR": { name: "claims-processor-dashboard" },
        "TELLER": { name: "teller-dashboard" }
      }

      return routeMap[highestRole] || { name: "login" }
    },

    // Check if user has specific role
    hasRole(role) {
      const userRoles = this.userData?.roles || []
      return userRoles.includes(role)
    },

    // Check if user has any of the specified roles
    hasAnyRole(roles) {
      if (!Array.isArray(roles)) return false
      const userRoles = this.userData?.roles || []
      return roles.some((role) => userRoles.includes(role))
    },

    // Check if user has all of the specified roles
    hasAllRoles(roles) {
      if (!Array.isArray(roles)) return false
      const userRoles = this.userData?.roles || []
      return roles.every((role) => userRoles.includes(role))
    },

    // Check if user has specific permission
    hasPermission(permission) {
      const userPermissions = this.userData?.permission || []
      return userPermissions.includes(permission)
    },

    // Check if user has any of the specified permissions
    hasAnyPermission(permissions) {
      if (!Array.isArray(permissions)) return false
      const userPermissions = this.userData?.permission || []
      return permissions.some((permission) => userPermissions.includes(permission))
    },

    // Check if user has all of the specified permissions
    hasAllPermissions(permissions) {
      if (!Array.isArray(permissions)) return false
      const userPermissions = this.userData?.permission || []
      return permissions.every((permission) => userPermissions.includes(permission))
    },

    // Get user's permissions from their roles (from backend role data)
    getRolePermissions() {
      const userRoles = this.userData?.roles || []
      const allPermissions = new Set()

      userRoles.forEach(roleName => {
        const role = this.availableRoles.find(r => r.name === roleName)
        if (role && role.permissions) {
          role.permissions.forEach(permission => {
            allPermissions.add(permission.name)
          })
        }
      })

      return Array.from(allPermissions)
    },

    // Get user's combined responsibilities from all roles (dynamic from backend)
    getAllResponsibilities() {
      const userRoles = this.userData?.roles || []
      const allResponsibilities = new Set()

      userRoles.forEach(roleName => {
        const role = this.availableRoles.find(r => r.name === roleName)
        if (role && role.permissions) {
          role.permissions.forEach(permission => {
            allResponsibilities.add(permission.description || permission.name)
          })
        }
      })

      return Array.from(allResponsibilities)
    },

    async registerStaff(staffData) {
      try {
        console.log("Registering staff with data:", staffData)
        
        // Validate that assigned roles exist in available roles
        const validRoles = this.availableRoles.map(role => role.name)
        const assignedRoles = staffData.roles || []
        const invalidRoles = assignedRoles.filter(role => !validRoles.includes(role))
        
        if (invalidRoles.length > 0) {
          throw new Error(`Invalid roles assigned: ${invalidRoles.join(', ')}`)
        }
        
        const response = await axios.post("/api/v1/auth/register", staffData,{
          headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json',
            'X-Tenant-ID': 'pcic'
          },
          withCredentials: true,
        })
        console.log("PCIC Tenant Staff registered successfully:", response.data)
        return { success: true, data: response.data }
      } catch (error) {
        console.error("Error registering staff:", error.response?.data || error.message)
        return { success: false, error: error.response?.data || error.message }
      }
    },

    async forgotPassword(form, setStatus, setErrors, processing) {
      processing.value = true

      axios
        .post("/forgot-password", form.value)
        .then((response) => {
          setStatus.value = response.data.status
          processing.value = false
        })
        .catch((error) => {
          if (error.response.status !== 422) throw error

          setErrors.value = Object.values(error.response.data.errors).flat()
          processing.value = false
        })
    },

    async resetPassword(form, setErrors, processing) {
      processing.value = true

      axios
        .post("/reset-password", form.value)
        .then((response) => {
          this.router.push("/login?reset=" + btoa(response.data.status))
          processing.value = false
        })
        .catch((error) => {
          if (error.response.status !== 422) throw error

          setErrors.value = Object.values(error.response.data.errors).flat()
          processing.value = false
        })
    },

    resendEmailVerification(setStatus, processing) {
      processing.value = true

      axios.post("/email/verification-notification").then((response) => {
        setStatus.value = response.data.status
        processing.value = false
      })
    },

    async logout() {
      await axios
        .post("/api/v1/auth/logout")
        .then(() => {
          this.$reset()
          this.router.push({ name: "login" })
        })
        .catch((error) => {
          if (error.response.status !== 422) throw error
        })
    },

    // Initialize store with roles and permissions (call this on app startup)
    async initialize() {
      try {
        await this.fetchRolesAndPermissions()
        console.log("User store initialized with dynamic roles and permissions")
      } catch (error) {
        console.error("Failed to initialize user store:", error)
        throw error
      }
    },

    // Check authentication status on app startup
    async checkAuthStatus() {
      try {
        // If we have user data in localStorage, verify it's still valid
        if (this.isAuthenticate) {
          console.log("Found stored user data, verifying with server...")
          try {
            await this.getData()
            console.log("Authentication verified successfully")
            this.isInitialized = true
            return true
          } catch (error) {
            console.log("Stored authentication invalid, clearing data")
            this.$reset()
            return false
          }
        } else {
          console.log("No stored authentication found")
          this.isInitialized = true
          return false
        }
      } catch (error) {
        console.error("Error checking auth status:", error)
        this.isInitialized = true
        return false
      }
    },

    // Reset store state
    $reset() {
      this.userData = {}
      this.availableRoles = []
      this.availablePermissions = []
      this.roleHierarchy = {}
      this.permissionMap = {}
      this.isInitialized = false
    },
  },
})

if (import.meta.hot) {
  import.meta.hot.accept(acceptHMRUpdate(useUserStore, import.meta.hot))
}
