<template>
    <AuthenticatedLayout
        :navigation="navigation"
        :role-title="roleTitle"
        page-title="Applications"
    >
<!-- Batch/Application Summary Section -->
<div class="flex items-center gap-6 mb-2 print:hidden">
  <div v-if="selectedBatch">
    <span class="text-green-700 font-semibold">
      {{
        batches.find(b => b.id === selectedBatch)?.name
      }}
    </span>
    <span class="ml-2 text-xs text-gray-500">
        <span class="m-2"> Total Applications:</span>
      {{
        batches.find(b => b.id === selectedBatch)?.totalApplications
          ?? filteredApplications.length
      }}
    </span>
    <span v-if="batches.find(b => b.id === selectedBatch)?.maxApplications" class="ml-2 text-xs text-gray-400">
      / Max: {{ batches.find(b => b.id === selectedBatch)?.maxApplications }}
    </span>
  </div>
  <div v-else>
    <span class="font-medium text-gray-700">All Applications:</span>
    <span class="text-green-700 font-semibold">{{ filteredApplications.length }}</span>
  </div>
</div>


<!-- Fixed Header - Single Row Layout -->
<div class="flex items-center justify-between mb-4 gap-4 print:hidden">
  <!-- Title -->
  <h1 class="text-2xl font-semibold text-gray-900 flex-shrink-0">
    Farmer Applications
  </h1>

  <!-- All Controls in One Row -->
  <div class="flex items-center gap-3 flex-wrap">
    <!-- Batch Management -->
    <div class="flex items-center gap-2">
      <!-- Create Batch -->
      <button
        class="flex items-center justify-center p-2 bg-green-600 text-white rounded-full shadow-sm hover:bg-gray-400 transition-colors flex-shrink-0"
        @click="showCreateBatchModal = true"
      >
        <Plus class="w-5 h-5" />
      </button>

      <!-- Batch Dropdown -->
      <select
        v-model="selectedBatch"
        class="w-40 border border-gray-300 rounded-lg px-3 py-2 text-sm text-gray-700 focus:border-green-400 focus:ring-2 focus:ring-green-400/40 transition duration-200 disabled:opacity-50"
      >
        <option value="">All Batches</option>
        <option
          v-for="batch in batches"
          :key="batch.id"
          :value="batch.id"
        >
          {{ batch.name }} ({{ batch.totalApplications }})
        </option>
      </select>
    </div>

    <!-- Action Buttons - Show inline when applications are selected -->
    <template v-if="selectedApplications.length > 0">
      <button
        class="inline-flex items-center px-3 py-2 rounded-lg text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 flex-shrink-0"
        :disabled="verificationStore.isForwarding.value"
        @click="handleForwardToPCIC"
      >
        <Edit class="h-4 w-4 mr-1" />
        <span v-if="verificationStore.isForwarding.value">Forwarding...</span>
        <span v-else>Forward to PCIC</span>
      </button>

      <button
        class="inline-flex items-center px-3 py-2 rounded-lg text-sm font-medium text-white bg-red-600 hover:bg-red-700 focus:ring-2 focus:ring-offset-2 focus:ring-red-500 flex-shrink-0"
        @click="handleDelete"
      >
        <Trash2 class="h-4 w-4 mr-1" />
        Delete ({{ selectedApplications.length }})
      </button>
    </template>

    <!-- Utility Buttons -->
    <div class="flex items-center gap-2">
      <!-- Print -->
      <button
        class="inline-flex items-center px-3 py-2 rounded-lg text-sm font-medium text-gray-700 bg-white border border-gray-300 shadow-sm hover:bg-green-600 hover:text-white focus:ring-2 focus:ring-offset-2 focus:ring-green-500 transition-all duration-300 ease-in-out flex-shrink-0"
        @click="handlePrint"
      >
        <Printer class="h-4 w-4 mr-1" />
        Print
      </button>

      <!-- Filter -->
      <button
        class="inline-flex items-center px-3 py-2 rounded-lg text-sm font-medium text-gray-700 bg-white border border-gray-300 shadow-sm hover:bg-green-600 hover:text-white focus:ring-2 focus:ring-offset-2 focus:ring-green-500 transition-all duration-300 ease-in-out flex-shrink-0"
        @click="showFilterModal = true"
      >
        <Filter class="h-4 w-4 mr-1" />
        Filter
      </button>
    </div>
  </div>
</div>


<!-- Loading state -->
<div 
  v-if="loading" 
  class="flex flex-col items-center justify-center min-h-[200px] space-y-4 print:hidden"
>
  <!-- Spinner -->
  <div class="relative">
    <div class="h-14 w-14 rounded-full border-4 border-gray-200"></div>
    <div class="absolute top-0 left-0 h-14 w-14 rounded-full border-4 border-green-600 border-t-transparent animate-spin"></div>
  </div>

  <!-- Loading Label -->
  <p class="text-gray-600 font-medium tracking-wide">
    Loading data…
  </p>
</div>


        <!-- Error state -->
        <div v-else-if="error" class="bg-red-50 border border-red-200 rounded-lg p-4 print:hidden">
            <p class="text-red-800">{{ error.message }}</p>
        </div>

        <!-- Applications table -->
        <div v-else class="bg-white shadow-sm rounded-lg overflow-hidden print:hidden">
            <div class="overflow-x-auto">
                <table class="min-w-full divide-y divide-gray-200">
                    <thead class="bg-gray-100">
                    <tr>
                        <th scope="col" class="w-12 px-6 py-3">
                            <input
                                type="checkbox"
                                :checked="isAllSelected"
                                class="h-4 w-4 text-green-600 focus:ring-green-400 focus:border-green-50 border-gray-300 rounded"
                                @change="toggleSelectAll"
                            />
                        </th>
                        <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                            Farmer Name
                        </th>
                        <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                            Crop Type
                        </th>
                        <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                            Cover Type
                        </th>
                        <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                            Amount
                        </th>
                        <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                            Location
                        </th>
                        <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                            Submitted Date
                        </th>
                    </tr>
                    </thead>
                    <tbody class="bg-white divide-y divide-gray-200">
                    <tr
                        v-for="application in filteredApplications"
                        :key="application.id"
                        class="hover:bg-gray-50 cursor-pointer transition-colors"
                        @click="handleRowClick(application.id, $event)"
                    >
                        <td class="px-6 py-4 whitespace-nowrap" @click.stop>
                            <input
                                type="checkbox"
                                :checked="isSelected(application.id)"
                                class="h-4 w-4 text-green-600 focus:ring-green-500 border-gray-300 rounded"
                                @change="toggleSelection(application.id)"
                            />
                        </td>
                        <td class="px-6 py-4 whitespace-nowrap">
                            <div class="text-sm font-medium text-gray-900">
                                {{ getFullName(application.dynamicFields) }}
                            </div>
                        </td>
                        <td class="px-6 py-4 whitespace-nowrap">
                            <div class="text-sm text-gray-900">{{ application.dynamicFields?.crop_type || 'N/A' }}</div>
                        </td>
                        <td class="px-6 py-4 whitespace-nowrap">
                            <div class="text-sm text-gray-900">{{ application.dynamicFields?.cover_type || 'N/A' }}</div>
                        </td>
                        <td class="px-6 py-4 whitespace-nowrap">
                            <div class="text-sm text-gray-900">
                                ₱{{ formatAmount(application.dynamicFields?.amount_of_cover) }}
                            </div>
                        </td>
                        <td class="px-6 py-4">
                            <div class="text-sm text-gray-900">{{ getLocation(application.dynamicFields?.lot_1_location) }}</div>
                        </td>
                        <td class="px-6 py-4 whitespace-nowrap">
                            <div class="text-sm text-gray-900">{{ formatDate(application.submittedAt) }}</div>
                        </td>
                    </tr>
                    </tbody>
                </table>
            </div>

            <!-- Empty state -->
            <div v-if="filteredApplications.length === 0" class="text-center py-12 print:hidden">
                <FileText class="mx-auto h-12 w-12 text-gray-400" />
                <h3 class="mt-2 text-sm font-medium text-gray-900">No applications found</h3>
                <p class="mt-1 text-sm text-gray-500">No farmer applications match your current filters.</p>
            </div>
        </div>

        <!-- Filter Modal -->
        <ApplicationFilterModal
            v-model:show="showFilterModal"
            :filters="filters"
            class="print:hidden"
            @apply-filters="applyFilters"
            @reset-filters="resetFilters"
        />

        <!-- Create Batch Modal -->
        <CreateBatchModal :show="showCreateBatchModal" @close="showCreateBatchModal = false" @created="handleBatchCreated" />

        <!-- Print Layout (hidden, only visible when printing) -->
        <div id="print-layout">
            <div v-for="(chunk, chunkIndex) in farmerChunks" :key="chunkIndex">
                <!-- PAGE 1: APPLICATION DETAILS -->
                <div class="pcic-page">
                    <!-- Restructured header into proper 3-column grid layout -->
                    <!-- Header - 3 Column Layout -->
                    <div class="grid grid-cols-12 gap-2 mb-2 text-xs">
                        <!-- Column 1: Requirements, Note, Organization Details (4 cols) -->
                        <div class="col-span-4">
                            <p class="font-bold">REQUIREMENTS:</p>
                            <p class="ml-4">• GOVERNMENT ID OR ANY VALID ID</p>
                            <p class="ml-4">• RSBSA STUB</p>
                            <p class="mt-1"><strong>NOTE:</strong> Please complete details.</p>

                            <div class="mt-2">
                                <p><strong>*Name of FO/PA/COOP/IA/Barangay:</strong></p>
                                <p class="border-b border-black mt-4">{{ chunk[0]?.dynamicFields.organization_name }}</p>
                            </div>

                            <div class="mt-2">
                                <p><strong>Underwriter / Solicitor:</strong></p>
                                <p class="border-b border-black mt-4">{{ chunk[0]?.dynamicFields.underwriter }}</p>
                            </div>

                            <div class="mt-2">
                                <p><strong>Program:</strong> [ ]Regular [ ]Sikat saka [ ]RBSSA [ ]APCP/CAP-PBD [ ] <br/> PUNLA [ ]Corporate Rice Farming [ ]Others:______</p>
                            </div>
                        </div>

                        <!-- Column 2: PCIC Header and Mailing Address (5 cols) -->
                        <div class="col-span-5 text-center">
                            <p class="font-bold text-sm">PHILIPPINE CROP INSURANCE CORPORATION</p>
                            <p>REGION _____</p>
                            <p class="font-bold underline mt-1">APPLICATION FOR CROP INSURANCE</p>
                            <p>(Group Application)</p>

                            <div class="mt-4 text-left">
                                <p><strong>Mailing Address:</strong></p>
                                <p class="border-b border-black mt-4">{{ chunk[0]?.dynamicFields.mailing_address }}</p>
                            </div>
                        </div>


                        <!-- Column 3: FOR PCIC ONLY and RC-UPI-07 (3 cols) -->
                        <div class="col-span-3">
                            <div class="text-right mb-2">
                                <p class="font-bold">RC-UPI-07</p>
                                <p>2017/FEB</p>
                                <p>PAGE 1</p>
                            </div>

                            <div class="border border-black p-1">
                                <p class="font-bold text-center mb-1">FOR PCIC ONLY:</p>
                                <div class="grid grid-cols-2 gap-x-1 text-xs leading-tight">
                                    <div>CIC No. ___________</div>
                                    <div>COC No. ___________</div>
                                    <div>Date Issued: _______</div>
                                    <div>Date Insured From: ___</div>
                                    <div>Crop: [ ]Rice [ ]Corn</div>
                                    <div>Phase: ___________</div>
                                    <div>Phase: Wet ___ Dry ___</div>
                                    <div>Period Insured From: ___</div>
                                    <div>O.R. No.: _________</div>
                                    <div>To: _____________</div>
                                    <div>Date: ___________</div>
                                    <div>Amount Paid: _____</div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Main Farmer Table -->
                    <table class="pcic-table w-full border-collapse border border-black mb-1">
                        <thead>
                        <tr class="bg-gray-100">
                            <th class="border border-black px-0.5 py-0.5 text-xs" rowspan="2">No.</th>
                            <th class="border border-black px-0.5 py-0.5 text-xs" colspan="3">Name of Farmers</th>
                            <th class="border border-black px-0.5 py-0.5 text-xs" rowspan="2">Suffix</th>
                            <th class="border border-black px-0.5 py-0.5 text-xs" rowspan="2">Civil<br/>Status</th>
                            <th class="border border-black px-0.5 py-0.5 text-xs" rowspan="2">Gender</th>
                            <th class="border border-black px-0.5 py-0.5 text-xs" rowspan="2">Address<br/>(Sitio & Barangay)</th>
                            <th class="border border-black px-0.5 py-0.5 text-xs" rowspan="2">Cellphone</th>
                            <th class="border border-black px-0.5 py-0.5 text-xs" rowspan="2">Spouse</th>
                            <th class="border border-black px-0.5 py-0.5 text-xs" rowspan="2">Beneficiary</th>
                            <th class="border border-black px-0.5 py-0.5 text-xs" rowspan="2">Bank name/<br/>Bank no.</th>
                            <th class="border border-black px-0.5 py-0.5 text-xs" rowspan="2">Amount<br/>of Cover</th>
                            <th class="border border-black px-0.5 py-0.5 text-xs" colspan="3">Planting Calendar</th>
                            <th class="border border-black px-0.5 py-0.5 text-xs" rowspan="2">Variety</th>
                        </tr>
                        <tr class="bg-gray-100">
                            <th class="border border-black px-0.5 py-0.5 text-xs">Last Name</th>
                            <th class="border border-black px-0.5 py-0.5 text-xs">First Name</th>
                            <th class="border border-black px-0.5 py-0.5 text-xs">Middle Name</th>
                            <th class="border border-black px-0.5 py-0.5 text-xs">Sowing/DS</th>
                            <th class="border border-black px-0.5 py-0.5 text-xs">TP/Planting</th>
                            <th class="border border-black px-0.5 py-0.5 text-xs">Harvest</th>
                        </tr>
                        </thead>
                        <tbody>
                        <!-- Display actual farmers from chunk -->
                        <tr v-for="(app, index) in chunk" :key="app.id">
                            <td class="border border-black px-0.5 py-0.5 text-center text-xs">{{ chunkIndex * 10 + index + 1 }}</td>
                            <td class="border border-black px-0.5 py-0.5 text-xs">{{ app.dynamicFields.last_name }}</td>
                            <td class="border border-black px-0.5 py-0.5 text-xs">{{ app.dynamicFields.first_name }}</td>
                            <td class="border border-black px-0.5 py-0.5 text-xs">{{ app.dynamicFields.middle_name || 'N/A' }}</td>
                            <td class="border border-black px-0.5 py-0.5 text-center text-xs">{{ app.dynamicFields.suffix || 'N/A' }}</td>
                            <td class="border border-black px-0.5 py-0.5 text-center text-xs">{{ getCivilStatusShort(app.dynamicFields.civil_status) }}</td>
                            <td class="border border-black px-0.5 py-0.5 text-center text-xs">{{ getGenderShort(app.dynamicFields.sex) }}</td>
                            <td class="border border-black px-0.5 py-0.5 text-xs">{{ getBarangay(app.dynamicFields.lot_1_location) }}</td>
                            <td class="border border-black px-0.5 py-0.5 text-xs">{{ app.dynamicFields.cell_phone_number }}</td>
                            <td class="border border-black px-0.5 py-0.5 text-xs">{{ app.dynamicFields.spouse_name || '' }}</td>
                            <td class="border border-black px-0.5 py-0.5 text-xs">{{ app.dynamicFields.primary_beneficiary }}</td>
                            <td class="border border-black px-0.5 py-0.5 text-xs"></td>
                            <td class="border border-black px-0.5 py-0.5 text-center text-xs">₱{{ formatAmount(app.dynamicFields.amount_of_cover) }}</td>
                            <td class="border border-black px-0.5 py-0.5 text-center text-xs">{{ formatShortDate(app.dynamicFields.lot_1_date_sowing) }}</td>
                            <td class="border border-black px-0.5 py-0.5 text-center text-xs">{{ formatShortDate(app.dynamicFields.lot_1_date_planting) }}</td>
                            <td class="border border-black px-0.5 py-0.5 text-center text-xs">{{ formatShortDate(app.dynamicFields.lot_1_date_harvest) }}</td>
                            <td class="border border-black px-0.5 py-0.5 text-xs">{{ app.dynamicFields.lot_1_variety }}</td>
                        </tr>
                        <!-- Fill empty rows if less than 10 farmers -->
                        <tr v-for="n in (10 - chunk.length)" :key="`empty-${n}`">
                            <td class="border border-black px-0.5 py-0.5 text-center text-xs">{{ chunkIndex * 10 + chunk.length + n }}</td>
                            <td class="border border-black px-0.5 py-0.5 text-xs">&nbsp;</td>
                            <td class="border border-black px-0.5 py-0.5 text-xs">&nbsp;</td>
                            <td class="border border-black px-0.5 py-0.5 text-xs">&nbsp;</td>
                            <td class="border border-black px-0.5 py-0.5 text-xs">&nbsp;</td>
                            <td class="border border-black px-0.5 py-0.5 text-xs">&nbsp;</td>
                            <td class="border border-black px-0.5 py-0.5 text-xs">&nbsp;</td>
                            <td class="border border-black px-0.5 py-0.5 text-xs">&nbsp;</td>
                            <td class="border border-black px-0.5 py-0.5 text-xs">&nbsp;</td>
                            <td class="border border-black px-0.5 py-0.5 text-xs">&nbsp;</td>
                            <td class="border border-black px-0.5 py-0.5 text-xs">&nbsp;</td>
                            <td class="border border-black px-0.5 py-0.5 text-xs">&nbsp;</td>
                            <td class="border border-black px-0.5 py-0.5 text-xs">&nbsp;</td>
                            <td class="border border-black px-0.5 py-0.5 text-xs">&nbsp;</td>
                            <td class="border border-black px-0.5 py-0.5 text-xs">&nbsp;</td>
                            <td class="border border-black px-0.5 py-0.5 text-xs">&nbsp;</td>
                        </tr>
                        <tr class="font-bold">
                            <td class="border border-black px-0.5 py-0.5 text-center text-xs" colspan="17">TOTAL</td>
                        </tr>
                        </tbody>
                    </table>

                    <!-- Certifications, Legends, and Premium -->
                    <div class="grid grid-cols-12 gap-0.5 text-xs mb-1">
                        <!-- Column 1: Certifications and Legends (10 cols) -->
                        <div class="col-span-10 flex flex-col h-full">
                            <!-- Top: Certifications (equal height split) -->
                            <div class="grid grid-cols-2 gap-0.5 flex-1" style="max-height: 110px;">
                                <!-- Technologist's Certification -->
                                <div class="border border-black p-0.5 flex flex-col justify-between overflow-hidden">
                                    <div class="flex-1 overflow-hidden">
                                        <p class="font-bold text-center mb-0.5 text-xs">TECHNOLOGIST'S CERTIFICATION</p>
                                        <p class="leading-tight mb-1 text-xs">
                                            I hereby certify that the above farmer-applicants have<br />
                                            for crop already planted/to be planted. (Encircle). NO RISK INSURED<br />
                                            against has occurred.
                                        </p>
                                    </div>
                                    <div class="grid grid-cols-3 gap-1 mt-auto pt-0.5 border-t border-black">
                                        <div class="text-center text-xs leading-tight">
                                            Signature Over Printed Name
                                        </div>
                                        <div class="text-center text-xs leading-tight">Office</div>
                                        <div class="text-center text-xs leading-tight">Date</div>
                                    </div>
                                </div>

                                <!-- Certification -->
                                <div class="border border-black p-0.5 flex flex-col justify-between overflow-hidden">
                                    <div class="flex-1 overflow-hidden">
                                        <p class="font-bold text-center mb-0.5 text-xs">CERTIFICATION</p>
                                        <p class="leading-tight mb-1 text-xs">
                                            I hereby certify that the above information are true and correct<br />
                                            to the best of my knowledge.
                                        </p>
                                    </div>
                                    <div class="grid grid-cols-2 gap-1 mt-auto pt-0.5 border-t border-black">
                                        <div class="text-center text-xs leading-tight">
                                            Signature Over Printed Name
                                        </div>
                                        <div class="text-center text-xs leading-tight">Date</div>
                                    </div>
                                </div>
                            </div>

                            <!-- Bottom: Legends (constrained height) -->
                            <div class="border border-black p-0.5 mt-0.5 legends-section" style="max-height: 50px;">
                                <p class="font-bold mr-2 text-xs inline">LEGENDS:</p>
                                <span class="text-xs leading-tight">
                                    FO - Farmers' Organization | PA - Farmers' Association | COOP - Cooperative | IA - Irrigation Association
                                </span>
                            </div>
                        </div>

                        <!-- Column 2: Premium Computation (2 cols) -->
                        <div class="col-span-2">
                            <div class="border border-black p-0.5 overflow-hidden" style="max-height: 140px;">
                                <p class="font-bold text-center mb-0.5 text-xs">
                                    PREMIUM COMPUTATION (FOR PCIC ONLY)
                                </p>
                                <div class="text-xs leading-tight space-y-0.5">
                                    <p>Premium Rate: ___________</p>
                                    <p>Sum Insured: ___________</p>
                                    <p>Gov't Premium Subsidy(GPS): ___________</p>
                                    <p>Gross Premium: ___________</p>
                                    <p>Less: GPS: ___________</p>
                                    <p>Farmer's share (less outstanding): ___________</p>
                                    <p>Total: ___________</p>
                                    <p>Net Premium Due to PCIC: ___________</p>
                                </div>
                            </div>
                        </div>
                    </div>

                </div>

                <!-- PAGE 2: APPLICATION INSURANCE -->
                <div class="pcic-page page-break">
                    <!-- Insurance Table -->
                    <table class="pcic-table w-full border-collapse border border-black mb-1">
                        <thead>
                        <tr class="bg-gray-100">
                            <th class="border border-black px-0.5 py-0.5 text-xs" rowspan="2">No.</th>
                            <th class="border border-black px-0.5 py-0.5 text-xs" rowspan="2">Name of the Farmers<br/>(Follow the order on page1)</th>
                            <th class="border border-black px-0.5 py-0.5 text-xs" rowspan="2">Farm Location</th>
                            <th class="border border-black px-0.5 py-0.5 text-xs" rowspan="2">Area<br/>(ha)</th>
                            <th class="border border-black px-0.5 py-0.5 text-xs" rowspan="2">Land Category<br/>/ Soil Type</th>
                            <th class="border border-black px-0.5 py-0.5 text-xs" rowspan="2">Tenural<br/>Status</th>
                            <th class="border border-black px-0.5 py-0.5 text-xs" colspan="4">Adjacent Lot Owners</th>
                            <th class="border border-black px-0.5 py-0.5 text-xs" rowspan="2">Signature</th>
                        </tr>
                        <tr class="bg-gray-100">
                            <th class="border border-black px-0.5 py-0.5 text-xs">North</th>
                            <th class="border border-black px-0.5 py-0.5 text-xs">South</th>
                            <th class="border border-black px-0.5 py-0.5 text-xs">East</th>
                            <th class="border border-black px-0.5 py-0.5 text-xs">West</th>
                        </tr>
                        </thead>
                        <tbody>
                        <!-- Display actual farmers from chunk -->
                        <tr v-for="(app, index) in chunk" :key="app.id">
                            <td class="border border-black px-0.5 py-0.5 text-center text-xs">{{ chunkIndex * 10 + index + 1 }}</td>
                            <td class="border border-black px-0.5 py-0.5 text-xs">
                                <div>{{ getFullName(app.dynamicFields) }}</div>
                                <div class="mt-0.5">
                                    IP <input type="checkbox" :checked="app.dynamicFields.indigenous_people" class="align-middle" />
                                    Tribe: {{ app.dynamicFields.tribe }}
                                </div>
                            </td>
                            <td class="border border-black px-0.5 py-0.5 text-xs">{{ getLocation(app.dynamicFields.lot_1_location) }}</td>
                            <td class="border border-black px-0.5 py-0.5 text-center text-xs">{{ app.dynamicFields.lot_1_area }} ha</td>
                            <td class="border border-black px-0.5 py-0.5 text-xs">{{ app.dynamicFields.lot_1_land_category }}<br/>{{ app.dynamicFields.lot_1_soil_type }}</td>
                            <td class="border border-black px-0.5 py-0.5 text-center text-xs">{{ app.dynamicFields.lot_1_tenurial_status }}</td>
                            <td class="border border-black px-0.5 py-0.5 text-xs">{{ app.dynamicFields.lot_1_boundaries?.north || 'N/A' }}</td>
                            <td class="border border-black px-0.5 py-0.5 text-xs">{{ app.dynamicFields.lot_1_boundaries?.south || 'N/A' }}</td>
                            <td class="border border-black px-0.5 py-0.5 text-xs">{{ app.dynamicFields.lot_1_boundaries?.east || 'N/A' }}</td>
                            <td class="border border-black px-0.5 py-0.5 text-xs">{{ app.dynamicFields.lot_1_boundaries?.west || 'N/A' }}</td>
                            <td class="border border-black p-0 text-center align-middle text-xs">
                                <div class="w-[75px] h-[50px] overflow-hidden mx-auto">
                                    <img
                                        :src="app.fileUploads[0]"
                                        alt="Farmer Signature"
                                        class="w-full h-full object-cover"
                                        @error="handleImageError"
                                    />
                                </div>
                            </td>
                        </tr>
                        <!-- Fill empty rows if less than 10 farmers -->
                        <tr v-for="n in (10 - chunk.length)" :key="`empty-${n}`">
                            <td class="border border-black px-0.5 py-0.5 text-center text-xs">{{ chunkIndex * 10 + chunk.length + n }}</td>
                            <td class="border border-black px-0.5 py-0.5 text-xs">
                                <div>&nbsp;</div>
                                <div class="mt-0.5">IP <input type="checkbox" class="align-middle" /> Tribe: ___________</div>
                            </td>
                            <td class="border border-black px-0.5 py-0.5 text-xs">&nbsp;</td>
                            <td class="border border-black px-0.5 py-0.5 text-xs">&nbsp;</td>
                            <td class="border border-black px-0.5 py-0.5 text-xs">&nbsp;</td>
                            <td class="border border-black px-0.5 py-0.5 text-xs">&nbsp;</td>
                            <td class="border border-black px-0.5 py-0.5 text-xs">&nbsp;</td>
                            <td class="border border-black px-0.5 py-0.5 text-xs">&nbsp;</td>
                            <td class="border border-black px-0.5 py-0.5 text-xs">&nbsp;</td>
                            <td class="border border-black px-0.5 py-0.5 text-xs">&nbsp;</td>
                            <td class="border border-black px-0.5 py-0.5 text-xs">&nbsp;</td>
                            <td class="border border-black px-0.5 py-0.5 text-xs">&nbsp;</td>
                            <td class="border border-black px-0.5 py-0.5 text-xs">&nbsp;</td>
                        </tr>
                        <tr class="font-bold">
                            <td class="border border-black px-0.5 py-0.5 text-center text-xs" colspan="11">TOTAL</td>
                        </tr>
                        </tbody>
                    </table>

                    <!-- Legends -->
                    <div class="text-xs border border-black p-1 legends-section">
                    <div class="grid grid-cols-3 gap-2">
                        <div>
                            <p class="font-bold">LAND CATEGORY/SOIL TYPE:</p>
                            <p class="mt-0.5"><strong>For Rice Crop (Land Category):</strong></p>
                            <p class="leading-tight">(1) Irrigated - Irrigated</p>
                            <p class="leading-tight">(2) Irrigated - Pump Well Pump/Shallow Tube Well (STW)</p>
                            <p class="leading-tight">(3) Irrigated - Open Source (Swlp, Creek, River)</p>
                            <p class="leading-tight">(4) Rainfed</p>
                        </div>
                        <div>
                            <p class="font-bold">&nbsp;</p>
                            <p class="mt-0.5"><strong>For Corn Crop (Soil Type/Topography):</strong></p>
                            <p class="leading-tight">(A) Broad Plain - Clay Loam</p>
                            <p class="leading-tight">(B) Broad Plain - Silty Clay Loam</p>
                            <p class="leading-tight">(C) Broad Plain - Silty Loam</p>
                            <p class="leading-tight">(E) Rolling/Upland</p>
                        </div>
                        <div>
                            <p class="font-bold">TENURAL STATUS:</p>
                            <p class="mt-0.5 leading-tight">(1) Landowner (2) Lessee (3) Other (please specify)</p>
                        </div>
                    </div>
                </div>
                </div>

                <!-- Page break after each chunk except the last one -->
                <div v-if="chunkIndex < farmerChunks.length - 1" class="page-break"></div>
            </div>
        </div>
    </AuthenticatedLayout>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import AuthenticatedLayout from '@/layouts/AuthenticatedLayout.vue'
import ApplicationFilterModal from '@/components/modals/ApplicationFilterModal.vue'
import CreateBatchModal from '@/components/modals/CreateBatchModal.vue'
import { Filter, Edit, Trash2, FileText, Printer, Plus } from 'lucide-vue-next'
import {
    ADMIN_NAVIGATION,
    MUNICIPAL_AGRICULTURIST_NAVIGATION,
    AGRICULTURAL_EXTENSION_WORKER_NAVIGATION
} from '@/lib/navigation'
import { useApplicationBatchStore, useApplicationStore } from '@/stores/applications'
import { useVerificationStore } from '@/stores/verification'

const router = useRouter()
const authStore = useAuthStore()
const applicationStore = useApplicationStore()
const batchStore = useApplicationBatchStore()
const verificationStore = useVerificationStore()

// State
const loading = ref(false)
const error = ref(null)
const selectedApplications = ref([])
const showFilterModal = ref(false)
const showCreateBatchModal = ref(false)
const filters = ref({
    cropType: '',
    coverType: '',
    location: '',
    dateFrom: '',
    dateTo: '',
    amountMin: '',
    amountMax: ''
})
const selectedBatch = ref('')
const batches = ref([])

const navigation = computed(() => {
    const role = authStore.userData?.roles?.[0]?.name
    if (role === 'Admin') return ADMIN_NAVIGATION
    if (role === 'Municipal Agriculturist') return MUNICIPAL_AGRICULTURIST_NAVIGATION
    if (role === 'Agricultural Extension Worker') return AGRICULTURAL_EXTENSION_WORKER_NAVIGATION
    return []
})

const roleTitle = computed(() => {
    const role = authStore.userData?.roles?.[0]?.name
    return role || 'Staff Portal'
})

const filteredApplications = computed(() => {
    let apps = verificationStore.applications.value || []

    // Apply filters
    if (filters.value.cropType) {
        apps = apps.filter(app => app.dynamicFields.crop_type === filters.value.cropType)
    }
    if (filters.value.coverType) {
        apps = apps.filter(app => app.dynamicFields.cover_type === filters.value.coverType)
    }
    if (filters.value.location) {
        apps = apps.filter(app => {
            const location = getLocation(app.dynamicFields.lot_1_location).toLowerCase()
            return location.includes(filters.value.location.toLowerCase())
        })
    }
    if (filters.value.dateFrom) {
        apps = apps.filter(app => new Date(app.submittedAt) >= new Date(filters.value.dateFrom))
    }
    if (filters.value.dateTo) {
        apps = apps.filter(app => new Date(app.submittedAt) <= new Date(filters.value.dateTo))
    }
    if (filters.value.amountMin) {
        apps = apps.filter(app => app.dynamicFields.amount_of_cover >= parseFloat(filters.value.amountMin))
    }
    if (filters.value.amountMax) {
        apps = apps.filter(app => app.dynamicFields.amount_of_cover <= parseFloat(filters.value.amountMax))
    }

    return apps
})

const farmerChunks = computed(() => {
    const chunks = []
    const apps = filteredApplications.value || []

    for (let i = 0; i < apps.length; i += 10) {
        chunks.push(apps.slice(i, i + 10))
    }

    return chunks
})

const isAllSelected = computed(() => {
    return (filteredApplications.value && filteredApplications.value.length > 0) &&
        selectedApplications.value.length === filteredApplications.value.length
})

// Methods
const fetchApplicationsList = async () => {
    loading.value = true
    error.value = null
    if (selectedBatch.value) {
        const result = await verificationStore.fetchApplicationByBatchId(selectedBatch.value)
        if (!result.success) error.value = result.error
    } else {
        const result = await verificationStore.fetchApplications()
        if (!result.success) error.value = result.error
    }
    loading.value = false
}

const fetchBatches = async () => {
    const result = await batchStore.fetchAllBatches()
    if (result.success) {
        batches.value = result.data
    }
}

watch(selectedBatch, async (newBatch) => {
    await fetchApplicationsList()
})

const getFullName = (fields) => {
    if (!fields) return 'N/A'
    const parts = [fields.first_name, fields.middle_name, fields.last_name].filter(Boolean)
    return parts.length ? parts.join(' ') : 'N/A'
}

const getLocation = (location) => {
    if (!location) return 'N/A'
    const parts = [
        location.barangay,
        location.municipality || location.city,
        location.province
    ].filter(Boolean)
    return parts.length ? parts.join(', ') : 'N/A'
}

const getBarangay = (location) => {
    if (!location) return 'N/A'
    return location.barangay || 'N/A'
}

const getGenderShort = (gender) => {
    if (!gender) return 'N/A'
    if (gender.toLowerCase() === 'male') return 'M'
    if (gender.toLowerCase() === 'female') return 'F'
    return gender.charAt(0).toUpperCase()
}

const getCivilStatusShort = (status) => {
    if (!status) return 'N/A'
    if (status.toLowerCase() === 'single') return 'S'
    if (status.toLowerCase() === 'married') return 'M'
    if (status.toLowerCase() === 'widow') return 'W'
    return status.charAt(0).toUpperCase()
}

const formatAmount = (amount) => {
    return new Intl.NumberFormat('en-PH').format(amount)
}

const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
    })
}

const isSelected = (id) => {
    return selectedApplications.value.includes(id)
}

const toggleSelection = (id) => {
    const index = selectedApplications.value.indexOf(id)
    if (index > -1) {
        selectedApplications.value.splice(index, 1)
    } else {
        selectedApplications.value.push(id)
    }
}

const toggleSelectAll = () => {
    if (isAllSelected.value) {
        selectedApplications.value = []
    } else {
        selectedApplications.value = filteredApplications.value.map(app => app.id)
    }
}

const handleRowClick = (id, event) => {
    // Don't navigate if clicking on checkbox
    if (event.target.type === 'checkbox') return

    router.push({ name: 'agriculturist-submit-crop-data-detail', params: { id } })
}

const handleUpdate = () => {
    if (selectedApplications.value.length === 1) {
        router.push({ name: 'agriculturist-submit-crop-data-detail', params: { id: selectedApplications.value[0] } })
    }
}

const handleDelete = async () => {
    if (!confirm(`Are you sure you want to delete ${selectedApplications.value.length} application(s)?`)) {
        return
    }

    loading.value = true

    for (const id of selectedApplications.value) {
        await applicationStore.deleteApplication(id)
    }

    selectedApplications.value = []
    await fetchApplicationsList()
}

const applyFilters = (newFilters) => {
    filters.value = { ...newFilters }
    showFilterModal.value = false
}

const resetFilters = () => {
    filters.value = {
        cropType: '',
        coverType: '',
        location: '',
        dateFrom: '',
        dateTo: '',
        amountMin: '',
        amountMax: ''
    }
    showFilterModal.value = false
}

const handlePrint = () => {
    window.print()
}


const formatShortDate = (dateString) => {
    if (!dateString) return 'N/A'
    return new Date(dateString).toLocaleDateString('en-US', {
        month: '2-digit',
        day: '2-digit',
        year: 'numeric'
    })
}

const handleImageError = (event) => {
    event.target.src = 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="200" height="200"%3E%3Crect fill="%23ddd" width="200" height="200"/%3E%3Ctext fill="%23999" x="50%25" y="50%25" text-anchor="middle" dy=".3em"%3EImage not found%3C/text%3E%3C/svg%3E'
}

const handleBatchCreated = async () => {
  await fetchBatches()
}

const handleForwardToPCIC = async () => {
    if (selectedApplications.value.length === 0) return
    if (!confirm(`Forward ${selectedApplications.value.length} application(s) to PCIC?`)) return
    const success = await verificationStore.forwardApplicationToPCIC(selectedApplications.value)
    if (success) {
        selectedApplications.value = []
        await fetchApplicationsList()
        alert('Applications forwarded to PCIC successfully.')
    } else {
        alert('Failed to forward applications to PCIC.')
    }
}

onMounted(() => {
    fetchBatches()
    fetchApplicationsList()
})
</script>
<style>
@media print {
    @page {
        size: letter landscape;
        margin: 0.4in;
    }

    /* Ensure all pages break properly, not just first two */
    .pcic-page {
        page-break-after: always;
    }

    /* Remove overflow hidden from legends to ensure visibility */
    .legends-section {
        overflow: visible;
        page-break-inside: avoid;
    }
}

/* Hide print layout on screen */
#print-layout {
    display: none;
}

@media print {
    #print-layout {
        display: block !important;
    }
}
</style>
