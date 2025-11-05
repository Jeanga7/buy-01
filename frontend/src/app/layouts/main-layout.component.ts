import { Component } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [RouterOutlet, RouterLink],
  template: `
    <div class="app-container">
      <header class="app-header">
        <h1>Mon Application</h1>
        <nav>
          <a routerLink="/auth/login">Connexion</a>
          <a routerLink="/auth/register">Inscription</a>
        </nav>
      </header>
      <main class="app-content">
        <router-outlet></router-outlet>
      </main>
      <footer class="app-footer">
        <p>&copy; 2025 Mon Application. Tous droits réservés.</p>
      </footer>
    </div>
  `,
})
export class MainLayoutComponent {}
