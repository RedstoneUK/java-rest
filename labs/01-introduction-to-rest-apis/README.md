# Lab 01 — Exploring REST APIs

**Module:** Introduction to REST APIs  
**Duration:** 30 minutes  
**Tools:** curl (or Postman), a text editor

---

## Objectives

By the end of this lab you will be able to:

- Send GET, POST, PUT, and DELETE requests using curl
- Read HTTP status codes and response headers
- Identify REST constraints in a real API
- Design resource-oriented URIs for a simple domain

---

## Setup

Verify curl is available:

```bash
curl --version
```

You will use `https://jsonplaceholder.typicode.com` as a practice API throughout this lab.

---

## Exercise 1 — Reading Resources (10 min)

### 1.1 — Retrieve a single resource

```bash
curl -s https://jsonplaceholder.typicode.com/users/1
```

**Questions:**
- What HTTP method did you use?
- What does the `/1` segment in the URI represent?

### 1.2 — Retrieve a collection

```bash
curl -s https://jsonplaceholder.typicode.com/posts
```

How many posts are returned? (hint: count the `"id"` fields)

### 1.3 — Filter with query parameters

```bash
curl -s "https://jsonplaceholder.typicode.com/posts?userId=1"
```

How many posts belong to user 1?

---

## Exercise 2 — Writing Resources (10 min)

### 2.1 — Create a post (POST)

```bash
curl -s -X POST https://jsonplaceholder.typicode.com/posts \
  -H "Content-Type: application/json" \
  -d '{
    "title": "My First Post",
    "body": "Learning REST APIs",
    "userId": 1
  }'
```

**Questions:**
- What HTTP status code did the server return?
- What ID was assigned to the new post?

### 2.2 — Update a post (PUT)

```bash
curl -s -X PUT https://jsonplaceholder.typicode.com/posts/1 \
  -H "Content-Type: application/json" \
  -d '{
    "id": 1,
    "title": "Updated Title",
    "body": "Updated content",
    "userId": 1
  }'
```

### 2.3 — Delete a post (DELETE)

```bash
curl -s -X DELETE https://jsonplaceholder.typicode.com/posts/1
curl -si -X DELETE https://jsonplaceholder.typicode.com/posts/1 | head -5
```

What status code is returned?

---

## Exercise 3 — Inspecting Headers and Status Codes (5 min)

```bash
# Successful resource
curl -si https://jsonplaceholder.typicode.com/posts/1 | head -20

# Non-existent resource
curl -si https://jsonplaceholder.typicode.com/posts/99999 | head -5
```

**Questions:**
- What `Content-Type` header does the server send?
- What status code is returned for a missing resource?

---

## Exercise 4 — URI Design (5 min)

You are designing a REST API for an online bookshop. Write RESTful URIs for each operation
below. Do not use verbs in the path.

| Operation | Your URI | Method |
|-----------|----------|--------|
| List all books | | |
| Get book with ID 42 | | |
| List reviews for book 42 | | |
| Create a new book | | |
| Delete book 42 | | |
| Search books by author | | |

---

## Acceptance Criteria

- [ ] You have successfully sent GET, POST, PUT, and DELETE requests
- [ ] You can identify the status code and Content-Type from response headers
- [ ] Your URI design table uses nouns, not verbs, and follows collection/item patterns
