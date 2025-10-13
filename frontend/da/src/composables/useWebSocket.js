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
    let reconnectTimeout = null;

    const connect = async () => {
        if (stompClient.value?.active) {
            console.log('[WebSocket] Already connected');
            return;
        }

        console.log('[WebSocket] Connecting to', url);

        try {
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
                },
                onStompError: (frame) => {
                    console.error('[WebSocket] ❌ STOMP error:', frame);
                    connected.value = false;
                    scheduleReconnect();
                },
                onDisconnect: () => {
                    connected.value = false;
                    console.warn('[WebSocket] 🔌 Disconnected from server.');
                    scheduleReconnect();
                },
                onWebSocketClose: (event) => {
                    connected.value = false;
                    console.warn('[WebSocket] 🔒 Closed:', event.reason);
                    scheduleReconnect();
                },
                onWebSocketError: (error) => {
                    console.error('[WebSocket] 🚨 WebSocket error:', error);
                    scheduleReconnect();
                },
            });

            await client.activate();
            stompClient.value = client;
        } catch (error) {
            console.error('[WebSocket] Connection error:', error);
            scheduleReconnect();
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

    const subscribe = (topic, callback) => {
        if (!connected.value || !stompClient.value) {
            console.warn('[WebSocket] Not connected, cannot subscribe:', topic);
            return null;
        }
        console.log('[WebSocket] Subscribing to', topic);
        try {
            return stompClient.value.subscribe(topic, (msg) => {
                try {
                    const payload = JSON.parse(msg.body);
                    callback(payload);
                } catch (e) {
                    console.error('[WebSocket] Failed to parse message:', e);
                    callback(msg.body); // Fallback to raw message
                }
            });
        } catch (error) {
            console.error('[WebSocket] Subscription error:', error);
            return null;
        }
    };

    const send = (destination, body) => {
        if (!connected.value || !stompClient.value) {
            console.error('[WebSocket] Not connected, cannot send message');
            throw new Error('WebSocket not connected');
        }

        try {
            stompClient.value.publish({
                destination: destination,
                body: typeof body === 'string' ? body : JSON.stringify(body)
            });
        } catch (error) {
            console.error('[WebSocket] Failed to send message:', error);
            throw error;
        }
    };

    // Auto-connect on mount
    onMounted(() => {
        connect();
    });

    // Clean up on unmount
    onUnmounted(() => {
        disconnect();
    });

    return {
        connected,
        connect,
        disconnect,
        subscribe,
        send,
        stompClient
    };
}