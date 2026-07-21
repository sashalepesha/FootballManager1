import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { Authority } from 'app/shared/jhipster/constants';

import { errorRoute } from './layouts/error/error.route';

import { TeamsComponent } from './teams.component';
import { PlayersComponent } from './entities/player/players.component';
import { GamesComponent } from './entities/game/games.component';
import { TransfersComponent } from './entities/transfer/transfers.component';

const routes: Routes = [
  {
    path: 'games',
    component: GamesComponent,
  },
  {
    path: 'players',
    component: PlayersComponent,
  },
  {
    path: 'teams',
    component: TeamsComponent,
  },
  {
    path: 'transfers',
    component: TransfersComponent,
  },
  {
    path: '',
    loadComponent: () => import('./home/home'),
    title: 'Welcome, Java Hipster!',
  },
  {
    path: '',
    loadComponent: () => import('./layouts/navbar/navbar'),
    outlet: 'navbar',
  },
  {
    path: 'admin',
    data: {
      authorities: [Authority.ADMIN],
    },
    canActivate: [UserRouteAccessService],
    loadChildren: () => import('./admin/admin.routes'),
  },
  {
    path: 'account',
    loadChildren: () => import('./account/account.route'),
  },
  {
    path: 'login',
    loadComponent: () => import('./login/login'),
    title: 'Sign in',
  },
  {
    path: '',
    loadChildren: () => import('./entities/entity.routes'),
  },
  ...errorRoute,
];

export default routes;
