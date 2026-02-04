@smoke
Feature: Search and open a movie in BookMyShow

  Scenario Outline: Search "<movie>" in "<city>" and validate details
    Given I the BookMyShow home page
    When I select city "<city>"
    And I search for movie "<movie>"
    Then I should see results containing "<movie>"
    When I open the first result
    Then the movie details page should show title containing "<movie>"

    Examples:
      | city    | movie     |
      | Chennai | Avatar    |
