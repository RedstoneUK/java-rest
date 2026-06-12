# Demo: Module 04 — Separation of Concerns and Unit Testing

**Duration:** 12 minutes  
**Prerequisite:** Module 03 project open; JUnit 5 and Mockito added to build.gradle

---

## Part 1: The Problem with a Single Handler Class (2 min)

Open the Module 03 `ProductHandler.java`. Point out that it does everything:
- HTTP routing
- Business logic (ID assignment, validation)
- Data storage

**Narration:** "This class has too many reasons to change. If we want to swap the storage
layer, we have to touch the handler. If the business rules change, we're editing HTTP code.
And we can't unit test any of it without starting an HTTP server."

---

## Part 2: Introducing the Layered Architecture (2 min)

Draw or show the three-layer diagram on the whiteboard/slides:

```
HttpHandler  →  ProductService  →  ProductRepository
(HTTP/JSON)     (business logic)    (data access)
```

**Narration:** "Each layer has one job. The handler speaks HTTP. The service owns the
business rules. The repository owns data access. Dependencies point downward only."

---

## Part 3: Live Refactor — Extract ProductRepository (3 min)

Show creating `ProductRepository.java`:

```java
public class ProductRepository {
    private final Map<Integer, Product> store = new ConcurrentHashMap<>();
    private final AtomicInteger nextId = new AtomicInteger(1);

    public List<Product> findAll() {
        return new ArrayList<>(store.values());
    }

    public Optional<Product> findById(int id) {
        return Optional.ofNullable(store.get(id));
    }

    public Product save(Product product) {
        if (product.getId() == 0) {
            product.setId(nextId.getAndIncrement());
        }
        store.put(product.getId(), product);
        return product;
    }

    public boolean deleteById(int id) {
        return store.remove(id) != null;
    }
}
```

**Narration:** "The repository knows nothing about HTTP. It just stores and retrieves products."

---

## Part 4: Extract ProductService and Wire It Up (3 min)

```java
public class ProductService {
    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public List<Product> getAllProducts() {
        return repository.findAll();
    }

    public Optional<Product> getProduct(int id) {
        return repository.findById(id);
    }

    public Product createProduct(Product product) {
        return repository.save(product);
    }

    public Optional<Product> updateProduct(int id, Product product) {
        return repository.findById(id).map(existing -> {
            product.setId(id);
            return repository.save(product);
        });
    }

    public boolean deleteProduct(int id) {
        return repository.deleteById(id);
    }
}
```

**Narration:** "The service receives its repository via constructor injection. This is the
key move that makes testing possible — we can inject a mock repository in tests."

---

## Part 5: Writing a Unit Test with Mockito (2 min)

```java
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    ProductRepository repository;

    @InjectMocks
    ProductService service;

    @Test
    void getProduct_returnsProduct_whenFound() {
        Product product = new Product(1, "Laptop", 999.99);
        when(repository.findById(1)).thenReturn(Optional.of(product));

        Optional<Product> result = service.getProduct(1);

        assertTrue(result.isPresent());
        assertEquals("Laptop", result.get().getName());
    }

    @Test
    void getProduct_returnsEmpty_whenNotFound() {
        when(repository.findById(99)).thenReturn(Optional.empty());
        assertTrue(service.getProduct(99).isEmpty());
    }
}
```

**Narration:** "No HTTP server, no real database. We mock the repository, define what it
returns, and test the service in complete isolation. This runs in milliseconds."

---

## Key message

Layered architecture makes each class testable in isolation. Constructor injection is the
mechanism — if a class takes its dependencies as constructor arguments, you can always
substitute a mock in tests.
