import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-login',
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
})
export class LoginComponent {
  // Form data
  username: string = '';
  password: string = '';

  // UI state
  showPassword: boolean = false;
  isLoading: boolean = false;
  errorMessage: string = '';

  // Toggle password visibility
  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
  }

  // Handle login
  onLogin(): void {
    this.errorMessage = '';

    // Basic validation
    if (!this.username || !this.password) {
      this.errorMessage = 'Please enter both username and password';
      return;
    }

    // Set loading state
    this.isLoading = true;

    // TODO: Call your authentication service here
    // Example:
    // this.authService.login(this.username, this.password).subscribe({
    //   next: (response) => {
    //     // Handle successful login
    //     // Store token and redirect to menu management
    //     this.router.navigate(['/menu']);
    //   },
    //   error: (error) => {
    //     this.errorMessage = 'Invalid username or password';
    //     this.isLoading = false;
    //   }
    // });

    // Temporary simulation
    setTimeout(() => {
      this.isLoading = false;
      console.log('Login attempted with:', this.username, this.password);
      // Simulate error for demo
      // this.errorMessage = 'Invalid username or password';
    }, 1500);
  }
}
