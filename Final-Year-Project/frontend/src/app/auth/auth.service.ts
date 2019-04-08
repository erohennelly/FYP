import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs/operators';
import { Observable, BehaviorSubject } from 'rxjs';
import { User } from './user';
import { JwtResponse } from './jwt-response';
import {environment} from '../../environments/environment';
import {AppComponent} from '../app.component';

@Injectable({
  providedIn: 'root'
})

export class AuthService {
  authSubject = new BehaviorSubject(false);

  constructor(private httpClient: HttpClient, private appComponent: AppComponent) {
  }

  register(user: User): Observable<JwtResponse> {
    return this.httpClient.post<JwtResponse>(`${environment.url}/register`, user).pipe(
      tap((res: JwtResponse) => {

        if (res.user) {
          this.appComponent.isLoggedIn = true;
          this.authSubject.next(true);
        }
      })
    );
  }

  signIn(user: User): Observable<JwtResponse> {
    return this.httpClient.post(`${environment.url}/login`, user).pipe(
      tap(async (res: JwtResponse) => {

        if (res.user) {
          this.appComponent.isLoggedIn = true;
          this.authSubject.next(true);
        }
      })
    );
  }

  signOut() {
    this.appComponent.username = null;
    this.appComponent.isLoggedIn = false;
    this.authSubject.next(false);
  }

  isAuthenticated() {
    return  this.authSubject.asObservable();
  }
}
