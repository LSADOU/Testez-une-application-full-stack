import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { Router } from '@angular/router';
import { expect } from '@jest/globals';
import { of, throwError } from 'rxjs';

import { RegisterComponent } from './register.component';
import { AuthService } from '../../services/auth.service';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let authService: AuthService;
  let router: Router;

  const mockAuthService = {
    register: jest.fn()
  };

  const mockRouter = {
    navigate: jest.fn()
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [RegisterComponent],
      imports: [
        BrowserAnimationsModule,
        HttpClientModule,
        ReactiveFormsModule,
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule
      ],
      providers: [
        { provide: AuthService, useValue: mockAuthService },
        { provide: Router, useValue: mockRouter }
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    authService = TestBed.inject(AuthService);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('Initial state', () => {
    it('should have onError set to false initially', () => {
      expect(component.onError).toBe(false);
    });

    it('should have a form with email, firstName, lastName, and password fields', () => {
      expect(component.form.contains('email')).toBe(true);
      expect(component.form.contains('firstName')).toBe(true);
      expect(component.form.contains('lastName')).toBe(true);
      expect(component.form.contains('password')).toBe(true);
    });
  });

  describe('submit - Success', () => {
    beforeEach(() => {
      mockAuthService.register.mockReturnValue(of(void 0));
    });

    it('should call authService.register with form values', () => {
      component.form.setValue({
        email: 'test@example.com',
        firstName: 'Loic',
        lastName: 'Sadou',
        password: 'mdploic123'
      });

      component.submit();

      expect(authService.register).toHaveBeenCalledWith({
        email: 'test@example.com',
        firstName: 'Loic',
        lastName: 'Sadou',
        password: 'mdploic123'
      });
    });

    it('should navigate to /login on successful registration', () => {
      component.form.setValue({
        email: 'test@example.com',
        firstName: 'Loic',
        lastName: 'Sadou',
        password: 'mdploic123'
      });

      component.submit();

      expect(router.navigate).toHaveBeenCalledWith(['/login']);
    });

    it('should not set onError to true on successful registration', () => {
      component.form.setValue({
        email: 'test@example.com',
        firstName: 'Loic',
        lastName: 'Sadou',
        password: 'mdploic123'
      });

      component.submit();

      expect(component.onError).toBe(false);
    });
  });

  describe('submit - Error', () => {
    beforeEach(() => {
      mockAuthService.register.mockReturnValue(
        throwError(() => new Error('Registration failed'))
      );
    });

    it('should set onError to true when registration fails', () => {
      component.form.setValue({
        email: 'test@example.com',
        firstName: 'Loic',
        lastName: 'Sadou',
        password: 'mdploic123'
      });

      component.submit();

      expect(component.onError).toBe(true);
    });

    it('should not navigate when registration fails', () => {
      component.form.setValue({
        email: 'test@example.com',
        firstName: 'Loic',
        lastName: 'Sadou',
        password: 'mdploic123'
      });

      component.submit();

      expect(router.navigate).not.toHaveBeenCalled();
    });

    it('should handle error when email already exists', () => {
      mockAuthService.register.mockReturnValue(
        throwError(() => ({ status: 409, message: 'Email already exists' }))
      );

      component.form.setValue({
        email: 'existing@example.com',
        firstName: 'Loic',
        lastName: 'Sadou',
        password: 'mdploic123'
      });

      component.submit();

      expect(component.onError).toBe(true);
    });
  });
});