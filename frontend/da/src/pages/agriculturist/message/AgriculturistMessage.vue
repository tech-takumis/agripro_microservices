<template>
  <AuthenticatedLayout
    :navigation="navigation"
    role-title="Municipal Agriculturist"
    page-title="Messages"
  >
    <template #header>
      <div class="flex justify-between items-center">
        <h1 class="text-2xl font-semibold text-gray-900">Messages</h1>
      </div>
    </template>

    <!-- 💬 Main Wrapper -->
    <div class="flex flex-col md:flex-row gap-4 h-[calc(110vh-8rem)]">

      <!-- 💭 Chat Messages Container -->
      <div
        class="flex-1 flex flex-col bg-white border border-gray-200 rounded-2xl shadow-sm overflow-hidden"
      >
        <div v-if="!selectedFarmer" class="flex-1 flex items-center justify-center bg-white">
          <div class="text-center space-y-3">
            <User class="h-20 w-20 text-gray-300 mx-auto" />
            <h3 class="text-xl font-medium text-gray-900">No conversation selected</h3>
            <p class="text-gray-500 text-sm">
              Choose a farmer from the list to start messaging
            </p>
          </div>
        </div>

        <template v-else>
          <!-- Header -->
          <div
            class="bg-white border-b border-gray-200 p-4 flex items-center gap-4 sticky top-0 z-10 shadow-sm"
          >
            <button
              v-if="isMobile"
              class="p-2 rounded-full hover:bg-gray-100 transition"
              @click="backToList"
            >
              <ChevronLeft class="h-6 w-6 text-gray-600" />
            </button>

            <div class="relative">
              <div
                class="w-12 h-12 rounded-full bg-green-600 text-white flex items-center justify-center font-medium text-lg"
              >
                {{ getInitials(selectedFarmer) }}
              </div>
              <span
                class="absolute bottom-0 right-0 block w-3 h-3 bg-green-400 border-2 border-white rounded-full"
                title="Online"
              ></span>
            </div>

            <div>
              <h3 class="text-lg font-semibold text-gray-900 leading-tight">
                {{ selectedFarmerName }}
              </h3>
              <p class="text-xs text-gray-500 flex items-center gap-1">
                <Mail class="h-3.5 w-3.5 text-gray-400" />
                {{ selectedFarmer.email || "No email" }}
              </p>
            </div>
          </div>

          <!-- Message List -->
          <div
            ref="messagesContainer"
            class="flex-1 overflow-y-auto p-5 space-y-4 bg-white scroll-smooth"
          >
            <div v-if="messages.length === 0" class="text-center text-gray-500 mt-8">
              No messages yet. Start the conversation!
            </div>

            <div
              v-for="message in messages"
              :key="message.id"
              :class="[
                'flex',
                message.isOwn ? 'justify-end' : 'justify-start',
              ]"
            >
              <div class="max-w-[80%] md:max-w-xl space-y-2">
                <!-- Message Text (separate from attachments) -->
                <div
                  v-if="message.text"
                  :class="[
                    'px-4 py-3 rounded-2xl shadow-sm transition-all duration-200',
                    message.isOwn
                      ? 'bg-green-600 text-white rounded-br-none ml-auto'
                      : 'bg-white text-gray-900 border border-gray-200 rounded-bl-none',
                  ]"
                >
                  <p class="text-sm leading-relaxed">{{ message.text }}</p>
                  <p
                    :class="[
                      'text-xs mt-1',
                      message.isOwn ? 'text-green-100' : 'text-gray-500',
                    ]"
                  >
                    {{ formatTime(message.timestamp) }}
                  </p>
                </div>

                <!-- Attachments (separate from text) -->
                <div
                  v-if="message.attachments && message.attachments.length > 0"
                  :class="[
                    'space-y-2',
                    message.isOwn ? 'ml-auto' : ''
                  ]"
                >
                  <div
                    v-for="(attachment, index) in message.attachments"
                    :key="attachment.documentId || index"
                    class="relative group"
                  >
                    <!-- Download Icon -->
                    <button
                      @click="handleDownload(attachment.documentId)"
                      class="absolute top-2 left-2 z-10 bg-black bg-opacity-50 hover:bg-opacity-70 text-white rounded-full p-2 opacity-0 group-hover:opacity-100 transition-opacity"
                      :disabled="documentStore.isLoading"
                    >
                      <Download class="h-4 w-4" />
                    </button>

                    <!-- Image Preview -->
                    <div v-if="isImageFile(attachment.url)" class="w-64 h-48">
                      <img
                        :src="attachment.url"
                        :alt="`Attachment ${index + 1}`"
                        class="w-full h-full object-cover rounded-lg cursor-pointer hover:opacity-90 transition border shadow-sm"
                        @click="openImageModal(attachment.url, message)"
                        @error="handleImageError"
                      />
                    </div>

                    <!-- File Download Link -->
                    <div v-else class="flex items-center gap-2 p-3 bg-gray-100 rounded-lg border max-w-xs">
                      <Paperclip class="h-4 w-4 text-gray-500" />
                      <span class="text-sm text-gray-700 truncate flex-1">
                        {{ getFileName(attachment.url) }}
                      </span>
                    </div>

                    <!-- Timestamp for attachments only -->
                    <p
                      v-if="!message.text"
                      class="text-xs text-gray-500 mt-1 px-1"
                    >
                      {{ formatTime(message.timestamp) }}
                    </p>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Input Section -->
          <div class="bg-white border-t border-gray-200 p-4">
            <!-- File Preview Section -->
            <div v-if="localFiles.length > 0" class="mb-3 p-3 bg-gray-50 rounded-lg border">
              <div class="flex items-center justify-between mb-2">
                <span class="text-sm font-medium text-gray-700">Files to send ({{ localFiles.length }})</span>
                <button
                  type="button"
                  @click="clearAllFiles"
                  class="text-xs text-red-600 hover:text-red-800"
                >
                  Clear all
                </button>
              </div>
              <div class="space-y-2 max-h-32 overflow-y-auto">
                <div
                  v-for="(fileItem, index) in localFiles"
                  :key="index"
                  class="flex items-center gap-3 p-2 bg-white rounded border"
                >
                  <!-- File Preview -->
                  <div class="flex-shrink-0">
                    <img
                      v-if="isImageFile(fileItem.file)"
                      :src="getFilePreviewUrl(fileItem.file)"
                      alt="Preview"
                      class="w-12 h-12 object-cover rounded border"
                    />
                    <div
                      v-else
                      class="w-12 h-12 bg-gray-200 rounded flex items-center justify-center border"
                    >
                      <Paperclip class="h-4 w-4 text-gray-600" />
                    </div>
                  </div>

                  <!-- File Info -->
                  <div class="flex-1 min-w-0">
                    <p class="text-sm font-medium text-gray-900 truncate">{{ fileItem.name }}</p>
                    <p class="text-xs text-gray-500">{{ formatFileSize(fileItem.file.size) }}</p>
                  </div>

                  <!-- Remove Button -->
                  <button
                    type="button"
                    @click="removeFile(index)"
                    class="flex-shrink-0 text-red-600 hover:text-red-800"
                  >
                    <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
                    </svg>
                  </button>
                </div>
              </div>
            </div>

            <form class="flex gap-3" @submit.prevent="sendChatMessage">
              <label
                class="flex-shrink-0 p-3 border border-gray-300 text-gray-600 rounded-lg hover:bg-gray-50 hover:border-green-400 transition cursor-pointer flex items-center justify-center"
              >
                <Paperclip class="h-5 w-5" />
                <input
                  ref="fileInput"
                  type="file"
                  multiple
                  accept="image/*,.pdf,.doc,.docx,.txt"
                  class="hidden"
                  @change="handleFileSelect"
                />
              </label>

              <input
                v-model="messageInput"
                type="text"
                placeholder="Type a message..."
                class="flex-1 px-4 py-3 text-sm border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-green-400 focus:border-transparent transition"
              />

              <button
                type="submit"
                :disabled="isSendDisabled"
                class="px-5 py-3 bg-green-600 text-white rounded-lg hover:bg-green-700 disabled:opacity-50 flex items-center justify-center transition"
              >
                <Send class="h-5 w-5" />
              </button>
            </form>
          </div>
        </template>
      </div>

       
      <!-- 🧑‍🌾 Farmer List Sidebar -->
      <div
        class="md:w-80 lg:w-96 flex flex-col bg-white border border-gray-200 rounded-2xl shadow-sm overflow-hidden"
      >
        <!-- 🔍 Search Bar -->
        <div class="p-4 border-b border-gray-200 bg-white sticky top-0 z-10 shadow-sm">
          <div class="relative">
            <Search
              class="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-gray-400"
            />
            <input
              v-model="searchQuery"
              type="text"
              placeholder="Search farmers..."
              class="w-full pl-9 pr-3 py-2 text-sm border border-gray-300 rounded-full focus:outline-none focus:ring-2 focus:ring-green-400 focus:border-transparent transition"
            />
          </div>
        </div>

        <!-- 👨‍🌾 Farmer List -->
        <div class="flex-1 overflow-y-auto">
          <div v-if="farmerStore.isLoading" class="p-4 text-center text-gray-500">
            Loading farmers...
          </div>
          <div v-else-if="filteredFarmers.length === 0" class="p-4 text-center text-gray-500">
            No farmers found
          </div>
          <div v-else>
            <button
              v-for="farmer in filteredFarmers"
              :key="farmer.id"
              :class="[
                'w-full p-4 flex items-center gap-4 hover:bg-gray-100 transition border-b border-gray-100 text-left',
                selectedFarmer?.id === farmer.id ? 'bg-green-100' : '',
              ]"
              @click="selectFarmer(farmer)"
            >
              <!-- Avatar -->
              <div class="relative">
                <div
                  class="w-12 h-12 rounded-full bg-green-600 text-white flex items-center justify-center font-medium text-lg"
                >
                  {{ getInitials(farmer) }}
                </div>
                <span
                  class="absolute bottom-0 right-0 block w-3 h-3 bg-green-400 border-2 border-white rounded-full"
                  title="Online"
                ></span>
              </div>

              <!-- Farmer Info -->
              <div class="flex-1 min-w-0">
                <p class="font-semibold text-gray-900 truncate">
                  {{ farmer.firstName }} {{ farmer.lastName }}
                </p>
                <p class="text-xs text-gray-500 truncate">
                  {{ farmer.username || "No RSBSA" }}
                </p>
              </div>
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Enhanced Image Modal with Message Context -->
    <div
      v-if="showImageModal"
      class="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-90"
      @click="closeImageModal"
    >
      <div class="relative max-w-6xl max-h-full p-4 flex gap-4">
        <!-- Image -->
        <div class="flex-1 flex items-center justify-center">
          <img
            :src="selectedImage"
            alt="Full size image"
            class="max-w-full max-h-full rounded-lg"
            @click.stop
          />
        </div>

        <!-- Message Context Panel -->
        <div
          v-if="selectedMessageContext"
          class="w-80 bg-white rounded-lg p-4 overflow-y-auto max-h-full"
          @click.stop
        >
          <div class="flex items-center gap-3 mb-4 pb-3 border-b">
            <div
              class="w-10 h-10 rounded-full bg-green-600 text-white flex items-center justify-center font-medium"
            >
              {{ selectedMessageContext.isOwn ? 'You' : getInitials(selectedFarmer) }}
            </div>
            <div>
              <p class="font-medium text-gray-900">
                {{ selectedMessageContext.isOwn ? 'You' : selectedFarmerName }}
              </p>
              <p class="text-xs text-gray-500">
                {{ formatTime(selectedMessageContext.timestamp) }}
              </p>
            </div>
          </div>

          <div v-if="selectedMessageContext.text" class="mb-3">
            <p class="text-sm text-gray-700 leading-relaxed">
              {{ selectedMessageContext.text }}
            </p>
          </div>

          <div v-if="selectedMessageContext.attachments?.length > 1" class="space-y-2">
            <p class="text-xs font-medium text-gray-600 uppercase tracking-wide">
              Other Attachments ({{ selectedMessageContext.attachments.length - 1 }})
            </p>
            <div
              v-for="(attachment, index) in selectedMessageContext.attachments"
              :key="attachment.documentId || index"
              class="flex items-center gap-2 text-xs text-gray-600"
            >
              <template v-if="attachment.url !== selectedImage">
                <Paperclip class="h-3 w-3" />
                <span class="truncate">{{ getFileName(attachment.url) }}</span>
              </template>
            </div>
          </div>
        </div>

        <!-- Close Button -->
        <button
          class="absolute top-4 right-4 bg-white text-gray-800 rounded-full p-2 hover:bg-gray-100 z-10"
          @click="closeImageModal"
        >
          ✕
        </button>
      </div>
    </div>

    <!-- Error Modal -->
    <div v-if="showErrorModal" class="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-30">
      <div class="bg-white rounded-lg shadow-lg p-6 max-w-sm w-full">
        <h2 class="text-lg font-semibold mb-2 text-red-600">Error</h2>
        <p class="mb-4 text-gray-700">{{ errorMessage }}</p>
        <button class="px-4 py-2 bg-green-600 text-white rounded hover:bg-green-700" @click="showErrorModal = false">Close</button>
      </div>
    </div>
  </AuthenticatedLayout>
</template>

<script setup>
import { ref, computed, onMounted, nextTick, onBeforeUnmount } from 'vue'
import { Search, Send, User, ChevronLeft, Paperclip, Mail, Download } from 'lucide-vue-next'
import { useFarmerStore } from '@/stores/farmer'
import { useAuthStore } from '@/stores/auth'
import { useMessageStore } from '@/stores/message'
import { useDocumentStore } from '@/stores/document'
import AuthenticatedLayout from '@/layouts/AuthenticatedLayout.vue'
import { MUNICIPAL_AGRICULTURIST_NAVIGATION } from '@/lib/navigation'

const farmerStore = useFarmerStore()
const authStore = useAuthStore()
const messageStore = useMessageStore()
const documentStore = useDocumentStore()

// Use the navigation directly
const navigation = MUNICIPAL_AGRICULTURIST_NAVIGATION

// State
const selectedFarmer = ref(null)
const searchQuery = ref('')
const messageInput = ref('')
const messagesContainer = ref(null)
const isMobile = ref(false)
const isInitialized = ref(false)
const fileInput = ref(null)

// Messages from store without preview loading
const messages = computed(() => messageStore.messages)

// Update computed property for files

// Add disabled state for send button with debug logging
const isSendDisabled = computed(() => {
    const hasText = messageInput.value.trim() !== ''
    const hasFiles = localFiles.value.length > 0
    const isDisabled = (!hasText && !hasFiles) || isUploading.value

    console.log('[MessageComponent] Send button state:', {
        hasText,
        hasFiles,
        isUploading: isUploading.value,
        isDisabled
    })
    
    return isDisabled
})

// Add new state for locally stored files
const localFiles = ref([])
const isUploading = ref(false)

// File validation states
const MAX_FILE_SIZE = 20 * 1024 * 1024 // 20MB
const ALLOWED_FILE_TYPES = [
  'application/pdf',
  'image/jpeg',
  'image/png',
  'application/msword',
  'application/vnd.openxmlformats-officedocument.wordprocessingml.document'
]
const errorMessage = ref('')
const showErrorModal = ref(false)

// Computed properties for farmers
const filteredFarmers = computed(() => {
    if (farmerStore.isLoading || !isInitialized.value) return []

    const farmers = farmerStore.allFarmers || []
    if (!searchQuery.value) return farmers

    const query = searchQuery.value.toLowerCase()
    return farmers.filter(farmer => {
        const firstName = farmer?.firstName || ''
        const lastName = farmer?.lastName || ''
        const fullName = `${firstName} ${lastName}`.toLowerCase()
        return fullName.includes(query)
    })
})

const selectedFarmerName = computed(() => {
    if (!selectedFarmer.value) return ''
    return `${selectedFarmer.value.firstName || ''} ${selectedFarmer.value.lastName || ''}`.trim()
})

const checkMobileView = () => {
    isMobile.value = window.innerWidth < 768
}

const backToList = () => {
    if (isMobile.value) {
        selectedFarmer.value = null
    }
}

const handleFileSelect = async event => {
    const files = Array.from(event.target.files)
    for (const file of files) {
        if (file.size > MAX_FILE_SIZE) {
            errorMessage.value = `File ${file.name} exceeds 20MB limit.`
            showErrorModal.value = true
            continue
        }
        if (!ALLOWED_FILE_TYPES.includes(file.type)) {
            errorMessage.value = `File type ${file.type} is not allowed.`
            showErrorModal.value = true
            continue
        }
        localFiles.value.push({ file, name: file.name, type: file.type })
    }
    if (fileInput.value) fileInput.value.value = ''
}

const selectFarmer = async farmer => {
    try {
        if (!farmer) {
            console.error('[MessageComponent] No farmer provided to selectFarmer')
            return
        }

        console.log('[MessageComponent] Selecting farmer:', {
            id: farmer.id,
            userId: farmer.userId,
            name: `${farmer.firstName} ${farmer.lastName}`
        })

        selectedFarmer.value = {
            ...farmer,
            // Ensure we have a userId, fall back to id if userId is not available
            userId: farmer.userId || farmer.id
        }

        if (selectedFarmer.value.userId) {
            console.log('[MessageComponent] Fetching messages for user:', selectedFarmer.value.userId)
            await messageStore.fetchMessages(authStore.userId, selectedFarmer.value.userId)
            scrollToBottom()
        } else {
            console.error('[MessageComponent] No userId available for selected farmer:', farmer)
        }
    } catch (error) {
        console.error('[MessageComponent] Error selecting farmer:', {
            error: error.message,
            farmer: farmer ? { id: farmer.id, name: `${farmer.firstName} ${farmer.lastName}` } : 'No farmer'
        })
    }
}

const sendChatMessage = async () => {
    try {
        const receiverId = selectedFarmer.value.userId
        if (!receiverId) {
            errorMessage.value = 'Role not implemented or receiver not found.'
            showErrorModal.value = true
            return
        }

        const files = localFiles.value.map(f => f.file)
        const messageText = messageInput.value.trim()

        // Allow sending if either text or files are present
        if (!messageText && files.length === 0) {
            errorMessage.value = 'Please enter a message or select files to send.'
            showErrorModal.value = true
            return
        }

        console.log(`[MessageComponent] Prepared ${files.length} files for upload`,
            files.map(f => ({ name: f.name, type: f.type, size: f.size })))

        // Prepare message payload - text is optional if files are present
        const messagePayload = {
            receiverId: receiverId,
            text: messageText || '', // Allow empty text if files are present
            type: 'FARMER_AGRICULTURE',
            files: files,
            sentAt: new Date().toISOString()
        }
        
        console.log('[MessageComponent] Sending message with payload:', {
            ...messagePayload,
            files: files.map(f => `${f.name} (${f.type}, ${(f.size / 1024).toFixed(2)} KB)`)
        })
        
        await messageStore.sendMessage(messagePayload)

        messageInput.value = ''
        localFiles.value = []
        scrollToBottom()
    } catch (error) {
        if (error.response?.status === 403 || error.response?.data?.error === 'ROLE_NOT_IMPLEMENTED') {
            errorMessage.value = 'Your role is not implemented for this action.'
            showErrorModal.value = true
        } else {
            errorMessage.value = 'Failed to send message.'
            showErrorModal.value = true
        }
    } finally {
        isUploading.value = false
    }
}

const scrollToBottom = () => {
    nextTick(() => {
        if (messagesContainer.value) {
            messagesContainer.value.scrollTop =
                messagesContainer.value.scrollHeight
        }
    })
}

const formatTime = timestamp => {
    if (!timestamp) return ''
    try {
        const date = new Date(timestamp)
        return date.toLocaleTimeString([], {
            hour: '2-digit',
            minute: '2-digit',
        })
    } catch (error) {
        console.error('Error formatting timestamp:', error)
        return ''
    }
}

const getInitials = user => {
    if (!user) return ''
    const firstName = user.firstName || ''
    const lastName = user.lastName || ''
    return `${firstName.charAt(0)}${lastName.charAt(0)}`.toUpperCase()
}

// Add new state for image modal
const showImageModal = ref(false)
const selectedImage = ref('')

// Add new state for enhanced image modal
const selectedMessageContext = ref(null)

// Add utility functions for attachments (updated to handle both URLs and File objects)
const isImageFile = (urlOrFile) => {
  if (!urlOrFile) return false

  // If it's a File object, check the type
  if (urlOrFile.type) {
    return urlOrFile.type.startsWith('image/')
  }

  // If it's a URL string, check the extension
  const imageExtensions = ['.jpg', '.jpeg', '.png', '.gif', '.webp']
  return imageExtensions.some(ext => urlOrFile.toLowerCase().includes(ext))
}

const getFileName = (url) => {
  if (!url) return 'Unknown file'
  try {
    const urlParts = url.split('/')
    const fileName = urlParts[urlParts.length - 1]
    // Remove query parameters
    return fileName.split('?')[0] || 'Download file'
  } catch {
    return 'Download file'
  }
}

const handleDownload = async (documentId) => {
  if (!documentId) {
    errorMessage.value = 'Document ID not found'
    showErrorModal.value = true
    return
  }

  try {
    const result = await documentStore.downloadDocument(documentId)
    if (!result.success) {
      errorMessage.value = result.message || 'Failed to download document'
      showErrorModal.value = true
    }
  } catch (error) {
    console.error('Error downloading document:', error)
    errorMessage.value = 'Failed to download document'
    showErrorModal.value = true
  }
}

const openImageModal = (imageUrl, messageContext = null) => {
  selectedImage.value = imageUrl
  selectedMessageContext.value = messageContext
  showImageModal.value = true
}

const closeImageModal = () => {
  showImageModal.value = false
  selectedImage.value = ''
  selectedMessageContext.value = null
}

const handleImageError = (event) => {
  console.warn('Failed to load image:', event.target.src)
  event.target.style.display = 'none'
}

// New utility functions for file preview
const getFilePreviewUrl = (file) => {
  try {
    return URL.createObjectURL(file)
  } catch (error) {
    console.error('Error creating preview URL:', error)
    return null
  }
}

const formatFileSize = (bytes) => {
  if (bytes === 0) return '0 Bytes'
  const k = 1024
  const sizes = ['Bytes', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

const removeFile = (index) => {
  localFiles.value.splice(index, 1)
}

const clearAllFiles = () => {
  localFiles.value = []
}

// Lifecycle hooks
onMounted(async () => {
    try {
        checkMobileView()
        window.addEventListener('resize', checkMobileView)

        // Then fetch farmers
        await farmerStore.fetchFarmers()
        isInitialized.value = true

        if (selectedFarmer.value) {
            await messageStore.fetchMessages(
                authStore.userId,
                selectedFarmer.value.id,
            )
        }
    } catch (error) {
        console.error('Error during component mount:', error)
    }
})

onBeforeUnmount(() => {
    window.removeEventListener('resize', checkMobileView)
})
</script>

<style scoped>
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

.attachment-item {
  margin-bottom: 8px;
}

.attachment-item:last-child {
  margin-bottom: 0;
}
</style>
