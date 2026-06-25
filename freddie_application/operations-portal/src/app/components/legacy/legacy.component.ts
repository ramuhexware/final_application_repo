import { Component } from '@angular/core';

@Component({
  selector: 'app-legacy',
  templateUrl: './legacy.component.html',
  styleUrls: []
})
export class LegacyComponent {
  legacyConsoleOutput = 'Ready...';

  onPingLegacy() {
    this.legacyConsoleOutput = `[Request] Outbound ping to Context path http://localhost:8087/legacy-service/index.jsp...\n`;
    setTimeout(() => {
      this.legacyConsoleOutput += `[Response] HTTP/1.1 200 OK\nServer: Apache-Coyote/1.1\nContent-Type: text/html;charset=ISO-8859-1\nLength: 1,421 bytes\n\nLegacy JSP sandbox response rendered successfully!`;
    }, 600);
  }
}
