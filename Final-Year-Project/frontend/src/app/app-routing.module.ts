import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { GameComponent } from './components/game/game.component';
import { AdminComponent } from './components/admin/admin.component';
import { AuthGuard } from './auth.guard';
import {LeaderBoardComponent} from './components/leader-board/leader-board.component';

const routes: Routes = [
  { path: '', component: GameComponent },
  { path: 'game', component: GameComponent },
  { path: 'auth', loadChildren: './auth/auth.module#AuthModule' },
  { path: 'admin', component: AdminComponent, canActivate: [AuthGuard] },
  { path: 'leaderboard', component: LeaderBoardComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
