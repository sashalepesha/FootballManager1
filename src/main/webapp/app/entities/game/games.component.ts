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

  page = 0;
  pageSize = 20;
  totalCount = 0;
  totalPages = 0;

  generating = false;
  generateError = '';

  newGame: Game = {
    matchDate: '',
    stadium: '',
    homeScore: 0,
    awayScore: 0,
    homeTeam: {} as Team,
    awayTeam: {} as Team,
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
    this.gameService.getPage(this.page, this.pageSize).subscribe(res => {
      this.games = res.games;
      this.totalCount = res.totalCount;
      this.totalPages = res.totalPages;
    });
  }

  goToPage(page: number): void {
    if (page < 0 || page >= this.totalPages || page === this.page) {
      return;
    }
    this.page = page;
    this.load();
  }

  nextPage(): void {
    this.goToPage(this.page + 1);
  }

  previousPage(): void {
    this.goToPage(this.page - 1);
  }

  onPageSizeChange(): void {
    this.page = 0;
    this.load();
  }

  loadTeams(): void {
    this.teamService.getAll().subscribe(res => {
      this.teams = res;
    });
  }

  isValid(): boolean {
    return !!(
      this.newGame.matchDate &&
      this.newGame.stadium &&
      this.newGame.homeTeam?.id &&
      this.newGame.awayTeam?.id &&
      this.newGame.homeTeam.id !== this.newGame.awayTeam.id &&
      this.newGame.homeScore! >= 0 &&
      this.newGame.awayScore! >= 0
    );
  }

  create(): void {
    if (!this.isValid()) {
      return;
    }

    const game: Game = {
      ...this.newGame,
      matchDate: new Date(this.newGame.matchDate).toISOString(),
    };

    this.gameService.create(game).subscribe({
      next: () => {
        this.newGame = {
          matchDate: '',
          stadium: '',
          homeScore: 0,
          awayScore: 0,
          homeTeam: {} as Team,
          awayTeam: {} as Team,
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

  generate500(): void {
    this.generateError = '';
    this.generating = true;

    this.gameService.generate(500).subscribe({
      next: () => {
        this.generating = false;
        this.load();
      },

      error: err => {
        this.generateError = err.error?.message || 'Не удалось сгенерировать матчи';
        this.generating = false;
      },
    });
  }
}
