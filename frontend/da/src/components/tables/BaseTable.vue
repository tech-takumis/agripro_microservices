<template>
    <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
            <thead class="bg-gray-50">
            <slot name="header" />
            </thead>
            <tbody class="bg-white divide-y divide-gray-200">
            <slot name="body" />
            </tbody>
        </table>
    </div>
</template>

<script setup>
// Pure UI component - no props needed for basic table structure
</script>
