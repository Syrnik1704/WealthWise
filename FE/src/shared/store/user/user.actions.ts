import { User } from './user.model';

export class AddUser {
  static readonly type = '[User] Add user';
  constructor(public user: User) {}
}

export class RemoveUserData {
  static readonly type = '[User] Remove user';
}
