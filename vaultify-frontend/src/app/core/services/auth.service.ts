import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';

export type UserRole = 'SUPER_ADMIN' | 'OWNER' | 'STAFF';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  tokenType: string;
  userId: string;
  businessId: string | null;
  fullName: string;
  email: string;
  role: UserRole;
  active: boolean;
  message: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly apiUrl = `${environment.apiUrl}/auth`;
  private readonly tokenKey = 'vaultify_token';
  private readonly roleKey = 'vaultify_role';
  private readonly userKey = 'vaultify_user';

  constructor(private http: HttpClient) {}

  login(payload: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, payload).pipe(
      tap((response) => {
        localStorage.setItem(this.tokenKey, response.token);
        localStorage.setItem(this.roleKey, response.role);
        localStorage.setItem(this.userKey, JSON.stringify(response));
      })
    );
  }

  logout(): void {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.roleKey);
    localStorage.removeItem(this.userKey);
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  getRole(): UserRole | null {
    return localStorage.getItem(this.roleKey) as UserRole | null;
  }

  getUser(): LoginResponse | null {
    const user = localStorage.getItem(this.userKey);
    return user ? JSON.parse(user) : null;
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  getRedirectPathByRole(role: UserRole): string {
    if (role === 'SUPER_ADMIN') {
      return '/super-admin/dashboard';
    }

    if (role === 'OWNER') {
      return '/owner/dashboard';
    }

    return '/staff/dashboard';
  }
}
