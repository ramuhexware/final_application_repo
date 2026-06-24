class ReportingComponent extends HTMLElement {
    async connectedCallback() {
        this.reports = await apiService.getReports();
        this.render();
        this.setupListeners();
    }

    render() {
        const rows = this.reports.map(rep => `
            <tr>
                <td style="font-weight: 500;">${rep.id}</td>
                <td style="font-weight: 600; color: var(--color-indigo);">${rep.title}</td>
                <td style="color: var(--text-secondary);">${rep.content}</td>
                <td style="color: var(--text-secondary); font-size:13px;">${new Date(rep.generatedAt).toLocaleString()}</td>
            </tr>
        `).join('');

        this.innerHTML = `
            <div class="view-container">
                <div style="display: grid; grid-template-columns: 1fr 2fr; gap: 24px;">
                    <!-- Generate Report Form -->
                    <div style="display: flex; flex-direction: column; gap: 20px;">
                        <div class="card" style="display: flex; flex-direction: column; gap: 16px; height: fit-content;">
                            <h3 style="font-weight: 600; font-size: 16px;">Generate Audit Report</h3>
                            
                            <form id="reportForm">
                                <div class="form-group">
                                    <label for="repTitle">Report Title</label>
                                    <input type="text" id="repTitle" placeholder="e.g. June Reconciliations" required>
                                </div>
                                <div class="form-group">
                                    <label for="repContent">Summary / Findings</label>
                                    <textarea id="repContent" placeholder="Enter transaction summaries..." rows="4" required></textarea>
                                </div>
                                <button type="submit" class="btn" style="width: 100%; margin-top: 8px;">
                                    <i class="fa-solid fa-file-invoice"></i> Compile Report
                                </button>
                            </form>
                            
                            <div id="reportSuccessBox" style="display:none; padding:12px; border-radius:8px; background:rgba(16,185,129,0.1); border:1px solid var(--color-green); text-align:center; font-size:13px; color:var(--color-green);">
                                <i class="fa-solid fa-circle-check"></i> Report stored in Oracle Database!
                            </div>
                        </div>

                        <!-- DB Status Metadata Box -->
                        <div class="card" style="display: flex; flex-direction: column; gap: 12px;">
                            <h4 style="font-weight: 600; font-size: 14px; color: var(--color-teal);"><i class="fa-solid fa-database"></i> Database Connection</h4>
                            <div style="display:flex; justify-content:space-between; align-items:center; border-bottom:1px solid var(--border-color); padding:6px 0; font-size:13px;">
                                <span style="color:var(--text-secondary);">Provider</span>
                                <span>Oracle Free (23c slim)</span>
                            </div>
                            <div style="display:flex; justify-content:space-between; align-items:center; border-bottom:1px solid var(--border-color); padding:6px 0; font-size:13px;">
                                <span style="color:var(--text-secondary);">Database User</span>
                                <span style="font-family:monospace;">report_user</span>
                            </div>
                            <div style="display:flex; justify-content:space-between; align-items:center; border-bottom:1px solid var(--border-color); padding:6px 0; font-size:13px;">
                                <span style="color:var(--text-secondary);">Table Target</span>
                                <span style="font-family:monospace; color:var(--color-indigo);">report_records</span>
                            </div>
                            <div style="display:flex; justify-content:space-between; align-items:center; padding:6px 0; font-size:13px;">
                                <span style="color:var(--text-secondary);">JDBC URL</span>
                                <span style="font-family:monospace; font-size:11px; max-width: 140px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">jdbc:oracle:thin:@oracle-db:1521/FREEPDB1</span>
                            </div>
                        </div>
                    </div>

                    <!-- Reports list table -->
                    <div class="card" style="display: flex; flex-direction: column; gap: 16px;">
                        <h3 style="font-weight: 600; font-size: 16px;">Oracle Database Report Records</h3>
                        
                        <div class="table-container">
                            <table>
                                <thead>
                                    <tr>
                                        <th>Report ID</th>
                                        <th>Title</th>
                                        <th>Content Findings</th>
                                        <th>Generated Timestamp</th>
                                    </tr>
                                </thead>
                                <tbody id="reportsTableBody">
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
        const form = this.querySelector('#reportForm');
        const successBox = this.querySelector('#reportSuccessBox');

        if (form) {
            form.addEventListener('submit', async (e) => {
                e.preventDefault();
                const title = this.querySelector('#repTitle').value;
                const content = this.querySelector('#repContent').value;
                
                try {
                    await apiService.generateReport(title, content);
                    
                    // Show success
                    if (successBox) {
                        successBox.style.display = 'block';
                        setTimeout(() => { successBox.style.display = 'none'; }, 4000);
                    }

                    // Reset form
                    form.reset();

                    // Reload
                    this.reports = await apiService.getReports();
                    this.renderBody();
                } catch (err) {
                    alert(`Error compiling report: ${err.message}`);
                }
            });
        }
    }

    renderBody() {
        const body = this.querySelector('#reportsTableBody');
        if (!body) return;
        
        body.innerHTML = this.reports.map(rep => `
            <tr>
                <td style="font-weight: 500;">${rep.id}</td>
                <td style="font-weight: 600; color: var(--color-indigo);">${rep.title}</td>
                <td style="color: var(--text-secondary);">${rep.content}</td>
                <td style="color: var(--text-secondary); font-size:13px;">${new Date(rep.generatedAt).toLocaleString()}</td>
            </tr>
        `).join('');
    }
}

customElements.define('app-reporting', ReportingComponent);
