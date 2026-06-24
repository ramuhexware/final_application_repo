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
