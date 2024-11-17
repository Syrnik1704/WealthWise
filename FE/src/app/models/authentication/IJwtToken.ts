import { UserRole } from '../../../shared';

export interface IJwtToken {
  role: UserRole;
  email: string;
  name: string;
  isActive: boolean;
  iat: number;
  exp: number;
}
