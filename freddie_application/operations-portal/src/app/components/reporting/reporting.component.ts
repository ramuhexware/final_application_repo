import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { ApiService, Report } from '../../api.service';

@Component({
  selector: 'app-reporting',
  templateUrl: './reporting.component.html',
  styleUrls: []
})
export class ReportingComponent implements OnInit {
  reports: Report[] = [];
  repTitle = '';
  repContent = '';
  showSuccess = false;
  isExporting = false;
  isImporting = false;

  exportStatusVisible = false;
  exportStatusStyle = {};
  exportStatusMsg = '';

  importStatusVisible = false;
  importStatusStyle = {};
  importStatusMsg = '';

  @ViewChild('fileInput') fileInput!: ElementRef<HTMLInputElement>;

  constructor(private apiService: ApiService) {}

  async ngOnInit() {
    this.reports = await this.apiService.getReports();
  }

  async onCompileReport(event: Event) {
    event.preventDefault();
    if (!this.repTitle || !this.repContent) return;

    try {
      await this.apiService.generateReport(this.repTitle, this.repContent);
      this.showSuccess = true;
      setTimeout(() => { this.showSuccess = false; }, 4000);

      this.repTitle = '';
      this.repContent = '';
      this.reports = await this.apiService.getReports();
    } catch (err: any) {
      alert(`Error compiling report: ${err.message}`);
    }
  }

  async onExportReports() {
    try {
      this.isExporting = true;
      const res = await this.apiService.exportReports();

      this.exportStatusVisible = true;
      this.exportStatusStyle = { 'background': 'rgba(20,184,166,0.1)', 'color': 'var(--color-teal)' };
      this.exportStatusMsg = res.serverExported
        ? 'Exported & saved to server exported_reports/!'
        : 'CSV generated & downloaded!';
      setTimeout(() => { this.exportStatusVisible = false; }, 5000);
    } catch (err: any) {
      alert(`Export failed: ${err.message}`);
    } finally {
      this.isExporting = false;
    }
  }

  async onImportReports() {
    const input = this.fileInput?.nativeElement;
    const file = input?.files?.[0];
    if (!file) {
      alert("Please select a CSV file to import first.");
      return;
    }

    try {
      this.isImporting = true;
      this.importStatusVisible = true;
      this.importStatusStyle = { 'background': 'rgba(234,179,8,0.1)', 'color': 'var(--color-amber)' };
      this.importStatusMsg = 'Processing File...';

      const res = await this.apiService.importReports(file);

      this.importStatusStyle = { 'background': 'rgba(16,185,129,0.1)', 'color': 'var(--color-green)' };
      this.importStatusMsg = res.serverImported
        ? `Server-side parsed: ${res.count} records imported! Saved in uploaded_reports/.`
        : `Mock parsed: ${res.count} records imported!`;
      setTimeout(() => { this.importStatusVisible = false; }, 6000);

      if (input) input.value = '';
      this.reports = await this.apiService.getReports();
    } catch (err: any) {
      this.importStatusStyle = { 'background': 'rgba(239,68,68,0.1)', 'color': 'var(--color-red)' };
      this.importStatusMsg = err.message;
    } finally {
      this.isImporting = false;
    }
  }
}
