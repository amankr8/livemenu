import { Component, computed, inject } from '@angular/core';
import { MenuItem } from '../../../model/menu-item';
import { MenuService } from '../../../service/menu.service';
import { CommonModule } from '@angular/common';
import { MenuItemCardComponent } from '../menu-item-card/menu-item-card.component';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Icons } from '../../../utils/icons';

@Component({
  selector: 'app-menu',
  imports: [CommonModule, MenuItemCardComponent, FontAwesomeModule],
  templateUrl: './menu.component.html',
})
export class MenuComponent {
  icons = Icons;

  private menuService = inject(MenuService);
  menuItems = this.menuService.menuItems;
  loading = this.menuService.loading;
  error = this.menuService.error;

  ngOnInit(): void {
    this.menuService.loadMenuItems();
  }

  trackById = (_: number, item: MenuItem) => item.id;

  groupedMenuItems = computed(() => {
    const items = this.menuItems() || [];
    const groups = items.reduce((acc: any, item) => {
      const cat = item.categoryId || 'Other';
      if (!acc[cat]) acc[cat] = [];
      acc[cat].push(item);
      return acc;
    }, {});

    return Object.keys(groups).map((category) => ({
      label: category,
      items: groups[category],
    }));
  });
}
