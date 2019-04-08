import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../auth.service';
import {AppComponent} from '../../app.component';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
  providers: [AuthService]
})
export class LoginComponent implements OnInit {

  constructor(private authService: AuthService, private router: Router, private appComponent: AppComponent) { }

  ngOnInit() {
  }

  login(form) {
    console.log(form.value);
    this.authService.signIn(form.value).subscribe((res) => {
      this.appComponent.username = res.user.userName;
      this.router.navigateByUrl('game');
    });
  }

}
