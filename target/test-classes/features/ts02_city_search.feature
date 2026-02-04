@TS_02 @smoke
Feature: City selection and movie search
 
  #
  # -------- City validations (from your manual TS_02) --------
  #
  # method:
  #   display         -> just verify current city label is present
  #   search          -> open city modal, type city and select
  #   currentLocation -> click "Use current location"
  #   popular         -> choose city from Popular section
  #   allCities       -> pick letter & city from full list
  #
  # expected:
  #   city name (e.g., Chennai) or the keyword ERROR for invalid case
  #
  Scenario Outline: City selection via "<method>"
    Given I the District Movies website
    When I perform city selection using "<method>" and "<city>"
    Then the home page city should be "<method>" and "<expected>"
 
    Examples:
      | TestCaseId | method          | city     | expected |
      | TC_SF_01   | display         |          |          |
      | TC_SF_02   | search          | Chennai | Chennai |
      | TC_SF_03   | searchError     | kplou | ERROR |
      | TC_SF_04   | currentLocation |          | Chennai |
	  | TC_SF_05   | popular         | Chennai | Chennai |
      | TC_SF_06   | allCities       | Chennai | Chennai |
 
  #
  # -------- Search validations (from your manual TS_02) --------
  #
  # expected:
  #   BAR        -> search bar visible & clickable
  #   SUGGEST    -> suggestions shown
  #   RESULTS    -> results/theatres shown after selecting suggestion
  #   NO_RESULT  -> "no results" message for invalid movie
  #   TRENDING   -> selecting from trending leads to results
  #
  Scenario Outline: Search flow for "<movie>"
    Given I am on the District Movies home page
    When I validate search for "<movie>"
    Then search outcome for "<movie>" should be "<expected>"
 
    Examples:
      | TestCaseId | movie                 | expected   |
      | TC_SF_07   | (bar visibility)      | BAR        |
      | TC_SF_08   | A | SUGGEST |
      | TC_SF_09   | Avatar Fire and Ash | RESULTS |
      | TC_SF_10   | Avuty | NO_RESULT |
      | TC_SF_11   | Avatar Fire and Ash | TRENDING |
 