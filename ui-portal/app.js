class AppRoot extends HTMLElement {
    constructor() {
        super();
        this.currentRoute = 'dashboard';
        this.loggedIn = true;
        this.currentUser = { name: 'Admin User', role: 'ROLE_ADMIN' };
    }

    connectedCallback() {
        this.render();
        this.setupListeners();
        this.navigate(this.currentRoute);
    }

    render() {
        if (!this.loggedIn) {
            this.innerHTML = `
                <div style="height: 100vh; width: 100vw; display: flex; align-items: center; justify-content: center; background: var(--bg-dark); box-sizing: border-box; padding: 20px;">
                    <div class="card" style="width: 400px; padding: 32px; display: flex; flex-direction: column; gap: 20px; text-align: center; border: 1px solid var(--border-color);">
                        <div>
                            <i class="fa-solid fa-microchip" style="font-size: 40px; color: var(--color-indigo); margin-bottom: 12px;"></i>
                            <h2 style="font-weight: 700; font-size: 24px; color: #fff;">RapidX Control Center</h2>
                            <p style="color: var(--text-secondary); font-size: 14px; margin-top: 4px;">Sign in to manage microservices</p>
                        </div>
                        
                        <form id="portalLoginForm" style="display: flex; flex-direction: column; gap: 16px; text-align: left;">
                            <div class="form-group">
                                <label for="portalUserType">Select User Profile</label>
                                <select id="portalUserType">
                                    <option value="admin">Admin User (ROLE_ADMIN)</option>
                                    <option value="regular">Regular User (ROLE_USER)</option>
                                </select>
                            </div>
                            <button type="submit" class="btn" style="width: 100%; padding: 12px;">
                                <i class="fa-solid fa-right-to-bracket"></i> Authenticate & Enter
                            </button>
                        </form>
                    </div>
                </div>
            `;
            this.setupLoginListeners();
            return;
        }

        this.innerHTML = `
            <div class="app-container">
                <!-- Sidebar -->
                <aside class="sidebar">
                    <div class="brand-section">
                        <i class="fa-solid fa-microchip"></i>
                        <span class="brand-title">RapidX Portal</span>
                    </div>
                    
                    <ul class="nav-menu">
                        <li>
                            <a class="nav-item active" data-route="dashboard">
                                <i class="fa-solid fa-chart-line"></i>
                                <span>Dashboard</span>
                            </a>
                        </li>
                        <li>
                            <a class="nav-item" data-route="auth">
                                <i class="fa-solid fa-shield-halved"></i>
                                <span>Auth Gateway (auth)</span>
                            </a>
                        </li>
                        <li>
                            <a class="nav-item" data-route="account">
                                <i class="fa-solid fa-wallet"></i>
                                <span>Accounts Hub (account)</span>
                            </a>
                        </li>
                        <li>
                            <a class="nav-item" data-route="portal">
                                <i class="fa-solid fa-circle-nodes"></i>
                                <span>OIM Sync Portal</span>
                            </a>
                        </li>
                        <li>
                            <a class="nav-item" data-route="bpel">
                                <i class="fa-solid fa-diagram-project"></i>
                                <span>BPEL Orchestration</span>
                            </a>
                        </li>
                        <li>
                            <a class="nav-item" data-route="audit">
                                <i class="fa-solid fa-list-check"></i>
                                <span>Audit Logs</span>
                            </a>
                        </li>
                        <li>
                            <a class="nav-item" data-route="reporting">
                                <i class="fa-solid fa-file-invoice"></i>
                                <span>Reports Center (report)</span>
                            </a>
                        </li>
                        <li>
                            <a class="nav-item" data-route="legacy">
                                <i class="fa-solid fa-server"></i>
                                <span>Legacy JSP Portal</span>
                            </a>
                        </li>
                    </ul>
                    
                    <div class="sidebar-footer">
                        <p>RapidX v2.1.0-Angular</p>
                        <p>Java 8 & Reactive Services</p>
                    </div>
                </aside>

                <!-- Main Section -->
                <main class="main-content">
                    <header class="top-bar">
                        <h2 class="page-title" id="page-title-header">Control Center Dashboard</h2>
                        <div style="display: flex; align-items: center; gap: 16px;">
                            <div class="user-profile">
                                <div class="avatar">${this.currentUser.name[0]}</div>
                                <div style="display: flex; flex-direction: column;">
                                    <span style="font-size: 14px; font-weight: 500;">${this.currentUser.name}</span>
                                    <span style="font-size: 11px; color: var(--text-secondary); font-weight: 600;">${this.currentUser.role}</span>
                                </div>
                            </div>
                            <button id="signout-btn" class="btn btn-secondary" style="padding: 6px 12px; font-size: 13px; cursor: pointer; border-radius: 8px;">
                                <i class="fa-solid fa-right-from-bracket"></i> Sign Out
                            </button>
                        </div>
                    </header>
                    
                    <!-- Router Outlet -->
                    <div id="router-outlet"></div>
                </main>
            </div>
        `;
    }

    setupLoginListeners() {
        const form = this.querySelector('#portalLoginForm');
        if (form) {
            form.addEventListener('submit', (e) => {
                e.preventDefault();
                const userType = this.querySelector('#portalUserType').value;
                if (userType === 'admin') {
                    this.currentUser = { name: 'Admin User', role: 'ROLE_ADMIN' };
                } else {
                    this.currentUser = { name: 'Regular User', role: 'ROLE_USER' };
                }
                this.loggedIn = true;
                this.render();
                this.setupListeners();
                this.navigate(this.currentRoute);
            });
        }
    }

    setupListeners() {
        this.querySelectorAll('.nav-item').forEach(item => {
            item.addEventListener('click', (e) => {
                e.preventDefault();
                
                // Clear active class from all items
                this.querySelectorAll('.nav-item').forEach(i => i.classList.remove('active'));
                
                const navLink = e.currentTarget;
                navLink.classList.add('active');
                
                const route = navLink.getAttribute('data-route');
                this.navigate(route);
            });
        });

        const signoutBtn = this.querySelector('#signout-btn');
        if (signoutBtn) {
            signoutBtn.addEventListener('click', () => {
                this.loggedIn = false;
                this.render();
            });
        }
    }

    navigate(route) {
        this.currentRoute = route;
        const outlet = this.querySelector('#router-outlet');
        const header = this.querySelector('#page-title-header');
        if (!outlet) return;

        // Clear previous content
        outlet.innerHTML = '';

        // Route mapping
        switch(route) {
            case 'dashboard':
                header.textContent = 'Control Center Dashboard';
                outlet.appendChild(document.createElement('app-dashboard'));
                break;
            case 'auth':
                header.textContent = 'Auth Security Gateway (auth-service)';
                outlet.appendChild(document.createElement('app-auth'));
                break;
            case 'account':
                header.textContent = 'Account Provisioning Hub (account-service)';
                outlet.appendChild(document.createElement('app-account'));
                break;
            case 'portal':
                header.textContent = 'OIM Synchronization Portal';
                outlet.appendChild(document.createElement('app-portal'));
                break;
            case 'bpel':
                header.textContent = 'Oracle SOA BPEL Workflow Process';
                outlet.appendChild(document.createElement('app-bpel'));
                break;
            case 'audit':
                header.textContent = 'Reactive Audit Tracking Logs';
                outlet.appendChild(document.createElement('app-audit'));
                break;
            case 'reporting':
                header.textContent = 'Analytics Reporting Hub (report-service)';
                outlet.appendChild(document.createElement('app-reporting'));
                break;
            case 'legacy':
                header.textContent = 'Legacy Tomcat Servlet Container';
                outlet.appendChild(document.createElement('app-legacy'));
                break;
            default:
                header.textContent = 'Control Center Dashboard';
                outlet.appendChild(document.createElement('app-dashboard'));
        }
    }
}

customElements.define('app-root', AppRoot);
