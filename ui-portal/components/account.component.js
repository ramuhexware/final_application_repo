class AccountComponent extends HTMLElement {
    async connectedCallback() {
        this.accounts = await apiService.getAccounts();
        this.render();
        this.setupListeners();
    }

    render() {
        const rows = this.accounts.map(acc => `
            <tr>
                <td style="font-weight: 500;">ACC-${1000 + acc.id}</td>
                <td>${acc.name}</td>
                <td>${acc.counterpartyName}</td>
                <td>
                    <span style="font-size:12px; font-weight:600; padding:4px 8px; border-radius:12px; 
                                 background:rgba(16,185,129,0.15); color:var(--color-green);">
                        ACTIVE
                    </span>
                </td>
            </tr>
        `).join('');

        this.innerHTML = `
            <div class="view-container">
                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 24px;">
                    <!-- Register Account form -->
                    <div style="display: flex; flex-direction: column; gap: 20px;">
                        <div class="card" style="display: flex; flex-direction: column; gap: 16px;">
                            <h3 style="font-weight: 600; font-size: 16px;">Register New Bank Account</h3>
                            
                            <form id="newAccForm">
                                <div class="form-group">
                                    <label for="regAccName">Account Name</label>
                                    <input type="text" id="regAccName" placeholder="e.g. Clearing Cash Reserves" required>
                                </div>
                                <div class="form-group">
                                    <label for="regCpName">Counterparty Name</label>
                                    <input type="text" id="regCpName" placeholder="e.g. Bank of New York Mellon" required>
                                </div>
                                <button type="submit" class="btn" style="width:100%; margin-top:8px;">
                                    <i class="fa-solid fa-plus"></i> Save Account
                                </button>
                            </form>
                        </div>

                        <!-- ActiveMQ Publisher Console -->
                        <div class="card" style="display: flex; flex-direction: column; gap: 16px; border: 1px solid var(--color-indigo);">
                            <h3 style="font-weight: 600; font-size: 16px; color: var(--color-indigo);">JmsTemplate ActiveMQ Event Publisher</h3>
                            <div style="padding:12px; background:rgba(99,102,241,0.08); border-radius:8px; font-size:13px; color:var(--text-secondary); line-height:1.4;">
                                <span style="font-weight:600; color:var(--color-indigo);">Queue Destination:</span> <code style="font-family:monospace; color:#fff;">SF-BA0352-EBMQueue.local</code>
                            </div>
                            
                            <form id="amqPublishForm">
                                <div class="form-group">
                                    <label for="amqEventType">Event Type</label>
                                    <select id="amqEventType">
                                        <option value="ACCOUNT_CREATED">ACCOUNT_CREATED</option>
                                        <option value="ACCOUNT_UPDATED">ACCOUNT_UPDATED</option>
                                        <option value="OIM_SYNC_TRIGGERED">OIM_SYNC_TRIGGERED</option>
                                    </select>
                                </div>
                                <button type="submit" class="btn btn-secondary" style="width:100%;">
                                    <i class="fa-solid fa-paper-plane"></i> jmsTemplate.convertAndSend()
                                </button>
                            </form>
                            <div class="console-output" id="amqConsole">Ready for JMS convertAndSend...</div>
                        </div>
                    </div>

                    <!-- Accounts list -->
                    <div class="card" style="display: flex; flex-direction: column; gap: 16px;">
                        <h3 style="font-weight: 600; font-size: 16px;">Account Service Registry</h3>
                        
                        <div class="table-container">
                            <table>
                                <thead>
                                    <tr>
                                        <th>Account ID</th>
                                        <th>Name</th>
                                        <th>Counterparty</th>
                                        <th>Status</th>
                                    </tr>
                                </thead>
                                <tbody id="serviceAccBody">
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
        const accForm = this.querySelector('#newAccForm');
        const amqForm = this.querySelector('#amqPublishForm');
        const amqConsole = this.querySelector('#amqConsole');

        if (accForm) {
            accForm.addEventListener('submit', async (e) => {
                e.preventDefault();
                const name = this.querySelector('#regAccName').value;
                const cpName = this.querySelector('#regCpName').value;

                // Add to list
                const newAcc = {
                    id: this.accounts.length + 1,
                    name,
                    counterpartyName: cpName,
                    status: 'SYNCHRONIZED',
                    updatedAt: new Date().toISOString()
                };
                this.accounts.push(newAcc);
                accForm.reset();
                this.renderBody();
            });
        }

        if (amqForm) {
            amqForm.addEventListener('submit', (e) => {
                e.preventDefault();
                const type = this.querySelector('#amqEventType').value;
                const payload = {
                    eventType: type,
                    timestamp: new Date().toISOString(),
                    publisher: "account-service"
                };

                amqConsole.textContent = `[JMS convertAndSend] Sending message to SF-BA0352-EBMQueue.local...\nPayload:\n${JSON.stringify(payload, null, 2)}`;
            });
        }
    }

    renderBody() {
        const body = this.querySelector('#serviceAccBody');
        if (!body) return;

        body.innerHTML = this.accounts.map(acc => `
            <tr>
                <td style="font-weight: 500;">ACC-${1000 + acc.id}</td>
                <td>${acc.name}</td>
                <td>${acc.counterpartyName}</td>
                <td>
                    <span style="font-size:12px; font-weight:600; padding:4px 8px; border-radius:12px; 
                                 background:rgba(16,185,129,0.15); color:var(--color-green);">
                        ACTIVE
                    </span>
                </td>
            </tr>
        `).join('');
    }
}

customElements.define('app-account', AccountComponent);
