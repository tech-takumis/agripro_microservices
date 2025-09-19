<template>
  <div class="bg-white rounded-xl shadow-lg border border-gray-200 p-6">
    <div class="flex items-center justify-between mb-6">
      <div>
        <h3 class="text-lg font-semibold text-gray-900">User Activity</h3>
        <p class="text-sm text-gray-500">Daily active users by role</p>
      </div>
      <div class="flex items-center space-x-4">
        <div class="flex items-center space-x-2">
          <div class="w-3 h-3 bg-indigo-500 rounded-full"></div>
          <span class="text-sm text-gray-600">Admins</span>
        </div>
        <div class="flex items-center space-x-2">
          <div class="w-3 h-3 bg-green-500 rounded-full"></div>
          <span class="text-sm text-gray-600">Underwriters</span>
        </div>
        <div class="flex items-center space-x-2">
          <div class="w-3 h-3 bg-yellow-500 rounded-full"></div>
          <span class="text-sm text-gray-600">Tellers</span>
        </div>
        <div class="flex items-center space-x-2">
          <div class="w-3 h-3 bg-purple-500 rounded-full"></div>
          <span class="text-sm text-gray-600">Claims Processors</span>
        </div>
      </div>
    </div>
    <div class="h-80">
      <canvas ref="chartCanvas"></canvas>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { Chart, registerables } from 'chart.js'

Chart.register(...registerables)

const chartCanvas = ref(null)
let chartInstance = null

const data = {
  labels: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
  admins: [8, 12, 10, 15, 18, 5, 3],
  underwriters: [25, 30, 28, 35, 40, 15, 10],
  tellers: [45, 50, 48, 55, 60, 25, 20],
  claimsProcessors: [20, 25, 22, 30, 35, 12, 8]
}

const createChart = () => {
  if (!chartCanvas.value) return
  
  if (chartInstance) {
    chartInstance.destroy()
  }
  
  const ctx = chartCanvas.value.getContext('2d')
  
  chartInstance = new Chart(ctx, {
    type: 'bar',
    data: {
      labels: data.labels,
      datasets: [
        {
          label: 'Admins',
          data: data.admins,
          backgroundColor: '#4f46e5',
          borderColor: '#4f46e5',
          borderWidth: 1
        },
        {
          label: 'Underwriters',
          data: data.underwriters,
          backgroundColor: '#059669',
          borderColor: '#059669',
          borderWidth: 1
        },
        {
          label: 'Tellers',
          data: data.tellers,
          backgroundColor: '#d97706',
          borderColor: '#d97706',
          borderWidth: 1
        },
        {
          label: 'Claims Processors',
          data: data.claimsProcessors,
          backgroundColor: '#7c3aed',
          borderColor: '#7c3aed',
          borderWidth: 1
        }
      ]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          display: false
        }
      },
      scales: {
        x: {
          stacked: true,
          grid: {
            color: '#f3f4f6'
          }
        },
        y: {
          stacked: true,
          beginAtZero: true,
          grid: {
            color: '#f3f4f6'
          }
        }
      }
    }
  })
}

onMounted(() => {
  nextTick(() => {
    createChart()
  })
})
</script>
