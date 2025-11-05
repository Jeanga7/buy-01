import { Routes } from '@angular/router';
import { AuthLayoutComponent } from './layouts/auth-layout.component';
import { MainLayoutComponent } from './layouts/main-layout.component';

export const routes: Routes = [
    {
        path: '',
        component: MainLayoutComponent,
        children: [
            // Les routes principales de votre application (dashboard, etc.) viendront ici
            { path: '', redirectTo: 'auth', pathMatch: 'full' } // Pour l'instant, on redirige vers la page de connexion
        ]
    },
    {
        path: 'auth',
        component: AuthLayoutComponent,
        loadChildren: () => import('./auth/auth.routes').then(m => m.AUTH_ROUTES)
    },
    // Rediriger toute autre route non trouvée vers la connexion
    { path: '**', redirectTo: 'auth' }
];
