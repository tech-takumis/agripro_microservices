<template>
  <div class="bg-white rounded-xl shadow-lg border border-gray-200 p-6">
    <div class="flex items-center justify-between mb-6">
      <div>
        <h3 class="text-lg font-semibold text-gray-900">Applications Overview</h3>
        <p class="text-sm text-gray-500">Monthly application trends</p>
      </div>
      <div class="flex space-x-2">
        <button 
          v-for="period in periods" 
          :key="period"
          @click="selectedPeriod = period"
          :class="[
            'px-3 py-1 text-sm font-medium rounded-lg transition-colors',
            selectedPeriod === period 
              ? 'bg-indigo-100 text-indigo-700' 
              : 'text-gray-500 hover:text-gray-700'
          ]"
        >
          {{ period }}
        </button>
      </div>
    </div>
    <div class="h-80">
      <canvas ref="chartCanvas"></canvas>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, nextTick } from 'vue'
import { Chart, registerables } from 'chart.js'

Chart.register(...registerables)

const chartCanvas = ref(null)
let chartInstance = null
const selectedPeriod = ref('6M')

const periods = ['1M', '3M', '6M', '1Y']

const chartData = {
  '1M': {
    labels: ['Week 1', 'Week 2', 'Week 3', 'Week 4'],
    applications: [45, 52, 38, 61],
    approvals: [42, 48, 35, 58],
    rejections: [3, 4, 3, 3]
  },
  '3M': {
    labels: ['Month 1', 'Month 2', 'Month 3'],
    applications: [180, 195, 220],
    approvals: [165, 180, 205],
    rejections: [15, 15, 15]
  },
  '6M': {
    labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'],
    applications: [120, 135, 150, 165, 180, 195],
    approvals: [110, 125, 140, 155, 170, 185],
    rejections: [10, 10, 10, 10, 10, 10]
  },
  '1Y': {
    labels: ['Q1', 'Q2', 'Q3', 'Q4'],
    applications: [450, 520, 580, 650],
    approvals: [420, 485, 545, 615],
    rejections: [30, 35, 35, 35]
  }
}

const createChart = () => {
  if (!chartCanvas.value) return
  
  if (chartInstance) {
    chartInstance.destroy()
  }
  
  const data = chartData[selectedPeriod.value]
  const ctx = chartCanvas.value.getContext('2d')
  
  chartInstance = new Chart(ctx, {
    type: 'line',
    data: {
      labels: data.labels,
      datasets: [
        {
          label: 'Applications',
          data: data.applications,
          borderColor: '#4f46e5',
          backgroundColor: '#4f46e520',
          borderWidth: 3,
          fill: false,
          tension: 0.4
        },
        {
          label: 'Approved',
          data: data.approvals,
          borderColor: '#059669',
          backgroundColor: '#05966920',
          borderWidth: 3,
          fill: false,
          tension: 0.4
        },
        {
          label: 'Rejected',
          data: data.rejections,
          borderColor: '#dc2626',
          backgroundColor: '#dc262620',
          borderWidth: 3,
          fill: false,
          tension: 0.4
        }
      ]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          position: 'top',
          labels: {
            usePointStyle: true,
            padding: 20
          }
        }
      },
      scales: {
        y: {
          beginAtZero: true,
          grid: {
            color: '#f3f4f6'
          }
        },
        x: {
          grid: {
            color: '#f3f4f6'
          }
        }
      },
      elements: {
        point: {
          radius: 6,
          hoverRadius: 8
        }
      }
    }
  })
}

watch(selectedPeriod, () => {
  nextTick(() => {
    createChart()
  })
})

onMounted(() => {
  nextTick(() => {
    createChart()
  })
})
</script>
