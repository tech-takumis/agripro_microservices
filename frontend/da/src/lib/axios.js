import Axios from 'axios';

const axios = Axios.create({
    baseURL: import.meta.env.VITE_PUBLIC_BACKEND_URL || 'http://localhost:9001',
    headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
        'X-Requested-With': 'XMLHttpRequest',
    },
    withCredentials: true
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

// Response interceptor
axios.interceptors.response.use(
    response => response,
    error => {
        // Handle 403 Forbidden errors
        if (error.response?.status === 403) {
            console.error('Access Denied:', error.response?.data?.message || 'You do not have permission to access this resource');
        }
        return Promise.reject(error);
    }
);

export default axios;
