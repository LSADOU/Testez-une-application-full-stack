import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { expect } from '@jest/globals';
import { of } from 'rxjs';
import { SessionService } from 'src/app/services/session.service';
import { TeacherService } from 'src/app/services/teacher.service';
import { SessionApiService } from '../../services/session-api.service';
import { Session } from '../../interfaces/session.interface';
import { Teacher } from 'src/app/interfaces/teacher.interface';

import { FormComponent } from './form.component';

describe('FormComponent', () => {
  let component: FormComponent;
  let fixture: ComponentFixture<FormComponent>;
  let sessionApiService: SessionApiService;
  let teacherService: TeacherService;
  let matSnackBar: MatSnackBar;
  let router: Router;

  const mockSession: Session = {
    id: 1,
    name: 'Existing Session',
    description: 'Existing description',
    date: new Date('2024-01-15'),
    teacher_id: 1,
    users: [1, 2],
    createdAt: new Date('2024-01-01'),
    updatedAt: new Date('2024-01-10')
  };

  const mockTeachers: Teacher[] = [
    { id: 1, firstName: 'Marie', lastName: 'Dupont', createdAt: new Date(), updatedAt: new Date() },
    { id: 2, firstName: 'Jean', lastName: 'Martin', createdAt: new Date(), updatedAt: new Date() }
  ];

  const mockSessionService = {
    sessionInformation: {
      admin: true,
      id: 1
    }
  };

  const mockSessionApiService = {
    detail: jest.fn(),
    create: jest.fn(),
    update: jest.fn()
  };

  const mockTeacherService = {
    all: jest.fn().mockReturnValue(of(mockTeachers))
  };

  const mockActivatedRoute = {
    snapshot: {
      paramMap: {
        get: jest.fn().mockReturnValue('1')
      }
    }
  };

  const mockRouter = {
    navigate: jest.fn(),
    url: '/sessions/create'
  };

  const mockMatSnackBar = {
    open: jest.fn()
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        HttpClientModule,
        MatCardModule,
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
        ReactiveFormsModule,
        MatSnackBarModule,
        MatSelectModule,
        BrowserAnimationsModule
      ],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        { provide: SessionApiService, useValue: mockSessionApiService },
        { provide: TeacherService, useValue: mockTeacherService },
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
        { provide: Router, useValue: mockRouter },
        { provide: MatSnackBar, useValue: mockMatSnackBar }
      ],
      declarations: [FormComponent]
    })
      .compileComponents();

    sessionApiService = TestBed.inject(SessionApiService);
    teacherService = TestBed.inject(TeacherService);
    matSnackBar = TestBed.inject(MatSnackBar);
    router = TestBed.inject(Router);
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  describe('Create mode', () => {
    beforeEach(() => {
      mockRouter.url = '/sessions/create';
      fixture = TestBed.createComponent(FormComponent);
      component = fixture.componentInstance;
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should set onUpdate to false in create mode', () => {
      expect(component.onUpdate).toBe(false);
    });

    it('should initialize empty form in create mode', () => {
      expect(component.sessionForm).toBeDefined();
      expect(component.sessionForm?.value.name).toBe('');
      expect(component.sessionForm?.value.description).toBe('');
    });

    it('should load teachers on initialization', () => {
      expect(teacherService.all).toHaveBeenCalled();
    });

    it('should have required validators on form fields', () => {
      const form = component.sessionForm!;

      expect(form.get('name')?.hasError('required')).toBe(true);
      expect(form.get('date')?.hasError('required')).toBe(true);
      expect(form.get('teacher_id')?.hasError('required')).toBe(true);
      expect(form.get('description')?.hasError('required')).toBe(true);
    });

    it('should call create on submit in create mode', () => {
      mockSessionApiService.create.mockReturnValue(of(mockSession));

      component.sessionForm?.setValue({
        name: 'New Session',
        date: '2024-02-01',
        teacher_id: 1,
        description: 'New description'
      });

      component.submit();

      expect(sessionApiService.create).toHaveBeenCalled();
    });

    it('should display success message and redirect after creation', () => {
      mockSessionApiService.create.mockReturnValue(of(mockSession));

      component.sessionForm?.setValue({
        name: 'New Session',
        date: '2024-02-01',
        teacher_id: 1,
        description: 'New description'
      });

      component.submit();

      expect(matSnackBar.open).toHaveBeenCalledWith('Session created !', 'Close', { duration: 3000 });
      expect(router.navigate).toHaveBeenCalledWith(['sessions']);
    });
  });

  describe('Update mode', () => {
    beforeEach(() => {
      mockRouter.url = '/sessions/update/1';
      mockSessionApiService.detail.mockReturnValue(of(mockSession));
      fixture = TestBed.createComponent(FormComponent);
      component = fixture.componentInstance;
      fixture.detectChanges();
    });

    it('should set onUpdate to true in update mode', () => {
      expect(component.onUpdate).toBe(true);
    });

    it('should load existing session in update mode', () => {
      expect(sessionApiService.detail).toHaveBeenCalledWith('1');
    });

    it('should pre-fill form with existing session data', () => {
      expect(component.sessionForm?.value.name).toBe('Existing Session');
      expect(component.sessionForm?.value.description).toBe('Existing description');
      expect(component.sessionForm?.value.teacher_id).toBe(1);
    });

    it('should call update on submit in update mode', () => {
      mockSessionApiService.update.mockReturnValue(of(mockSession));

      component.sessionForm?.patchValue({
        name: 'Updated Session'
      });

      component.submit();

      expect(sessionApiService.update).toHaveBeenCalledWith('1', expect.any(Object));
    });

    it('should display success message and redirect after update', () => {
      mockSessionApiService.update.mockReturnValue(of(mockSession));

      component.submit();

      expect(matSnackBar.open).toHaveBeenCalledWith('Session updated !', 'Close', { duration: 3000 });
      expect(router.navigate).toHaveBeenCalledWith(['sessions']);
    });
  });

  describe('Admin protection', () => {
    it('should redirect non-admin users to sessions list', () => {
      mockSessionService.sessionInformation.admin = false;
      mockRouter.url = '/sessions/create';

      fixture = TestBed.createComponent(FormComponent);
      component = fixture.componentInstance;
      fixture.detectChanges();

      expect(router.navigate).toHaveBeenCalledWith(['/sessions']);
    });

    it('should allow admin users to access form', () => {
      mockSessionService.sessionInformation.admin = true;
      mockRouter.url = '/sessions/create';
      mockRouter.navigate.mockClear();

      fixture = TestBed.createComponent(FormComponent);
      component = fixture.componentInstance;
      fixture.detectChanges();

      expect(router.navigate).not.toHaveBeenCalledWith(['/sessions']);
    });
  });
});
