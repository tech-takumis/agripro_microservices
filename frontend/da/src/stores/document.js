import {defineStore} from 'pinia'
import {computed, ref} from 'vue'
import axios from "@/lib/axios"


export const useDocumentStore = defineStore('document', () => {
    const documents = ref([])
    const loading = ref(false)
    const error = ref(null)
    // error.value = undefined
    const previewUrl = ref(null)
    const downloadUrl = ref(null)
    const basePath = ref('/api/v1/documents')

    const allDocuments = computed(() => documents.value)
    const isLoading = computed(() => loading.value)
    const getPreviewUrl = computed(() => previewUrl.value)
    const getDownloadURl = computed(() => downloadUrl.value)
    const hasError = computed(() => error.value !== null)


    const fetchDocumentPreviewUrl = async (documentId) => {
        try{
            loading.value = true
            error.value = null
            previewUrl.value = null

            const url = await axios.get(`${basePath.value}/${documentId}/preview`)

            if(url.status === 200){
                previewUrl.value = url.data

                return {success: "true", message: "Preview URL fetched successfully", data: previewUrl.value}
            }

            return {success: "false", message: "Failed to fetch preview URL", data: null}

        }catch (error){
            console.log("Error fetching document preview URL:", error)
            error.value = error.data
            return {success: error.success, message: error.message, data: null}
        }finally {
            loading.value = false
        }

    }

    const fetchDownloadUrl = async (documentId) => {
        try{
            loading.value = true
            error.value = null
            downloadUrl.value = null

            const url = await axios.get(`${basePath.value}/${documentId}/download-url`)

            if(url.status === 200){
                downloadUrl.value = url.data
                loading.value = false
                error.value = null
                return {success: "true", message: "Download URL fetched successfully", data: downloadUrl.value}
            }

            return {success: "false", message: "Failed to fetch download URL", data: null}

        }catch (error){
            console.log("Error fetching document download URL:", error)
            error.value = error.data
            return {success: error.success, message: error.message, data: null}
        }finally {
            loading.value = false
            error.value = null
        }
    }

    const downloadDocument = async (documentId) => {
        try{
            loading.value = true
            error.value = null

            const response = await  axios.get(`${basePath.value}/${documentId}/download`, {
                responseType: 'blob'
            })

            if(response.status === 200){
                // Extract filename from Content-Disposition header or use document ID as fallback
                let filename = `document-${documentId}`

                const contentDisposition = response.headers['content-disposition']
                if (contentDisposition) {
                    const filenameMatch = contentDisposition.match(/filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/)
                    if (filenameMatch && filenameMatch[1]) {
                        filename = filenameMatch[1].replace(/['"]/g, '')
                    }
                }

                // If no filename from header, try to determine extension from content-type
                if (!filename.includes('.')) {
                    const contentType = response.headers['content-type']
                    if (contentType) {
                        if (contentType.includes('image/jpeg') || contentType.includes('image/jpg')) {
                            filename += '.jpg'
                        } else if (contentType.includes('image/png')) {
                            filename += '.png'
                        } else if (contentType.includes('image/gif')) {
                            filename += '.gif'
                        } else if (contentType.includes('application/pdf')) {
                            filename += '.pdf'
                        } else if (contentType.includes('text/plain')) {
                            filename += '.txt'
                        } else if (contentType.includes('application/msword')) {
                            filename += '.doc'
                        } else if (contentType.includes('application/vnd.openxmlformats-officedocument.wordprocessingml.document')) {
                            filename += '.docx'
                        }
                        // Add more content-type mappings as needed
                    }
                }

                const url = window.URL.createObjectURL(new Blob([response.data]))
                const link = document.createElement('a')
                link.href = url
                link.setAttribute('download', filename)
                document.body.appendChild(link)
                link.click()
                link.remove()
                window.URL.revokeObjectURL(url)

                return {success: "true", message: "Document downloaded successfully", data: null}
            }
            return {success: "false", message: "Failed to download document", data: null}
        }catch (error){
            console.log("Error downloading document:", error)
            error.value = error.data
            return {success: error.success, message: error.message, data: null}
        }finally {
            loading.value = false
            error.value = null
        }
    }

    return {
        allDocuments,
        isLoading,
        hasError,
        getPreviewUrl,
        getDownloadURl,

        downloadDocument,
        fetchDocumentPreviewUrl,
        fetchDownloadUrl
    }


})