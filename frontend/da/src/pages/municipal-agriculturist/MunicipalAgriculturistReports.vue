<script setup lang="ts">
import FeatureUnavailable from '../../components/FeatureUnavailable.vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// <CHANGE> Handle back navigation when user clicks back button
const handleBack = () => {
  router.push({'name': "municipal-agriculturist-dashboard"})
}
</script>

<template>
  <FeatureUnavailable 
    title="Municipal Agriculturist Reports"
    description="This survey management feature is currently under development. We're working hard to bring you a comprehensive tool for generating agricultural reports."
    @back="handleBack"
  />
</template>

