import { Component, effect, inject, input, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { MenuService } from '../../../../service/menu.service';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { UiService } from '../../../../service/ui.service';
import { Icons } from '../../../../utils/icons';
import { CommonModule } from '@angular/common';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import {
  ALLOWED_FILE_TYPES,
  MAX_FILE_SIZE,
} from '../../../../constants/app.constant';
import { CategoryService } from '../../../../service/category.service';

@Component({
  selector: 'app-edit-menu-item',
  imports: [CommonModule, FontAwesomeModule, ReactiveFormsModule, RouterLink],
  templateUrl: './edit-menu-item.component.html',
})
export class EditMenuItemComponent {
  id = input.required<string>();
  private route = inject(ActivatedRoute);
  private menuService = inject(MenuService);
  private fb = inject(FormBuilder);
  private uiService = inject(UiService);
  private router = inject(Router);
  categoryService = inject(CategoryService);

  imagePreview = signal<string | null>(null);
  selectedFile: File | null = null;

  menuItems = this.menuService.menuItems;
  saving = signal(false);

  icons = Icons;

  itemForm: FormGroup = this.fb.group({
    name: ['', Validators.required],
    price: [null, [Validators.required, Validators.min(0)]],
    categoryId: [null],
    desc: [''],
    isVeg: [true],
    imageUrl: [''],
  });

  constructor() {
    effect(() => {
      const itemId = Number(this.id());
      const menuItems = this.menuItems();

      if (!itemId || !menuItems) return;

      const item = menuItems?.find((i) => i.id === itemId);

      if (item) {
        this.itemForm.patchValue(item);
        if (item.imageUrl) {
          this.imagePreview.set(item.imageUrl);
        }
        this.itemForm.markAsPristine();
      } else {
        this.uiService.showToast('Item not found', 'error');
        this.router.navigate(['/dashboard/menu']);
      }
    });
  }

  ngOnInit() {
    this.menuService.loadMenuItems();
    this.categoryService.loadCategories();
  }

  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file && this.isImageValid(file)) {
      this.selectedFile = file;
      this.itemForm.markAsDirty();

      const reader = new FileReader();
      reader.onload = () => this.imagePreview.set(reader.result as string);
      reader.readAsDataURL(file);
    }
  }

  isImageValid(file: File): boolean {
    if (file.size > MAX_FILE_SIZE) {
      this.uiService.showToast('File size is greater than 5MB', 'error');
      return false;
    }
    if (!ALLOWED_FILE_TYPES.includes(file.type)) {
      this.uiService.showToast('Only JPG and PNG files are allowed', 'error');
      return false;
    }
    return true;
  }

  toggleVeg() {
    const current = this.itemForm.get('isVeg')?.value;
    this.itemForm.patchValue({ isVeg: !current });
    this.itemForm.markAsDirty();
  }

  removeImage(event: Event) {
    event.stopPropagation();
    if (this.saving()) return;

    this.imagePreview.set(null);
    this.selectedFile = null;
    this.itemForm.markAsDirty();
  }

  onUpdate() {
    if (this.itemForm.valid && this.itemForm.dirty && !this.saving()) {
      this.saving.set(true);
      this.itemForm.disable();

      const formData = new FormData();
      Object.keys(this.itemForm.controls).forEach((key) => {
        const value = this.itemForm.get(key)?.value;
        if (value !== null && key !== 'imageUrl') {
          formData.append(key, value);
        }
      });

      if (this.selectedFile) {
        formData.append('image', this.selectedFile);
      }

      const itemId = Number(this.id());
      this.menuService.updateMenuItem(itemId, formData).subscribe({
        next: () => {
          this.uiService.showToast('Dish updated successfully!');
          this.router.navigate(['/dashboard/menu']);
        },
        error: () => {
          this.uiService.showToast('Update failed', 'error');
          this.itemForm.enable();
          this.saving.set(false);
        },
      });
    }
  }
}
