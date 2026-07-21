import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
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

@Injectable({
  providedIn: 'root',
})
export class GameService {
  private api = '/api/games';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Game[]> {
    return this.http.get<Game[]>(this.api);
  }

  create(game: Game): Observable<Game> {
    return this.http.post<Game>(this.api, game);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/${id}`);
  }
}
