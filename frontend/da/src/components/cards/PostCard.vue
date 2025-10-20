<template>
    <div class="bg-white rounded-2xl shadow-md border border-gray-100 p-6">
        <!-- Create Post Section -->
        <div class="mb-6">
            <div class="flex items-center space-x-3 mb-4">
                <div class="p-2 bg-blue-50 rounded-lg">
                    <FileText class="h-6 w-6 text-blue-600" />
                </div>
                <h3 class="text-lg font-semibold text-blue-700">Posts</h3>
            </div>

            <!-- Post Creation Input -->
            <div class="bg-gray-50 rounded-lg p-4 border border-gray-200">
                <!-- Title Input -->
                <input
                    v-model="newTitle"
                    placeholder="Title"
                    class="w-full mb-2 p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 text-base"
                />

                <textarea
                    v-model="newPostContent"
                    placeholder="Share your thoughts or updates..."
                    class="w-full p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 resize-none"
                    rows="3"
                ></textarea>

                <div class="mt-3 flex items-center justify-between">
                    <div class="flex items-center space-x-2">
                        <label class="flex items-center cursor-pointer">
                            <input
                                type="file"
                                accept="image/*,video/*"
                                class="hidden"
                                multiple
                                @change="handleFileUpload"
                            />
                            <Paperclip class="h-5 w-5 text-gray-600 hover:text-blue-600" />
                        </label>
                        <!-- Show all selected files -->
                        <span v-if="selectedFile.length" class="text-xs text-gray-600">
                            <template v-for="file in selectedFile" :key="file.name">{{ file.name }} </template>
                        </span>
                    </div>
                    <button
                        class="bg-blue-600 hover:bg-blue-700 disabled:bg-gray-400 text-white px-4 py-2 rounded-lg text-sm font-medium transition-colors"
                        :disabled="!newPostContent.trim() || !newTitle.trim() || creatingPost"
                        @click="onCreatePost"
                    >
                        <span v-if="!creatingPost">Post</span>
                        <span v-else class="flex items-center space-x-2">
                            <Loader2 class="h-4 w-4 animate-spin" />
                            <span>Posting...</span>
                        </span>
                    </button>
                </div>
            </div>
        </div>

        <!-- Posts List -->
        <div class="space-y-4">
            <div
                v-for="post in posts"
                :key="post.id"
                class="border border-gray-200 rounded-lg p-4 hover:bg-gray-50 transition"
            >
                <div class="flex items-start justify-between mb-2">
                    <div class="flex items-center space-x-3">
                        <div class="h-10 w-10 rounded-full bg-blue-100 flex items-center justify-center">
                            <User class="h-5 w-5 text-blue-600" />
                        </div>
                        <div>
                            <p class="font-medium text-gray-900">{{ post.authorName || 'Anonymous' }}</p>
                            <p class="text-xs text-gray-500">{{ formatDate(post.createdAt) }}</p>
                        </div>
                    </div>
                    <MoreVertical class="h-5 w-5 text-gray-400 cursor-pointer hover:text-gray-600" />
                </div>

                <p class="text-gray-700 text-sm mb-3">{{ post.content }}</p>

                <div v-if="post.image" class="mb-3 rounded-lg overflow-hidden">
                    <img :src="post.image" :alt="post.content" class="w-full h-48 object-cover" />
                </div>

                <div class="flex items-center justify-between text-xs text-gray-600 pt-3 border-t border-gray-100">
                    <button class="flex items-center space-x-1 hover:text-blue-600">
                        <Heart class="h-4 w-4" />
                        <span>{{ post.likes || 0 }}</span>
                    </button>
                    <button class="flex items-center space-x-1 hover:text-blue-600">
                        <MessageCircle class="h-4 w-4" />
                        <span>{{ post.comments || 0 }}</span>
                    </button>
                    <button class="flex items-center space-x-1 hover:text-blue-600">
                        <Share2 class="h-4 w-4" />
                        <span>Share</span>
                    </button>
                </div>
            </div>

            <!-- Loading Trigger for Infinite Scroll -->
            <div
                v-if="hasMore"
                ref="loadTrigger"
                class="load-trigger py-4 text-center"
            >
                <div v-if="loading" class="flex items-center justify-center space-x-2">
                    <Loader2 class="h-5 w-5 animate-spin text-blue-600" />
                    <span class="text-sm text-gray-600">Loading more posts...</span>
                </div>
            </div>

            <!-- Empty State -->
            <div v-if="posts.length === 0 && !loading" class="text-center py-8">
                <FileText class="h-12 w-12 text-gray-300 mx-auto mb-3" />
                <p class="text-gray-500">No posts yet. Be the first to share!</p>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { usePostStore } from '@/stores/post'
import { FileText, Paperclip, Loader2, User, MoreVertical, Heart, MessageCircle, Share2 } from 'lucide-vue-next'

const postStore = usePostStore()
const loadTrigger = ref(null)
const newTitle = ref('')
const newPostContent = ref('')
const selectedFile = ref([])
const creatingPost = ref(false)

const props = defineProps({
  posts: {
    type: Array,
    default: () => []
  },
})

const loading = computed(() => postStore.loading)
const hasMore = computed(() => postStore.hasMore)

const handleFileUpload = async (event) => {
    const files = Array.from(event.target.files || [])
    if (!files.length) return
    selectedFile.value = files
}

// Use a wrapper to ensure console.log works and await createPost
const onCreatePost = async () => {
    await createPost()
}

const createPost = async () => {
    if (!(newPostContent.value.trim() && newTitle.value.trim())) return

    creatingPost.value = true
    try {
        const formData = new FormData()
        formData.append('title', newTitle.value)
        formData.append('content', newPostContent.value)
        // Append all selected files
        if (selectedFile.value && selectedFile.value.length) {
            selectedFile.value.forEach(file => {
                formData.append('files', file)
            })
        }

        for(const [key,value] of formData.entries()) {
            if(value instanceof File){
                console.log(`[POST CARD][FORM DATA] ${key}: File - name=${value.name}, size=${value.size}`)
            } else {
                console.log(`[POST CARD][FORM DATA] ${key}: ${value}`)
            }
        }
        await postStore.createPost(formData)
        newPostContent.value = ''
        newTitle.value = ''
        selectedFile.value = []
    } catch (error) {
        console.error('Error creating post:', error)
    } finally {
        creatingPost.value = false
    }
}

const formatDate = (date) => {
    if (!date) return ''
    const d = new Date(date)
    const now = new Date()
    const diff = now - d
    const minutes = Math.floor(diff / 60000)
    const hours = Math.floor(diff / 3600000)
    const days = Math.floor(diff / 86400000)

    if (minutes < 1) return 'just now'
    if (minutes < 60) return `${minutes}m ago`
    if (hours < 24) return `${hours}h ago`
    if (days < 7) return `${days}d ago`
    return d.toLocaleDateString()
}

let observer = null
let handleScroll = null

onMounted(async  () => {
    await postStore.fetchPosts()

    // IntersectionObserver for infinite scroll
    observer = new IntersectionObserver(
        (entries) => {
            const entry = entries[0]
            if (entry.isIntersecting && !loading.value && hasMore.value) {
                postStore.fetchPosts()
            }
        },
        { threshold: 0.1 }
    )

    if (loadTrigger.value) {
        observer.observe(loadTrigger.value)
    }

    // Fallback scroll listener
    handleScroll = () => {
        if (
            window.innerHeight + window.scrollY >= document.body.offsetHeight - 500 &&
            !loading.value &&
            hasMore.value
        ) {
            postStore.fetchPosts()
        }
    }

    window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
    if (handleScroll) {
        window.removeEventListener('scroll', handleScroll)
    }
    if (observer) {
        observer.disconnect()
    }
})
</script>
