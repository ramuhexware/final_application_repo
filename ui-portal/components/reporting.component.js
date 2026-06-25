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

                        <!-- File IO Operations Card -->
                        <div class="card" style="display: flex; flex-direction: column; gap: 14px;">
                            <h4 style="font-weight: 600; font-size: 14px; color: var(--color-indigo);"><i class="fa-solid fa-file-csv"></i> File IO Operations</h4>
                            
                            <!-- Export Section -->
                            <div style="display: flex; flex-direction: column; gap: 8px; border-bottom: 1px solid var(--border-color); padding-bottom: 12px;">
                                <span style="font-size: 13px; font-weight: 500;">Export Reports</span>
                                <button id="btnExportReports" class="btn" style="width: 100%; display: flex; align-items: center; justify-content: center; gap: 6px; padding: 8px 12px; font-size: 13px;">
                                    <i class="fa-solid fa-file-arrow-down"></i> Export to CSV
                                </button>
                                <div id="exportStatusBox" style="display:none; font-size:11px; text-align:center; padding: 6px; border-radius: 6px;"></div>
                            </div>
                            
                            <!-- Import Section -->
                            <div style="display: flex; flex-direction: column; gap: 8px;">
                                <span style="font-size: 13px; font-weight: 500;">Import Reports</span>
                                <input type="file" id="importFileInput" accept=".csv" style="font-size: 12px; background: var(--bg-dark); border: 1px solid var(--border-color); padding: 5px; border-radius: 6px; width: 100%; color: var(--text-secondary);">
                                <button id="btnImportReports" class="btn btn-secondary" style="width: 100%; display: flex; align-items: center; justify-content: center; gap: 6px; padding: 8px 12px; font-size: 13px;">
                                    <i class="fa-solid fa-file-arrow-up"></i> Upload & Import
                                </button>
                                <div id="importStatusBox" style="display:none; font-size:11px; text-align:center; padding: 6px; border-radius: 6px;"></div>
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

        const btnExport = this.querySelector('#btnExportReports');
        const exportStatus = this.querySelector('#exportStatusBox');
        if (btnExport) {
            btnExport.addEventListener('click', async () => {
                try {
                    btnExport.disabled = true;
                    btnExport.innerHTML = '<i class="fa-solid fa-spinner fa-spin"></i> Exporting...';
                    const res = await apiService.exportReports();
                    if (exportStatus) {
                        exportStatus.style.display = 'block';
                        exportStatus.style.background = 'rgba(20,184,166,0.1)';
                        exportStatus.style.color = 'var(--color-teal)';
                        exportStatus.innerHTML = res.serverExported 
                            ? '<i class="fa-solid fa-circle-check"></i> Exported & saved to server <code>exported_reports/</code>!' 
                            : '<i class="fa-solid fa-circle-info"></i> CSV generated & downloaded!';
                        setTimeout(() => { exportStatus.style.display = 'none'; }, 5000);
                    }
                } catch (err) {
                    alert(`Export failed: ${err.message}`);
                } finally {
                    btnExport.disabled = false;
                    btnExport.innerHTML = '<i class="fa-solid fa-file-arrow-down"></i> Export to CSV';
                }
            });
        }

        const btnImport = this.querySelector('#btnImportReports');
        const fileInput = this.querySelector('#importFileInput');
        const importStatus = this.querySelector('#importStatusBox');
        if (btnImport && fileInput) {
            btnImport.addEventListener('click', async () => {
                const file = fileInput.files[0];
                if (!file) {
                    alert("Please select a CSV file to import first.");
                    return;
                }
                try {
                    btnImport.disabled = true;
                    btnImport.innerHTML = '<i class="fa-solid fa-spinner fa-spin"></i> Importing...';
                    if (importStatus) {
                        importStatus.style.display = 'block';
                        importStatus.style.background = 'rgba(234,179,8,0.1)';
                        importStatus.style.color = 'var(--color-yellow)';
                        importStatus.innerHTML = '<i class="fa-solid fa-circle-notch fa-spin"></i> Processing File...';
                    }
                    
                    const res = await apiService.importReports(file);
                    
                    if (importStatus) {
                        importStatus.style.background = 'rgba(16,185,129,0.1)';
                        importStatus.style.color = 'var(--color-green)';
                        importStatus.innerHTML = res.serverImported
                            ? `<i class="fa-solid fa-circle-check"></i> Server-side parsed: ${res.count} records imported! Saved in <code>uploaded_reports/</code>.`
                            : `<i class="fa-solid fa-circle-check"></i> Mock parsed: ${res.count} records imported!`;
                        setTimeout(() => { importStatus.style.display = 'none'; }, 6000);
                    }
                    
                    fileInput.value = '';
                    
                    // Reload reports
                    this.reports = await apiService.getReports();
                    this.renderBody();
                } catch (err) {
                    if (importStatus) {
                        importStatus.style.display = 'block';
                        importStatus.style.background = 'rgba(239,68,68,0.1)';
                        importStatus.style.color = 'var(--color-red)';
                        importStatus.innerHTML = `<i class="fa-solid fa-circle-xmark"></i> ${err.message}`;
                    }
                } finally {
                    btnImport.disabled = false;
                    btnImport.innerHTML = '<i class="fa-solid fa-file-arrow-up"></i> Upload & Import';
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
