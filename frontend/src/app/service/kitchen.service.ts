import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Kitchen } from '../model/kitchen';

@Injectable({
  providedIn: 'root',
})
export class KitchenService {
  private apiUrl = environment.apiBaseUrl + '/api/v1/kitchens';
  constructor(private http: HttpClient) {}

  getKitchen(): Observable<Kitchen> {
    return this.http.get<Kitchen>(this.apiUrl);
  }
}
