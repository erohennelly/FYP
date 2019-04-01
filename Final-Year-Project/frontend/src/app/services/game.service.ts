import { Injectable } from '@angular/core';
import { WebsocketService } from './websocket.service';
import { Observable, Subject } from 'rxjs';
import { map } from 'rxjs/operators';
import { ServerMessage } from '../models/serverMessage';

@Injectable({
  providedIn: 'root'
})
export class GameService {
  gameUpdates: Subject<any>
  playerJoins: Subject<any>
  playerLeaves: Subject<any>

  constructor(private websocketService: WebsocketService) {
    this.gameUpdates = <Subject<any>>websocketService.gameUpdates().pipe(map((res) => res));
    this.playerJoins = <Subject<any>>websocketService.playerJoin().pipe(map((res) => res));
    this.playerLeaves = <Subject<any>>websocketService.playerLeave().pipe(map((res) => res));
   }

   sendGameUpdates(data: ServerMessage){
    this.gameUpdates.next(data)
  }

  sendPlayerJoin(data){
    this.playerJoins.next(data)
  }

  sendPlayerLeave(data){
    this.playerLeaves.next(data)
  }
}
