// Example usage of the new notification system

import { useNotificationStore } from '@/stores/notification'

export function exampleUsage() {
    const notificationStore = useNotificationStore()

    // Success notification
    notificationStore.showSuccess('Operation completed successfully!')

    // Error notification with longer duration
    notificationStore.showError('Something went wrong. Please try again.', 8000)

    // Info notification
    notificationStore.showInfo('Here is some helpful information.')

    // Warning notification
    notificationStore.showWarning('Please review your input before proceeding.')

    // Custom notification
    notificationStore.addNotification('info', 'Custom message with specific duration', 3000)
}

// Example with auth store integration
import { useAuthStore } from '@/stores/auth'

export async function exampleAuthUsage() {
    const authStore = useAuthStore()
    const notificationStore = useNotificationStore()

    // Login example
    const loginResult = await authStore.login(credentials)
    if (loginResult.success) {
        notificationStore.showSuccess(loginResult.message)
    } else {
        notificationStore.showError(loginResult.message)
    }

    // Registration example
    const registerResult = await authStore.register(userData, token)
    if (registerResult.success) {
        notificationStore.showSuccess(registerResult.message)
        // Navigate to login page
    } else {
        notificationStore.showError(registerResult.message)
    }

    // Invite example
    const inviteResult = await authStore.inviteStaff(email)
    if (inviteResult.success) {
        notificationStore.showSuccess(inviteResult.message)
    } else {
        notificationStore.showError(inviteResult.message)
    }
}
