import { createWebHistory, createRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import {
    ADMIN_ROUTES,
    MUNICIPALITY_ROUTES,
    AGRICULTURAL_EXTENSION_WORKER_ROUTES
} from '@/lib/route';

const APP_NAME = import.meta.env.VITE_APP_NAME;

// Combine routes from all route arrays
const routes = [
    ...ADMIN_ROUTES,
    ...MUNICIPALITY_ROUTES,
    ...AGRICULTURAL_EXTENSION_WORKER_ROUTES,
    // Login
    {
        path: '/',
        name: 'login',
        component: () => import('@/pages/auth/Login.vue'),
        query: {
            reset: 'reset'
        },
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

const router = createRouter({
    history: createWebHistory(),
    routes
});

// Main navigation guard
router.beforeEach(async (to, from, next) => {
    const store = useAuthStore();

    // Skip if already navigating to the same route to prevent loops
    if (to.path === from.path) {
        return next();
    }

    const requiresAuth = to.matched.some(route => route.meta.guard === 'auth');
    const requiresGuest = to.matched.some(route => route.meta.guard === 'guest');

    // Handle guest routes (login, register, etc.) - no need to initialize store
    if (requiresGuest) {
        // Only check authentication if store is already initialized
        if (store.isInitialized && store.isAuthenticate) {
            console.log('Already authenticated, redirecting to dashboard');
            const redirectPath = store.getRedirectPath();
            // Prevent redirect loop by checking if we're already going to the target
            if (to.name !== redirectPath.name) {
                return next(redirectPath);
            }
        }
        return next();
    }

    // Initialize store only when needed (for authenticated routes)
    if (requiresAuth && !store.isInitialized) {
        try {
            await store.initialize();
        } catch (error) {
            console.error('Failed to initialize store:', error);
        }
    }

    // Handle protected routes
    if (requiresAuth) {
        // If not authenticated, try to authenticate
        if (!store.isAuthenticate) {
            try {
                console.log('Not authenticated, attempting to fetch user data...');
                await store.getData();
            } catch (error) {
                console.error('Authentication failed:', error);
                return next({ name: 'login', query: { redirect: to.fullPath } });
            }
        }

        // Check if user has valid staff role
        if (!store.isValidStaff) {
            console.error('User is not valid staff:', store.userData?.roles);
            return next({ name: 'access-denied' });
        }

        // Check permission-based access if required
        const requiredPermissions = to.meta.permissions || [];
        if (requiredPermissions.length > 0) {
            const hasRequiredPermission = requiredPermissions.some(permission =>
                store.hasPermission(permission)
            );

            if (!hasRequiredPermission) {
                console.log('Insufficient permissions, redirecting to dashboard');
                const redirectPath = store.getRedirectPath();
                // Only redirect if not already going to the target
                if (to.name !== redirectPath.name) {
                    return next(redirectPath);
                }
            }
        }

        // Handle dashboard redirection
        if (to.name === 'dashboard') {
            const redirectPath = store.getRedirectPath();
            if (redirectPath.name !== to.name) {
                console.log('Redirecting to role-specific dashboard:', redirectPath);
                return next(redirectPath);
            }
        }
    }

    // If we've made it here, allow the navigation
    next();
});

// Page Title and Metadata
router.beforeEach((to, from, next) => {
    const nearestWithTitle = to.matched
        .slice()
        .reverse()
        .find((r) => r.meta && r.meta.title);

    const nearestWithMeta = to.matched
        .slice()
        .reverse()
        .find((r) => r.meta && r.meta.metaTags);

    if (nearestWithTitle) {
        document.title = nearestWithTitle.meta.title + ' - ' + APP_NAME;
    } else {
        document.title = APP_NAME;
    }

    Array.from(document.querySelectorAll('[data-vue-router-controlled]')).map((el) => el.parentNode.removeChild(el));

    if (!nearestWithMeta) return next();

    nearestWithMeta.meta.metaTags
        .map((tagDef) => {
            const tag = document.createElement('meta');
            Object.keys(tagDef).forEach((key) => {
                tag.setAttribute(key, tagDef[key]);
            });
            tag.setAttribute('data-vue-router-controlled', '');
            return tag;
        })
        .forEach((tag) => document.head.appendChild(tag));

    next();
});

export default router;