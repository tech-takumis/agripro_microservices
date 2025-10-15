import { ref, onMounted, onUnmounted } from 'vue';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';

// Polyfill for SockJS (needed in Vite/Vue3)
if (typeof global === 'undefined') {
    window.global = window;
}

// Singleton instance
let instance = null;

/**
 * useWebSocket composable with singleton pattern
 * Handles connection, disconnection, subscription, and event dispatch.
 */
export function useWebSocket(url = 'http://localhost:9040/ws') {
    if (instance) {
        return instance;
    }

    const stompClient = ref(null);
    const connected = ref(false);
    let reconnectTimeout = null;
    let connectionPromise = null;

    const connect = async () => {
        // If already connecting, return the existing promise
        if (connectionPromise) {
            return connectionPromise;
        }

        // If already connected, return immediately
        if (stompClient.value?.active) {
            console.log('[WebSocket] Already connected');
            return Promise.resolve();
        }

        // Create new connection promise
        connectionPromise = new Promise((resolve, reject) => {
            try {
                console.log('[WebSocket] Connecting to', url);

                const socket = new SockJS(url, null, {
                    withCredentials: true,
                });

                const client = new Client({
                    webSocketFactory: () => socket,
                    debug: (str) => console.log('[WebSocket Debug]', str),
                    reconnectDelay: 5000,
                    heartbeatIncoming: 10000,
                    heartbeatOutgoing: 10000,
                    onConnect: () => {
                        connected.value = true;
                        console.log('[WebSocket] ✅ Connected to server.');
                        connectionPromise = null;
                        resolve();
                    },
                    onStompError: (frame) => {
                        console.error('[WebSocket] ❌ STOMP error:', frame);
                        connected.value = false;
                        connectionPromise = null;
                        scheduleReconnect();
                        reject(new Error('STOMP connection error'));
                    },
                    onDisconnect: () => {
                        connected.value = false;
                        connectionPromise = null;
                        console.warn('[WebSocket] 🔌 Disconnected from server.');
                        scheduleReconnect();
                    },
                });

                client.activate();
                stompClient.value = client;
            } catch (error) {
                console.error('[WebSocket] Connection error:', error);
                connectionPromise = null;
                reject(error);
            }
        });

        return connectionPromise;
    };

    const waitForConnection = async (timeout = 5000) => {
        const start = Date.now();
        while (!stompClient.value?.active) {
            if (Date.now() - start > timeout) {
                throw new Error('WebSocket connection timeout');
            }
            await new Promise(resolve => setTimeout(resolve, 100));
        }
    };

    const scheduleReconnect = () => {
        if (reconnectTimeout) {
            clearTimeout(reconnectTimeout);
        }
        reconnectTimeout = setTimeout(() => {
            console.log('[WebSocket] Attempting to reconnect...');
            connect();
        }, 5000);
    };

    const disconnect = async () => {
        if (reconnectTimeout) {
            clearTimeout(reconnectTimeout);
            reconnectTimeout = null;
        }

        if (stompClient.value) {
            console.log('[WebSocket] Disconnecting...');
            try {
                await stompClient.value.deactivate();
                console.log('[WebSocket] Disconnected');
            } catch (error) {
                console.error('[WebSocket] Error during disconnect:', error);
            }
            stompClient.value = null;
            connected.value = false;
        }
    };

    // Create the singleton instance
    instance = {
        stompClient,
        connected,
        connect,
        waitForConnection,
        disconnect
    };

    // Auto-connect on mount
    onMounted(() => {
        connect();
    });

    // Clean up on unmount
    onUnmounted(() => {
        if (reconnectTimeout) {
            clearTimeout(reconnectTimeout);
        }
        if (stompClient.value?.active) {
            stompClient.value.deactivate();
        }
    });

    return instance;
}