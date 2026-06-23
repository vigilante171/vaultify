import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService, UserRole } from '../services/auth.service';

export const roleGuard: CanActivateFn = (route) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const expectedRoles = route.data?.['roles'] as UserRole[] | undefined;
  const token = authService.getToken();
  const role = authService.getRole();

  if (!token || !role) {
    router.navigateByUrl('/login');
    return false;
  }

  if (expectedRoles && !expectedRoles.includes(role)) {
    router.navigateByUrl(authService.getRedirectPathByRole(role));
    return false;
  }

  return true;
};

