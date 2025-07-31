import { Routes } from '@angular/router';
import { HomeComponent } from './pages/home/home.component';
import { ContactComponent } from './pages/contact/contact.component';
import { AboutComponent } from './pages/about/about.component';
import { AdminComponent } from './features/admin/admin.component';
import { BookDetailComponent } from './features/books/components/book-detail/book-detail.component';
import { CartComponent } from './features/cart/cart.component';
import { LoginComponent } from './features/auth/login/login.component';
import { RegisterComponent } from './features/auth/register/register.component';
import { roleGuard } from './core/guards/auth.guard';
import { OrderComponent } from './features/order/order.component';
import { CustomerPurchaseComponent } from './features/customer/customer-purchase/customer-purchase.component';
import { CustomerInfoComponent } from './features/customer/customer-info/customer-info.component';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'contact', component: ContactComponent },
  { path: 'about', component: AboutComponent },
    { path: 'book/:id/:slug', component: BookDetailComponent },
  { path: 'cart',
     canActivate: [roleGuard],
  data: { roles: ['ROLE_KHACH_HANG'] },
    component: CartComponent },
     { path: 'customer-info',
     canActivate: [roleGuard],
  data: { roles: ['ROLE_KHACH_HANG'] },
    component: CustomerInfoComponent },
     { path: 'order',
     canActivate: [roleGuard],
  data: { roles: ['ROLE_KHACH_HANG'] },
    component: OrderComponent },
     { path: 'activityPurchase',
     canActivate: [roleGuard],
  data: { roles: ['ROLE_KHACH_HANG'] },
    component: CustomerPurchaseComponent },
    { path: 'login', component: LoginComponent },
        { path: 'register', component: RegisterComponent },


  {
    path: 'admin',
    canActivate: [roleGuard],
  data: { roles: ['ROLE_ADMIN'] },
    component: AdminComponent,
    children: [
      {
        path: 'dashboard',
        loadComponent: () => import('./features/admin/dashboard/dashboard.component').then(m => m.DashboardComponent)
      },
      {
        path: 'customer',
        loadComponent: () => import('./features/admin/users/users.component').then(m => m.UsersComponent)
      },
      {
        path: 'employee',
        loadComponent: () => import('./features/admin/employees/employees.component').then(m => m.EmployeesComponent)
      },
       {
        path: 'list-order',
        loadComponent: () => import('./features/admin/list-order/list-order.component').then(m => m.ListOrderComponent)
      },
       {
        path: 'products/author',
        loadComponent: () => import('./features/admin/authors/authors.component').then(m => m.AuthorsComponent)
      },
      {
        path: 'products/category',
        loadComponent: () => import('./features/admin/category/category.component').then(m => m.CategoryComponent)
      },
       {
        path: 'products/topic',
        loadComponent: () => import('./features/admin/topic/topic.component').then(m => m.TopicComponent)
      },
       {
        path: 'products/paper',
        loadComponent: () => import('./features/admin/paper/paper.component').then(m => m.PaperComponent)
      },
       {
        path: 'products/cover',
        loadComponent: () => import('./features/admin/cover/cover.component').then(m => m.CoverComponent)
      },
       {
        path: 'products/size',
        loadComponent: () => import('./features/admin/size/size.component').then(m => m.SizeComponent)
      },
       {
        path: 'products/publisher',
        loadComponent: () => import('./features/admin/publisher/publisher.component').then(m => m.PublisherComponent)
      },
      {
        path: 'products/product-book',
        loadComponent: () => import('./features/admin/product-book/product-book.component').then(m => m.ProductBookComponent)
      },
      {
        path: 'products/book',
        loadComponent: () => import('./features/admin/book-management/book-management.component').then(m => m.BookManagementComponent)
      },
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' }
    ]
  }
];
