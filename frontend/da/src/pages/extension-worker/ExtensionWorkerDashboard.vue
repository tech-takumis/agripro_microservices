<script setup>
import AuthenticatedLayout from '@/layouts/AuthenticatedLayout.vue'
import {AGRICULTURAL_EXTENSION_WORKER_NAVIGATION} from "@/lib/constants"
import {useAuthStore} from '@/stores/auth'
import { computed } from 'vue'

const navigation = AGRICULTURAL_EXTENSION_WORKER_NAVIGATION
const authStore = useAuthStore()

const currentDate = computed(() => {
    return new Date().toLocaleDateString('en-US', {
        weekday: 'long',
        year: 'numeric',
        month: 'long',
        day: 'numeric'
    })
})
</script>

<template>
   <AuthenticatedLayout
       :navigation="navigation"
       role-title="MUNICIPAL AGRICULTURIST PORTAL"
       page-title="Dashboard"
   >
       <template #header>
           <div class="flex items-center justify-between">
               <div>
                   <h1 class="text-2xl font-bold text-gray-900"> Dashboard</h1>
               </div>
               <div class="flex items-center space-x-2">
                   <span class="text-sm text-gray-500">{{ currentDate }}</span>
                   <div class="h-4 w-px bg-gray-300"></div>
                   <span class="text-sm font-medium text-purple-600">{{ authStore.userAddress }}</span>
               </div>
           </div>
       </template>
   </AuthenticatedLayout>
</template>

<style scoped></style>
