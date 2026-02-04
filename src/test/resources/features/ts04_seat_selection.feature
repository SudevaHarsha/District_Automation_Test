@TS_04 @seat @regression
Feature: Seat selection flow on District Movies

  Scenario Outline: Apply filters, open first show, and validate seat selection
    Given District movie booking page "<city>" and "<movie>"
    And I open the first available showtime
    Then I should be able to select "<availableCount>" available seats
    And I should not be able to select "<unavailableCount>" unavailable seats
    When I click Proceed
    Then I should reach booking summary or next step

    Examples:
      | TestCaseId | movie  | city     | availableCount | unavailableCount |
      | TC_SB_001  | Avatar | Chennai  | 2              | 2                |