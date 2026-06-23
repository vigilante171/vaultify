import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { timeout } from 'rxjs';
import {
  ApiService,
  BusinessResponse,
  BusinessType,
  CreateBusinessRequest,
  Plan,
  SubscriptionStatus,
  UpdateBusinessStatusRequest,
  UpdateBusinessSubscriptionRequest
} from '../../../core/services/api.service';

@Component({
  selector: 'app-super-admin-businesses',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './businesses.component.html',
  styleUrl: './businesses.component.scss'
})
export class BusinessesComponent implements OnInit {
  businesses: BusinessResponse[] = [];
  loading = false;
  saving = false;
  savingSubscription = false;
  savingStatus = false;
  errorMessage = '';
  successMessage = '';
  searchQuery = '';
  showCreateForm = false;
  selectedBusinessId = '';

  form: CreateBusinessRequest = {
    businessName: '',
    businessType: 'MEN_BARBER',
    ownerName: '',
    ownerEmail: '',
    ownerPassword: 'Owner@12345',
    phone: '',
    address: '',
    plan: 'STARTER'
  };

  subscriptionForm: UpdateBusinessSubscriptionRequest = {
    plan: 'STARTER',
    subscriptionStatus: 'TRIAL',
    subscriptionEndDate: ''
  };

  statusForm: UpdateBusinessStatusRequest = {
    active: true
  };

  businessTypes: BusinessType[] = ['HAIR_SALON', 'MEN_BARBER'];
  plans: Plan[] = ['STARTER', 'PRO', 'PREMIUM'];
  subscriptionStatuses: SubscriptionStatus[] = ['TRIAL', 'ACTIVE', 'EXPIRED', 'SUSPENDED'];

  constructor(
    private apiService: ApiService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadBusinesses();
  }

  getBusinessId(business: BusinessResponse): string {
    const item = business as any;
    return item.id || item.businessId || item._id || '';
  }

  get selectedBusiness(): BusinessResponse | null {
    return this.businesses.find((business) => this.getBusinessId(business) === this.selectedBusinessId) || null;
  }

  loadBusinesses(): void {
    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';
    this.cdr.detectChanges();

    this.apiService.getBusinesses().pipe(timeout(8000)).subscribe({
      next: (response) => {
        this.businesses = response || [];

        if (!this.selectedBusinessId && this.businesses.length > 0) {
          this.selectedBusinessId = this.getBusinessId(this.businesses[0]);
          this.syncSelectedBusinessForms();
        } else {
          this.syncSelectedBusinessForms();
        }

        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (error: any) => {

        this.loading = false;
        this.errorMessage =
          error.error?.message ||
          error.error?.error ||
          'Could not load businesses.';

        this.cdr.detectChanges();
      }
    });
  }

  search(): void {
    const query = this.searchQuery.trim();

    if (!query) {
      this.loadBusinesses();
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';
    this.cdr.detectChanges();

    this.apiService.searchBusinesses(query).pipe(timeout(8000)).subscribe({
      next: (response) => {
        this.businesses = response || [];

        if (this.businesses.length > 0) {
          this.selectedBusinessId = this.getBusinessId(this.businesses[0]);
          this.syncSelectedBusinessForms();
        }

        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (error: any) => {

        this.loading = false;
        this.errorMessage =
          error.error?.message ||
          error.error?.error ||
          'Search failed.';

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

  createBusiness(): void {
    this.errorMessage = '';
    this.successMessage = '';

    if (!this.form.businessName || !this.form.ownerName || !this.form.ownerEmail || !this.form.ownerPassword) {
      this.errorMessage = 'Business name, owner name, owner email, and password are required.';
      this.cdr.detectChanges();
      return;
    }

    this.saving = true;
    this.cdr.detectChanges();

    this.apiService.createBusiness(this.form).pipe(timeout(8000)).subscribe({
      next: () => {
        this.saving = false;
        this.successMessage = 'Business created successfully.';
        this.showCreateForm = false;

        this.form = {
          businessName: '',
          businessType: 'MEN_BARBER',
          ownerName: '',
          ownerEmail: '',
          ownerPassword: 'Owner@12345',
          phone: '',
          address: '',
          plan: 'STARTER'
        };

        this.cdr.detectChanges();
        this.loadBusinesses();
      },
      error: (error: any) => {

        this.saving = false;
        this.errorMessage =
          error.error?.message ||
          error.error?.error ||
          'Could not create business.';

        this.cdr.detectChanges();
      }
    });
  }

  onSelectedBusinessChange(): void {
    this.errorMessage = '';
    this.successMessage = '';
    this.syncSelectedBusinessForms();
    this.cdr.detectChanges();
  }

  syncSelectedBusinessForms(): void {
    const business = this.selectedBusiness;

    if (!business) {
      return;
    }

    this.subscriptionForm = {
      plan: business.plan || 'STARTER',
      subscriptionStatus: business.subscriptionStatus || 'TRIAL',
      subscriptionEndDate: this.toDateInputValue(business.subscriptionEndDate)
    };

    this.statusForm = {
      active: business.active
    };
  }

  updateSubscription(): void {
    if (!this.selectedBusinessId) {
      this.errorMessage = 'Please select a business first.';
      this.cdr.detectChanges();
      return;
    }

    if (!this.subscriptionForm.subscriptionEndDate) {
      this.errorMessage = 'Subscription end date is required.';
      this.cdr.detectChanges();
      return;
    }

    const payload: UpdateBusinessSubscriptionRequest = {
      plan: this.subscriptionForm.plan,
      subscriptionStatus: this.subscriptionForm.subscriptionStatus,
      subscriptionEndDate: this.toBackendDateValue(this.subscriptionForm.subscriptionEndDate)
    };

    this.savingSubscription = true;
    this.errorMessage = '';
    this.successMessage = '';
    this.cdr.detectChanges();

    this.apiService.updateBusinessSubscription(this.selectedBusinessId, payload).pipe(timeout(8000)).subscribe({
      next: () => {
        this.savingSubscription = false;
        this.successMessage = 'Business subscription updated successfully.';
        this.cdr.detectChanges();
        this.loadBusinesses();
      },
      error: (error: any) => {

        this.savingSubscription = false;
        this.errorMessage =
          error.error?.message ||
          error.error?.error ||
          'Could not update business subscription.';

        this.cdr.detectChanges();
      }
    });
  }

  updateStatus(): void {
    if (!this.selectedBusinessId) {
      this.errorMessage = 'Please select a business first.';
      this.cdr.detectChanges();
      return;
    }

    this.savingStatus = true;
    this.errorMessage = '';
    this.successMessage = '';
    this.cdr.detectChanges();

    this.apiService.updateBusinessStatus(this.selectedBusinessId, this.statusForm).pipe(timeout(8000)).subscribe({
      next: () => {
        this.savingStatus = false;
        this.successMessage = 'Business status updated successfully.';
        this.cdr.detectChanges();
        this.loadBusinesses();
      },
      error: (error: any) => {

        this.savingStatus = false;
        this.errorMessage =
          error.error?.message ||
          error.error?.error ||
          'Could not update business status.';

        this.cdr.detectChanges();
      }
    });
  }

  private toDateInputValue(value: string): string {
    if (!value) {
      return '';
    }

    return value.substring(0, 10);
  }

  private toBackendDateValue(value: string): string {
    if (value.length === 10) {
      return `${value}T23:59:59`;
    }

    return value;
  }
}


