Feature: Borrowing, Holding, and Return Operations
#TODO: reword background given
  Background:
    Given the library is initialized with books and users
    And today is 2025-09-20
    And nobody is logged in

  @a1_scenario
  Scenario Outline: existing user successfully logs in for the first time and selects to borrow a book
    When "<username1>" logs in and selects to borrow a book
    Then "<username1>" should be logged in
    And see "<book_title>" is "Available"
    And see their current book count is 0
    And get no notification about a held book being available

    When they check out "<book_title>"
    Then "<username1>" should be the current borrower of "<book_title>"
    And should see "<book_title>" is "Checked Out"
    And "<book_title>" is due on "2025-10-04"

    When they log out
    Then "<username1>" should be logged out

    When "<username2>" logs in and selects to borrow a book
    Then "<username2>" should be logged in
    And see "<book_title>" is "Checked Out"
    And see their current book count is 0
    And get no notification about a held book being available

    When they log out
    Then "<username2>" should be logged out

    When "<username1>" logs in and selects to borrow a book
    Then "<username1>" should be logged in
    And they should see their current book count is 1

    When they return "<book_title>"
    Then they should see "<book_title>" is "Available"
    And "<username1>" should NOT be the current borrower of "<book_title>"

    When they log out
    Then "<username1>" should be logged out

    When "<username2>" logs in and selects to borrow a book
    Then they should see "<book_title>" is "Available"
    And see their current book count is 0

    When they log out
    Then "<username2>" should be logged out

    Examples:
      | username1 | username2 | book_title       |
      | alice     | bob       | The Great Gatsby |

  @multiple_holds_queue_processing
  Scenario Outline: processing queue of multiple holds
    When "<borrower>" logs in and selects to borrow a book
    And they check out "<book_title>"
    Then "<borrower>" should be the current borrower of "<book_title>"

    When they log out
    Then "<borrower>" should be logged out

    When "<holder1>" logs in and selects to borrow a book
    And places a hold on "<book_title>"
    Then "<holder1>" should be the current holder of "<book_title>"
    And get no notification about a held book being available
    And see "<book_title>" is "Checked Out"

    When they log out
    Then "<holder1>" should be logged out

    When "<holder2>" logs in and selects to borrow a book
    And places a hold on "<book_title>"
    Then they should get no notification about a held book being available
    And see "<book_title>" is "Checked Out"

    When they log out
    Then "<holder2>" should be logged out

    When "<borrower>" logs in
    And they return "<book_title>"
    Then "<holder1>" should be the current holder of "<book_title>"
    And "<holder1>" should NOT be the current borrower of "<book_title>"
    And "<borrower>" should NOT be the current borrower of "<book_title>"

    When they log out
    Then "<borrower>" should be logged out

    When "<holder2>" logs in and selects to borrow a book
    Then they should get no notification about a held book being available
    And see "<book_title>" is "On Hold"

    When they check out "<book_title>"
    Then "<holder2>" should NOT be the current borrower of "<book_title>"

    When they log out
    Then "<holder2>" should be logged out

    When "<holder1>" logs in
    Then they should get a notification about a held book being available
    And see "<book_title>" is "Available"

    When they check out "<book_title>"
    Then "<holder1>" should be the current borrower of "<book_title>"
    And "<holder2>" should be the current holder of "<book_title>"

    Examples:
      | borrower | holder1 | holder2 | book_title |
      | charlie  | bob     | alice   | Hamlet     |

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