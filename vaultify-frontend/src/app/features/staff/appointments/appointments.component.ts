import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { timeout } from 'rxjs';
import { ApiService, AppointmentResponse } from '../../../core/services/api.service';

@Component({
  selector: 'app-staff-appointments',
  standalone: true,
  templateUrl: './appointments.component.html',
  styleUrl: './appointments.component.scss'
})
export class AppointmentsComponent implements OnInit {
  appointments: AppointmentResponse[] = [];
  loading = false;
  errorMessage = '';

  constructor(
    private apiService: ApiService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadAppointments();
  }

  loadAppointments(): void {
    this.loading = true;
    this.errorMessage = '';
    this.cdr.detectChanges();

    this.apiService.getStaffAppointments().pipe(timeout(8000)).subscribe({
      next: (response) => {

        this.appointments = response || [];
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (error) => {

        this.loading = false;
        this.errorMessage =
          error.error?.message ||
          error.error?.error ||
          'Could not load appointments.';
        this.cdr.detectChanges();
      }
    });
  }
}

