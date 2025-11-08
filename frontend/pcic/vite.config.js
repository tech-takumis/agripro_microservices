import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
const path = require('path')
import VueDevTools from 'vite-plugin-vue-devtools'

// https://vitejs.dev/config/
export default defineConfig({
    server: {
        port: 3000,
    },
    resolve: {
        alias: {
            '@': path.resolve(__dirname, './src'),
        },
    },
    plugins: [
        VueDevTools(),,
        vue()
    ],
})
