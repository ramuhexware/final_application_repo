import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';

import { AuthComponent } from './components/auth/auth.component';
import { AccountComponent } from './components/account/account.component';
import { PortalComponent } from './components/portal/portal.component';
import { BpelComponent } from './components/bpel/bpel.component';
import { AuditComponent } from './components/audit/audit.component';
import { ReportingComponent } from './components/reporting/reporting.component';
import { LegacyComponent } from './components/legacy/legacy.component';

@NgModule({
  declarations: [
    AppComponent,
    AuthComponent,
    AccountComponent,
    PortalComponent,
    BpelComponent,
    AuditComponent,
    ReportingComponent,
    LegacyComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
