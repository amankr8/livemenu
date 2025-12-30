import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { MenuItem } from '../../model/menu';
import { MenuService } from '../../service/menu.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-dashboard',
  imports: [CommonModule, FormsModule],
  templateUrl: './dashboard.component.html',
})
export class DashboardComponent {
  menuItems: MenuItem[] = [];
  filteredMenuItems: MenuItem[] = [];
  isLoading: boolean = true;
  errorMessage: string = '';

  // Modal state
  showModal: boolean = false;
  modalMode: 'add' | 'edit' = 'add';

  // Form data
  currentItem: MenuItem = this.getEmptyMenuItem();

  // Search and filter
  searchTerm: string = '';
  selectedCategory: string = 'all';
  categories: string[] = [
    'All',
    'Starters',
    'Main Course',
    'Desserts',
    'Beverages',
  ];

  // Delete confirmation
  showDeleteConfirm: boolean = false;
  itemToDelete: MenuItem | null = null;

  constructor(private menuService: MenuService) {}

  ngOnInit(): void {
    this.loadMenuItems();
  }

  // Load all menu items
  loadMenuItems(): void {
    this.isLoading = true;
    this.menuService.getMenuItems().subscribe({
      next: (data) => {
        this.menuItems = data;
        this.filteredMenuItems = data;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading menu items:', error);
        this.errorMessage = 'Failed to load menu items';
        this.isLoading = false;
      },
    });
  }

  // Filter menu items
  filterMenuItems(): void {
    this.filteredMenuItems = this.menuItems.filter((item) => {
      const matchesSearch =
        item.name.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        item.desc.toLowerCase().includes(this.searchTerm.toLowerCase());
      const matchesCategory =
        this.selectedCategory === 'all' ||
        item.category.toLowerCase() === this.selectedCategory.toLowerCase();
      return matchesSearch && matchesCategory;
    });
  }

  // Search handler
  onSearch(): void {
    this.filterMenuItems();
  }

  // Category filter handler
  onCategoryChange(): void {
    this.filterMenuItems();
  }

  // Open modal for adding
  openAddModal(): void {
    this.modalMode = 'add';
    this.currentItem = this.getEmptyMenuItem();
    this.showModal = true;
  }

  // Open modal for editing
  openEditModal(item: MenuItem): void {
    this.modalMode = 'edit';
    this.currentItem = { ...item }; // Create a copy
    this.showModal = true;
  }

  // Close modal
  closeModal(): void {
    this.showModal = false;
    this.currentItem = this.getEmptyMenuItem();
  }

  // Save menu item (add or edit)
  saveMenuItem(): void {
    if (!this.isValidMenuItem(this.currentItem)) {
      alert('Please fill all required fields');
      return;
    }

    if (this.modalMode === 'add') {
      this.menuService.addMenuItem(this.currentItem).subscribe({
        next: (newItem) => {
          this.menuItems.push(newItem);
          this.filterMenuItems();
          this.closeModal();
        },
        error: (error) => {
          console.error('Error creating menu item:', error);
          alert('Failed to create menu item');
        },
      });
    } else {
      if (this.currentItem.id) {
        this.menuService
          .updateMenuItem(this.currentItem.id, this.currentItem)
          .subscribe({
            next: (updatedItem) => {
              const index = this.menuItems.findIndex(
                (item) => item.id === updatedItem.id
              );
              if (index !== -1) {
                this.menuItems[index] = updatedItem;
                this.filterMenuItems();
              }
              this.closeModal();
            },
            error: (error) => {
              console.error('Error updating menu item:', error);
              alert('Failed to update menu item');
            },
          });
      }
    }
  }

  // Open delete confirmation
  openDeleteConfirm(item: MenuItem): void {
    this.itemToDelete = item;
    this.showDeleteConfirm = true;
  }

  // Close delete confirmation
  closeDeleteConfirm(): void {
    this.showDeleteConfirm = false;
    this.itemToDelete = null;
  }

  // Delete menu item
  deleteMenuItem(): void {
    if (this.itemToDelete && this.itemToDelete.id) {
      this.menuService.deleteMenuItem(this.itemToDelete.id).subscribe({
        next: () => {
          this.menuItems = this.menuItems.filter(
            (item) => item.id !== this.itemToDelete!.id
          );
          this.filterMenuItems();
          this.closeDeleteConfirm();
        },
        error: (error) => {
          console.error('Error deleting menu item:', error);
          alert('Failed to delete menu item');
        },
      });
    }
  }

  // Helper methods
  private getEmptyMenuItem(): MenuItem {
    return {
      id: 0,
      name: '',
      desc: '',
      price: 0,
      imageUrl: '',
      kitchenId: 0,
      inStock: true,
      category: 'Starters',
      isVeg: true,
    };
  }

  private isValidMenuItem(item: MenuItem): boolean {
    return !!(item.name && item.desc && item.price > 0 && item.category);
  }

  // Logout
  logout(): void {
    // TODO: Implement logout logic
    console.log('Logging out...');
  }
}
