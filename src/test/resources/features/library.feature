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

  @multiple_holds_queue_processing
  Scenario: user can place a hold on a borrowed/unavailable book
    Given "charlie" checked out "1984"
    When "bob" places a hold on "1984"
    Then "bob" should be the current holder of "1984"
    And "bob" should NOT be the current borrower of "1984"
    And "bob" should NOT get a notification that their held book is available

  @multiple_holds_queue_processing
  Scenario: user is still the current holder after the book was returned and gets a notification
    Given "alice" checked out "Wuthering Heights"
    And "charlie" is the current holder of "1984"
    When "alice" returns "Wuthering Heights"
    Then "charlie" should be the current holder of "1984"
    And "charlie" should NOT be the current borrower of "1984"
    And "charlie" should get a notification that their held book is available

  @multiple_holds_queue_processing
  Scenario: user is added to the holder queue if they attempt to hold a book that has a current holder
    Given "alice" is the current holder of "Crime and Punishment"
    When "bob" places a hold on "Crime and Punishment"
    Then "bob" should be first in the hold queue of "Crime and Punishment"

  @multiple_holds_queue_processing
  Scenario: queue advances when the current holder borrows the book
    Given "charlie" is the current holder of "Hamlet"
    And "bob" places a hold on "Hamlet"
    And "alice" places a hold on "Hamlet"
    When "charlie" checks out "Hamlet"
    Then "charlie" should be the current borrower of "Hamlet"
    And "bob" should be the current holder of "Hamlet"
    And "alice" should be first in the hold queue of "Hamlet"
    And "bob" should NOT get a notification that their held book is available
    And "alice" should NOT get a notification that their held book is available

  @multiple_holds_queue_processing
  Scenario: user that isn't the current holder, but is in the queue, cannot borrow the book
    Given "alice" is the current holder of "The Hobbit"
    And "charlie" places a hold on "The Hobbit"
    When "charlie" checks out "The Hobbit"
    Then "charlie" should NOT be the current borrower of "The Hobbit"
    And "charlie" should be first in the hold queue of "The Hobbit"
    And "alice" should get a notification that their held book is available

  @borrowing_limit_and_hold_interactions
  Scenario: user can't borrow a book if they're at the borrowing limit
    Given I'm logged in as "bob"
    And I check out "Lord of the Flies"
    And I check out "Ulysses"
    And I check out "The Iliad"
    When I check out "War and Peace"
    Then "bob" should NOT be the current borrower of "War and Peace"
    And "bob" should have 3 books
    And I should get offered to place a hold for "War and Peace"

  @borrowing_limit_and_hold_interactions
  Scenario: user can place a hold when they're at the borrowing limit
    Given I'm logged in as "bob"
    And I check out "Lord of the Flies"
    And I check out "Ulysses"
    And I check out "The Iliad"
    When I place a hold on "War and Peace"
    Then "bob" should be the current holder of "War and Peace"

  @borrowing_limit_and_hold_interactions
  Scenario: user gains borrowing capacity after checking out books and returning one
    Given I'm logged in as "charlie"
    And I check out "War and Peace"
    And I check out "To Kill a Mockingbird"
    And I check out "Don Quixote"
    And I return "War and Peace"
    When I check out "The Great Gatsby"
    Then "charlie" should be the current borrower of "The Great Gatsby"

  @borrowing_limit_and_hold_interactions
  Scenario Outline: user gets a notification that their held book is available even though they have 3 books borrowed
    Given I'm logged in as "alice"
    And I check out "The Catcher in the Rye"
    And I check out "Crime and Punishment"
    And I check out "1984"
    And "bob" checked out "<returned_book>"
    And "charlie" checked out "<not_returned_book>"
    And I place a hold on "<held_book>"
    When "bob" returns "<returned_book>"
    Then I should get "<notified>" that my held book is available

    Examples:
     | returned_book    | not_returned_book | held_book   | notified        |
     | Animal Farm      | The Odyssey       | Animal Farm | a notification  |
     | Animal Farm      | The Odyssey       | The Odyssey | no notification |