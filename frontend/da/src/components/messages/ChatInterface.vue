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
import { useAuthStore } from '@/stores/auth'
import MessageList from './MessageList.vue'

const messageStore = useMessageStore()
const authStore = useAuthStore()
const newMessage = ref('')

// Get current user ID from auth store
const currentUserId = authStore.userId

onMounted(async () => {
  if (currentUserId) {
    await messageStore.fetchMessages(currentUserId)
    await messageStore.subscribeToUserMessages(currentUserId)
  }
})

onUnmounted(() => {
  messageStore.cleanup()
})

const sendMessage = async () => {
  if (!newMessage.value.trim()) return

  try {
    await messageStore.sendMessage({
      receiverId: props.selectedUserId, // This should be passed as a prop from parent
      text: newMessage.value,
      type: props.messageType || 'FARMER_AGRICULTURE' // This should be passed as a prop based on user roles
    })

    newMessage.value = ''
  } catch (error) {
    console.error('Failed to send message:', error)
    // You might want to show an error toast here
  }
}

// Define props for the component
const props = defineProps({
  selectedUserId: {
    type: String,
    required: true
  },
  messageType: {
    type: String,
    default: 'FARMER_AGRICULTURE'
  }
})
&lt;/script>
