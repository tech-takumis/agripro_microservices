import { defineStore } from 'pinia'
import axios from '@/lib/axios'
import { ref, computed } from 'vue'

export const useDocumentStore = defineStore('document', () => {
    // State
    const selectedFiles = ref([])
    const isUploading = ref(false)
    const uploadProgress = ref(0)
    const documents = ref([])
    const currentDocument = ref(null)

    // Getters as computed
    const getFiles = computed(() => selectedFiles.value)
    const getUploadProgress = computed(() => uploadProgress.value)
    const getUploadStatus = computed(() => isUploading.value)
    const getAllDocuments = computed(() => documents.value)
    const getCurrentDocument = computed(() => currentDocument.value)

    // Actions as functions
    async function uploadDocument(file) {
        try {
            isUploading.value = true
            const formData = new FormData()
            formData.append('file', file)

            const response = await axios.post('/api/v1/documents', formData, {
                headers: {
                    'Content-Type': 'multipart/form-data'
                },
                onUploadProgress: (progressEvent) => {
                    uploadProgress.value = Math.round(
                        (progressEvent.loaded * 100) / progressEvent.total
                    )
                }
            })
            return response.data
        } catch (error) {
            console.error('Error uploading document:', error)
            throw error
        } finally {
            isUploading.value = false
            uploadProgress.value = 0
        }
    }

    function addSelectedFile(documentInfo) {
        if (!documentInfo) return

        // Create a new array to ensure reactivity is triggered
        const newFiles = Array.isArray(selectedFiles.value) ? [...selectedFiles.value] : []
        newFiles.push({
            documentId: documentInfo.documentId,
            preview: documentInfo.preview,
            name: documentInfo.name
        })

        // Reassign to trigger reactivity
        selectedFiles.value = newFiles
    }

    function removeSelectedFile(documentId) {
        selectedFiles.value = selectedFiles.value.filter(file => file.documentId !== documentId)
    }

    async function fetchAllDocuments() {
        try {
            const response = await axios.get('/api/v1/documents')
            documents.value = response.data
            return response.data
        } catch (error) {
            console.error('Error fetching documents:', error)
            throw error
        }
    }

    async function getDocumentPreview(documentId) {
        try {
            const response = await axios.get(`/api/v1/documents/${documentId}/preview`, {
                responseType: 'blob'
            })
            return URL.createObjectURL(response.data)
        } catch (error) {
            console.error('Error fetching document preview:', error)
            throw error
        }
    }

    async function getDocumentById(documentId) {
        try {
            const response = await axios.get(`/api/v1/documents/${documentId}`)
            currentDocument.value = response.data
            return response.data
        } catch (error) {
            console.error('Error fetching document:', error)
            throw error
        }
    }

    async function getDocumentsByUser(userId) {
        try {
            const response = await axios.get(`/api/v1/documents/user/${userId}`)
            return response.data
        } catch (error) {
            console.error('Error fetching user documents:', error)
            throw error
        }
    }

    async function deleteDocument(documentId) {
        try {
            await axios.delete(`/api/v1/documents/document-id/${documentId}`)
            // Remove from documents array if it exists
            documents.value = documents.value.filter(doc => doc.documentId !== documentId)
            // Clear currentDocument if it's the one being deleted
            if (currentDocument.value?.documentId === documentId) {
                currentDocument.value = null
            }
            // Remove from selectedFiles if present
            selectedFiles.value = selectedFiles.value.filter(file => file.documentId !== documentId)
        } catch (error) {
            console.error('Error deleting document:', error)
            throw error
        }
    }

    return {
        // State
        selectedFiles,
        isUploading,
        uploadProgress,
        documents,
        currentDocument,

        // Getters
        getFiles,
        getUploadProgress,
        getUploadStatus,
        getAllDocuments,
        getCurrentDocument,

        // Actions
        uploadDocument,
        addSelectedFile,
        removeSelectedFile,
        fetchAllDocuments,
        getDocumentPreview,
        getDocumentById,
        getDocumentsByUser,
        deleteDocument
    }
})
