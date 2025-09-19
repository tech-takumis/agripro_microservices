<template>
  <div class="bg-white rounded-xl shadow-lg border border-gray-200 p-6">
    <div class="flex items-center justify-between mb-6">
      <div>
        <h3 class="text-lg font-semibold text-gray-900">System Health</h3>
        <p class="text-sm text-gray-500">Performance metrics over time</p>
      </div>
      <div class="flex items-center space-x-4">
        <div class="text-center">
          <div class="text-2xl font-bold text-green-600">99.9%</div>
          <div class="text-xs text-gray-500">Uptime</div>
        </div>
        <div class="text-center">
          <div class="text-2xl font-bold text-blue-600">2.1s</div>
          <div class="text-xs text-gray-500">Avg Response</div>
        </div>
        <div class="text-center">
          <div class="text-2xl font-bold text-purple-600">1.2k</div>
          <div class="text-xs text-gray-500">Requests/min</div>
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
  labels: ['00:00', '04:00', '08:00', '12:00', '16:00', '20:00'],
  uptime: [99.8, 99.9, 99.9, 99.8, 99.9, 99.9],
  responseTime: [2.1, 1.8, 2.3, 2.0, 1.9, 2.2],
  requests: [800, 1200, 1500, 1800, 1600, 1000]
}

const createChart = () => {
  if (!chartCanvas.value) return
  
  if (chartInstance) {
    chartInstance.destroy()
  }
  
  const ctx = chartCanvas.value.getContext('2d')
  
  chartInstance = new Chart(ctx, {
    type: 'line',
    data: {
      labels: data.labels,
      datasets: [
        {
          label: 'Uptime (%)',
          data: data.uptime,
          borderColor: '#059669',
          backgroundColor: '#05966920',
          borderWidth: 3,
          fill: false,
          tension: 0.4,
          yAxisID: 'y'
        },
        {
          label: 'Response Time (s)',
          data: data.responseTime,
          borderColor: '#0284c7',
          backgroundColor: '#0284c720',
          borderWidth: 3,
          fill: false,
          tension: 0.4,
          yAxisID: 'y1'
        },
        {
          label: 'Requests/min',
          data: data.requests,
          borderColor: '#7c3aed',
          backgroundColor: '#7c3aed20',
          borderWidth: 3,
          fill: false,
          tension: 0.4,
          yAxisID: 'y2'
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
        x: {
          grid: {
            color: '#f3f4f6'
          }
        },
        y: {
          type: 'linear',
          display: true,
          position: 'left',
          min: 99,
          max: 100,
          grid: {
            color: '#f3f4f6'
          },
          title: {
            display: true,
            text: 'Uptime (%)'
          }
        },
        y1: {
          type: 'linear',
          display: true,
          position: 'right',
          min: 0,
          max: 3,
          grid: {
            drawOnChartArea: false
          },
          title: {
            display: true,
            text: 'Response Time (s)'
          }
        },
        y2: {
          type: 'linear',
          display: false,
          position: 'right',
          min: 0,
          max: 2000
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

onMounted(() => {
  nextTick(() => {
    createChart()
  })
})
</script>
