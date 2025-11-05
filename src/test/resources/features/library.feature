Feature: Borrowing, Holding, and Return Operations

  Background:
    Given the library is initialized with books and users
    And today is 2025-09-20

  @a1_scenario
    #TODO: delete 3rd Then
    #TODO: modify 4th Then
    #TODO: delete book_title and book_author columns
  Scenario Outline: existing user successfully logs in for the first time and selects to borrow a book
    Given I'm not logged in
    And "The Great Gatsby" has no borrower
    And "The Great Gatsby" has no holders
    When I login as "<username>"
    Then I should be logged in as "<username>"
    And "<username>" should see "The Great Gatsby" is "Available"
    And "<username>" should see their current book count is 0
    And "<username>" should get no notification about a held being available

    Examples:
      | username |
      | alice    |
      | bob      |

  @a1_scenario
    #TODO: don't login
  Scenario: user borrows a book
    Given I'm logged in as "alice"
    When "alice" checks out "The Great Gatsby"
    Then "alice" should be the current borrower of "The Great Gatsby"
    And "alice" should see "The Great Gatsby" is "Checked Out"
    And "The Great Gatsby" is due on "2025-10-04"

  @a1_scenario
    #TODO: modify to login again and check state
  Scenario: user logs out after borrowing a book
    Given I'm logged in as "alice"
    And "alice" checks out "The Great Gatsby"
    When I log out
    Then I am logged out
    And "alice" should be the current borrower of "The Great Gatsby"
    And "The Great Gatsby" is due on "2025-10-04"

  @a1_scenario
    #TODO: delete login
  Scenario: book is unavailable to user2 after user1 checked it out
    Given "alice" checked out "The Great Gatsby"
    And I'm logged in as "bob"
    When "bob" checks out "The Great Gatsby"
    Then "bob" should NOT be the current borrower of "The Great Gatsby"

  @a1_scenario
    #TODO: delete login
  Scenario: user returns a book
    Given I'm logged in as "alice"
    And "alice" checks out "The Great Gatsby"
    When "alice" returns "The Great Gatsby"
    Then "alice" should NOT be the current borrower of "The Great Gatsby"
    And "alice" should see "The Great Gatsby" is "Available"

  @a1_scenario
    #TODO: delete login and "borrowed and returned"
  Scenario: user2 sees book as 'Available' after user1 returned it
    Given "alice" borrowed and returned "The Great Gatsby"
    When I login as "bob"
    Then "bob" should see "The Great Gatsby" is "Available"

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
    #TODO: delete login
  Scenario: user can't borrow a book if they're at the borrowing limit
    Given I'm logged in as "bob"
    And "bob" checked out "Lord of the Flies"
    And "bob" checked out "Ulysses"
    And "bob" checked out "The Iliad"
    When "bob" checks out "War and Peace"
    Then "bob" should NOT be the current borrower of "War and Peace"
    And "bob" should have 3 books
    And "bob" should get offered to place a hold for "War and Peace"

  @borrowing_limit_and_hold_interactions
    #TODO: delete login
  Scenario: user can place a hold when they're at the borrowing limit
    Given I'm logged in as "bob"
    And "bob" checked out "Lord of the Flies"
    And "bob" checked out "Ulysses"
    And "bob" checked out "The Iliad"
    When "bob" places a hold on "War and Peace"
    Then "bob" should be the current holder of "War and Peace"

  @borrowing_limit_and_hold_interactions
    #TODO: delete login
  Scenario: user gains borrowing capacity after checking out books and returning one
    Given I'm logged in as "charlie"
    And "charlie" checked out "War and Peace"
    And "charlie" checked out "To Kill a Mockingbird"
    And "charlie" checked out "Don Quixote"
    And "charlie" returns "War and Peace"
    When "charlie" checks out "The Great Gatsby"
    Then "charlie" should be the current borrower of "The Great Gatsby"

  @borrowing_limit_and_hold_interactions
    #TODO: delete login
  Scenario Outline: user gets a notification that their held book is available even though they have 3 books borrowed
    Given I'm logged in as "alice"
    And "alice" checked out "The Catcher in the Rye"
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