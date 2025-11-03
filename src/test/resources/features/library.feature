Feature: Borrowing, Holding, and Return Operations

  Background:
    Given the library is initialized with books and users
    And today is 2025-09-20

  @a1_scenario
  Scenario Outline: existing user successfully logs in for the firs time and selects to borrow a book
    Given I'm not logged in
    And "<book_title>" has no borrower
    And "<book_title>" has no holders
    When I login as "<username>"
    Then I should be logged in as "<username>"
    And I should see "<book_title>" is "Available"
    And I should see "<book_title>" has author "<book_author>"
    And I should see my current book count is 0
    And I should get no notification about a held being available

    Examples:
      | username | book_title       | book_author         |
      | alice    | The Great Gatsby | F. Scott FitzGerald |
      | bob      | The Great Gatsby | F. Scott FitzGerald |

  @a1_scenario
  Scenario: user borrows a book
    Given I'm logged in as "alice"
    When I check out "The Great Gatsby"
    Then "alice" should be the current borrower of "The Great Gatsby"
    And I should see "The Great Gatsby" is "Checked Out"
    And "The Great Gatsby" is due on "2025-10-04"

  @a1_scenario
  Scenario: user logs out after borrowing a book
    Given I'm logged in as "alice"
    And I check out "The Great Gatsby"
    When I log out
    Then I am logged out
    And "alice" should be the current borrower of "The Great Gatsby"
    And "The Great Gatsby" is due on "2025-10-04"

  @a1_scenario
  Scenario: book is unavailable to user2 after user1 checked it out
    Given "alice" checked out "The Great Gatsby"
    And I'm logged in as "bob"
    When I check out "The Great Gatsby"
    Then "bob" should NOT be the current borrower of "The Great Gatsby"


  @a1_scenario
  Scenario: user returns a book
    Given I'm logged in as "alice"
    And I check out "The Great Gatsby"
    When I return "The Great Gatsby"
    Then "alice" should NOT be the current borrower of "The Great Gatsby"
    And I should see "The Great Gatsby" is "Available"

  @a1_scenario
  Scenario: user2 sees book as 'Available' after user1 returned it
    Given "alice" borrowed and returned "The Great Gatsby"
    When I login as "bob"
    Then I should see "The Great Gatsby" is "Available"
