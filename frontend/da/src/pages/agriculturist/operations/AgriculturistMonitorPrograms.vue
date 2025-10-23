<script setup>
import AuthenticatedLayout from '@/layouts/AuthenticatedLayout.vue'
import FeatureUnavailable from '@/components/FeatureUnavailable.vue'
import { MUNICIPAL_AGRICULTURIST_NAVIGATION } from '@/lib/navigation'
import { useRouter } from 'vue-router'

const navigation = MUNICIPAL_AGRICULTURIST_NAVIGATION
const router = useRouter()

const handleBack = () => {
  router.push({'name': "agriculturist-dashboard"})
}
</script>

<template>
  <AuthenticatedLayout
    :navigation="navigation"
    role-title="Municipal Agriculturist"
    page-title="Monitor Programs">
    <FeatureUnavailable
      title="Municipal Agriculturist Monitor Programs"
      description="This survey management feature is currently under development. We're working hard to bring you a comprehensive tool for monitoring and managing agricultural programs."
      @back="handleBack"
    />
  </AuthenticatedLayout>
</template>
