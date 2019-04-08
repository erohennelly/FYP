export interface JwtResponse {
  user: {
    id: number,
    userName: string,
    email: string,
    access_token: string,
    expires_in: number
  };
}
