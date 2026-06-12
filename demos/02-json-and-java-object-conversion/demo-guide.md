# Demo: Module 02 — JSON and Java Object Conversion

**Duration:** 12 minutes  
**Prerequisite:** Gradle project open in IDE with Gson dependency added to build.gradle

---

## Part 1: Serialising a Java Object to JSON (4 min)

Show Gson converting a plain Java object to a JSON string.

Open `Product.java` — walk through the fields briefly.

```java
Gson gson = new Gson();
Product product = new Product(1, "Laptop", 999.99, true);
String json = gson.toJson(product);
System.out.println(json);
```

**Expected output:**
```json
{"id":1,"name":"Laptop","price":999.99,"inStock":true}
```

**Narration:** "Gson reflects over the object's fields and builds the JSON automatically.
No annotations, no configuration — just `new Gson()` and `toJson()`. Field names map
directly to JSON keys."

---

## Part 2: Deserialising JSON into a Java Object (3 min)

```java
String incoming = "{\"id\":2,\"name\":\"Mouse\",\"price\":29.99,\"inStock\":false}";
Product parsed = gson.fromJson(incoming, Product.class);
System.out.println(parsed.getName() + " costs " + parsed.getPrice());
```

**Narration:** "The reverse is equally simple. `fromJson` takes the raw JSON string and
the target class. Gson maps each key back to the matching field by name."

---

## Part 3: Collections and Nested Objects (3 min)

Show serialising a list and a nested object.

```java
// List of products
List<Product> products = List.of(
    new Product(1, "Laptop", 999.99, true),
    new Product(2, "Mouse", 29.99, true)
);
System.out.println(gson.toJson(products));

// Nested object - Order containing a Product
Order order = new Order(101, products.get(0), "PENDING");
System.out.println(gson.toJson(order));
```

**Narration:** "Gson handles lists and nested objects transparently. The entire object
graph is serialised recursively."

---

## Part 4: Edge Cases — Null Fields and Pretty Printing (2 min)

```java
// Null fields are omitted by default
Product noName = new Product(3, null, 0.0, false);
System.out.println(gson.toJson(noName));

// Pretty printing for readability
Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();
System.out.println(prettyGson.toJson(product));
```

**Narration:** "By default, null fields are omitted from the output. Use `GsonBuilder`
to customise behaviour — pretty printing is useful during development and debugging."

---

## Key message

Gson gives you JSON serialisation in two method calls: `toJson()` and `fromJson()`.
Everything we receive from HTTP clients and send back as responses will flow through
these two methods.
