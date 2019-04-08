import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppRoutingModule } from './app-routing.module';
import { FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MatTableModule} from '@angular/material';

import { AppComponent } from './app.component';
import { NavComponent } from './components/nav/nav.component';
import { GameComponent } from './components/game/game.component';
import { AdminComponent } from './components/admin/admin.component';
import { LeaderBoardComponent} from './components/leader-board/leader-board.component';

import { GameService } from './services/game.service';
import { WebsocketService } from './services/websocket.service';


@NgModule({
  declarations: [
    AppComponent,
    NavComponent,
    GameComponent,
    AdminComponent,
    LeaderBoardComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    MatTableModule,
  ],
  providers: [
    GameService,
    WebsocketService,
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
