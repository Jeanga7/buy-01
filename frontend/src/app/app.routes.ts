import { Routes } from '@angular/router';
import { AuthLayoutComponent } from './layouts/auth-layout.component';
import { MainLayoutComponent } from './layouts/main-layout.component';
import { authGuard } from './auth/auth.guard';
import { authRedirectGuard } from './auth/auth-redirect.guard';

export const routes: Routes = [
    {
        path: '',
        component: MainLayoutComponent,
        canActivate: [authGuard],
    },
    {
        path: 'auth',
        component: AuthLayoutComponent,
        canActivate: [authRedirectGuard],
        loadChildren: () => import('./auth/auth.routes').then(m => m.AUTH_ROUTES)
    },
    // Rediriger toute autre route non trouvée vers la connexion
    { path: '**', redirectTo: 'auth' }
];
