import { Component, inject, signal } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MenuService } from '../../../../service/menu.service';
import { CommonModule } from '@angular/common';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Icons } from '../../../../utils/icons';
import { Router, RouterLink } from '@angular/router';
import { UiService } from '../../../../service/ui.service';
import {
  ALLOWED_FILE_TYPES,
  MAX_FILE_SIZE,
} from '../../../../constants/app.constant';
import { CategoryService } from '../../../../service/category.service';

@Component({
  selector: 'app-add-menu-item',
  imports: [CommonModule, FontAwesomeModule, RouterLink, ReactiveFormsModule],
  templateUrl: './add-menu-item.component.html',
})
export class AddMenuItemComponent {
  private fb = inject(FormBuilder);
  private router = inject(Router);
  private menuService = inject(MenuService);
  private uiService = inject(UiService);
  categoryService = inject(CategoryService);

  imagePreview = signal<string | null>(null);
  selectedFile: File | null = null;

  icons = Icons;

  saving = signal(false);

  itemForm: FormGroup = this.fb.group({
    name: ['', Validators.required],
    desc: ['', Validators.required],
    category: ['', Validators.required],
    price: ['', [Validators.required, Validators.min(0)]],
    isVeg: [true],
  });

  ngOnInit() {
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

  onSubmit() {
    if (this.itemForm.valid) {
      this.saving.set(true);
      this.itemForm.disable();

      const formData = new FormData();
      Object.keys(this.itemForm.value).forEach((key) => {
        formData.append(key, this.itemForm.value[key]);
      });

      if (this.selectedFile) {
        formData.append('image', this.selectedFile);
      }

      this.menuService.addMenuItem(formData).subscribe({
        next: (res) => {
          this.uiService.showToast('Created menu item successfully!');
          this.router.navigate(['/dashboard/menu']);
        },
        error: () => {
          this.uiService.showToast('Failed to create item', 'error');
          this.itemForm.enable();
          this.saving.set(false);
        },
      });
    }
  }
}
