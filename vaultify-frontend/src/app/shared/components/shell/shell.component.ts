import { Component } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService, LoginResponse, UserRole } from '../../../core/services/auth.service';

interface NavItem {
  label: string;
  path: string;
  icon: string;
  roles: UserRole[];
}

@Component({
  selector: 'app-shell',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './shell.component.html',
  styleUrl: './shell.component.scss'
})
export class ShellComponent {
  user: LoginResponse | null;
  darkMode = document.body.classList.contains('dark');

  navItems: NavItem[] = [
    { label: 'Dashboard', path: '/super-admin/dashboard', icon: '', roles: ['SUPER_ADMIN'] },
    { label: 'Businesses', path: '/super-admin/businesses', icon: '', roles: ['SUPER_ADMIN'] },
    { label: 'Users', path: '/super-admin/users', icon: '', roles: ['SUPER_ADMIN'] },
    { label: 'Audit Logs', path: '/super-admin/audit-logs', icon: '', roles: ['SUPER_ADMIN'] },
    { label: 'Profile', path: '/super-admin/profile', icon: '', roles: ['SUPER_ADMIN'] },

    { label: 'Dashboard', path: '/owner/dashboard', icon: '', roles: ['OWNER'] },
    { label: 'Business Profile', path: '/owner/business-profile', icon: '', roles: ['OWNER'] },
    { label: 'Services', path: '/owner/services', icon: '', roles: ['OWNER'] },
    { label: 'Clients', path: '/owner/clients', icon: '', roles: ['OWNER'] },
    { label: 'Appointments', path: '/owner/appointments', icon: '', roles: ['OWNER'] },
    { label: 'Staff', path: '/owner/staff', icon: '', roles: ['OWNER'] },
    { label: 'Profile', path: '/owner/profile', icon: '', roles: ['OWNER'] },

    { label: 'Dashboard', path: '/staff/dashboard', icon: '', roles: ['STAFF'] },
    { label: 'Appointments', path: '/staff/appointments', icon: '', roles: ['STAFF'] },
    { label: 'Clients', path: '/staff/clients', icon: '', roles: ['STAFF'] },
    { label: 'Services', path: '/staff/services', icon: '', roles: ['STAFF'] }
  ];

  constructor(
    private authService: AuthService,
    private router: Router
  ) {
    this.user = this.authService.getUser();
  }

  get visibleNavItems(): NavItem[] {
    const role = this.authService.getRole();

    if (!role) {
      return [];
    }

    return this.navItems.filter((item) => item.roles.includes(role));
  }

  toggleTheme(): void {
    this.darkMode = !this.darkMode;
    document.body.classList.toggle('dark', this.darkMode);
  }

  logout(): void {
    this.authService.logout();
    this.router.navigateByUrl('/login');
  }
}

