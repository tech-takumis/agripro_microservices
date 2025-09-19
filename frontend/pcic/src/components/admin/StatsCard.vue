<template>
  <div class="bg-white rounded-xl shadow-lg border border-gray-200 p-10
              hover:shadow-xl hover:border-indigo-300 transition-all duration-300 cursor-pointer
              transform hover:-translate-y-1 h-full flex flex-col min-h-[400px]">
    
    <!-- Header Section with Icon and Title -->
    <div class="flex items-start justify-between mb-10">
      <div class="flex items-start space-x-5 flex-1 min-w-0">
        <div :class="[
          'p-5 rounded-2xl flex-shrink-0 mt-1',
          iconBgColor
        ]">
          <component :is="icon" :class="['h-10 w-10', iconColor]" />
        </div>
        <div class="min-w-0 flex-1">
          <h3 class="text-xl font-bold text-gray-900 leading-tight mb-3">{{ title }}</h3>
          <p class="text-base text-gray-500 leading-relaxed">{{ description }}</p>
        </div>
      </div>
      <div v-if="trend" class="flex items-center text-sm font-semibold px-4 py-2 rounded-full bg-gray-50" :class="trendColor">
        <component :is="trendIcon" class="h-4 w-4 mr-2" />
        {{ trend }}
      </div>
    </div>
    
    <!-- Main Value Section -->
    <div class="flex-1 flex flex-col justify-between">
      <div class="mb-8">
        <div class="flex items-baseline space-x-3 mb-4">
          <span class="text-6xl font-bold text-gray-900 leading-none">{{ value }}</span>
          <div v-if="change" class="flex items-center">
            <span :class="[
              'text-base font-bold px-3 py-2 rounded-lg',
              changeColor,
              change.startsWith('+') ? 'bg-green-50' : 'bg-red-50'
            ]">
              {{ change }}
            </span>
          </div>
        </div>
      </div>
      
      <!-- Enhanced Chart Section -->
      <div v-if="chartData" class="h-32 flex items-end bg-gray-50 rounded-xl p-4">
        <div class="w-full h-full">
          <div class="flex items-center justify-between mb-2">
            <span class="text-xs font-medium text-gray-600">Trend</span>
            <span class="text-xs text-gray-500">Last 7 days</span>
          </div>
          <canvas ref="miniChart" class="w-full h-full"></canvas>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, onMounted, nextTick } from 'vue'
import { TrendingUp, TrendingDown, Minus } from 'lucide-vue-next'
import { Chart, registerables } from 'chart.js'

Chart.register(...registerables)

const props = defineProps({
  title: String,
  description: String,
  value: [String, Number],
  change: String,
  icon: [Object, Function],
  variant: {
    type: String,
    default: 'default'
  },
  trend: String,
  chartData: Array
})

const miniChart = ref(null)
let chartInstance = null

const iconBgColor = computed(() => {
  const variants = {
    primary: 'bg-indigo-100',
    success: 'bg-green-100',
    warning: 'bg-yellow-100',
    danger: 'bg-red-100',
    info: 'bg-blue-100',
    default: 'bg-gray-100'
  }
  return variants[props.variant] || variants.default
})

const iconColor = computed(() => {
  const variants = {
    primary: 'text-indigo-600',
    success: 'text-green-600',
    warning: 'text-yellow-600',
    danger: 'text-red-600',
    info: 'text-blue-600',
    default: 'text-gray-600'
  }
  return variants[props.variant] || variants.default
})

const changeColor = computed(() => {
  if (!props.change) return ''
  return props.change.startsWith('+') ? 'text-green-600' : 'text-red-600'
})

const trendColor = computed(() => {
  if (!props.trend) return ''
  if (props.trend.includes('+') || props.trend.includes('up')) return 'text-green-600'
  if (props.trend.includes('-') || props.trend.includes('down')) return 'text-red-600'
  return 'text-gray-600'
})

const trendIcon = computed(() => {
  if (!props.trend) return Minus
  if (props.trend.includes('+') || props.trend.includes('up')) return TrendingUp
  if (props.trend.includes('-') || props.trend.includes('down')) return TrendingDown
  return Minus
})

const createMiniChart = () => {
  if (!miniChart.value || !props.chartData) return
  
  if (chartInstance) {
    chartInstance.destroy()
  }
  
  const ctx = miniChart.value.getContext('2d')
  const primaryColor = props.variant === 'primary' ? '#4f46e5' : 
                      props.variant === 'success' ? '#059669' :
                      props.variant === 'warning' ? '#d97706' :
                      props.variant === 'danger' ? '#dc2626' :
                      props.variant === 'info' ? '#0284c7' : '#6b7280'
  
  chartInstance = new Chart(ctx, {
    type: 'line',
    data: {
      labels: props.chartData.map((_, index) => index),
      datasets: [{
        data: props.chartData,
        borderColor: primaryColor,
        backgroundColor: `${primaryColor}20`,
        borderWidth: 4,
        fill: true,
        tension: 0.5,
        pointRadius: 0,
        pointHoverRadius: 8,
        pointHoverBackgroundColor: primaryColor,
        pointHoverBorderColor: '#ffffff',
        pointHoverBorderWidth: 3,
        pointBackgroundColor: primaryColor,
        pointBorderColor: '#ffffff',
        pointBorderWidth: 2
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: { display: false },
        tooltip: {
          enabled: true,
          backgroundColor: 'rgba(0, 0, 0, 0.8)',
          titleColor: '#ffffff',
          bodyColor: '#ffffff',
          borderColor: primaryColor,
          borderWidth: 1,
          cornerRadius: 8,
          displayColors: false,
          callbacks: {
            title: () => '',
            label: (context) => `Value: ${context.parsed.y}`
          }
        }
      },
      scales: {
        x: { 
          display: false,
          grid: { display: false }
        },
        y: { 
          display: false,
          grid: { display: false }
        }
      },
      elements: {
        point: { 
          radius: 0,
          hoverRadius: 8
        },
        line: {
          borderJoinStyle: 'round',
          borderCapStyle: 'round'
        }
      },
      interaction: {
        intersect: false,
        mode: 'index'
      },
      animation: {
        duration: 2000,
        easing: 'easeInOutQuart'
      }
    }
  })
}

onMounted(() => {
  nextTick(() => {
    createMiniChart()
  })
})
</script>
