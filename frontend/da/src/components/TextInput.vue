<template>
  <input v-bind="$attrs" :value="modelValue" @input="$emit('update:modelValue', $event.target.value)" class="border rounded px-3 py-2 w-full focus:outline-none focus:ring" />
</template>
<script setup>
defineProps({ modelValue: String })
</script>

