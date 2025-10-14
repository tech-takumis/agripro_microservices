&lt;template>
  <div class="flex flex-col h-full">
    <div class="flex-1 overflow-y-auto">
      <MessageList :messages="messageStore.messages" />
    </div>
    <div class="p-4 border-t">
      <form @submit.prevent="sendMessage" class="flex gap-2">
        <input
          v-model="newMessage"
          type="text"
          placeholder="Type a message..."
          class="flex-1 px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
        />
        <button
          type="submit"
          class="px-4 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 focus:outline-none focus:ring-2 focus:ring-blue-500"
        >
          Send
        </button>
      </form>
    </div>
  </div>
&lt;/template>

&lt;script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useMessageStore } from '@/stores/message'
import MessageList from './MessageList.vue'

const messageStore = useMessageStore()
const newMessage = ref('')

// Replace these with actual user IDs from your auth system
const currentUserId = 'YOUR_CURRENT_USER_ID'
const selectedUserId = 'SELECTED_USER_ID'

onMounted(async () => {
  await messageStore.fetchMessages(currentUserId, selectedUserId)
  await messageStore.subscribeToUserMessages(currentUserId, selectedUserId)
})

onUnmounted(() => {
  messageStore.cleanup()
})

const sendMessage = async () => {
  if (!newMessage.value.trim()) return

  await messageStore.sendMessage({
    senderId: currentUserId,
    receiverId: selectedUserId,
    text: newMessage.value
  })

  newMessage.value = ''
}
&lt;/script>
