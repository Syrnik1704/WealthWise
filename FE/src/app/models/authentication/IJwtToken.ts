export interface IJwtToken {
    scopes: string[];     
    sub: string;        
    iat: number;         
    exp: number;           
  }
  