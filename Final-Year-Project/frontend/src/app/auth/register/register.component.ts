import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../auth.service';
import { AppComponent } from '../../app.component';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss'],
  providers: [AuthService]
})
export class RegisterComponent implements OnInit {

  constructor(private authService: AuthService, private router: Router, private appComponent: AppComponent) { }

  ngOnInit() {}

  register(form) {
    delete form.value.email
    this.authService.register(form.value).subscribe((res) => {
      this.appComponent.username = res.user.userName;
      this.router.navigateByUrl('game');
    });
  }
}
