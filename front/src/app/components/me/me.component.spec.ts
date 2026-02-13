import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { expect } from '@jest/globals';
import { of } from 'rxjs';
import { SessionService } from 'src/app/services/session.service';
import { UserService } from 'src/app/services/user.service';
import { User } from 'src/app/interfaces/user.interface';

import { MeComponent } from './me.component';

describe('MeComponent', () => {
  let component: MeComponent;
  let fixture: ComponentFixture<MeComponent>;
  let userService: UserService;
  let sessionService: SessionService;
  let router: Router;
  let matSnackBar: MatSnackBar;

  const mockUser: User = {
    id: 1,
    email: 'test@example.com',
    lastName: 'Sadou',
    firstName: 'Loic',
    admin: false,
    password: 'mdploic123',
    createdAt: new Date('2024-01-01'),
    updatedAt: new Date('2024-01-10')
  };

  const mockSessionService = {
    sessionInformation: {
      admin: true,
      id: 1
    },
    logOut: jest.fn()
  };

  const mockUserService = {
    getById: jest.fn(),
    delete: jest.fn()
  };

  const mockRouter = {
    navigate: jest.fn()
  };

  const mockMatSnackBar = {
    open: jest.fn()
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MeComponent],
      imports: [
        MatSnackBarModule,
        HttpClientModule,
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule
      ],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        { provide: UserService, useValue: mockUserService },
        { provide: Router, useValue: mockRouter },
        { provide: MatSnackBar, useValue: mockMatSnackBar }
      ],
    })
      .compileComponents();

    fixture = TestBed.createComponent(MeComponent);
    component = fixture.componentInstance;
    userService = TestBed.inject(UserService);
    sessionService = TestBed.inject(SessionService);
    router = TestBed.inject(Router);
    matSnackBar = TestBed.inject(MatSnackBar);
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('ngOnInit', () => {
    it('should load user information on initialization', () => {
      mockUserService.getById.mockReturnValue(of(mockUser));

      component.ngOnInit();

      expect(userService.getById).toHaveBeenCalledWith('1');
    });

    it('should store user data in component.user', () => {
      mockUserService.getById.mockReturnValue(of(mockUser));

      component.ngOnInit();

      expect(component.user).toEqual(mockUser);
    });

    it('should use session information id to fetch user', () => {
      mockUserService.getById.mockReturnValue(of(mockUser));

      component.ngOnInit();

      expect(userService.getById).toHaveBeenCalledWith(
        mockSessionService.sessionInformation.id.toString()
      );
    });
  });

  describe('back', () => {
    it('should call window.history.back()', () => {
      const backSpy = jest.spyOn(window.history, 'back');

      component.back();

      expect(backSpy).toHaveBeenCalled();
    });
  });

  describe('delete', () => {
    beforeEach(() => {
      mockUserService.delete.mockReturnValue(of(null));
    });

    it('should call userService.delete with user id', () => {
      component.delete();

      expect(userService.delete).toHaveBeenCalledWith('1');
    });

    it('should display a snackbar message after deletion', () => {
      component.delete();

      expect(matSnackBar.open).toHaveBeenCalledWith(
        'Your account has been deleted !',
        'Close',
        { duration: 3000 }
      );
    });

    it('should call sessionService.logOut after deletion', () => {
      component.delete();

      expect(sessionService.logOut).toHaveBeenCalled();
    });

    it('should navigate to home page after deletion', () => {
      component.delete();

      expect(router.navigate).toHaveBeenCalledWith(['/']);
    });

    it('should display user information in the template', () => {
      mockUserService.getById.mockReturnValue(of(mockUser));

      component.ngOnInit();
      fixture.detectChanges();

      const compiled = fixture.nativeElement;

      expect(compiled.textContent).toContain('Loic');
      expect(compiled.textContent).toContain('SADOU');
      expect(compiled.textContent).toContain('test@example.com');
    });
  });
});
