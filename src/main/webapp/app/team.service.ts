import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Team {
  id?: number;
  name: string;
  city?: string;
  stadium?: string;
  budget?: number;
}

@Injectable({
  providedIn: 'root'
})
export class TeamService {

  private api = '/api/teams';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Team[]> {
    return this.http.get<Team[]>(this.api);
  }

  create(team: Team): Observable<Team> {
    return this.http.post<Team>(this.api, team);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/${id}`);
  }
}
