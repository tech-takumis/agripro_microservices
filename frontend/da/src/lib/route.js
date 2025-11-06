export const ADMIN_ROUTES = [
    {
        path: '/admin/dashboard',
        name: 'admin-dashboard',
        component: () => import('@/pages/admin/AdminDashboard.vue'),
        meta: {
            title: 'Admin Dashboard',
            guard: 'auth',
            role: 'ADMIN'
        }
    },
    {
        path: '/admin/users',
        name: 'admin-users',
        component: () => import('@/pages/admin/users/AllUsers.vue'),
        meta: {
            title: 'All Users',
            guard: 'auth',
            role: 'ADMIN'
        }
    },
    {
        path: '/admin/users/:id',
        name: 'admin-user-view',
        component: () => import('@/pages/admin/users/ViewUser.vue'),
        meta: {
            title: 'View User',
            guard: 'auth',
            role: 'ADMIN'
        }
    },
    {
        path: '/admin/users/:id/edit',
        name: 'admin-user-edit',
        component: () => import('@/pages/admin/users/EditUser.vue'),
        meta: {
            title: 'Edit User',
            guard: 'auth',
            role: 'ADMIN'
        }
    },
    {
        path: '/admin/roles',
        name: 'admin-roles-permissions',
        component: () => import('@/pages/admin/roles/RolesPermissions.vue'),
        meta: {
            title: 'Roles & Permissions',
            guard: 'auth',
            role: 'ADMIN'
        }
    },
    {
        path: '/admin/applications',
        name: 'admin-applications',
        component: () => import('@/pages/admin/applications/ViewApplications.vue'),
        meta: {
            title: 'View Applications',
            guard: 'auth',
            role: 'ADMIN'
        }
    },
    {
        path: '/admin/applications/new',
        name: 'admin-new-application',
        component: () => import('@/pages/admin/applications/NewApplication.vue'),
        meta: {
            title: 'New Application',
            guard: 'auth',
            role: 'ADMIN'
        }
    },
    {
        path: '/admin/approvals',
        name: 'admin-approvals',
        component: () => import('@/pages/admin/approvals/Approvals.vue'),
        meta: {
            title: 'Approvals',
            guard: 'auth',
            role: 'ADMIN'
        }
    },
    {
        path: '/admin/analytics',
        name: 'admin-analytics',
        component: () => import('@/pages/admin/analytics/Analytics.vue'),
        meta: {
            title: 'Analytics',
            guard: 'auth',
            role: 'ADMIN'
        }
    },
    {
        path: '/admin/reports',
        name: 'admin-reports',
        component: () => import('@/pages/admin/AdminReports.vue'),
        meta: {
            title: 'Reports',
            guard: 'auth',
            role: 'ADMIN'
        }
    }
];

export const MUNICIPALITY_ROUTES = [
    {
        path: '/agriculturist/dashboard',
        name: 'agriculturist-dashboard',
        component: () => import('@/pages/agriculturist/AgriculturistDashboard.vue'),
        meta: {
            title: 'Municipal Agriculturist Dashboard',
            guard: 'auth'
        }
    },
    {
        path: '/agriculturist/submit-crop-data',
        name: 'agriculturist-submit-crop-data',
        component: () => import('@/pages/agriculturist/applications/ApplicationList.vue'),
        meta: {
            title: 'Submit Crop Data',
            guard: 'auth'
        }
    },
    {
        path: '/agriculturist/submit-crop-data/:id',
        name: 'agriculturist-submit-crop-data-detail',
        component: () => import('@/pages/agriculturist/applications/ApplicationDetail.vue'),
        meta: {
            title: 'Submit Crop Data Detail',
            guard: 'auth'
        }
    },
    {
        path: '/agriculturist/develop-plans',
        name: 'agriculturist-develop-plans',
        component: () => import('@/pages/agriculturist/operations/AgriculturistDevelopPlans.vue'),
        meta: {
            title: 'Develop Plans',
            guard: 'auth'
        }
    },
    {
        path: '/agriculturist/coordinate-agencies',
        name: 'agriculturist-coordinate-agencies',
        component: () => import('@/pages/agriculturist/operations/AgriculturistCoordinateAgencies.vue'),
        meta: {
            title: 'Coordinate Agencies',
            guard: 'auth'
        }
    },
    {
        path: '/agriculturist/provide-infrastructure',
        name: 'agriculturist-provide-infrastructure',
        component: () => import('@/pages/agriculturist/operations/AgriculturistProvideInfrastructure.vue'),
        meta: {
            title: 'Provide Infrastructure',
            guard: 'auth'
        }
    },
    {
        path: '/agriculturist/technical-advice',
        name: 'agriculturist-technical-advice',
        component: () => import('@/pages/agriculturist/operations/AgriculturistTechnicalAdvice.vue'),
        meta: {
            title: 'Provide Technical Advice',
            guard: 'auth'
        }
    },
    {
        path: '/agriculturist/monitor-programs',
        name: 'agriculturist-monitor-programs',
        component: () => import('@/pages/agriculturist/operations/AgriculturistMonitorPrograms.vue'),
        meta: {
            title: 'Monitor Programs',
            guard: 'auth'
        }
    },
    {
        path: '/agriculturist/conduct-surveys',
        name: 'agriculturist-conduct-surveys',
        component: () => import('@/pages/agriculturist/survey/AgriculturistConductSurveys.vue'),
        meta: {
            title: 'Conduct Surveys',
            guard: 'auth'
        }
    },
    {
        path: '/agriculturist/process-claims',
        name: 'agriculturist-process-claims',
        component: () => import('@/pages/agriculturist/claims/AgriculturistProcessClaims.vue'),
        meta: {
            title: 'Process Claims',
            guard: 'auth'
        }
    },
    {
        path: '/agriculturist/reports',
        name: 'agriculturist-reports',
        component: () => import('@/pages/agriculturist/report/AgriculturistReports.vue'),
        meta: {
            title: 'Reports',
            guard: 'auth'
        }
    },
    {
        path: '/agriculturist/message',
        name: 'agriculturist-message',
        component: () => import('@/pages/agriculturist/message/AgriculturistMessage.vue'),
        meta: {
            title: 'Message',
            guard: 'auth'
        }
    }
];

export const AGRICULTURAL_EXTENSION_WORKER_ROUTES = [
    {
        path: '/extension-worker/dashboard',
        name: 'extension-worker-dashboard',
        component: () => import('@/pages/extension-worker/ExtensionWorkerDashboard.vue'),
        meta: {
            title: 'Extension Worker Dashboard',
            guard: 'auth'
        }
    },
    {
        path: '/extension-worker/submit-crop-data',
        name: 'extension-worker-submit-crop-data',
        component: () => import('@/pages/extension-worker/ExtensionWorkerSubmitCropData.vue'),
        meta: {
            title: 'Submit Crop Data',
            guard: 'auth'
        }
    },
    {
        path: '/extension-worker/conduct-training',
        name: 'extension-worker-conduct-training',
        component: () => import('@/pages/extension-worker/ExtensionWorkerConductTraining.vue'),
        meta: {
            title: 'Conduct Training',
            guard: 'auth'
        }
    },
    {
        path: '/extension-worker/facilitate-tech-adoption',
        name: 'extension-worker-facilitate-tech-adoption',
        component: () => import('@/pages/extension-worker/ExtensionWorkerFacilitateTechAdoption.vue'),
        meta: {
            title: 'Facilitate Tech Adoption',
            guard: 'auth'
        }
    },
    {
        path: '/extension-worker/diagnostic-services',
        name: 'extension-worker-diagnostic-services',
        component: () => import('@/pages/extension-worker/ExtensionWorkerDiagnosticServices.vue'),
        meta: {
            title: 'Provide Diagnostic Services',
            guard: 'auth'
        }
    },
    {
        path: '/extension-worker/market-access',
        name: 'extension-worker-market-access',
        component: () => import('@/pages/extension-worker/ExtensionWorkerMarketAccess.vue'),
        meta: {
            title: 'Enhance Market Access',
            guard: 'auth'
        }
    },
    {
        path: '/extension-worker/promote-sustainability',
        name: 'extension-worker-promote-sustainability',
        component: () => import('@/pages/extension-worker/ExtensionWorkerPromoteSustainability.vue'),
        meta: {
            title: 'Promote Sustainability',
            guard: 'auth'
        }
    },
    {
        path: '/extension-worker/support-veterinary',
        name: 'extension-worker-support-veterinary',
        component: () => import('@/pages/extension-worker/ExtensionWorkerSupportVeterinary.vue'),
        meta: {
            title: 'Support Veterinary',
            guard: 'auth'
        }
    },
    {
        path: '/extension-worker/process-claims',
        name: 'extension-worker-process-claims',
        component: () => import('@/pages/extension-worker/ExtensionWorkerProcessClaims.vue'),
        meta: {
            title: 'Process Claims',
            guard: 'auth'
        }
    }
];