import { defineStore } from 'pinia';
import axios from '@/lib/axios';
import { useRoleStore } from './role';
import { usePermissionStore } from './permission';

export const useAuthStore = defineStore('auth', {
    state: () => ({
        userData: {},
        loading: false,
        error: null,
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
        userRoles: (state) => state.userData?.roles || [],
        userPermissions: (state) => state.userData?.permission || [],
        userPrimaryRole: (state) => {
            const roles = state.userData?.roles || [];
            if (roles.length === 0) return null;

            // Get role store to access role hierarchy
            const roleStore = useRoleStore();
            const roleHierarchy = roleStore.roles.reduce((acc, role) => {
                acc[role.name] = role.id; // Using ID as priority for now, adjust if needed
                return acc;
            }, {});

            return roles.reduce((highest, role) => {
                const currentPriority = roleHierarchy[role] || 0;
                const highestPriority = roleHierarchy[highest] || 0;
                return currentPriority > highestPriority ? role : highest;
            }, roles[0]);
        },
        userPhoneNumber: (state) => state.userData?.phoneNumber || "",
        userAddress: (state) => state.userData?.address || "",
        userId: (state) => state.userData?.userId || "",

        // Dynamic role-specific checks
        hasRole: (state) => (roleName) => (state.userData?.roles).includes(roleName),
        checkRole: (state) => (roleName) => (state.userData?.roles).includes(roleName),

        // Permission-specific checks
        // Permission-specific checks
        hasPermission: (state) => (permissionName) => (state.userData?.permission || []).includes(permissionName),
        checkPermission: (state) => (permissionName) => (state.userData?.permission || []).includes(permissionName),

        // Check if user is valid staff (has at least one valid role from backend)
        isValidStaff: (state) => {
            const userRoles = state.userData?.roles || [];
            const roleStore = useRoleStore();
            const validRoles = roleStore.roles.map(role => role.name);
            console.log("Checking roles:", userRoles, "Valid roles:", validRoles);
            return userRoles.some((role) => validRoles.includes(role));
        },

        // Get user display info
        userDisplayInfo: (state) => {
            if (!state.userData || Object.keys(state.userData).length === 0) return null;

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
            };
        },

        // Alias for compatibility
        currentUser: (state) => state.userData,
        isLoading: (state) => state.loading,
        getError: (state) => state.error,
        isAdmin: (state) => (state.userData?.roles || []).includes('ADMIN'),

        // Alias for compatibility
        fullName: (state) => state.userData?.firstName && state.userData?.lastName
            ? `${state.userData.firstName} ${state.userData.lastName}`.trim()
            : state.userData?.username || ""

        // Note: hasAnyRole and hasAllRoles are not directly used in the provided store
        // but we'll keep them for backward compatibility
    },

    actions: {
        // Initialize the store with roles and permissions
        async initialize() {
            if (this.isInitialized) return;

            try {
                // Try to fetch user data first to check if authenticated
                try {
                    await this.fetchUserData();
                    
                    // Only fetch roles and permissions if user is authenticated
                    if (this.isAuthenticate) {
                        const roleStore = useRoleStore();
                        const permissionStore = usePermissionStore();

                        // Fetch roles and permissions in parallel
                        await Promise.all([
                            roleStore.fetchRoles(),
                            permissionStore.fetchPermissions()
                        ]);
                        
                        console.log('User store initialized with roles and permissions');
                    }
                    
                    this.isInitialized = true;
                    return { success: true, user: this.userData };
                } catch (error) {
                    // Not authenticated, but store is still initialized
                    this.isInitialized = true;
                    return { success: true, user: null };
                }
            } catch (error) {
                console.error('Failed to initialize user store:', error);
                this.isInitialized = true; // Mark as initialized even on error to prevent loops
                return { success: false, error: error.message };
            }
        },
        // Fetch current user data
        async fetchUserData() {
            this.loading = true;
            this.error = null;

            try {
                const response = await axios.get('/api/v1/auth/me');

                this.userData = response.data;
                console.log('User data fetched:', this.userData);
                return this.userData;
            } catch (error) {
                console.error('Failed to fetch user data:', error);
                this.userData = {};
                throw error;
            } finally {
                this.loading = false;
            }
        },

        // Alias for fetchUserData for compatibility
        async getData() {
            return this.fetchUserData();
        },

        // Get redirect path based on user's highest priority role
        getRedirectPath() {
            const roles = this.userData?.roles || [];
            if (!roles || roles.length === 0) return { name: 'login' };

            // Define role-based redirect paths
            const roleRoutes = {
                'ADMIN': { name: 'admin-dashboard' },
                'Municipal Agriculturists': { name: 'municipal-agriculturist-dashboard' },
                'Agricultural Extension Workers': { name: 'extension-worker-dashboard' },
            };

            // Get the highest priority role (first one in the array by default)
            const primaryRole = roles[0];
            console.log('Primary role for redirect:', primaryRole, 'Available routes:', Object.keys(roleRoutes));
            
            return roleRoutes[primaryRole] || { name: 'login' };
        },

        // Login user
        async login(credentials, setErrors, processing) {
            try {
                if (processing) processing.value = true;
                this.loading = true;
                this.error = null;

                // First login
                const loginResponse = await axios.post('/api/v1/auth/login',
                    credentials.value,
                    {
                        headers: {
                            'Content-Type': 'application/json',
                            'Accept': 'application/json',
                            'X-Tenant-ID': 'agriculture'
                        },
                        withCredentials: true,
                    }
                );

                if (loginResponse.status === 200) {
                    // Fetch the authenticated user
                    const userResponse = await axios.get('/api/v1/auth/me');

                    if (userResponse.status === 200) {
                        this.userData = userResponse.data;
                        const redirectPath = this.getRedirectPath();
                        console.log('Login successful, redirect path:', redirectPath); // Debug log

                        return {
                            success: true,
                            data: userResponse.data,
                            redirectPath: redirectPath
                        };
                    }
                }
            } catch (error) {
                console.error('Login error details:', {
                    message: error.message,
                    response: error.response?.data,
                    status: error.response?.status,
                    headers: error.response?.headers
                });

                const errorMessage = error.response?.data?.message || 'Login failed. Please check your credentials.';
                this.error = errorMessage;

                if (setErrors) {
                    setErrors.value = [errorMessage];
                }

                return {
                    success: false,
                    error: errorMessage,
                    status: error.response?.status,
                    data: error.response?.data
                };
            } finally {
                this.loading = false;
                if (processing) {
                    processing.value = false;
                }
            }
        },

        // Register new user
        async register(userData) {
            this.loading = true;
            this.error = null;

            try {
                const response = await axios.post('/api/v1/auth/register', userData);

                return { success: true, data: response.data };
            } catch (error) {
                this.error = error.response?.data?.message || 'Registration failed. Please try again.';
                return { success: false, error: this.error };
            } finally {
                this.loading = false;
            }
        },

        // Logout user
        async logout() {
            try {
                // Call the logout endpoint to clear the server-side session
                await axios.post('/api/v1/auth/logout');
            } catch (error) {
                console.error('Logout error:', error);
            } finally {
                // Always clear the user data regardless of logout API success
                this.userData = {};
                this.error = null;
                this.loading = false;
                
                // Use window.location to redirect to avoid router issues
                window.location.href = '/';
            }
        },

        // Update user profile
        async updateProfile(profileData) {
            this.loading = true;
            this.error = null;

            try {
                const response = await axios.put(`/api/v1/users/${this.userId}`, profileData);
                this.user = { ...this.user, ...response.data };
                return { success: true, user: this.user };
            } catch (error) {
                this.error = error.response?.data?.message || 'Failed to update profile';
                return { success: false, error: this.error };
            } finally {
                this.loading = false;
            }
        },

        // Change password
        async changePassword(currentPassword, newPassword) {
            this.loading = true;
            this.error = null;

            try {
                await axios.post('/api/v1/auth/change-password', {
                    currentPassword,
                    newPassword
                });
                return { success: true };
            } catch (error) {
                this.error = error.response?.data?.message || 'Failed to change password';
                return { success: false, error: this.error };
            } finally {
                this.loading = false;
            }
        },

        // Request password reset
        async requestPasswordReset(email) {
            this.loading = true;
            this.error = null;

            try {
                await axios.post('/api/v1/auth/forgot-password', { email });
                return { success: true };
            } catch (error) {
                this.error = error.response?.data?.message || 'Failed to request password reset';
                return { success: false, error: this.error };
            } finally {
                this.loading = false;
            }
        },

        // Reset password with token
        async resetPassword(token, password, passwordConfirmation) {
            this.loading = true;
            this.error = null;

            try {
                await axios.post('/api/v1/auth/reset-password', {
                    token,
                    password,
                    password_confirmation: passwordConfirmation
                });
                return { success: true };
            } catch (error) {
                this.error = error.response?.data?.message || 'Failed to reset password';
                return { success: false, error: this.error };
            } finally {
                this.loading = false;
            }
        },

        // Check if user has a specific permission
        can(permission) {
            if (!this.user?.permissions) return false;
            return this.user.permissions.includes(permission);
        },

        // Check if user has any of the given permissions
        canAny(permissions) {
            if (!this.user?.permissions) return false;
            return permissions.some(permission => this.user.permissions.includes(permission));
        },

        // Check if user has all of the given permissions
        canAll(permissions) {
            if (!this.user?.permissions) return false;
            return permissions.every(permission => this.user.permissions.includes(permission));
        },

        // Clear error state
        clearError() {
            this.error = null;
        },

        // Reset store state
        $reset() {
            this.userData = {};
            this.loading = false;
            this.error = null;
            this.isInitialized = false;
        }
    }

});



