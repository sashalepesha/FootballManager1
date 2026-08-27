import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { Player, PlayerService } from './player.service';
import { Team, TeamService } from '../../team.service';

@Component({
  selector: 'app-players',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './players.component.html',
})
export class PlayersComponent implements OnInit {
  players: Player[] = [];
  teams: Team[] = [];

  page = 0;
  pageSize = 20;
  totalCount = 0;
  totalPages = 0;

  newPlayer: Player = {
    firstName: '',
    lastName: '',
    birthDate: '',
    nationality: '',
    position: '',
    marketValue: undefined,
    team: {},
  };

  today: string = new Date().toISOString().split('T')[0];

  constructor(
    private playerService: PlayerService,
    private teamService: TeamService,
  ) {}

  ngOnInit(): void {
    this.load();
    this.loadTeams();
  }

  load(): void {
    this.playerService.getPage(this.page, this.pageSize).subscribe(res => {
      this.players = res.players;
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
      this.newPlayer.firstName?.trim() &&
      this.newPlayer.lastName?.trim() &&
      this.newPlayer.nationality?.trim() &&
      this.newPlayer.position?.trim() &&
      this.newPlayer.birthDate &&
      this.newPlayer.team &&
      this.newPlayer.team.id &&
      (this.newPlayer.marketValue == null || this.newPlayer.marketValue >= 0) &&
      this.newPlayer.birthDate <= this.today
    );
  }

  create(): void {
    if (!this.isValid()) {
      return;
    }

    this.playerService.create(this.newPlayer).subscribe({
      next: () => {
        this.newPlayer = {
          firstName: '',
          lastName: '',
          birthDate: '',
          nationality: '',
          position: '',
          marketValue: undefined,
          team: {},
        };

        this.load();
      },

      error: err => console.error(err),
    });
  }

  delete(id: number): void {
    this.playerService.delete(id).subscribe(() => {
      this.load();
    });
  }
}
