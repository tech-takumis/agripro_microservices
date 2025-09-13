export const RBAC_CONFIG = {
  roleCategories: {
    ADMIN: 'Management',
    MANAGEMENT: 'Management',
    UNDERWRITER: 'Insurance Operations',
    'CLAIM PROCESSOR': 'Insurance Operations',
    TELLER: 'Financial Services',
    // Default for unrecognized roles
    default: 'Staff'
  },

  roleRedirects: {
    ADMIN: { name: 'admin-dashboard' },
    UNDERWRITER: { name: 'underwriter-dashboard' },
    'CLAIM PROCESSOR': { name: 'claims-processor-dashboard' },
    TELLER: { name: 'teller-dashboard' },
    MANAGEMENT: { name: 'management-dashboard' },
    // Default redirect
    default: { name: 'login' }
  },

  featurePermissions: {
    'staff-management': ['CAN CREATE USER', 'CAN UPDATE USER', 'CAN DELETE USER', 'CAN VIEW USER'],
    'system-administration': ['CAN MANAGE ROLES'],
    'financial-reports': ['CAN MANAGE FINANCE'],
    'policy-underwriting': ['CAN PROCESS CLAIM'],
    'risk-assessment': ['CAN PROCESS CLAIM'],
    'policy-issuance': ['CAN PROCESS CLAIM'],
    'claims-processing': ['CAN PROCESS CLAIM', 'CAN APPROVE CLAIM'],
    'claims-investigation': ['CAN PROCESS CLAIM'],
    'claims-approval': ['CAN APPROVE CLAIM'],
    'payment-processing': ['CAN MANAGE FINANCE'],
    'transaction-management': ['CAN MANAGE FINANCE'],
    'view-policies': ['CAN VIEW USER'],
    'view-claims': ['CAN PROCESS CLAIM'],
    'basic-reports': ['CAN VIEW USER', 'CAN MANAGE FINANCE']
  }
}