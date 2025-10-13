import { FileBarChart2, FileCheck, LayoutDashboard, MessageCircle, Sprout, Users } from 'lucide-vue-next'

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
                to: { name: 'admin-users' }
            },
            {
                title: 'Roles & Permissions',
                to: { name: 'admin-roles-permissions' }
            }
        ]
    },
    {
        title: 'Reports',
        icon: FileBarChart2,
        to: { name: 'admin-reports' }
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
                to: { name: 'municipal-agriculturist-submit-crop-data' }
            },
            {
                title: 'Develop Plans',
                to: { name: 'municipal-agriculturist-develop-plans' }
            },
            {
                title: 'Coordinate Agencies',
                to: { name: 'municipal-agriculturist-coordinate-agencies' }
            },
            {
                title: 'Monitor Programs',
                to: { name: 'municipal-agriculturist-monitor-programs' }
            },
            {
                title: 'Conduct Surveys',
                to: { name: 'municipal-agriculturist-conduct-surveys' }
            }
        ]
    },
    {
        title: 'Claims',
        icon: FileCheck,
        children: [
            {
                title: 'Process Claims',
                to: { name: 'municipal-agriculturist-process-claims' }
            }
        ]
    },
    {
        title: 'Reports',
        icon: FileBarChart2,
        to: { name: 'municipal-agriculturist-reports' }
    },
    {
        title: 'Messages',
        icon: MessageCircle,
        to: { name: 'municipal-agriculturist-message' }
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
                to: { name: 'extension-worker-submit-crop-data' }
            },
            {
                title: 'Conduct Training',
                to: { name: 'extension-worker-conduct-training' }
            },
            {
                title: 'Facilitate Tech Adoption',
                to: { name: 'extension-worker-facilitate-tech-adoption' }
            },
            {
                title: 'Provide Diagnostic Services',
                to: { name: 'extension-worker-diagnostic-services' }
            },
            {
                title: 'Enhance Market Access',
                to: { name: 'extension-worker-market-access' }
            },
            {
                title: 'Promote Sustainability',
                to: { name: 'extension-worker-promote-sustainability' }
            },
            {
                title: 'Support Veterinary',
                to: { name: 'extension-worker-support-veterinary' }
            }
        ]
    },
    {
        title: 'Claims',
        icon: FileCheck,
        children: [
            {
                title: 'Process Claims',
                to: { name: 'extension-worker-process-claims' }
            }
        ]
    }
];
