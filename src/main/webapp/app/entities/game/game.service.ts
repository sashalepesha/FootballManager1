import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Team } from '../../team.service';

export interface Game {
  id?: number;
  matchDate: string;
  stadium?: string;
  homeScore?: number;
  awayScore?: number;
  homeTeam: Team;
  awayTeam: Team;
}

export interface PagedGames {
  games: Game[];
  totalCount: number;
  totalPages: number;
}

@Injectable({
  providedIn: 'root',
})
export class GameService {
  private api = '/api/games';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Game[]> {
    return this.http.get<Game[]>(this.api);
  }

  getPage(page: number, size: number): Observable<PagedGames> {
    const params = new HttpParams().set('page', page.toString()).set('size', size.toString());

    return new Observable<PagedGames>(subscriber => {
      this.http.get<Game[]>(this.api, { params, observe: 'response' }).subscribe({
        next: (res: HttpResponse<Game[]>) => {
          subscriber.next({
            games: res.body ?? [],
            totalCount: Number(res.headers.get('X-Total-Count') ?? 0),
            totalPages: Number(res.headers.get('X-Total-Pages') ?? 0),
          });
          subscriber.complete();
        },
        error: err => subscriber.error(err),
      });
    });
  }

  create(game: Game): Observable<Game> {
    return this.http.post<Game>(this.api, game);
  }

  generate(count: number): Observable<number> {
    return this.http.post<number>(`${this.api}/generate?count=${count}`, {});
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/${id}`);
  }
}
