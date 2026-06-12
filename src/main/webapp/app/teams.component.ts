import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { TeamService, Team } from './team.service';

@Component({
  selector: 'app-teams',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './teams.component.html'
})
export class TeamsComponent implements OnInit {

  teams: Team[] = [];

  newTeam: Team = {
    name: ''
  };

  constructor(private teamService: TeamService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.teamService.getAll().subscribe(res => {
      this.teams = res;
    });
  }

  create(): void {
    this.teamService.create(this.newTeam).subscribe(() => {
      this.newTeam = { name: '' };
      this.load();
    });
  }

  delete(id: number): void {
    this.teamService.delete(id).subscribe(() => {
      this.load();
    });
  }
}
