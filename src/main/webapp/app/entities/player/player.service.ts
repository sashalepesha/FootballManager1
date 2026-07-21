import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Player {
  id?: number;

  firstName: string;
  lastName: string;

  birthDate?: string;

  nationality?: string;

  position?: string;

  marketValue?: number;

  team?: any;
}

@Injectable({
  providedIn: 'root',
})
export class PlayerService {
  private api = '/api/players';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Player[]> {
    return this.http.get<Player[]>(this.api);
  }

  getByTeam(teamId: number): Observable<Player[]> {
    return this.http.get<Player[]>(`${this.api}/team/${teamId}`);
  }

  create(player: Player): Observable<Player> {
    return this.http.post<Player>(this.api, player);
  }

  update(player: Player): Observable<Player> {
    return this.http.put<Player>(`${this.api}/${player.id}`, player);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/${id}`);
  }
}
