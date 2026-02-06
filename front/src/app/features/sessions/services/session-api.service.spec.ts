import { HttpClientModule } from '@angular/common/http';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';

import { SessionApiService } from './session-api.service';
import { Session } from '../interfaces/session.interface';

describe('SessionsService', () => {
  let service: SessionApiService;
  let httpMock: HttpTestingController;

  const mockSession: Session = {
    id: 1,
    name: 'Yoga Session',
    description: 'A relaxing yoga session',
    date: new Date('2024-01-15'),
    teacher_id: 1,
    users: [1, 2, 3],
    createdAt: new Date('2024-01-01'),
    updatedAt: new Date('2024-01-10')
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule
      ],
      providers: [SessionApiService]
    });
    service = TestBed.inject(SessionApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('all', () => {
    it('should retrieve all sessions via GET', () => {
      const mockSessions: Session[] = [mockSession];

      service.all().subscribe((sessions: Session[]) => {
        expect(sessions).toEqual(mockSessions);
        expect(sessions.length).toBe(1);
      });

      const req = httpMock.expectOne('api/session');
      expect(req.request.method).toBe('GET');
      req.flush(mockSessions);
    });
  });

  describe('detail', () => {
    it('should retrieve a session by id via GET', () => {
      const sessionId = '1';

      service.detail(sessionId).subscribe((session: Session) => {
        expect(session).toEqual(mockSession);
        expect(session.id).toBe(1);
      });

      const req = httpMock.expectOne(`api/session/${sessionId}`);
      expect(req.request.method).toBe('GET');
      req.flush(mockSession);
    });
  });

  describe('delete', () => {
    it('should delete a session by id via DELETE', () => {
      const sessionId = '1';

      service.delete(sessionId).subscribe((response) => {
        expect(response).toEqual({});
      });

      const req = httpMock.expectOne(`api/session/${sessionId}`);
      expect(req.request.method).toBe('DELETE');
      req.flush({});
    });
  });

  describe('create', () => {
    it('should create a new session via POST', () => {
      const newSession: Session = {
        name: 'New Yoga Session',
        description: 'Brand new session',
        date: new Date('2024-02-01'),
        teacher_id: 2,
        users: []
      } as Session;

      service.create(newSession).subscribe((session: Session) => {
        expect(session).toEqual(mockSession);
      });

      const req = httpMock.expectOne('api/session');
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(newSession);
      req.flush(mockSession);
    });
  });

  describe('update', () => {
    it('should update a session via PUT', () => {
      const sessionId = '1';
      const updatedSession: Session = {
        ...mockSession,
        name: 'Updated Yoga Session'
      };

      service.update(sessionId, updatedSession).subscribe((session: Session) => {
        expect(session).toEqual(updatedSession);
        expect(session.name).toBe('Updated Yoga Session');
      });

      const req = httpMock.expectOne(`api/session/${sessionId}`);
      expect(req.request.method).toBe('PUT');
      expect(req.request.body).toEqual(updatedSession);
      req.flush(updatedSession);
    });
  });

  describe('participate', () => {
    it('should add a user to a session via POST', () => {
      const sessionId = '1';
      const userId = '5';

      service.participate(sessionId, userId).subscribe((response) => {
        expect(response).toBeNull();
      });

      const req = httpMock.expectOne(`api/session/${sessionId}/participate/${userId}`);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toBeNull();
      req.flush(null);
    });
  });

  describe('unParticipate', () => {
    it('should remove a user from a session via DELETE', () => {
      const sessionId = '1';
      const userId = '5';

      service.unParticipate(sessionId, userId).subscribe((response) => {
        expect(response).toBeNull();
      });

      const req = httpMock.expectOne(`api/session/${sessionId}/participate/${userId}`);
      expect(req.request.method).toBe('DELETE');
      req.flush(null);
    });
  });
});
