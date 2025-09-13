<template>
  <AuthenticatedLayout 
    :navigation="underwriterNavigation" 
    role-title="Underwriter Portal"
    page-title="All Applications"
  >
    <template #header>
      <div class="flex items-center justify-between">
        <div>
          <h1 class="text-2xl font-bold text-gray-900">All Applications</h1>
          <p class="text-gray-600">Review and manage crop insurance applications</p>
        </div>
        <div class="flex items-center space-x-3">
          <button
            @click="refreshApplications"
            :disabled="loading"
            class="inline-flex items-center px-4 py-2 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 disabled:opacity-50"
          >
            <RefreshCw :class="['h-4 w-4 mr-2', loading ? 'animate-spin' : '']" />
            Refresh
          </button>
          <button
            @click="printApplication"
            class="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-green-600 hover:bg-green-700"
          >
            <Printer class="h-4 w-4 mr-2" />
            Print Application
          </button>
        </div>
      </div>
    </template>

    <div class="space-y-6">
      <!-- Filters and Search -->
      <div class="bg-white rounded-lg shadow-sm border border-gray-200">
        <div class="px-6 py-4 border-b border-gray-200">
          <h2 class="text-lg font-semibold text-gray-900">Filter Applications</h2>
        </div>
        <div class="p-6">
          <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
            <!-- Search -->
            <div class="md:col-span-2">
              <label for="search" class="block text-sm font-medium text-gray-700 mb-1">
                Search Applications
              </label>
              <div class="relative">
                <Search class="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-gray-400" />
                <input
                  v-model="searchQuery"
                  type="text"
                  id="search"
                  placeholder="Search by farmer name, location..."
                  class="w-full pl-10 pr-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                />
              </div>
            </div>

            <!-- Status Filter -->
            <div>
              <label for="statusFilter" class="block text-sm font-medium text-gray-700 mb-1">
                Status
              </label>
              <select
                v-model="selectedStatus"
                id="statusFilter"
                class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
              >
                <option value="">All Status</option>
                <option value="pending">Pending</option>
                <option value="approved">Approved</option>
                <option value="rejected">Rejected</option>
              </select>
            </div>

            <!-- Crop Type Filter -->
            <div>
              <label for="cropFilter" class="block text-sm font-medium text-gray-700 mb-1">
                Crop Type
              </label>
            <select
              v-model="selectedCrop"
              id="cropFilter"
              class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
            >
              <option value="">All Crops</option>
              <option value="rice">Rice</option>
              <option value="corn">Corn</option>
            </select>
          </div>
        </div>
      </div>
    </div>

    <!-- Applications List -->
    <div class="bg-white rounded-lg shadow-sm border border-gray-200">
      <div class="px-6 py-4 border-b border-gray-200">
        <h2 class="text-lg font-semibold text-gray-900">Applications</h2>
      </div>
      
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Application ID
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Farmer/Group Name
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Crop Type
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Location
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Status
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Date Applied
              </th>
              <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                Actions
              </th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr 
              v-for="application in filteredApplications" 
              :key="application.id"
              :class="[
                'hover:bg-gray-50 cursor-pointer',
                selectedApplication?.id === application.id ? 'bg-indigo-50' : ''
              ]"
              @click="selectApplication(application)"
            >
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                {{ application.id }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                {{ application.groupName || application.farmerName }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                {{ application.cropType }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                {{ application.location }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span :class="[
                  'inline-flex px-2 py-1 text-xs font-semibold rounded-full',
                  application.status === 'approved' ? 'bg-green-100 text-green-800' :
                  application.status === 'pending' ? 'bg-yellow-100 text-yellow-800' :
                  'bg-red-100 text-red-800'
                ]">
                  {{ application.status.charAt(0).toUpperCase() + application.status.slice(1) }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                {{ formatDate(application.dateApplied) }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                <button
                  @click.stop="viewApplication(application)"
                  class="text-indigo-600 hover:text-indigo-900"
                >
                  View
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- PCIC Form Preview (Hidden, for printing) -->
    <div id="printable-form" class="hidden print:block">
      <div v-for="appToPrint in applicationsToPrint" :key="appToPrint.id" class="print-container">
        <!-- Page 1 -->
        <div class="print-page page-1">
          <!-- Header -->
          <div class="form-header">
            <div class="header-left">
              <div class="requirements">
                <strong>REQUIREMENTS:</strong><br>
                GOVERNMENT ID OR ANY VALID ID<br>
                RSBSA STUB<br>
                <strong>NOTE:</strong> Please complete details.
              </div>
            </div>
            <div class="header-center">
              <div class="pcic-logo">
                <img src="https://hebbkx1anhila5yf.public.blob.vercel-storage.com/pcic.jpg-ymdsA0RBXJ1O58Wx4oDrmGSD8rRBY0.jpeg" alt="PCIC Logo" class="logo">
              </div>
              <div class="title">
                <h1>PHILIPPINE CROP INSURANCE CORPORATION</h1>
                <h2>REGION ___________</h2>
                <h3>APPLICATION FOR CROP INSURANCE</h3>
                <h4>(Group Application)</h4>
              </div>
            </div>
            <div class="header-right">
              <div class="form-info">
                <div>RC-UPI-07</div>
                <div>2017/FEB</div>
                <div>PAGE 1</div>
              </div>
            </div>
          </div>

          <!-- Group Information -->
          <div class="group-info">
            <div class="info-row">
              <span>*Name of FO/PA/COOP/IA/Barangay: {{ appToPrint.groupName }}</span>
            </div>
            <div class="info-row">
              <span>Underwriter / Solicitor: {{ appToPrint.underwriter }}</span>
              <span class="ml-8">Mailing Address: {{ appToPrint.mailingAddress }}</span>
            </div>
            <div class="program-row">
              <span>Program:</span>
              <label><input type="checkbox" :checked="appToPrint.program === 'regular'"> Regular</label>
              <label><input type="checkbox" :checked="appToPrint.program === 'sikat'"> Sikat saka</label>
              <label><input type="checkbox" :checked="appToPrint.program === 'rsbsa'"> RSBSA</label>
              <label><input type="checkbox" :checked="appToPrint.program === 'apcp'"> APCP/CAP-PBD</label>
              <label><input type="checkbox" :checked="appToPrint.program === 'punla'"> PUNLA</label>
              <label><input type="checkbox" :checked="appToPrint.program === 'corporate'"> Corporate Rice Farming</label>
              <label><input type="checkbox" :checked="appToPrint.program === 'others'"> Others: ________</label>
            </div>
          </div>

          <!-- Farmers Table -->
          <div class="farmers-table">
            <table class="pcic-table">
              <thead>
                <tr>
                  <th rowspan="2">No.</th>
                  <th colspan="4">Name of Farmers</th>
                  <th rowspan="2">Suffix<br>(Sr, Jr<br>.ect)</th>
                  <th rowspan="2">Civil<br>Status<br>(S/M/W)</th>
                  <th rowspan="2">Gender<br>(F/M)</th>
                  <th rowspan="2">Address<br>(Sitio & Barangay)</th>
                  <th rowspan="2">Cellphone<br>No.</th>
                  <th rowspan="2">Bank<br>Beneficiary</th>
                </tr>
                <tr>
                  <th>Last Name</th>
                  <th>First Name</th>
                  <th>Middle Name</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(farmer, index) in appToPrint.farmers" :key="index">
                  <td>{{ index + 1 }}</td>
                  <td>{{ farmer.lastName }}</td>
                  <td>{{ farmer.firstName }}</td>
                  <td>{{ farmer.middleName }}</td>
                  <td>{{ farmer.suffix }}</td>
                  <td>{{ farmer.civilStatus }}</td>
                  <td>{{ farmer.gender }}</td>
                  <td>{{ farmer.address }}</td>
                  <td>{{ farmer.cellphone }}</td>
                  <td>{{ farmer.bankBeneficiary }}</td>
                </tr>
                <tr class="total-row">
                  <td colspan="11"><strong>TOTAL</strong></td>
                </tr>
              </tbody>
            </table>
          </div>

          <!-- PCIC Only Section -->
          <div class="pcic-only">
            <div class="pcic-header">FOR PCIC ONLY:</div>
            <div class="pcic-info">
              <div class="pcic-left">
                <div>CIC No. ___________</div>
                <div>Date Issued: ___________</div>
                <div>Crop: <input type="checkbox" :checked="appToPrint.cropType === 'rice'"> Rice <input type="checkbox" :checked="appToPrint.cropType === 'corn'"> Corn</div>
                <div>Phase: ___________</div>
                <div>COC No. ___________</div>
                <div>Date Issued: ___________</div>
                <div>Period Covered From: _______ To: _______</div>
                <div>Rice: Wet _______ Dry _______</div>
                <div>Corn A. _______ B. _______</div>
              </div>
            </div>
          </div>

          <!-- Planting Calendar Table -->
          <div class="planting-calendar">
            <table class="pcic-table">
              <thead>
                <tr>
                  <th>O.R No.</th>
                  <th>O.R No.</th>
                  <th>Amount Paid</th>
                  <th colspan="3">Planting Calendar</th>
                </tr>
                <tr>
                  <th></th>
                  <th></th>
                  <th></th>
                  <th>Variety</th>
                  <th>Sowing/DS</th>
                  <th>TP/Planting</th>
                  <th>Harvest</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(farmer, index) in appToPrint.farmers" :key="index">
                  <td>{{ farmer.orNumber1 }}</td>
                  <td>{{ farmer.orNumber2 }}</td>
                  <td>{{ farmer.amountPaid }}</td>
                  <td>{{ farmer.variety }}</td>
                  <td>{{ farmer.sowingDate }}</td>
                  <td>{{ farmer.plantingDate }}</td>
                  <td>{{ farmer.harvestDate }}</td>
                </tr>
              </tbody>
            </table>
          </div>

          <!-- Certifications -->
          <div class="certifications">
            <div class="cert-section">
              <h4>TECHNOLOGIST'S CERTIFICATION</h4>
              <p>I hereby certify that above farmer-applicants follow POT/GAP, and that, for crop already planted at the time of the application, no risk insured against has occurred.</p>
              <div class="signature-section">
                <div>Signature Over Printed Name: _________________</div>
                <div>Office: _________________</div>
                <div>Date: _________________</div>
              </div>
            </div>
            
            <div class="cert-section">
              <h4>CERTIFICATION</h4>
              <p>I hereby certify that the above information are true and correct to the best of my knowledge</p>
              <div class="signature-section">
                <div>Signature Over Printed Name: _________________</div>
                <div>Office: _________________</div>
                <div>Date: _________________</div>
              </div>
            </div>
          </div>

          <!-- Premium Computation -->
          <div class="premium-computation">
            <h4>PREMIUM COMPUTATION (FOR PCIC ONLY)</h4>
            <div class="premium-details">
              <div>Premium Rate: _________________</div>
              <div>Lending Institution Share(LI): _________________</div>
              <div>Gov't Premium Subsidy (GPS): _________________</div>
              <div>Gross Premium: _________________</div>
              <div>Less: Underwriter's/Solicitor's Incentive (less withholding tax): _________________</div>
              <div>Net Premium due to PCIC: _________________</div>
            </div>
          </div>

          <!-- Legends -->
          <div class="legends">
            <div class="legend-section">
              <h4>LEGENDS:</h4>
              <div>*Type of Group:</div>
              <div>FO - Farmers' Organization</div>
              <div>FA - Farmers' Association</div>
              <div>COOP - Cooperative</div>
              <div>IA - Irrigators' Association</div>
            </div>
          </div>
        </div>

        <!-- Page 2 -->
        <div class="print-page page-2">
          <div class="page-header">
            <h3>APPLICATION INSURANCE</h3>
          </div>

          <!-- Farm Details Table -->
          <div class="farm-details">
            <table class="pcic-table">
              <thead>
                <tr>
                  <th rowspan="2">No.</th>
                  <th rowspan="2">Name of the Farmers<br>(Follow the order on page1)</th>
                  <th rowspan="2">Farm Location</th>
                  <th rowspan="2">Area<br>(ha)</th>
                  <th rowspan="2">Land Category<br>/ Soil Type</th>
                  <th rowspan="2">Tenural<br>Status</th>
                  <th colspan="4">Adjacent Lot Owners</th>
                  <th rowspan="2">Signature</th>
                </tr>
                <tr>
                  <th>North</th>
                  <th>South</th>
                  <th>East</th>
                  <th>West</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(farmer, index) in appToPrint.farmers" :key="index">
                  <td>{{ index + 1 }}</td>
                  <td>{{ farmer.firstName }} {{ farmer.lastName }}</td>
                  <td>{{ farmer.farmLocation }}</td>
                  <td>{{ farmer.area }}</td>
                  <td>{{ farmer.landCategory }}</td>
                  <td>{{ farmer.tenuralStatus }}</td>
                  <td>{{ farmer.adjacentNorth }}</td>
                  <td>{{ farmer.adjacentSouth }}</td>
                  <td>{{ farmer.adjacentEast }}</td>
                  <td>{{ farmer.adjacentWest }}</td>
                  <td class="signature-cell">Picture</td>
                </tr>
                <tr class="total-row">
                  <td colspan="11"><strong>TOTAL</strong></td>
                </tr>
              </tbody>
            </table>
          </div>

          <!-- Legends for Page 2 -->
          <div class="legends-page2">
            <div class="legend-column">
              <h4>LAND CATEGORY/SOIL TYPE:</h4>
              <div><strong>For Rice Crop (Land Category):</strong></div>
              <div>(1) Irrigated NIA/CIA</div>
              <div>(2) Irrigated Deep Well Pump/Shallow Tube Well (STW)</div>
              <div>(3) Irrigated Open Source (Swip, Creek, River)</div>
              <div>(4) Rainfed</div>
            </div>
            
            <div class="legend-column">
              <h4>LEGENDS</h4>
              <div><strong>For Corn Crop (Soil Type/Topography):</strong></div>
              <div>(A) Broad Plain - Clay Loam</div>
              <div>(B) Broad Plain - Silty Clay Loam</div>
              <div>(C) Broad Plain - Silty Loam</div>
              <div>(E) Rolling/Upload</div>
            </div>
            
            <div class="legend-column">
              <h4>TENURAL STATUS:</h4>
              <div>(1) Landowner</div>
              <div>(2) Lessee</div>
              <div>(3) Other (please specify)</div>
            </div>
          </div>
        </div>
      </div>
    </div>
<!-- Application Detail Modal -->
<div 
  v-if="showDetailModal" 
  class="fixed inset-0 z-50 overflow-hidden"
  @click="closeDetailModal"
>
  <div class="absolute inset-0 bg-black bg-opacity-50"></div>
  
  <div class="flex items-start justify-center min-h-screen p-4 pt-16">
    <div 
      class="bg-white rounded-lg shadow-xl max-w-4xl w-full max-h-[80vh] overflow-hidden relative z-10"
      @click.stop
    >
      <div class="flex flex-col max-h-[80vh]">
        <!-- Modal Header -->
        <div class="px-6 py-4 border-b border-gray-200 bg-white flex-shrink-0">
          <div class="flex items-center justify-between">
            <div>
              <h3 class="text-xl font-semibold text-gray-900">
                {{ viewingApplication?.groupName || viewingApplication?.farmerName }}
              </h3>
              <p class="text-sm text-gray-500 mt-1">
                Application ID: {{ viewingApplication?.id }}
              </p>
            </div>
            <button
              @click="closeDetailModal"
              class="p-2 rounded-md text-gray-400 hover:text-gray-600 hover:bg-gray-100 transition-colors"
            >
              <X class="h-5 w-5" />
            </button>
          </div>
        </div>

        <!-- Modal Body -->
        <div class="flex-1 overflow-y-auto p-6">
          <div v-if="viewingApplication" class="space-y-6">
            <!-- Basic Information -->
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div>
                <h4 class="text-sm font-medium text-gray-700 mb-2">Group/Farmer Name</h4>
                <p class="text-sm text-gray-900 bg-gray-50 p-3 rounded-md">
                  {{ viewingApplication.groupName || viewingApplication.farmerName }}
                </p>
              </div>
              <div>
                <h4 class="text-sm font-medium text-gray-700 mb-2">Location</h4>
                <p class="text-sm text-gray-900 bg-gray-50 p-3 rounded-md">
                  {{ viewingApplication.location }}
                </p>
              </div>
              <div>
                <h4 class="text-sm font-medium text-gray-700 mb-2">Crop Type</h4>
                <span class="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-green-100 text-green-800">
                  {{ viewingApplication.cropType }}
                </span>
              </div>
              <div>
                <h4 class="text-sm font-medium text-gray-700 mb-2">Status</h4>
                <span :class="[
                  'inline-flex items-center px-3 py-1 rounded-full text-sm font-medium',
                  viewingApplication.status === 'approved' ? 'bg-green-100 text-green-800' :
                  viewingApplication.status === 'pending' ? 'bg-yellow-100 text-yellow-800' :
                  'bg-red-100 text-red-800'
                ]">
                  {{ viewingApplication.status.charAt(0).toUpperCase() + viewingApplication.status.slice(1) }}
                </span>
              </div>
              <div>
                <h4 class="text-sm font-medium text-gray-700 mb-2">Program</h4>
                <p class="text-sm text-gray-900 bg-gray-50 p-3 rounded-md">
                  {{ viewingApplication.program?.charAt(0).toUpperCase() + viewingApplication.program?.slice(1) }}
                </p>
              </div>
              <div>
                <h4 class="text-sm font-medium text-gray-700 mb-2">Date Applied</h4>
                <p class="text-sm text-gray-900 bg-gray-50 p-3 rounded-md">
                  {{ formatDate(viewingApplication.dateApplied) }}
                </p>
              </div>
            </div>

            <!-- Farmers List -->
            <div>
              <h4 class="text-lg font-semibold text-gray-900 mb-4">Farmers ({{ viewingApplication.farmers?.length || 0 }})</h4>
              <div class="overflow-x-auto">
                <table class="min-w-full divide-y divide-gray-200">
                  <thead class="bg-gray-50">
                    <tr>
                      <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Name</th>
                      <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Gender</th>
                      <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Civil Status</th>
                      <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Contact</th>
                      <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Area (ha)</th>
                    </tr>
                  </thead>
                  <tbody class="bg-white divide-y divide-gray-200">
                    <tr v-for="farmer in viewingApplication.farmers" :key="farmer.id">
                      <td class="px-4 py-4 text-sm text-gray-900">
                        {{ farmer.firstName }} {{ farmer.middleName }} {{ farmer.lastName }}
                      </td>
                      <td class="px-4 py-4 text-sm text-gray-900">{{ farmer.gender }}</td>
                      <td class="px-4 py-4 text-sm text-gray-900">{{ farmer.civilStatus }}</td>
                      <td class="px-4 py-4 text-sm text-gray-900">{{ farmer.cellphone }}</td>
                      <td class="px-4 py-4 text-sm text-gray-900">{{ farmer.area }}</td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        </div>

        <!-- Modal Footer -->
        <div class="px-6 py-4 border-t border-gray-200 bg-white flex-shrink-0">
          <div class="flex justify-end space-x-3">
            <button
              @click="closeDetailModal"
              class="px-4 py-2 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 transition-colors"
            >
              Close
            </button>
          </div>
        </div>
      </div>
    </div>
      </div>
    </div>
  </div>
</AuthenticatedLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { RefreshCw, Search, Printer, X } from 'lucide-vue-next'
import AuthenticatedLayout from '@/layouts/AuthenticatedLayout.vue'
import { UNDERWRITER_NAVIGATION } from '@/lib/constants'

const router = useRouter()
const underwriterNavigation = UNDERWRITER_NAVIGATION

// State
const applications = ref([])
const selectedApplication = ref(null)
const viewingApplication = ref(null)
const showDetailModal = ref(false)
const loading = ref(false)

// Filters
const searchQuery = ref('')
const selectedStatus = ref('')
const selectedCrop = ref('')

// Enhanced sample data with more records
const sampleApplications = [
  {
    id: 'APP-2024-001',
    groupName: 'Bayugan Farmers Association',
    farmerName: 'John Paul Pracullos',
    cropType: 'Rice',
    location: 'P-7 Charito Bayugan City',
    status: 'pending',
    dateApplied: '2024-01-15',
    underwriter: 'Maria Santos',
    mailingAddress: 'P-7 Charito Bayugan City, Agusan del Sur',
    program: 'regular',
    farmers: [
      {
        id: 1,
        lastName: 'Pracullos',
        firstName: 'John Paul',
        middleName: 'Baguio',
        suffix: '',
        civilStatus: 'S',
        gender: 'M',
        address: 'P-7 Charito Bayugan City',
        cellphone: '09501986109',
        bankBeneficiary: 'BDO',
        orNumber1: '10000',
        orNumber2: '102',
        amountPaid: '10000',
        variety: 'IR-64',
        sowingDate: '01-01-2025',
        plantingDate: '01-15-2025',
        harvestDate: '04-15-2025',
        farmLocation: 'P-7 Charito Bayugan City',
        area: '5 ha',
        landCategory: 'Sandy soil',
        tenuralStatus: 'Owned',
        adjacentNorth: 'Albert Agbo',
        adjacentSouth: 'Roman Osorio',
        adjacentEast: 'Juan Dela Cruz',
        adjacentWest: 'John Doe'
      },
      {
        id: 2,
        lastName: 'Santos',
        firstName: 'Maria',
        middleName: 'Cruz',
        suffix: '',
        civilStatus: 'M',
        gender: 'F',
        address: 'P-8 Charito Bayugan City',
        cellphone: '09123456789',
        bankBeneficiary: 'LBP',
        orNumber1: '10001',
        orNumber2: '103',
        amountPaid: '8000',
        variety: 'NSIC Rc 222',
        sowingDate: '01-05-2025',
        plantingDate: '01-20-2025',
        harvestDate: '04-20-2025',
        farmLocation: 'P-8 Charito Bayugan City',
        area: '3 ha',
        landCategory: 'Clay soil',
        tenuralStatus: 'Owned',
        adjacentNorth: 'Pedro Garcia',
        adjacentSouth: 'Ana Lopez',
        adjacentEast: 'Carlos Reyes',
        adjacentWest: 'Linda Torres'
      }
    ]
  },
  {
    id: 'APP-2024-002',
    groupName: 'Nueva Ecija Rice Cooperative',
    farmerName: 'Roberto Garcia',
    cropType: 'Rice',
    location: 'Cabanatuan City, Nueva Ecija',
    status: 'approved',
    dateApplied: '2024-01-10',
    underwriter: 'Jose Rizal',
    mailingAddress: 'Cabanatuan City, Nueva Ecija',
    program: 'sikat',
    farmers: [
      {
        id: 1,
        lastName: 'Garcia',
        firstName: 'Roberto',
        middleName: 'Santos',
        suffix: 'Sr.',
        civilStatus: 'M',
        gender: 'M',
        address: 'Brgy. San Roque, Cabanatuan City',
        cellphone: '09171234567',
        bankBeneficiary: 'PNB',
        orNumber1: '20000',
        orNumber2: '201',
        amountPaid: '15000',
        variety: 'PSB Rc 18',
        sowingDate: '12-15-2024',
        plantingDate: '01-01-2025',
        harvestDate: '04-01-2025',
        farmLocation: 'Brgy. San Roque, Cabanatuan City',
        area: '8 ha',
        landCategory: 'Irrigated NIA',
        tenuralStatus: 'Owned',
        adjacentNorth: 'Miguel Santos',
        adjacentSouth: 'Carmen Flores',
        adjacentEast: 'Diego Morales',
        adjacentWest: 'Elena Valdez'
      },
      {
        id: 2,
        lastName: 'Mendoza',
        firstName: 'Carmen',
        middleName: 'Reyes',
        suffix: '',
        civilStatus: 'W',
        gender: 'F',
        address: 'Brgy. Poblacion, Cabanatuan City',
        cellphone: '09189876543',
        bankBeneficiary: 'BPI',
        orNumber1: '20001',
        orNumber2: '202',
        amountPaid: '12000',
        variety: 'IR-64',
        sowingDate: '12-20-2024',
        plantingDate: '01-05-2025',
        harvestDate: '04-05-2025',
        farmLocation: 'Brgy. Poblacion, Cabanatuan City',
        area: '6 ha',
        landCategory: 'Irrigated Deep Well',
        tenuralStatus: 'Lessee',
        adjacentNorth: 'Antonio Cruz',
        adjacentSouth: 'Rosa Martinez',
        adjacentEast: 'Felipe Gonzales',
        adjacentWest: 'Luz Hernandez'
      },
      {
        id: 3,
        lastName: 'Torres',
        firstName: 'Eduardo',
        middleName: 'Luna',
        suffix: 'Jr.',
        civilStatus: 'S',
        gender: 'M',
        address: 'Brgy. Aduas, Cabanatuan City',
        cellphone: '09205551234',
        bankBeneficiary: 'Metrobank',
        orNumber1: '20002',
        orNumber2: '203',
        amountPaid: '9000',
        variety: 'NSIC Rc 160',
        sowingDate: '12-25-2024',
        plantingDate: '01-10-2025',
        harvestDate: '04-10-2025',
        farmLocation: 'Brgy. Aduas, Cabanatuan City',
        area: '4 ha',
        landCategory: 'Rainfed',
        tenuralStatus: 'Owned',
        adjacentNorth: 'Benito Aquino',
        adjacentSouth: 'Gloria Arroyo',
        adjacentEast: 'Rodrigo Duterte',
        adjacentWest: 'Bongbong Marcos'
      }
    ]
  },
  {
    id: 'APP-2024-003',
    groupName: 'Isabela Corn Growers Association',
    farmerName: 'Pedro Dela Cruz',
    cropType: 'Corn',
    location: 'Santiago City, Isabela',
    status: 'rejected',
    dateApplied: '2024-01-08',
    underwriter: 'Ana Bonifacio',
    mailingAddress: 'Santiago City, Isabela',
    program: 'rsbsa',
    farmers: [
      {
        id: 1,
        lastName: 'Dela Cruz',
        firstName: 'Pedro',
        middleName: 'Aguinaldo',
        suffix: '',
        civilStatus: 'M',
        gender: 'M',
        address: 'Brgy. Centro, Santiago City',
        cellphone: '09351112233',
        bankBeneficiary: 'RCBC',
        orNumber1: '30000',
        orNumber2: '301',
        amountPaid: '7500',
        variety: 'Pioneer 30G19',
        sowingDate: '01-10-2025',
        plantingDate: '01-25-2025',
        harvestDate: '05-25-2025',
        farmLocation: 'Brgy. Centro, Santiago City',
        area: '10 ha',
        landCategory: 'Broad Plain Clay Loam',
        tenuralStatus: 'Owned',
        adjacentNorth: 'Emilio Jacinto',
        adjacentSouth: 'Andres Bonifacio',
        adjacentEast: 'Apolinario Mabini',
        adjacentWest: 'Marcelo Del Pilar'
      }
    ]
  },
  {
    id: 'APP-2024-004',
    groupName: 'Pangasinan Farmers Union',
    farmerName: 'Luisa Fernandez',
    cropType: 'Rice',
    location: 'Dagupan City, Pangasinan',
    status: 'pending',
    dateApplied: '2024-01-12',
    underwriter: 'Carlos Palanca',
    mailingAddress: 'Dagupan City, Pangasinan',
    program: 'punla',
    farmers: [
      {
        id: 1,
        lastName: 'Fernandez',
        firstName: 'Luisa',
        middleName: 'Morales',
        suffix: '',
        civilStatus: 'S',
        gender: 'F',
        address: 'Brgy. Bonuan, Dagupan City',
        cellphone: '09778889999',
        bankBeneficiary: 'UnionBank',
        orNumber1: '40000',
        orNumber2: '401',
        amountPaid: '11000',
        variety: 'NSIC Rc 238',
        sowingDate: '01-08-2025',
        plantingDate: '01-23-2025',
        harvestDate: '04-23-2025',
        farmLocation: 'Brgy. Bonuan, Dagupan City',
        area: '7 ha',
        landCategory: 'Irrigated Open Source',
        tenuralStatus: 'Lessee',
        adjacentNorth: 'Ramon Magsaysay',
        adjacentSouth: 'Elpidio Quirino',
        adjacentEast: 'Manuel Roxas',
        adjacentWest: 'Sergio Osmena'
      },
      {
        id: 2,
        lastName: 'Villanueva',
        firstName: 'Antonio',
        middleName: 'Ramos',
        suffix: '',
        civilStatus: 'M',
        gender: 'M',
        address: 'Brgy. Pantal, Dagupan City',
        cellphone: '09456667777',
        bankBeneficiary: 'Security Bank',
        orNumber1: '40001',
        orNumber2: '402',
        amountPaid: '13000',
        variety: 'PSB Rc 10',
        sowingDate: '01-12-2025',
        plantingDate: '01-27-2025',
        harvestDate: '04-27-2025',
        farmLocation: 'Brgy. Pantal, Dagupan City',
        area: '9 ha',
        landCategory: 'Irrigated NIA',
        tenuralStatus: 'Owned',
        adjacentNorth: 'Jose Laurel',
        adjacentSouth: 'Manuel Quezon',
        adjacentEast: 'Carlos Garcia',
        adjacentWest: 'Diosdado Macapagal'
      }
    ]
  },
  {
    id: 'APP-2024-005',
    groupName: 'Tarlac Agricultural Cooperative',
    farmerName: 'Miguel Santos',
    cropType: 'Rice',
    location: 'Tarlac City, Tarlac',
    status: 'approved',
    dateApplied: '2024-01-05',
    underwriter: 'Teresa Aquino',
    mailingAddress: 'Tarlac City, Tarlac',
    program: 'corporate',
    farmers: [
      {
        id: 1,
        lastName: 'Santos',
        firstName: 'Miguel',
        middleName: 'Castro',
        suffix: '',
        civilStatus: 'M',
        gender: 'M',
        address: 'Brgy. San Vicente, Tarlac City',
        cellphone: '09234445555',
        bankBeneficiary: 'China Bank',
        orNumber1: '50000',
        orNumber2: '501',
        amountPaid: '20000',
        variety: 'Hybrid Rice',
        sowingDate: '12-28-2024',
        plantingDate: '01-12-2025',
        harvestDate: '04-12-2025',
        farmLocation: 'Brgy. San Vicente, Tarlac City',
        area: '15 ha',
        landCategory: 'Irrigated NIA',
        tenuralStatus: 'Owned',
        adjacentNorth: 'Ferdinand Marcos',
        adjacentSouth: 'Corazon Aquino',
        adjacentEast: 'Fidel Ramos',
        adjacentWest: 'Joseph Estrada'
      }
    ]
  }
]

// Computed
const filteredApplications = computed(() => {
  let filtered = [...applications.value]

  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter(app => 
      app.groupName?.toLowerCase().includes(query) ||
      app.farmerName?.toLowerCase().includes(query) ||
      app.location?.toLowerCase().includes(query)
    )
  }

  if (selectedStatus.value) {
    filtered = filtered.filter(app => app.status === selectedStatus.value)
  }

  if (selectedCrop.value) {
    filtered = filtered.filter(app => app.cropType.toLowerCase() === selectedCrop.value)
  }

  return filtered
})

const applicationsToPrint = computed(() => {
  return selectedApplication.value ? [selectedApplication.value] : filteredApplications.value
})

// Methods
const refreshApplications = () => {
  loading.value = true
  // Simulate API call
  setTimeout(() => {
    applications.value = sampleApplications
    loading.value = false
  }, 1000)
}

const selectApplication = (application) => {
  selectedApplication.value = application
}

const viewApplication = (application) => {
  viewingApplication.value = application
  showDetailModal.value = true
}

const closeDetailModal = () => {
  showDetailModal.value = false
  viewingApplication.value = null
}

const printApplication = () => {
  window.print()
}

const formatDate = (dateString) => {
  return new Date(dateString).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  })
}

// Initialize
onMounted(() => {
  refreshApplications()
})
</script>

<style scoped>
/* Print Styles */
@media print {
  body * {
    visibility: hidden;
  }
  
  #printable-form,
  #printable-form * {
    visibility: visible;
  }
  
  #printable-form {
    position: absolute;
    left: 0;
    top: 0;
    width: 100%;
  }
}

.print-container {
  font-family: Arial, sans-serif;
  font-size: 10px;
  line-height: 1.2;
}

.print-page {
  width: 11in;
  height: 8.5in;
  margin: 0;
  padding: 0.5in;
  page-break-after: always;
  background: white;
  border: 1px solid #000;
  box-sizing: border-box;
}

.print-page:last-child {
  page-break-after: avoid;
}

/* Header Styles */
.form-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 15px;
  border-bottom: 2px solid #000;
  padding-bottom: 10px;
}

.header-left {
  flex: 1;
  font-size: 8px;
}

.header-center {
  flex: 2;
  text-align: center;
}

.header-right {
  flex: 1;
  text-align: right;
  font-size: 8px;
}

.title h1 {
  font-size: 14px;
  font-weight: bold;
  margin: 0;
}

.title h2 {
  font-size: 12px;
  margin: 2px 0;
}

.title h3 {
  font-size: 12px;
  font-weight: bold;
  margin: 2px 0;
}

.title h4 {
  font-size: 10px;
  margin: 2px 0;
}

.logo {
  width: 50px;
  height: 50px;
  margin-bottom: 5px;
}

/* Group Info Styles */
.group-info {
  margin-bottom: 15px;
}

.info-row {
  margin-bottom: 5px;
  display: flex;
  gap: 20px;
}

.program-row {
  display: flex;
  gap: 15px;
  align-items: center;
  flex-wrap: wrap;
}

.program-row label {
  display: flex;
  align-items: center;
  gap: 3px;
  font-size: 9px;
}

/* Table Styles */
.pcic-table {
  width: 100%;
  border-collapse: collapse;
  margin-bottom: 15px;
  font-size: 8px;
}

.pcic-table th,
.pcic-table td {
  border: 1px solid #000;
  padding: 3px;
  text-align: center;
  vertical-align: middle;
}

.pcic-table th {
  background-color: #f0f0f0;
  font-weight: bold;
}

.total-row {
  font-weight: bold;
}

/* PCIC Only Section */
.pcic-only {
  margin-bottom: 15px;
  border: 1px solid #000;
  padding: 10px;
}

.pcic-header {
  font-weight: bold;
  margin-bottom: 5px;
}

.pcic-info {
  display: flex;
  gap: 20px;
}

.pcic-left div {
  margin-bottom: 3px;
}

/* Certifications */
.certifications {
  display: flex;
  gap: 20px;
  margin-bottom: 15px;
}

.cert-section {
  flex: 1;
  border: 1px solid #000;
  padding: 10px;
}

.cert-section h4 {
  font-size: 10px;
  font-weight: bold;
  margin-bottom: 5px;
  text-align: center;
}

.cert-section p {
  font-size: 8px;
  margin-bottom: 10px;
}

.signature-section div {
  margin-bottom: 5px;
  font-size: 8px;
}

/* Premium Computation */
.premium-computation {
  border: 1px solid #000;
  padding: 10px;
  margin-bottom: 15px;
}

.premium-computation h4 {
  font-size: 10px;
  font-weight: bold;
  margin-bottom: 5px;
  text-align: center;
}

.premium-details div {
  margin-bottom: 3px;
  font-size: 8px;
}

/* Legends */
.legends {
  font-size: 8px;
}

.legend-section h4 {
  font-weight: bold;
  margin-bottom: 3px;
}

.legend-section div {
  margin-bottom: 2px;
}

/* Page 2 Styles */
.page-header {
  text-align: center;
  margin-bottom: 15px;
  border-bottom: 2px solid #000;
  padding-bottom: 10px;
}

.page-header h3 {
  font-size: 14px;
  font-weight: bold;
  margin: 0;
}

.farm-details {
  margin-bottom: 20px;
}

.signature-cell {
  height: 40px;
  vertical-align: middle;
}

.legends-page2 {
  display: flex;
  gap: 20px;
  font-size: 8px;
}

.legend-column {
  flex: 1;
}

.legend-column h4 {
  font-weight: bold;
  margin-bottom: 5px;
  text-decoration: underline;
}

.legend-column div {
  margin-bottom: 2px;
}
</style>
