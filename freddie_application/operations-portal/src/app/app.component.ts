import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  constructor(private router: Router) {}

  getHeaderTitle(): string {
    const url = this.router.url;
    if (url.includes('/auth')) return 'Auth Security Gateway (auth-service)';
    if (url.includes('/accounts')) return 'Account Provisioning Hub (account-service)';
    if (url.includes('/registry')) return 'OIM Synchronization Portal';
    if (url.includes('/bpel')) return 'Oracle SOA BPEL Workflow Process';
    if (url.includes('/audit')) return 'Reactive Audit Tracking Logs';
    if (url.includes('/reports')) return 'Analytics Reporting Hub (report-service)';
    if (url.includes('/legacy')) return 'Legacy Tomcat Servlet Container';
    return 'Control Center Operations';
  }
}
