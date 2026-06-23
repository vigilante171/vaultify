import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { timeout } from 'rxjs';
import { ApiService, SuperAdminUserResponse, UserRole } from '../../../core/services/api.service';

@Component({
  selector: 'app-super-admin-users',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './users.component.html',
  styleUrl: './users.component.scss'
})
export class UsersComponent implements OnInit {
  users: SuperAdminUserResponse[] = [];
  loading = false;
  savingUserId = '';
  errorMessage = '';
  successMessage = '';
  selectedRole: UserRole | 'ALL' = 'ALL';

  roles: (UserRole | 'ALL')[] = ['ALL', 'SUPER_ADMIN', 'OWNER', 'STAFF'];

  constructor(
    private apiService: ApiService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  getUserId(user: SuperAdminUserResponse): string {
    const item = user as any;
    return item.id || item.userId || item._id || '';
  }

  loadUsers(): void {
    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';
    this.cdr.detectChanges();

    const request =
      this.selectedRole === 'ALL'
        ? this.apiService.getSuperAdminUsers()
        : this.apiService.getSuperAdminUsersByRole(this.selectedRole);

    request.pipe(timeout(8000)).subscribe({
      next: (response) => {

        this.users = response || [];
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (error) => {

        this.loading = false;
        this.errorMessage =
          error.error?.message ||
          error.error?.error ||
          'Could not load users.';
        this.cdr.detectChanges();
      }
    });
  }

  changeRole(): void {
    this.loadUsers();
  }

  toggleStatus(user: SuperAdminUserResponse): void {
    const userId = this.getUserId(user);

    if (!userId) {
      this.errorMessage = 'User ID is missing.';
      this.cdr.detectChanges();
      return;
    }

    this.savingUserId = userId;
    this.errorMessage = '';
    this.successMessage = '';
    this.cdr.detectChanges();

    this.apiService.updateUserStatus(userId, !user.active).pipe(timeout(8000)).subscribe({
      next: () => {
        this.savingUserId = '';
        this.successMessage = 'User status updated successfully.';
        this.cdr.detectChanges();
        this.loadUsers();
      },
      error: (error) => {

        this.savingUserId = '';
        this.errorMessage =
          error.error?.message ||
          error.error?.error ||
          'Could not update user status.';
        this.cdr.detectChanges();
      }
    });
  }
}

