import { HttpClient } from '@angular/common/http';
import { computed, effect, inject, Injectable, signal } from '@angular/core';
import { environment } from '../../environments/environment';
import { Order, OrderPayload } from '../model/order';
import { catchError, Observable, tap, throwError } from 'rxjs';
import { OrderStatus } from '../enum/order-status.enum';

import { Client } from '@stomp/stompjs';
import { KitchenService } from './kitchen.service';
import { UiService } from './ui.service';
import { AuthService } from './auth.service';
import { UserRole } from '../enum/user-role.enum';
import { UserService } from './user.service';

@Injectable({
  providedIn: 'root',
})
export class OrderService {
  private stompClient: Client;
  private http = inject(HttpClient);
  private authService = inject(AuthService);
  private userService = inject(UserService);
  private kitchenService = inject(KitchenService);
  private uiService = inject(UiService);

  private apiUrl = environment.apiBaseUrl + '/api/v1/orders';

  // 🔹 State signals
  private readonly _kitchenOrders = signal<Order[] | null>(null);
  readonly kitchenOrders = this._kitchenOrders.asReadonly();

  private readonly _userOrders = signal<Order[] | null>(null);
  readonly userOrders = this._userOrders.asReadonly();

  private readonly _loading = signal(false);
  readonly loading = this._loading.asReadonly();

  private readonly _error = signal<string | null>(null);
  readonly error = this._error.asReadonly();

  private readonly _wsConnected = signal(false);
  readonly wsConnected = this._wsConnected.asReadonly();

  sortByNewest = (a: Order, b: Order) =>
    new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime();

  readonly pendingKitchenOrders = computed(
    () =>
      this._kitchenOrders()
        ?.filter((o) => o.status === OrderStatus.PENDING)
        .sort(this.sortByNewest) ?? [],
  );

  readonly preparingKitchenOrders = computed(
    () =>
      this._kitchenOrders()
        ?.filter((o) => o.status === OrderStatus.PREPARING)
        .sort(this.sortByNewest) ?? [],
  );

  readonly dispatchedKitchenOrders = computed(
    () =>
      this._kitchenOrders()
        ?.filter((o) => o.status === OrderStatus.DISPATCHED)
        .sort(this.sortByNewest) ?? [],
  );

  readonly deliveredKitchenOrders = computed(
    () =>
      this._kitchenOrders()
        ?.filter((o) => o.status === OrderStatus.DELIVERED)
        .sort(this.sortByNewest) ?? [],
  );

  readonly cancelledKitchenOrders = computed(
    () =>
      this._kitchenOrders()
        ?.filter((o) => o.status === OrderStatus.CANCELLED)
        .sort(this.sortByNewest) ?? [],
  );

  private notificationSound = new Audio('audio/notification.mp3');
  private activeSubscriptions: Map<string, any> = new Map();

  constructor() {
    this.stompClient = new Client({
      brokerURL: environment.apiBaseUrl + '/ws-orders',
      connectHeaders: {
        Authorization: `Bearer ${this.authService.token()}`,
      },
      heartbeatIncoming: 10000,
      heartbeatOutgoing: 10000,
      reconnectDelay: 5000,
      debug: (str) => console.log(str),
    });

    this.stompClient.onConnect = (frame) => {
      console.log('✅ Connected to WS');
      this.activeSubscriptions.clear();
      this._wsConnected.set(true);
    };

    this.stompClient.onDisconnect = () => {
      this._wsConnected.set(false);
    };

    this.stompClient.activate();

    effect(() => {
      const user = this.userService.user();
      const kitchen = this.kitchenService.kitchen();
      const isConnected = this.wsConnected();

      if (!isConnected) return;

      if (this.authService.hasRole(UserRole.KITCHEN_OWNER) && kitchen?.id) {
        this.subscribeToTopic(`/topic/kitchen/${kitchen.id}`, (msg) => {
          const newOrder = JSON.parse(msg.body);
          this.playKitchenAlert(newOrder);
          this.handleNewKitchenOrder(newOrder);
        });
      }

      if (this.authService.hasRole(UserRole.USER) && user?.id) {
        this.subscribeToTopic(`/topic/user/${user.id}`, (msg) => {
          const updatedOrder = JSON.parse(msg.body);
          this.uiService.showToast(
            `Order #${updatedOrder.id} is now ${updatedOrder.status}!`,
          );
          this.handleUserOrderUpdate(updatedOrder);
        });
      }
    });
  }

  private subscribeToTopic(topic: string, callback: (msg: any) => void) {
    if (this.activeSubscriptions.has(topic)) return;

    const subscription = this.stompClient.subscribe(topic, callback);
    this.activeSubscriptions.set(topic, subscription);
    console.log(`✅ Subscription established for: ${topic}`);
  }

  private playKitchenAlert(order: Order) {
    this.notificationSound.currentTime = 0;
    this.notificationSound
      .play()
      .catch((e) => console.warn('Audio blocked', e));
    this.uiService.showToast(`Incoming Order! Ticket #${order.id}`, 'info');
  }

  // --------------------
  // Load orders
  // --------------------
  loadKitchenOrders(): void {
    if (this._kitchenOrders() !== null || this._loading()) return;
    this.fetchKitchenOrders();
  }

  refreshKitchenOrders(): void {
    this.fetchKitchenOrders();
  }

  private fetchKitchenOrders(): void {
    if (this._loading()) return;

    this._loading.set(true);
    this._error.set(null);

    this.http.get<Order[]>(this.apiUrl).subscribe({
      next: (items) => {
        this._kitchenOrders.set(items);
        this._loading.set(false);
      },
      error: () => {
        this._error.set('Failed to load orders');
        this._loading.set(false);
      },
    });
  }

  loadUserOrders(): void {
    if (this._userOrders() !== null || this._loading()) return;
    this.fetchUserOrders();
  }

  refreshUserOrders(): void {
    this.fetchUserOrders();
  }

  private fetchUserOrders(): void {
    if (this._loading()) return;

    this._loading.set(true);
    this._error.set(null);

    this.http.get<Order[]>(`${this.apiUrl}/user`).subscribe({
      next: (items) => {
        this._userOrders.set(items);
        this._loading.set(false);
      },
      error: () => {
        this._error.set('Failed to load orders');
        this._loading.set(false);
      },
    });
  }

  // --------------------
  // Mutations
  // --------------------
  private handleNewKitchenOrder(order: Order): void {
    if (!this._kitchenOrders()) {
      this.refreshKitchenOrders();
    } else {
      this._kitchenOrders.update((orders) => [...orders!, order]);
    }
  }

  private handleUserOrderUpdate(updatedOrder: Order) {
    if (!this._userOrders()) {
      this.refreshUserOrders();
    } else {
      this._userOrders.update((orders) => {
        return orders!.map((o) =>
          o.id === updatedOrder.id ? updatedOrder : o,
        );
      });
    }
  }

  placeOrder(orderPayload: OrderPayload): Observable<Order> {
    return this.http.post<Order>(this.apiUrl, orderPayload).pipe(
      tap((order) => {
        if (this._userOrders() === null) {
          this.refreshUserOrders();
        } else {
          this._userOrders.update((orders) => [...orders!, order]);
        }
      }),
    );
  }

  updateOrderStatus(orderId: number, newStatus: string): Observable<Order> {
    const existingOrders = this._kitchenOrders();
    this._kitchenOrders.update((orders) =>
      orders!.map((o) => (o.id === orderId ? { ...o, status: newStatus } : o)),
    );

    return this.http
      .patch<Order>(`${this.apiUrl}/${orderId}/update`, null, {
        params: { newStatus },
      })
      .pipe(
        catchError((err) => {
          this._kitchenOrders.set(existingOrders);
          return throwError(() => err);
        }),
      );
  }

  discardOrder(orderId: number): Observable<void> {
    const existingOrders = this._kitchenOrders();
    this._kitchenOrders.update((orders) =>
      orders!.map((o) =>
        o.id === orderId ? { ...o, status: OrderStatus.CANCELLED } : o,
      ),
    );

    return this.http.delete<void>(`${this.apiUrl}/${orderId}`).pipe(
      catchError((err) => {
        this._kitchenOrders.set(existingOrders);
        return throwError(() => err);
      }),
    );
  }

  // --------------------
  // Utilities
  // --------------------
  clearUserCache(): void {
    this._userOrders.set(null);
  }
}
