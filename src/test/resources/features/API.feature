Feature: API
  As an API client,
  I should be able to access the Data Custodian API,
  So that I can access electric usage data from Data Custodian

Scenario: API client requests a list of Usage Points
  Given a Retail Customer with Usage Points
  When I GET /espi/1_1/resource/RetailCustomer/{RetailCustomerID}/UsagePoint
  Then I should receive the list of Usage Points
