class ApiService {
    constructor() {
        this.baseUrls = {
            auth: 'http://localhost:8888/auth', // Proxy/Direct port
            aggregator: 'http://localhost:8082',
            audit: 'http://localhost:8084',
            report: 'http://localhost:8086',
            legacy: 'http://localhost:8087',
            soa: 'http://localhost:8088'
        };
        
        // In-memory mock states
        this.mockServices = {
            'account-service': 'UP',
            'auth-service': 'UP',
            'aggregator-service': 'UP',
            'audit-service': 'UP',
            'email-service': 'UP',
            'history-service': 'UP',
            'report-service': 'UP',
            'legacy-service': 'UP',
            'soa-service': 'UP'
        };

        this.mockAccounts = [
            { id: 1, name: 'Freddie Mac Primary Account', counterpartyName: 'Chase Bank', status: 'SYNCHRONIZED', updatedAt: '2026-06-24 18:30:11' },
            { id: 2, name: 'Securitization Clearing Fund', counterpartyName: 'Wells Fargo', status: 'SYNCHRONIZED', updatedAt: '2026-06-24 18:45:00' },
            { id: 3, name: 'Liquidity Cash Buffer', counterpartyName: 'Bank of America', status: 'FAILED_OIM_SYNC', updatedAt: '2026-06-24 19:15:20' }
        ];

        this.mockAuditLogs = [
            { id: 'AUD-8821', action: 'CREATE_ACCOUNT', service: 'account-service', details: 'Account id 1 created', timestamp: '2026-06-24 18:30:00' },
            { id: 'AUD-8822', action: 'OIM_SYNC_TRIGGERED', service: 'aggregator-service', details: 'Counterparties OIM batch sync executed', timestamp: '2026-06-24 18:45:10' },
            { id: 'AUD-8823', action: 'OIM_SYNC_FAILURE', service: 'aggregator-service', details: 'Database connection timeout during OIM push to Oracle DB', timestamp: '2026-06-24 19:15:20' }
        ];

        this.mockReports = [
            { id: 101, title: 'Quarterly Audit Performance Report', content: 'Aggregated transactions count: 4,521. Active counterparties: 12. OIM reconciliation complete.', generatedAt: '2026-06-24T12:00:00Z' }
        ];

        this.mockBpelInstances = [
            { processId: 'BPEL-47da-992a', processName: 'AccountProvisioningProcess', status: 'COMPLETED', payload: '{"accountId": 101, "accountName": "Wells Fargo Cash"}', response: '{"status": "COMPLETED"}', createdAt: '2026-06-24T18:45:00Z' }
        ];
    }

    async getServiceStatuses() {
        // Return active status of services
        return this.mockServices;
    }

    async getAccounts() {
        try {
            const res = await fetch(`${this.baseUrls.aggregator}/api/v1/aggregator/counterparties`, { method: 'GET' });
            if (res.ok) return await res.json();
        } catch (e) {}
        return this.mockAccounts;
    }

    async triggerOimSync(account) {
        try {
            const res = await fetch(`${this.baseUrls.aggregator}/api/v1/aggregator/oim-sync`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(account)
            });
            if (res.ok) return await res.json();
        } catch (e) {}
        
        // Mock execution
        const newSync = {
            id: Math.floor(Math.random() * 1000),
            name: account.name,
            counterpartyName: account.counterpartyName,
            status: 'SYNCHRONIZED',
            updatedAt: new Date().toISOString().replace('T', ' ').substring(0, 19)
        };
        this.mockAccounts.push(newSync);
        return { message: 'OIM Sync execution completed successfully (Mock)', account: newSync };
    }

    async getAuditLogs() {
        try {
            const res = await fetch(`${this.baseUrls.audit}/api/audit/logs`, { method: 'GET' });
            if (res.ok) return await res.json();
        } catch (e) {}
        return this.mockAuditLogs;
    }

    async getReports() {
        try {
            const res = await fetch(`${this.baseUrls.report}/api/reports`, { method: 'GET' });
            if (res.ok) return await res.json();
        } catch (e) {}
        return this.mockReports;
    }

    async generateReport(title, content) {
        try {
            const res = await fetch(`${this.baseUrls.report}/api/reports`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ title, content })
            });
            if (res.ok) return await res.json();
        } catch (e) {}

        const newReport = {
            id: Math.floor(Math.random() * 1000),
            title,
            content,
            generatedAt: new Date().toISOString()
        };
        this.mockReports.push(newReport);
        return newReport;
    }

    async exportReports() {
        try {
            const res = await fetch(`${this.baseUrls.report}/api/reports/export`, { method: 'GET' });
            if (res.ok) {
                const blob = await res.blob();
                const url = window.URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.style.display = 'none';
                a.href = url;
                a.download = 'reports_export.csv';
                document.body.appendChild(a);
                a.click();
                window.URL.revokeObjectURL(url);
                return { success: true, serverExported: true };
            }
        } catch (e) {
            console.warn("Backend report-service not running, executing client-side mock export", e);
        }
        
        // Mock export
        let csvContent = "Title,Content Findings,Generated At\n";
        this.mockReports.forEach(rep => {
            const escapeVal = (val) => {
                if (!val) return "";
                const valStr = String(val);
                if (valStr.includes(",") || valStr.includes("\"") || valStr.includes("\n")) {
                    return `"${valStr.replace(/"/g, '""')}"`;
                }
                return valStr;
            };
            csvContent += `${escapeVal(rep.title)},${escapeVal(rep.content)},${rep.generatedAt}\n`;
        });
        
        const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.style.display = 'none';
        a.href = url;
        a.download = 'mock_reports_export.csv';
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url);
        
        return { success: true, serverExported: false };
    }

    async importReports(file) {
        try {
            const formData = new FormData();
            formData.append("file", file);
            const res = await fetch(`${this.baseUrls.report}/api/reports/import`, {
                method: 'POST',
                body: formData
            });
            if (res.ok) {
                const importedData = await res.json();
                importedData.forEach(item => {
                    if (!this.mockReports.some(rep => rep.id === item.id)) {
                        this.mockReports.push(item);
                    }
                });
                return { success: true, serverImported: true, count: importedData.length, data: importedData };
            }
        } catch (e) {
            console.warn("Backend report-service not running, executing client-side mock import", e);
        }
        
        // Mock import parser
        return new Promise((resolve, reject) => {
            const reader = new FileReader();
            reader.onload = (event) => {
                try {
                    const text = event.target.result;
                    const lines = text.split(/\r?\n/);
                    const imported = [];
                    for (let i = 1; i < lines.length; i++) {
                        const line = lines[i].trim();
                        if (!line) continue;
                        
                        const tokens = [];
                        let curVal = '';
                        let inQuotes = false;
                        for (let j = 0; j < line.length; j++) {
                            const char = line[j];
                            if (inQuotes) {
                                if (char === '"') {
                                    if (j + 1 < line.length && line[j+1] === '"') {
                                        curVal += '"';
                                        j++;
                                    } else {
                                        inQuotes = false;
                                    }
                                } else {
                                    curVal += char;
                                }
                            } else {
                                if (char === '"') {
                                    inQuotes = true;
                                } else if (char === ',') {
                                    tokens.push(curVal);
                                    curVal = '';
                                } else {
                                    curVal += char;
                                }
                            }
                        }
                        tokens.push(curVal);
                        
                        if (tokens.length >= 2) {
                            const newRep = {
                                id: Math.floor(Math.random() * 1000),
                                title: tokens[0],
                                content: tokens[1],
                                generatedAt: tokens[2] || new Date().toISOString()
                            };
                            this.mockReports.push(newRep);
                            imported.push(newRep);
                        }
                    }
                    resolve({ success: true, serverImported: false, count: imported.length, data: imported });
                } catch (err) {
                    reject(new Error("Failed to parse mock CSV file: " + err.message));
                }
            };
            reader.onerror = () => reject(new Error("File read error"));
            reader.readAsText(file);
        });
    }


    async triggerBpelProcess(accountId, accountName) {
        const payloadStr = JSON.stringify({ accountId, accountName });
        try {
            const res = await fetch(`${this.baseUrls.soa}/api/osb/proxy/process-account`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: payloadStr
            });
            if (res.ok) return await res.json();
        } catch (e) {}

        // Mock BPEL Process instance creation
        const processId = 'BPEL-' + Math.random().toString(36).substring(2, 10) + '-' + Math.random().toString(36).substring(2, 6);
        const newInstance = {
            processId,
            processName: 'AccountProvisioningProcess',
            status: 'COMPLETED',
            payload: payloadStr,
            response: `{"processId": "${processId}", "status": "COMPLETED", "message": "Orchestration workflow completed successfully (Mock)"}`,
            createdAt: new Date().toISOString()
        };
        this.mockBpelInstances.push(newInstance);
        return JSON.parse(newInstance.response);
    }

    async getBpelInstances() {
        return this.mockBpelInstances;
    }
}

// Instantiate and make globally available
const apiService = new ApiService();
