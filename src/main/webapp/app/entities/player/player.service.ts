import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpResponse } from '@angular/common/http';
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

export interface PagedPlayers {
  players: Player[];
  totalCount: number;
  totalPages: number;
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

  getPage(page: number, size: number): Observable<PagedPlayers> {
    const params = new HttpParams().set('page', page.toString()).set('size', size.toString());

    return new Observable<PagedPlayers>(subscriber => {
      this.http.get<Player[]>(this.api, { params, observe: 'response' }).subscribe({
        next: (res: HttpResponse<Player[]>) => {
          subscriber.next({
            players: res.body ?? [],
            totalCount: Number(res.headers.get('X-Total-Count') ?? 0),
            totalPages: Number(res.headers.get('X-Total-Pages') ?? 0),
          });
          subscriber.complete();
        },
        error: err => subscriber.error(err),
      });
    });
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
