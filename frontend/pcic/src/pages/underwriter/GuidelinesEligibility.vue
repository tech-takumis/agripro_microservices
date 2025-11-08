<template>
  <div class="flex flex-col items-center justify-center h-full p-6 text-center">
    <!-- Icon -->
    <svg
      class="w-16 h-16 text-gray-400 mb-4"
      fill="none"
      stroke="currentColor"
      stroke-width="2"
      viewBox="0 0 24 24"
    >
      <path stroke-linecap="round" stroke-linejoin="round"
        d="M13 16h-1v-4h-1m0-4h.01M12 2a10 10 0 100 20 10 10 0 000-20z" />
    </svg>

    <!-- Title -->
    <h1 class="text-2xl font-bold text-gray-800 mb-2">Page Not Available</h1>
    
    <!-- Description -->
    <p class="text-gray-500 mb-4">
      The Underwriter Guidelines & Eligibility page is currently unavailable. 
      Please check back later.
    </p>

    <!-- Optional button -->
    <router-link
      to="/dashboard"
      class="inline-flex items-center px-4 py-2 bg-green-600 text-white rounded-md hover:bg-green-700 transition"
    >
      Go Back to Dashboard
    </router-link>
  </div>
</template>