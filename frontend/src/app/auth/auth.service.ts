import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor() { }

  login(credentials: any) {
    console.log('login attempt', credentials);
    // Logique d'appel HTTP à venir
  }

  register(userInfo: FormData) {
    console.log('register attempt with FormData:');
    userInfo.forEach((value, key) => {
      console.log(key, value);
    });
    // Logique d'appel HTTP à venir
  }
}
