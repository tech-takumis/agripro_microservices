<template>
    <div v-if="hasRequiredRole">
        <slot></slot>
    </div>
</template>

<script setup>
import { computed } from 'vue';
import { useAuthStore } from '@/stores/auth';

const props = defineProps({
    role: {
        type: String,
        required: true
    }
});

const auth = useAuthStore();
const hasRequiredRole = computed(() => auth.hasRole(props.role));
</script>
