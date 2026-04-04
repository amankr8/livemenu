import {
  Component,
  computed,
  effect,
  ElementRef,
  HostListener,
  inject,
  signal,
  viewChild,
} from '@angular/core';
import { MenuComponent } from '../components/menu/menu.component';
import { CommonModule } from '@angular/common';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Icons } from '../../utils/icons';
import { CartService } from '../../service/cart.service';
import { KitchenService } from '../../service/kitchen.service';
import { AuthService } from '../../service/auth.service';
import { Router } from '@angular/router';
import { OtpLoginComponent } from '../components/otp-login/otp-login.component';
import { APP_NAME } from '../../constants/app.constant';
import { UiService } from '../../service/ui.service';
import { OrderService } from '../../service/order.service';
import { UserRole } from '../../enum/user-role.enum';
import { UserService } from '../../service/user.service';
import { MenuService } from '../../service/menu.service';
import { FoodPreference } from '../../model/menu-item';

@Component({
  selector: 'app-homepage',
  imports: [MenuComponent, CommonModule, FontAwesomeModule, OtpLoginComponent],
  templateUrl: './homepage.component.html',
  styleUrl: './homepage.component.scss',
})
export class HomepageComponent {
  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent) {
    const target = event.target as HTMLElement;
    if (!target.closest('#userMenuContainer')) {
      this.showUserMenu.set(false);
    }
  }

  private kitchenService = inject(KitchenService);
  private cartService = inject(CartService);
  private authService = inject(AuthService);
  private userService = inject(UserService);
  private orderService = inject(OrderService);
  private menuService = inject(MenuService);
  private uiService = inject(UiService);
  private router = inject(Router);

  cartButton = viewChild<ElementRef>('cartButton');

  kitchen = this.kitchenService.kitchen;
  username = this.authService.username;

  isCartEmpty = this.cartService.isEmpty;
  actualCartCount = this.cartService.totalCount;
  displayedCount = signal(this.actualCartCount());

  isLoggedIn = computed(() => this.authService.isUserLoggedIn());

  isBadgePulsing = signal(false);
  showLoginModal = signal(false);
  showUserMenu = signal(false);
  isOpeningCart = signal(false);

  searchTerm = this.menuService.searchTerm;
  showFilterMenu = signal(false);
  activeFilter = this.menuService.activeFilter;

  icons = Icons;

  defaultImage: string = 'images/dish.png';

  flyingItem = signal<{
    x: number;
    y: number;
    imageUrl: string;
  } | null>(null);

  flyX = signal(0);
  flyY = signal(0);
  flyStyle = signal<Record<string, string>>({});

  constructor() {
    effect(() => {
      const actual = this.actualCartCount();
      const displayed = this.displayedCount();

      if (actual < displayed) {
        this.displayedCount.set(actual);
      }
    });

    effect(() => {
      const animation = this.cartService.consumeAnimation();
      const cartButton = this.cartButton();

      if (!animation || !cartButton) return;

      this.startFlyAnimation(animation, cartButton);
    });
  }

  ngOnInit() {
    const kitchenName = this.kitchen()?.name ?? APP_NAME;
    document.title = kitchenName + ' - Home';
    if (this.authService.hasRole(UserRole.USER)) {
      this.userService.loadUser();
    }
  }

  onSearch(event: Event) {
    const value = (event.target as HTMLInputElement).value;
    this.menuService.setSearch(value);
  }

  toggleFilterMenu() {
    this.showFilterMenu.update((v) => !v);
  }

  toggleFilter(type: FoodPreference) {
    this.menuService.setFilter(type);
    this.showFilterMenu.set(false);
  }

  clearSearch() {
    this.menuService.setSearch('');
    const input = document.querySelector(
      'input[type="text"]',
    ) as HTMLInputElement;
    if (input) input.value = '';
  }

  toggleUserMenu() {
    this.showUserMenu.update((v) => !v);
  }

  logout() {
    const kitchenName = this.kitchen()?.name ?? APP_NAME;
    this.uiService.ask({
      title: 'Logout?',
      message: `Please confirm if you want to logout from ${kitchenName}?`,
      confirmText: 'Logout',
      action: () => {
        this.orderService.clearUserCache();
        this.authService.logout();
        this.showUserMenu.set(false);
        this.uiService.showToast('Logged out successfully');
      },
    });
  }

  navigateToOrders() {
    this.showUserMenu.set(false);
    this.router.navigate(['/my-orders']);
  }

  onImageError(event: any): void {
    event.target.src = this.defaultImage;
  }

  private startFlyAnimation(
    data: { x: number; y: number; imageUrl: string },
    cartButton: ElementRef,
  ) {
    this.flyingItem.set(data);
    this.flyX.set(data.x);
    this.flyY.set(data.y);
    this.flyStyle.set({ opacity: '1', transform: 'scale(1)' });

    const rect = cartButton.nativeElement.getBoundingClientRect();
    const targetX = rect.left + rect.width / 2 - 24;
    const targetY = rect.top + rect.height / 2 - 24;

    requestAnimationFrame(() => {
      this.flyX.set(targetX);
      this.flyY.set(targetY);
      this.flyStyle.set({
        opacity: '0',
        transform: 'scale(0.2) rotate(720deg)',
        transition: 'all 0.8s cubic-bezier(0.42, 0, 0.58, 1)',
      });
    });

    setTimeout(() => {
      this.flyingItem.set(null);
    }, 800);

    setTimeout(() => {
      this.pulseBadge();
    }, 750);
  }

  private pulseBadge() {
    this.displayedCount.set(this.actualCartCount());
    this.isBadgePulsing.set(true);

    setTimeout(() => {
      this.isBadgePulsing.set(false);
    }, 300);
  }

  onViewCart(): void {
    if (this.isCartEmpty()) {
      return;
    }

    this.isOpeningCart.set(true);
    if (this.isLoggedIn()) {
      this.router.navigate(['/cart']);
    } else {
      this.showLoginModal.set(true);
    }
  }

  onLoginSuccess(): void {
    this.showLoginModal.set(false);
    this.uiService.showToast('Logged in successfully!');
    if (this.isOpeningCart()) {
      this.router.navigate(['/cart']);
    }
  }
}
