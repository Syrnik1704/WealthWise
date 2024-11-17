import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import {
  FormControl,
  FormGroup,
  NonNullableFormBuilder,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { RouterModule } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { catchError, EMPTY } from 'rxjs';
import { AuthService } from '../../auth';
import { LoginForm, LoginFormKeys } from '../models';

@Component({
  selector: 'ww-login',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    CommonModule,
    TranslateModule,
    ReactiveFormsModule,
    MatInputModule,
    MatFormFieldModule,
    MatButtonModule,
    RouterModule,
  ],
  template: `
    @if (modelForm) {
      <form class="login-form" [formGroup]="modelForm">
        <img [src]="'/assets/imgs/wealthwise.webp'" [alt]="'COMMON.APP_NAME' | translate" />
        <mat-form-field>
          <mat-label>{{ 'COMMON.LABELS.EMAIL' | translate }}</mat-label>
          <input type="email" matInput [formControlName]="LoginFormKeys.EMAIL" />
          @if (emailFormControl?.hasError('email') && !emailFormControl?.hasError('required')) {
            <mat-error>{{ 'COMMON.ERRORS.INVALID_EMAIL' | translate }}</mat-error>
          }
          @if (emailFormControl?.hasError('required')) {
            <mat-error>{{
              'COMMON.ERRORS.REQUIRED' | translate: { value: 'COMMON.LABELS.EMAIL' | translate }
            }}</mat-error>
          }
        </mat-form-field>
        <mat-form-field>
          <mat-label>{{ 'COMMON.LABELS.PASSWORD' | translate }}</mat-label>
          <input type="password" matInput [formControlName]="LoginFormKeys.PASSWORD" />
          @if (emailFormControl?.hasError('required')) {
            <mat-error>{{
              'COMMON.ERRORS.REQUIRED' | translate: { value: 'COMMON.LABELS.PASSWORD' | translate }
            }}</mat-error>
          }
        </mat-form-field>
        @if (modelForm.hasError(authenticationError)) {
          <mat-error>{{ 'COMMON.ERRORS.AUTHENTICATION_ERROR' | translate }}</mat-error>
        }
        <button type="button" mat-button [routerLink]="['register']">
          {{ 'LOGIN_FORM.SINGUP_MESSAGE' | translate }}
        </button>
        <button mat-flat-button (click)="onLoginClick()">
          {{ 'COMMON.LOGIN' | translate }}
        </button>
      </form>
    }
  `,
  styles: `
    :host {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 100%;
      height: 100%;
    }
    .login-form {
      display: flex;
      gap: 8px;
      flex-direction: column;
      width: 75%;
      background: aliceblue;
      border-radius: 8px;
      border: 1px solid lightgrey;
      padding: 24px;
    }
  `,
})
export class LoginComponent implements OnInit {
  protected modelForm?: FormGroup<LoginForm>;
  protected readonly LoginFormKeys = LoginFormKeys;
  protected readonly authenticationError = 'authenticationError';
  private readonly formBuild = inject(NonNullableFormBuilder);
  private readonly authService = inject(AuthService);
  private readonly destroyRef = inject(DestroyRef);

  public ngOnInit(): void {
    this.modelForm = this.createForm();
  }

  protected get emailFormControl(): FormControl<string> | undefined {
    return this.modelForm?.controls?.[LoginFormKeys.EMAIL];
  }

  protected onLoginClick(): void {
    if (!this.modelForm?.valid) {
      return;
    }
    this.authService
      .login(this.modelForm.getRawValue())
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        catchError(() => {
          this.modelForm?.setErrors({ [this.authenticationError]: true });
          return EMPTY;
        })
      )
      .subscribe();
  }

  private createForm(): FormGroup<LoginForm> {
    return this.formBuild.group<LoginForm>({
      [LoginFormKeys.EMAIL]: new FormControl<string>('', {
        validators: [Validators.required, Validators.email],
        nonNullable: true,
      }),
      [LoginFormKeys.PASSWORD]: new FormControl<string>('', {
        validators: [Validators.required],
        nonNullable: true,
      }),
    });
  }
}
