Feature: Retail Customers
  As a Retail Customer,
  I want to be able to view my usage point in my browser
  So that I can see my UsagePoints

  Scenario: Retail Customer visits Retail Customer's home page
    Given I have a Retail Customer account

    When I log in as Alan Turing
    Then I should see Retail Customer home page

  Scenario: Retail Customer views their usage data
    Given a Retail Customer with Usage Points

    When I login as Retail Customer
    Then the logged in retail customer can see their usage data

  Scenario: Retail Customer downloads Usage Points in XML format
    Given a Retail Customer with Usage Points

    When I navigate to the Usage Points list
    Then I should be able to download Usage Points in XML format
    And the XML includes Service categories
    And the XML includes Meter Readings
    And the XML includes Reading Types
    And the XML includes Electric Power Usage Summary
    And the XML includes Interval Blocks
