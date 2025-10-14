import { defineStore } from 'pinia'
import axios from '@/lib/axios'

export const useDocumentStore = defineStore('document', {
    state: () => ({
        selectedFiles: [],
        isUploading: false,
        uploadProgress: 0,
        documents: [], // Add documents array to store all documents
        currentDocument: null // Add currentDocument to store single document details
    }),

    getters: {
        getFiles: (state) => state.selectedFiles,
        getUploadProgress: (state) => state.uploadProgress,
        getUploadStatus: (state) => state.isUploading,
        getAllDocuments: (state) => state.documents,
        getCurrentDocument: (state) => state.currentDocument
    },

    actions: {
        async uploadDocument(file) {
            try {
                this.isUploading = true
                const formData = new FormData()
                formData.append('file', file)

                const response = await axios.post('/api/v1/documents', formData, {
                    headers: {
                        'Content-Type': 'multipart/form-data'
                    },
                    onUploadProgress: (progressEvent) => {
                        this.uploadProgress = Math.round(
                            (progressEvent.loaded * 100) / progressEvent.total
                        )
                    }
                })
                return response.data
            } catch (error) {
                console.error('Error uploading document:', error)
                throw error
            } finally {
                this.isUploading = false
                this.uploadProgress = 0
            }
        },

        addSelectedFile(documentInfo) {
            if (!documentInfo) return

            // Create a new array to ensure reactivity is triggered
            const newFiles = Array.isArray(this.selectedFiles) ? [...this.selectedFiles] : []
            newFiles.push({
                documentId: documentInfo.documentId,
                preview: documentInfo.preview,
                name: documentInfo.name
            })

            // Reassign to trigger reactivity
            this.selectedFiles = newFiles
        },

        removeSelectedFile(documentId) {
            this.selectedFiles = this.selectedFiles.filter(file => file.documentId !== documentId)
        },

        async fetchAllDocuments() {
            try {
                const response = await axios.get('/api/v1/documents')
                this.documents = response.data
                return response.data
            } catch (error) {
                console.error('Error fetching documents:', error)
                throw error
            }
        },

        async getDocumentPreview(documentId) {
            try {
                const response = await axios.get(`/api/v1/documents/${documentId}/preview`, {
                    responseType: 'blob'
                })
                return URL.createObjectURL(response.data)
            } catch (error) {
                console.error('Error fetching document preview:', error)
                throw error
            }
        },

        async getDocumentById(documentId) {
            try {
                const response = await axios.get(`/api/v1/documents/${documentId}`)
                this.currentDocument = response.data
                return response.data
            } catch (error) {
                console.error('Error fetching document:', error)
                throw error
            }
        },

        async getDocumentsByUser(userId) {
            try {
                const response = await axios.get(`/api/v1/documents/user/${userId}`)
                return response.data
            } catch (error) {
                console.error('Error fetching user documents:', error)
                throw error
            }
        },

        async deleteDocument(documentId) {
            try {
                await axios.delete(`/api/v1/documents/document-id/${documentId}`)
                // Remove from documents array if it exists
                this.documents = this.documents.filter(doc => doc.documentId !== documentId)
                // Clear currentDocument if it's the one being deleted
                if (this.currentDocument?.documentId === documentId) {
                    this.currentDocument = null
                }
                // Remove from selectedFiles if present
                this.selectedFiles = this.selectedFiles.filter(file => file.documentId !== documentId)
            } catch (error) {
                console.error('Error deleting document:', error)
                throw error
            }
        },

        clearSelectedFiles() {
            this.selectedFiles = []
        }
    }
})
