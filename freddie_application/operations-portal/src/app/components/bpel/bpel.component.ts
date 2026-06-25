import { Component, OnInit } from '@angular/core';
import { ApiService, BpelInstance } from '../../api.service';

@Component({
  selector: 'app-bpel',
  templateUrl: './bpel.component.html',
  styleUrls: []
})
export class BpelComponent implements OnInit {
  instances: BpelInstance[] = [];
  bpelAccId: number | null = null;
  bpelAccName = '';
  activeSteps: Record<number, boolean> = {};

  constructor(private apiService: ApiService) {}

  async ngOnInit() {
    this.instances = await this.apiService.getBpelInstances();
  }

  async onInvokeBpel(event: Event) {
    event.preventDefault();
    if (this.bpelAccId === null || !this.bpelAccName) return;

    this.activeSteps = {};

    const activateStep = (stepNum: number) => {
      return new Promise<void>((resolve) => {
        setTimeout(() => {
          this.activeSteps[stepNum] = true;
          resolve();
        }, 400);
      });
    };

    await activateStep(1);
    await activateStep(2);
    await activateStep(3);
    await activateStep(4);
    await activateStep(5);

    try {
      await this.apiService.triggerBpelProcess(this.bpelAccId, this.bpelAccName);
      await activateStep(6);

      this.instances = await this.apiService.getBpelInstances();
      this.bpelAccId = null;
      this.bpelAccName = '';
    } catch (err: any) {
      alert(`BPEL invocation error: ${err.message}`);
    }
  }
}
