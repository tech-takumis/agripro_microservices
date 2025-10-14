<template>
  <div class="flex flex-col space-y-4 p-4">
    <div v-for="message in messages" :key="message.id"
         :class="[
           'p-3 rounded-lg max-w-[70%]',
           message.isOwn ? 'ml-auto bg-blue-500 text-white' : 'bg-gray-100'
         ]">
      <div class="text-sm">{{ message.text }}</div>
      <div class="text-xs mt-1" :class="message.isOwn ? 'text-blue-100' : 'text-gray-500'">
        {{ new Date(message.timestamp).toLocaleTimeString() }}
      </div>
      <div v-if="message.attachments?.length" class="mt-2">
        <div v-for="attachment in message.attachments" :key="attachment"
             class="text-xs underline">
          Attachment: {{ attachment }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { defineProps } from 'vue'

defineProps({
  messages: {
    type: Array,
    required: true,
    default: () => []
  }
})
</script>
