import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../auth.service';
import { Router, RouterLink } from '@angular/router';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './register.component.html',
  styleUrl: '../auth.css'
})
export class RegisterComponent {
  registerForm: FormGroup;
  authService = inject(AuthService);
  router = inject(Router);
  avatarPreview: string | ArrayBuffer | null = null;
  passwordVisible = false;

  constructor(private fb: FormBuilder) {
    this.registerForm = this.fb.group({
      name: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required]],
      avatar: [null]
    });
  }

  onFileSelect(event: Event) {
    const element = event.currentTarget as HTMLInputElement;
    let fileList: FileList | null = element.files;
    if (fileList && fileList[0]) {
      const file = fileList[0];
      this.registerForm.patchValue({ avatar: file });

      const reader = new FileReader();
      reader.onload = () => {
        this.avatarPreview = reader.result;
      };
      reader.readAsDataURL(file);
    }
  }

  togglePasswordVisibility(): void {
    this.passwordVisible = !this.passwordVisible;
  }

  onSubmit() {
    if (this.registerForm.valid) {
      const formData = new FormData();
      Object.keys(this.registerForm.value).forEach(key => {
        formData.append(key, this.registerForm.get(key)?.value);
      });
      
      this.authService.register(formData);
      // Redirection ou gestion de la réponse à venir
      // this.router.navigate(['/auth/login']);
    }
  }
}
