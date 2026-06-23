import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { timeout } from 'rxjs';
import { ApiService, BusinessResponse, UpdateBusinessProfileRequest } from '../../../core/services/api.service';

@Component({
  selector: 'app-owner-business-profile',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './business-profile.component.html',
  styleUrl: './business-profile.component.scss'
})
export class BusinessProfileComponent implements OnInit {
  loading = true;
  saving = false;
  errorMessage = '';
  successMessage = '';
  business: BusinessResponse | null = null;

  form: UpdateBusinessProfileRequest = {
    businessName: '',
    phone: '',
    address: '',
    logoUrl: ''
  };

  constructor(
    private apiService: ApiService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadBusiness();
  }

  loadBusiness(): void {
    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';
    this.cdr.detectChanges();

    this.apiService.getOwnerBusiness().pipe(timeout(8000)).subscribe({
      next: (response: any) => {

        this.business = response;

        this.form = {
          businessName: response.businessName || response.name || '',
          phone: response.phone || '',
          address: response.address || '',
          logoUrl: response.logoUrl || ''
        };

        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (error) => {

        this.loading = false;
        this.errorMessage =
          error.error?.message ||
          error.error?.error ||
          'Could not load business profile.';

        this.cdr.detectChanges();
      }
    });
  }

  save(): void {
    this.saving = true;
    this.errorMessage = '';
    this.successMessage = '';
    this.cdr.detectChanges();

    this.apiService.updateOwnerBusiness(this.form).subscribe({
      next: () => {
        this.saving = false;
        this.successMessage = 'Business profile updated successfully.';
        this.cdr.detectChanges();
        this.loadBusiness();
      },
      error: (error) => {

        this.saving = false;
        this.errorMessage =
          error.error?.message ||
          error.error?.error ||
          'Could not update business profile.';

        this.cdr.detectChanges();
      }
    });
  }
}

