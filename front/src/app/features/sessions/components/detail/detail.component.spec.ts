import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { expect } from '@jest/globals';
import { of } from 'rxjs';
import { SessionService } from '../../../../services/session.service';
import { TeacherService } from '../../../../services/teacher.service';
import { SessionApiService } from '../../services/session-api.service';
import { Session } from '../../interfaces/session.interface';
import { Teacher } from '../../../../interfaces/teacher.interface';

import { DetailComponent } from './detail.component';

describe('DetailComponent', () => {
  let component: DetailComponent;
  let fixture: ComponentFixture<DetailComponent>;
  let sessionApiService: SessionApiService;
  let teacherService: TeacherService;
  let matSnackBar: MatSnackBar;
  let router: Router;

  const mockSession: Session = {
    id: 1,
    name: 'Yoga Session',
    description: 'A relaxing yoga session',
    date: new Date('2024-01-15'),
    teacher_id: 1,
    users: [2, 3],
    createdAt: new Date('2024-01-01'),
    updatedAt: new Date('2024-01-10')
  };

  const mockTeacher: Teacher = {
    id: 1,
    firstName: 'Marie',
    lastName: 'Dupont',
    createdAt: new Date('2024-01-01'),
    updatedAt: new Date('2024-01-01')
  };

  const mockSessionService = {
    sessionInformation: {
      admin: false,
      id: 1
    }
  };

  const mockSessionApiService = {
    detail: jest.fn(),
    delete: jest.fn(),
    participate: jest.fn(),
    unParticipate: jest.fn()
  };

  const mockTeacherService = {
    detail: jest.fn()
  };

  const mockActivatedRoute = {
    snapshot: {
      paramMap: {
        get: jest.fn().mockReturnValue('1')
      }
    }
  };

  const mockRouter = {
    navigate: jest.fn()
  };

  const mockMatSnackBar = {
    open: jest.fn()
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        HttpClientModule,
        MatSnackBarModule,
        ReactiveFormsModule
      ],
      declarations: [DetailComponent],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        { provide: SessionApiService, useValue: mockSessionApiService },
        { provide: TeacherService, useValue: mockTeacherService },
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
        { provide: Router, useValue: mockRouter },
        { provide: MatSnackBar, useValue: mockMatSnackBar }
      ],
    })
      .compileComponents();

    sessionApiService = TestBed.inject(SessionApiService);
    teacherService = TestBed.inject(TeacherService);
    matSnackBar = TestBed.inject(MatSnackBar);
    router = TestBed.inject(Router);

    mockSessionApiService.detail.mockReturnValue(of(mockSession));
    mockTeacherService.detail.mockReturnValue(of(mockTeacher));

    fixture = TestBed.createComponent(DetailComponent);
    component = fixture.componentInstance;
  });

  afterEach(() => {
    jest.clearAllMocks();
    mockSessionService.sessionInformation.id = 1;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('ngOnInit', () => {
    it('should load session details on initialization', () => {
      component.ngOnInit();

      expect(sessionApiService.detail).toHaveBeenCalledWith('1');
    });

    it('should load teacher details after session is loaded', () => {
      component.ngOnInit();

      expect(teacherService.detail).toHaveBeenCalledWith('1');
    });

    it('should store session and teacher data', () => {
      component.ngOnInit();

      expect(component.session).toEqual(mockSession);
      expect(component.teacher).toEqual(mockTeacher);
    });

    it('should set isParticipate to false when user is not participating', () => {
      mockSessionService.sessionInformation.id = 5;

      component.ngOnInit();

      expect(component.isParticipate).toBe(false);
    });

    it('should set isParticipate to true when user is participating', () => {
      mockSessionService.sessionInformation.id = 2;

      component.ngOnInit();

      expect(component.isParticipate).toBe(true);
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
      mockSessionApiService.delete.mockReturnValue(of(null));
    });

    it('should call sessionApiService.delete with session id', () => {
      component.delete();

      expect(sessionApiService.delete).toHaveBeenCalledWith('1');
    });

    it('should display a snackbar message after deletion', () => {
      component.delete();

      expect(matSnackBar.open).toHaveBeenCalledWith(
        'Session deleted !',
        'Close',
        { duration: 3000 }
      );
    });

    it('should navigate to sessions list after deletion', () => {
      component.delete();

      expect(router.navigate).toHaveBeenCalledWith(['sessions']);
    });
  });

  describe('participate', () => {
    beforeEach(() => {
      mockSessionApiService.participate.mockReturnValue(of(null));
    });

    it('should call sessionApiService.participate with session and user ids', () => {
      component.participate();

      expect(sessionApiService.participate).toHaveBeenCalledWith('1', '1');
    });

    it('should reload session data after participation', () => {
      component.participate();

      expect(sessionApiService.detail).toHaveBeenCalledWith('1');
    });
  });

  describe('unParticipate', () => {
    beforeEach(() => {
      mockSessionApiService.unParticipate.mockReturnValue(of(null));
    });

    it('should call sessionApiService.unParticipate with session and user ids', () => {
      component.unParticipate();

      expect(sessionApiService.unParticipate).toHaveBeenCalledWith('1', '1');
    });

    it('should reload session data after unparticipation', () => {
      component.unParticipate();

      expect(sessionApiService.detail).toHaveBeenCalledWith('1');
    });
  });

  describe('Admin detection', () => {
    it('should set isAdmin to false for non-admin users', () => {
      mockSessionService.sessionInformation.admin = false;

      const newFixture = TestBed.createComponent(DetailComponent);
      const newComponent = newFixture.componentInstance;

      expect(newComponent.isAdmin).toBe(false);
    });

    it('should set isAdmin to true for admin users', () => {
      mockSessionService.sessionInformation.admin = true;

      const newFixture = TestBed.createComponent(DetailComponent);
      const newComponent = newFixture.componentInstance;

      expect(newComponent.isAdmin).toBe(true);
    });
  });
});
