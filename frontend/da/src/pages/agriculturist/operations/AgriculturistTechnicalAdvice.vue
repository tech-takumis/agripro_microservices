<script setup>
import AuthenticatedLayout from '@/layouts/AuthenticatedLayout.vue'
import FeatureUnavailable from '@/components/FeatureUnavailable.vue'
import { MUNICIPAL_AGRICULTURIST_NAVIGATION } from '@/lib/navigation'
import { useRouter } from 'vue-router'

const router = useRouter()
const navigation = MUNICIPAL_AGRICULTURIST_NAVIGATION

const handleBack = () => {
  router.push({'name': "agriculturist-dashboard"})
}
</script>

<template>
  <AuthenticatedLayout
    :navigation="navigation"
    role-title="Municipal Agriculturist"
    page-title="Technical Advice">
    <FeatureUnavailable
      title="Municipal Agriculturist Technical Advice"
      description="This survey management feature is currently under development. We're working hard to bring you a comprehensive tool for providing agricultural technical advice."
      @back="handleBack"
    />
  </AuthenticatedLayout>
</template>
