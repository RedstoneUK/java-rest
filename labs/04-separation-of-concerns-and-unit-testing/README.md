# Lab 04 — Separation of Concerns and Unit Testing

**Module:** Separation of Concerns and Unit Testing  
**Duration:** 75 minutes  
**Tools:** Java 17, Gradle, JUnit 5, Mockito, IDE

---

## Objectives

By the end of this lab you will be able to:

- Refactor a single-class handler into a three-layer architecture
- Use constructor injection to make dependencies testable
- Write unit tests for a service class using JUnit 5
- Use Mockito to mock dependencies and verify behaviour

---

## Setup

Add JUnit 5 and Mockito to `build.gradle`:

```groovy
dependencies {
    implementation 'com.google.code.gson:gson:2.10.1'
    testImplementation 'org.junit.jupiter:junit-jupiter:5.10.2'
    testImplementation 'org.mockito:mockito-junit-jupiter:5.11.0'
}

test {
    useJUnitPlatform()
}
```

Copy your `Product.java` from Module 03 into `src/main/java/com/example/api/model/`.

---

## Exercise 1 — Create ProductRepository (15 min)

Create `src/main/java/com/example/api/repository/ProductRepository.java`.

The repository manages the in-memory store. It must provide:

| Method | Return type | Description |
|--------|-------------|-------------|
| `findAll()` | `List<Product>` | Return all products |
| `findById(int id)` | `Optional<Product>` | Return product or empty |
| `save(Product product)` | `Product` | Assign ID if 0, store and return |
| `deleteById(int id)` | `boolean` | Remove; return true if it existed |

Use `ConcurrentHashMap` for the store and `AtomicInteger` for ID generation.
Pre-populate with two seed products in the constructor.

---

## Exercise 2 — Create ProductService (15 min)

Create `src/main/java/com/example/api/service/ProductService.java`.

The service must:
- Accept a `ProductRepository` via its constructor (not create one internally)
- Delegate all data access to the repository
- Expose these methods:

| Method | Return type |
|--------|-------------|
| `getAllProducts()` | `List<Product>` |
| `getProduct(int id)` | `Optional<Product>` |
| `createProduct(Product p)` | `Product` |
| `updateProduct(int id, Product p)` | `Optional<Product>` |
| `deleteProduct(int id)` | `boolean` |

`updateProduct` should return `Optional.empty()` if the ID does not exist.

---

## Exercise 3 — Refactor ProductHandler (15 min)

Refactor `ProductHandler` so that:
- It accepts a `ProductService` via its constructor (no direct store access)
- All business logic is delegated to the service
- The handler only deals with HTTP: reading requests, sending responses

Update `App.java` to wire the layers together:

```java
ProductRepository repository = new ProductRepository();
ProductService    service    = new ProductService(repository);
ProductHandler    handler    = new ProductHandler(service);
server.createContext("/api/products", handler);
```

Run the application and verify all five endpoints still work with curl.

---

## Exercise 4 — Write Unit Tests for ProductService (20 min)

Create `src/test/java/com/example/api/service/ProductServiceTest.java`.

Use `@ExtendWith(MockitoExtension.class)` and `@Mock`/`@InjectMocks`.

Write tests for:

1. `getAllProducts` — mock repository to return a list; assert size
2. `getProduct` — found case: mock `findById` to return a product; assert present and name
3. `getProduct` — not found case: mock `findById` to return empty; assert empty
4. `createProduct` — mock `save`; assert the saved product is returned
5. `updateProduct` — found case: mock `findById` and `save`; assert updated name
6. `updateProduct` — not found case: mock `findById` to return empty; assert empty
7. `deleteProduct` — true case and false case

Run tests: `./gradlew test`

---

## Exercise 5 — Verify with verify() (10 min)

In your `getAllProducts` test, add a `verify` assertion to confirm the repository method
was called exactly once:

```java
verify(repository, times(1)).findAll();
```

Add a test that confirms `createProduct` calls `repository.save()` with the correct argument:

```java
verify(repository).save(incoming);
```

---

## Acceptance Criteria

- [ ] `ProductRepository` manages the store; `ProductService` has no store access
- [ ] `ProductService` receives its repository via constructor
- [ ] `ProductHandler` receives its service via constructor
- [ ] `App.java` wires the three layers manually
- [ ] All five curl endpoints still return correct responses
- [ ] All unit tests pass: `./gradlew test`
- [ ] At least one test uses `verify()` to assert repository interactions
