export const ADMIN_ROUTES = [
  {
    path: "/admin/dashboard",
    name: "admin-dashboard",
    component: () => import("@/pages/admin/AdminDashboard.vue"),
    meta: {
      title: "Admin Dashboard",
      guard: "auth",
      roles: ["ADMIN"],
    },
  },
  {
    path: "/admin/staff/register",
    name: "admin-register-staff",
    component: () => import("@/pages/admin/staff/RegisterStaff.vue"),
    meta: {
      title: "Register New Staff",
      guard: "auth",
      roles: ["ADMIN"],
    },
  },
  {
    path: "/admin/applications/new",
    name: "admin-new-application",
    component: () => import("@/pages/admin/applications/NewApplication.vue"),
    meta: {
      title: "Create New Application Type",
      guard: "auth",
      roles: ["ADMIN"],
    },
  },
  {
    path: "/admin/applications/all",
    name: "admin-view-applications",
    component: () => import("@/pages/admin/applications/ViewApplications.vue"),
    meta: {
      title: "All Application Types",
      guard: "auth",
      roles: ["ADMIN"],
    },
  },
  {
    path: "/admin/roles",
    name: "admin-roles-permissions",
    component: () => import("@/pages/admin/roles/RolesPermissions.vue"),
    meta: {
      title: "Roles & Permissions",
      guard: "auth",
      roles: ["ADMIN"],
    },
  },
]

export const UNDERWRITER_ROUTES = [
    {
        path: '/underwriter/dashboard',
        name: 'underwriter-dashboard',
        component: () => import('@/pages/underwriter/UnderwriterDashboard.vue'),
        meta: {
            title: 'Insurance Underwriter Dashboard',
            guard: 'auth',
            roles: ['UNDERWRITER'],
        },
    },
    {
        path: '/underwriter/applications/all',
        name: 'underwriter-applications-all',
        component: () => import('@/pages/underwriter/applications/ViewApplications.vue'),
        meta: {
            title: 'All Applications',
            guard: 'auth',
            roles: ['UNDERWRITER'],
        },
    },
     {
        path: '/underwriter/damage/claim-review',
        name: 'damage-claim-review',
        component: () => import('@/pages/underwriter/applications/DamageClaimReview.vue'),
        meta: {
            title: 'All Applications',
            guard: 'auth',
            roles: ['UNDERWRITER'],
        },
    },
    {
        path: '/underwriter/risk-factors',
        name: 'underwriter-risk-factors',
        component: () => import('@/pages/underwriter/applications/ViewApplications.vue'),
        meta: {
            title: 'Risk Factors',
            guard: 'auth',
            roles: ['UNDERWRITER'],
        },
    },
    {
        path: '/underwriter/risk-history',
        name: 'underwriter-risk-history',
        component: () => import('@/pages/underwriter/applications/ViewApplications.vue'),
        meta: {
            title: 'Risk History',
            guard: 'auth',
            roles: ['UNDERWRITER'],
        },
    },
    {
        path: '/underwriter/risk-geo',
        name: 'underwriter-risk-geo',
        component: () => import('@/pages/underwriter/applications/ViewApplications.vue'),
        meta: {
            title: 'Geographic Analysis',
            guard: 'auth',
            roles: ['UNDERWRITER'],
        },
    },
    {
        path: '/underwriter/guidelines-eligibility',
        name: 'underwriter-guidelines-eligibility',
        component: () => import('@/pages/underwriter/GuidelinesEligibility.vue'),
        meta: {
            title: 'Guidelines & Eligibility',
            guard: 'auth',
            roles: ['UNDERWRITER'],
        },
    },
    {
        path: '/underwriter/guidelines-coverage',
        name: 'underwriter-guidelines-coverage',
        component: () => import('@/pages/underwriter/GuidelinesCoverage.vue'),
        meta: {
            title: 'Guidelines & Coverage',
            guard: 'auth',
            roles: ['UNDERWRITER'],
        },
    },
    {
        path: '/underwriter/guidelines-manual',
        name: 'underwriter-guidelines-manual',
        component: () => import('@/pages/underwriter/GuidelinesManual.vue'),
        meta: {
            title: 'Guidelines Manual',
            guard: 'auth',
            roles: ['UNDERWRITER'],
        },
    },
    {
        path: '/underwriter/reports',
        name: 'underwriter-reports',
        component: () => import('@/pages/underwriter/Reports.vue'),
        meta: {
            title: 'Underwriter Reports',
            guard: 'auth',
            roles: ['UNDERWRITER'],
        },
    },
]

export const CLAIMS_PROCESSOR_ROUTES = [
  {
    path: "/claims-processor/dashboard",
    name: "claims-processor-dashboard",
    component: () => import("@/pages/claims-processor/ClaimsProcessorDashboard.vue"),
    meta: {
      title: "Claims Processor Dashboard",
      guard: "auth",
      roles: ["CLAIM_PROCESSOR"],
    },
  },
]

export const TELLER_ROUTES = [
  {
    path: "/teller/dashboard",
    name: "teller-dashboard",
    component: () => import("@/pages/teller/TellerDashboard.vue"),
    meta: {
      title: "Teller Dashboard",
      guard: "auth",
      roles: ["TELLER"],
    },
  },
]
