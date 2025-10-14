<script setup>
import AuthenticatedLayout from '@/layouts/AuthenticatedLayout.vue'
import FeatureUnavailable from '@/components/FeatureUnavailable.vue'
import { MUNICIPAL_AGRICULTURIST_NAVIGATION } from '@/lib/navigation'
import { useRouter } from 'vue-router'

const navigation = MUNICIPAL_AGRICULTURIST_NAVIGATION
const router = useRouter()

// <CHANGE> Handle back navigation when user clicks back button
const handleBack = () => {
    router.push({'name': "municipal-agriculturist-dashboard"})
}
</script>

<template>
    <AuthenticatedLayout
        :navigation="navigation"
        role-title="Admin"
        page-title="EDIT User">
        <FeatureUnavailable
            title="EDIT User From Admin"
            description="This feature is not yet available."
            @back="handleBack"
        />
    </AuthenticatedLayout>
</template>

