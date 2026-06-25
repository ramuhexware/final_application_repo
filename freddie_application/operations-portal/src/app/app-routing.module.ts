import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthComponent } from './components/auth/auth.component';
import { AccountComponent } from './components/account/account.component';
import { PortalComponent } from './components/portal/portal.component';
import { BpelComponent } from './components/bpel/bpel.component';
import { AuditComponent } from './components/audit/audit.component';
import { ReportingComponent } from './components/reporting/reporting.component';
import { LegacyComponent } from './components/legacy/legacy.component';

const routes: Routes = [
  { path: '', redirectTo: 'auth', pathMatch: 'full' },
  { path: 'auth', component: AuthComponent },
  { path: 'accounts', component: AccountComponent },
  { path: 'registry', component: PortalComponent },
  { path: 'bpel', component: BpelComponent },
  { path: 'audit', component: AuditComponent },
  { path: 'reports', component: ReportingComponent },
  { path: 'legacy', component: LegacyComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
