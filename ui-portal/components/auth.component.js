class AuthComponent extends HTMLElement {
    connectedCallback() {
        this.tokenData = null;
        this.render();
        this.setupListeners();
    }

    render() {
        this.innerHTML = `
            <div class="view-container">
                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 24px;">
                    <!-- Token request panel -->
                    <div class="card" style="display: flex; flex-direction: column; gap: 16px;">
                        <h3 style="font-weight: 600; font-size: 16px;">PingFederate Token Request (auth-service)</h3>
                        
                        <form id="tokenForm">
                            <div class="form-group">
                                <label for="authClientId">Client ID</label>
                                <input type="text" id="authClientId" value="RapidX_Gateway_Client" required>
                            </div>
                            <div class="form-group">
                                <label for="authClientSecret">Client Secret</label>
                                <input type="password" id="authClientSecret" value="••••••••••••••••" required>
                            </div>
                            <div class="form-group">
                                <label for="authFlow">Authorization Flow</label>
                                <select id="authFlow">
                                    <option value="client_credentials">Client Credentials</option>
                                    <option value="password">Resource Owner Password Credentials (ROPC)</option>
                                </select>
                            </div>
                            <button type="submit" class="btn" style="width:100%; margin-top:8px;">
                                <i class="fa-solid fa-key"></i> Request Access Token
                            </button>
                        </form>
                    </div>

                    <!-- Token result & JWT Decoder -->
                    <div class="card" style="display: flex; flex-direction: column; gap: 16px;">
                        <h3 style="font-weight: 600; font-size: 16px;">Active JWT Authorization Token</h3>
                        
                        <div id="noTokenBox" style="padding: 24px; text-align: center; border: 1px dashed var(--border-color); border-radius: 12px; color: var(--text-secondary);">
                            <i class="fa-solid fa-lock" style="font-size: 24px; margin-bottom: 8px;"></i>
                            <p>No active token. Submit request to generate access credentials.</p>
                        </div>

                        <div id="tokenDetailsBox" style="display: none; flex-direction: column; gap: 12px;">
                            <div style="background: rgba(16,185,129,0.08); border: 1px solid var(--color-green); padding: 12px; border-radius: 8px; font-size: 13px; color: var(--color-green);">
                                <i class="fa-solid fa-circle-check"></i> JWT Token successfully acquired and cached!
                            </div>
                            
                            <div>
                                <label style="font-size: 12px; color: var(--text-secondary); font-weight:600;">ENCODED STRING:</label>
                                <div class="console-output" style="max-height: 80px; word-break: break-all; color: var(--color-indigo); margin-top: 4px;" id="encodedTokenStr"></div>
                            </div>

                            <div>
                                <label style="font-size: 12px; color: var(--text-secondary); font-weight:600;">JWT PAYLOAD (DECODED):</label>
                                <div class="console-output" style="margin-top: 4px;" id="decodedTokenPayload"></div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        `;
    }

    setupListeners() {
        const form = this.querySelector('#tokenForm');
        const noToken = this.querySelector('#noTokenBox');
        const tokenDetails = this.querySelector('#tokenDetailsBox');
        const encodedBox = this.querySelector('#encodedTokenStr');
        const decodedBox = this.querySelector('#decodedTokenPayload');

        if (form) {
            form.addEventListener('submit', (e) => {
                e.preventDefault();
                
                // Simulate JWT token generation
                const payload = {
                    iss: "https://securedev.fhlmc.com/as/token.oauth2",
                    sub: "ucnt_ping_ext_np_user",
                    aud: "RapidX_Gateway_Client",
                    exp: Math.floor(Date.now() / 1000) + 3600,
                    scope: ["read", "write", "oim-sync"],
                    token_key: "FMACJWT",
                    roles: ["ROLE_ADMIN", "ROLE_RECONCILIATOR"]
                };

                const mockToken = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IkZNS0VZMjEifQ." + 
                                  btoa(JSON.stringify(payload)) + 
                                  ".SignatureVerifiedForFreddieMacControlCenterKey";

                // Show boxes
                if (noToken) noToken.style.display = 'none';
                if (tokenDetails) tokenDetails.style.display = 'flex';
                
                if (encodedBox) encodedBox.textContent = mockToken;
                if (decodedBox) decodedBox.textContent = JSON.stringify(payload, null, 2);
            });
        }
    }
}

customElements.define('app-auth', AuthComponent);
