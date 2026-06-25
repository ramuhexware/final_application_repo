import { Component } from '@angular/core';
import { ApiService } from '../../api.service';

@Component({
  selector: 'app-auth',
  templateUrl: './auth.component.html',
  styleUrls: []
})
export class AuthComponent {
  clientId = 'RapidX_Gateway_Client';
  clientSecret = '••••••••••••••••';
  authFlow = 'client_credentials';
  tokenData: any = null;
  encodedToken = '';

  constructor(private apiService: ApiService) {}

  onRequestToken(event: Event) {
    event.preventDefault();
    const payload = {
      iss: "https://securedev.fhlmc.com/as/token.oauth2",
      sub: "ucnt_ping_ext_np_user",
      aud: "RapidX_Gateway_Client",
      exp: Math.floor(Date.now() / 1000) + 3600,
      scope: ["read", "write", "oim-sync"],
      token_key: "FMACJWT",
      roles: ["ROLE_ADMIN", "ROLE_RECONCILIATOR"]
    };

    this.encodedToken = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IkZNS0VZMjEifQ." +
                      btoa(JSON.stringify(payload)) +
                      ".SignatureVerifiedForFreddieMacControlCenterKey";
    this.tokenData = payload;
    this.apiService.saveToken(this.encodedToken);
  }
}
