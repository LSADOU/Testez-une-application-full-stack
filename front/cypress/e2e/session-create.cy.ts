describe('Session create spec', () => {
  const mockSessions = [
    {
      id: 1,
      name: 'Morning Yoga',
      description: 'A relaxing morning yoga session',
      date: '2025-01-15T10:00:00',
      teacher_id: 1,
      users: [],
      createdAt: '2025-01-01T00:00:00',
      updatedAt: '2025-01-01T00:00:00'
    }
  ];

  const mockTeachers = [
    { id: 1, firstName: 'Jean', lastName: 'Dupont', createdAt: '2024-01-01T00:00:00', updatedAt: '2024-01-01T00:00:00' },
    { id: 2, firstName: 'Marie', lastName: 'Martin', createdAt: '2024-01-01T00:00:00', updatedAt: '2024-01-01T00:00:00' }
  ];

  function loginAs(admin: boolean) {
    cy.intercept('POST', '/api/auth/login', {
      body: {
        id: 1,
        username: 'yoga@studio.com',
        firstName: 'Admin',
        lastName: 'User',
        admin: admin,
        token: 'fake-jwt-token',
        type: 'Bearer'
      }
    });

    cy.intercept('GET', '/api/session', {
      body: mockSessions
    });

    cy.intercept('GET', '/api/teacher', {
      body: mockTeachers
    });

    cy.visit('/login');
    cy.get('input[formControlName=email]').type('yoga@studio.com');
    cy.get('input[formControlName=password]').type(`${"test!1234"}{enter}{enter}`);

    cy.url().should('include', '/sessions');
  }

  describe('Access control', () => {
    it('should allow admin user to access create page', () => {
      loginAs(true);

      cy.get('button[routerLink=create]').click();
      cy.url().should('include', '/sessions/create');
      cy.get('h1').should('contain', 'Create session');
    });
  });

  describe('Form validation', () => {
    beforeEach(() => {
      loginAs(true);
      cy.get('button[routerLink=create]').click();
    });

    it('should disable submit button when form is empty', () => {
      cy.get('button[type=submit]').should('be.disabled');
    });

    it('should enable submit button when all fields are filled', () => {
      cy.get('input[formControlName=name]').type('New Yoga Session');
      cy.get('input[formControlName=date]').type('2025-03-01');
      cy.get('mat-select[formControlName=teacher_id]').click();
      cy.get('mat-option').first().click();
      cy.get('textarea[formControlName=description]').type('A new session');

      cy.get('button[type=submit]').should('not.be.disabled');
    });
  });

  describe('Session creation', () => {
    beforeEach(() => {
      loginAs(true);
      cy.get('button[routerLink=create]').click();
    });

    it('should create a session and redirect to sessions list', () => {
      cy.intercept('POST', '/api/session', {
        statusCode: 200,
        body: {
          id: 2,
          name: 'New Yoga Session',
          description: 'A brand new yoga session',
          date: '2025-03-01T00:00:00',
          teacher_id: 1,
          users: []
        }
      }).as('createSession');

      cy.get('input[formControlName=name]').type('New Yoga Session');
      cy.get('input[formControlName=date]').type('2025-03-01');
      cy.get('mat-select[formControlName=teacher_id]').click();
      cy.get('mat-option').first().click();
      cy.get('textarea[formControlName=description]').type('A brand new yoga session');

      cy.get('button[type=submit]').click();

      cy.wait('@createSession');
      cy.url().should('include', '/sessions');
    });
  });
});
