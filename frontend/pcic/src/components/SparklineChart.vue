<template>
    <div class="w-full h-8 flex items-center justify-center">
        <!-- Simple placeholder sparkline -->
        <svg width="100" height="24">
            <polyline :points="points" fill="none" stroke="currentColor" stroke-width="2" />
        </svg>
    </div>
</template>
<script>
export default {
    name: 'SparklineChart',
    props: {
        data: {
            type: Array,
            required: true
        },
        positive: {
            type: Boolean,
            default: true
        }
    },
    computed: {
        points() {
            // Simple linear mapping for placeholder
            const max = Math.max(...this.data);
            const min = Math.min(...this.data);
            return this.data.map((v, i) => `${i * 15},${24 - ((v - min) / (max - min || 1)) * 20}`).join(' ');
        }
    }
};
</script>
<style scoped>
svg {
    color: #16a34a;
}
</style>