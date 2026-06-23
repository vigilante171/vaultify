import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  email = 'admin@vaultify.com';
  password = 'Admin@12345';
  loading = false;
  errorMessage = '';

  darkMode = document.body.classList.contains('dark');

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  toggleTheme(): void {
    this.darkMode = !this.darkMode;
    document.body.classList.toggle('dark', this.darkMode);
  }

  login(): void {
    this.errorMessage = '';

    if (!this.email || !this.password) {
      this.errorMessage = 'Please enter your email and password.';
      return;
    }

    this.loading = true;

    this.authService.login({
      email: this.email,
      password: this.password
    }).subscribe({
      next: (response) => {
        this.loading = false;
        const redirectPath = this.authService.getRedirectPathByRole(response.role);
        this.router.navigateByUrl(redirectPath);
      },
      error: (error) => {
        this.loading = false;

        if (error.status === 0) {
          this.errorMessage = 'Backend is not reachable. Make sure Spring Boot is running on port 8080.';
          return;
        }

        this.errorMessage =
          error.error?.message ||
          error.error?.error ||
          'Invalid email or password.';
      }
    });
  }
}

