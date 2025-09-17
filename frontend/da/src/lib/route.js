export const ADMIN_ROUTES = [
    {
        path: '/admin/dashboard',
        name: 'admin-dashboard',
        component: () => import('@/pages/admin/AdminDashboard.vue'),
        meta: {
            title: 'Admin Dashboard',
            guard: 'auth',
            roles: ['Admin'],
        },
    },
    {
        path: '/admin/register-staff',
        name: 'admin-register-staff',
        component: () => import('@/pages/admin/staff/RegisterStaff.vue'),
        meta: {
            title: 'Register New Staff',
            guard: 'auth',
            roles: ['Admin'],
            permissions: ['CAN_CREATE_USER'],
        },
    },
    {
        path: '/admin/users',
        name: 'admin-users',
        component: () => import('@/pages/admin/users/AllUsers.vue'),
        meta: {
            title: 'All Users',
            guard: 'auth',
            roles: [
                'Admin',
                'Municipal Agriculturists',
                'Agricultural Extension Workers',
            ],
            permissions: ['CAN_VIEW_USER'],
        },
    },
    {
        path: '/admin/roles-permissions',
        name: 'admin-roles-permissions',
        component: () => import('@/pages/admin/roles/RolesPermissions.vue'),
        meta: {
            title: 'Roles & Permissions',
            guard: 'auth',
            roles: ['Admin'],
            permissions: ['CAN_MANAGE_ROLES'],
        },
    },
    {
        path: '/admin/reports',
        name: 'admin-reports',
        component: () => import('@/pages/admin/AdminReports.vue'),
        meta: {
            title: 'Reports',
            guard: 'auth',
            roles: ['Admin', 'Municipal Agriculturists'],
            permissions: ['CAN_VIEW_REPORTS']
        }
    }
]

export const MUNICIPALITY_ROUTES = [
    {
        path: '/municipal-agriculturist/dashboard',
        name: 'municipal-agriculturist-dashboard',
        component: () =>
            import(
                '@/pages/municipal-agriculturist/MunicipalAgriculturistDashboard.vue'
            ),
        meta: {
            title: 'Municipal Agriculturist Dashboard',
            guard: 'auth',
            roles: ['Municipal Agriculturists'],
        },
    },
    {
        path: '/municipal-agriculturist/submit-crop-data',
        name: 'municipal-agriculturist-submit-crop-data',
        component: () =>
            import(
                '@/pages/municipal-agriculturist/MunicipalAgriculturistSubmitCropData.vue'
            ),
        meta: {
            title: 'Submit Crop Data',
            guard: 'auth',
            roles: [
                'Municipal Agriculturists',
                'Agricultural Extension Workers',
            ],
            permissions: ['CAN_SUBMIT_CROP_DATA'],
        },
    },
    {
        path: '/municipal-agriculturist/develop-plans',
        name: 'municipal-agriculturist-develop-plans',
        component: () =>
            import(
                '@/pages/municipal-agriculturist/MunicipalAgriculturistDevelopPlans.vue'
            ),
        meta: {
            title: 'Develop Plans',
            guard: 'auth',
            roles: ['Municipal Agriculturists'],
            permissions: ['CAN_DEVELOP_PLANS'],
        },
    },
    {
        path: '/municipal-agriculturist/coordinate-agencies',
        name: 'municipal-agriculturist-coordinate-agencies',
        component: () =>
            import(
                '@/pages/municipal-agriculturist/MunicipalAgriculturistCoordinateAgencies.vue'
            ),
        meta: {
            title: 'Coordinate Agencies',
            guard: 'auth',
            roles: ['Municipal Agriculturists'],
            permissions: ['CAN_COORDINATE_AGENCIES'],
        },
    },
    {
        path: '/municipal-agriculturist/provide-infrastructure',
        name: 'municipal-agriculturist-provide-infrastructure',
        component: () =>
            import(
                '@/pages/municipal-agriculturist/MunicipalAgriculturistProvideInfrastructure.vue'
            ),
        meta: {
            title: 'Provide Infrastructure',
            guard: 'auth',
            roles: ['Municipal Agriculturists'],
            permissions: ['CAN_PROVIDE_INFRASTRUCTURE'],
        },
    },
    {
        path: '/municipal-agriculturist/technical-advice',
        name: 'municipal-agriculturist-technical-advice',
        component: () =>
            import(
                '@/pages/municipal-agriculturist/MunicipalAgriculturistTechnicalAdvice.vue'
            ),
        meta: {
            title: 'Provide Technical Advice',
            guard: 'auth',
            roles: ['Municipal Agriculturists'],
            permissions: ['CAN_PROVIDE_TECHNICAL_ADVICE'],
        },
    },
    {
        path: '/municipal-agriculturist/monitor-programs',
        name: 'municipal-agriculturist-monitor-programs',
        component: () =>
            import(
                '@/pages/municipal-agriculturist/MunicipalAgriculturistMonitorPrograms.vue'
            ),
        meta: {
            title: 'Monitor Programs',
            guard: 'auth',
            roles: ['Municipal Agriculturists'],
            permissions: ['CAN_MONITOR_PROGRAMS'],
        },
    },
    {
        path: '/municipal-agriculturist/conduct-surveys',
        name: 'municipal-agriculturist-conduct-surveys',
        component: () =>
            import(
                '@/pages/municipal-agriculturist/MunicipalAgriculturistConductSurveys.vue'
            ),
        meta: {
            title: 'Conduct Surveys',
            guard: 'auth',
            roles: ['Municipal Agriculturists'],
            permissions: ['CAN_CONDUCT_SURVEYS'],
        },
    },
    {
        path: '/municipal-agriculturist/process-claims',
        name: 'municipal-agriculturist-process-claims',
        component: () =>
            import(
                '@/pages/municipal-agriculturist/MunicipalAgriculturistProcessClaims.vue'
            ),
        meta: {
            title: 'Process Claims',
            guard: 'auth',
            roles: [
                'Municipal Agriculturists',
                'Agricultural Extension Workers',
            ],
            permissions: ['CAN_PROCESS_CLAIM'],
        },
    },
    {
        path: '/municipal-agriculturist/reports',
        name: 'municipal-agriculturist-reports',
        component: () =>
            import(
                '@/pages/municipal-agriculturist/MunicipalAgriculturistReports.vue'
            ),
        meta: {
            title: 'Reports',
            guard: 'auth',
            roles: ['Municipal Agriculturists'],
            permissions: ['CAN_VIEW_REPORTS'],
        },
    },
]

export const AGRICULTURAL_EXTENSION_WORKER_ROUTES = [
    {
        path: '/extension-worker/dashboard',
        name: 'extension-worker-dashboard',
        component: () => import('@/pages/extension-worker/ExtensionWorkerDashboard.vue'),
        meta: {
            title: 'Extension Worker Dashboard',
            guard: 'auth',
            roles: ['Agricultural Extension Workers']
        }
    },
    {
        path: '/extension-worker/submit-crop-data',
        name: 'extension-worker-submit-crop-data',
        component: () => import('@/pages/extension-worker/ExtensionWorkerSubmitCropData.vue'),
        meta: {
            title: 'Submit Crop Data',
            guard: 'auth',
            roles: ['Municipal Agriculturists', 'Agricultural Extension Workers'],
            permissions: ['CAN_SUBMIT_CROP_DATA']
        }
    },
    {
        path: '/extension-worker/conduct-training',
        name: 'extension-worker-conduct-training',
        component: () => import('@/pages/extension-worker/ExtensionWorkerConductTraining.vue'),
        meta: {
            title: 'Conduct Training',
            guard: 'auth',
            roles: ['Agricultural Extension Workers'],
            permissions: ['CAN_CONDUCT_TRAINING']
        }
    },
    {
        path: '/extension-worker/facilitate-tech-adoption',
        name: 'extension-worker-facilitate-tech-adoption',
        component: () => import('@/pages/extension-worker/ExtensionWorkerFacilitateTechAdoption.vue'),
        meta: {
            title: 'Facilitate Tech Adoption',
            guard: 'auth',
            roles: ['Agricultural Extension Workers'],
            permissions: ['CAN_FACILITATE_TECH_ADOPTION']
        }
    },
    {
        path: '/extension-worker/diagnostic-services',
        name: 'extension-worker-diagnostic-services',
        component: () => import('@/pages/extension-worker/ExtensionWorkerDiagnosticServices.vue'),
        meta: {
            title: 'Provide Diagnostic Services',
            guard: 'auth',
            roles: ['Agricultural Extension Workers'],
            permissions: ['CAN_PROVIDE_DIAGNOSTIC_SERVICES']
        }
    },
    {
        path: '/extension-worker/market-access',
        name: 'extension-worker-market-access',
        component: () => import('@/pages/extension-worker/ExtensionWorkerMarketAccess.vue'),
        meta: {
            title: 'Enhance Market Access',
            guard: 'auth',
            roles: ['Agricultural Extension Workers'],
            permissions: ['CAN_ENHANCE_MARKET_ACCESS']
        }
    },
    {
        path: '/extension-worker/promote-sustainability',
        name: 'extension-worker-promote-sustainability',
        component: () => import('@/pages/extension-worker/ExtensionWorkerPromoteSustainability.vue'),
        meta: {
            title: 'Promote Sustainability',
            guard: 'auth',
            roles: ['Agricultural Extension Workers'],
            permissions: ['CAN_PROMOTE_SUSTAINABILITY']
        }
    },
    {
        path: '/extension-worker/support-veterinary',
        name: 'extension-worker-support-veterinary',
        component: () => import('@/pages/extension-worker/ExtensionWorkerSupportVeterinary.vue'),
        meta: {
            title: 'Support Veterinary',
            guard: 'auth',
            roles: ['Agricultural Extension Workers'],
            permissions: ['CAN_SUPPORT_VETERINARY']
        }
    },
    {
        path: '/extension-worker/process-claims',
        name: 'extension-worker-process-claims',
        component: () => import('@/pages/extension-worker/ExtensionWorkerProcessClaims.vue'),
        meta: {
            title: 'Process Claims',
            guard: 'auth',
            roles: ['Municipal Agriculturists', 'Agricultural Extension Workers'],
            permissions: ['CAN_PROCESS_CLAIM']
        }
    }
];