import { Component, OnInit } from '@angular/core';
import { ApiService, AuditLog } from '../../api.service';

@Component({
  selector: 'app-audit',
  templateUrl: './audit.component.html',
  styleUrls: []
})
export class AuditComponent implements OnInit {
  allLogs: AuditLog[] = [];
  filteredLogs: AuditLog[] = [];
  searchQuery = '';
  selectedService = 'ALL';

  constructor(private apiService: ApiService) {}

  async ngOnInit() {
    await this.loadLogs();
  }

  async loadLogs() {
    this.allLogs = await this.apiService.getAuditLogs();
    this.applyFilters();
  }

  applyFilters() {
    const query = this.searchQuery.toLowerCase().trim();
    const service = this.selectedService;

    this.filteredLogs = this.allLogs.filter(log => {
      const matchesQuery = !query ||
                           log.id.toLowerCase().includes(query) ||
                           log.action.toLowerCase().includes(query) ||
                           log.service.toLowerCase().includes(query) ||
                           log.details.toLowerCase().includes(query);
      const matchesService = service === 'ALL' || log.service === service;
      return matchesQuery && matchesService;
    });
  }

  async onRefresh() {
    await this.loadLogs();
  }
}
