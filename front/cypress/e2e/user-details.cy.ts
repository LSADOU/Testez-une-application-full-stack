describe('Me (User profile) spec', () => {
  const mockUser = {
    id: 1,
    email: 'loic.sadou@example.com',
    firstName: 'Loic',
    lastName: 'Sadou',
    admin: false,
    password: 'mdploic123',
    createdAt: '2024-01-01T00:00:00',
    updatedAt: '2024-01-10T00:00:00'
  };

  function loginAndNavigateToProfile() {
    cy.intercept('POST', '/api/auth/login', {
      body: {
        id: 1,
        username: 'loic.sadou@example.com',
        firstName: 'John',
        lastName: 'Doe',
        admin: false,
        token: 'fake-jwt-token',
        type: 'Bearer'
      }
    });

    cy.intercept('GET', '/api/session', {
      body: []
    });

    cy.intercept('GET', '/api/user/1', {
      body: mockUser
    }).as('getUser');

    cy.visit('/login');
    cy.get('input[formControlName=email]').type('loic.sadou@example.com');
    cy.get('input[formControlName=password]').type(`${"mdploic123"}{enter}{enter}`);

    cy.url().should('include', '/sessions');

    cy.get('[routerLink=me]').click();
    cy.wait('@getUser');
  }

  describe('User information display', () => {
    beforeEach(() => {
      loginAndNavigateToProfile();
    });

    it('should display user first name', () => {
      cy.get('p').should('contain', 'Loic');
    });

    it('should display user last name', () => {
      cy.get('p').should('contain', 'SADOU');
    });

    it('should display user email', () => {
      cy.get('p').should('contain', 'loic.sadou@example.com');
    });

    it('should display creation date', () => {
      cy.get('p').should('contain', 'January 1, 2024');
    });

    it('should display delete button', () => {
      cy.get('button').contains('Detail').should('be.visible');
    });
  });

  describe('Account deletion', () => {
    beforeEach(() => {
      loginAndNavigateToProfile();
    });

    it('should delete account and redirect to home', () => {
      cy.intercept('DELETE', '/api/user/1', {
        statusCode: 200,
        body: {}
      }).as('deleteUser');

      cy.get('button').contains('Detail').click();

      cy.wait('@deleteUser');

      cy.get('.mat-snack-bar-container').should('contain', 'Your account has been deleted !');

      cy.url().should('eq', Cypress.config().baseUrl);
    });
  });

  describe('Navigation', () => {
    beforeEach(() => {
      loginAndNavigateToProfile();
    });

    it('should navigate back when back button is clicked', () => {
      cy.get('button[mat-icon-button]').first().click();

      cy.url().should('include', '/sessions');
    });
  });
});
