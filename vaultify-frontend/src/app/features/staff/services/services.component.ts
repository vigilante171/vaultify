import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { timeout } from 'rxjs';
import { ApiService, ServiceResponse } from '../../../core/services/api.service';

@Component({
  selector: 'app-staff-services',
  standalone: true,
  templateUrl: './services.component.html',
  styleUrl: './services.component.scss'
})
export class ServicesComponent implements OnInit {
  services: ServiceResponse[] = [];
  loading = false;
  errorMessage = '';

  constructor(
    private apiService: ApiService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadServices();
  }

  loadServices(): void {
    this.loading = true;
    this.errorMessage = '';
    this.cdr.detectChanges();

    this.apiService.getStaffServices().pipe(timeout(8000)).subscribe({
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
}

