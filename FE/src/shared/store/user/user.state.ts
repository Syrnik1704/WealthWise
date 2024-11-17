import { Injectable } from '@angular/core';
import { Action, State, StateContext, StateToken } from '@ngxs/store';
import { AddUser, RemoveUserData } from './user.actions';
import { User } from './user.model';

export interface UserStateModel {
  user?: User;
}

export const USER_STATE_TOKEN = new StateToken<UserStateModel>('userState');

@State({
  name: USER_STATE_TOKEN,
  defaults: {
    user: undefined,
  },
})
@Injectable()
export class UserState {
  @Action(AddUser)
  addUser(ctx: StateContext<UserStateModel>, action: AddUser) {
    ctx.setState({
      user: { ...action.user },
    });
  }

  @Action(RemoveUserData)
  removeUser(ctx: StateContext<UserStateModel>) {
    ctx.setState({ user: undefined });
  }
}
