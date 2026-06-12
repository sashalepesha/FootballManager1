import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { GameService, Game } from './game.service';

@Component({
  selector: 'app-games',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './games.component.html'
})
export class GamesComponent implements OnInit {

  games: Game[] = [];

  newGame: any = {
    matchDate: '',
    stadium: '',
    homeScore: 0,
    awayScore: 0,
    homeTeam: {
      id: null
    },
    awayTeam: {
      id: null
    }
  };

  constructor(private gameService: GameService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.gameService.getAll().subscribe(res => {
      this.games = res;
    });
  }

  create(): void {
    this.gameService.create(this.newGame).subscribe(() => {
      this.load();
    });
  }

  delete(id: number): void {
    this.gameService.delete(id).subscribe(() => {
      this.load();
    });
  }
}
