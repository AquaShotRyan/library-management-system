Feature: Borrowing, Holding, and Return Operations
#TODO: reword background given
  Background:
    Given the library is initialized with books and users
    And today is 2025-09-20

  @a1_scenario
  Scenario Outline: existing user successfully logs in for the first time and selects to borrow a book
    Given I'm not logged in

    When I login as "<username1>" and select to borrow a book
    Then I should be logged in as "<username1>"
    And "<username1>" should see "<book_title>" is "Available"
    And "<username1>" should see their current book count is 0
    And "<username1>" should get no notification about a held book being available

    When "<username1>" checks out "<book_title>"
    Then "<username1>" should be the current borrower of "<book_title>"
    And "<username1>" should see "<book_title>" is "Checked Out"
    And "<book_title>" is due on "2025-10-04"

    When I log out
    Then I should be logged out
    And "<username1>" should be the current borrower of "<book_title>"
    And "<book_title>" is due on "2025-10-04"

    When I login as "<username2>" and select to borrow a book
    Then I should be logged in as "<username2>"
    And "<username2>" should see "<book_title>" is "Checked Out"
    And "<username2>" should see their current book count is 0
    And "<username2>" should get no notification about a held book being available

    When I log out
    Then I should be logged out

    When I login as "<username1>"
    And "<username1>" returns "<book_title>"
    Then I should be logged in as "<username1>"
    And "<username1>" should NOT be the current borrower of "<book_title>"
    And "<username1>" should see "<book_title>" is "Available"

    When I log out
    Then I should be logged out

    When I login as "<username2>" and select to borrow a book
    Then "<username2>" should see "<book_title>" is "Available"
    And "<username2>" should see their current book count is 0

    When I log out
    Then I should be logged out

    Examples:
      | username1 | username2 | book_title       |
      | alice     | bob       | The Great Gatsby |

  @multiple_holds_queue_processing
  Scenario: user can place a hold on a borrowed/unavailable book
    Given "charlie" checked out "1984"
    When "bob" places a hold on "1984"
    Then "bob" should be the current holder of "1984"
    And "bob" should NOT be the current borrower of "1984"
    And "bob" should NOT get a notification that their held book is available

  @multiple_holds_queue_processing
  Scenario: user is still the current holder after the book was returned, and gets a notification
    Given "alice" checked out "Wuthering Heights"
    And "charlie" is the current holder of "Wuthering Heights"
    When "alice" returns "Wuthering Heights"
    Then "charlie" should be the current holder of "Wuthering Heights"
    And "charlie" should NOT be the current borrower of "Wuthering Heights"
    And "charlie" should get a notification that their held book is available

  @multiple_holds_queue_processing
  Scenario: user is added to the holder queue if they attempt to hold a book that has a current holder
    Given "charlie" checked out "Crime and Punishment"
    And "alice" is the current holder of "Crime and Punishment"
    When "bob" places a hold on "Crime and Punishment"
    Then "bob" should be first in the hold queue of "Crime and Punishment"

  @multiple_holds_queue_processing
  Scenario: queue advances when the current holder borrows the book
    Given "charlie" checked out "Hamlet"
    And "bob" places a hold on "Hamlet"
    And "alice" places a hold on "Hamlet"
    And "charlie" returns "Hamlet"
    And "charlie" places a hold on "Hamlet"
    When "bob" checks out "Hamlet"
    Then "bob" should be the current borrower of "Hamlet"
    And "alice" should be the current holder of "Hamlet"
    And "charlie" should be first in the hold queue of "Hamlet"
    And "alice" should NOT get a notification that their held book is available
    And "charlie" should NOT get a notification that their held book is available

  @multiple_holds_queue_processing
  Scenario: user that isn't the current holder, but is in the queue, cannot borrow the book
    Given "bob" checked out "The Hobbit"
    And "alice" is the current holder of "The Hobbit"
    And "charlie" places a hold on "The Hobbit"
    When "charlie" checks out "The Hobbit"
    Then "charlie" should NOT be the current borrower of "The Hobbit"
    And "charlie" should be first in the hold queue of "The Hobbit"

  @borrowing_limit_and_hold_interactions
  Scenario: user can't borrow a book if they're at the borrowing limit
    Given "bob" checked out "Lord of the Flies"
    And "bob" checked out "Ulysses"
    And "bob" checked out "The Iliad"
    When "bob" checks out "War and Peace"
    Then "bob" should NOT be the current borrower of "War and Peace"
    And "bob" should have 3 books
    And "bob" should get offered to place a hold for "War and Peace"

  @borrowing_limit_and_hold_interactions
  Scenario: user can place a hold when they're at the borrowing limit
    Given "charlie" checked out "War and Peace"
    And "bob" checked out "Lord of the Flies"
    And "bob" checked out "Ulysses"
    And "bob" checked out "The Iliad"
    When "bob" places a hold on "War and Peace"
    Then "bob" should be the current holder of "War and Peace"

  @borrowing_limit_and_hold_interactions
  Scenario: user gains borrowing capacity after checking out books and returning one
    Given "charlie" checked out "War and Peace"
    And "charlie" checked out "To Kill a Mockingbird"
    And "charlie" checked out "Don Quixote"
    And "charlie" returns "War and Peace"
    When "charlie" checks out "The Great Gatsby"
    Then "charlie" should be the current borrower of "The Great Gatsby"

  @borrowing_limit_and_hold_interactions
  Scenario Outline: user gets a notification that their held book is available even though they have 3 books borrowed
    Given "alice" checked out "The Catcher in the Rye"
    And "alice" checked out "Crime and Punishment"
    And "alice" checked out "1984"
    And "bob" checked out "<returned_book>"
    And "charlie" checked out "<not_returned_book>"
    And "alice" places a hold on "<held_book>"
    When "bob" returns "<returned_book>"
    Then "alice" should get "<notified>" that my held book is available

    Examples:
     | returned_book    | not_returned_book | held_book   | notified        |
     | Animal Farm      | The Odyssey       | Animal Farm | a notification  |
     | Animal Farm      | The Odyssey       | The Odyssey | no notification |