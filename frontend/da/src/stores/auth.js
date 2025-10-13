import { defineStore } from 'pinia';
import axios from '@/lib/axios';
import { useRouter } from 'vue-router';

export const useAuthStore = defineStore('auth', {
    state: () => ({
        userData: {},
        loading: false,
        error: null,
        router: null,
        normalizedPermissions: new Set(),
        normalizedRoles: new Set(),
        isAuthenticated: false,
        initializationPromise: null
    }),

    getters: {
        userFullName: (state) => state.userData?.firstName && state.userData?.lastName
            ? `${state.userData.firstName} ${state.userData.lastName}`
            : state.userData?.username || "",
        userEmail: (state) => state.userData?.email || "",
        userRoles: (state) => Array.from(state.normalizedRoles),
        userPermissions: (state) => Array.from(state.normalizedPermissions),
        userPhoneNumber: (state) => state.userData?.phoneNumber || "",
        userId: (state) => state.userData?.id || "",

        // Permission and role checks
        hasRole: (state) => (roleName) => {
            if (!roleName) return false;
            return state.userData.roles?.some(role => role.name.toUpperCase() === roleName.toUpperCase());
        },

        hasPermission: (state) => (permissionName) => {
            if (!permissionName) return true;
            return state.userData.roles?.some(role =>
                role.permissions.some(perm => perm.name.toUpperCase() === permissionName.toUpperCase())
            );
        },

        // Get default route from primary role
        defaultRoute: (state) => {
            if (!state.userData.roles || state.userData.roles.length === 0) return null;
            return state.userData.roles[0].defaultRoute;
        }
    },

    actions: {
        initializeRouter() {
            if (!this.router) {
                this.router = useRouter();
            }
        },

        // Initialize auth state by fetching current user, with option to skip for guest routes
        async initialize(skipForGuest = false) {
            // If already initializing, return the existing promise
            if (this.initializationPromise) {
                return this.initializationPromise;
            }

            // Skip initialization for guest routes if specified
            if (skipForGuest) {
                this.isAuthenticated = false;
                return Promise.resolve();
            }

            this.initializationPromise = this.fetchCurrentUser()
                .finally(() => {
                    this.initializationPromise = null;
                });

            return this.initializationPromise;
        },

        // Fetch current authenticated user
        async fetchCurrentUser() {
            try {
                const response = await axios.get('/api/v1/agriculture/auth/me');
                this.userData = response.data;
                this.normalizeUserData();
                this.isAuthenticated = true;
                return response.data;
            } catch (error) {
                console.error('Failed to fetch current user:', error);
                this.$reset();
                throw error;
            }
        },

        // Normalize user roles and permissions
        normalizeUserData() {
            this.normalizedRoles.clear();
            this.normalizedPermissions.clear();

            if (this.userData.roles) {
                this.userData.roles.forEach(role => {
                    this.normalizedRoles.add(role.name.toUpperCase());
                    role.permissions.forEach(permission => {
                        this.normalizedPermissions.add(permission.name.toUpperCase());
                    });
                });
            }
        },

        // Login user
        async login(credentials, setErrors, processing) {
            this.initializeRouter();
            try {
                if (processing) processing.value = true;
                this.loading = true;
                this.error = null;

                const response = await axios.post('/api/v1/agriculture/auth/login',
                    credentials.value,
                    {
                        headers: {
                            'Content-Type': 'application/json',
                            'Accept': 'application/json'
                        },
                        withCredentials: true,
                    }
                );

                if (response.status === 200) {
                    // After successful login, fetch user data
                    const userData = await this.fetchCurrentUser();

                    if (userData && this.isAuthenticated) {
                        const defaultRoute = this.defaultRoute;
                        if (defaultRoute) {
                            await this.router.push(defaultRoute);
                        } else {
                            throw new Error('No default route found for user role');
                        }
                        return { success: true };
                    } else {
                        throw new Error('Failed to authenticate user');
                    }
                }
            } catch (error) {
                console.error('Login error:', error);
                const errorMessage = error.response?.data?.message || 'Login failed. Please check your credentials.';
                this.error = errorMessage;
                if (setErrors) {
                    setErrors.value = [errorMessage];
                }
                return { success: false, error: errorMessage };
            } finally {
                this.loading = false;
                if (processing) {
                    processing.value = false;
                }
            }
        },

        // Logout user
        async logout() {
            try {
                // Initialize router first
                this.initializeRouter();

                // Perform logout request
                await axios.post('/api/v1/agriculture/auth/logout', {}, {
                    withCredentials: true
                });
            } catch (error) {
                console.error('Logout error:', error);
            } finally {
                // Reset the store state
                this.$reset();

                // Re-initialize router after reset since it was cleared
                this.initializeRouter();

                // Navigate to login page
                if (this.router) {
                    await this.router.push({ name: 'login' });
                } else {
                    // Fallback if router initialization fails
                    window.location.href = '/';
                }
            }
        },

        // Reset store state
        $reset() {
            this.userData = {};
            this.loading = false;
            this.error = null;
            this.normalizedPermissions.clear();
            this.normalizedRoles.clear();
            this.isAuthenticated = false;
            this.initializationPromise = null;
            // Don't reset router here anymore
        },
    }
});
