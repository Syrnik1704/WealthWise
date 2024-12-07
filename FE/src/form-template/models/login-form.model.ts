import { FormControl } from '@angular/forms';

export enum LoginFormKeys {
  EMAIL = 'email',
  PASSWORD = 'password',
}

export interface LoginForm {
  [LoginFormKeys.EMAIL]: FormControl<string>;
  [LoginFormKeys.PASSWORD]: FormControl<string>;
}

export interface LoginFormValue {
  [LoginFormKeys.EMAIL]: string;
  [LoginFormKeys.PASSWORD]: string;
}
