import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';

import { SessionService } from './session.service';
import { SessionInformation } from '../interfaces/sessionInformation.interface';

describe('SessionService', () => {
  let service: SessionService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SessionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('Initial state', () => {
    it('should have isLogged set to false initially', () => {
      expect(service.isLogged).toBe(false);
    });

    it('should have sessionInformation undefined initially', () => {
      expect(service.sessionInformation).toBeUndefined();
    });
  });

  describe('logIn', () => {
    it('should set isLogged to true when user logs in', () => {
      const mockUser: SessionInformation = {
        token: 'fake-jwt-token',
        type: 'Bearer',
        id: 1,
        username: 'test@example.com',
        firstName: 'John',
        lastName: 'Doe',
        admin: false
      };

      service.logIn(mockUser);

      expect(service.isLogged).toBe(true);
    });

    it('should store user information when user logs in', () => {
      const mockUser: SessionInformation = {
        token: 'fake-jwt-token',
        type: 'Bearer',
        id: 1,
        username: 'test@example.com',
        firstName: 'John',
        lastName: 'Doe',
        admin: false
      };

      service.logIn(mockUser);

      expect(service.sessionInformation).toEqual(mockUser);
    });

    it('should emit true via $isLogged() observable when user logs in', (done) => {
      const mockUser: SessionInformation = {
        token: 'fake-jwt-token',
        type: 'Bearer',
        id: 1,
        username: 'test@example.com',
        firstName: 'John',
        lastName: 'Doe',
        admin: false
      };

      service.$isLogged().subscribe((isLogged: boolean) => {
        if (isLogged) {
          expect(isLogged).toBe(true);
          done();
        }
      });

      service.logIn(mockUser);
    });
  });

  describe('logOut', () => {
    beforeEach(() => {
      // Set up an authenticated state first
      const mockUser: SessionInformation = {
        token: 'fake-jwt-token',
        type: 'Bearer',
        id: 1,
        username: 'test@example.com',
        firstName: 'John',
        lastName: 'Doe',
        admin: false
      };
      service.logIn(mockUser);
    });

    it('should set isLogged to false when user logs out', () => {
      service.logOut();

      expect(service.isLogged).toBe(false);
    });

    it('should clear sessionInformation when user logs out', () => {
      service.logOut();

      expect(service.sessionInformation).toBeUndefined();
    });

    it('should emit false via $isLogged() observable when user logs out', (done) => {
      let emissionCount = 0;

      service.$isLogged().subscribe((isLogged: boolean) => {
        emissionCount++;
        // Skip the first emission (from logIn in beforeEach)
        if (emissionCount === 2) {
          expect(isLogged).toBe(false);
          done();
        }
      });

      service.logOut();
    });
  });

  describe('$isLogged Observable', () => {
    it('should return an Observable', () => {
      const observable = service.$isLogged();

      expect(observable).toBeDefined();
      expect(typeof observable.subscribe).toBe('function');
    });

    it('should emit current isLogged state to new subscribers', (done) => {
      const mockUser: SessionInformation = {
        token: 'fake-jwt-token',
        type: 'Bearer',
        id: 1,
        username: 'test@example.com',
        firstName: 'John',
        lastName: 'Doe',
        admin: false
      };

      service.logIn(mockUser);

      // New subscription after logIn
      service.$isLogged().subscribe((isLogged: boolean) => {
        expect(isLogged).toBe(true);
        done();
      });
    });

    it('should emit state changes to all subscribers', (done) => {
      const emissions: boolean[] = [];
      const mockUser: SessionInformation = {
        token: 'fake-jwt-token',
        type: 'Bearer',
        id: 1,
        username: 'test@example.com',
        firstName: 'John',
        lastName: 'Doe',
        admin: false
      };

      service.$isLogged().subscribe((isLogged: boolean) => {
        emissions.push(isLogged);

        if (emissions.length === 3) {
          expect(emissions[0]).toBe(false); // Initial state
          expect(emissions[1]).toBe(true);  // After logIn
          expect(emissions[2]).toBe(false); // After logOut
          done();
        }
      });

      service.logIn(mockUser);
      service.logOut();
    });
  });
});
