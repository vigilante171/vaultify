import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { timeout } from 'rxjs';
import {
  ApiService,
  CreateServiceRequest,
  ServiceCategory,
  ServiceResponse
} from '../../../core/services/api.service';

@Component({
  selector: 'app-owner-services',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './services.component.html',
  styleUrl: './services.component.scss'
})
export class ServicesComponent implements OnInit {
  services: ServiceResponse[] = [];
  loading = false;
  saving = false;
  errorMessage = '';
  successMessage = '';
  showCreateForm = false;

  categories: ServiceCategory[] = ['HAIR', 'BEARD', 'COLORING', 'TREATMENT', 'STYLING', 'OTHER'];

  form: CreateServiceRequest = {
    name: '',
    description: '',
    category: 'HAIR',
    price: 0,
    durationMinutes: 30
  };

  constructor(
    private apiService: ApiService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadServices();
  }

  getServiceId(service: ServiceResponse): string {
    const item = service as any;
    return item.id || item.serviceId || item._id || '';
  }

  loadServices(): void {
    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';
    this.cdr.detectChanges();

    this.apiService.getOwnerServices().pipe(timeout(8000)).subscribe({
      next: (response) => {

        this.services = response || [];
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (error) => {

        this.loading = false;
        this.errorMessage =
          error.error?.message ||
          error.error?.error ||
          'Could not load services.';

        this.cdr.detectChanges();
      }
    });
  }

  toggleCreateForm(): void {
    this.showCreateForm = !this.showCreateForm;
    this.errorMessage = '';
    this.successMessage = '';
    this.cdr.detectChanges();
  }

  createService(): void {
    this.errorMessage = '';
    this.successMessage = '';

    if (!this.form.name || this.form.price <= 0 || this.form.durationMinutes <= 0) {
      this.errorMessage = 'Service name, valid price, and duration are required.';
      this.cdr.detectChanges();
      return;
    }

    this.saving = true;
    this.cdr.detectChanges();

    this.apiService.createOwnerService(this.form).pipe(timeout(8000)).subscribe({
      next: () => {
        this.saving = false;
        this.successMessage = 'Service created successfully.';
        this.showCreateForm = false;

        this.form = {
          name: '',
          description: '',
          category: 'HAIR',
          price: 0,
          durationMinutes: 30
        };

        this.cdr.detectChanges();
        this.loadServices();
      },
      error: (error) => {

        this.saving = false;
        this.errorMessage =
          error.error?.message ||
          error.error?.error ||
          'Could not create service.';

        this.cdr.detectChanges();
      }
    });
  }

  disableService(service: ServiceResponse): void {
    const serviceId = this.getServiceId(service);

    if (!serviceId) {
      this.errorMessage = 'Service ID is missing.';
      this.cdr.detectChanges();
      return;
    }

    this.apiService.deleteOwnerService(serviceId).pipe(timeout(8000)).subscribe({
      next: () => {
        this.successMessage = 'Service disabled successfully.';
        this.cdr.detectChanges();
        this.loadServices();
      },
      error: (error) => {

        this.errorMessage =
          error.error?.message ||
          error.error?.error ||
          'Could not disable service.';

        this.cdr.detectChanges();
      }
    });
  }
}

