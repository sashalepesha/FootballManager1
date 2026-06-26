import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { GameService, Game } from './game.service';
import { Team, TeamService } from '../../team.service';

@Component({
  selector: 'app-games',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './games.component.html',
})
export class GamesComponent implements OnInit {
  games: Game[] = [];
  teams: Team[] = [];

  newGame: any = {
    matchDate: '',
    stadium: '',
    homeScore: 0,
    awayScore: 0,
    homeTeam: null,
    awayTeam: null,
  };

  constructor(
    private gameService: GameService,
    private teamService: TeamService,
  ) {}

  ngOnInit(): void {
    this.load();
    this.loadTeams();
  }

  load(): void {
    this.gameService.getAll().subscribe(res => {
      this.games = res;
    });
  }

  loadTeams(): void {
    this.teamService.getAll().subscribe(res => {
      this.teams = res;
    });
  }

  create(): void {
    this.gameService.create(this.newGame).subscribe({
      next: () => {
        this.newGame = {
          matchDate: '',
          stadium: '',
          homeScore: 0,
          awayScore: 0,
          homeTeam: null,
          awayTeam: null,
        };

        this.load();
      },
      error: err => {
        console.error(err);
      },
    });
  }

  delete(id: number): void {
    this.gameService.delete(id).subscribe(() => {
      this.load();
    });
  }
}
