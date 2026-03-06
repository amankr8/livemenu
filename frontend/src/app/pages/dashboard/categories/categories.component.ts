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
  location = inject(Location);

  categoryName = signal('');
  isEditing = signal(false);
  editingId = signal<number | null>(null);
  isSubmitting = signal(false);

  icons = Icons;

  ngOnInit() {
    this.categoryService.loadCategories();
  }

  onSaveCategory() {
    const name = this.categoryName().trim();
    if (!name) return;

    this.isSubmitting.set(true);

    if (this.isEditing() && this.editingId()) {
      this.categoryService
        .updateCategory(this.editingId()!, { categoryName: name })
        .subscribe({
          next: () => this.resetForm(),
          error: () => this.isSubmitting.set(false),
        });
    } else {
      this.categoryService.addCategory({ categoryName: name }).subscribe({
        next: () => this.resetForm(),
        error: () => this.isSubmitting.set(false),
      });
    }
  }

  startEdit(cat: Category) {
    this.isEditing.set(true);
    this.editingId.set(cat.id);
    this.categoryName.set(cat.name);

    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  cancelEdit() {
    this.resetForm();
  }

  onDelete(id: number) {
    this.uiService.ask({
      title: 'Delete Category?',
      message: `Are you sure you want to delete this category? This action cannot be undone.`,
      confirmText: 'Yes, Delete',
      cancelText: 'Keep it',
      action: () => {
        this.categoryService.deleteCategory(id).subscribe({
          next: () => {
            this.uiService.showToast('Category removed successfully');
            if (this.editingId() === id) {
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

  private resetForm() {
    this.categoryName.set('');
    this.isEditing.set(false);
    this.editingId.set(null);
    this.isSubmitting.set(false);
  }
}
