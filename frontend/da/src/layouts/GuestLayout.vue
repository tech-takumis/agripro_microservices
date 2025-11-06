<template>
  <div :class="fullPage ? 'min-h-screen bg-white' : 'min-h-screen flex flex-col items-center justify-center bg-gray-50'">
    <div v-if="!fullPage" class="w-full max-w-md p-6 bg-white rounded-lg shadow-md">
      <slot />
    </div>
    <div v-else class="w-full h-screen">
      <slot />
    </div>
  </div>
</template>

<script setup>
defineProps({
  fullPage: { type: Boolean, default: false }
})
</script>
