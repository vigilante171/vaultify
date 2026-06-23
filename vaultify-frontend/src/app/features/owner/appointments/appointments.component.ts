import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { catchError, forkJoin, of, timeout } from 'rxjs';
import {
  ApiService,
  AppointmentResponse,
  AppointmentStatus,
  ClientResponse,
  CreateAppointmentRequest,
  ServiceResponse,
  UpdateAppointmentRequest
} from '../../../core/services/api.service';

@Component({
  selector: 'app-owner-appointments',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './appointments.component.html',
  styleUrl: './appointments.component.scss'
})
export class AppointmentsComponent implements OnInit {
  appointments: AppointmentResponse[] = [];
  clients: ClientResponse[] = [];
  services: ServiceResponse[] = [];

  loading = false;
  saving = false;
  savingAppointmentId = '';
  errorMessage = '';
  successMessage = '';
  showCreateForm = false;

  statuses: AppointmentStatus[] = ['PENDING', 'CONFIRMED', 'COMPLETED', 'CANCELLED', 'NO_SHOW'];
  statusDrafts: Record<string, AppointmentStatus> = {};

  form: CreateAppointmentRequest = {
    clientId: '',
    serviceId: '',
    appointmentDateTime: '',
    notes: ''
  };

  constructor(
    private apiService: ApiService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadPageData();
  }

  getClientId(client: ClientResponse): string {
    const item = client as any;
    return item.id || item.clientId || item._id || '';
  }

  getServiceId(service: ServiceResponse): string {
    const item = service as any;
    return item.id || item.serviceId || item._id || '';
  }

  getAppointmentId(appointment: AppointmentResponse): string {
    const item = appointment as any;
    return item.id || item.appointmentId || item._id || '';
  }

  loadPageData(): void {
    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';
    this.cdr.detectChanges();

    forkJoin({
      clients: this.apiService.getOwnerClients().pipe(
        timeout(8000),
        catchError((error) => {

          this.errorMessage = 'Could not load clients.';
          return of([] as ClientResponse[]);
        })
      ),
      services: this.apiService.getOwnerServices().pipe(
        timeout(8000),
        catchError((error) => {

          this.errorMessage = 'Could not load services.';
          return of([] as ServiceResponse[]);
        })
      ),
      appointments: this.apiService.getOwnerAppointments().pipe(
        timeout(8000),
        catchError((error) => {

          this.errorMessage = error.error?.message || 'Could not load appointments.';
          return of([] as AppointmentResponse[]);
        })
      )
    }).subscribe({
      next: (response) => {
        this.clients = response.clients.filter((client: any) => {
          return client.active !== false && !!this.getClientId(client);
        });

        this.services = response.services.filter((service: any) => {
          return service.active !== false && !!this.getServiceId(service);
        });

        this.appointments = response.appointments || [];
        this.statusDrafts = {};

        this.appointments.forEach((appointment) => {
          const appointmentId = this.getAppointmentId(appointment);
          if (appointmentId) {
            this.statusDrafts[appointmentId] = appointment.status || 'PENDING';
          }
        });

        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (error) => {

        this.loading = false;
        this.errorMessage = 'Could not load appointments page.';
        this.cdr.detectChanges();
      }
    });
  }

  createAppointment(): void {
    this.errorMessage = '';
    this.successMessage = '';

    if (!this.form.clientId || !this.form.serviceId || !this.form.appointmentDateTime) {
      this.errorMessage = 'Client, service, and appointment date are required.';
      this.cdr.detectChanges();
      return;
    }

    const payload: CreateAppointmentRequest = {
      clientId: this.form.clientId,
      serviceId: this.form.serviceId,
      appointmentDateTime: this.normalizeDateTime(this.form.appointmentDateTime),
      notes: this.form.notes || ''
    };

    this.saving = true;
    this.cdr.detectChanges();

    this.apiService.createOwnerAppointment(payload).pipe(timeout(8000)).subscribe({
      next: () => {
        this.saving = false;
        this.successMessage = 'Appointment created successfully.';
        this.showCreateForm = false;

        this.form = {
          clientId: '',
          serviceId: '',
          appointmentDateTime: '',
          notes: ''
        };

        this.cdr.detectChanges();
        this.loadPageData();
      },
      error: (error) => {

        this.saving = false;
        this.errorMessage =
          error.error?.message ||
          error.error?.error ||
          JSON.stringify(error.error) ||
          'Could not create appointment.';

        this.cdr.detectChanges();
      }
    });
  }

  updateStatus(appointment: AppointmentResponse): void {
    const appointmentId = this.getAppointmentId(appointment);

    if (!appointmentId) {
      this.errorMessage = 'Appointment ID is missing.';
      this.cdr.detectChanges();
      return;
    }

    const selectedStatus = this.statusDrafts[appointmentId] || appointment.status || 'PENDING';

    const payload: UpdateAppointmentRequest = {
      clientId: appointment.clientId,
      serviceId: appointment.serviceId,
      appointmentDateTime: this.normalizeDateTime(appointment.appointmentDateTime),
      status: selectedStatus,
      notes: appointment.notes || ''
    };

    this.savingAppointmentId = appointmentId;
    this.errorMessage = '';
    this.successMessage = '';
    this.cdr.detectChanges();

    this.apiService.updateOwnerAppointment(appointmentId, payload).pipe(timeout(8000)).subscribe({
      next: () => {
        this.savingAppointmentId = '';
        this.successMessage = 'Appointment status updated successfully.';
        this.cdr.detectChanges();
        this.loadPageData();
      },
      error: (error) => {

        this.savingAppointmentId = '';
        this.errorMessage =
          error.error?.message ||
          error.error?.error ||
          'Could not update appointment status.';

        this.cdr.detectChanges();
      }
    });
  }

  quickStatus(appointment: AppointmentResponse, status: AppointmentStatus): void {
    const appointmentId = this.getAppointmentId(appointment);

    if (!appointmentId) {
      this.errorMessage = 'Appointment ID is missing.';
      this.cdr.detectChanges();
      return;
    }

    this.statusDrafts[appointmentId] = status;
    this.updateStatus(appointment);
  }

  cancelAppointment(appointment: AppointmentResponse): void {
    this.quickStatus(appointment, 'CANCELLED');
  }

  private normalizeDateTime(value: string): string {
    if (!value) {
      return value;
    }

    if (value.length === 16) {
      return `${value}:00`;
    }

    return value;
  }
}

