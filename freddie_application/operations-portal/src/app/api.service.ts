import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';

export interface Account {
  id?: number;
  name: string;
  counterpartyName: string;
  status?: string;
  updatedAt?: string;
}

export interface Report {
  id?: number;
  title: string;
  content: string;
  generatedAt?: string;
}

export interface AuditLog {
  id: string;
  action: string;
  service: string;
  details: string;
  timestamp: string;
}

export interface BpelInstance {
  processId: string;
  processName: string;
  status: string;
  payload: string;
  response: string;
  createdAt: string;
}

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private baseUrls = {
    auth: 'http://localhost:8888/auth',
    aggregator: 'http://localhost:8082',
    audit: 'http://localhost:8084',
    report: 'http://localhost:8086',
    legacy: 'http://localhost:8087',
    soa: 'http://localhost:8088'
  };

  private mockAccounts: Account[] = [
    { id: 1, name: 'Freddie Mac Primary Account', counterpartyName: 'Chase Bank', status: 'SYNCHRONIZED', updatedAt: '2026-06-24 18:30:11' },
    { id: 2, name: 'Securitization Clearing Fund', counterpartyName: 'Wells Fargo', status: 'SYNCHRONIZED', updatedAt: '2026-06-24 18:45:00' },
    { id: 3, name: 'Liquidity Cash Buffer', counterpartyName: 'Bank of America', status: 'FAILED_OIM_SYNC', updatedAt: '2026-06-24 19:15:20' }
  ];

  private mockAuditLogs: AuditLog[] = [
    { id: 'AUD-8821', action: 'CREATE_ACCOUNT', service: 'account-service', details: 'Account id 1 created', timestamp: '2026-06-24 18:30:00' },
    { id: 'AUD-8822', action: 'OIM_SYNC_TRIGGERED', service: 'aggregator-service', details: 'Counterparties OIM batch sync executed', timestamp: '2026-06-24 18:45:10' },
    { id: 'AUD-8823', action: 'OIM_SYNC_FAILURE', service: 'aggregator-service', details: 'Database connection timeout during OIM push to Oracle DB', timestamp: '2026-06-24 19:15:20' }
  ];

  private mockReports: Report[] = [
    { id: 101, title: 'Quarterly Audit Performance Report', content: 'Aggregated transactions count: 4,521. Active counterparties: 12. OIM reconciliation complete.', generatedAt: '2026-06-24T12:00:00Z' }
  ];

  private mockBpelInstances: BpelInstance[] = [
    { processId: 'BPEL-47da-992a', processName: 'AccountProvisioningProcess', status: 'COMPLETED', payload: '{"accountId": 101, "accountName": "Wells Fargo Cash"}', response: '{"status": "COMPLETED"}', createdAt: '2026-06-24T18:45:00Z' }
  ];

  constructor(private http: HttpClient) {}

  getToken(): string | null {
    return localStorage.getItem('FMAC_JWT');
  }

  saveToken(token: string) {
    localStorage.setItem('FMAC_JWT', token);
  }

  clearToken() {
    localStorage.removeItem('FMAC_JWT');
  }

  async login(username: string, password: string): Promise<any> {
    try {
      const res = await firstValueFrom(this.http.post<any>(`${this.baseUrls.auth}/api/auth/login`, { username, password }));
      if (res && res.token) {
        this.saveToken(res.token);
      }
      return res;
    } catch (e) {
      // Mock login check
      if (username === 'admin' && password === 'admin') {
        const mockRes = { token: 'FMACJWT-MOCK-TOKEN-XYZ' };
        this.saveToken(mockRes.token);
        return mockRes;
      }
      throw new Error('Authentication failed (Mock checks only allow admin/admin)');
    }
  }

  async register(username: string, email: string, password: string): Promise<any> {
    try {
      return await firstValueFrom(this.http.post<any>(`${this.baseUrls.auth}/api/auth/register`, { username, email, password }));
    } catch (e) {
      return { message: 'Registered successfully (Mock fallback)' };
    }
  }

  async getAccounts(): Promise<Account[]> {
    try {
      return await firstValueFrom(this.http.get<Account[]>(`${this.baseUrls.aggregator}/api/v1/aggregator/counterparties`));
    } catch (e) {
      return this.mockAccounts;
    }
  }

  async triggerOimSync(account: Account): Promise<any> {
    try {
      return await firstValueFrom(this.http.post<any>(`${this.baseUrls.aggregator}/api/v1/aggregator/oim-sync`, account));
    } catch (e) {
      const newSync: Account = {
        id: Math.floor(Math.random() * 1000),
        name: account.name,
        counterpartyName: account.counterpartyName,
        status: 'SYNCHRONIZED',
        updatedAt: new Date().toISOString().replace('T', ' ').substring(0, 19)
      };
      this.mockAccounts.push(newSync);
      return { message: 'OIM Sync execution completed successfully (Mock)', account: newSync };
    }
  }

  async createAccount(account: Account): Promise<any> {
    try {
      return await firstValueFrom(this.http.post<any>(`${this.baseUrls.aggregator}/api/v1/aggregator/accounts/create`, account));
    } catch (e) {
      const newAcc: Account = {
        id: Math.floor(Math.random() * 1000),
        name: account.name,
        counterpartyName: account.counterpartyName,
        status: 'PENDING_SYNC',
        updatedAt: new Date().toISOString().replace('T', ' ').substring(0, 19)
      };
      this.mockAccounts.push(newAcc);
      return newAcc;
    }
  }

  async getAuditLogs(): Promise<AuditLog[]> {
    try {
      return await firstValueFrom(this.http.get<AuditLog[]>(`${this.baseUrls.audit}/api/audit/logs`));
    } catch (e) {
      return this.mockAuditLogs;
    }
  }

  async getReports(): Promise<Report[]> {
    try {
      return await firstValueFrom(this.http.get<Report[]>(`${this.baseUrls.report}/api/reports`));
    } catch (e) {
      return this.mockReports;
    }
  }

  async generateReport(title: string, content: string): Promise<Report> {
    try {
      return await firstValueFrom(this.http.post<Report>(`${this.baseUrls.report}/api/reports`, { title, content }));
    } catch (e) {
      const newReport = {
        id: Math.floor(Math.random() * 1000),
        title,
        content,
        generatedAt: new Date().toISOString()
      };
      this.mockReports.push(newReport);
      return newReport;
    }
  }

  async exportReports(): Promise<{ success: boolean; serverExported: boolean }> {
    try {
      const blob = await firstValueFrom(this.http.get(`${this.baseUrls.report}/api/reports/export`, { responseType: 'blob' }));
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.style.display = 'none';
      a.href = url;
      a.download = 'reports_export.csv';
      document.body.appendChild(a);
      a.click();
      window.URL.revokeObjectURL(url);
      return { success: true, serverExported: true };
    } catch (e) {
      // Mock export
      let csvContent = 'Title,Content Findings,Generated At\n';
      this.mockReports.forEach(rep => {
        const escapeVal = (val: string | undefined) => {
          if (!val) return '';
          const valStr = String(val);
          if (valStr.includes(',') || valStr.includes('"') || valStr.includes('\n')) {
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
  }

  async importReports(file: File): Promise<{ success: boolean; serverImported: boolean; count: number; data: any[] }> {
    try {
      const formData = new FormData();
      formData.append('file', file);
      const importedData = await firstValueFrom(this.http.post<any[]>(`${this.baseUrls.report}/api/reports/import`, formData));
      importedData.forEach(item => {
        if (!this.mockReports.some(rep => rep.id === item.id)) {
          this.mockReports.push(item);
        }
      });
      return { success: true, serverImported: true, count: importedData.length, data: importedData };
    } catch (e) {
      // Mock import parser
      return new Promise((resolve, reject) => {
        const reader = new FileReader();
        reader.onload = (event: any) => {
          try {
            const text = event.target.result;
            const lines = text.split(/\r?\n/);
            const imported: Report[] = [];
            for (let i = 1; i < lines.length; i++) {
              const line = lines[i].trim();
              if (!line) continue;

              const tokens: string[] = [];
              let curVal = '';
              let inQuotes = false;
              for (let j = 0; j < line.length; j++) {
                const char = line[j];
                if (inQuotes) {
                  if (char === '"') {
                    if (j + 1 < line.length && line[j + 1] === '"') {
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
          } catch (err: any) {
            reject(new Error('Failed to parse mock CSV file: ' + err.message));
          }
        };
        reader.onerror = () => reject(new Error('File read error'));
        reader.readAsText(file);
      });
    }
  }

  async triggerBpelProcess(accountId: number, accountName: string): Promise<any> {
    const payloadStr = JSON.stringify({ accountId, accountName });
    try {
      return await firstValueFrom(this.http.post<any>(`${this.baseUrls.soa}/api/osb/proxy/process-account`, payloadStr, {
        headers: { 'Content-Type': 'application/json' }
      }));
    } catch (e) {
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
  }

  async getBpelInstances(): Promise<BpelInstance[]> {
    return Promise.resolve(this.mockBpelInstances);
  }
}
