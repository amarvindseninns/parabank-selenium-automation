Feature: ParaBank user registration and login

  Background:
    Given I am on the ParaBank home page

  @smoke @positive
  Scenario: Register a new user and login successfully
    When I register as a new ParaBank user
    Then I should see successful registration message
    When I logout from ParaBank
    And I login with the newly created user credentials
    Then I should be logged in successfully
    And I print the account amount displayed after login

  @negative @registration
  Scenario: Registration with missing mandatory details should fail
    When I submit the registration form without mandatory details
    Then I should see registration validation errors

  @edge @registration
  Scenario: Registration with duplicate username should fail
    When I register as a new ParaBank user
    Then I should see successful registration message
    When I logout from ParaBank
    And I try to register again with the same username
    Then I should see duplicate username error message

  @negative @login
  Scenario: Login with invalid credentials should fail
    When I login with username "invalid_user_12345" and password "wrongPassword"
    Then I should see login error message
