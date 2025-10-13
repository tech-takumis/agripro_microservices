<template>
    <div v-if="hasRequiredPermission">
        <slot></slot>
    </div>
</template>

<script setup>
import { computed } from 'vue';
import { useAuthStore } from '@/stores/auth';

const props = defineProps({
    permission: {
        type: String,
        required: true
    }
});

const auth = useAuthStore();
const hasRequiredPermission = computed(() => auth.hasPermission(props.permission));
</script>
