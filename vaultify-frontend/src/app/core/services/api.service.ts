import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface SuperAdminDashboardResponse {
  totalBusinesses: number;
  activeBusinesses: number;
  inactiveBusinesses: number;
  trialBusinesses: number;
  activeSubscriptions: number;
  expiredSubscriptions: number;
  suspendedSubscriptions: number;
  totalOwners: number;
  totalStaff: number;
  message: string;
}

export type BusinessType = 'HAIR_SALON' | 'MEN_BARBER';
export type Plan = 'STARTER' | 'PRO' | 'PREMIUM';
export type SubscriptionStatus = 'TRIAL' | 'ACTIVE' | 'EXPIRED' | 'SUSPENDED';
export type UserRole = 'SUPER_ADMIN' | 'OWNER' | 'STAFF';
export type ServiceCategory = 'HAIR' | 'BEARD' | 'COLORING' | 'TREATMENT' | 'STYLING' | 'OTHER';
export type Gender = 'MALE' | 'FEMALE' | 'OTHER';
export type AppointmentStatus = 'PENDING' | 'CONFIRMED' | 'COMPLETED' | 'CANCELLED' | 'NO_SHOW';

export interface BusinessResponse {
  id: string;
  name: string;
  type: BusinessType;
  ownerName: string;
  phone: string;
  address: string;
  logoUrl: string;
  plan: Plan;
  subscriptionStatus: SubscriptionStatus;
  subscriptionStartDate: string;
  subscriptionEndDate: string;
  active: boolean;
  createdAt: string;
  updatedAt: string;
  message: string;
}

export interface CreateBusinessRequest {
  businessName: string;
  businessType: BusinessType;
  ownerName: string;
  ownerEmail: string;
  ownerPassword: string;
  phone: string;
  address: string;
  plan: Plan;
}

export interface UpdateBusinessSubscriptionRequest {
  plan: Plan;
  subscriptionStatus: SubscriptionStatus;
  subscriptionEndDate: string;
}

export interface UpdateBusinessStatusRequest {
  active: boolean;
}

export interface SuperAdminUserResponse {
  id: string;
  businessId: string | null;
  fullName: string;
  email: string;
  role: UserRole;
  active: boolean;
  createdAt: string;
  updatedAt: string;
  message: string;
}

export interface AuditLogResponse {
  id: string;
  businessId: string | null;
  actorUserId: string;
  actorEmail: string;
  actorRole: UserRole;
  action: string;
  targetType: string;
  targetId: string;
  description: string;
  createdAt: string;
  message: string;
}

export interface OwnerDashboardResponse {
  businessId: string;
  businessName: string;
  businessType: BusinessType;
  plan: Plan;
  subscriptionStatus: SubscriptionStatus;
  subscriptionEndDate: string;
  ownerName: string;
  ownerEmail: string;
  totalClients: number;
  totalServices: number;
  totalAppointments: number;
  monthlyRevenue: number;
  message: string;
}

export interface UpdateBusinessProfileRequest {
  businessName: string;
  phone: string;
  address: string;
  logoUrl: string;
}

export interface ServiceResponse {
  id: string;
  businessId: string;
  name: string;
  description: string;
  category: ServiceCategory;
  price: number;
  durationMinutes: number;
  active: boolean;
  createdAt: string;
  updatedAt: string;
  message: string;
}

export interface CreateServiceRequest {
  name: string;
  description: string;
  category: ServiceCategory;
  price: number;
  durationMinutes: number;
}

export interface ClientResponse {
  id: string;
  businessId: string;
  fullName: string;
  phone: string;
  email: string;
  gender: Gender;
  notes: string;
  totalVisits: number;
  totalSpent: number;
  lastVisitDate: string;
  active: boolean;
  createdAt: string;
  updatedAt: string;
  message: string;
}

export interface CreateClientRequest {
  fullName: string;
  phone: string;
  email: string;
  gender: Gender;
  notes: string;
}

export interface AppointmentResponse {
  id: string;
  businessId: string;
  clientId: string;
  clientName: string;
  serviceId: string;
  serviceName: string;
  appointmentDateTime: string;
  status: AppointmentStatus;
  price: number;
  durationMinutes: number;
  notes: string;
  active: boolean;
  createdAt: string;
  updatedAt: string;
  message: string;
}

export interface CreateAppointmentRequest {
  clientId: string;
  serviceId: string;
  appointmentDateTime: string;
  notes: string;
}

export interface UpdateAppointmentRequest {
  clientId: string;
  serviceId: string;
  appointmentDateTime: string;
  status: AppointmentStatus;
  notes: string;
}

export interface StaffResponse {
  id: string;
  businessId: string;
  fullName: string;
  email: string;
  role: UserRole;
  active: boolean;
  createdAt: string;
  updatedAt: string;
  message: string;
}

export interface CreateStaffRequest {
  fullName: string;
  email: string;
  password: string;
  phone: string;
}

export interface ProfileResponse {
  id: string;
  businessId: string | null;
  fullName: string;
  email: string;
  role: UserRole;
  active: boolean;
  createdAt: string;
  updatedAt: string;
  message: string;
}

export interface UpdateProfileRequest {
  fullName: string;
  email: string;
}

export interface ChangePasswordRequest {
  currentPassword: string;
  newPassword: string;
}

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private readonly baseUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  getSuperAdminDashboard(): Observable<SuperAdminDashboardResponse> {
    return this.http.get<SuperAdminDashboardResponse>(`${this.baseUrl}/super-admin/dashboard`);
  }

  getBusinesses(): Observable<BusinessResponse[]> {
    return this.http.get<BusinessResponse[]>(`${this.baseUrl}/super-admin/businesses`);
  }

  searchBusinesses(query: string): Observable<BusinessResponse[]> {
    return this.http.get<BusinessResponse[]>(`${this.baseUrl}/super-admin/businesses/search?query=${encodeURIComponent(query)}`);
  }

  createBusiness(payload: CreateBusinessRequest): Observable<BusinessResponse> {
    return this.http.post<BusinessResponse>(`${this.baseUrl}/super-admin/businesses`, payload);
  }
  updateBusinessSubscription(businessId: string, payload: UpdateBusinessSubscriptionRequest): Observable<BusinessResponse> {
    return this.http.put<BusinessResponse>(`${this.baseUrl}/super-admin/businesses/${businessId}/subscription`, payload);
  }

  updateBusinessStatus(businessId: string, payload: UpdateBusinessStatusRequest): Observable<BusinessResponse> {
    return this.http.put<BusinessResponse>(`${this.baseUrl}/super-admin/businesses/${businessId}/status`, payload);
  }

  getSuperAdminUsers(): Observable<SuperAdminUserResponse[]> {
    return this.http.get<SuperAdminUserResponse[]>(`${this.baseUrl}/super-admin/users`);
  }

  getSuperAdminUsersByRole(role: UserRole): Observable<SuperAdminUserResponse[]> {
    return this.http.get<SuperAdminUserResponse[]>(`${this.baseUrl}/super-admin/users/role/${role}`);
  }

  updateUserStatus(userId: string, active: boolean): Observable<SuperAdminUserResponse> {
    return this.http.put<SuperAdminUserResponse>(`${this.baseUrl}/super-admin/users/${userId}/status`, { active });
  }

  getAuditLogs(): Observable<AuditLogResponse[]> {
    return this.http.get<AuditLogResponse[]>(`${this.baseUrl}/super-admin/audit-logs`);
  }

  getOwnerDashboard(): Observable<OwnerDashboardResponse> {
    return this.http.get<OwnerDashboardResponse>(`${this.baseUrl}/owner/dashboard`);
  }

  getOwnerBusiness(): Observable<BusinessResponse> {
    return this.http.get<BusinessResponse>(`${this.baseUrl}/owner/my-business`);
  }

  updateOwnerBusiness(payload: UpdateBusinessProfileRequest): Observable<BusinessResponse> {
    return this.http.put<BusinessResponse>(`${this.baseUrl}/owner/my-business`, payload);
  }

  getOwnerServices(): Observable<ServiceResponse[]> {
    return this.http.get<ServiceResponse[]>(`${this.baseUrl}/owner/services`);
  }

  createOwnerService(payload: CreateServiceRequest): Observable<ServiceResponse> {
    return this.http.post<ServiceResponse>(`${this.baseUrl}/owner/services`, payload);
  }

  deleteOwnerService(serviceId: string): Observable<ServiceResponse> {
    return this.http.delete<ServiceResponse>(`${this.baseUrl}/owner/services/${serviceId}`);
  }

  getOwnerClients(): Observable<ClientResponse[]> {
    return this.http.get<ClientResponse[]>(`${this.baseUrl}/owner/clients`);
  }

  createOwnerClient(payload: CreateClientRequest): Observable<ClientResponse> {
    return this.http.post<ClientResponse>(`${this.baseUrl}/owner/clients`, payload);
  }

  deleteOwnerClient(clientId: string): Observable<ClientResponse> {
    return this.http.delete<ClientResponse>(`${this.baseUrl}/owner/clients/${clientId}`);
  }

  getOwnerAppointments(): Observable<AppointmentResponse[]> {
    return this.http.get<AppointmentResponse[]>(`${this.baseUrl}/owner/appointments`);
  }

  createOwnerAppointment(payload: CreateAppointmentRequest): Observable<AppointmentResponse> {
    return this.http.post<AppointmentResponse>(`${this.baseUrl}/owner/appointments`, payload);
  }

  updateOwnerAppointment(appointmentId: string, payload: UpdateAppointmentRequest): Observable<AppointmentResponse> {
    return this.http.put<AppointmentResponse>(`${this.baseUrl}/owner/appointments/${appointmentId}`, payload);
  }

  deleteOwnerAppointment(appointmentId: string): Observable<AppointmentResponse> {
    return this.http.delete<AppointmentResponse>(`${this.baseUrl}/owner/appointments/${appointmentId}`);
  }

  getOwnerStaff(): Observable<StaffResponse[]> {
    return this.http.get<StaffResponse[]>(`${this.baseUrl}/owner/staff`);
  }

  createOwnerStaff(payload: CreateStaffRequest): Observable<StaffResponse> {
    return this.http.post<StaffResponse>(`${this.baseUrl}/owner/staff`, payload);
  }

  deleteOwnerStaff(staffId: string): Observable<StaffResponse> {
    return this.http.delete<StaffResponse>(`${this.baseUrl}/owner/staff/${staffId}`);
  }

  getOwnerProfile(): Observable<ProfileResponse> {
    return this.http.get<ProfileResponse>(`${this.baseUrl}/owner/profile`);
  }

  updateOwnerProfile(payload: UpdateProfileRequest): Observable<ProfileResponse> {
    return this.http.put<ProfileResponse>(`${this.baseUrl}/owner/profile`, payload);
  }

  changeOwnerPassword(payload: ChangePasswordRequest): Observable<ProfileResponse> {
    return this.http.put<ProfileResponse>(`${this.baseUrl}/owner/profile/password`, payload);
  }

  getSuperAdminProfile(): Observable<ProfileResponse> {
    return this.http.get<ProfileResponse>(`${this.baseUrl}/super-admin/profile`);
  }

  updateSuperAdminProfile(payload: UpdateProfileRequest): Observable<ProfileResponse> {
    return this.http.put<ProfileResponse>(`${this.baseUrl}/super-admin/profile`, payload);
  }

  changeSuperAdminPassword(payload: ChangePasswordRequest): Observable<ProfileResponse> {
    return this.http.put<ProfileResponse>(`${this.baseUrl}/super-admin/profile/password`, payload);
  }
  getStaffAppointments(): Observable<AppointmentResponse[]> {
    return this.http.get<AppointmentResponse[]>(`${this.baseUrl}/staff/appointments`);
  }

  getStaffTodayAppointments(): Observable<AppointmentResponse[]> {
    return this.http.get<AppointmentResponse[]>(`${this.baseUrl}/staff/appointments/today`);
  }

  getStaffClients(): Observable<ClientResponse[]> {
    return this.http.get<ClientResponse[]>(`${this.baseUrl}/staff/clients`);
  }

  getStaffServices(): Observable<ServiceResponse[]> {
    return this.http.get<ServiceResponse[]>(`${this.baseUrl}/staff/services`);
  }
}












