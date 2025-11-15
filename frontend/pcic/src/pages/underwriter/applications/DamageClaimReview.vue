<template>
  <AuthenticatedLayout
    :navigation="underwriterNavigation"
    role-title="Underwriter Portal"
    page-title="Damage Claim Review"
  >
    <template #header>
      <div class="flex items-start justify-between w-full">
        <div>
          <h1 class="text-2xl font-bold text-gray-900">Damage Claim Review</h1>
          <p class="text-sm text-gray-500 mt-1">
            Submitted by <span class="font-semibold text-gray-700">John Farmer</span> — May 1, 2025
          </p>
        </div>

        <div class="flex items-center space-x-3">
          <button
            class="px-4 py-2 border border-gray-300 rounded-lg text-sm font-medium text-gray-700 bg-white hover:bg-gray-100 transition"
          >
            Flag Issue
          </button>
          <button
            class="px-4 py-2 rounded-lg text-sm font-medium text-white bg-green-600 hover:bg-green-700 transition"
          >
            Approve
          </button>
        </div>
      </div>
    </template>

    <div class="space-y-10 overflow-x-hidden">
      <!-- Section: AI Damage Analysis -->
      <section class="grid grid-cols-1 lg:grid-cols-3 gap-8 items-start">
        <!-- AI Image + Confidence -->
        <div class="lg:col-span-2 bg-white border border-gray-200 rounded-2xl shadow-sm p-6">
          <img
            src="@/assets/rice.jpg"
            alt="AI analysis inspection on damaged crop"
            class="w-full h-80 object-cover rounded-xl mb-6 shadow-sm"
          />

          <h4 class="text-gray-800 font-semibold mb-4">
            Primary Classification:
            <span class="text-green-600 font-bold">Pest Damage</span>
          </h4>

          <!-- Confidence Bars -->
          <div class="space-y-4">
            <div>
              <p class="text-sm font-medium text-gray-700 mb-1">Pest Damage</p>
              <div class="w-full bg-gray-200 rounded-full h-2">
                <div class="bg-green-600 h-2 rounded-full" style="width: 92%"></div>
              </div>
            </div>

            <div>
              <p class="text-sm font-medium text-gray-700 mb-1">Disease</p>
              <div class="w-full bg-gray-200 rounded-full h-2">
                <div class="bg-yellow-400 h-2 rounded-full" style="width: 5%"></div>
              </div>
            </div>

            <div>
              <p class="text-sm font-medium text-gray-700 mb-1">Weather Damage</p>
              <div class="w-full bg-gray-200 rounded-full h-2">
                <div class="bg-blue-400 h-2 rounded-full" style="width: 3%"></div>
              </div>
            </div>
          </div>
        </div>

        <!-- Supporting Documents -->
        <aside class="space-y-5">
          <h3 class="text-lg font-semibold text-gray-900">Supporting Documents</h3>

          <!-- Verified Document -->
          <div class="border border-gray-200 rounded-xl p-5 bg-white shadow-sm hover:shadow-md transition">
            <div class="flex justify-between items-center mb-2">
              <p class="font-medium text-gray-800">Field_Damage_1.jpg</p>
              <span class="text-green-600 text-sm font-semibold">Verified</span>
            </div>
            <p class="text-sm text-gray-500 mb-2">Pest Damage — 92% Confidence</p>
            <a href="#" class="text-blue-600 text-sm hover:underline font-medium">View</a>
          </div>

          <!-- Pending Document -->
          <div class="border border-gray-200 rounded-xl p-5 bg-white shadow-sm hover:shadow-md transition">
            <div class="flex justify-between items-center mb-2">
              <p class="font-medium text-gray-800">Inspection_Report.pdf</p>
              <span class="text-yellow-600 text-sm font-semibold">Pending Review</span>
            </div>
            <p class="text-sm text-gray-500 mb-2">Manual inspection required</p>
            <a href="#" class="text-blue-600 text-sm hover:underline font-medium">View</a>
          </div>

          <!-- Action Buttons -->
          <div class="flex flex-col space-y-3 pt-4">
            <button
              class="w-full bg-green-600 text-white py-2 rounded-md text-sm font-semibold hover:bg-green-700 transition"
            >
              Accept AI Analysis
            </button>
            <button
              class="w-full border border-gray-300 bg-white text-gray-700 py-2 rounded-md text-sm font-medium hover:bg-gray-100 transition"
            >
              Override Classification
            </button>
            <button
              class="w-full border border-gray-300 bg-white text-gray-700 py-2 rounded-md text-sm font-medium hover:bg-gray-100 transition"
            >
              Flag for Manual Review
            </button>
          </div>
        </aside>
      </section>

      <!-- Section: Additional Information -->
      <section class="bg-white border border-gray-200 rounded-2xl p-6 shadow-sm">
        <h3 class="text-lg font-semibold text-gray-900 mb-6">Additional Information</h3>

        <div class="grid grid-cols-1 md:grid-cols-2 gap-8">
          <!-- Claim Details -->
          <div>
            <h4 class="text-sm font-medium text-gray-500 mb-2">Claim Details</h4>
            <ul class="space-y-1 text-sm text-gray-800">
              <li><span class="font-semibold text-gray-700">Claim Type:</span> Crop Damage</li>
              <li><span class="font-semibold text-gray-700">Affected Area:</span> 12.5 acres</li>
              <li><span class="font-semibold text-gray-700">Estimated Loss:</span> $25,000</li>
            </ul>
          </div>

          <!-- Timeline -->
          <div>
            <h4 class="text-sm font-medium text-gray-500 mb-2">Timeline</h4>
            <ul class="space-y-2 text-sm text-gray-800">
              <li><span class="font-semibold text-gray-700">Claim Submitted:</span> May 1, 2025, 10:30 AM</li>
              <li><span class="font-semibold text-gray-700">AI Analysis Completed:</span> May 1, 2025, 10:32 AM</li>
            </ul>
          </div>
        </div>
      </section>
    </div>
  </AuthenticatedLayout>
</template>

<script setup>
import AuthenticatedLayout from '@/layouts/AuthenticatedLayout.vue'
import { UNDERWRITER_NAVIGATION } from '@/lib/navigation.js'
import { X } from 'lucide-vue-next'

const underwriterNavigation = UNDERWRITER_NAVIGATION
</script>

<style>
/* Hide scrollbar for Chrome, Safari and Opera */
.overflow-y-auto::-webkit-scrollbar {
  display: none;
}

/* Hide scrollbar for IE, Edge and Firefox */
.overflow-y-auto {
  -ms-overflow-style: none;  /* IE and Edge */
  scrollbar-width: none;  /* Firefox */
}
</style>
