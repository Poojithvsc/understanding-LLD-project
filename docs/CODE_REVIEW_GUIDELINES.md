# Code Review Guidelines

## Purpose

Code reviews help us maintain high code quality, share knowledge, and catch issues early. This document provides guidelines for both authors and reviewers.

## For PR Authors

### Before Creating a PR

1. **Self-Review First**
   - Review your own code changes
   - Check for commented-out code
   - Remove debug statements
   - Verify all tests pass

2. **Update Documentation**
   - Update API documentation
   - Update design documents if architecture changed
   - Add code comments for complex logic
   - Update README if needed

3. **Write Good Commit Messages**
   ```
   Good: "Add order validation to prevent negative quantities"
   Bad: "fix bug"

   Good: "Implement Redis caching for order queries"
   Bad: "changes"
   ```

4. **Keep PRs Small**
   - Aim for < 400 lines of code changes
   - One feature or fix per PR
   - Break large changes into multiple PRs

### Creating the PR

1. **Write a Descriptive Title**
   ```
   Good: "Add inventory reservation logic to Inventory Service"
   Bad: "Update code"
   ```

2. **Fill Out PR Template Completely**
   - Describe what changed and why
   - Link related issues
   - List testing done
   - Note any breaking changes

3. **Add Context**
   - Explain design decisions
   - Call out areas that need extra attention
   - Mention any trade-offs made

### After Creating the PR

1. **Respond to Feedback Promptly**
   - Address comments within 24 hours
   - Ask for clarification if needed
   - Be open to suggestions

2. **Don't Take Feedback Personally**
   - Reviews are about code quality, not about you
   - Everyone's code can be improved
   - Learn from feedback

3. **Mark Conversations as Resolved**
   - Once you've addressed a comment, mark it resolved
   - If you disagree, explain your reasoning

## For Reviewers

### Review Timeline

- **Acknowledge**: Within 4 hours
- **Complete Review**: Within 24 hours
- **Follow-up**: Within 12 hours

### What to Look For

#### 1. Correctness
- [ ] Does the code do what it's supposed to do?
- [ ] Are edge cases handled?
- [ ] Is error handling appropriate?
- [ ] Are there any logical errors?

#### 2. Code Quality
- [ ] Is the code readable and understandable?
- [ ] Are naming conventions followed?
- [ ] Is the code well-structured?
- [ ] Is there duplicated code that could be refactored?
- [ ] Are magic numbers/strings replaced with constants?

#### 3. Testing
- [ ] Are there adequate unit tests?
- [ ] Are there integration tests if needed?
- [ ] Do tests cover edge cases?
- [ ] Are tests meaningful and not just for coverage?
- [ ] Do all tests pass?

#### 4. Performance
- [ ] Are there any obvious performance issues?
- [ ] Are database queries optimized?
- [ ] Is pagination used for large result sets?
- [ ] Are expensive operations cached?

#### 5. Security
- [ ] Is input validated?
- [ ] Are SQL queries parameterized?
- [ ] Are sensitive data logged?
- [ ] Is authentication/authorization checked?

#### 6. Design & Architecture
- [ ] Does it follow existing patterns?
- [ ] Is the design scalable?
- [ ] Are dependencies appropriate?
- [ ] Is the code in the right layer (controller/service/repository)?

#### 7. Documentation
- [ ] Are complex sections commented?
- [ ] Is API documentation updated?
- [ ] Are public methods documented?
- [ ] Are design docs updated if needed?

### How to Give Feedback

#### Be Constructive and Specific

```
❌ "This is bad"
✅ "Consider extracting this logic into a separate method for better readability"

❌ "Wrong approach"
✅ "This approach might cause performance issues with large datasets.
    Consider using pagination here. Here's an example: [link]"
```

#### Distinguish Between Issues and Suggestions

Use prefixes to indicate severity:
- **MUST**: Blocking issue that must be fixed
- **SHOULD**: Strong suggestion, should be addressed
- **NIT**: Minor style/preference, nice to have
- **QUESTION**: Asking for clarification

Examples:
```
MUST: This creates a SQL injection vulnerability. Use parameterized queries.
SHOULD: Consider adding error handling here for the case when the service is unavailable.
NIT: Variable name could be more descriptive - suggest `orderTotal` instead of `total`
QUESTION: Why did you choose this approach over using the existing utility method?
```

#### Provide Reasoning

```
❌ "Don't use Optional here"
✅ "Avoid using Optional as a method parameter. It adds unnecessary overhead and
    makes the API confusing. Instead, use method overloading or allow null with
    clear documentation. Reference: [Java best practices link]"
```

#### Offer Solutions

```
❌ "This won't scale"
✅ "This approach might have performance issues with many items. Consider:
    1. Using pagination
    2. Implementing caching
    3. Moving processing to background job
    What do you think?"
```

#### Praise Good Work

```
✅ "Nice refactoring! This is much more readable."
✅ "Great test coverage on the edge cases."
✅ "I like this approach - very clean and maintainable."
```

### Review Process

1. **First Pass - Big Picture**
   - Understand the purpose of the PR
   - Check overall approach and design
   - Verify tests exist and pass

2. **Second Pass - Details**
   - Line-by-line code review
   - Check for bugs and edge cases
   - Verify code quality

3. **Third Pass - Verification**
   - Check documentation updates
   - Verify all checklist items completed
   - Ensure all feedback addressed

### Making a Decision

#### Approve ✅
Use when:
- Code meets quality standards
- All critical issues resolved
- Tests pass and coverage adequate
- Documentation updated

#### Request Changes 🔴
Use when:
- Bugs or security issues present
- Missing tests
- Breaking changes not documented
- Major design issues

#### Comment 💬
Use when:
- Minor suggestions only
- Questions need answering
- Want to provide feedback but not blocking

## Common Review Comments

### Code Smells

**Long Method**
```java
// SHOULD: This method is doing too much. Consider breaking it into smaller methods:
// - validateOrder()
// - calculateTotal()
// - persistOrder()
// - publishEvent()
```

**Deep Nesting**
```java
// SHOULD: Consider early returns to reduce nesting
// Before:
if (order != null) {
    if (order.getItems() != null) {
        if (!order.getItems().isEmpty()) {
            // logic
        }
    }
}

// After:
if (order == null || order.getItems() == null || order.getItems().isEmpty()) {
    return;
}
// logic
```

**Magic Numbers**
```java
// MUST: Replace magic number with named constant
// Before:
if (items.size() > 50) { ... }

// After:
private static final int MAX_ORDER_ITEMS = 50;
if (items.size() > MAX_ORDER_ITEMS) { ... }
```

### Testing Issues

**Missing Tests**
```java
// MUST: Add tests for this error case
// MUST: Add tests for null inputs
// SHOULD: Add integration test for the full flow
```

**Poor Test Names**
```java
// NIT: Test names should describe what is being tested
// Before:
@Test void test1() { ... }

// After:
@Test void createOrder_WithInvalidItems_ThrowsValidationException() { ... }
```

### Performance Issues

**N+1 Queries**
```java
// MUST: This will cause N+1 queries. Use JOIN FETCH or entity graph
// Before:
List<Order> orders = orderRepository.findAll();
orders.forEach(o -> o.getItems().size()); // Triggers N queries

// After:
@Query("SELECT o FROM Order o JOIN FETCH o.items")
List<Order> findAllWithItems();
```

**Missing Pagination**
```java
// SHOULD: Add pagination to prevent memory issues with large datasets
// Before:
@GetMapping
List<Order> getAllOrders() { ... }

// After:
@GetMapping
Page<Order> getAllOrders(Pageable pageable) { ... }
```

### Security Issues

**SQL Injection**
```java
// MUST: SQL injection vulnerability. Use parameterized query
// Before:
String query = "SELECT * FROM orders WHERE id = " + orderId;

// After:
@Query("SELECT o FROM Order o WHERE o.id = :orderId")
Order findById(@Param("orderId") Long orderId);
```

**Logging Sensitive Data**
```java
// MUST: Don't log sensitive information like passwords or tokens
// Before:
log.info("User login: {}", userCredentials);

// After:
log.info("User login attempt: userId={}", userId);
```

## Anti-Patterns to Avoid

### As an Author

❌ Submitting huge PRs (> 1000 lines)
❌ Not testing your changes
❌ Ignoring review comments
❌ Getting defensive about feedback
❌ Rushing to merge without addressing feedback

### As a Reviewer

❌ Nitpicking on personal preferences
❌ Being condescending or rude
❌ Approving without actually reviewing
❌ Blocking on minor style issues
❌ Reviewing too late

## Example Review

### Good Review Example

```
Overall looks good! Nice work on the error handling. A few suggestions:

MUST: Line 45 - SQL injection vulnerability
The string concatenation creates a SQL injection risk.
Use @Query with @Param instead.

SHOULD: Line 78 - Consider caching
This query is called frequently. Consider adding @Cacheable
to improve performance.

NIT: Line 102 - Variable naming
Consider renaming `temp` to `previousOrderStatus` for clarity.

QUESTION: Line 120 - Design decision
Why did you choose to synchronously call the inventory service here?
Would an event-driven approach be better for reliability?

Great test coverage on the happy path! Could you add a test for
the case when the database is unavailable?

Nice refactoring of the validation logic - much cleaner than before! ✅
```

## Metrics

Track these metrics to improve the review process:

- **Time to First Review**: < 4 hours
- **Time to Approval**: < 24 hours
- **PR Size**: < 400 lines (median)
- **Comments per PR**: 3-10
- **Iterations**: < 3
- **Bug Escape Rate**: < 5%

## Resources

- [Google's Code Review Guidelines](https://google.github.io/eng-practices/review/)
- [Conventional Comments](https://conventionalcomments.org/)
- [The Art of Code Review](https://www.alexandra-hill.com/2018/06/25/the-art-of-giving-and-receiving-code-reviews/)

## Summary

**For Authors:**
1. Self-review first
2. Keep PRs small
3. Write clear descriptions
4. Be responsive to feedback

**For Reviewers:**
1. Review promptly
2. Be constructive and specific
3. Distinguish severity
4. Praise good work

**Everyone:**
- Be respectful and professional
- Focus on code quality
- Learn from each other
- Improve continuously
