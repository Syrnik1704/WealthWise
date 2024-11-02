import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { ILogin } from '../../../models/authentication/ILogin';
import { IErrorAPIModel } from '../../../models/IErrorAPIModel';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../services/auth/auth.service';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { TranslateModule } from '@ngx-translate/core';
import { ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldControl, MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'ww-login',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [CommonModule, TranslateModule, MatButtonModule,ReactiveFormsModule,MatFormFieldModule,MatIconModule,MatCheckboxModule,MatInputModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent implements OnInit {
  constructor(
    private authService: AuthService,
    @Inject(Router) private router: Router,
    private formBuilder: FormBuilder,
    private snackBar: MatSnackBar
  ) { }

  userForm!: FormGroup;
  hide: boolean = true;
  email: string | null = '';
  rememberMe: boolean = false;

  ngOnInit() {

    this.authService.token.next({ tokenAccess: '', tokenRefresh: '' });
    this.authService.loggedUser.next(undefined);
    localStorage.removeItem('access_token');

    if(localStorage.getItem('rememberMe'))
      this.rememberMe = true;

    if (localStorage.getItem('email') != null)
      this.email = localStorage.getItem('email')

    this.userForm = this.formBuilder.group({
      email: [this.email, Validators.compose([
        Validators.required,
        Validators.email
      ])],
      password: ['', Validators.required]

    })
  }
  
  async onChangeRememberMe() {
    this.rememberMe = !this.rememberMe
  }
  async goToMainPage() {
    this.router.navigate(['/']);
  }
  async goRegisterPage() {
    this.router.navigate(['/register']);
  }

  async onSubmit(formData: ILogin) {

    
    if (this.rememberMe) {
      localStorage.setItem('rememberMe', 'true');
      localStorage.setItem('email', formData.email);
    }else{
      localStorage.removeItem('rememberMe');
      localStorage.removeItem('email');
    }

    this.authService.login(formData).subscribe({
      next: () => {
        this.snackBar.open('Zostałeś zalogowany', '', {
          duration: 5000,
          panelClass: ['custom-snackbar']
        })},
      error: (err: IErrorAPIModel) => { console.log(this.userForm, err) },
    });

  }
}


