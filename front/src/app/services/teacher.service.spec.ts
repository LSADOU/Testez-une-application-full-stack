import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';
import { Teacher } from '../interfaces/teacher.interface';

import { TeacherService } from './teacher.service';

describe('TeacherService', () => {
  let service: TeacherService;
  let httpMock: HttpTestingController;

  const mockTeachers: Teacher[] = [
    { id: 1, firstName: 'Marie', lastName: 'Dupont', createdAt: new Date(), updatedAt: new Date() },
    { id: 2, firstName: 'Jean', lastName: 'Martin', createdAt: new Date(), updatedAt: new Date() }
  ];

  const mockTeacher: Teacher = {
    id: 1,
    firstName: 'Marie',
    lastName: 'Dupont',
    createdAt: new Date(),
    updatedAt: new Date()
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports:[
        HttpClientTestingModule
      ]
    });
    service = TestBed.inject(TeacherService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('all', () => {
    it('should return all teachers', () => {
      service.all().subscribe((teachers) => {
        expect(teachers).toEqual(mockTeachers);
        expect(teachers.length).toBe(2);
      });

      const req = httpMock.expectOne('api/teacher');
      expect(req.request.method).toBe('GET');
      req.flush(mockTeachers);
    });
  });

  describe('detail', () => {
    it('should return teacher by id', () => {
      const teacherId = '1';

      service.detail(teacherId).subscribe((teacher) => {
        expect(teacher).toEqual(mockTeacher);
        expect(teacher.firstName).toBe('Marie');
      });

      const req = httpMock.expectOne(`api/teacher/${teacherId}`);
      expect(req.request.method).toBe('GET');
      req.flush(mockTeacher);
    });
  });
});
