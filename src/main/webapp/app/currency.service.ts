import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface CurrencyRate {
  usdToByn: number;
}

@Injectable({
  providedIn: 'root',
})
export class CurrencyService {
  private api = '/api/currency';

  constructor(private http: HttpClient) {}

  getUsdRate(): Observable<CurrencyRate> {
    return this.http.get<CurrencyRate>(`${this.api}/usd`);
  }
}
