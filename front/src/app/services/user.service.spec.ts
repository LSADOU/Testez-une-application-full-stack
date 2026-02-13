import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';
import { User } from '../interfaces/user.interface';

import { UserService } from './user.service';

describe('UserService', () => {
  let service: UserService;
  let httpMock: HttpTestingController;

  const mockUser: User = {
    id: 1,
    email: 'loic.sadou@example.com',
    firstName: 'Loic',
    lastName: 'Sadou',
    admin: false,
    password: 'mdploic123',
    createdAt: new Date('2024-01-01'),
    updatedAt: new Date('2024-01-10')
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports:[
        HttpClientTestingModule
      ]
    });
    service = TestBed.inject(UserService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('getById', () => {
    it('should return user by id', () => {
      const userId = '1';

      service.getById(userId).subscribe((user) => {
        expect(user).toEqual(mockUser);
        expect(user.email).toBe('loic.sadou@example.com');
      });

      const req = httpMock.expectOne(`api/user/${userId}`);
      expect(req.request.method).toBe('GET');
      req.flush(mockUser);
    });
  });

  describe('delete', () => {
    it('should delete user by id', () => {
      const userId = '1';

      service.delete(userId).subscribe((response) => {
        expect(response).toBeNull();
      });

      const req = httpMock.expectOne(`api/user/${userId}`);
      expect(req.request.method).toBe('DELETE');
      req.flush(null);
    });
  });
});
