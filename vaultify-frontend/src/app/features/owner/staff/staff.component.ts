import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { timeout } from 'rxjs';
import { ApiService, CreateStaffRequest, StaffResponse } from '../../../core/services/api.service';

@Component({
  selector: 'app-owner-staff',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './staff.component.html',
  styleUrl: './staff.component.scss'
})
export class StaffComponent implements OnInit {
  staff: StaffResponse[] = [];
  loading = false;
  saving = false;
  errorMessage = '';
  successMessage = '';
  showCreateForm = false;

  form: CreateStaffRequest = {
    fullName: '',
    email: '',
    password: 'Staff@12345',
    phone: ''
  };

  constructor(
    private apiService: ApiService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadStaff();
  }

  getStaffId(staff: StaffResponse): string {
    const item = staff as any;
    return item.id || item.userId || item.staffId || item._id || '';
  }

  loadStaff(): void {
    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';
    this.cdr.detectChanges();

    this.apiService.getOwnerStaff().pipe(timeout(8000)).subscribe({
      next: (response) => {
        this.staff = response || [];
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (error) => {

        this.loading = false;
        this.errorMessage =
          error.error?.message ||
          error.error?.error ||
          'Could not load staff.';

        this.cdr.detectChanges();
      }
    });
  }

  createStaff(): void {
    this.errorMessage = '';
    this.successMessage = '';

    if (!this.form.fullName || !this.form.email || !this.form.password) {
      this.errorMessage = 'Full name, email, and password are required.';
      this.cdr.detectChanges();
      return;
    }

    this.saving = true;
    this.cdr.detectChanges();

    this.apiService.createOwnerStaff(this.form).pipe(timeout(8000)).subscribe({
      next: () => {
        this.saving = false;
        this.successMessage = 'Staff member created successfully.';
        this.showCreateForm = false;

        this.form = {
          fullName: '',
          email: '',
          password: 'Staff@12345',
          phone: ''
        };

        this.cdr.detectChanges();
        this.loadStaff();
      },
      error: (error) => {

        this.saving = false;
        this.errorMessage =
          error.error?.message ||
          error.error?.error ||
          'Could not create staff member.';

        this.cdr.detectChanges();
      }
    });
  }

  disableStaff(staffMember: StaffResponse): void {
    const staffId = this.getStaffId(staffMember);

    if (!staffId) {
      this.errorMessage = 'Staff ID is missing.';
      this.cdr.detectChanges();
      return;
    }

    this.apiService.deleteOwnerStaff(staffId).pipe(timeout(8000)).subscribe({
      next: () => {
        this.successMessage = 'Staff member disabled successfully.';
        this.cdr.detectChanges();
        this.loadStaff();
      },
      error: (error) => {

        this.errorMessage =
          error.error?.message ||
          error.error?.error ||
          'Could not disable staff member.';

        this.cdr.detectChanges();
      }
    });
  }
}

