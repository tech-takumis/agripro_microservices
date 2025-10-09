import { ref, onMounted, onUnmounted } from 'vue';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';

// Polyfill for SockJS (needed in Vite/Vue3)
if (typeof global === 'undefined') {
    window.global = window;
}

/**
 * useWebSocket composable
 * Handles connection, disconnection, subscription, and event dispatch.
 */
export function useWebSocket(url = 'http://localhost:9040/ws') {
    const stompClient = ref(null);
    const connected = ref(false);

    const connect = () => {

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
                // Subscribe to a topic after connection
                subscribe('/topic/public', (payload) => {
                    console.log('[WebSocket] Received:', payload);
                });
            },
            onStompError: (frame) => {
                console.error('[WebSocket] ❌ STOMP error:', frame);
                connected.value = false;
            },
            onDisconnect: () => {
                connected.value = false;
                console.warn('[WebSocket] 🔌 Disconnected from server.');
            },
            onWebSocketClose: (event) => {
                connected.value = false;
                console.warn('[WebSocket] 🔒 Closed:', event.reason);
            },
            onWebSocketError: (error) => {
                console.error('[WebSocket] 🚨 WebSocket error:', error);
            },
        });

        client.activate();
        stompClient.value = client;
    };

    const disconnect = () => {
        if (stompClient.value) {
            console.log('[WebSocket] Disconnecting...');
            stompClient.value.deactivate().then(() => {
                console.log('[WebSocket] Disconnected');
            });
            stompClient.value = null;
            connected.value = false;
        }
    };

    const subscribe = (topic, callback) => {
        if (!connected.value || !stompClient.value) {
            console.warn('[WebSocket] Not connected, cannot subscribe:', topic);
            return;
        }
        console.log('[WebSocket] Subscribing to', topic);
        return stompClient.value.subscribe(topic, (msg) => {
            try {
                const payload = JSON.parse(msg.body);
                callback(payload);
            } catch (e) {
                console.error('[WebSocket] Failed to parse message:', e);
            }
        });
    };

    // Auto-connect on mount
    onMounted(() => {
        connect();
    });

    // Auto-disconnect when composable owner is destroyed
    onUnmounted(() => {
        disconnect();
    });

    return {
        stompClient,
        connected,
        connect,
        disconnect,
        subscribe,
    };
}