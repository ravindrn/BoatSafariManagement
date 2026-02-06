// auth.js - Complete Authentication System
console.log('🔐 Loading Boat Safari Authentication System...');

const AUTH = {
    // Configuration
    debug: true,

    // Logging function
    log: function(message, data = null) {
        if (this.debug) {
            console.log(`🔐 AUTH: ${message}`, data || '');
        }
    },

    // ==================== CORE FUNCTIONS ====================

    // Check if user is logged in
    isLoggedIn: function() {
        const token = localStorage.getItem('currentUserToken');
        const email = localStorage.getItem('currentUserEmail');
        const role = localStorage.getItem('currentUserRole');
        const isLoggedIn = token !== null && email !== null && role !== null;

        this.log('Login Status Check', {
            isLoggedIn,
            token: token ? '✓ Set' : '✗ Missing',
            email: email ? '✓ Set' : '✗ Missing',
            role: role || '✗ Missing'
        });

        return isLoggedIn;
    },

    // Get user role
    getUserRole: function() {
        const role = localStorage.getItem('currentUserRole');
        this.log('Current User Role', { role });
        return role;
    },

    // Get user display name
    getUserName: function() {
        const name = localStorage.getItem('currentUserFirstName') ||
            localStorage.getItem('currentUserName') ||
            'User';
        this.log('User Name', { name });
        return name;
    },

    // Get user email
    getUserEmail: function() {
        return localStorage.getItem('currentUserEmail');
    },

    // Logout function
    logout: function() {
        this.log('Logging out user');
        localStorage.clear();
        window.location.href = 'index.html';
    },

    // Check if user has specific role
    hasRole: function(role) {
        const userRole = this.getUserRole();
        const hasRole = userRole === role;
        this.log('Role Check', { required: role, actual: userRole, hasAccess: hasRole });
        return hasRole;
    },

    // ==================== NAVBAR FUNCTIONS ====================

    // Main function to update navbar
    updateNavbar: function() {
        this.log('🔄 Starting Navbar Update');

        try {
            const navbarAuthSection = document.getElementById('navbarAuthSection');
            const navbarLinks = this.findNavbarLinks();

            // Validate elements exist
            if (!this.validateNavbarElements(navbarAuthSection, navbarLinks)) {
                return;
            }

            if (this.isLoggedIn()) {
                this.updateNavbarForLoggedInUser(navbarAuthSection, navbarLinks);
            } else {
                this.updateNavbarForGuest(navbarAuthSection);
            }

            this.log('✅ Navbar Update Complete');
        } catch (error) {
            console.error('❌ Navbar update failed:', error);
        }
    },

    // Find navbar links container (more flexible approach)
    findNavbarLinks: function() {
        // Try different possible selectors
        const selectors = [
            '.navbar-nav',
            '.navbar-collapse .nav',
            '.navbar .nav',
            'nav ul'
        ];

        for (const selector of selectors) {
            const element = document.querySelector(selector);
            if (element) {
                this.log('Found navbar links using selector:', selector);
                return element;
            }
        }

        console.error('❌ Could not find navbar links container');
        return null;
    },

    // Validate that required navbar elements exist
    validateNavbarElements: function(navbarAuthSection, navbarLinks) {
        if (!navbarAuthSection) {
            console.error('❌ CRITICAL ERROR: navbarAuthSection element not found!');
            console.error('Make sure your navbar has: <div id="navbarAuthSection">');
            return false;
        }

        if (!navbarLinks) {
            console.error('❌ CRITICAL ERROR: navbar links container not found!');
            console.error('Make sure your navbar has a <ul> with class "navbar-nav"');
            return false;
        }

        this.log('Navbar Elements Validated', {
            authSection: '✓ Found',
            navLinks: '✓ Found'
        });

        return true;
    },

    // Update navbar for logged-in users
    updateNavbarForLoggedInUser: function(navbarAuthSection, navbarLinks) {
        const userRole = this.getUserRole();
        const userName = this.getUserName();
        const roleIcon = this.getRoleIcon(userRole);

        this.log('Updating Navbar for Logged-in User', { userRole, userName });

        // Update user info and logout button
        navbarAuthSection.innerHTML = `
            <span class="user-welcome me-3">
                <i class="${roleIcon} me-2"></i>
                <span>${userName}</span>
                <small class="ms-2 badge ${this.getRoleBadgeClass(userRole)}">${this.getRoleDisplayName(userRole)}</small>
            </span>
            <a href="#" class="btn btn-outline-primary" id="logoutBtn">
                <i class="fas fa-sign-out-alt me-2"></i>Logout
            </a>
        `;

        // Add role-specific navigation links
        this.addRoleSpecificLinks(userRole, navbarLinks);

        // Setup logout functionality
        this.setupLogoutHandler();
    },

    // Update navbar for guests (not logged in)
    updateNavbarForGuest: function(navbarAuthSection) {
        this.log('Updating Navbar for Guest User');
        navbarAuthSection.innerHTML = `
            <a href="user-management.html" class="btn btn-primary">
                <i class="fas fa-users me-2"></i>Enter to your role
            </a>
        `;
    },

    // Setup logout button event handler
    setupLogoutHandler: function() {
        const logoutBtn = document.getElementById('logoutBtn');
        if (logoutBtn) {
            logoutBtn.addEventListener('click', (e) => {
                e.preventDefault();
                this.logout();
            });
            this.log('Logout Handler Setup', { status: '✓ Ready' });
        }
    },

    // ==================== ROLE-SPECIFIC LINKS ====================

    // Get appropriate icon for user role
    getRoleIcon: function(role) {
        const icons = {
            'SYSTEM_ADMINISTRATOR': 'fas fa-user-shield',
            'USER_MANAGER': 'fas fa-users-cog',
            'BOAT_OWNER': 'fas fa-anchor',
            'OPERATIONS_MANAGER': 'fas fa-chart-line',
            'STAFF': 'fas fa-user-tie',
            'CUSTOMER': 'fas fa-user-circle'
        };
        return icons[role] || 'fas fa-user-circle';
    },

    // Get role badge class for styling
    getRoleBadgeClass: function(role) {
        const badgeClasses = {
            'SYSTEM_ADMINISTRATOR': 'bg-danger',
            'USER_MANAGER': 'bg-primary',
            'BOAT_OWNER': 'bg-success',
            'OPERATIONS_MANAGER': 'bg-warning',
            'STAFF': 'bg-info',
            'CUSTOMER': 'bg-secondary'
        };
        return badgeClasses[role] || 'bg-secondary';
    },

    // Get display name for role
    getRoleDisplayName: function(role) {
        const displayNames = {
            'SYSTEM_ADMINISTRATOR': 'Admin',
            'USER_MANAGER': 'User Manager',
            'BOAT_OWNER': 'Boat Owner',
            'OPERATIONS_MANAGER': 'Operations',
            'STAFF': 'Staff',
            'CUSTOMER': 'Customer'
        };
        return displayNames[role] || role;
    },

    // Add navigation links based on user role
    addRoleSpecificLinks: function(role, navbarLinks) {
        this.log('Adding Role-Specific Links', { role });

        // Clean up any existing role links
        this.removeExistingRoleLinks(navbarLinks);

        // Generate new links based on role
        const roleLinks = this.generateRoleLinks(role);

        if (roleLinks) {
            this.insertRoleLinks(roleLinks, navbarLinks);
        } else {
            this.log('No Role Links Generated', { role, reason: 'Unknown role or no links defined' });
        }
    },

    // Remove any existing role-specific links
    removeExistingRoleLinks: function(navbarLinks) {
        const existingLinks = navbarLinks.querySelectorAll('.role-specific-link');
        this.log('Cleaning Existing Role Links', { count: existingLinks.length });
        existingLinks.forEach(link => link.remove());
    },

    // Generate HTML for role-specific links
    generateRoleLinks: function(role) {
        const linkTemplates = {
            'CUSTOMER': `
                <li class="nav-item role-specific-link">
                    <a class="nav-link" href="booking.html">
                        <i class="fas fa-ticket-alt me-1"></i>Book Trip
                    </a>
                </li>
                <li class="nav-item role-specific-link">
                    <a class="nav-link" href="my-bookings.html">
                        <i class="fas fa-calendar-check me-1"></i>My Bookings
                    </a>
                </li>
            `,

            'SYSTEM_ADMINISTRATOR': `
                <li class="nav-item role-specific-link">
                    <a class="nav-link" href="admin.html">
                        <i class="fas fa-tachometer-alt me-1"></i>Admin Dashboard
                    </a>
                </li>
                <li class="nav-item role-specific-link">
                    <a class="nav-link" href="adminUserManagement.html">
                        <i class="fas fa-users-cog me-1"></i>User Management
                    </a>
                </li>
                <li class="nav-item role-specific-link">
                    <a class="nav-link" href="schedule.html">
                        <i class="fas fa-calendar-alt me-1"></i>Staff Schedule
                    </a>
                </li>
                <li class="nav-item role-specific-link">
                    <a class="nav-link" href="reports.html">
                        <i class="fas fa-chart-bar me-1"></i>Reports
                    </a>
                </li>
            `,

            'USER_MANAGER': `
                <li class="nav-item role-specific-link">
                    <a class="nav-link" href="adminUserManagement.html">
                        <i class="fas fa-users-cog me-1"></i>User Management
                    </a>
                </li>
            `,

            'BOAT_OWNER': `
                <li class="nav-item role-specific-link">
                    <a class="nav-link" href="boats.html">
                        <i class="fas fa-ship me-1"></i>My Boats
                    </a>
                </li>
            `,

            'OPERATIONS_MANAGER': `
                <li class="nav-item role-specific-link">
                    <a class="nav-link" href="reports.html">
                        <i class="fas fa-chart-line me-1"></i>Reports & Analytics
                    </a>
                </li>
            `,

            'STAFF': `
                <li class="nav-item role-specific-link">
                    <a class="nav-link" href="schedule.html">
                        <i class="fas fa-calendar-alt me-1"></i>Schedule Management
                    </a>
                </li>
            `
        };

        return linkTemplates[role];
    },

    // Insert generated links into navbar
    insertRoleLinks: function(roleLinks, navbarLinks) {
        // Find the home link or insert at the beginning
        const homeLink = navbarLinks.querySelector('li:first-child');
        if (homeLink) {
            homeLink.insertAdjacentHTML('afterend', roleLinks);
            this.log('Role Links Inserted Successfully', {
                linksAdded: (roleLinks.match(/<li/g) || []).length
            });
        } else {
            // If no home link found, just append to the navbar
            navbarLinks.insertAdjacentHTML('beforeend', roleLinks);
            this.log('Role Links Appended (no home link found)');
        }
    },

    // Check if current page matches the given page
    isActivePage: function(pageName) {
        const currentPage = window.location.pathname.split('/').pop();
        return currentPage === pageName;
    },

    // ==================== PAGE PROTECTION ====================

    // Protect page - redirect if not logged in or doesn't have required role
    protectPage: function(requiredRole = null) {
        if (!this.isLoggedIn()) {
            this.log('Page Protection: User not logged in - redirecting to login');
            window.location.href = 'user-management.html';
            return false;
        }

        if (requiredRole && !this.hasRole(requiredRole)) {
            this.log('Page Protection: Access denied - insufficient permissions', {
                required: requiredRole,
                actual: this.getUserRole()
            });
            alert('Access denied. You do not have permission to access this page.');
            this.redirectToDashboard();
            return false;
        }

        this.log('Page Protection: Access granted', {
            user: this.getUserName(),
            role: this.getUserRole()
        });
        return true;
    },

    // Redirect to appropriate dashboard based on role
    redirectToDashboard: function() {
        const role = this.getUserRole();
        const dashboards = {
            'SYSTEM_ADMINISTRATOR': 'admin.html',
            'USER_MANAGER': 'adminUserManagement.html',
            'CUSTOMER': 'booking.html',
            'BOAT_OWNER': 'boats.html',
            'OPERATIONS_MANAGER': 'reports.html',
            'STAFF': 'schedule.html'
        };

        const dashboard = dashboards[role] || 'index.html';
        this.log('Redirecting to Dashboard', { role, dashboard });
        window.location.href = dashboard;
    },

    // Initialize protected page (call this in protected pages)
    initializeProtectedPage: function(requiredRole = null) {
        this.log('Initializing Protected Page', { requiredRole });

        if (!this.protectPage(requiredRole)) {
            return false;
        }

        // Update navbar
        this.updateNavbar();

        // Update user display name if element exists
        const userNameElement = document.getElementById('userName');
        if (userNameElement) {
            userNameElement.textContent = this.getUserName();
        }

        this.log('Protected Page Initialized Successfully');
        return true;
    },

    // ==================== USER MANAGEMENT ====================

    // Get complete user info
    getUserInfo: function() {
        const info = {
            name: this.getUserName(),
            email: this.getUserEmail(),
            role: this.getUserRole(),
            token: localStorage.getItem('currentUserToken') ? '✓ Set' : '✗ Missing'
        };

        this.log('User Info Retrieved', info);
        return info;
    },

    // Check if user can access a specific feature
    canAccess: function(feature, requiredRole) {
        const canAccess = this.hasRole(requiredRole);
        this.log('Feature Access Check', { feature, requiredRole, canAccess });
        return canAccess;
    },

    // ==================== DEBUGGING TOOLS ====================

    // Debug current authentication state
    debugAuthState: function() {
        console.group('🔐 AUTH DEBUG INFO');
        console.log('📍 Current Page:', window.location.pathname);
        console.log('👤 User Info:', this.getUserInfo());
        console.log('🔑 LocalStorage:', {
            token: localStorage.getItem('currentUserToken'),
            email: localStorage.getItem('currentUserEmail'),
            role: localStorage.getItem('currentUserRole'),
            name: localStorage.getItem('currentUserName'),
            firstName: localStorage.getItem('currentUserFirstName')
        });
        console.log('🏗️ Navbar Elements:', {
            authSection: document.getElementById('navbarAuthSection') ? '✓ Found' : '✗ Missing',
            navbarLinks: document.querySelector('.navbar-nav') ? '✓ Found' : '✗ Missing'
        });
        console.groupEnd();
    }
};

// ==================== INITIALIZATION ====================

// Initialize when DOM is fully loaded
document.addEventListener('DOMContentLoaded', function() {
    console.log('🚀 AUTH: Initializing Authentication System...');

    // Debug current state
    AUTH.debugAuthState();

    // Update navbar
    AUTH.updateNavbar();

    console.log('✅ AUTH: System Ready');
});

// Make AUTH globally available for debugging
window.AUTH = AUTH;

console.log('✅ Boat Safari Authentication System Loaded Successfully');
console.log('💡 Tip: Open browser console and type "AUTH.debugAuthState()" to debug');