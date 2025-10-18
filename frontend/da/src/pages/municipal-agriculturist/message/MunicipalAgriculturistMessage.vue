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
        <div
            class="flex flex-col md:flex-row bg-white rounded-lg shadow h-[calc(100vh-12rem)]">
            <!-- Left Side - Chat Interface with responsive width -->
            <div
                class="flex-1 flex flex-col min-w-0 order-2 md:order-1"
                :class="{ 'hidden md:flex': !selectedFarmer && isMobile }">
                <!-- Empty State -->
                <div
                    v-if="!selectedFarmer"
                    class="flex-1 flex items-center justify-center bg-gray-50">
                    <div class="text-center">
                        <User class="h-20 w-20 text-gray-300 mx-auto mb-4" />
                        <h3 class="text-xl font-medium text-gray-900 mb-2">
                            No conversation selected
                        </h3>
                        <p class="text-gray-500">
                            Choose a farmer from the list to start messaging
                        </p>
                    </div>
                </div>

                <!-- Chat Interface -->
                <template v-else>
                    <!-- Chat Header with back button for mobile -->
                    <div
                        class="bg-white border-b border-gray-200 p-4 md:p-6 flex items-center gap-4">
                        <button
                            v-if="isMobile"
                            @click="backToList"
                            class="p-1 mr-2 rounded-full hover:bg-gray-100">
                            <ChevronLeft class="h-6 w-6 text-gray-600" />
                        </button>
                        <div
                            class="w-10 h-10 md:w-12 md:h-12 rounded-full bg-blue-500 text-white flex items-center justify-center font-medium text-lg">
                            {{ getInitials(selectedFarmer) }}
                        </div>
                        <div>
                            <h3
                                class="text-base md:text-lg font-semibold text-gray-900">
                                {{ selectedFarmerName }}
                            </h3>
                            <p class="text-xs md:text-sm text-gray-500">
                                {{ selectedFarmer.email || 'No email' }}
                            </p>
                        </div>
                    </div>

                    <!-- Messages Container with increased height -->
                    <div
                        ref="messagesContainer"
                        class="flex-1 overflow-y-auto p-4 md:p-6 space-y-4 bg-gray-50">
                        <div
                            v-if="messages.length === 0"
                            class="text-center text-gray-500 mt-8">
                            No messages yet. Start the conversation!
                        </div>

                        <div
                            v-for="message in messages"
                            :key="message.id"
                            :class="[
                                'flex',
                                message.isOwn ? 'justify-end' : 'justify-start',
                            ]">
                            <div
                                :class="[
                                    'max-w-[80%] md:max-w-xl px-4 md:px-6 py-2 md:py-3 rounded-lg',
                                    message.isOwn
                                        ? 'bg-blue-500 text-white'
                                        : 'bg-white text-gray-900 border border-gray-200',
                                ]">
                                <p class="text-sm md:text-base">
                                    {{ message.text }}
                                </p>
                                <!-- Display message attachments -->
                                <div
                                    v-if="message.attachments && message.attachments.length > 0"
                                    class="mt-2 flex flex-wrap gap-2">
                                    <div
                                        v-for="(attachment, index) in message.attachments"
                                        :key="attachment.documentId"
                                        @click="openAttachment(attachment.url)"
                                        class="h-24 w-24 flex items-center justify-center bg-gray-100 rounded cursor-pointer hover:bg-gray-200 transition-colors">
                                        <img
                                            v-if="attachment.url"
                                            :src="attachment.url"
                                            class="h-24 w-24 object-cover rounded"
                                            :alt="'Attachment ' + (index + 1)"
                                        />
                                        <svg v-else xmlns="http://www.w3.org/2000/svg" class="h-8 w-8 text-gray-400" viewBox="0 0 20 20" fill="currentColor">
                                            <path fill-rule="evenodd" d="M4 4a2 2 0 012-2h4.586A2 2 0 0112 2.586L15.414 6A2 2 0 0116 7.414V16a2 2 0 01-2 2H6a2 2 0 01-2-2V4z" clip-rule="evenodd" />
                                        </svg>
                                    </div>
                                </div>
                                <p
                                    :class="[
                                        'text-xs mt-1',
                                        message.isOwn
                                            ? 'text-blue-100'
                                            : 'text-gray-500',
                                    ]">
                                    {{ formatTime(message.timestamp) }}
                                </p>
                            </div>
                        </div>
                    </div>

                    <!-- File Preview Section -->
                    <div v-if="localFiles.length > 0" class="bg-gray-50 border-t border-gray-200 px-3 md:px-6 py-3">
                        <div class="flex flex-wrap gap-2">
                            <div
                                v-for="(file, index) in localFiles"
                                :key="index"
                                class="group relative flex items-center gap-2 bg-white border border-gray-200 rounded-lg px-3 py-2 pr-8 hover:border-gray-300 transition-colors"
                            >
                                <img
                                    v-if="file.preview"
                                    :src="file.preview"
                                    class="h-8 w-8 object-cover rounded"
                                    :alt="file.name"
                                />
                                <Paperclip v-else class="h-4 w-4 text-gray-400 flex-shrink-0" />
                                <span class="text-sm text-gray-700 truncate max-w-[150px] md:max-w-[200px]">
                                    {{ file.name }}
                                </span>
                                <button
                                    class="absolute right-1 top-1/2 -translate-y-1/2 p-1 rounded-full hover:bg-gray-100 transition-colors"
                                    type="button"
                                    @click="removeFile(index)"
                                >
                                    <X class="h-4 w-4 text-gray-500" />
                                </button>
                            </div>
                        </div>
                    </div>

                    <!-- Message Input with file upload -->
                    <div class="bg-white border-t border-gray-200 p-3 md:p-6">
                        <form
                            @submit.prevent="sendChatMessage"
                            class="flex gap-2 md:gap-3">
                            <!-- File Upload Button -->
                            <label
                                class="flex-shrink-0 px-3 md:px-4 py-2 md:py-3 border border-gray-300 text-gray-600 rounded-lg hover:bg-gray-50 hover:border-gray-400 transition-colors cursor-pointer flex items-center justify-center">
                                <Paperclip class="h-5 w-5 md:h-6 md:w-6" />
                                <input
                                    ref="fileInput"
                                    type="file"
                                    multiple
                                    accept="image/*,.pdf,.doc,.docx,.txt"
                                    class="hidden"
                                    @change="handleFileSelect" />
                            </label>

                            <!-- Message Input -->
                            <input
                                v-model="messageInput"
                                type="text"
                                placeholder="Type a message..."
                                class="flex-1 px-4 md:px-6 py-2 md:py-3 text-sm md:text-base border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent" />

                            <!-- Send Button -->
                            <button
                                type="submit"
                                :disabled="isSendDisabled"
                                class="flex-shrink-0 px-4 md:px-6 py-2 md:py-3 bg-blue-500 text-white rounded-lg hover:bg-blue-600 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
                                :class="{ 'opacity-50': isUploading }"
                            >
                                <Send class="h-5 w-5 md:h-6 md:w-6" />
                                <span v-if="isUploading" class="ml-2">Uploading...</span>
                            </button>
                        </form>
                    </div>
                </template>
            </div>

            <!-- Right Sidebar - Farmer List with responsive width -->
            <div
                class="border-b md:border-l md:border-b-0 border-gray-200 flex flex-col order-1 md:order-2 md:w-80 lg:w-96"
                :class="{ 'hidden md:flex': selectedFarmer && isMobile }">
                <!-- Search Header -->
                <div class="p-4 md:p-6 border-b border-gray-200">
                    <div class="relative">
                        <Search
                            class="absolute left-3 md:left-4 top-1/2 -translate-y-1/2 h-4 w-4 md:h-5 md:w-5 text-gray-400" />
                        <input
                            v-model="searchQuery"
                            type="text"
                            placeholder="Search farmers..."
                            class="w-full pl-9 md:pl-12 pr-3 md:pr-4 py-2 md:py-3 text-sm md:text-base border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent" />
                    </div>
                </div>

                <!-- Farmer List with responsive items -->
                <div class="flex-1 overflow-y-auto">
                    <div
                        v-if="farmerStore.isLoading"
                        class="p-4 md:p-6 text-center text-gray-500">
                        Loading farmers...
                    </div>
                    <div
                        v-else-if="filteredFarmers.length === 0"
                        class="p-4 md:p-6 text-center text-gray-500">
                        No farmers found
                    </div>
                    <div v-else>
                        <button
                            v-for="farmer in filteredFarmers"
                            :key="farmer.id"
                            @click="selectFarmer(farmer)"
                            :class="[
                                'w-full p-4 md:p-6 flex items-center gap-3 md:gap-4 hover:bg-gray-50 transition-colors border-b border-gray-100',
                                selectedFarmer?.id === farmer.id
                                    ? 'bg-blue-50'
                                    : '',
                            ]">
                            <!-- Avatar -->
                            <div
                                class="flex-shrink-0 w-10 h-10 md:w-14 md:h-14 rounded-full bg-blue-500 text-white flex items-center justify-center font-medium text-base md:text-lg">
                                {{ getInitials(farmer) }}
                            </div>

                            <!-- Farmer Info -->
                            <div class="flex-1 text-left min-w-0">
                                <p
                                    class="font-medium text-base md:text-lg text-gray-900 truncate">
                                    {{ farmer.firstName }} {{ farmer.lastName }}
                                </p>
                                <p
                                    class="text-xs md:text-sm text-gray-500 truncate">
                                    {{ farmer.username || 'No RSBSA' }}
                                </p>
                                <p
                                    class="text-xs md:text-sm text-gray-500 truncate">
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
import { ref, computed, onMounted, nextTick, onBeforeUnmount } from 'vue'
import { Search, Send, User, ChevronLeft, Paperclip, X } from 'lucide-vue-next'
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

// Add disabled state for send button
const isSendDisabled = computed(() => {
    return (!messageInput.value.trim() && localFiles.value.length === 0) || isUploading.value
})

// Add new state for locally stored files
const localFiles = ref([])
const isUploading = ref(false)

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
        localFiles.value.push({
            file,
            name: file.name,
            type: file.type
        })
    }

    if (fileInput.value) {
        fileInput.value.value = '' 
    }
}

const removeFile = index => {
    if (localFiles.value[index]?.preview) {
        URL.revokeObjectURL(localFiles.value[index].preview)
    }
    localFiles.value.splice(index, 1)
}

const selectFarmer = async farmer => {
    try {
        selectedFarmer.value = farmer

        if (farmer && farmer.userId) {
            await Promise.all([
                messageStore.fetchMessages(authStore.userId, farmer.userId),
            ])
            scrollToBottom()
        }
    } catch (error) {
        console.error('Error selecting farmer:', error)
    }
}

// Update sendMessage to handle file uploads and exclude senderId
const sendChatMessage = async () => {
    if (!messageInput.value.trim() && !localFiles.value.length) return
    if (!selectedFarmer.value?.userId) return

    try {
        isUploading.value = true

        // First upload all files if any
        let attachments = []
        if (localFiles.value.length > 0) {
            try {
                const uploadPromises = localFiles.value.map(fileData =>
                    documentStore.uploadDocument(fileData.file)
                )
                const uploadResults = await Promise.all(uploadPromises)

                attachments = uploadResults.map(doc => ({
                    documentId: doc.documentId,
                    url: doc.preview
                }))
            } catch (error) {
                console.error('Error uploading files:', error)
                throw error
            }
        }

        await messageStore.sendMessage({
            receiverId: selectedFarmer.value.userId,
            text: messageInput.value || (attachments.length ? 'File Uploaded' : ''),
            type: 'FARMER_AGRICULTURE',
            attachments: attachments,
            sentAt: new Date().toISOString()
        })

        // Clear message and files
        messageInput.value = ''
        localFiles.value = []
        scrollToBottom()
    } catch (error) {
        console.error('Failed to send message:', error)
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

// Remove the utility functions and just keep the core functionality
const openAttachment = (url) => {
    if (url) {
        window.open(url, '_blank')
    }
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
</style>
