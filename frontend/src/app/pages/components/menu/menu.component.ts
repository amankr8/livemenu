import { Component, computed, inject } from '@angular/core';
import { MenuItem } from '../../../model/menu-item';
import { MenuService } from '../../../service/menu.service';
import { CommonModule } from '@angular/common';
import { MenuItemCardComponent } from '../menu-item-card/menu-item-card.component';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Icons } from '../../../utils/icons';
import { CategoryService } from '../../../service/category.service';

@Component({
  selector: 'app-menu',
  imports: [CommonModule, MenuItemCardComponent, FontAwesomeModule],
  templateUrl: './menu.component.html',
})
export class MenuComponent {
  icons = Icons;

  private menuService = inject(MenuService);
  private categoryService = inject(CategoryService);

  menuItems = this.menuService.menuItems;
  loading = this.menuService.loading;
  error = this.menuService.error;

  searchTerm = this.menuService.searchTerm;

  categories = this.categoryService.categories;

  ngOnInit(): void {
    this.menuService.loadMenuItems();
    this.categoryService.loadCategories();
  }

  trackById = (_: number, item: MenuItem) => item.id;

  // TODO: Optimize grouping logic by pre-processing data in service or using a memoized selector
  groupedMenuItems = computed(() => {
    const items = this.menuItems() || [];
    const categories = this.categories() || [];
    const search = this.searchTerm().toLowerCase();

    const filteredItems = items.filter(
      (item) =>
        item.name.toLowerCase().includes(search) ||
        item.desc?.toLowerCase().includes(search),
    );

    const groups = filteredItems.reduce(
      (acc: { [key: string]: MenuItem[] }, item) => {
        const catKey = item.categoryId
          ? item.categoryId.toString()
          : 'uncategorized';
        if (!acc[catKey]) acc[catKey] = [];
        acc[catKey].push(item);
        return acc;
      },
      {},
    );

    return Object.keys(groups).map((key) => ({
      label:
        categories.find((c) => c.id === Number(key))?.name || 'Other Items',
      items: groups[key],
    }));
  });
}
