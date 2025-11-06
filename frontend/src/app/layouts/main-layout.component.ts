import { Component, inject } from '@angular/core';
import { Router, RouterLink, RouterOutlet } from '@angular/router';
import { AuthService } from '../auth/auth.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [RouterOutlet, RouterLink, CommonModule],
  template: `
    <div class="app-container">
      <header class="app-header">
        <h1>Mon Application</h1>
        <nav *ngIf="authService.isAuthenticated(); else loggedOut">
          <a (click)="logout()" style="cursor: pointer;">Déconnexion</a>
        </nav>
        <ng-template #loggedOut>
          <nav>
            <a routerLink="/auth/login">Connexion</a>
            <a routerLink="/auth/register">Inscription</a>
          </nav>
        </ng-template>
      </header>
      <main class="app-content">
        <h2>Bienvenue sur la page d'accueil !</h2>
        <p>Ceci est le contenu principal de l'application après la connexion.</p>
      </main>
      <footer class="app-footer">
        <p>&copy; 2025 Mon Application. Tous droits réservés.</p>
      </footer>
    </div>
  `,
})
export class MainLayoutComponent {
  authService = inject(AuthService);
  router = inject(Router);

  logout() {
    this.authService.logout();
    this.router.navigate(['/auth/login']);
  }
}
