import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { Router } from '@angular/router';
import { expect } from '@jest/globals';
import { of, throwError } from 'rxjs';
import { SessionService } from 'src/app/services/session.service';
import { AuthService } from '../../services/auth.service';
import { SessionInformation } from 'src/app/interfaces/sessionInformation.interface';
import { LoginComponent } from './login.component';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authService: AuthService;
  let sessionService: SessionService;
  let router: Router;

  const mockSessionInformation: SessionInformation = {
    token: 'mock-jwt-token',
    type: 'Bearer',
    id: 1,
    username: 'test@example.com',
    firstName: 'loic',
    lastName: 'sadou',
    admin: false
  };

  beforeEach(async () => {
 
    const mockAuthService = {
      login: jest.fn()
    };

    const mockSessionService = {
      logIn: jest.fn()
    };

    const mockRouter = {
      navigate: jest.fn()
    };

    await TestBed.configureTestingModule({
      declarations: [LoginComponent],
      providers: [
        { provide: AuthService, useValue: mockAuthService },
        { provide: SessionService, useValue: mockSessionService },
        { provide: Router, useValue: mockRouter }
      ],
      imports: [
        BrowserAnimationsModule,
        HttpClientModule,
        MatCardModule,
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
        ReactiveFormsModule
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    authService = TestBed.inject(AuthService);
    sessionService = TestBed.inject(SessionService);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('Login Success', () => {
    beforeEach(() => {
      (authService.login as jest.Mock).mockReturnValue(of(mockSessionInformation));
    });

    it('should call authService.login with form values on submit', () => {
      component.form.setValue({
        email: 'test@example.com',
        password: 'password123'
      });

      component.submit();

      expect(authService.login).toHaveBeenCalledWith({
        email: 'test@example.com',
        password: 'password123'
      });
    });

    it('should call sessionService.logIn with response on successful login', () => {
      component.form.setValue({
        email: 'test@example.com',
        password: 'password123'
      });

      component.submit();

      expect(sessionService.logIn).toHaveBeenCalledWith(mockSessionInformation);
    });

    it('should navigate to /sessions on successful login', () => {
      component.form.setValue({
        email: 'test@example.com',
        password: 'password123'
      });

      component.submit();

      expect(router.navigate).toHaveBeenCalledWith(['/sessions']);
    });

    it('should not set onError to true on successful login', () => {
      component.form.setValue({
        email: 'test@example.com',
        password: 'password123'
      });

      component.submit();

      expect(component.onError).toBeFalsy();
    });
  });

  describe('Login Error', () => {
    beforeEach(() => {
      (authService.login as jest.Mock).mockReturnValue(
        throwError(() => new Error('Invalid credentials'))
      );
    });

    it('should set onError to true when login fails', () => {
      component.form.setValue({
        email: 'wrong@example.com',
        password: 'wrongpassword'
      });

      component.submit();

      expect(component.onError).toBeTruthy();
    });

    it('should not call sessionService.logIn when login fails', () => {
      component.form.setValue({
        email: 'wrong@example.com',
        password: 'wrongpassword'
      });

      component.submit();

      expect(sessionService.logIn).not.toHaveBeenCalled();
    });

    it('should not navigate when login fails', () => {
      component.form.setValue({
        email: 'wrong@example.com',
        password: 'wrongpassword'
      });

      component.submit();

      expect(router.navigate).not.toHaveBeenCalled();
    });

    it('should handle 401 Unauthorized error', () => {
      (authService.login as jest.Mock).mockReturnValue(
        throwError(() => ({ status: 401, message: 'Unauthorized' }))
      );

      component.form.setValue({
        email: 'test@example.com',
        password: 'wrongpassword'
      });

      component.submit();

      expect(component.onError).toBeTruthy();
    });
  });

  describe('UI Behavior', () => {
    it('should initialize with hide password set to true', () => {
      expect(component.hide).toBeTruthy();
    });

    it('should initialize with onError set to false', () => {
      expect(component.onError).toBeFalsy();
    });
  });
});
