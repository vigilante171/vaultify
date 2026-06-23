import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { catchError, forkJoin, of, timeout } from 'rxjs';
import {
  ApiService,
  AppointmentResponse,
  ClientResponse,
  ServiceResponse
} from '../../../core/services/api.service';

interface StaffStat {
  label: string;
  value: string | number;
  hint: string;
}

@Component({
  selector: 'app-staff-dashboard',
  standalone: true,
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit {
  todayAppointments: AppointmentResponse[] = [];
  allAppointments: AppointmentResponse[] = [];
  clients: ClientResponse[] = [];
  services: ServiceResponse[] = [];

  stats: StaffStat[] = [];
  nextAppointment: AppointmentResponse | null = null;

  loading = false;
  errorMessage = '';

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
    this.stats = [];
    this.nextAppointment = null;
    this.cdr.detectChanges();

    forkJoin({
      todayAppointments: this.apiService.getStaffTodayAppointments().pipe(
        timeout(8000),
        catchError((error) => {

          return of([] as AppointmentResponse[]);
        })
      ),
      allAppointments: this.apiService.getStaffAppointments().pipe(
        timeout(8000),
        catchError((error) => {

          return of([] as AppointmentResponse[]);
        })
      ),
      clients: this.apiService.getStaffClients().pipe(
        timeout(8000),
        catchError((error) => {

          return of([] as ClientResponse[]);
        })
      ),
      services: this.apiService.getStaffServices().pipe(
        timeout(8000),
        catchError((error) => {

          return of([] as ServiceResponse[]);
        })
      )
    }).subscribe({
      next: (response) => {
        this.todayAppointments = response.todayAppointments || [];
        this.allAppointments = response.allAppointments || [];
        this.clients = response.clients || [];
        this.services = response.services || [];

        this.nextAppointment = this.findNextAppointment(this.allAppointments);

        this.stats = [
          {
            label: 'Today',
            value: this.todayAppointments.length,
            hint: 'Appointments scheduled today'
          },
          {
            label: 'Appointments',
            value: this.allAppointments.length,
            hint: 'All visible reservations'
          },
          {
            label: 'Clients',
            value: this.clients.length,
            hint: 'Client book access'
          },
          {
            label: 'Services',
            value: this.services.length,
            hint: 'Available salon menu'
          }
        ];

        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (error) => {

        this.loading = false;
        this.errorMessage =
          error.error?.message ||
          error.error?.error ||
          'Could not load staff dashboard.';

        this.cdr.detectChanges();
      }
    });
  }

  private findNextAppointment(appointments: AppointmentResponse[]): AppointmentResponse | null {
    const now = new Date().getTime();

    const upcoming = appointments
      .filter((appointment) => {
        const appointmentTime = new Date(appointment.appointmentDateTime).getTime();
        return appointmentTime >= now && appointment.status !== 'CANCELLED' && appointment.status !== 'NO_SHOW';
      })
      .sort((a, b) => {
        return new Date(a.appointmentDateTime).getTime() - new Date(b.appointmentDateTime).getTime();
      });

    return upcoming[0] || null;
  }
}

