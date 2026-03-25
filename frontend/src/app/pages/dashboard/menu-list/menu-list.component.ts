import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Icons } from '../../../utils/icons';
import { MenuItem } from '../../../model/menu-item';
import { MenuService } from '../../../service/menu.service';
import { RouterLink, RouterOutlet } from '@angular/router';
import { UiService } from '../../../service/ui.service';
import { CategoryService } from '../../../service/category.service';

@Component({
  selector: 'app-menu-list',
  imports: [CommonModule, FontAwesomeModule, RouterLink],
  templateUrl: './menu-list.component.html',
  styleUrl: './menu-list.component.scss',
})
export class MenuListComponent {
  private readonly menuService = inject(MenuService);
  private readonly categoryService = inject(CategoryService);
  private uiService = inject(UiService);
  icons = Icons;
  defaultImage: string = 'images/dish.png';

  menuItems = this.menuService.menuItems;
  loading = this.menuService.loading;
  error = this.menuService.error;

  categories = this.categoryService.categories;

  ngOnInit() {
    this.menuService.loadMenuItems();
    this.categoryService.loadCategories();
  }

  onImageError(event: any): void {
    event.target.src = this.defaultImage;
  }

  getImageUrl(imageUrl: string): string {
    return imageUrl || this.defaultImage;
  }

  getCategoryName(categoryId: number | null): string {
    if (!categoryId) return 'Uncategorized';

    const categories = this.categories();
    if (!categories) return 'Loading...';

    const category = categories.find((c) => c.id === categoryId);
    return category ? category.name : 'Unknown';
  }

  toggleAvailability(item: MenuItem) {
    this.menuService.toggleAvailability(item.id).subscribe({
      next: () => this.uiService.showToast('Item status updated'),
      error: () => {
        this.uiService.showToast(
          'Failed to update item status. Please try again',
          'error',
        );
      },
    });
  }

  onDeleteItem(item: MenuItem) {
    this.uiService.ask({
      title: 'Remove Dish?',
      message: `Are you sure you want to remove "${item.name}" from your menu? This action cannot be undone.`,
      confirmText: 'Yes, Remove',
      action: () => {
        this.menuService.deleteItem(item.id).subscribe({
          next: () => this.uiService.showToast('Item deleted successfully'),
          error: () =>
            this.uiService.showToast(
              'Failed to delete item. Please try again later',
              'error',
            ),
        });
      },
    });
  }
}
