import { Component, OnInit } from '@angular/core';
import { CommonModule, DecimalPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { Player, PlayerService } from '../player/player.service';
import { Team, TeamService } from '../../team.service';

import { Transfer, TransferService } from './transfer.service';

import { CurrencyService } from '../../currency.service';

@Component({
  selector: 'app-transfers',
  standalone: true,
  imports: [CommonModule, FormsModule, DecimalPipe],
  templateUrl: './transfers.component.html',
})
export class TransfersComponent implements OnInit {
  transfers: Transfer[] = [];

  players: Player[] = [];

  teams: Team[] = [];

  usdRate = 0;

  editing = false;

  errorMessage = '';

  rateLoading = false;

  rateError = '';

  newTransfer: Transfer = {
    id: undefined,

    player: {} as Player,

    fromTeam: {} as Team,

    toTeam: {} as Team,

    transferFeeUsd: 0,

    transferFeeByn: 0,

    transferDate: '',
  };

  constructor(
    private transferService: TransferService,

    private playerService: PlayerService,

    private teamService: TeamService,

    private currencyService: CurrencyService,
  ) {}

  ngOnInit(): void {
    this.load();

    this.loadPlayers();

    this.loadTeams();

    this.loadCurrency();
  }

  load(): void {
    this.transferService.getAll().subscribe(res => {
      this.transfers = res;
    });
  }

  loadPlayers(): void {
    this.playerService.getAll().subscribe(res => {
      this.players = res;
    });
  }

  loadTeams(): void {
    this.teamService.getAll().subscribe(res => {
      this.teams = res;
    });
  }

  loadCurrency(): void {
    this.currencyService.getUsdRate().subscribe(res => {
      this.usdRate = res.usdToByn;
    });
  }

  getRate(): void {
    this.rateError = '';
    this.rateLoading = true;

    this.currencyService.getUsdRate().subscribe({
      next: res => {
        this.usdRate = res.usdToByn;

        const usd = Number(this.newTransfer.transferFeeUsd) || 0;
        this.newTransfer.transferFeeByn = Math.round(usd * this.usdRate * 100) / 100;

        this.rateLoading = false;
      },

      error: () => {
        this.rateError = 'Не удалось получить курс валют';
        this.rateLoading = false;
      },
    });
  }

  isValid(): boolean {
    return !!(
      this.newTransfer.player?.id &&
      this.newTransfer.fromTeam?.id &&
      this.newTransfer.toTeam?.id &&
      this.newTransfer.transferDate &&
      this.newTransfer.fromTeam.id !== this.newTransfer.toTeam.id &&
      this.newTransfer.transferFeeUsd >= 0 &&
      this.newTransfer.transferFeeByn >= 0
    );
  }

  save(): void {
    if (!this.isValid()) {
      return;
    }

    this.errorMessage = '';

    const request = this.editing ? this.transferService.update(this.newTransfer) : this.transferService.create(this.newTransfer);

    request.subscribe({
      next: () => {
        this.cancel();

        this.load();
      },

      error: err => {
        this.errorMessage = err.error?.message || 'Transfer error';
      },
    });
  }

  edit(transfer: Transfer): void {
    this.editing = true;

    this.newTransfer = {
      id: transfer.id,

      player: transfer.player,

      fromTeam: transfer.fromTeam,

      toTeam: transfer.toTeam,

      transferFeeUsd: transfer.transferFeeUsd,

      transferFeeByn: transfer.transferFeeByn,

      transferDate: transfer.transferDate,
    };
  }

  cancel(): void {
    this.editing = false;

    this.errorMessage = '';

    this.rateError = '';

    this.newTransfer = {
      id: undefined,

      player: {} as Player,

      fromTeam: {} as Team,

      toTeam: {} as Team,

      transferFeeUsd: 0,

      transferFeeByn: 0,

      transferDate: '',
    };
  }

  delete(id: number): void {
    this.transferService
      .delete(id)

      .subscribe(() => {
        this.load();
      });
  }
}
