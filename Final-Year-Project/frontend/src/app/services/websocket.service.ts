import { Injectable } from '@angular/core';
import * as io from 'socket.io-client';
import { Observable } from 'rxjs';
import * as Rx from 'rxjs';
import { environment } from '../../environments/environment';
import { ServerMessage } from '../models/serverMessage';

@Injectable({
  providedIn: 'root'
})
export class WebsocketService {

  private socket;

  constructor() { }

  playerJoin(): Rx.Subject<MessageEvent> {
    this.socket = io(environment.ws_url);

    const observable = new Observable(observer => {
      this.socket.on('setPlayer', (data) => {
        observer.next(data);
      });
      return () => {
        this.socket.disconnect();
      };
    });

    const observer = {
      next: (data: Object) => {
        this.socket.emit('playerJoined', data);
      },
    };
    return Rx.Subject.create(observer, observable);
  }

  playerLeave(): Rx.Subject<MessageEvent> {
    this.socket = io(environment.ws_url);

    const observable = new Observable(() => { this.socket.disconnect(); });

    const observer = {
      next: (data: Object) => {
        this.socket.emit('playerLeft', data);
      },
    };
    return Rx.Subject.create(observer, observable);
  }

  gameUpdates(): Rx.Subject<MessageEvent> {
    this.socket = io(environment.ws_url);

    const observable = new Observable(observer => {
      this.socket.on('mapUpdate', (data) => {
        observer.next(data);
      });
      return () => {
        this.socket.disconnect();
      };
    });

    const observer = {
      next: (data: ServerMessage) => {
        this.socket.emit('updatePlayerPosition', data);
      },
    };
    return Rx.Subject.create(observer, observable);
  }
}
