import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { AppRoutingModule } from './app-routing.module';

import { AppComponent } from './app.component';
import { NavComponent } from './components/nav/nav.component';
import { LoginComponent } from './components/login/login.component';
import { GameComponent } from './components/game/game.component'

import { GameService } from './services/game.service'
import { WebsocketService } from './services/websocket.service';

@NgModule({
  declarations: [
    AppComponent,
    NavComponent,
    LoginComponent,
    GameComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    ReactiveFormsModule,
  ],
  providers: [
    GameService,
    WebsocketService,
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
