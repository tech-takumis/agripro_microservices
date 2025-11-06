import Axios from 'axios';

const axios = Axios.create({
    baseURL: import.meta.env.VITE_PUBLIC_BACKEND_URL || 'http://localhost:9001',
    headers: {
        'Accept': 'application/json',
        'X-Requested-With': 'XMLHttpRequest',
    },
    withCredentials: true // Ensure credentials (cookies) are sent with every request
});

// Request interceptor
axios.interceptors.request.use(
    config => {
        // You can add auth token here if needed
        // const token = localStorage.getItem('token');
        // if (token) {
        //     config.headers.Authorization = `Bearer ${token}`;
        // }
        return config;
    },
    error => {
        return Promise.reject(error);
    }
);

// Response interceptor to handle authentication errors
axios.interceptors.response.use(
    response => response,
    error => {
        // Handle authentication errors (401 Unauthorized)
        if (error.response?.status === 401) {
            // Only redirect if this is NOT a login request
            // Login requests should handle their own 401 errors
            const isLoginRequest = error.config?.url?.includes('/auth/login');
            if (!isLoginRequest) {
                console.log('[Axios] 401 detected, redirecting to login');
                window.location.href = '/';
            } else {
                console.log('[Axios] 401 from login request, letting login handler deal with it');
            }
        }
        // Handle 403 Forbidden errors
        if (error.response?.status === 403) {
            console.error('Access Denied:', error.response?.data?.message || 'You do not have permission to access this resource');
        }
        return Promise.reject(error);
    }
);

export default axios;
