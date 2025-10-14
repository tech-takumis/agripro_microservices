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
        path: '/admin/staff/register',
        name: 'admin-register-staff',
        component: () => import('@/pages/admin/staff/RegisterStaff.vue'),
        meta: {
            title: 'Register New Staff',
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
        path: '/municipal-agriculturist/dashboard',
        name: 'municipal-agriculturist-dashboard',
        component: () => import('@/pages/municipal-agriculturist/MunicipalAgriculturistDashboard.vue'),
        meta: {
            title: 'Municipal Agriculturist Dashboard',
            guard: 'auth'
        }
    },
    {
        path: '/municipal-agriculturist/submit-crop-data',
        name: 'municipal-agriculturist-submit-crop-data',
        component: () => import('@/pages/municipal-agriculturist/applications/ApplicationList.vue'),
        meta: {
            title: 'Submit Crop Data',
            guard: 'auth'
        }
    },
    {
        path: '/municipal-agriculturist/submit-crop-data/:id',
        name: 'municipal-agriculturist-submit-crop-data-detail',
        component: () => import('@/pages/municipal-agriculturist/applications/ApplicationDetail.vue'),
        meta: {
            title: 'Submit Crop Data Detail',
            guard: 'auth'
        }
    },
    {
        path: '/municipal-agriculturist/develop-plans',
        name: 'municipal-agriculturist-develop-plans',
        component: () => import('@/pages/municipal-agriculturist/MunicipalAgriculturistDevelopPlans.vue'),
        meta: {
            title: 'Develop Plans',
            guard: 'auth'
        }
    },
    {
        path: '/municipal-agriculturist/coordinate-agencies',
        name: 'municipal-agriculturist-coordinate-agencies',
        component: () => import('@/pages/municipal-agriculturist/MunicipalAgriculturistCoordinateAgencies.vue'),
        meta: {
            title: 'Coordinate Agencies',
            guard: 'auth'
        }
    },
    {
        path: '/municipal-agriculturist/provide-infrastructure',
        name: 'municipal-agriculturist-provide-infrastructure',
        component: () => import('@/pages/municipal-agriculturist/MunicipalAgriculturistProvideInfrastructure.vue'),
        meta: {
            title: 'Provide Infrastructure',
            guard: 'auth'
        }
    },
    {
        path: '/municipal-agriculturist/technical-advice',
        name: 'municipal-agriculturist-technical-advice',
        component: () => import('@/pages/municipal-agriculturist/MunicipalAgriculturistTechnicalAdvice.vue'),
        meta: {
            title: 'Provide Technical Advice',
            guard: 'auth'
        }
    },
    {
        path: '/municipal-agriculturist/monitor-programs',
        name: 'municipal-agriculturist-monitor-programs',
        component: () => import('@/pages/municipal-agriculturist/MunicipalAgriculturistMonitorPrograms.vue'),
        meta: {
            title: 'Monitor Programs',
            guard: 'auth'
        }
    },
    {
        path: '/municipal-agriculturist/conduct-surveys',
        name: 'municipal-agriculturist-conduct-surveys',
        component: () => import('@/pages/municipal-agriculturist/MunicipalAgriculturistConductSurveys.vue'),
        meta: {
            title: 'Conduct Surveys',
            guard: 'auth'
        }
    },
    {
        path: '/municipal-agriculturist/process-claims',
        name: 'municipal-agriculturist-process-claims',
        component: () => import('@/pages/municipal-agriculturist/MunicipalAgriculturistProcessClaims.vue'),
        meta: {
            title: 'Process Claims',
            guard: 'auth'
        }
    },
    {
        path: '/municipal-agriculturist/reports',
        name: 'municipal-agriculturist-reports',
        component: () => import('@/pages/municipal-agriculturist/MunicipalAgriculturistReports.vue'),
        meta: {
            title: 'Reports',
            guard: 'auth'
        }
    },
    {
        path: '/municipal-agriculturist/message',
        name: 'municipal-agriculturist-message',
        component: () => import('@/pages/municipal-agriculturist/message/MunicipalAgriculturistMessage.vue'),
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