import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class KitchenService {
  private apiUrl = 'http://localhost:8080/api/v1/kitchens';
  constructor(private http: HttpClient) {}

  getKitchen() {
    return this.http.get(this.apiUrl);
  }
}
