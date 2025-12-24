import { Routes } from '@angular/router';
import { MenuComponent } from './pages/menu/menu.component';
import { LoginComponent } from './pages/login/login.component';

export const routes: Routes = [
  { path: '', component: MenuComponent },
  { path: 'login', component: LoginComponent },
];
