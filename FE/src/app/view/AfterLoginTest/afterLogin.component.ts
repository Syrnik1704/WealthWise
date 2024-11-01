import { ChangeDetectorRef, Component, Input, OnInit } from '@angular/core';
import { jwtDecode } from 'jwt-decode';
import { interval, observable, Subscription } from 'rxjs';
import { IJwtToken } from '../../models/authentication/IJwtToken';
import { AuthService } from '../../services/auth/auth.service';
import { CommonModule, DecimalPipe } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AfterLoginService } from './afterLogin.service';

@Component({
  selector: 'app-afterLogin',
  templateUrl: './afterLogin.component.html',
  styleUrls: ['./afterLogin.component.css'], 
 standalone: true,
  imports: [CommonModule,RouterModule],

})
export class AfterLoginComponent implements OnInit {

  @Input() token: string | null = null;
  timeLeftInSeconds: number = 0;
  private timerSubscription!: Subscription;

  constructor(private authService: AuthService, private afterLoginService:AfterLoginService, private cdr: ChangeDetectorRef) {
    this.timeLeftInSeconds = 0;
  }

  ngOnInit(): void {
   // const observable = interval(1000);
   
    this.token = this.token || this.authService.token.value?.tokenAccess || null;

    if (this.token) {
      const decodedToken: IJwtToken = jwtDecode<IJwtToken>(this.token);
      const expirationTime = decodedToken.exp;

      this.updateTimeLeft(expirationTime); // Inicjalizuj pozostały czas
      

      this.timerSubscription = interval(1000).subscribe(() => {
        this.updateTimeLeft(expirationTime);
      });
    } else {
      console.error("Token jest niedostępny.");
      this.timeLeftInSeconds = 0;
    }
  }
  protected getData() {
    this.afterLoginService.getData().subscribe((data) => {
      console.log(data);
    });
  }
  protected logOut() {
   this.authService.logout();
  }

  private updateTimeLeft(expirationTime: number): void {
    const currentTimeInSeconds = Math.floor(Date.now() / 1000);
    this.timeLeftInSeconds = expirationTime - currentTimeInSeconds;
    this.cdr.detectChanges(); 
    if (this.timeLeftInSeconds <= 0) {
      this.timeLeftInSeconds = 0;
      console.error("Token wygasł.");
      this.timerSubscription.unsubscribe();
    }
  }

  ngOnDestroy(): void {
    if (this.timerSubscription) {
      this.timerSubscription.unsubscribe();
    }
  }

}
