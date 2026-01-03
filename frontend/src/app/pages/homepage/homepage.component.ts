import { Component } from '@angular/core';
import { MenuComponent } from '../../components/menu/menu.component';
import { KitchenService } from '../../service/kitchen.service';
import { Kitchen } from '../../model/kitchen';
import { TenantService } from '../../service/tenant.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-homepage',
  imports: [MenuComponent, CommonModule],
  templateUrl: './homepage.component.html',
})
export class HomepageComponent {
  loading: boolean = true;
  kitchenName: string = '';
  kitchenTagline: string = '';

  constructor(
    private kitchenService: KitchenService,
    public tenantService: TenantService
  ) {}

  ngOnInit(): void {
    this.getKitchenDetails();
  }

  getKitchenDetails() {
    this.kitchenService.getKitchen().subscribe({
      next: (data: Kitchen) => {
        this.tenantService.isKitchenValid = true;
        document.title = data.name + ' - Home';
        this.kitchenName = data.name;
        this.kitchenTagline = data.tagline;
        this.loading = false;
      },
      error: (error) => {
        this.tenantService.isKitchenValid = false;
        document.title = 'Kitchen Not Found';
        console.error('Error fetching kitchen details:', error);
        this.loading = false;
      },
    });
  }
}
