import { Component, inject, signal } from '@angular/core';
import { CategoryService } from '../../../service/category.service';
import { Category } from '../../../model/category';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { CommonModule, Location } from '@angular/common';
import { Icons } from '../../../utils/icons';
import { FormsModule } from '@angular/forms';
import { UiService } from '../../../service/ui.service';

@Component({
  selector: 'app-categories',
  imports: [FontAwesomeModule, CommonModule, FormsModule],
  templateUrl: './categories.component.html',
})
export class CategoriesComponent {
  private uiService = inject(UiService);
  categoryService = inject(CategoryService);

  categoryName = signal('');
  isEditing = signal(false);
  editingId = signal<number | null>(null);
  isSubmitting = signal(false);
  showModal = signal(false); // New signal for modal visibility

  icons = Icons;

  ngOnInit() {
    this.categoryService.loadCategories();
  }

  getItemCount(categoryId: number): number {
    return 0;
    // return this.menuService.menuItems().filter(item => item.categoryId === categoryId).length;
  }

  openAddModal() {
    this.resetForm();
    this.showModal.set(true);
  }

  startEdit(cat: Category) {
    this.isEditing.set(true);
    this.editingId.set(cat.id);
    this.categoryName.set(cat.name);
    this.showModal.set(true);
  }

  onSaveCategory() {
    const name = this.categoryName().trim();
    if (!name) return;

    this.isSubmitting.set(true);
    const request = this.isEditing()
      ? this.categoryService.updateCategory(this.editingId()!, {
          categoryName: name,
        })
      : this.categoryService.addCategory({ categoryName: name });

    request.subscribe({
      next: () => {
        this.uiService.showToast(
          `Category ${this.isEditing() ? 'updated' : 'created'}!`,
        );
        this.closeModal();
      },
      error: () => this.isSubmitting.set(false),
    });
  }

  closeModal() {
    this.showModal.set(false);
    this.resetForm();
  }

  private resetForm() {
    this.categoryName.set('');
    this.isEditing.set(false);
    this.editingId.set(null);
    this.isSubmitting.set(false);
  }

  onDelete(cat: Category) {
    this.uiService.ask({
      title: 'Delete Category?',
      message: `Are you sure you want to delete "${cat.name}"? This action cannot be undone.`,
      confirmText: 'Yes, Delete',
      cancelText: 'Keep it',
      action: () => {
        this.categoryService.deleteCategory(cat.id).subscribe({
          next: () => {
            this.uiService.showToast('Category removed successfully');
            if (this.editingId() === cat.id) {
              this.resetForm();
            }
          },
          error: () => {
            this.uiService.showToast(
              'Could not delete category. It might be in use.',
              'error',
            );
          },
        });
      },
    });
  }
}
