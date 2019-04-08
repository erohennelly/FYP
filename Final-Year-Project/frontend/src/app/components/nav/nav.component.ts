import { Component, OnInit } from '@angular/core';
import {AppComponent} from '../../app.component';
import {AuthService} from '../../auth/auth.service';

@Component({
  selector: 'app-nav',
  templateUrl: './nav.component.html',
  styleUrls: ['./nav.component.scss'],
  providers: [AuthService]
})

export class NavComponent implements OnInit {

  appTitle = 'Bee Buzz';
  constructor(private appComponent: AppComponent, private authService: AuthService) { }

  ngOnInit() {
  }

  onLogout() {
    this.authService.signOut();
  }
}
