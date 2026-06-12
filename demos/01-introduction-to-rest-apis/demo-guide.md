# Demo: Module 01 — Introduction to REST APIs

**Duration:** 10 minutes  
**Prerequisite:** Browser open; curl or Postman installed

---

## Part 1: Exploring a Public REST API with curl (4 min)

Show the class what a real REST API looks like before writing any code.

**Prompt to run:**

```bash
curl -s https://jsonplaceholder.typicode.com/users/1
```

**Narration:** "This is a GET request to a public REST API. The server returns a JSON
representation of user 1. Notice we used the resource URI `/users/1` — that's a noun,
not a verb. The action is expressed by the HTTP method."

```bash
curl -s https://jsonplaceholder.typicode.com/posts?userId=1
```

**Narration:** "Query parameters let us filter a collection resource. This is still a GET
— we're reading, not changing anything."

---

## Part 2: Mapping CRUD to HTTP Methods (3 min)

Walk through the four CRUD operations against the same API.

```bash
# CREATE — POST sends a body; server allocates the ID
curl -s -X POST https://jsonplaceholder.typicode.com/posts \
  -H "Content-Type: application/json" \
  -d '{"title":"Hello REST","body":"First post","userId":1}'

# UPDATE — PUT replaces the whole resource
curl -s -X PUT https://jsonplaceholder.typicode.com/posts/1 \
  -H "Content-Type: application/json" \
  -d '{"id":1,"title":"Updated","body":"Changed","userId":1}'

# DELETE
curl -s -X DELETE https://jsonplaceholder.typicode.com/posts/1
```

**Narration:** "Same base URL `/posts`, different HTTP methods — that's the REST
constraint of uniform interface. We're not calling `/createPost` or `/deletePost`."

---

## Part 3: Reading HTTP Headers and Status Codes (3 min)

```bash
curl -si https://jsonplaceholder.typicode.com/posts/1 | head -20
curl -si https://jsonplaceholder.typicode.com/posts/99999 | head -5
```

**Narration:** "The `-i` flag includes response headers. A 200 means success. The second
call returns 404 — resource not found. Status codes carry meaning; we'll design our own
API to use them correctly."

---

## Key message

REST is a set of constraints on top of HTTP — uniform interface, stateless, resource-oriented
URIs — not a protocol or library. Everything we build this week will follow these constraints.
