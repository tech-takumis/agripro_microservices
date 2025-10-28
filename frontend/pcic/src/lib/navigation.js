import {
  LayoutDashboard,
  FileText,
  Shield,
  Calculator,
  BarChart3,
  Users,
  Settings,
  ClipboardList,
} from "lucide-vue-next"

// Navigation for underwriter role
export const UNDERWRITER_NAVIGATION = [
  {
    title: "Dashboard",
    icon: LayoutDashboard,
    to: { name: "underwriter-dashboard" },
    exact: true,
  },
  {
    title: "Applications",
    icon: FileText,
    children: [
      { title: "All Applications", to: { name: "underwriter-applications-all" } },
    ],
  },
  {
    title: "Risk Assessment",
    icon: Shield,
    children: [
      { title: "Risk Factors", to: { name: "underwriter-risk-factors" } },
      { title: "Historical Data", to: { name: "underwriter-risk-history" } },
      { title: "Geographic Analysis", to: { name: "underwriter-risk-geo" } },
    ],
  },
  {
    title: "Guidelines & Tools",
    icon: Calculator,
    children: [
      { title: "Eligibility Criteria", to: { name: "underwriter-guidelines-eligibility" } },
      { title: "Coverage & Premium", to: { name: "underwriter-guidelines-coverage" } },
      { title: "Underwriting Manual", to: { name: "underwriter-guidelines-manual" } },
    ],
  },
  {
    title: "Reports",
    icon: BarChart3,
    to: { name: "underwriter-reports" },
  },
]

// Navigation for admin role
export const ADMIN_NAVIGATION = [
  {
    title: "Dashboard",
    icon: LayoutDashboard,
    to: { name: "admin-dashboard" },
    exact: true,
  },
  {
    title: "Staff Management",
    icon: Users,
    children: [
      { title: "Register Staff", to: { name: "admin-staff-register" } },
      { title: "Manage Staff Accounts", to: { name: "admin-staff-manage" } },
      { title: "Roles & Permissions", to: { name: "admin-roles" } },
    ],
  },
  {
    title: "Insurance Management",
    icon: ClipboardList,
    children: [
      { title: "Create Application Type", to: { name: "admin-applications-new" } },
      { title: "View All Applications", to: { name: "admin-applications-all" } },
      { title: "Manage Application Types", to: { name: "admin-applications-manage" } },
    ],
  },
  {
    title: "System Settings",
    icon: Settings,
    children: [
      { title: "General Settings", to: { name: "admin-settings-general" } },
      { title: "Audit Logs", to: { name: "admin-settings-audit-logs" } },
      { title: "Database Management", to: { name: "admin-settings-database" } },
    ],
  },
  {
    title: "Reports & Analytics",
    icon: BarChart3,
    children: [
      { title: "Overall Performance", to: { name: "admin-reports-overall" } },
      { title: "User Activity", to: { name: "admin-reports-user-activity" } },
      { title: "Financial Overview", to: { name: "admin-reports-financial" } },
    ],
  },
]

// Data types for application fields
export const DATA_TYPES = ["TEXT", "NUMBER", "DATE", "BOOLEAN", "FILE", "ENUM", "GEOLOCATION"]
