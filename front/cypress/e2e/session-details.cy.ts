describe('Session detail spec', () => {
  const mockSession = {
    id: 1,
    name: 'Morning Yoga',
    description: 'A relaxing morning yoga session',
    date: '2025-01-15T10:00:00',
    teacher_id: 1,
    users: [2, 3],
    createdAt: '2025-01-01T00:00:00',
    updatedAt: '2025-01-10T00:00:00'
  };

  const mockTeacher = {
    id: 1,
    firstName: 'Jean',
    lastName: 'Dupont',
    createdAt: '2024-01-01T00:00:00',
    updatedAt: '2024-01-01T00:00:00'
  };

  function loginAndNavigateToDetail(admin: boolean, userId: number) {
    cy.intercept('POST', '/api/auth/login', {
      body: {
        id: userId,
        username: 'yoga@studio.com',
        firstName: 'Test',
        lastName: 'User',
        admin: admin,
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

    cy.intercept('GET', '/api/teacher/1', {
      body: mockTeacher
    }).as('getTeacher');

    cy.visit('/login');
    cy.get('input[formControlName=email]').type('yoga@studio.com');
    cy.get('input[formControlName=password]').type(`${"test!1234"}{enter}{enter}`);

    cy.url().should('include', '/sessions');
    cy.get('.items .item').first().find('button').contains('Detail').click();

    cy.wait('@getSession');
    cy.wait('@getTeacher');
  }

  describe('Session information display', () => {
    beforeEach(() => {
      loginAndNavigateToDetail(true, 1);
    });

    it('should display session name', () => {
      cy.get('h1').should('contain', 'Morning Yoga');
    });

    it('should display teacher name', () => {
      cy.get('mat-card-subtitle').should('contain', 'Jean DUPONT');
    });

    it('should display number of attendees', () => {
      cy.get('mat-card-content').should('contain', '2 attendees');
    });

    it('should display session description', () => {
      cy.get('.description').should('contain', 'A relaxing morning yoga session');
    });
  });

  describe('As admin user', () => {
    beforeEach(() => {
      loginAndNavigateToDetail(true, 1);
    });

    it('should display the Delete button', () => {
      cy.get('button').contains('Delete').should('be.visible');
    });

    it('should NOT display the Participate button', () => {
      cy.get('button').contains('Participate').should('not.exist');
    });

    it('should NOT display the Do not participate button', () => {
      cy.get('button').contains('Do not participate').should('not.exist');
    });

    it('should delete session and redirect to sessions list', () => {
      cy.intercept('DELETE', '/api/session/1', {
        statusCode: 200,
        body: {}
      }).as('deleteSession');

      cy.get('button').contains('Delete').click();

      cy.wait('@deleteSession');
      cy.url().should('include', '/sessions');
    });
  });

  describe('As non-admin user not participating', () => {
    beforeEach(() => {
      loginAndNavigateToDetail(false, 5);
    });

    it('should NOT display the Delete button', () => {
      cy.get('button').contains('Delete').should('not.exist');
    });

    it('should display the Participate button', () => {
      cy.get('button').contains('Participate').should('be.visible');
    });

    it('should allow user to participate', () => {
      const sessionAfterParticipation = { ...mockSession, users: [2, 3, 5] };

      cy.intercept('POST', '/api/session/1/participate/5', {
        statusCode: 200,
        body: null
      }).as('participate');

      cy.intercept('GET', '/api/session/1', {
        body: sessionAfterParticipation
      });

      cy.get('button').contains('Participate').click();

      cy.wait('@participate');
      cy.get('mat-card-content').should('contain', '3 attendees');
    });
  });

  describe('As non-admin user already participating', () => {
    beforeEach(() => {
      loginAndNavigateToDetail(false, 2);
    });

    it('should NOT display the Delete button', () => {
      cy.get('button').contains('Delete').should('not.exist');
    });

    it('should display the Do not participate button', () => {
      cy.get('button').contains('Do not participate').should('be.visible');
    });

    it('should allow user to unparticipate', () => {
      const sessionAfterUnparticipation = { ...mockSession, users: [3] };

      cy.intercept('DELETE', '/api/session/1/participate/2', {
        statusCode: 200,
        body: null
      }).as('unParticipate');

      cy.intercept('GET', '/api/session/1', {
        body: sessionAfterUnparticipation
      });

      cy.get('button').contains('Do not participate').click();

      cy.wait('@unParticipate');
      cy.get('mat-card-content').should('contain', '1 attendees');
    });
  });
});
