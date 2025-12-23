import { Component } from '@angular/core';
import { MenuItem, MOCK_MENU } from '../../model/menu';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-menu',
  imports: [CommonModule],
  templateUrl: './menu.component.html',
  styleUrl: './menu.component.scss',
})
export class MenuComponent {
  menu = MOCK_MENU;
  categories = ['Starters', 'Main Course', 'Breads'];

  // Logic to filter items based on the category for the UI
  getItemsByCategory(cat: string) {
    return this.menu.filter((item) => item.category === cat);
  }
}
