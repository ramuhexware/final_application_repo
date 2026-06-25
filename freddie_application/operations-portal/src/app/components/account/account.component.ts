import { Component, OnInit } from '@angular/core';
import { ApiService, Account } from '../../api.service';

@Component({
  selector: 'app-account',
  templateUrl: './account.component.html',
  styleUrls: []
})
export class AccountComponent implements OnInit {
  accounts: Account[] = [];
  regAccName = '';
  regCpName = '';
  amqEventType = 'ACCOUNT_CREATED';
  amqConsoleOutput = 'Ready for JMS convertAndSend...';

  constructor(private apiService: ApiService) {}

  async ngOnInit() {
    this.accounts = await this.apiService.getAccounts();
  }

  async onSaveAccount(event: Event) {
    event.preventDefault();
    if (!this.regAccName || !this.regCpName) return;
    const newAcc: Account = {
      name: this.regAccName,
      counterpartyName: this.regCpName
    };
    await this.apiService.createAccount(newAcc);
    this.accounts = await this.apiService.getAccounts();
    this.regAccName = '';
    this.regCpName = '';
  }

  onPublishJms(event: Event) {
    event.preventDefault();
    const payload = {
      eventType: this.amqEventType,
      timestamp: new Date().toISOString(),
      publisher: "account-service"
    };
    this.amqConsoleOutput = `[JMS convertAndSend] Sending message to SF-BA0352-EBMQueue.local...\nPayload:\n${JSON.stringify(payload, null, 2)}`;
  }
}
