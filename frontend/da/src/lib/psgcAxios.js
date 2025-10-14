import axios from 'axios'

const psgcAxios = axios.create({
    baseURL: 'https://psgc.gitlab.io/api',
    headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
    }
})

// Add response interceptor to handle the data structure
psgcAxios.interceptors.response.use(
    (response) => {
        // Extract the data from the response
        return response.data;
    },
    (error) => {
        // Handle network errors and provide more descriptive messages
        if (error.code === 'ERR_NETWORK') {
            error.message = 'Unable to connect to PSGC service. Please check your internet connection or try again later.';
        }
        return Promise.reject(error);
    }
);

export default psgcAxios;
