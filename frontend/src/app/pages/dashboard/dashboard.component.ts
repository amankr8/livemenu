import { Component, inject, signal } from '@angular/core';
import { CommonModule, TitleCasePipe } from '@angular/common';
import {
  RouterOutlet,
  Router,
  RouterLink,
  RouterLinkActive,
} from '@angular/router';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome'; //
import { Icons } from '../../utils/icons'; //
import { AuthService } from '../../service/auth.service';
import { KitchenService } from '../../service/kitchen.service';
import { UiService } from '../../service/ui.service';
import { APP_NAME } from '../../constants/app.constant';
import { IconDefinition } from '@fortawesome/free-solid-svg-icons';

interface NavLink {
  path: string;
  label: string;
  icon: IconDefinition;
  showPulse?: boolean;
}

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    RouterOutlet,
    RouterLink,
    RouterLinkActive,
    FontAwesomeModule,
  ],
  templateUrl: './dashboard.component.html',
})
export class DashboardComponent {
  private kitchenService = inject(KitchenService);
  private authService = inject(AuthService);
  private router = inject(Router);
  private uiService = inject(UiService);

  kitchen = this.kitchenService.kitchen;

  icons = Icons;

  isSidebarCollapsed = signal(true);

  navLinks: NavLink[] = [
    {
      path: '/dashboard/live-orders',
      label: 'Live Orders',
      icon: this.icons.location,
      showPulse: true,
    },
    {
      path: '/dashboard/history',
      label: 'Order History',
      icon: this.icons.history,
    },
    { path: '/dashboard/menu', label: 'Menu List', icon: this.icons.bowl },
    {
      path: '/dashboard/categories',
      label: 'Categories',
      icon: this.icons.tag,
    },
    {
      path: '/dashboard/kitchen',
      label: 'My Kitchen',
      icon: this.icons.kitchen,
    },
  ];

  ngOnInit() {
    const kitchenName = this.kitchen()?.name ?? APP_NAME;
    document.title = kitchenName + ' - Dashboard';
  }

  toggleSidebar() {
    this.isSidebarCollapsed.update((val) => !val);
  }

  closeOnMobile() {
    if (window.innerWidth < 768) {
      this.isSidebarCollapsed.set(true);
    }
  }

  onLogout() {
    this.uiService.ask({
      title: 'Logout?',
      message:
        'Are you sure you want to end your session? You will need to login again to manage your menu.',
      confirmText: 'Logout',
      cancelText: 'Stay Logged In',
      action: () => {
        this.authService.logout();
        this.uiService.showToast('Logged out successfully!');
      },
    });
  }

  getPageRouteName(): string {
    const url = this.router.url;
    if (url.includes('/history')) return 'Order History';
    if (url.includes('/live-orders')) return 'Live Orders';
    if (url.includes('/categories')) return 'Menu Categories';
    if (url.includes('/menu/add')) return 'Add New Item';
    if (url.includes('/menu/edit')) return 'Edit Menu Item';
    if (url.includes('/menu')) return 'Menu Items';
    if (url.includes('/kitchen')) return 'Kitchen Profile';
    return 'Welcome';
  }

  getBreadcrumbs(): string[] {
    const url = this.router.url;
    const crumbs: string[] = [];

    if (url.includes('/history')) {
      crumbs.push('Order History');
    } else if (url.includes('/live-orders')) {
      crumbs.push('Live Orders');
    } else if (url.includes('/menu')) {
      crumbs.push('Menu Items');
      if (url.includes('/add')) {
        crumbs.push('Add New Item');
      } else if (url.includes('/edit')) {
        crumbs.push('Edit Item');
      }
    } else if (url.includes('/kitchen')) {
      crumbs.push('Kitchen Profile');
    }

    return crumbs;
  }
}
