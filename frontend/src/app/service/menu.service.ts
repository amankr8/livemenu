import { HttpClient } from '@angular/common/http';
import { Injectable, signal } from '@angular/core';
import { FoodPreference, MenuItem } from '../model/menu-item';
import { catchError, Observable, tap, throwError } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class MenuService {
  private apiUrl = environment.apiBaseUrl + '/api/v1/menu';

  // 🔹 State signals
  private readonly _menuItems = signal<MenuItem[] | null>(null);
  readonly menuItems = this._menuItems.asReadonly();

  private readonly _loading = signal(false);
  readonly loading = this._loading.asReadonly();

  private readonly _error = signal<string | null>(null);
  readonly error = this._error.asReadonly();

  searchTerm = signal<string>('');

  setSearch(term: string) {
    this.searchTerm.set(term.toLowerCase());
  }

  private readonly _activeFilter = signal<FoodPreference>('all');
  readonly activeFilter = this._activeFilter.asReadonly();

  setFilter(filter: FoodPreference) {
    this._activeFilter.set(filter);
  }

  constructor(private http: HttpClient) {}

  // --------------------
  // Load menu
  // --------------------
  loadMenuItems(): void {
    if (this._menuItems() !== null || this._loading()) return;
    this.fetchMenuItems();
  }

  refreshMenuItems(): void {
    this.fetchMenuItems();
  }

  private fetchMenuItems(): void {
    if (this._loading()) return;

    this._loading.set(true);
    this._error.set(null);

    this.http.get<MenuItem[]>(this.apiUrl).subscribe({
      next: (items) => {
        this._menuItems.set(items);
        this._loading.set(false);
      },
      error: () => {
        this._error.set('Failed to load menu items');
        this._loading.set(false);
      },
    });
  }

  // --------------------
  // Mutations
  // --------------------
  addMenuItem(formData: FormData): Observable<MenuItem> {
    this._error.set(null);

    return this.http.post<MenuItem>(this.apiUrl, formData).pipe(
      tap((item) => {
        if (this._menuItems() === null) {
          this.refreshMenuItems();
        } else {
          this.appendMenuItem(item);
        }
      }),
    );
  }

  private appendMenuItem(item: MenuItem): void {
    this._menuItems.update((items) => [...items!, item]);
  }

  updateMenuItem(itemId: number, formData: FormData): Observable<MenuItem> {
    return this.http.put<MenuItem>(`${this.apiUrl}/${itemId}`, formData).pipe(
      tap((updated) => {
        if (this._menuItems() === null) {
          this.refreshMenuItems();
        } else {
          this.replaceMenuItem(updated);
        }
      }),
    );
  }

  private replaceMenuItem(updated: MenuItem): void {
    this._menuItems.update((items) =>
      items!.map((i) => (i.id === updated.id ? updated : i)),
    );
  }

  toggleAvailability(itemId: number): Observable<void> {
    const previousItems = this._menuItems();
    this._menuItems.update((items) =>
      items!.map((i) => (i.id === itemId ? { ...i, inStock: !i.inStock } : i)),
    );

    return this.http.patch<void>(`${this.apiUrl}/${itemId}`, null).pipe(
      catchError((err) => {
        this._menuItems.set(previousItems);
        return throwError(() => err);
      }),
    );
  }

  deleteItem(id: number): Observable<void> {
    const previousItems = this._menuItems();
    this._menuItems.update((items) => items!.filter((i) => i.id !== id));

    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      catchError((err) => {
        this._menuItems.set(previousItems);
        return throwError(() => err);
      }),
    );
  }

  // --------------------
  // Utilities
  // --------------------
  clearError(): void {
    this._error.set(null);
  }

  clearCache(): void {
    this._menuItems.set(null);
  }
}
