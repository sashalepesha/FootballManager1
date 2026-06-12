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

  newPlayer: Player = {
    firstName: '',
    lastName: '',
    nationality: '',
    position: '',
    team: {},
  };

  constructor(
    private playerService: PlayerService,
    private teamService: TeamService,
  ) {}

  ngOnInit(): void {
    this.load();
    this.loadTeams();
  }

  load(): void {
    this.playerService.getAll().subscribe(res => {
      this.players = res;
    });
  }

  loadTeams(): void {
    this.teamService.getAll().subscribe(res => {
      this.teams = res;
    });
  }

  create(): void {
    this.playerService.create(this.newPlayer).subscribe(() => {
      this.newPlayer = {
        firstName: '',
        lastName: '',
        nationality: '',
        position: '',
        team: {},
      };

      this.load();
    });
  }

  delete(id: number): void {
    this.playerService.delete(id).subscribe(() => {
      this.load();
    });
  }
}
