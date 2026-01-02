import { Component } from '@angular/core';
import { MenuComponent } from '../../components/menu/menu.component';
import { KitchenService } from '../../service/kitchen.service';

@Component({
  selector: 'app-homepage',
  imports: [MenuComponent],
  templateUrl: './homepage.component.html',
})
export class HomepageComponent {
  kitchenName: string = '';
  kitchenTagline: string = '';

  constructor(private kitchenService: KitchenService) {}

  ngOnInit(): void {
    this.getKitchenDetails();
  }

  getKitchenDetails() {
    this.kitchenService.getKitchen().subscribe((data: any) => {
      this.kitchenName = data.name;
      this.kitchenTagline = data.tagline;
      document.title = this.kitchenName + ' - Home';
    });
  }
}
