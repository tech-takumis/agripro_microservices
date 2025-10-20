import { defineStore } from "pinia"
import { computed, ref } from "vue"
import axios from "@/lib/axios"

export const usePostStore = defineStore("post", () => {
    // State
    const posts = ref([])
    const loading = ref(false)
    const error = ref(null)
    const hasMore = ref(true)
    const nextCursor = ref(null)

    // Getters as computed
    const allPosts = computed(() => posts.value)

    async function fetchPosts() {
        if (loading.value || !hasMore.value) return
        loading.value = true
        error.value = null

        const params = { limit: 10 }
        if (nextCursor.value) params.value = nextCursor.value

        try {
            const response = await axios.get("/api/v1/posts", { params })

            const { posts: newPosts, nextCursor: newCursor, hasMore: more } = response.data

            if (newPosts?.length) {
                posts.value.push(...newPosts)
            }

            nextCursor.value = newCursor
            hasMore.value = more
        } catch (err) {
            error.value = err.response?.data?.message || err.message || "Failed to fetch posts"
            console.error("Error fetching posts:", err)
        } finally {
            loading.value = false
        }
    }

    async function createPost(postData) {
        loading.value = true
        error.value = null

        try {
            const response = await axios.post("/api/v1/posts",
                postData)
            posts.value.unshift(response.data)
            return response.data
        } catch (err) {
            error.value = err.response?.data?.message || err.message || "Failed to create post"
            console.error("Error creating post:", err)
            throw err
        } finally {
            loading.value = false
        }
    }

    return {
        // State
        posts,
        loading,
        error,
        hasMore,
        nextCursor
        ,
        // Getters
        allPosts,
        // Actions
        fetchPosts,
        createPost,

    }
})
