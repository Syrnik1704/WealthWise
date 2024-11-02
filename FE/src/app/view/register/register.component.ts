import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators, ReactiveFormsModule, AbstractControl } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { TranslateModule } from '@ngx-translate/core';
import { IRegister } from '../../models/authentication/IRegister';
import { IErrorAPIModel } from '../../models/IErrorAPIModel';
import { AuthService } from '../../services/auth/auth.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss'],
  imports: [CommonModule,ReactiveFormsModule, TranslateModule, MatButtonModule,MatFormFieldModule,MatIconModule,MatCheckboxModule,MatInputModule],
})
export class RegisterComponent implements OnInit {

  registerForm!: FormGroup;
  hidePassword = true;
  hideConfirmPassword = true;

  constructor(private fb: FormBuilder, private authService: AuthService, private snackBar: MatSnackBar) {}

  ngOnInit(): void {
    this.registerForm = this.fb.group({
      name: ['', Validators.required],
      surname: ['', Validators.required],
      birthDay: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, this.passwordValidator]],
      confirmPassword: ['', Validators.required]
    }, { validators: this.passwordMatchValidator });
    this.snackBar.open('Konto zostało utworzone', '', {
      duration: 116000,
      panelClass: ['custom-snackbar'],
    });
  }

  passwordValidator(control: AbstractControl): { [key: string]: boolean } | null {
    const passwordRegex = /^[a-zA-Z]{7,}\d$/;
    if (!control.value.match(passwordRegex)) {
      return { invalidPassword: true };
    }
    return null;
  }

  passwordMatchValidator(form: FormGroup) {
    return form.get('password')?.value === form.get('confirmPassword')?.value
      ? null : { passwordMismatch: true };
  }

  async onSubmit(formData: IRegister) {
    if (this.registerForm.valid) {
      console.log('Form is valid:', this.registerForm.value);
      this.authService.register(formData).subscribe({
        next: () => {
          this.snackBar.open('Konto zostało utworzone', '', {
            duration: 6000,
            panelClass: ['custom-snackbar']
          });
    },
        error: (err: IErrorAPIModel) => { console.log(this.registerForm, err) },
      });
    } else {
      console.log('Form is invalid:', this.registerForm.errors);
    }
  }
}