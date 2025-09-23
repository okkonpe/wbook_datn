import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';

/**
 * Guard kiểm tra đăng nhập và vai trò phù hợp
 */
export const roleGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const token = localStorage.getItem('token');
  const role = localStorage.getItem('role');

  // Các route public không cần token
  const publicUrls = ['/login', '/register', '/forgot-password', '/reset-password'];
  if (publicUrls.some(url => state.url.startsWith(url))) {
    return true;
  }

  if (!token) {
    return router.createUrlTree(['/login'], {
      queryParams: { returnUrl: state.url }
    });
  }

  const allowedRoles: string[] = route.data?.['roles'] || [];
  if (allowedRoles.length === 0 || (role && allowedRoles.includes(role))) {
    return true;
  }

  return router.createUrlTree(['/']);
};


