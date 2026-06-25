import { Injectable } from '@angular/core';

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

  private mockServices: Record<string, string> = {
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

  constructor() {}

  async getServiceStatuses(): Promise<Record<string, string>> {
    return Promise.resolve(this.mockServices);
  }

  async getMetricsSummary() {
    return Promise.resolve({
      totalAccounts: 15,
      activeCounterparties: 5,
      pendingBpelTasks: 1,
      totalAuditLogs: 342
    });
  }
}
