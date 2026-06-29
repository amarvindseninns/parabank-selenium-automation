@parabank @regression
Feature: ParaBank user registration and login

  Background:
    Given the user is on the ParaBank home page

  @TC_001 @smoke @positive @e2e @registration @login
  Scenario: TC_001 Register a new user and login successfully
    When the user register as a new ParaBank user
    Then the user should see successful registration message
    When the user logout from ParaBank
    And the user login with the newly created user credentials
    Then the user should be logged in successfully
    And the user print the account amount displayed after login

  @TC_002 @negative @validation @registration
  Scenario: TC_002 Registration with missing mandatory details should fail
    When the user submit the registration form without mandatory details
    Then the user should see registration validation errors

  @TC_003 @negative @edge @validation @registration
  Scenario: TC_003 Registration with duplicate username should fail
    When the user register as a new ParaBank user
    Then the user should see successful registration message
    When the user logout from ParaBank
    And the user try to register again with the same username
    Then the user should see duplicate username error message


#  Website https://parabank.parasoft.com/parabank/overview.htm is accpeting any username password combination as credentials.
#  Hence unable to validate invalid credentials use case

#  @TC_004 @negative @validation @login
#  Scenario: TC_004 Login with invalid credentials should fail
#    When the user login with configured invalid credentials
#    Then the user should see login error message
