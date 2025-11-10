Feature: Borrowing, Holding, and Return Operations
  Background:
    Given the library is initialized with books and users

  @a1_scenario
  Scenario Outline: A-TEST-1 scenario
    When "<username1>" logs in with password "<password1>" and selects to borrow a book
    Then they should see "<book_title>" is "Available"

    When they borrow "<book_title>"
    Then "<username1>" should be borrowing "<book_title>"
    And should see "<book_title>" is "Checked Out"

    When they log out
    And "<username2>" logs in with password "<password2>" and selects to borrow a book
    Then they should see "<book_title>" is "Checked Out"

    When they attempt to borrow "<book_title>"
    Then "<username2>" should NOT be borrowing "<book_title>"

    When they log out
    And "<username1>" logs in with password "<password1>" and selects to borrow a book
    And they return "<book_title>"
    Then they should see "<book_title>" is "Available"
    And "<username1>" should NOT be borrowing "<book_title>"

    When they log out
    And "<username2>" logs in with password "<password2>" and selects to borrow a book
    Then they should see "<book_title>" is "Available"

    Examples:
      | username1 | username2 | book_title       | password1 | password2 |
      | alice     | bob       | The Great Gatsby | pass123   | pass456   |

  @multiple_holds_queue_processing
  Scenario Outline: Processing queue of multiple holds
    When "<borrower>" logs in with password "<password1>"
    And they borrow "<book_title>"
    And they log out
    And "<holder1>" logs in with password "<password2>"
    And places a hold on "<book_title>"
    Then "<holder1>" should be the current holder of "<book_title>"
    But NOT get notified that their held book is available

    When they log out
    And "<holder2>" logs in with password "<password3>"
    And places a hold on "<book_title>"
    Then they should NOT get notified that their held book is available
    And "<holder2>" should be in the hold-queue of "<book_title>"
    But "<holder2>" should NOT be the current holder of "<book_title>"

    When they log out
    And "<borrower>" logs in with password "<password1>"
    And they return "<book_title>"
    Then "<holder1>" should be the current holder of "<book_title>"
    But "<holder1>" should NOT be borrowing "<book_title>"

    When they log out
    And "<holder2>" logs in with password "<password3>"
    Then they should NOT get notified that their held book is available

    When they borrow "<book_title>"
    Then "<holder2>" should NOT be borrowing "<book_title>"

    When they log out
    And "<holder1>" logs in with password "<password2>"
    Then they should get notified that their held book is available

    When they borrow "<book_title>"
    Then "<holder1>" should be borrowing "<book_title>"
    And "<holder2>" should be the current holder of "<book_title>"

    Examples:
      | borrower | holder1 | holder2 | book_title | password1 | password2 | password3 |
      | charlie  | bob     | alice   | Hamlet     | pass789   | pass456   | pass123   |

  @borrowing_limit_and_hold_interactions
  Scenario Outline: Borrowing limit and hold interactions
    When "<user2>" logs in with password "<password2>"
    And they borrow "<held_book>"
    And they log out
    And "<user1>" logs in with password "<password1>"
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
    And "<user2>" logs in with password "<password2>"
    And they return "<held_book>"
    And they log out
    And "<user1>" logs in with password "<password1>"
    Then they should get notified that their held book is available

    When they return "Lord of the Flies"
    Then "<user1>" should have borrowing capacity

    Examples:
      | user1 | user2   | held_book              | password1 | password2 |
      | alice | charlie | The Catcher in the Rye | pass123   | pass789   |

  @no_books_borrowed_scenario
  Scenario Outline: Returning when no books are borrowed
    When "<user>" logs in with password "<password>" and selects to borrow a book
    Then they should see all books as Available

    When they attempt to return a book
    Then they are informed they have no books currently borrowed

    Examples:
      | user    | password |
      | alice   | pass123  |
      | bob     | pass456  |
      | charlie | pass789  |