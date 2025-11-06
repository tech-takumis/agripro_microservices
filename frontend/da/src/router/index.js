import { createWebHistory, createRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import {
    ADMIN_ROUTES,
    MUNICIPALITY_ROUTES,
    AGRICULTURAL_EXTENSION_WORKER_ROUTES
} from '@/lib/route';
import Register from '@/pages/auth/Register.vue';

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
    // Registration
    {
        path: '/register',
        name: 'register',
        component: Register,
        meta: {
            title: 'User Registration',
            guard: 'guest',
            role: 'PERSOR',
            requiresToken: true
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
                const defaultRoute = auth.defaultRoute || '/admin/dashboard'; // fallback to admin dashboard
                if (to.path === '/') {
                    // If user lands on login page and is authenticated, redirect to dashboard
                    return next(defaultRoute);
                }
                return next(defaultRoute);
            }
            // Skip auth check for guest routes
            await auth.initialize(true);
            return next();
        }

        // For protected routes, initialize normally
        if (!auth.isAuthenticated) {
            // Don't initialize if we're already on the login page
            // This prevents page refresh after failed login attempts
            if (to.name !== 'login') {
                await auth.initialize();
            }
        }

        // Check authentication for protected routes
        if (!auth.isAuthenticated) {
            if (to.name !== 'login') {
                console.log('[Router Guard] Redirecting to login...');
                return next({ name: 'login' });
            }
            console.log('[Router Guard] Already on login, not redirecting.');
            return next();
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

    // In case of register route, check for token
    if (to.name === 'register') {
        const token = to.query.token;
        if (!token) {
            return next({ name: 'access-denied' });
        }
    }
});
