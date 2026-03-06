import { Injectable, signal } from '@angular/core';
import { environment } from '../../environments/environment';
import { Category } from '../model/category';
import { HttpClient } from '@angular/common/http';
import { catchError, Observable, tap, throwError } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class CategoryService {
  private apiUrl = environment.apiBaseUrl + '/api/v1/categories';

  // 🔹 State signals
  private readonly _categories = signal<Category[] | null>(null);
  readonly categories = this._categories.asReadonly();

  private readonly _loading = signal(false);
  readonly loading = this._loading.asReadonly();

  private readonly _error = signal<string | null>(null);
  readonly error = this._error.asReadonly();

  constructor(private http: HttpClient) {}

  // --------------------
  // Load categories
  // --------------------
  loadCategories(): void {
    if (this._categories() !== null || this._loading()) return;
    this.fetchCategories();
  }

  refreshCategories(): void {
    this.fetchCategories();
  }

  private fetchCategories(): void {
    if (this._loading()) return;

    this._loading.set(true);
    this._error.set(null);

    this.http.get<Category[]>(this.apiUrl).subscribe({
      next: (categories) => {
        this._categories.set(categories);
        this._loading.set(false);
      },
      error: () => {
        this._error.set('Failed to load categories');
        this._loading.set(false);
      },
    });
  }

  // --------------------
  // Mutations
  // --------------------
  addCategory(payload: { categoryName: string }): Observable<Category> {
    this._error.set(null);

    return this.http.post<Category>(this.apiUrl, payload).pipe(
      tap((category) => {
        if (this._categories() === null) {
          this.refreshCategories();
        } else {
          this.appendCategory(category);
        }
      }),
    );
  }

  private appendCategory(category: Category): void {
    this._categories.update((categories) => [...categories!, category]);
  }

  updateCategory(
    itemId: number,
    payload: { categoryName: string },
  ): Observable<Category> {
    return this.http.put<Category>(`${this.apiUrl}/${itemId}`, payload).pipe(
      tap((updated) => {
        if (this._categories() === null) {
          this.refreshCategories();
        } else {
          this.replaceCategory(updated);
        }
      }),
    );
  }

  private replaceCategory(updated: Category): void {
    this._categories.update((items) =>
      items!.map((i) => (i.id === updated.id ? updated : i)),
    );
  }

  deleteCategory(id: number): Observable<void> {
    const previousItems = this._categories();
    this._categories.update((items) => items!.filter((i) => i.id !== id));

    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      catchError((err) => {
        this._categories.set(previousItems);
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
    this._categories.set([]);
  }
}
