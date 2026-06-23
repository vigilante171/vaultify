import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { ApiService, SuperAdminDashboardResponse } from '../../../core/services/api.service';

interface StatCard {
  label: string;
  value: number;
  hint: string;
}

@Component({
  selector: 'app-super-admin-dashboard',
  standalone: true,
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit {
  loading = false;
  errorMessage = '';
  dashboard: SuperAdminDashboardResponse | null = null;
  stats: StatCard[] = [];

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

    this.apiService.getSuperAdminDashboard().subscribe({
      next: (response) => {

        this.dashboard = response;

        this.stats = [
          { label: 'Total Businesses', value: response.totalBusinesses ?? 0, hint: 'All salons and barbers' },
          { label: 'Active Businesses', value: response.activeBusinesses ?? 0, hint: 'Currently enabled' },
          { label: 'Inactive Businesses', value: response.inactiveBusinesses ?? 0, hint: 'Disabled accounts' },
          { label: 'Trial Businesses', value: response.trialBusinesses ?? 0, hint: 'Trying Vaultify' },
          { label: 'Active Subscriptions', value: response.activeSubscriptions ?? 0, hint: 'Paid or valid access' },
          { label: 'Expired Subscriptions', value: response.expiredSubscriptions ?? 0, hint: 'Need renewal' },
          { label: 'Suspended Subscriptions', value: response.suspendedSubscriptions ?? 0, hint: 'Blocked access' },
          { label: 'Owners', value: response.totalOwners ?? 0, hint: 'Business owner users' },
          { label: 'Staff', value: response.totalStaff ?? 0, hint: 'Employee accounts' }
        ];

        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (error) => {

        this.loading = false;

        if (error.status === 0) {
          this.errorMessage = 'Backend is not reachable. Make sure Spring Boot is running on http://localhost:8080.';
        } else if (error.status === 403) {
          this.errorMessage = 'Access denied. Login with a SUPER_ADMIN account.';
        } else {
          this.errorMessage = error.error?.message || 'Could not load dashboard.';
        }

        this.cdr.detectChanges();
      }
    });
  }
}

