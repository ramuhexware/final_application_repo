class BpelComponent extends HTMLElement {
    async connectedCallback() {
        this.instances = await apiService.getBpelInstances();
        this.render();
        this.setupListeners();
    }

    render() {
        const rows = this.instances.map(inst => `
            <tr>
                <td style="font-family:'JetBrains Mono', monospace; font-size:12px; color:var(--color-indigo);">${inst.processId}</td>
                <td>${inst.processName}</td>
                <td>
                    <span style="font-size:12px; font-weight:600; padding:4px 8px; border-radius:12px; 
                                 background:rgba(16,185,129,0.15); color:var(--color-green);">
                        ${inst.status}
                    </span>
                </td>
                <td style="font-family:'JetBrains Mono', monospace; font-size:11px; color:var(--text-secondary); max-width: 200px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">
                    ${inst.payload}
                </td>
                <td style="color:var(--text-secondary); font-size:12px;">${new Date(inst.createdAt).toLocaleString()}</td>
            </tr>
        `).join('');

        this.innerHTML = `
            <div class="view-container">
                <div style="display: grid; grid-template-columns: 1.2fr 1.8fr; gap: 24px;">
                    
                    <!-- BPEL Trigger Form -->
                    <div style="display: flex; flex-direction: column; gap: 20px;">
                        <div class="card" style="display: flex; flex-direction: column; gap: 16px;">
                            <h3 style="font-weight: 600; font-size: 16px;">Trigger Orchestration</h3>
                            <form id="bpelForm">
                                <div class="form-group">
                                    <label for="bpelAccId">Account ID</label>
                                    <input type="number" id="bpelAccId" placeholder="e.g. 5051" required>
                                </div>
                                <div class="form-group">
                                    <label for="bpelAccName">Account Name</label>
                                    <input type="text" id="bpelAccName" placeholder="e.g. Securitization Escrow" required>
                                </div>
                                <button type="submit" class="btn" style="width:100%; margin-top:8px;">
                                    <i class="fa-solid fa-play"></i> Invoke BPEL Process
                                </button>
                            </form>
                        </div>

                        <!-- Step visualizer panel -->
                        <div class="card">
                            <h3 style="font-weight: 600; font-size: 16px; margin-bottom:12px;">Process Activity Sequence Monitor</h3>
                            <div class="bpel-sequence">
                                <div class="bpel-step" id="step1">
                                    <div class="bpel-step-badge">1</div>
                                    <div class="bpel-step-info">
                                        <div class="bpel-step-title">OSB Proxy Endpoint Inbound</div>
                                        <div class="bpel-step-desc">Proxy receives client HTTP POST request</div>
                                    </div>
                                </div>
                                <div class="bpel-step" id="step2">
                                    <div class="bpel-step-badge">2</div>
                                    <div class="bpel-step-info">
                                        <div class="bpel-step-title">OSB Message Transformation</div>
                                        <div class="bpel-step-desc">Applies XQuery/XSLT wrapper mapping</div>
                                    </div>
                                </div>
                                <div class="bpel-step" id="step3">
                                    <div class="bpel-step-badge">3</div>
                                    <div class="bpel-step-info">
                                        <div class="bpel-step-title">BPEL Receive & Variables Assign</div>
                                        <div class="bpel-step-desc">Initializes instance tracking variables</div>
                                    </div>
                                </div>
                                <div class="bpel-step" id="step4">
                                    <div class="bpel-step-badge">4</div>
                                    <div class="bpel-step-info">
                                        <div class="bpel-step-title">Invoke Partner Link: Oracle DB</div>
                                        <div class="bpel-step-desc">Persists RUNNING status tracking logs</div>
                                    </div>
                                </div>
                                <div class="bpel-step" id="step5">
                                    <div class="bpel-step-badge">5</div>
                                    <div class="bpel-step-info">
                                        <div class="bpel-step-title">Invoke Partner Link: Downstream Gateway</div>
                                        <div class="bpel-step-desc">Dispatches webhook request to target broker</div>
                                    </div>
                                </div>
                                <div class="bpel-step" id="step6">
                                    <div class="bpel-step-badge">6</div>
                                    <div class="bpel-step-info">
                                        <div class="bpel-step-title">BPEL Process Reply (Completed)</div>
                                        <div class="bpel-step-desc">Persists COMPLETED state and replies to proxy</div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Instance Table list -->
                    <div class="card" style="display:flex; flex-direction:column; gap:16px;">
                        <h3 style="font-weight: 600; font-size: 16px;">Orchestration Process Instance Registry</h3>
                        <div class="table-container">
                            <table>
                                <thead>
                                    <tr>
                                        <th>Process Instance ID</th>
                                        <th>Process Name</th>
                                        <th>Status</th>
                                        <th>Input Payload</th>
                                        <th>Created</th>
                                    </tr>
                                </thead>
                                <tbody id="bpelTableBody">
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
        const form = this.querySelector('#bpelForm');
        if (form) {
            form.addEventListener('submit', async (e) => {
                e.preventDefault();
                const accId = this.querySelector('#bpelAccId').value;
                const accName = this.querySelector('#bpelAccName').value;

                // Reset step classes
                for (let i = 1; i <= 6; i++) {
                    const el = this.querySelector(`#step${i}`);
                    if (el) el.classList.remove('completed');
                }

                // Simulating animated progression timing
                const activateStep = (stepNum) => {
                    return new Promise((resolve) => {
                        setTimeout(() => {
                            const el = this.querySelector(`#step${stepNum}`);
                            if (el) el.classList.add('completed');
                            resolve();
                        }, 500);
                    });
                };

                // Sequential activation animation
                await activateStep(1);
                await activateStep(2);
                await activateStep(3);
                await activateStep(4);
                await activateStep(5);
                
                try {
                    await apiService.triggerBpelProcess(parseInt(accId), accName);
                    await activateStep(6);
                    
                    // Reload instance grid
                    this.instances = await apiService.getBpelInstances();
                    this.renderBody();
                } catch (err) {
                    alert(`BPEL invocation error: ${err.message}`);
                }
            });
        }
    }

    renderBody() {
        const body = this.querySelector('#bpelTableBody');
        if (!body) return;

        body.innerHTML = this.instances.map(inst => `
            <tr>
                <td style="font-family:'JetBrains Mono', monospace; font-size:12px; color:var(--color-indigo);">${inst.processId}</td>
                <td>${inst.processName}</td>
                <td>
                    <span style="font-size:12px; font-weight:600; padding:4px 8px; border-radius:12px; 
                                 background:rgba(16,185,129,0.15); color:var(--color-green);">
                        ${inst.status}
                    </span>
                </td>
                <td style="font-family:'JetBrains Mono', monospace; font-size:11px; color:var(--text-secondary); max-width: 200px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">
                    ${inst.payload}
                </td>
                <td style="color:var(--text-secondary); font-size:12px;">${new Date(inst.createdAt).toLocaleString()}</td>
            </tr>
        `).join('');
    }
}

customElements.define('app-bpel', BpelComponent);
