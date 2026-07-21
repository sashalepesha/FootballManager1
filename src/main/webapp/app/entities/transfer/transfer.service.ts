import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Player } from '../player/player.service';
import { Team } from '../../team.service';

export interface Transfer {
  id?: number;

  player: Player;

  fromTeam: Team;

  toTeam: Team;

  transferFeeUsd: number;

  transferFeeByn: number;

  transferDate: string;
}

@Injectable({
  providedIn: 'root',
})
export class TransferService {
  private api = '/api/transfers';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Transfer[]> {
    return this.http.get<Transfer[]>(this.api);
  }

  get(id: number): Observable<Transfer> {
    return this.http.get<Transfer>(`${this.api}/${id}`);
  }

  create(transfer: Transfer): Observable<Transfer> {
    return this.http.post<Transfer>(this.api, transfer);
  }

  update(transfer: Transfer): Observable<Transfer> {
    return this.http.put<Transfer>(`${this.api}/${transfer.id}`, transfer);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/${id}`);
  }
}
