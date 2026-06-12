# Lab 02 — JSON and Java Object Conversion with Gson

**Module:** JSON and Java Object Conversion  
**Duration:** 45 minutes  
**Tools:** Java 17, Gradle, IDE

---

## Objectives

By the end of this lab you will be able to:

- Add Gson as a Gradle dependency
- Serialise a Java object to a JSON string
- Deserialise a JSON string into a Java object
- Handle lists and nested objects
- Customise Gson behaviour with GsonBuilder

---

## Setup

Create a new Gradle project (or use the provided starter):

```bash
mkdir json-lab && cd json-lab
gradle init --type java-application --dsl groovy
```

Add the Gson dependency to `build.gradle`:

```groovy
dependencies {
    implementation 'com.google.code.gson:gson:2.10.1'
}
```

---

## Exercise 1 — Create the Model Classes (10 min)

Create a `Customer` class in `src/main/java/com/example/` with the following fields:

| Field | Type |
|-------|------|
| `id` | `int` |
| `firstName` | `String` |
| `lastName` | `String` |
| `email` | `String` |
| `active` | `boolean` |

- Add a constructor that takes all five fields.
- Add getters for each field.

Create an `Address` class with fields: `street` (String), `city` (String), `postcode` (String).

Add an `address` field of type `Address` to `Customer`.

---

## Exercise 2 — Serialise to JSON (10 min)

In your `main` method (or a separate runner class):

1. Create a `Customer` instance with an `Address`.
2. Create a `Gson` instance with `new Gson()`.
3. Call `gson.toJson(customer)` and print the result.

**Expected output shape:**
```json
{"id":1,"firstName":"Alice","lastName":"Smith","email":"alice@example.com","active":true,"address":{"street":"10 High St","city":"London","postcode":"EC1A 1BB"}}
```

**Questions:**
- How does Gson determine the JSON key names?
- What happens to the `active` boolean field in the JSON output?

---

## Exercise 3 — Deserialise from JSON (10 min)

Take the JSON string produced in Exercise 2 and deserialise it back into a `Customer` object:

```java
String json = /* the string from Exercise 2 */;
Customer customer = gson.fromJson(json, Customer.class);
System.out.println(customer.getFirstName() + " lives in " + customer.getAddress().getCity());
```

Now try deserialising a JSON string that has an **extra field** the class does not define:

```java
String withExtra = "{\"id\":2,\"firstName\":\"Bob\",\"unknownField\":\"ignored\",\"lastName\":\"Jones\",\"email\":\"bob@example.com\",\"active\":false,\"address\":{\"street\":\"5 Main Rd\",\"city\":\"Manchester\",\"postcode\":\"M1 1AE\"}}";
Customer c2 = gson.fromJson(withExtra, Customer.class);
System.out.println(c2.getFirstName());
```

**Question:** What does Gson do with JSON keys that have no matching field in the class?

---

## Exercise 4 — Serialise a List (5 min)

Create a `List<Customer>` with at least three customers and serialise it:

```java
List<Customer> customers = List.of(/* ... */);
System.out.println(gson.toJson(customers));
```

Now deserialise a JSON array back into a list. Because of Java's type erasure you need a
`TypeToken`:

```java
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

Type listType = new TypeToken<List<Customer>>(){}.getType();
List<Customer> parsed = gson.fromJson(jsonArray, listType);
System.out.println("Count: " + parsed.size());
```

---

## Exercise 5 — GsonBuilder Customisation (10 min)

### 5.1 — Pretty printing

```java
Gson pretty = new GsonBuilder().setPrettyPrinting().create();
System.out.println(pretty.toJson(customer));
```

### 5.2 — Serialise null fields

By default Gson omits fields that are `null`. Add a customer with a `null` email and observe:

```java
Customer noEmail = new Customer(4, "Carol", "White", null, true, address);
System.out.println(gson.toJson(noEmail));                        // null omitted
System.out.println(new GsonBuilder().serializeNulls().create().toJson(noEmail)); // null included
```

### 5.3 — Custom field name with @SerializedName

Add the `@SerializedName` annotation to the `email` field to map it to `"email_address"` in JSON:

```java
@SerializedName("email_address")
private String email;
```

Serialise a customer and confirm the key is now `email_address`.

---

## Acceptance Criteria

- [ ] `Customer` and `Address` classes created with all fields and getters
- [ ] Serialisation produces correct JSON including nested `Address`
- [ ] Deserialisation correctly populates all fields; unknown JSON fields are ignored
- [ ] List serialisation and deserialisation using TypeToken works
- [ ] Pretty printing produces indented output
- [ ] Null serialisation controlled via GsonBuilder
- [ ] `@SerializedName` changes the key in the JSON output
