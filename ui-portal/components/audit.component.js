class AuditComponent extends HTMLElement {
    async connectedCallback() {
        this.allLogs = await apiService.getAuditLogs();
        this.filteredLogs = [...this.allLogs];
        this.render();
        this.setupListeners();
    }

    render() {
        const rows = this.filteredLogs.map(log => `
            <tr>
                <td style="font-family:'JetBrains Mono', monospace; color:var(--color-teal);">${log.id}</td>
                <td>
                    <span style="font-size:12px; font-weight:600; padding:4px 8px; border-radius:6px; background:rgba(255,255,255,0.05); border:1px solid var(--border-color);">
                        ${log.action}
                    </span>
                </td>
                <td style="font-weight: 500;">${log.service}</td>
                <td style="color:var(--text-secondary);">${log.details}</td>
                <td style="color:var(--text-secondary);">${log.timestamp}</td>
            </tr>
        `).join('');

        this.innerHTML = `
            <div class="view-container">
                <div class="card" style="display: flex; flex-direction: column; gap: 16px;">
                    <!-- Filter Section -->
                    <div style="display: flex; justify-content: space-between; align-items: center; gap: 20px;">
                        <div style="display: flex; align-items: center; gap: 12px; flex-grow: 1; max-width: 400px;">
                            <i class="fa-solid fa-magnifying-glass" style="color: var(--text-secondary);"></i>
                            <input type="text" id="auditSearchInput" placeholder="Filter by action, service, or details..." style="width: 100%;">
                        </div>
                        <div style="display: flex; gap: 8px;">
                            <select id="serviceSelectFilter">
                                <option value="ALL">All Services</option>
                                <option value="account-service">account-service</option>
                                <option value="aggregator-service">aggregator-service</option>
                                <option value="soa-service">soa-service</option>
                            </select>
                            <button class="btn" id="auditRefreshBtn"><i class="fa-solid fa-rotate"></i> Refresh</button>
                        </div>
                    </div>

                    <!-- Log Table Grid -->
                    <div class="table-container">
                        <table>
                            <thead>
                                <tr>
                                    <th>Audit ID</th>
                                    <th>Action</th>
                                    <th>Service</th>
                                    <th>Details</th>
                                    <th>Timestamp</th>
                                </tr>
                            </thead>
                            <tbody id="auditTableBody">
                                ${rows.length ? rows : '<tr><td colspan="5" style="text-align:center; color:var(--text-secondary);">No audit logs matching filters found.</td></tr>'}
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        `;
    }

    setupListeners() {
        const searchInput = this.querySelector('#auditSearchInput');
        const filterSelect = this.querySelector('#serviceSelectFilter');
        const refreshBtn = this.querySelector('#auditRefreshBtn');

        const applyFilters = () => {
            const query = searchInput.value.toLowerCase().trim();
            const service = filterSelect.value;

            this.filteredLogs = this.allLogs.filter(log => {
                const matchesQuery = !query || 
                                     log.id.toLowerCase().includes(query) ||
                                     log.action.toLowerCase().includes(query) ||
                                     log.service.toLowerCase().includes(query) ||
                                     log.details.toLowerCase().includes(query);
                const matchesService = service === 'ALL' || log.service === service;
                return matchesQuery && matchesService;
            });
            this.renderTableBody();
        };

        if (searchInput) searchInput.addEventListener('input', applyFilters);
        if (filterSelect) filterSelect.addEventListener('change', applyFilters);
        if (refreshBtn) {
            refreshBtn.addEventListener('click', async () => {
                this.allLogs = await apiService.getAuditLogs();
                applyFilters();
            });
        }
    }

    renderTableBody() {
        const body = this.querySelector('#auditTableBody');
        if (!body) return;

        const rows = this.filteredLogs.map(log => `
            <tr>
                <td style="font-family:'JetBrains Mono', monospace; color:var(--color-teal);">${log.id}</td>
                <td>
                    <span style="font-size:12px; font-weight:600; padding:4px 8px; border-radius:6px; background:rgba(255,255,255,0.05); border:1px solid var(--border-color);">
                        ${log.action}
                    </span>
                </td>
                <td style="font-weight: 500;">${log.service}</td>
                <td style="color:var(--text-secondary);">${log.details}</td>
                <td style="color:var(--text-secondary);">${log.timestamp}</td>
            </tr>
        `).join('');

        body.innerHTML = rows.length ? rows : '<tr><td colspan="5" style="text-align:center; color:var(--text-secondary);">No audit logs matching filters found.</td></tr>';
    }
}

customElements.define('app-audit', AuditComponent);
