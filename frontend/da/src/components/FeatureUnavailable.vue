<script setup lang="ts">
import { Construction, ArrowLeft } from 'lucide-vue-next'

interface Props {
  title?: string
  description?: string
  showIcon?: boolean
  showBackButton?: boolean
}

interface Emits {
  back: []
}

const props = withDefaults(defineProps<Props>(), {
  title: 'Feature Not Available',
  description: 'This feature is currently under development and will be available soon.',
  showIcon: true,
  showBackButton: true
})

const emit = defineEmits<Emits>()

const handleBack = () => {
  // <CHANGE> Emit event to parent component for navigation handling
  emit('back')
}
</script>

<template>
  <div class="flex items-center justify-center min-h-[400px] p-8">
    <div class="text-center max-w-md">
      <div v-if="props.showIcon" class="flex justify-center mb-6">
        <div class="w-16 h-16 rounded-full bg-gray-100 flex items-center justify-center">
          <Construction class="w-8 h-8 text-gray-400" />
        </div>
      </div>
      
      <h2 class="text-2xl font-semibold text-gray-800 mb-3">
        {{ props.title }}
      </h2>
      
      <p class="text-gray-600 leading-relaxed">
        {{ props.description }}
      </p>
      
      <div v-if="props.showBackButton" class="mt-8">
        <button
          @click="handleBack"
          class="inline-flex items-center gap-2 px-6 py-2.5 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition-colors duration-200 font-medium"
        >
          <ArrowLeft class="w-4 h-4" />
          Back to Dashboard
        </button>
      </div>
      
      <div class="mt-6 pt-6 border-t border-gray-200">
        <p class="text-sm text-gray-500">
          Check back later for updates
        </p>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* Minimal scoped styles - relying on Tailwind for consistency */
</style>
