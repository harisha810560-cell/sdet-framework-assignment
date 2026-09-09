# Traceability

Maps each requirement to the test method that covers it, records how deeply each endpoint is
validated, and lists the service behaviours that deviate from what a production API would do.

## UI — SauceDemo purchase journey

| Requirement | Test method | Groups |
|---|---|---|
| Sign in with valid credentials | `SauceDemoLoginTest.validCredentialsSignTheShopperIn` | ui, smoke, regression |
| Add a product to the cart | `SauceDemoCartTest.addingProductPutsItInTheCart` | ui, regression |
| Complete checkout and confirm the order | `SauceDemoCheckoutTest.completeShippingDetailsConfirmTheOrder` | ui, regression |
| Full journey, login through order confirmation | `SauceDemoOrderTest.shopperCompletesOrderEndToEnd` | ui, smoke, regression |
| Unregistered credentials are refused | `SauceDemoLoginTest.loginIsRejected` (row 1) | ui, negative, regression |
| Blank username is refused | `SauceDemoLoginTest.loginIsRejected` (row 2) | ui, negative, regression |
| Blank password is refused | `SauceDemoLoginTest.loginIsRejected` (row 3) | ui, negative, regression |
| Locked-out account is refused | `SauceDemoLoginTest.loginIsRejected` (row 4) | ui, negative, regression |
| Mandatory shipping field is enforced | `SauceDemoCheckoutTest.shippingDetailsWithoutFirstNameAreRefused` | ui, negative, regression |
| Cart is empty in a fresh session (zero-item boundary) | `SauceDemoCartTest.freshSessionStartsWithAnEmptyCart` | ui, regression, boundary |
| Cart holds several products (multi-item boundary) | `SauceDemoCartTest.cartHoldsEveryProductAdded` | ui, regression, boundary |

### What each UI stage asserts

| Stage | Assertions |
|---|---|
| Login | Inventory container rendered, browser on `/inventory.html` |
| Add to cart | Cart item count, item name and price match what the inventory page advertised, cart badge count |
| Multi-item cart | Cart item count, badge count and the set of item names match everything added |
| Empty cart | Cart item count is zero and no badge element is rendered |
| Checkout | Confirmation header text, non-blank confirmation body, browser on `/checkout-complete.html` |
| Rejected login | Exact inline error message for each rejection reason |
| Rejected checkout | Exact `Error: First Name is required` message |

## API — JSONPlaceholder `/posts`

| Requirement | Test method | Groups |
|---|---|---|
| GET a single resource | `PostApiTest.getsAnExistingPost` | api, smoke, regression |
| GET both ends of the seeded id range (1, 50, 100) | `PostApiTest.getsEachSeededPost` (3 rows) | api, regression, boundary |
| GET a collection | `PostApiTest.getsTheWholePostCollection` | api, regression |
| GET a filtered collection | `PostApiTest.getsOnlyPostsOwnedByTheRequestedUser` | api, regression |
| POST creates a resource | `PostApiTest.createsPostFromBuilderPayload` | api, smoke, regression |
| PUT replaces a resource | `PostApiTest.replacesEveryFieldOfAPost` | api, regression |
| PATCH updates part of a resource | `PostApiTest.patchesOnlyTheFieldsThatWereSent` | api, regression |
| DELETE removes a resource | `PostApiTest.deletesAPost` | api, regression |
| Ids outside the seeded range return 404 (0, 101, 999999) | `PostApiNegativeTest.unknownPostIdReturnsNotFound` (3 rows) | api, negative, regression, boundary |
| Unknown route returns 404 | `PostApiNegativeTest.unknownResourceReturnsNotFound` | api, negative, regression |
| Malformed JSON body is refused | `PostApiNegativeTest.malformedJsonBodyIsRefused` | api, negative, regression |
| Missing mandatory fields | `PostApiNegativeTest.payloadWithoutMandatoryFieldsRecordsServiceBehaviour` | api, negative, regression |
| Filter matching nothing returns an empty collection | `PostApiNegativeTest.filterMatchingNoPostsReturnsEmptyCollection` | api, boundary, regression |

### Validation depth per endpoint

| Endpoint | Status | JSON schema | Field assertions |
|---|---|---|---|
| `GET /posts/{id}` | 200 | `post-schema.json` | `id` matches request, non-blank `title` and `body`, positive `userId` |
| `GET /posts/0`, `GET /posts/101` | 404 | — | Confirms the seeded range is exactly 1&ndash;100 |
| `GET /posts` | 200 | `post-list-schema.json` | Collection size is 100 |
| `GET /posts?userId=` | 200 | `post-list-schema.json` | Non-empty, and every item's `userId` matches the filter |
| `POST /posts` | 201 | `post-schema.json` | Generated `id`, and `title`, `body`, `userId` echo the request |
| `PUT /posts/{id}` | 200 | `post-schema.json` | `id` preserved, all three fields replaced |
| `PATCH /posts/{id}` | 200 | — | `id` preserved, only the sent fields applied |
| `DELETE /posts/{id}` | 200 | — | Response body is an empty document |
| `GET /posts/999999` | 404 | — | — |
| `GET /unknown-resource` | 404 | — | — |
| `POST /posts` (malformed) | >= 400 | — | No resource id in the response |
| `POST /posts` (`{}`) | 201 | — | Generated `id` present |

PATCH and DELETE are not schema-validated because their responses are legitimately partial: PATCH
echoes only the fields that were sent, and DELETE returns an empty document.

## Known service deviations

JSONPlaceholder is a mock. These behaviours differ from what a production API should do, so the
tests assert the observed contract and the gap is recorded here rather than failing the suite over
a defect this project does not own.

| Case | Correct behaviour | Observed | How it is handled |
|---|---|---|---|
| `POST /posts` with malformed JSON | 400 Bad Request | 500 with a parser stack trace | Asserts `status >= 400` and that nothing was created; `PostValidator` logs a warning naming the deviation |
| `POST /posts` with `{}` | 400, mandatory fields missing | 201 with a generated id | Pinned as current behaviour, so pointing the suite at a validating backend fails here on purpose |
| `PATCH /posts/999999` | 404 Not Found | 200 echoing the patch | No negative PATCH test written — there is no rule to assert |
| `DELETE /posts/999999` | 404 Not Found | 200 with an empty body | No negative DELETE test written — there is no rule to assert |

## Deliberate coverage gaps

| Not covered | Reason |
|---|---|
| 401 / 403 authentication | JSONPlaceholder exposes no authenticated endpoint |
| Out-of-stock product | SauceDemo has no stock model |
| Empty-cart checkout rejection | SauceDemo allows checkout with an empty cart, so there is no rule to assert |
| Write persistence | JSONPlaceholder does not persist writes, so a resource cannot be read back after `POST` or `DELETE` |
