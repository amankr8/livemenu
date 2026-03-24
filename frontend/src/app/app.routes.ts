import { Routes } from '@angular/router';
import { HomepageComponent } from './pages/homepage/homepage.component';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { LoginComponent } from './pages/login/login.component';
import { authGuard } from './guard/auth.guard';
import { KitchenComponent } from './pages/dashboard/kitchen/kitchen.component';
import { MenuListComponent } from './pages/dashboard/menu-list/menu-list.component';
import { AddMenuItemComponent } from './pages/dashboard/menu-list/add-menu-item/add-menu-item.component';
import { EditMenuItemComponent } from './pages/dashboard/menu-list/edit-menu-item/edit-menu-item.component';
import { PageNotFoundComponent } from './pages/page-not-found/page-not-found.component';
import { UserRole } from './enum/user-role.enum';
import { CartComponent } from './pages/homepage/cart/cart.component';
import { OrderSuccessComponent } from './pages/homepage/order-success/order-success.component';
import { DeliveryDetailsComponent } from './pages/homepage/cart/delivery-details/delivery-details.component';
import { LiveOrdersComponent } from './pages/dashboard/live-orders/live-orders.component';
import { CartItemsComponent } from './pages/homepage/cart/cart-items/cart-items.component';
import { MyOrdersComponent } from './pages/homepage/user/orders/my-orders.component';
import { CategoriesComponent } from './pages/dashboard/categories/categories.component';
import { isRootDomain } from './utils/domain.util';
import { BrandMarketingComponent } from './pages/brand-marketing/brand-marketing.component';

export const routes: Routes = [
  {
    path: '',
    canMatch: [() => isRootDomain()],
    children: [{ path: '', component: BrandMarketingComponent }],
  },
  { path: '', component: HomepageComponent },
  {
    path: 'my-orders',
    component: MyOrdersComponent,
    canActivate: [authGuard(UserRole.USER)],
  },
  {
    path: 'cart',
    component: CartComponent,
    canActivate: [authGuard(UserRole.USER)],
    children: [
      { path: '', pathMatch: 'full', component: CartItemsComponent },
      { path: 'delivery-details', component: DeliveryDetailsComponent },
    ],
  },
  {
    path: 'order-success/:id',
    component: OrderSuccessComponent,
  },
  { path: 'login', component: LoginComponent },
  {
    path: 'dashboard',
    component: DashboardComponent,
    canActivate: [authGuard(UserRole.KITCHEN_OWNER)],
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'live-orders' },
      { path: 'live-orders', component: LiveOrdersComponent },
      {
        path: 'menu',
        children: [
          { path: '', component: MenuListComponent },
          { path: 'add', component: AddMenuItemComponent },
          { path: 'edit/:id', component: EditMenuItemComponent },
        ],
      },
      { path: 'kitchen', component: KitchenComponent },
      { path: 'categories', component: CategoriesComponent },
    ],
  },
  { path: '**', component: PageNotFoundComponent },
];
