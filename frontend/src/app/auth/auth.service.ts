import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { API_BASE_URL } from '../core/config';
import { BehaviorSubject, Observable, catchError, map, tap, throwError } from 'rxjs';
import { Router } from '@angular/router';

interface LoginResponse {
  token: string;
  message: string;
  deviceId: string;
  expiresAt: string; // LocalDateTime is serialized as a string
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);
  private apiUrl = API_BASE_URL;
  private readonly TOKEN_KEY = 'auth_token';
  private readonly USER_KEY = 'user_data';
  private authStatus = new BehaviorSubject<boolean>(this.isAuthenticated());

  get currentUser() {
    const user = localStorage.getItem(this.USER_KEY);
    return user ? JSON.parse(user) : null;
  }

  get isLoggedIn$() {
    return this.authStatus.asObservable();
  }

  private setSession(authResult: LoginResponse) {
    const expiresAt = new Date(authResult.expiresAt).getTime();
    
    localStorage.setItem(this.TOKEN_KEY, authResult.token);
    // localStorage.setItem(this.USER_KEY, JSON.stringify(authResult.user)); // user is not part of the response anymore
    localStorage.setItem('expires_at', expiresAt.toString());
    
    this.authStatus.next(true);
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  isAuthenticated(): boolean {
    const token = this.getToken();
    const expiresAt = localStorage.getItem('expires_at');
    
    if (!token || !expiresAt) {
      return false;
    }
    
    return new Date().getTime() < Number(expiresAt);
  }

  logout() {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);
    localStorage.removeItem('expires_at');
    this.authStatus.next(false);
    this.router.navigate(['/login']);
  }

  login(credentials: { email: string; password: string }): Observable<any> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/api/auth/login`, credentials).pipe(
      tap(response => {
        this.setSession(response);
      }),
      catchError(this.handleError)
    );
  }

  register(userInfo: { name: string; email: string; password: string }): Observable<any> {
    return this.http.post(`${this.apiUrl}/api/auth/register`, userInfo).pipe(
      catchError(this.handleError)
    );
  }

  private handleError(error: HttpErrorResponse) {
    let errorMessage = 'Une erreur est survenue';
    if (error.error instanceof ErrorEvent) {
      // Erreur côté client
      errorMessage = error.error.message;
    } else {
      // Erreur côté serveur
      errorMessage = error.error?.message || error.statusText;
    }
    return throwError(() => new Error(errorMessage));
  }
}
