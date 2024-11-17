import { Selector } from '@ngxs/store';
import { User } from './user.model';
import { UserState, UserStateModel } from './user.state';

export class UserSelectors {
  @Selector([UserState])
  static user(state: UserStateModel): User | undefined {
    return state?.user;
  }
}
