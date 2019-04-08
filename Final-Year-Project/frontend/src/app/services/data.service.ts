import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {PlayerList} from '../models/PlayerList';
import {environment} from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class DataService {

  constructor(private http: HttpClient) {
  }

  getPlayers() {
    return this.http.get<PlayerList>(environment.url + '/players');
  }

  getUser(userId) {
    return this.http.get(environment.url + '/player/' + userId);
  }
}
