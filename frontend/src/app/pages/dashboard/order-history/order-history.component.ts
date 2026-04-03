import { CommonModule } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { OrderService } from '../../../service/order.service';
import { Icons } from '../../../utils/icons';

@Component({
  selector: 'app-order-history',
  standalone: true,
  imports: [CommonModule, FontAwesomeModule],
  templateUrl: './order-history.component.html',
})
export class OrderHistoryComponent {
  private orderService = inject(OrderService);

  icons = Icons;
  activeTab = signal<'fulfilled' | 'cancelled'>('fulfilled');

  loading = this.orderService.loading;
  deliveredOrders = this.orderService.deliveredKitchenOrders;
  cancelledOrders = this.orderService.cancelledKitchenOrders;

  displayOrders = computed(() =>
    this.activeTab() === 'fulfilled'
      ? this.deliveredOrders()
      : this.cancelledOrders(),
  );

  ngOnInit() {
    this.orderService.loadKitchenOrders();
  }

  setTab(tab: 'fulfilled' | 'cancelled') {
    this.activeTab.set(tab);
  }
}
