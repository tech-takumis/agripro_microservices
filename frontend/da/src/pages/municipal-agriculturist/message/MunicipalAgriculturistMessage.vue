<template>
  <AuthenticatedLayout
    :navigation="navigation"
    role-title="Municipal Agriculturist"
    page-title="Messages">
    <template #header>
      <div class="flex justify-between items-center">
        <h1 class="text-2xl font-semibold text-gray-900">Messages</h1>
      </div>
    </template>

    <!-- Container with responsive classes -->
    <div class="flex flex-col md:flex-row bg-white rounded-lg shadow h-[calc(100vh-12rem)]">
      <!-- Left Side - Chat Interface with responsive width -->
      <div class="flex-1 flex flex-col min-w-0 order-2 md:order-1" :class="{ 'hidden md:flex': !selectedFarmer && isMobile }">
        <!-- Empty State -->
        <div v-if="!selectedFarmer" class="flex-1 flex items-center justify-center bg-gray-50">
          <div class="text-center">
            <User class="h-20 w-20 text-gray-300 mx-auto mb-4" />
            <h3 class="text-xl font-medium text-gray-900 mb-2">No conversation selected</h3>
            <p class="text-gray-500">Choose a farmer from the list to start messaging</p>
          </div>
        </div>

        <!-- Chat Interface -->
        <template v-else>
          <!-- Chat Header with back button for mobile -->
          <div class="bg-white border-b border-gray-200 p-4 md:p-6 flex items-center gap-4">
            <button
              v-if="isMobile"
              @click="backToList"
              class="p-1 mr-2 rounded-full hover:bg-gray-100"
            >
              <ChevronLeft class="h-6 w-6 text-gray-600" />
            </button>
            <div class="w-10 h-10 md:w-12 md:h-12 rounded-full bg-blue-500 text-white flex items-center justify-center font-medium text-lg">
              {{ getInitials(selectedFarmer) }}
            </div>
            <div>
              <h3 class="text-base md:text-lg font-semibold text-gray-900">{{ selectedFarmerName }}</h3>
              <p class="text-xs md:text-sm text-gray-500">{{ selectedFarmer.email || 'No email' }}</p>
            </div>
          </div>

          <!-- Messages Container with increased height -->
          <div ref="messagesContainer" class="flex-1 overflow-y-auto p-4 md:p-6 space-y-4 bg-gray-50">
            <div v-if="messages.length === 0" class="text-center text-gray-500 mt-8">
              No messages yet. Start the conversation!
            </div>

            <div
              v-for="message in messages"
              :key="message.id"
              :class="[
                  'flex',
                  message.isOwn ? 'justify-end' : 'justify-start'
              ]"
            >
              <div
                :class="[
                    'max-w-[80%] md:max-w-xl px-4 md:px-6 py-2 md:py-3 rounded-lg',
                    message.isOwn
                        ? 'bg-blue-500 text-white'
                        : 'bg-white text-gray-900 border border-gray-200'
                ]"
              >
                <p class="text-sm md:text-base">{{ message.content }}</p>
                <p
                  :class="[
                      'text-xs mt-1',
                      message.isOwn ? 'text-blue-100' : 'text-gray-500'
                  ]"
                >
                  {{ formatTime(message.timestamp) }}
                </p>
              </div>
            </div>
          </div>

          <!-- Message Input with responsive sizing -->
          <div class="bg-white border-t border-gray-200 p-3 md:p-6">
            <form @submit.prevent="sendMessage" class="flex gap-2 md:gap-3">
              <input
                v-model="messageInput"
                type="text"
                placeholder="Type a message..."
                class="flex-1 px-4 md:px-6 py-2 md:py-3 text-sm md:text-base border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
              <button
                type="submit"
                :disabled="!messageInput.trim()"
                class="px-4 md:px-6 py-2 md:py-3 bg-blue-500 text-white rounded-lg hover:bg-blue-600 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
              >
                <Send class="h-5 w-5 md:h-6 md:w-6" />
              </button>
            </form>
          </div>
        </template>
      </div>

      <!-- Right Sidebar - Farmer List with responsive width -->
      <div
        class="border-b md:border-l md:border-b-0 border-gray-200 flex flex-col order-1 md:order-2 md:w-80 lg:w-96"
        :class="{ 'hidden md:flex': selectedFarmer && isMobile }"
      >
        <!-- Search Header -->
        <div class="p-4 md:p-6 border-b border-gray-200">
          <div class="relative">
            <Search class="absolute left-3 md:left-4 top-1/2 -translate-y-1/2 h-4 w-4 md:h-5 md:w-5 text-gray-400" />
            <input
              v-model="searchQuery"
              type="text"
              placeholder="Search farmers..."
              class="w-full pl-9 md:pl-12 pr-3 md:pr-4 py-2 md:py-3 text-sm md:text-base border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            />
          </div>
        </div>

        <!-- Farmer List with responsive items -->
        <div class="flex-1 overflow-y-auto">
          <div v-if="farmerStore.isLoading" class="p-4 md:p-6 text-center text-gray-500">
            Loading farmers...
          </div>
          <div v-else-if="filteredFarmers.length === 0" class="p-4 md:p-6 text-center text-gray-500">
            No farmers found
          </div>
          <div v-else>
            <button
              v-for="farmer in filteredFarmers"
              :key="farmer.id"
              @click="selectFarmer(farmer)"
              :class="[
                  'w-full p-4 md:p-6 flex items-center gap-3 md:gap-4 hover:bg-gray-50 transition-colors border-b border-gray-100',
                  selectedFarmer?.id === farmer.id ? 'bg-blue-50' : ''
              ]"
            >
              <!-- Avatar -->
              <div class="flex-shrink-0 w-10 h-10 md:w-14 md:h-14 rounded-full bg-blue-500 text-white flex items-center justify-center font-medium text-base md:text-lg">
                {{ getInitials(farmer) }}
            </div>

            <!-- Farmer Info -->
            <div class="flex-1 text-left min-w-0">
              <p class="font-medium text-base md:text-lg text-gray-900 truncate">
                {{ farmer.firstName }} {{ farmer.lastName }}
              </p>
              <p class="text-xs md:text-sm text-gray-500 truncate">
                {{ farmer.email || 'No email' }}
              </p>
            </div>
          </button>
        </div>
      </div>
    </div>
    </div>
  </AuthenticatedLayout>
</template>

<script setup>
import { ref, computed, onMounted, nextTick, onBeforeUnmount, watch } from 'vue'
import { Search, Send, User, ChevronLeft } from 'lucide-vue-next'
import { useFarmerStore } from '@/stores/farmer'
import { useWebSocket } from '@/composables/useWebSocket'
import { useAuthStore } from '@/stores/auth'
import AuthenticatedLayout from '@/layouts/AuthenticatedLayout.vue'
import { MUNICIPAL_AGRICULTURIST_NAVIGATION } from '@/lib/navigation'

const farmerStore = useFarmerStore()
const authStore = useAuthStore()
const ws = useWebSocket()

// Use the navigation directly
const navigation = MUNICIPAL_AGRICULTURIST_NAVIGATION

// State
const selectedFarmer = ref(null)
const searchQuery = ref('')
const messageInput = ref('')
const messages = ref([])
const messagesContainer = ref(null)
const currentSubscription = ref(null)
const isMobile = ref(false)

// Check if the screen is mobile size
const checkMobileView = () => {
    isMobile.value = window.innerWidth < 768
}

// Go back to the farmer list on mobile
const backToList = () => {
    if (isMobile.value) {
        selectedFarmer.value = null
    }
}

// Computed properties
const filteredFarmers = computed(() => {
    if (!searchQuery.value) return farmerStore.allFarmers

    const query = searchQuery.value.toLowerCase()
    return farmerStore.allFarmers.filter(farmer => {
        const firstName = farmer.firstName || ''
        const lastName = farmer.lastName || ''
        const fullName = `${firstName} ${lastName}`.toLowerCase()
        return fullName.includes(query)
    })
})

const selectedFarmerName = computed(() => {
    if (!selectedFarmer.value) return ''
    return `${selectedFarmer.value.firstName || ''} ${selectedFarmer.value.lastName || ''}`.trim()
})

// Methods
const selectFarmer = async (farmer) => {
    try {
        // Unsubscribe from previous subscription if exists
        if (currentSubscription.value) {
            currentSubscription.value.unsubscribe()
        }

        selectedFarmer.value = farmer
        messages.value = []

        if (farmer && farmer.id) {
            await subscribeToFarmerMessages(farmer.id)
        }
    } catch (error) {
        console.error('Error selecting farmer:', error)
    }
}

const subscribeToFarmerMessages = async (farmerId) => {
    try {
        if (!ws.stompClient.value?.connected) {
            console.log('WebSocket not connected, attempting to connect...')
            await ws.connect()
        }

        // Subscribe to private messages
        currentSubscription.value = ws.stompClient.value.subscribe(
            `/user/${authStore.userId}/topic/private-messages`,
            (message) => {
                try {
                    const data = JSON.parse(message.body)
                    console.log('Received message:', data)

                    if (data.senderId === selectedFarmer.value?.id) {
                        messages.value.push({
                            id: data.id || Date.now(),
                            content: data.content,
                            senderId: data.senderId,
                            recipientId: data.recipientId,
                            timestamp: data.timestamp || new Date().toISOString(),
                            isOwn: false
                        })
                        scrollToBottom()
                    }
                } catch (error) {
                    console.error('Error handling message:', error)
                }
            }
        )

        console.log('Subscribed to messages for farmer:', farmerId)
    } catch (error) {
        console.error('Failed to subscribe to messages:', error)
    }
}

const sendMessage = async () => {
    if (!messageInput.value.trim() || !selectedFarmer.value?.id) return

    const messageDto = {
        senderId: authStore.userId,
        recipientId: selectedFarmer.value.id,
        content: messageInput.value.trim(),
        timestamp: new Date().toISOString()
    }

    try {
        if (!ws.stompClient.value?.connected) {
            console.log('WebSocket not connected, attempting to connect...')
            await ws.connect()
        }

        // Send message through WebSocket
        ws.stompClient.value.publish({
            destination: '/app/chat.private',
            body: JSON.stringify(messageDto)
        })

        // Add message to local state
        messages.value.push({
            id: Date.now(),
            content: messageInput.value.trim(),
            senderId: authStore.userId,
            recipientId: selectedFarmer.value.id,
            timestamp: new Date().toISOString(),
            isOwn: true
        })

        // Clear input and scroll to bottom
        messageInput.value = ''
        scrollToBottom()
    } catch (error) {
        console.error('Failed to send message:', error)
        // You might want to show an error notification here
    }
}

const scrollToBottom = () => {
    nextTick(() => {
        if (messagesContainer.value) {
            messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
        }
    })
}

const formatTime = (timestamp) => {
    if (!timestamp) return ''
    try {
        const date = new Date(timestamp)
        return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
    } catch (error) {
        console.error('Error formatting timestamp:', error)
        return ''
    }
}

const getInitials = (user) => {
    if (!user) return ''
    const firstName = user.firstName || ''
    const lastName = user.lastName || ''
    return `${firstName.charAt(0)}${lastName.charAt(0)}`.toUpperCase()
}

// Lifecycle hooks
onMounted(async () => {
    try {
        checkMobileView()
        window.addEventListener('resize', checkMobileView)

        // Initialize WebSocket connection
        await ws.connect()

        // Fetch farmers
        await farmerStore.fetchFarmers()
    } catch (error) {
        console.error('Error during component mount:', error)
    }
})

onBeforeUnmount(() => {
    window.removeEventListener('resize', checkMobileView)
    if (currentSubscription.value) {
        currentSubscription.value.unsubscribe()
    }
    ws.disconnect()
})

// Watch for WebSocket connection changes
watch(() => ws.stompClient.value?.connected, async (isConnected) => {
    if (isConnected && selectedFarmer.value) {
        await subscribeToFarmerMessages(selectedFarmer.value.id)
    }
})
</script>

<style scoped>
/* Smooth transitions for mobile view */
.fade-enter-active,
.fade-leave-active {
    transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
    opacity: 0;
}

/* Custom scrollbar for messages */
.overflow-y-auto {
    scrollbar-width: thin;
    scrollbar-color: rgba(156, 163, 175, 0.5) transparent;
}

.overflow-y-auto::-webkit-scrollbar {
    width: 6px;
}

.overflow-y-auto::-webkit-scrollbar-track {
    background: transparent;
}

.overflow-y-auto::-webkit-scrollbar-thumb {
    background-color: rgba(156, 163, 175, 0.5);
    border-radius: 3px;
}


</style>
