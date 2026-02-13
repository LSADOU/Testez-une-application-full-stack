describe('Session update spec', () => {
  const mockSession = {
    id: 1,
    name: 'Morning Yoga',
    description: 'A relaxing morning yoga session',
    date: '2025-01-15T10:00:00',
    teacher_id: 1,
    users: [2, 3],
    createdAt: '2025-01-01T00:00:00',
    updatedAt: '2025-01-01T00:00:00'
  };

  const mockTeachers = [
    { id: 1, firstName: 'Jean', lastName: 'Dupont', createdAt: '2024-01-01T00:00:00', updatedAt: '2024-01-01T00:00:00' },
    { id: 2, firstName: 'Marie', lastName: 'Martin', createdAt: '2024-01-01T00:00:00', updatedAt: '2024-01-01T00:00:00' }
  ];

  function loginAndNavigateToUpdate() {
    cy.intercept('POST', '/api/auth/login', {
      body: {
        id: 1,
        username: 'yoga@studio.com',
        firstName: 'Admin',
        lastName: 'User',
        admin: true,
        token: 'fake-jwt-token',
        type: 'Bearer'
      }
    });

    cy.intercept('GET', '/api/session', {
      body: [mockSession]
    });

    cy.intercept('GET', '/api/session/1', {
      body: mockSession
    }).as('getSession');

    cy.intercept('GET', '/api/teacher', {
      body: mockTeachers
    });

    cy.visit('/login');
    cy.get('input[formControlName=email]').type('yoga@studio.com');
    cy.get('input[formControlName=password]').type(`${"test!1234"}{enter}{enter}`);

    cy.url().should('include', '/sessions');
    cy.get('.items .item').first().find('button').contains('Edit').click();

    cy.wait('@getSession');
  }

  it('should display Update session title', () => {
    loginAndNavigateToUpdate();

    cy.get('h1').should('contain', 'Update session');
  });

  it('should pre-fill the form with existing session data', () => {
    loginAndNavigateToUpdate();

    cy.get('input[formControlName=name]').should('have.value', 'Morning Yoga');
    cy.get('input[formControlName=date]').should('have.value', '2025-01-15');
    cy.get('textarea[formControlName=description]').should('have.value', 'A relaxing morning yoga session');
  });

  it('should update session and redirect to sessions list', () => {
    loginAndNavigateToUpdate();

    const updatedSession = { ...mockSession, name: 'Evening Yoga' };

    cy.intercept('PUT', '/api/session/1', {
      statusCode: 200,
      body: updatedSession
    }).as('updateSession');

    cy.intercept('GET', '/api/session', {
      body: [updatedSession]
    });

    cy.get('input[formControlName=name]').clear().type('Evening Yoga');

    cy.get('button[type=submit]').click();

    cy.wait('@updateSession').its('request.body').should('have.property', 'name', 'Evening Yoga');
    cy.url().should('include', '/sessions');
    cy.get('.items .item').first().find('mat-card-title').should('contain', 'Evening Yoga');
  });
});
