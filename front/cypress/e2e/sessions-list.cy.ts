describe('Sessions list spec', () => {
  const mockSessions = [
    {
      id: 1,
      name: 'Morning Yoga',
      description: 'A relaxing morning yoga session',
      date: '2025-01-15T10:00:00',
      teacher_id: 1,
      users: [2, 3],
      createdAt: '2025-01-01T00:00:00',
      updatedAt: '2025-01-01T00:00:00'
    },
    {
      id: 2,
      name: 'Power Yoga',
      description: 'An intense power yoga session',
      date: '2025-02-20T14:00:00',
      teacher_id: 1,
      users: [2],
      createdAt: '2025-01-05T00:00:00',
      updatedAt: '2025-01-05T00:00:00'
    }
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
    }).as('getSessions');

    cy.visit('/login');
    cy.get('input[formControlName=email]').type('yoga@studio.com');
    cy.get('input[formControlName=password]').type(`${"test!1234"}{enter}{enter}`);

    cy.url().should('include', '/sessions');
  }

  describe('As admin user', () => {
    beforeEach(() => {
      loginAs(true);
    });

    it('should display the list of sessions', () => {
      cy.get('.items .item').should('have.length', 2);
    });

    it('should display session name, date and description', () => {
      cy.get('.items .item').first().within(() => {
        cy.get('mat-card-title').should('contain', 'Morning Yoga');
        cy.get('mat-card-subtitle').should('contain', 'January 15, 2025');
        cy.get('mat-card-content').should('contain', 'A relaxing morning yoga session');
      });
    });

    it('should display the Create button', () => {
      cy.get('button[routerLink=create]').should('be.visible');
      cy.get('button[routerLink=create]').should('contain', 'Create');
    });

    it('should display Detail button on each session', () => {
      cy.get('.items .item').each(() => {
        cy.get('button').contains('Detail').should('be.visible');
      });
    });

    it('should display Edit button on each session', () => {
      cy.get('.items .item').each(($card) => {
        cy.wrap($card).find('button').contains('Edit').should('be.visible');
      });
    });
  });

  describe('As non-admin user', () => {
    beforeEach(() => {
      loginAs(false);
    });

    it('should display the list of sessions', () => {
      cy.get('.items .item').should('have.length', 2);
    });

    it('should NOT display the Create button', () => {
      cy.get('button[routerLink=create]').should('not.exist');
    });

    it('should display Detail button on each session', () => {
      cy.get('.items .item').each(($card) => {
        cy.wrap($card).find('button').contains('Detail').should('be.visible');
      });
    });

    it('should NOT display Edit button', () => {
      cy.get('.items .item').each(($card) => {
        cy.wrap($card).find('button').contains('Edit').should('not.exist');
      });
    });
  });
});
