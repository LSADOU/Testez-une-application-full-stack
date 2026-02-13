describe('Register spec', () => {
  beforeEach(() => {
    cy.visit('/register')
  })

  it('should display registration form', () => {
    cy.get('input[formControlName=firstName]').should('be.visible')
    cy.get('input[formControlName=lastName]').should('be.visible')
    cy.get('input[formControlName=email]').should('be.visible')
    cy.get('input[formControlName=password]').should('be.visible')
    cy.get('button[type=submit]').should('be.visible')
  })

  it('should register successfully', () => {
    cy.intercept('POST', '/api/auth/register', {
      statusCode: 200,
      body: null
    }).as('registerRequest')

    cy.get('input[formControlName=firstName]').type('Loic')
    cy.get('input[formControlName=lastName]').type('Sadou')
    cy.get('input[formControlName=email]').type('loic.sadou@example.com')
    cy.get('input[formControlName=password]').type('mdploic123{enter}')

    cy.wait('@registerRequest').its('request.body').should('deep.equal', {
      firstName: 'Loic',
      lastName: 'Sadou',
      email: 'loic.sadou@example.com',
      password: 'mdploic123'
    })

    cy.url().should('include', '/login')
  })

  it('should show error when registration fails', () => {
    cy.intercept('POST', '/api/auth/register', {
      statusCode: 400,
      body: { message: 'Email already exists' }
    })

    cy.get('input[formControlName=firstName]').type('Loic')
    cy.get('input[formControlName=lastName]').type('Sadou')
    cy.get('input[formControlName=email]').type('existing@example.com')
    cy.get('input[formControlName=password]').type('mdploic123{enter}')

    cy.get('.error').should('be.visible')
    cy.url().should('include', '/register')
  })

  it('should disable submit button when form is invalid', () => {
    cy.get('button[type=submit]').should('be.disabled')

    cy.get('input[formControlName=email]').type('test@example.com')
    cy.get('button[type=submit]').should('be.disabled')

    cy.get('input[formControlName=firstName]').type('Loic')
    cy.get('button[type=submit]').should('be.disabled')

    cy.get('input[formControlName=lastName]').type('Sadou')
    cy.get('button[type=submit]').should('be.disabled')

    cy.get('input[formControlName=password]').type('mdploic123')
    cy.get('button[type=submit]').should('not.be.disabled')
  })

  it('should complete registration flow and redirect to login', () => {
    cy.intercept('POST', '/api/auth/register', {
      statusCode: 200
    })

    cy.get('input[formControlName=firstName]').type('Jane')
    cy.get('input[formControlName=lastName]').type('Smith')
    cy.get('input[formControlName=email]').type('jane.smith@yoga.com')
    cy.get('input[formControlName=password]').type('SecurePass123!')

    cy.get('button[type=submit]').click()

    cy.url().should('include', '/login')
  })
})
