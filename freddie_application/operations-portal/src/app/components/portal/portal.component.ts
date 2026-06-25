import { Component, OnInit } from '@angular/core';
import { ApiService, Account } from '../../api.service';

@Component({
  selector: 'app-portal',
  templateUrl: './portal.component.html',
  styleUrls: []
})
export class PortalComponent implements OnInit {
  accounts: Account[] = [];
  accName = '';
  cpName = '';
  oimConsoleOutput = 'Ready to launch...';

  constructor(private apiService: ApiService) {}

  async ngOnInit() {
    this.accounts = await this.apiService.getAccounts();
  }

  async onLaunchOimSync(event: Event) {
    event.preventDefault();
    if (!this.accName || !this.cpName) return;

    this.oimConsoleOutput = `[Request] Triggering OIM Sync for "${this.accName}"...\n`;

    try {
      const result = await this.apiService.triggerOimSync({ name: this.accName, counterpartyName: this.cpName });
      this.oimConsoleOutput += `[Response] Code: 200 OK\n${JSON.stringify(result, null, 2)}`;
      this.accounts = await this.apiService.getAccounts();
      this.accName = '';
      this.cpName = '';
    } catch (err: any) {
      this.oimConsoleOutput += `[Error] ${err.message}`;
    }
  }

  async onRefresh() {
    this.accounts = await this.apiService.getAccounts();
  }
}
