class PortalComponent extends HTMLElement {
    async connectedCallback() {
        this.accounts = await apiService.getAccounts();
        this.render();
        this.setupListeners();
    }

    render() {
        const rows = this.accounts.map(acc => `
            <tr>
                <td style="font-weight: 500;">${acc.id}</td>
                <td>${acc.name}</td>
                <td>${acc.counterpartyName}</td>
                <td>
                    <span style="font-size:12px; font-weight:600; padding:4px 8px; border-radius:12px; 
                                 background:${acc.status === 'SYNCHRONIZED' ? 'rgba(16,185,129,0.15)' : 'rgba(239,68,68,0.15)'}; 
                                 color:${acc.status === 'SYNCHRONIZED' ? 'var(--color-green)' : 'var(--color-red)'};">
                        ${acc.status}
                    </span>
                </td>
                <td style="color:var(--text-secondary);">${acc.updatedAt}</td>
            </tr>
        `).join('');

        this.innerHTML = `
            <div class="view-container">
                <div style="display: grid; grid-template-columns: 1fr 2fr; gap: 24px;">
                    <!-- Sync Trigger form -->
                    <div class="card" style="display: flex; flex-direction: column; gap: 16px; height: fit-content;">
                        <h3 style="font-weight: 600; font-size: 16px;">Trigger OIM Synchronization</h3>
                        
                        <form id="oimSyncForm">
                            <div class="form-group">
                                <label for="accName">Account Name</label>
                                <input type="text" id="accName" placeholder="e.g. JPMorgan Cash Account" required>
                            </div>
                            <div class="form-group">
                                <label for="cpName">Counterparty Name</label>
                                <input type="text" id="cpName" placeholder="e.g. JPMorgan Chase" required>
                            </div>
                            <button type="submit" class="btn" style="width: 100%; margin-top: 8px;">
                                <i class="fa-solid fa-rotate"></i> Launch OIM Sync
                            </button>
                        </form>

                        <div style="margin-top: 8px;">
                            <h4 style="font-size:13px; font-weight:600; margin-bottom:8px;">OIM Sync Response:</h4>
                            <div class="console-output" id="oimConsole">Ready to launch...</div>
                        </div>
                    </div>

                    <!-- Counterparty Table -->
                    <div class="card" style="display: flex; flex-direction: column; gap: 16px;">
                        <div style="display:flex; justify-content:space-between; align-items:center;">
                            <h3 style="font-weight: 600; font-size: 16px;">Aggregated Counterparty Registry</h3>
                            <button class="btn btn-secondary" id="refreshBtn"><i class="fa-solid fa-arrows-rotate"></i> Refresh</button>
                        </div>
                        
                        <div class="table-container">
                            <table>
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Account Name</th>
                                        <th>Counterparty</th>
                                        <th>OIM Sync State</th>
                                        <th>Last Update</th>
                                    </tr>
                                </thead>
                                <tbody id="accountsTableBody">
                                    ${rows}
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        `;
    }

    setupListeners() {
        const form = this.querySelector('#oimSyncForm');
        const consoleEl = this.querySelector('#oimConsole');
        const refreshBtn = this.querySelector('#refreshBtn');

        if (form) {
            form.addEventListener('submit', async (e) => {
                e.preventDefault();
                const accName = this.querySelector('#accName').value;
                const cpName = this.querySelector('#cpName').value;
                
                consoleEl.textContent = `[Request] Triggering OIM Sync for "${accName}"...\n`;
                
                try {
                    const result = await apiService.triggerOimSync({ name: accName, counterpartyName: cpName });
                    consoleEl.textContent += `[Response] Code: 200 OK\n${JSON.stringify(result, null, 2)}`;
                    
                    // Reload accounts
                    this.accounts = await apiService.getAccounts();
                    this.renderBody();
                } catch (err) {
                    consoleEl.textContent += `[Error] ${err.message}`;
                }
            });
        }

        if (refreshBtn) {
            refreshBtn.addEventListener('click', async () => {
                this.accounts = await apiService.getAccounts();
                this.renderBody();
            });
        }
    }

    renderBody() {
        const body = this.querySelector('#accountsTableBody');
        if (!body) return;
        
        body.innerHTML = this.accounts.map(acc => `
            <tr>
                <td style="font-weight: 500;">${acc.id}</td>
                <td>${acc.name}</td>
                <td>${acc.counterpartyName}</td>
                <td>
                    <span style="font-size:12px; font-weight:600; padding:4px 8px; border-radius:12px; 
                                 background:${acc.status === 'SYNCHRONIZED' ? 'rgba(16,185,129,0.15)' : 'rgba(239,68,68,0.15)'}; 
                                 color:${acc.status === 'SYNCHRONIZED' ? 'var(--color-green)' : 'var(--color-red)'};">
                        ${acc.status}
                    </span>
                </td>
                <td style="color:var(--text-secondary);">${acc.updatedAt}</td>
            </tr>
        `).join('');
    }
}

customElements.define('app-portal', PortalComponent);
