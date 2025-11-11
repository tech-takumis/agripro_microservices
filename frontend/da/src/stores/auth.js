import { defineStore } from 'pinia';
import axios from '@/lib/axios';
import { ref, computed } from 'vue';
import { router } from '@/router';

export const useAuthStore = defineStore('auth', () => {
    // State
    const userData = ref({
        roles: [],
        permissions: []
    });
    const loading = ref(false);
    const error = ref(null);
    const normalizedPermissions = ref(new Set());
    const normalizedRoles = ref(new Set());
    const isAuthenticated = ref(false);
    const initializationPromise = ref(null);

    const userFullName = computed(() =>
        userData.value?.firstName && userData.value?.lastName
            ? `${userData.value.firstName} ${userData.value.lastName}`
            : userData.value?.username
    );

    const userEmail = computed(() => userData.value?.email);
    const userRoles = computed(() => Array.from(normalizedRoles.value));
    const userPermissions = computed(() => Array.from(normalizedPermissions.value));
    const userPhoneNumber = computed(() => userData.value?.phoneNumber);
    const userId = computed(() => userData.value?.id);

    const defaultRoute = computed(() => {
        if (!userData.value?.roles || userData.value.roles.length === 0) return null;
        return userData.value.roles[0].defaultRoute;
    });


    const hasRole = (roleName) => {
        if (!roleName) return false;
        return userData.value?.roles?.some(role =>
            role.name.toUpperCase() === roleName.toUpperCase()
        );
    };

    const hasPermission = (permissionName) => {
        if (!permissionName) return true;
        if (Array.isArray(permissionName)) {
            return permissionName.some(name =>
                userData.value?.roles?.some(role =>
                    role.permissions.some(perm =>
                        perm.name.toUpperCase() === name.toUpperCase()
                    )
                )
            );
        }
        return userData.value?.roles?.some(role =>
            role.permissions.some(perm =>
                perm.name.toUpperCase() === permissionName.toUpperCase()
            )
        );
    };

    // Actions
    async function initialize(skipForGuest = false) {
        if (initializationPromise.value) {
            return initializationPromise.value;
        }

        if (skipForGuest) {
            isAuthenticated.value = false;
            return Promise.resolve({ success: true, message: 'Skipped for guest' });
        }

        initializationPromise.value = fetchCurrentUser()
            .finally(() => {
                initializationPromise.value = null;
            });

        return initializationPromise.value;
    }

    async function fetchCurrentUser() {
        try {
            const response = await axios.get('/api/v1/agriculture/auth/me');
            userData.value = response.data;
            console.log('Fetched current user:', userData.value);
            normalizeUserData();
            isAuthenticated.value = true;
            return { success: true, message: 'User data fetched successfully', data: response.data };
        } catch (error) {
            isAuthenticated.value = false;
            console.error('Failed to fetch current user:', error);
            const errorMessage = error.response?.data?.message || error.message || 'Failed to fetch user data';
            error.value = errorMessage;
            reset();
            return { success: false, message: errorMessage, error: error };
        }
    }

    function normalizeUserData() {
        normalizedRoles.value.clear();
        normalizedPermissions.value.clear();

        if (userData.value.roles) {
            userData.value.roles.forEach(role => {
                normalizedRoles.value.add(role.name.toUpperCase());
                role.permissions.forEach(permission => {
                    normalizedPermissions.value.add(permission.name.toUpperCase());
                });
            });
        }
    }

    async function login(credentials, setErrors, processing) {
        try {
            if (processing) processing.value = true;
            loading.value = true;
            error.value = null;

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
                // Store WebSocket token in localStorage
                if (response.data.webSocketToken) {
                    localStorage.setItem('webSocketToken', response.data.webSocketToken);
                }

                console.log('[Auth Store] Login successful, setting authentication state');
                isAuthenticated.value = true;
                // Update to use the new user object structure
                userData.value = response.data.user;
                normalizeUserData();

                if (userData.value && isAuthenticated.value) {
                    const route = defaultRoute.value;
                    console.log('[Auth Store] Default route:', route);
                    if (route) {
                        console.log('[Auth Store] Navigating to:', route);
                        await router.push(route);
                        return { success: true, message: 'Login successful! Redirecting to dashboard...' };
                    } else {
                        return { success: false, message: 'No default route found for user role' };
                    }
                } else {
                    return { success: false, message: 'Failed to authenticate user' };
                }
            }
        } catch (err) {
            console.error('Login error:', err);
            console.log('[Auth Store] Setting isAuthenticated to false after login error');
            const errorMessage = null;
            if(err.response.status === 503){
                errorMessage = 'Service is currently unavailable. Please try again later.';
                error.value = errorMessage;
                isAuthenticated.value = false;
                userData.value = { roles: [], permissions: [] };
                return { success: false, message: 'Service is currently unavailable. Please try again later.' };
            }
            errorMessage = err.response?.data?.message || err.message || 'Login failed. Please check your credentials.';
            error.value = errorMessage;
            isAuthenticated.value = false;
            userData.value = { roles: [], permissions: [] }; // Reset user data
            normalizedRoles.value.clear();
            normalizedPermissions.value.clear();
            if (setErrors) {
                setErrors.value = [errorMessage];
            }
            return { success: false, message: errorMessage };
        } finally {
            loading.value = false;
            if (processing) {
                processing.value = false;
            }
        }
    }

    async function register(userData, token) {
        try {
            loading.value = true;
            error.value = null;
            if (!token) {
                return { success: false, message: 'Invitation token is required for registration.' };
            }
            const response = await axios.post(`/api/v1/agriculture/auth/registration?token=${encodeURIComponent(token)}`,
                userData
            );
            if (response.status === 201 || response.status === 200) {
                // RegistrationResponse structure
                return {
                    success: response.data.success,
                    data: response.data,
                    message: response.data.message || 'Registration successful!',
                    error: response.data.error,
                    status: response.data.status,
                    username: response.data.username,
                    timestamp: response.data.timestamp
                };
            } else {
                return { success: false, message: 'Registration failed' };
            }
        } catch (err) {
            console.error('Registration error:', err);
            const errorMessage = err.response?.data?.message || err.message || 'Registration failed. Please try again.';
            error.value = errorMessage;
            return { success: false, message: errorMessage };
        } finally {
            loading.value = false;
        }
    }

    async function inviteStaff(email) {
        try {
            loading.value = true;
            error.value = null;
            if (!email) {
                return { success: false, message: 'Email is required for invitation.' };
            }
            const response = await axios.post(`/api/v1/agriculture/auth/invite?email=${encodeURIComponent(email)}`);
            if (response.status === 200) {
                return { success: true, message: response.data };
            } else {
                return { success: false, message: 'Invitation failed' };
            }
        } catch (err) {
            console.error('Invite error:', err);
            const errorMessage = err.response?.data?.message || err.message || 'Invitation failed. Please try again.';
            error.value = errorMessage;
            return { success: false, message: errorMessage };
        } finally {
            loading.value = false;
        }
    }

    async function logout() {
        try {
            await axios.post('/api/v1/agriculture/auth/logout', {}, {
                withCredentials: true
            });
        } catch (error) {
            console.error('Logout error:', error);
        } finally {
            // Remove webSocketToken from localStorage on logout
            localStorage.removeItem('webSocketToken');
            reset();
            await router.push({ name: 'login' });
        }
    }

    function reset() {
        userData.value = {};
        loading.value = false;
        error.value = null;
        normalizedPermissions.value.clear();
        normalizedRoles.value.clear();
        isAuthenticated.value = false;
        initializationPromise.value = null;
    }

    return {
        // State
        userData,
        loading,
        error,
        isAuthenticated,

        // Getters
        userFullName,
        userEmail,
        userRoles,
        userPermissions,
        userPhoneNumber,
        userId,
        defaultRoute,

        // Methods
        hasRole,
        hasPermission,
        initialize,
        login,
        register,
        inviteStaff,
        logout,
        reset
    };
});
