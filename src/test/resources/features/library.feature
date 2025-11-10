Feature: Borrowing, Holding, and Return Operations
#TODO: reword background given
  Background:
    Given the library is initialized with books and users

  @a1_scenario
  Scenario Outline: A-TEST-1 scenario
    When "<username1>" logs in and selects to borrow a book
    Then they should see "<book_title>" is "Available"

    When they borrow "<book_title>"
    Then "<username1>" should be borrowing "<book_title>"
    And should see "<book_title>" is "Checked Out"

    When they log out
    And "<username2>" logs in and selects to borrow a book
    Then they should see "<book_title>" is "Checked Out"

    When they attempt to borrow "<book_title>"
    Then "<username2>" should NOT be borrowing "<book_title>"

    When they log out
    And "<username1>" logs in and selects to borrow a book
    And they return "<book_title>"
    Then they should see "<book_title>" is "Available"
    And "<username1>" should NOT be borrowing "<book_title>"

    When they log out
    And "<username2>" logs in and selects to borrow a book
    Then they should see "<book_title>" is "Available"

    Examples:
      | username1 | username2 | book_title       |
      | alice     | bob       | The Great Gatsby |

  @multiple_holds_queue_processing
  Scenario Outline: processing queue of multiple holds
    When "<borrower>" logs in and selects to borrow a book
    And they borrow "<book_title>"
    And they log out
    And "<holder1>" logs in and selects to borrow a book
    And places a hold on "<book_title>"
    Then "<holder1>" should be the current holder of "<book_title>"
    And NOT get notified that their held book is available

    When they log out
    And "<holder2>" logs in and selects to borrow a book
    And places a hold on "<book_title>"
    Then they should NOT get notified that their held book is available
    And "<holder2>" should be in the hold-queue of "<book_title>"
    But "<holder2>" should NOT be the current holder of "<book_title>"

    When they log out
    And "<borrower>" logs in
    And they return "<book_title>"
    Then "<holder1>" should be the current holder of "<book_title>"
    And "<holder1>" should NOT be borrowing "<book_title>"

    When they log out
    And "<holder2>" logs in and selects to borrow a book
    Then they should NOT get notified that their held book is available

    When they borrow "<book_title>"
    Then "<holder2>" should NOT be borrowing "<book_title>"

    When they log out
    And "<holder1>" logs in
    Then they should get notified that their held book is available

    When they borrow "<book_title>"
    Then "<holder1>" should be borrowing "<book_title>"
    And "<holder2>" should be the current holder of "<book_title>"

    Examples:
      | borrower | holder1 | holder2 | book_title |
      | charlie  | bob     | alice   | Hamlet     |

  @borrowing_limit_and_hold_interactions
  Scenario Outline: borrowing limit and hold interactions
    When "<user2>" logs in
    And they borrow "<held_book>"
    And they log out
    And "<user1>" logs in
    And they borrow "Lord of the Flies"
    And they borrow "Ulysses"
    And they borrow "The Iliad"
    And they attempt to borrow "<held_book>"
    Then "<user1>" should NOT be borrowing "<held_book>"
    And "<user1>" should get offered to place a hold for "<held_book>"

    When they place a hold on "<held_book>"
    Then "<user1>" should be the current holder of "<held_book>"
    And NOT get notified that their held book is available

    When they log out
    And "<user2>" logs in
    And they return "<held_book>"
    And they log out
    And "<user1>" logs in
    Then they should get notified that their held book is available

    When they return "Lord of the Flies"
    Then "<user1>" should have borrowing capacity

    Examples:
      | user1 | user2   | held_book              |
      | alice | charlie | The Catcher in the Rye |