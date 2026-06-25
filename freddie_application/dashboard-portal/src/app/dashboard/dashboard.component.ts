import { Component, OnInit, AfterViewInit, ElementRef, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from '../api.service';

declare var Chart: any;

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit, AfterViewInit {
  statuses: Record<string, string> = {};
  metrics = {
    totalAccounts: 0,
    activeCounterparties: 0,
    pendingBpelTasks: 0,
    totalAuditLogs: 0
  };
  serviceList: { name: string; status: string }[] = [];

  @ViewChild('auditChart') chartCanvas!: ElementRef<HTMLCanvasElement>;

  constructor(private apiService: ApiService) {}

  async ngOnInit() {
    this.statuses = await this.apiService.getServiceStatuses();
    this.serviceList = Object.entries(this.statuses).map(([name, status]) => ({ name, status }));
    this.metrics = await this.apiService.getMetricsSummary();
  }

  ngAfterViewInit() {
    this.initializeChart();
  }

  initializeChart() {
    const ctx = this.chartCanvas?.nativeElement;
    if (!ctx) return;

    new Chart(ctx, {
      type: 'bar',
      data: {
        labels: ['account-service', 'aggregator-service', 'soa-service', 'report-service', 'audit-service'],
        datasets: [{
          label: 'Audit Events',
          data: [12, 28, 8, 4, 18],
          backgroundColor: [
            'rgba(99, 102, 241, 0.6)',
            'rgba(20, 184, 166, 0.6)',
            'rgba(168, 85, 247, 0.6)',
            'rgba(16, 185, 129, 0.6)',
            'rgba(245, 158, 11, 0.6)'
          ],
          borderColor: [
            '#6366f1',
            '#14b8a6',
            '#a855f7',
            '#10b981',
            '#f59e0b'
          ],
          borderWidth: 1.5,
          borderRadius: 6
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: { display: false }
        },
        scales: {
          y: {
            beginAtZero: true,
            ticks: { color: '#9ca3af' },
            grid: { color: 'rgba(255,255,255,0.05)' }
          },
          x: {
            ticks: { color: '#9ca3af' },
            grid: { display: false }
          }
        }
      }
    });
  }
}
