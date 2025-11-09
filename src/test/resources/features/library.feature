Feature: Borrowing, Holding, and Return Operations
#TODO: reword background given
  Background:
    Given the library is initialized with books and users

  @a1_scenario
  Scenario Outline: A-TEST-1 scenario
    When "<username1>" logs in and selects to borrow a book
    Then "<username1>" should be logged in
    And see "<book_title>" is "Available"
    And see their current book count is 0
    And get no notification about a held book being available

    When they check out "<book_title>"
    Then "<username1>" should be the current borrower of "<book_title>"
    And should see "<book_title>" is "Checked Out"

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
  Scenario Outline: borrowing limit and hold interactions
    When "<user2>" logs in
    And they check out "<held_book>"
    Then "<user2>" should be the current borrower of "<held_book>"

    When they log out
    Then "<user2>" should be logged out

    When "<user1>" logs in
    And they check out "Lord of the Flies"
    And they check out "Ulysses"
    And they check out "The Iliad"
    And they check out "<held_book>"
    Then "<user1>" should be the current borrower of "Lord of the Flies"
    And "<user1>" should be the current borrower of "Ulysses"
    And "<user1>" should be the current borrower of "The Iliad"
    And "<user1>" should NOT be the current borrower of "<held_book>"
    And "<user1>" should have 3 books
    And "<user1>" should get offered to place a hold for "<held_book>"

    When they place a hold on "<held_book>"
    Then "<user1>" should be the current holder of "<held_book>"
    And get no notification about a held book being available

    When they log out
    Then "<user1>" should be logged out

    When "<user2>" logs in
    And they return "<held_book>"
    Then "<user2>" should NOT be the current borrower of "<held_book>"
    And "<user1>" should be the current holder of "<held_book>"

    When they log out
    Then "<user2>" should be logged out

    When "<user1>" logs in
    Then they should get a notification about a held book being available

    When they return "Lord of the Flies"
    Then "<user1>" should NOT be the current borrower of "Lord of the Flies"
    And "<user1>" should have 2 books

    When they check out "<held_book>"
    Then "<user1>" should be the current borrower of "<held_book>"
    And "<user1>" should have 3 books

    Examples:
      | user1 | user2   | held_book              |
      | alice | charlie | The Catcher in the Rye |