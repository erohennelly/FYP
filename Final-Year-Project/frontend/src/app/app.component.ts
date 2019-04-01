import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})

export class AppComponent {
  title = 'newfrontend';
  username: string;

  constructor() {}

  getUsername() {
    return this.username;
  }

  setUsername(username: string) {
    this.username = username;
  }
}
