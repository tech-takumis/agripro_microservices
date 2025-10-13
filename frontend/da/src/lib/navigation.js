import {
    LayoutDashboard,
    Users,
    FileCheck,
    BarChart3,
    UserPlus,
    ListChecks,
    FileBarChart2,
    Tractor,
    Sprout,
    BookOpen,
    Map,
    Network,
    MonitorCheck,
    ShoppingCart,
    HeartPulse,
    Pipette,
    MessageCircleIcon
} from 'lucide-vue-next';

/**
 * Admin navigation items
 */
export const ADMIN_NAVIGATION = [
    {
        title: 'Dashboard',
        icon: LayoutDashboard,
        to: { name: 'admin-dashboard' },
        exact: true
    },
    {
        title: 'User Management',
        icon: Users,
        children: [
            {
                title: 'All Users',
                to: { name: 'admin-users' },
                permission: 'CAN_VIEW_USER'
            },
            {
                title: 'Roles & Permissions',
                to: { name: 'admin-roles-permissions' },
                permission: 'CAN_MANAGE_ROLES'
            }
        ]
    },
    {
        title: 'Reports',
        icon: FileBarChart2,
        to: { name: 'admin-reports' },
        permission: 'CAN_VIEW_REPORTS'
    }
];

/**
 * Admin quick actions for dashboard
 */
export const adminQuickActions = [
    {
        title: 'Register New Staff',
        description: 'Add a new staff member to the system',
        icon: UserPlus,
        to: { name: 'admin-register-staff' },
        permission: 'CAN_CREATE_USER'
    },
    {
        title: 'Manage Roles',
        description: 'Configure roles and permissions',
        icon: Users,
        to: { name: 'admin-roles-permissions' },
        permission: 'CAN_MANAGE_ROLES'
    },
    {
        title: 'View Reports',
        description: 'Generate system reports',
        icon: FileBarChart2,
        to: { name: 'admin-reports' },
        permission: 'CAN_VIEW_REPORTS'
    }
];

/**
 * Municipal Agriculturists navigation items
 */
export const MUNICIPAL_AGRICULTURIST_NAVIGATION = [
    {
        title: 'Dashboard',
        icon: LayoutDashboard,
        to: { name: 'municipal-agriculturist-dashboard' },
        exact: true
    },
    {
        title: 'Agricultural Operations',
        icon: Sprout,
        children: [
            {
                title: 'Submit Crop Data',
                to: { name: 'municipal-agriculturist-submit-crop-data' },
                permission: 'CAN_SUBMIT_CROP_DATA'
            },
            {
                title: 'Develop Plans',
                to: { name: 'municipal-agriculturist-develop-plans' },
                permission: 'CAN_DEVELOP_PLANS'
            },
            {
                title: 'Coordinate Agencies',
                to: { name: 'municipal-agriculturist-coordinate-agencies' },
                permission: 'CAN_COORDINATE_AGENCIES'
            },
            {
                title: 'Provide Infrastructure',
                to: { name: 'municipal-agriculturist-provide-infrastructure' },
                permission: 'CAN_PROVIDE_INFRASTRUCTURE'
            },
            {
                title: 'Provide Technical Advice',
                to: { name: 'municipal-agriculturist-technical-advice' },
                permission: 'CAN_PROVIDE_TECHNICAL_ADVICE'
            },
            {
                title: 'Monitor Programs',
                to: { name: 'municipal-agriculturist-monitor-programs' },
                permission: 'CAN_MONITOR_PROGRAMS'
            },
            {
                title: 'Conduct Surveys',
                to: { name: 'municipal-agriculturist-conduct-surveys' },
                permission: 'CAN_CONDUCT_SURVEYS'
            }
        ]
    },
    {
        title: 'Claims',
        icon: FileCheck,
        children: [
            {
                title: 'Process Claims',
                to: { name: 'municipal-agriculturist-process-claims' },
                permission: 'CAN_PROCESS_CLAIM'
            }
        ]
    },
    {
        title: 'Reports',
        icon: FileBarChart2,
        to: { name: 'municipal-agriculturist-reports' },
        permission: 'CAN_VIEW_REPORTS'
    },
    {
        title: 'Messages',
        icon: MessageCircleIcon,
        to: { name: 'municipal-agriculturist-message' },
        // permission: 'CAN_VIEW_MESSAGES'
    }
];

/**
 * Municipal Agriculturists quick actions for dashboard
 */
export const municipalAgriculturistQuickActions = [
    {
        title: 'Submit Crop Data',
        description: 'Record yield or damage reports',
        icon: Sprout,
        to: { name: 'municipal-agriculturist-submit-crop-data' },
        permission: 'CAN_SUBMIT_CROP_DATA'
    },
    {
        title: 'Process Claims',
        description: 'Handle insurance claims for farmers',
        icon: FileCheck,
        to: { name: 'municipal-agriculturist-process-claims' },
        permission: 'CAN_PROCESS_CLAIM'
    },
    {
        title: 'View Reports',
        description: 'Access agricultural reports',
        icon: FileBarChart2,
        to: { name: 'municipal-agriculturist-reports' },
        permission: 'CAN_VIEW_REPORTS'
    },
    {
        title: 'Conduct Surveys',
        description: 'Perform area and needs assessments',
        icon: Map,
        to: { name: 'municipal-agriculturist-conduct-surveys' },
        permission: 'CAN_CONDUCT_SURVEYS'
    }
];

/**
 * Agricultural Extension Workers navigation items
 */
export const AGRICULTURAL_EXTENSION_WORKER_NAVIGATION = [
    {
        title: 'Dashboard',
        icon: LayoutDashboard,
        to: { name: 'extension-worker-dashboard' },
        exact: true
    },
    {
        title: 'Agricultural Operations',
        icon: Sprout,
        children: [
            {
                title: 'Submit Crop Data',
                to: { name: 'extension-worker-submit-crop-data' },
                permission: 'CAN_SUBMIT_CROP_DATA'
            },
            {
                title: 'Conduct Training',
                to: { name: 'extension-worker-conduct-training' },
                permission: 'CAN_CONDUCT_TRAINING'
            },
            {
                title: 'Facilitate Tech Adoption',
                to: { name: 'extension-worker-facilitate-tech-adoption' },
                permission: 'CAN_FACILITATE_TECH_ADOPTION'
            },
            {
                title: 'Provide Diagnostic Services',
                to: { name: 'extension-worker-diagnostic-services' },
                permission: 'CAN_PROVIDE_DIAGNOSTIC_SERVICES'
            },
            {
                title: 'Enhance Market Access',
                to: { name: 'extension-worker-market-access' },
                permission: 'CAN_ENHANCE_MARKET_ACCESS'
            },
            {
                title: 'Promote Sustainability',
                to: { name: 'extension-worker-promote-sustainability' },
                permission: 'CAN_PROMOTE_SUSTAINABILITY'
            },
            {
                title: 'Support Veterinary',
                to: { name: 'extension-worker-support-veterinary' },
                permission: 'CAN_SUPPORT_VETERINARY'
            }
        ]
    },
    {
        title: 'Claims',
        icon: FileCheck,
        children: [
            {
                title: 'Process Claims',
                to: { name: 'extension-worker-process-claims' },
                permission: 'CAN_PROCESS_CLAIM'
            }
        ]
    }
];

/**
 * Agricultural Extension Workers quick actions for dashboard
 */
export const agriculturalExtensionWorkerQuickActions = [
    {
        title: 'Submit Crop Data',
        description: 'Record yield or damage reports',
        icon: Sprout,
        to: { name: 'extension-worker-submit-crop-data' },
        permission: 'CAN_SUBMIT_CROP_DATA'
    },
    {
        title: 'Process Claims',
        description: 'Handle insurance claims for farmers',
        icon: FileCheck,
        to: { name: 'extension-worker-process-claims' },
        permission: 'CAN_PROCESS_CLAIM'
    },
    {
        title: 'Conduct Training',
        description: 'Organize farmer workshops',
        icon: BookOpen,
        to: { name: 'extension-worker-conduct-training' },
        permission: 'CAN_CONDUCT_TRAINING'
    }
];