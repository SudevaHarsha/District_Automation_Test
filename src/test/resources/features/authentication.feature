@TS_01
Feature: Authentication Validation

  Scenario: Login validation with Excel driven data
	Given I open the District Movies website
    When I enter mobile number "<mobile>"
    And I click continue
    Then login result should be "<result>"

    Examples:
      | TestCaseId | mobile        | result   |
      | TC_AF_001  | 9618415401    | OTP      |
      | TC_AF_002  | 9876543210    | INVALID OTP    |
      | TC_AF_003  | 1234567890    | ERROR    |
      | TC_AF_004  | 961841501     | INVALID  |
      | TC_AF_005  | 96184150111   | INVALID  |
