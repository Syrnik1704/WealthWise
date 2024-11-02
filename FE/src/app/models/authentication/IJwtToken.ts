import { Role } from "./ERole";

export interface IJwtToken {
    role: Role; 
    email: string;
    name: string;   
    isActive: boolean; 
    iat: number;         
    exp: number;           
  }
  