import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';

/**
 * Guard kiểm tra đăng nhập và vai trò phù hợp
 */
export const roleGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const token = localStorage.getItem('token');
  const role = localStorage.getItem('role'); // Ex: ROLE_ADMIN, ROLE_KHACHHANG, ROLE_NHANVIEN

  if (!token) {
    // Chưa đăng nhập → chuyển về login
    return router.createUrlTree(['/login'], {
      queryParams: { returnUrl: state.url }
    });
  }

  const allowedRoles: string[] = route.data?.['roles'] || [];

  // Nếu không yêu cầu role cụ thể hoặc người dùng thuộc role hợp lệ
  if (allowedRoles.length === 0 || (role && allowedRoles.includes(role))) {
  return true;
}


  // Có token nhưng không đủ quyền → về trang không đủ quyền
  return router.createUrlTree(['/']);
};
