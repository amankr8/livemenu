import { Component } from '@angular/core';
import { MenuItem } from '../../model/menu';
import { MenuService } from '../../service/menu.service';
import { CommonModule } from '@angular/common';
import { MenuItemCardComponent } from '../menu-item-card/menu-item-card.component';
import { TenantService } from '../../service/tenant.service';

@Component({
  selector: 'app-menu',
  imports: [CommonModule, MenuItemCardComponent],
  templateUrl: './menu.component.html',
})
export class MenuComponent {
  menuItems: MenuItem[] = [];
  isLoading: boolean = true;
  skeletons = Array(6);
  errorMessage: string = '';

  constructor(
    private menuService: MenuService,
    private tenantService: TenantService
  ) {}

  ngOnInit(): void {
    this.fetchMenuItems();
  }

  trackById(index: number, item: MenuItem) {
    return item.id;
  }

  fetchMenuItems(): void {
    this.menuService.getMenuItems().subscribe({
      next: (data) => {
        this.tenantService.isKitchenValid = true;
        this.menuItems = data;
        this.isLoading = false;
      },
      error: (error) => {
        this.tenantService.isKitchenValid = false;
        console.error('Error fetching menu items:', error);
        this.errorMessage =
          'Failed to load menu items. Please try again later.';
        this.isLoading = false;
      },
    });
  }
}
