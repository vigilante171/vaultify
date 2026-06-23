import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { timeout } from 'rxjs';
import {
  ApiService,
  ChangePasswordRequest,
  ProfileResponse,
  UpdateProfileRequest
} from '../../../core/services/api.service';

@Component({
  selector: 'app-owner-profile',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.scss'
})
export class ProfileComponent implements OnInit {
  profile: ProfileResponse | null = null;

  loading = false;
  savingProfile = false;
  savingPassword = false;
  errorMessage = '';
  successMessage = '';

  profileForm: UpdateProfileRequest = {
    fullName: '',
    email: ''
  };

  passwordForm: ChangePasswordRequest = {
    currentPassword: '',
    newPassword: ''
  };

  constructor(
    private apiService: ApiService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadProfile();
  }

  loadProfile(): void {
    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';
    this.cdr.detectChanges();

    this.apiService.getOwnerProfile().pipe(timeout(8000)).subscribe({
      next: (response) => {
        this.profile = response;
        this.profileForm = {
          fullName: response.fullName || '',
          email: response.email || ''
        };

        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (error) => {

        this.loading = false;
        this.errorMessage =
          error.error?.message ||
          error.error?.error ||
          'Could not load profile.';

        this.cdr.detectChanges();
      }
    });
  }

  updateProfile(): void {
    this.errorMessage = '';
    this.successMessage = '';

    if (!this.profileForm.fullName || !this.profileForm.email) {
      this.errorMessage = 'Full name and email are required.';
      this.cdr.detectChanges();
      return;
    }

    this.savingProfile = true;
    this.cdr.detectChanges();

    this.apiService.updateOwnerProfile(this.profileForm).pipe(timeout(8000)).subscribe({
      next: (response) => {
        this.profile = response;
        this.savingProfile = false;
        this.successMessage = 'Profile updated successfully.';
        this.cdr.detectChanges();
      },
      error: (error) => {

        this.savingProfile = false;
        this.errorMessage =
          error.error?.message ||
          error.error?.error ||
          'Could not update profile.';

        this.cdr.detectChanges();
      }
    });
  }

  changePassword(): void {
    this.errorMessage = '';
    this.successMessage = '';

    if (!this.passwordForm.currentPassword || !this.passwordForm.newPassword) {
      this.errorMessage = 'Current password and new password are required.';
      this.cdr.detectChanges();
      return;
    }

    if (this.passwordForm.newPassword.length < 8) {
      this.errorMessage = 'New password must contain at least 8 characters.';
      this.cdr.detectChanges();
      return;
    }

    this.savingPassword = true;
    this.cdr.detectChanges();

    this.apiService.changeOwnerPassword(this.passwordForm).pipe(timeout(8000)).subscribe({
      next: () => {
        this.savingPassword = false;
        this.successMessage = 'Password changed successfully.';
        this.passwordForm = {
          currentPassword: '',
          newPassword: ''
        };

        this.cdr.detectChanges();
      },
      error: (error) => {

        this.savingPassword = false;
        this.errorMessage =
          error.error?.message ||
          error.error?.error ||
          'Could not change password.';

        this.cdr.detectChanges();
      }
    });
  }
}

