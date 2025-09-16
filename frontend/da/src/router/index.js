import { createWebHistory, createRouter } from "vue-router"
import { useAuthStore } from "@/stores/auth"

const APP_NAME = import.meta.env.VITE_APP_NAME

const routes = [
  // Admin Dashboard
  {
    path: "/admin/dashboard",
    name: "admin-dashboard",
    component: () => import("@/pages/admin/AdminDashboard.vue"),
    meta: {
      title: "Admin Dashboard",
      guard: "auth",
      roles: ["Admin"],
    },
  },
  // Register Staff Page for Admin
  {
    path: "/admin/staff/register",
    name: "admin-register-staff",
    component: () => import("@/pages/admin/staff/RegisterStaff.vue"),
    meta: {
      title: "Register New Staff",
      guard: "auth",
      roles: ["Admin"],
      permissions: ["CAN_CREATE_USER"],
    },
  },
  // Create New Application Type Page for Admin
  {
    path: "/admin/applications/new",
    name: "admin-new-application",
    component: () => import("@/pages/admin/applications/NewApplication.vue"),
    meta: {
      title: "Create New Application Type",
      guard: "auth",
      roles: ["Admin"],
      permissions: ["CAN_CREATE_APPLICATION"],
    },
  },
  // View All Applications Page for Admin
  {
    path: "/admin/applications/all",
    name: "admin-view-applications",
    component: () => import("@/pages/admin/applications/ViewApplications.vue"),
    meta: {
      title: "All Application Types",
      guard: "auth",
      roles: ["Admin"],
      permissions: ["CAN_VIEW_APPLICATION"],
    },
  },
  // Roles & Permissions Page for Admin
  {
    path: "/admin/roles",
    name: "admin-roles-permissions",
    component: () => import("@/pages/admin/roles/RolesPermissions.vue"),
    meta: {
      title: "Roles & Permissions",
      guard: "auth",
      roles: ["Admin"],
      permissions: ["CAN_MANAGE_ROLES"],
    },
  },
  // All Users Page for Admin
  {
    path: "/admin/users",
    name: "admin-users",
    component: () => import("@/pages/admin/users/AllUsers.vue"),
    meta: {
      title: "All Users",
      guard: "auth",
      roles: ["Admin"],
      permissions: ["CAN_VIEW_USER"],
    },
  },
  // Approvals Page for Admin
  {
    path: "/admin/approvals",
    name: "admin-approvals",
    component: () => import("@/pages/admin/approvals/Approvals.vue"),
    meta: {
      title: "Approvals",
      guard: "auth",
      roles: ["Admin"],
      permissions: ["CAN_APPROVE_APPLICATION"],
    },
  },
  // Analytics Page for Admin
  {
    path: "/admin/analytics",
    name: "admin-analytics",
    component: () => import("@/pages/admin/analytics/Analytics.vue"),
    meta: {
      title: "Analytics",
      guard: "auth",
      roles: ["Admin"],
      permissions: ["CAN_VIEW_ANALYTICS"],
    },
  },

    // Municipality Dashboard
    {
        path: "/admin/dashboard",
        name: "admin-dashboard",
        component: () => import("@/pages/admin/AdminDashboard.vue"),
        meta: {
            title: "Admin Dashboard",
            guard: "auth",
            roles: ["Admin"],
        },
    },

    // Municipality Dashboard
    {
        path: "/municipality/dashboard",
        name: "municipality-dashboard",
        component: () => import("@/pages/municipal-agriculturists/Dashboard.vue"),
        meta: {
            title: "Municipality Dashboard",
            guard: "auth",
            roles: ["Municipal Agriculturists"],
        },
    },

    // Agricultural Extension Workers
    {
        path: "/agricultural-extension-worker/dashboard",
        name: "agricultural-extension-worker-dashboard",
        component: () => import("@/pages/agricultural-extension-workers/Dashboard.vue"),
        meta: {
            title: "Municipality Dashboard",
            guard: "auth",
            roles: ["Agricultural Extension Workers"],
        },
    },



  // Legacy Dashboard (redirects to appropriate dashboard)
  {
    path: "/dashboard",
    name: "dashboard",
    redirect: (to) => {
      const store = useAuthStore()
      return store.getRedirectPath()
    },
    meta: {
      title: "Dashboard",
      guard: "auth",
    },
  },

  // Login
  {
    path: "/",
    name: "login",
    component: () => import("@/pages/auth/Login.vue"),
    query: {
      reset: "reset",
    },
    meta: {
      title: "PCIC Staff Login",
      guard: "guest",
    },
  },

  // Error Pages
  {
    path: "/access-denied",
    name: "access-denied",
    component: () => import("@/pages/errors/AccessDenied.vue"),
    meta: {
      title: "Access Denied",
    },
  },
  {
    path: "/page-not-found",
    name: "page-not-found",
    component: () => import("@/pages/errors/404.vue"),
    meta: {
      title: "Page Not Found",
    },
  },
  {
    path: "/:pathMatch(.*)*",
    redirect: "/page-not-found",
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

// Main navigation guard
router.beforeEach(async (to, from, next) => {
  const store = useAuthStore()

  // Skip if already navigating to the same route to prevent loops
  if (to.path === from.path) {
    return next()
  }

  const requiresAuth = to.matched.some(route => route.meta.guard === 'auth')
  const requiresGuest = to.matched.some(route => route.meta.guard === 'guest')
  const requiredRoles = to.meta.roles || []

  // Initialize store if not already done
  if (!store.isInitialized) {
    try {
      await store.initialize()
    } catch (error) {
      console.error('Failed to initialize store:', error)
    }
  }

  // Handle guest routes (login, register, etc.)
  if (requiresGuest) {
    if (store.isAuthenticate) {
      console.log('Already authenticated, redirecting to dashboard')
      const redirectPath = store.getRedirectPath()
      // Prevent redirect loop by checking if we're already going to the target
      if (to.name !== redirectPath.name) {
        return next(redirectPath)
      }
    }
    return next()
  }

  // Handle protected routes
  if (requiresAuth) {
    // If not authenticated, try to authenticate
    if (!store.isAuthenticate) {
      try {
        console.log('Not authenticated, attempting to fetch user data...')
        await store.getData()
      } catch (error) {
        console.error('Authentication failed:', error)
        return next({ name: 'login', query: { redirect: to.fullPath } })
      }
    }

    // Check if user has valid staff role
    if (!store.isValidStaff) {
      console.error('User is not valid staff:', store.userData?.roles)
      return next({ name: 'access-denied' })
    }

    // Check permission-based access if required
    const requiredPermissions = to.meta.permissions || []
    if (requiredPermissions.length > 0) {
      const hasRequiredPermission = requiredPermissions.some(permission =>
        store.hasPermission(permission)
      )

      if (!hasRequiredPermission) {
        console.log('Insufficient permissions, redirecting to dashboard')
        const redirectPath = store.getRedirectPath()
        // Only redirect if not already going to the target
        if (to.name !== redirectPath.name) {
          return next(redirectPath)
        }
      }
    }

    // Handle dashboard redirection
    if (to.name === 'dashboard') {
      const redirectPath = store.getRedirectPath()
      if (redirectPath.name !== to.name) {
        console.log('Redirecting to role-specific dashboard:', redirectPath)
        return next(redirectPath)
      }
    }
  }

  // If we've made it here, allow the navigation
  next()
})

// Page Title and Metadata
router.beforeEach((to, from, next) => {
  const nearestWithTitle = to.matched
    .slice()
    .reverse()
    .find((r) => r.meta && r.meta.title)

  const nearestWithMeta = to.matched
    .slice()
    .reverse()
    .find((r) => r.meta && r.meta.metaTags)

  if (nearestWithTitle) {
    document.title = nearestWithTitle.meta.title + " - " + APP_NAME
  } else {
    document.title = APP_NAME
  }

  Array.from(document.querySelectorAll("[data-vue-router-controlled]")).map((el) => el.parentNode.removeChild(el))

  if (!nearestWithMeta) return next()

  nearestWithMeta.meta.metaTags
    .map((tagDef) => {
      const tag = document.createElement("meta")

      Object.keys(tagDef).forEach((key) => {
        tag.setAttribute(key, tagDef[key])
      })

      tag.setAttribute("data-vue-router-controlled", "")

      return tag
    })
    .forEach((tag) => document.head.appendChild(tag))

  next()
})

export default router
