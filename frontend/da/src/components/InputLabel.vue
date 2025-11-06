<template>
  <label :for="for" class="block text-sm font-medium text-gray-700 mb-1">
    {{ value }}
  </label>
</template>
<script setup>
defineProps({ for: String, value: String })
</script>

