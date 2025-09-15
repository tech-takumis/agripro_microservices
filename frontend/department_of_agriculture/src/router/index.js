import { defineStore } from "pinia"
import { ref, computed } from "vue"
import {
    REGIONAL_FIELD_OFFICES_NAVIGATION,
    REGIONAL_RICE_FOCAL_NAVIGATION,
    PROVINCIAL_COORDINATOR_NAVIGATION,
    MUNICIPAL_AGRICULTURISTS_NAVIGATION,
    RICE_SPECIALISTS_NAVIGATION,
    AGRICULTURAL_EXTENSION_NAVIGATION,
} from "../lib/constants.js"

export const useAgricultureStore = defineStore("agriculture", () => {
    // State
    const currentRole = ref(null)
    const userPermissions = ref([])
    const claims = ref([])
    const programs = ref([])
    const users = ref([])
    const loading = ref(false)
    const error = ref(null)

    // Getters
    const hasPermission = computed(() => {
        return (permission) => {
            return userPermissions.value.includes(permission)
        }
    })

    const canApproveClaimsRole = computed(() => {
        return currentRole.value?.permissions?.includes("CAN_APPROVE_CLAIM") || false
    })

    const canProcessClaimsRole = computed(() => {
        return currentRole.value?.permissions?.includes("CAN_PROCESS_CLAIM") || false
    })

    const canManageFinanceRole = computed(() => {
        return currentRole.value?.permissions?.includes("CAN_MANAGE_FINANCE") || false
    })

    const canManageRolesRole = computed(() => {
        return currentRole.value?.permissions?.includes("CAN_MANAGE_ROLES") || false
    })

    const roleNavigation = computed(() => {
        if (!currentRole.value) return []

        const roleSlug = currentRole.value.slug
        switch (roleSlug) {
            case "regional-field-offices":
                return REGIONAL_FIELD_OFFICES_NAVIGATION
            case "regional-rice-focal-person":
                return REGIONAL_RICE_FOCAL_NAVIGATION
            case "provincial-rice-program-coordinator":
                return PROVINCIAL_COORDINATOR_NAVIGATION
            case "municipal-agriculturists":
                return MUNICIPAL_AGRICULTURISTS_NAVIGATION
            case "rice-specialists":
                return RICE_SPECIALISTS_NAVIGATION
            case "agricultural-extension-workers":
                return AGRICULTURAL_EXTENSION_NAVIGATION
            default:
                return []
        }
    })

    // Actions
    const setCurrentRole = (role) => {
        currentRole.value = role
        userPermissions.value = role?.permissions || []
    }

    const fetchClaims = async () => {
        loading.value = true
        error.value = null
        try {
            // Mock API call
            await new Promise((resolve) => setTimeout(resolve, 1000))
            claims.value = [
                {
                    id: "1",
                    claimNumber: "CLM-2024-001",
                    farmerName: "Juan Dela Cruz",
                    cropType: "Rice",
                    area: "2.5 hectares",
                    status: "Pending Review",
                    dateSubmitted: "2024-03-15",
                    amount: 125000,
                },
                {
                    id: "2",
                    claimNumber: "CLM-2024-002",
                    farmerName: "Maria Santos",
                    cropType: "Corn",
                    area: "1.8 hectares",
                    status: "Under Processing",
                    dateSubmitted: "2024-03-14",
                    amount: 89000,
                },
            ]
        } catch (err) {
            error.value = err.message
        } finally {
            loading.value = false
        }
    }

    const fetchPrograms = async () => {
        loading.value = true
        error.value = null
        try {
            await new Promise((resolve) => setTimeout(resolve, 800))
            programs.value = [
                {
                    id: "1",
                    name: "Rice Insurance Program",
                    description: "Comprehensive rice crop insurance",
                    coverage: "₱50,000 per hectare",
                    participants: 1250,
                    status: "Active",
                },
                {
                    id: "2",
                    name: "Corn Protection Scheme",
                    description: "Corn crop protection program",
                    coverage: "₱35,000 per hectare",
                    participants: 890,
                    status: "Active",
                },
            ]
        } catch (err) {
            error.value = err.message
        } finally {
            loading.value = false
        }
    }

    const fetchUsers = async () => {
        loading.value = true
        error.value = null
        try {
            await new Promise((resolve) => setTimeout(resolve, 600))
            users.value = [
                {
                    id: "1",
                    name: "John Smith",
                    email: "john.smith@pcic.gov.ph",
                    role: "Regional Field Officer",
                    status: "Active",
                    lastLogin: "2024-03-15 09:30:00",
                },
                {
                    id: "2",
                    name: "Jane Doe",
                    email: "jane.doe@pcic.gov.ph",
                    role: "Rice Specialist",
                    status: "Active",
                    lastLogin: "2024-03-15 08:45:00",
                },
            ]
        } catch (err) {
            error.value = err.message
        } finally {
            loading.value = false
        }
    }

    const approveClaim = async (claimId) => {
        loading.value = true
        try {
            await new Promise((resolve) => setTimeout(resolve, 1000))
            const claimIndex = claims.value.findIndex((c) => c.id === claimId)
            if (claimIndex !== -1) {
                claims.value[claimIndex].status = "Approved"
            }
        } catch (err) {
            error.value = err.message
        } finally {
            loading.value = false
        }
    }

    const processClaim = async (claimId) => {
        loading.value = true
        try {
            await new Promise((resolve) => setTimeout(resolve, 1000))
            const claimIndex = claims.value.findIndex((c) => c.id === claimId)
            if (claimIndex !== -1) {
                claims.value[claimIndex].status = "Processing"
            }
        } catch (err) {
            error.value = err.message
        } finally {
            loading.value = false
        }
    }

    return {
        // State
        currentRole,
        userPermissions,
        claims,
        programs,
        users,
        loading,
        error,

        // Getters
        hasPermission,
        canApproveClaimsRole,
        canProcessClaimsRole,
        canManageFinanceRole,
        canManageRolesRole,
        roleNavigation,

        // Actions
        setCurrentRole,
        fetchClaims,
        fetchPrograms,
        fetchUsers,
        approveClaim,
        processClaim,
    }
})
