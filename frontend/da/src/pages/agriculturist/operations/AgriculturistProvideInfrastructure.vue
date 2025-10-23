<script setup>
import AuthenticatedLayout from '@/layouts/AuthenticatedLayout.vue'
import { MUNICIPAL_AGRICULTURIST_NAVIGATION } from '@/lib/navigation'
import FeatureUnavailable from '@/components/FeatureUnavailable.vue'
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
    page-title="Provide Infrastructure">
    <FeatureUnavailable
      title="Municipal Agriculturist Provide Infrastructure"
      description="This survey management feature is currently under development. We're working hard to bring you a comprehensive tool for providing agricultural infrastructure."
      @back="handleBack"
    />
  </AuthenticatedLayout>
</template>
