import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { environment } from '../../app/environments/environment.development';
import { Category } from '../models';

@Injectable({ providedIn: 'root' })
export class CategoriesApiService {
  private readonly http = inject(HttpClient);
  public getCategories(): Observable<Category[]> {
    return this.http
      .get<Category[]>(`${environment.apiUrl}/categories/getCategories`)
      .pipe(map(values => values));
  }
}
