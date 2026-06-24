class DashboardComponent extends HTMLElement {
    async connectedCallback() {
        this.statuses = await apiService.getServiceStatuses();
        this.accounts = await apiService.getAccounts();
        this.reports = await apiService.getReports();
        this.bpelInstances = await apiService.getBpelInstances();
        this.auditLogs = await apiService.getAuditLogs();
        this.render();
        this.initializeChart();
    }

    render() {
        // Statuses
        const statusItems = Object.entries(this.statuses).map(([name, status]) => `
            <div class="health-item">
                <span style="font-weight: 500; font-size: 14px;">${name}</span>
                <span class="status-badge">
                    <span class="status-dot ${status === 'UP' ? 'active' : 'inactive'}"></span>
                    ${status}
                </span>
            </div>
        `).join('');

        this.innerHTML = `
            <div class="view-container">
                <!-- Quick Stats Grid -->
                <div class="stats-grid">
                    <div class="card stats-card">
                        <div>
                            <div class="stats-label">Total Counterparties</div>
                            <div class="stats-value">${this.accounts.length}</div>
                            <div style="font-size:12px; color:var(--color-green);"><i class="fa-solid fa-arrow-up"></i> 100% active</div>
                        </div>
                        <div class="icon-box indigo"><i class="fa-solid fa-circle-nodes"></i></div>
                    </div>
                    
                    <div class="card stats-card">
                        <div>
                            <div class="stats-label">BPEL Orchestrations</div>
                            <div class="stats-value">${this.bpelInstances.length}</div>
                            <div style="font-size:12px; color:var(--color-indigo);"><i class="fa-solid fa-sync fa-spin"></i> Active tracking</div>
                        </div>
                        <div class="icon-box violet"><i class="fa-solid fa-diagram-project"></i></div>
                    </div>

                    <div class="card stats-card">
                        <div>
                            <div class="stats-label">Generated Reports</div>
                            <div class="stats-value">${this.reports.length}</div>
                            <div style="font-size:12px; color:var(--color-teal);"><i class="fa-solid fa-database"></i> Oracle DB</div>
                        </div>
                        <div class="icon-box teal"><i class="fa-solid fa-file-invoice"></i></div>
                    </div>

                    <div class="card stats-card">
                        <div>
                            <div class="stats-label">Audit Logs</div>
                            <div class="stats-value">${this.auditLogs.length}</div>
                            <div style="font-size:12px; color:var(--color-green);"><i class="fa-solid fa-check-double"></i> Reactive stream</div>
                        </div>
                        <div class="icon-box green"><i class="fa-solid fa-list-check"></i></div>
                    </div>
                </div>

                <!-- Live Health status -->
                <div>
                    <h3 style="margin-bottom: 16px; font-weight:600; font-size:18px;">Live Microservices Health Monitor</h3>
                    <div class="health-grid">
                        ${statusItems}
                    </div>
                </div>

                <!-- Flow Visualizer and Chart Grid -->
                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 24px;">
                    <!-- Architecture flow -->
                    <div class="flow-diagram-container">
                        <div class="diagram-header">
                            <span style="font-weight: 600; font-size:16px;">Microservice Architecture Message Bus Flow</span>
                            <span style="font-size: 12px; color: var(--color-teal);"><i class="fa-solid fa-circle-nodes fa-pulse"></i> Live routing</span>
                        </div>
                        <div class="diagram-nodes">
                            <div class="node active">
                                <i class="fa-solid fa-laptop-code"></i>
                                <span>UI Portal</span>
                            </div>
                            <div class="node active">
                                <i class="fa-solid fa-network-wired"></i>
                                <span>OSB Bus</span>
                            </div>
                            <div class="node active">
                                <i class="fa-solid fa-diagram-project"></i>
                                <span>BPEL Eng</span>
                            </div>
                            <div class="node active">
                                <i class="fa-solid fa-database"></i>
                                <span>Oracle DB</span>
                            </div>
                        </div>
                        <p style="font-size:12px; color:var(--text-secondary); line-height: 1.5;">
                            Outbound client queries hitting the <strong>Oracle Service Bus (OSB) Proxy</strong> trigger pipeline validation & message transformation, which routes to <strong>Oracle SOA BPEL engine</strong> to coordinate persistence inside <strong>Oracle Database</strong>.
                        </p>
                    </div>

                    <!-- Chart panel -->
                    <div class="card" style="display:flex; flex-direction:column; gap:16px;">
                        <h4 style="font-size:16px; font-weight:600;">Audit Logs by Microservice</h4>
                        <div style="flex-grow:1; position:relative; min-height:160px;">
                            <canvas id="auditChart"></canvas>
                        </div>
                    </div>
                </div>
            </div>
        `;
    }

    initializeChart() {
        const ctx = this.querySelector('#auditChart');
        if (!ctx) return;
        
        // Count services
        const counts = {};
        this.auditLogs.forEach(log => {
            counts[log.service] = (counts[log.service] || 0) + 1;
        });

        new Chart(ctx, {
            type: 'bar',
            data: {
                labels: Object.keys(counts).length ? Object.keys(counts) : ['account-service', 'aggregator-service', 'soa-service'],
                datasets: [{
                    label: 'Audit Events',
                    data: Object.values(counts).length ? Object.values(counts) : [5, 12, 3],
                    backgroundColor: [
                        'rgba(99, 102, 241, 0.6)',
                        'rgba(20, 184, 166, 0.6)',
                        'rgba(168, 85, 247, 0.6)'
                    ],
                    borderColor: [
                        '#6366f1',
                        '#14b8a6',
                        '#a855f7'
                    ],
                    borderWidth: 1.5,
                    borderRadius: 6
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { display: false }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: { color: '#9ca3af' },
                        grid: { color: 'rgba(255,255,255,0.05)' }
                    },
                    x: {
                        ticks: { color: '#9ca3af' },
                        grid: { display: false }
                    }
                }
            }
        });
    }
}

customElements.define('app-dashboard', DashboardComponent);
