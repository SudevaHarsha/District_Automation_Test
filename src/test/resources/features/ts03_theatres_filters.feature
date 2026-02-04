@TS_03 @regression
Feature: Theatres list and filters for a selected movie

  # TS_03 / TC_TT_001
  Scenario Outline: Validate theatres are listed for "<movie>" in "<city>"
    Given open the District Movies website
    And I set city to "<city>"
    When I search and open movie "<movie>"
    Then theatres list should be visible

    Examples:
      | TestCaseId | city    | movie   |
      | TC_TT_001  | Chennai | Avatar  |

  # TS_03 / TC_TT_002
  Scenario Outline: Opening theatre details page for "<movie>"
    Given open the District Movies website
    And I set city to "<city>"
    And I search and open movie "<movie>"
    When I open theatre number "<index>"
    Then theatre details page should show name and address

    Examples:
      | TestCaseId | city    | movie  | index |
      | TC_TT_002  | Chennai | Avatar | 1     |

  # TS_03 / TC_TT_003
  Scenario Outline: Showtimes update when date is changed for "<movie>"
    Given open the District Movies website
    And I set city to "<city>"
    And I search and open movie "<movie>"
    When I change show date to "<date>"
    Then showtimes should be visible for "<date>"

    Examples:
      | TestCaseId | city    | movie  | date      |
      | TC_TT_003  | Chennai | Avatar | tomorrow  |

  # TS_03 / TC_TT_004
  Scenario Outline: Cancellation policy is displayed for a theatre of "<movie>"
    Given open the District Movies website
    And I set city to "<city>"
    And I search and open movie "<movie>"
    When I open theatre number "<index>"
    Then cancellation policy should be visible

    Examples:
      | TestCaseId | city    | movie  | index |
      | TC_TT_004  | Chennai | Avatar | 1     |

  # TS_03 / TC_TT_005
  Scenario Outline: Services & amenities render properly on theatre page
    Given open the District Movies website
    And I set city to "<city>"
    And I search and open movie "<movie>"
    When I open theatre number "<index>"
    Then services and amenities should be loaded

    Examples:
      | TestCaseId | city    | movie  | index |
      | TC_TT_005  | Chennai | Avatar | 1     |

  # TS_03 / TC_TT_006
  Scenario Outline: Language filter on showtimes page for "<movie>"
    Given open the District Movies website
    And I set city to "<city>"
    And I search and open movie "<movie>"
    When I apply filter type "Language" with value "<language>"
    Then filtered showtimes should match selection "Language" "<language>"

    Examples:
      | TestCaseId | city    | movie  | language |
      | TC_TT_006  | Chennai | Avatar | English  |
      | TC_TT_006  | Chennai | Avatar | Tamil    |

  # TS_03 / TC_TT_007
  Scenario Outline: Show Time filter - Morning
    Given open the District Movies website
    And I set city to "<city>"
    And I search and open movie "<movie>"
    When I apply filter type "Show Time" with value "<slot>"
    Then filtered showtimes should match selection "Show Time" "<slot>"

    Examples:
      | TestCaseId | city    | movie  | slot     |
      | TC_TT_007  | Chennai | Avatar | Night    |

  # TS_03 / TC_TT_008
  Scenario Outline: Format filter - IMAX
    Given open the District Movies website
    And I set city to "<city>"
    And I search and open movie "<movie>"
    When I apply filter type "distance" with value "<format>"
    Then filtered showtimes should match selection "distance" "<format>"

    Examples:
      | TestCaseId | city    | movie  | distance |
      | TC_TT_008  | Chennai | Avatar | IMAX   |