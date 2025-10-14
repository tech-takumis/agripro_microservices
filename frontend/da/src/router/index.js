import { createWebHistory, createRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import {
    ADMIN_ROUTES,
    MUNICIPALITY_ROUTES,
    AGRICULTURAL_EXTENSION_WORKER_ROUTES
} from '@/lib/route';

const APP_NAME = import.meta.env.VITE_APP_NAME;

const routes = [
    ...ADMIN_ROUTES,
    ...MUNICIPALITY_ROUTES,
    ...AGRICULTURAL_EXTENSION_WORKER_ROUTES,
    // Login
    {
        path: '/',
        name: 'login',
        component: () => import('@/pages/auth/Login.vue'),
        meta: {
            title: 'PCIC Staff Login',
            guard: 'guest'
        }
    },
    // Error Pages
    {
        path: '/access-denied',
        name: 'access-denied',
        component: () => import('@/pages/errors/AccessDenied.vue'),
        meta: {
            title: 'Access Denied'
        }
    },
    {
        path: '/page-not-found',
        name: 'page-not-found',
        component: () => import('@/pages/errors/404.vue'),
        meta: {
            title: 'Page Not Found'
        }
    },
    {
        path: '/:pathMatch(.*)*',
        redirect: '/page-not-found'
    }
];

export const router = createRouter({
    history: createWebHistory(),
    routes
});

// Global navigation guard
router.beforeEach(async (to, from, next) => {
    const auth = useAuthStore();

    // Set page title
    document.title = to.meta.title
        ? `${to.meta.title} - ${APP_NAME}`
        : APP_NAME;

    try {
        // Handle guest routes (like login) first
        if (to.meta.guard === 'guest') {
            if (auth.isAuthenticated) {
                // If already authenticated, redirect to user's default route
                const defaultRoute = auth.defaultRoute;
                if (defaultRoute) {
                    return next(defaultRoute);
                }
                return next('/');
            }
            // Skip auth check for guest routes
            await auth.initialize(true);
            return next();
        }

        // For protected routes, initialize normally
        if (!auth.isAuthenticated) {
            await auth.initialize();
        }

        // Check authentication for protected routes
        if (!auth.isAuthenticated) {
            return next({ name: 'login' });
        }

        // Check role-based access
        if (to.meta.role && !auth.hasRole(to.meta.role)) {
            console.warn(`Access denied: User does not have required role ${to.meta.role}`);
            return next({ name: 'access-denied' });
        }

        // User is authenticated and has required role, allow navigation
        return next();
    } catch (error) {
        console.error('Navigation guard error:', error);
        // Clear auth state and redirect to login on any error
        auth.$reset();
        if (to.name !== 'login') {
            return next({ name: 'login' });
        }
        return next();
    }
});
