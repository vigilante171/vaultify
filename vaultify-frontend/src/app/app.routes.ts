import { Routes } from '@angular/router';
import { LoginComponent } from './features/auth/login/login.component';
import { roleGuard } from './core/guards/role.guard';
import { ShellComponent } from './shared/components/shell/shell.component';
import { DashboardComponent as SuperAdminDashboardComponent } from './features/super-admin/dashboard/dashboard.component';
import { BusinessesComponent } from './features/super-admin/businesses/businesses.component';
import { UsersComponent } from './features/super-admin/users/users.component';
import { AuditLogsComponent } from './features/super-admin/audit-logs/audit-logs.component';
import { ProfileComponent as SuperAdminProfileComponent } from './features/super-admin/profile/profile.component';
import { DashboardComponent as OwnerDashboardComponent } from './features/owner/dashboard/dashboard.component';
import { BusinessProfileComponent } from './features/owner/business-profile/business-profile.component';
import { ServicesComponent as OwnerServicesComponent } from './features/owner/services/services.component';
import { ClientsComponent as OwnerClientsComponent } from './features/owner/clients/clients.component';
import { AppointmentsComponent as OwnerAppointmentsComponent } from './features/owner/appointments/appointments.component';
import { StaffComponent as OwnerStaffComponent } from './features/owner/staff/staff.component';
import { ProfileComponent as OwnerProfileComponent } from './features/owner/profile/profile.component';
import { DashboardComponent as StaffDashboardComponent } from './features/staff/dashboard/dashboard.component';
import { AppointmentsComponent as StaffAppointmentsComponent } from './features/staff/appointments/appointments.component';
import { ClientsComponent as StaffClientsComponent } from './features/staff/clients/clients.component';
import { ServicesComponent as StaffServicesComponent } from './features/staff/services/services.component';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  {
    path: '',
    component: ShellComponent,
    children: [
      { path: 'super-admin/dashboard', component: SuperAdminDashboardComponent, canActivate: [roleGuard], data: { roles: ['SUPER_ADMIN'] } },
      { path: 'super-admin/businesses', component: BusinessesComponent, canActivate: [roleGuard], data: { roles: ['SUPER_ADMIN'] } },
      { path: 'super-admin/users', component: UsersComponent, canActivate: [roleGuard], data: { roles: ['SUPER_ADMIN'] } },
      { path: 'super-admin/audit-logs', component: AuditLogsComponent, canActivate: [roleGuard], data: { roles: ['SUPER_ADMIN'] } },
      { path: 'super-admin/profile', component: SuperAdminProfileComponent, canActivate: [roleGuard], data: { roles: ['SUPER_ADMIN'] } },

      { path: 'owner/dashboard', component: OwnerDashboardComponent, canActivate: [roleGuard], data: { roles: ['OWNER'] } },
      { path: 'owner/business-profile', component: BusinessProfileComponent, canActivate: [roleGuard], data: { roles: ['OWNER'] } },
      { path: 'owner/services', component: OwnerServicesComponent, canActivate: [roleGuard], data: { roles: ['OWNER'] } },
      { path: 'owner/clients', component: OwnerClientsComponent, canActivate: [roleGuard], data: { roles: ['OWNER'] } },
      { path: 'owner/appointments', component: OwnerAppointmentsComponent, canActivate: [roleGuard], data: { roles: ['OWNER'] } },
      { path: 'owner/staff', component: OwnerStaffComponent, canActivate: [roleGuard], data: { roles: ['OWNER'] } },
      { path: 'owner/profile', component: OwnerProfileComponent, canActivate: [roleGuard], data: { roles: ['OWNER'] } },

      { path: 'staff/dashboard', component: StaffDashboardComponent, canActivate: [roleGuard], data: { roles: ['STAFF'] } },
      { path: 'staff/appointments', component: StaffAppointmentsComponent, canActivate: [roleGuard], data: { roles: ['STAFF'] } },
      { path: 'staff/clients', component: StaffClientsComponent, canActivate: [roleGuard], data: { roles: ['STAFF'] } },
      { path: 'staff/services', component: StaffServicesComponent, canActivate: [roleGuard], data: { roles: ['STAFF'] } }
    ]
  },
  { path: '**', redirectTo: 'login' }
];

