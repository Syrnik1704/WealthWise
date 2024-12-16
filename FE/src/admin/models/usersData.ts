export interface UsersData {
    UserData: UserData[];
  }

  export interface UserData {
    id: number;
    name: string;
    surname: string;
    email: string;
    isActive: boolean;
    role: string;
    birthDay: string;
  }
