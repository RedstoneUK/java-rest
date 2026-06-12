# HTTP Methods Quick Reference

| Method | CRUD   | Safe | Idempotent | Body? | Typical Status |
|--------|--------|------|------------|-------|----------------|
| GET    | Read   | Yes  | Yes        | No    | 200            |
| POST   | Create | No   | No         | Yes   | 201            |
| PUT    | Update | No   | Yes        | Yes   | 200            |
| PATCH  | Update | No   | No         | Yes   | 200            |
| DELETE | Delete | No   | Yes        | No    | 204            |

## Safe
No side effects — the server state is unchanged.

## Idempotent
Repeating the same call N times gives the same result as calling it once.
POST is neither — each call creates a new resource.

## URI Design Rules
- Use nouns, not verbs: `/products` not `/getProducts`
- Collections are plural: `/orders`, `/users`
- Nested resources show ownership: `/users/42/orders`
- Query strings filter/sort: `/products?category=books&sort=price`
