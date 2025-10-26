import { createWebHistory, createRouter } from "vue-router"
// import { useUserStore } from "@/stores/user"
import { useAuthStore } from "@/stores/auth"
import {
  ADMIN_ROUTES,
  UNDERWRITER_ROUTES,
  CLAIMS_PROCESSOR_ROUTES,
  TELLER_ROUTES,
} from "@/lib/routes"

const APP_NAME = import.meta.env.VITE_APP_NAME

const routes = [
  ...ADMIN_ROUTES,
  ...UNDERWRITER_ROUTES,
  ...CLAIMS_PROCESSOR_ROUTES,
  ...TELLER_ROUTES,
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
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

// Main navigation guard
router.beforeEach(async (to, from, next) => {
  const store = useAuthStore()

  const requiresAuth = to.matched.some((route) => route.meta.guard === "auth")
  const requiresGuest = to.matched.some((route) => route.meta.guard === "guest")
  const requiredRoles = to.meta.roles

  // Only fetch user data if route requires auth and user is not authenticated
  if (requiresAuth && !store.isAuthenticate) {
    try {
      console.log("Fetching authenticated user...")
      await store.getAuthenticated()
      console.log("Authenticated user fetched successfully")
    } catch (error) {
      console.error("Auth check failed:", error)
      store.$reset()
      return next({ name: "login" })
    }
  }

  // Check authentication and role-based access
  if (requiresAuth) {
    if (!store.isAuthenticate) {
      console.log("User not authenticated, redirecting to login")
      return next({ name: "login" })
    }

    // Validate PCIC staff access
    if (!store.userData?.roles || store.userData.roles.length === 0) {
      console.error("User is not valid staff:", store.userData?.roles)
      return next({ name: "access-denied" })
    }

    console.log("User is valid staff with roles:", store.userData?.roles)

    // Check role-based access (user must have at least one of the required roles)
    if (requiredRoles && requiredRoles.length > 0) {
      const userRoles = store.userData?.roles || []

      console.log("Checking role access - User roles:", userRoles, "Required roles:", requiredRoles)

      const hasRequiredRole = userRoles.some(role => requiredRoles.includes(role.name))

      if (!hasRequiredRole) {
        console.log("User roles not allowed for this route, redirecting to appropriate dashboard")
        // Redirect to appropriate dashboard based on role
        return next(store.getRedirectPath())
      }
    }

    // If user is authenticated and trying to access a general dashboard,
    // redirect to their role-specific dashboard
    if (to.name === "dashboard" || to.name === "staff-dashboard") {
      const redirectPath = store.getRedirectPath()
      if (redirectPath.name !== to.name) {
        console.log("Redirecting to role-specific dashboard:", redirectPath)
        return next(redirectPath)
      }
    }
  }

  // Guest routes (login, register, etc.)
  if (requiresGuest && store.isAuthenticate) {
    console.log("Authenticated user trying to access guest route, redirecting to dashboard")
    return next(store.getRedirectPath())
  }

  console.log("Navigation allowed to:", to.name)
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
