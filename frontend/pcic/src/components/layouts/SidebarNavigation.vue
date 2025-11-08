<template>
<!-- Logo / Header -->
<div class="flex flex-col items-center mb-5 mt-2 px-4 py-4 bg-green-600 border-b border-white">
  <!-- Logo -->
  <img
    src="@/assets/pcic.svg"
    alt="PCIC Logo"
    class="h-12 w-10 mb-2"
  />

  <!-- Title -->
  <h1 class="text-2xl font-bold text-white leading-tight">PCIC</h1>

  <!-- Role Badge -->
  <span
    class="inline-block bg-yellow-100 text-yellow-800 text-xs font-semibold px-3 py-1 rounded-full mb-2 mt-2"
  >
    {{ roleTitle }}
  </span>
</div>


    <!-- Navigation -->
    <nav class="flex-1 px-2 space-y-1 bg-green-600">
      <template v-for="item in navigation" :key="item.name || item.title">
        <!-- Single Navigation Item -->
        <router-link
          v-if="!item.children && item.to"
          :to="item.to"
          :class="[
            isActive(item.to)
              ? 'bg-green-600 text-white font-semibold border-r-4 border-green-300'
              : 'text-white hover:bg-green-700 hover:text-white',
            'group flex items-center px-3 py-2 text-sm rounded-md transition-all'
          ]"
        >
          <component 
            :is="item.icon" 
            :class="[
              isActive(item.to) ? 'text-white' : 'text-white group-hover:text-yellow-300',
              'mr-3 h-5 w-5'
            ]" 
          />
          {{ item.title || item.name }}
        </router-link>
        <span v-else-if="!item.children && !item.to" class="group flex items-center px-3 py-2 text-sm rounded-md transition-all text-green-100 opacity-50 cursor-not-allowed">
          <component :is="item.icon" class="mr-3 h-5 w-5 text-green-200" />
          {{ item.title || item.name }}
        </span>

        <!-- Navigation Group with Children -->
        <div v-else>
          <button
            @click="toggleGroup(item.title || item.name)"
            :class="[
              'text-white hover:bg-green-700 hover:text-white',
              'group w-full flex items-center justify-between px-3 py-2 text-sm rounded-md transition-all'
            ]"
          >
            <div class="flex items-center">
              <component 
                :is="item.icon" 
                class="text-white group-hover:text-yellow-300 mr-3 h-5 w-5" 
              />
              {{ item.title || item.name }}
            </div>
            <ChevronDown 
              :class="[
                expandedGroups.includes(item.title || item.name) ? 'rotate-180 text-white' : 'text-white',
                'h-4 w-4 transition-transform'
              ]"
            />
          </button>
          <!-- Submenu with Tree Lines -->
          <div
            v-show="expandedGroups.includes(item.title || item.name)"
            class="mt-1 pl-6 border-l border-green-600"
          >
            <template v-for="child in getChildren(item)" :key="child.name || child.title">
              <router-link
                v-if="child.to"
                :to="child.to"
                class="group relative flex items-center pl-6 pr-3 py-2 text-sm rounded-md transition-all"
                :class="isActive(child.to)
                  ? 'bg-green-700 text-white'
                  : 'text-white hover:bg-green-600 hover:text-yellow-300'"
              >
                <span
                  :class="[
                    'absolute -left-4 top-1/2 -translate-y-1/2 w-4 border-t',
                    isActive(child.to) ? 'border-green-400' : 'border-green-700 group-hover:border-green-500'
                  ]"
                ></span>
                {{ child.title || child.name }}
              </router-link>
              <span v-else class="group relative flex items-center pl-6 pr-3 py-2 text-sm rounded-md transition-all text-green-100 opacity-50 cursor-not-allowed">
                {{ child.title || child.name }}
              </span>
            </template>
          </div>
        </div>
      </template>
    </nav>

<!-- User Profile Section -->
<div class="flex-shrink-0 border-t border-white px-4 py-3 bg-green-600">
  <button
    @click="$emit('logout')"
    class="group flex w-full px-4 py-3 text-sm font-semibold bg-white text-red-600 rounded-lg border-2 border-none transition-all duration-200 hover:bg-red-500 hover:border-red-100 hover:text-white hover:shadow-md active:scale-95"
    title="Logout"
  >
    <LogOut class="h-4 w-4 mr-2" />
    <span>Logout</span>
  </button>
</div>

</template>


<script setup>
import { ref } from 'vue'
import { useRoute } from 'vue-router'
import { ChevronDown, LogOut } from 'lucide-vue-next'

const route = useRoute()

const props = defineProps({
  navigation: {
    type: Array,
    default: () => []
  },
  roleTitle: {
    type: String,
    default: 'Staff Portal'
  },
  userFullName: {
    type: String,
    default: ''
  },
  userEmail: {
    type: String,
    default: ''
  },
  userInitials: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['logout'])

const expandedGroups = ref([])

const isActive = (href) => {
  return route.path === href || route.path.startsWith(href + '/')
}

const toggleGroup = (groupName) => {
  const index = expandedGroups.value.indexOf(groupName)
  if (index > -1) {
    expandedGroups.value.splice(index, 1)
  } else {
    expandedGroups.value.push(groupName)
  }
}

function getChildren(item) {
  return Array.isArray(item.children) ? item.children : [];
}
</script>
