<template>
  <div v-if="errors && errors.length" class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
    <ul>
      <li v-for="(error, i) in errors" :key="i">{{ error }}</li>
    </ul>
  </div>
</template>
<script setup>
defineProps({ errors: { type: Array, default: () => [] } })
</script>
