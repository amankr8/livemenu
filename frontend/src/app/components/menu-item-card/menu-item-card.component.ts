import { Component, Input } from '@angular/core';
import { MenuItem } from '../../model/menu';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-menu-item-card',
  imports: [CommonModule],
  templateUrl: './menu-item-card.component.html',
  styleUrl: './menu-item-card.component.scss',
})
export class MenuItemCardComponent {
  @Input() menuItem!: MenuItem;

  defaultImage: string = 'https://picsum.photos/400/300';

  onImageError(event: any): void {
    event.target.src = this.defaultImage;
  }

  getImageUrl(): string {
    return this.menuItem.imageUrl || this.defaultImage;
  }
}
