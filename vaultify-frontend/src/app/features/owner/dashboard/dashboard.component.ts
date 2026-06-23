import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { ApiService, OwnerDashboardResponse } from '../../../core/services/api.service';

interface OwnerStat {
  label: string;
  value: string | number;
  hint: string;
}

@Component({
  selector: 'app-owner-dashboard',
  standalone: true,
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit {
  loading = false;
  errorMessage = '';
  dashboard: OwnerDashboardResponse | null = null;
  stats: OwnerStat[] = [];

  constructor(
    private apiService: ApiService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadDashboard();
  }

  loadDashboard(): void {
    this.loading = true;
    this.errorMessage = '';
    this.dashboard = null;
    this.stats = [];
    this.cdr.detectChanges();

    this.apiService.getOwnerDashboard().subscribe({
      next: (response) => {

        this.dashboard = response;

        this.stats = [
          { label: 'Clients', value: response.totalClients ?? 0, hint: 'Active client book' },
          { label: 'Services', value: response.totalServices ?? 0, hint: 'Salon menu' },
          { label: 'Appointments', value: response.totalAppointments ?? 0, hint: 'All reservations' },
          { label: 'Monthly Revenue', value: `${response.monthlyRevenue ?? 0} TND`, hint: 'Completed appointments' }
        ];

        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (error) => {

        this.loading = false;

        if (error.status === 0) {
          this.errorMessage = 'Backend is not reachable.';
        } else if (error.status === 401) {
          this.errorMessage = 'Unauthorized. Please logout and login again.';
        } else if (error.status === 403) {
          this.errorMessage = 'Access denied. Your owner account or business subscription is blocked.';
        } else {
          this.errorMessage = error.error?.message || 'Could not load owner dashboard.';
        }

        this.cdr.detectChanges();
      }
    });
  }
}

