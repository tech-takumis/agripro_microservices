<template>
  <button
    class="quick-action-button"
    @click="handleClick"
    :disabled="disabled"
  >
    <div class="quick-action-button__icon">
      <component :is="icon" v-if="typeof icon === 'function'"/>
      <component :is="icon" v-else-if="typeof icon === 'object'"/>
      <span v-else>{{ icon }}</span>
    </div>
    <div class="quick-action-button__label">
      {{ label }}
    </div>
  </button>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue';

const props = defineProps({
  label: {
    type: String,
    required: true,
  },
  icon: [Object, Function],
  onClick: {
    type: Function,
    required: true,
  },
  disabled: {
    type: Boolean,
    default: false,
  },
});

const emit = defineEmits(['click']);

const handleClick = () => {
  if (!props.disabled) {
    props.onClick();
    emit('click');
  }
};
</script>

<style scoped>
.quick-action-button {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 8px 12px;
  border: 1px solid #ccc;
  border-radius: 4px;
  background-color: #fff;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.quick-action-button:hover {
  background-color: #f0f0f0;
}

.quick-action-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.quick-action-button__icon {
  margin-bottom: 4px;
}

.quick-action-button__label {
  font-size: 12px;
}
</style>
