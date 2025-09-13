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
    name: "Dashboard",
    href: "/underwriter/dashboard",
    icon: LayoutDashboard,
  },
  {
    name: "Applications",
    icon: FileText,
    children: [
      { name: "Pending Review", href: "/underwriter/applications/pending" },
      { name: "Approved Applications", href: "/underwriter/applications/approved" },
      { name: "Rejected Applications", href: "/underwriter/applications/rejected" },
      { name: "All Applications", href: "/underwriter/applications/all" },
    ],
  },
  {
    name: "Risk Assessment",
    icon: Shield,
    children: [
      { name: "Risk Factors", href: "/underwriter/risk/factors" },
      { name: "Historical Data", href: "/underwriter/risk/history" },
      { name: "Geographic Analysis", href: "/underwriter/risk/geo" },
    ],
  },
  {
    name: "Guidelines & Tools",
    icon: Calculator,
    children: [
      { name: "Eligibility Criteria", href: "/underwriter/guidelines/eligibility" },
      { name: "Coverage & Premium", href: "/underwriter/guidelines/coverage" },
      { name: "Underwriting Manual", href: "/underwriter/guidelines/manual" },
    ],
  },
  {
    name: "Reports",
    href: "/underwriter/reports",
    icon: BarChart3,
  },
]

// Navigation for admin role
export const ADMIN_NAVIGATION = [
  {
    name: "Dashboard",
    href: "/admin/dashboard",
    icon: LayoutDashboard,
  },
  {
    name: "Staff Management",
    icon: Users,
    children: [
      { name: "Register Staff", href: "/admin/staff/register" },
      { name: "Manage Staff Accounts", href: "/admin/staff/manage" },
      { name: "Roles & Permissions", href: "/admin/roles" },
    ],
  },
  {
    name: "Insurance Management",
    icon: ClipboardList,
    children: [
      { name: "Create Application Type", href: "/admin/applications/new" },
      { name: "View All Applications", href: "/admin/applications/all" }, // Added this line
      { name: "Manage Application Types", href: "/admin/applications/manage" },
    ],
  },
  {
    name: "System Settings",
    icon: Settings,
    children: [
      { name: "General Settings", href: "/admin/settings/general" },
      { name: "Audit Logs", href: "/admin/settings/audit-logs" },
      { name: "Database Management", href: "/admin/settings/database" },
    ],
  },
  {
    name: "Reports & Analytics",
    icon: BarChart3,
    children: [
      { name: "Overall Performance", href: "/admin/reports/overall" },
      { name: "User Activity", href: "/admin/reports/user-activity" },
      { name: "Financial Overview", href: "/admin/reports/financial" },
    ],
  },
]

// Data types for application fields
export const DATA_TYPES = ["TEXT", "NUMBER", "DATE", "BOOLEAN", "FILE", "ENUM", "GEOLOCATION"]

