# Learnext Backend API Docs

Base URL for local development:

```text
http://localhost:1201
```

Swagger/OpenAPI:

- Swagger UI: `http://localhost:1201/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:1201/v3/api-docs`

All application endpoints are prefixed with `/api/v1`.

## Authentication

Most user-specific endpoints require a JWT access token:

```http
Authorization: Bearer <accessToken>
```

Admin endpoints require:

```http
X-Admin-Key: <admin-key>
```

Instructor course endpoints require:

```http
X-Instructor-Id: <instructor-user-uuid>
```

Swagger and OpenAPI docs are public:

- `GET /swagger-ui/**`
- `GET /v3/api-docs/**`

## Standard Response Shape

Most feature APIs return an `ApiResponse<T>`:

```json
{
  "success": true,
  "data": {}
}
```

Some legacy user/teacher endpoints return the response object directly.

## Public And Auth APIs

| Method | Endpoint | Auth | Description |
| --- | --- | --- | --- |
| `POST` | `/api/v1/auth/register` | Public | Register a user. |
| `POST` | `/api/v1/auth/login` | Public | Login and receive access/refresh tokens. |
| `POST` | `/api/v1/auth/refresh` | Public | Refresh an access token. |
| `POST` | `/api/v1/auth/logout` | Bearer token | Revoke a refresh token. |
| `POST` | `/api/v1/auth/logout-all` | Bearer token | Revoke all current user sessions. |
| `GET` | `/api/v1/courses` | Public | Search/list courses. |
| `GET` | `/api/v1/courses/{id}` | Public | Get course detail. |
| `GET` | `/api/v1/categories` | Public | List categories. |
| `GET` | `/api/v1/courses/{courseId}/reviews` | Public | List course reviews. |
| `GET` | `/api/v1/courses/{courseId}/comments` | Public | List course comments. |
| `POST` | `/api/v1/support/leads` | Public | Submit a support lead/contact request. |

## User And Profile

| Method | Endpoint | Auth | Description |
| --- | --- | --- | --- |
| `POST` | `/api/v1/users/new` | Public/legacy | Create a user. |
| `GET` | `/api/v1/users` | Bearer token | List users. |
| `GET` | `/api/v1/users/{id}` | Bearer token | Get user by ID. |
| `PATCH` | `/api/v1/users/{id}` | Bearer token | Update user basic fields. |
| `DELETE` | `/api/v1/users/{id}` | Bearer token | Delete user. |
| `GET` | `/api/v1/profile/me` | Bearer token | Get current user profile. |
| `PATCH` | `/api/v1/profile/me` | Bearer token | Update current user profile. |

## Course, Instructor, Upload

| Method | Endpoint | Auth | Description |
| --- | --- | --- | --- |
| `POST` | `/api/v1/teachers/new` | Public/legacy | Create a teacher account. |
| `POST` | `/api/v1/uploads/signed-url` | Public in current security config | Create a signed MinIO upload URL. |
| `GET` | `/api/v1/instructor/courses` | `X-Instructor-Id` | List instructor's courses. |
| `POST` | `/api/v1/instructor/courses` | `X-Instructor-Id` | Create a course. |
| `PUT` | `/api/v1/instructor/courses/{courseId}` | `X-Instructor-Id` | Update a course. |
| `PATCH` | `/api/v1/instructor/courses/{courseId}/publish` | `X-Instructor-Id` | Publish a course. |
| `DELETE` | `/api/v1/instructor/courses/{courseId}` | `X-Instructor-Id` | Archive a course. |

## Orders, Payments, Activation

| Method | Endpoint | Auth | Description |
| --- | --- | --- | --- |
| `POST` | `/api/v1/orders` | Bearer token | Create an order for a course. |
| `POST` | `/api/v1/orders/{orderId}/proof` | Bearer token | Submit payment proof URL. |
| `GET` | `/api/v1/orders` | Bearer token | Get current user's order history. |
| `GET` | `/api/v1/admin/orders` | `X-Admin-Key` | List all orders for admin review. |
| `PATCH` | `/api/v1/admin/orders/{orderId}/confirm` | `X-Admin-Key` | Confirm payment and generate activation code. |
| `PATCH` | `/api/v1/admin/orders/{orderId}/reject` | `X-Admin-Key` | Reject payment. |
| `POST` | `/api/v1/activations/activate` | Bearer token | Activate course access with an activation code. |

## Learning And Interactions

| Method | Endpoint | Auth | Description |
| --- | --- | --- | --- |
| `GET` | `/api/v1/learning/enrollments` | Bearer token | List current user's enrollments. |
| `GET` | `/api/v1/learning/courses/{courseId}/access` | Bearer token | Check if current user can access a course. |
| `POST` | `/api/v1/learning/courses/{courseId}/complete` | Bearer token | Mark a lesson complete. |
| `GET` | `/api/v1/favorites` | Bearer token | List favorite courses. |
| `POST` | `/api/v1/favorites/{courseId}` | Bearer token | Add course to favorites. |
| `DELETE` | `/api/v1/favorites/{courseId}` | Bearer token | Remove course from favorites. |
| `POST` | `/api/v1/courses/{courseId}/reviews` | Bearer token | Create or update course review. |
| `POST` | `/api/v1/courses/{courseId}/comments` | Bearer token | Add course comment. |

## Admin And Misc

| Method | Endpoint | Auth | Description |
| --- | --- | --- | --- |
| `GET` | `/api/v1/admin/support/leads` | `X-Admin-Key` | List support leads. |
| `GET` | `/api/v1/greeting/` | Bearer token | Health/demo greeting endpoint. |

## Request Body Reference

### Auth

`POST /api/v1/auth/register`

```json
{
  "email": "student@example.com",
  "password": "password123",
  "fullName": "Student Name"
}
```

`POST /api/v1/auth/login`

```json
{
  "email": "student@example.com",
  "password": "password123"
}
```

`POST /api/v1/auth/refresh` and `POST /api/v1/auth/logout`

```json
{
  "refreshToken": "<refresh-token>"
}
```

### Courses

`GET /api/v1/courses` query parameters:

| Name | Type | Notes |
| --- | --- | --- |
| `q` | string | Search keyword. |
| `categoryId` | UUID | Filter by category. |
| `instructorId` | UUID | Filter by instructor. |
| `minPrice` | number | Minimum price. |
| `maxPrice` | number | Maximum price. |
| `hasPreview` | boolean | Filter courses with preview video. |
| `sort` | string | `relevance`, `newest`, `priceAsc`, `priceDesc`, `rating`. |

`POST /api/v1/instructor/courses` and `PUT /api/v1/instructor/courses/{courseId}`

```json
{
  "title": "Course title",
  "description": "Course description",
  "price": 199000,
  "category": "Programming",
  "thumbnailUrl": "https://example.com/thumbnail.jpg",
  "previewVideoUrl": "https://example.com/preview.mp4"
}
```

Required fields: `title`, `category`.

### Uploads

`POST /api/v1/uploads/signed-url`

```json
{
  "fileName": "thumbnail.jpg",
  "contentType": "image/jpeg",
  "size": 123456
}
```

### Orders And Payments

`POST /api/v1/orders`

```json
{
  "courseId": "00000000-0000-0000-0000-000000000000"
}
```

`POST /api/v1/orders/{orderId}/proof`

```json
{
  "paymentProofUrl": "https://example.com/payment-proof.jpg"
}
```

`POST /api/v1/activations/activate`

```json
{
  "activationCode": "ABC123"
}
```

### Profile And Users

`PATCH /api/v1/profile/me`

```json
{
  "fullName": "Student Name",
  "bio": "Short bio",
  "phone": "0900000000"
}
```

`PATCH /api/v1/users/{id}`

```json
{
  "fullName": "Student Name",
  "avatarUrl": "https://example.com/avatar.jpg"
}
```

### Reviews And Comments

`POST /api/v1/courses/{courseId}/reviews`

```json
{
  "rating": 5,
  "content": "Great course"
}
```

`POST /api/v1/courses/{courseId}/comments`

```json
{
  "content": "Question or comment"
}
```

### Learning

`POST /api/v1/learning/courses/{courseId}/complete`

```json
{
  "lessonId": "00000000-0000-0000-0000-000000000000"
}
```

### Support

`POST /api/v1/support/leads`

```json
{
  "name": "Customer Name",
  "email": "customer@example.com",
  "category": "billing",
  "subject": "Payment question",
  "message": "I need help with my payment."
}
```

## Common Response Models

`TokenResponse`

```json
{
  "accessToken": "<jwt>",
  "refreshToken": "<refresh-token>",
  "accessTokenExpiresIn": 900
}
```

`CourseResponse`

```json
{
  "id": "00000000-0000-0000-0000-000000000000",
  "title": "Course title",
  "description": "Course description",
  "price": 199000,
  "thumbnailUrl": "https://example.com/thumbnail.jpg",
  "previewVideoUrl": "https://example.com/preview.mp4",
  "hasPreview": true,
  "rating": 4.8,
  "status": "PUBLISHED",
  "categoryId": "00000000-0000-0000-0000-000000000000",
  "categoryName": "Programming",
  "instructorId": "00000000-0000-0000-0000-000000000000",
  "instructorName": "Instructor Name",
  "createdAt": "2026-06-06T12:00:00Z"
}
```

`OrderResponse`

```json
{
  "id": "00000000-0000-0000-0000-000000000000",
  "courseId": "00000000-0000-0000-0000-000000000000",
  "courseTitle": "Course title",
  "amount": 199000,
  "paymentProofUrl": "https://example.com/payment-proof.jpg",
  "status": "PENDING",
  "createdAt": "2026-06-06T12:00:00Z"
}
```

`AdminPaymentResponse`

```json
{
  "order": {
    "id": "00000000-0000-0000-0000-000000000000",
    "courseId": "00000000-0000-0000-0000-000000000000",
    "courseTitle": "Course title",
    "amount": 199000,
    "paymentProofUrl": "https://example.com/payment-proof.jpg",
    "status": "PAID",
    "createdAt": "2026-06-06T12:00:00Z"
  },
  "activationCode": "ABC123"
}
```

## Notes

- UUID values in examples use `00000000-0000-0000-0000-000000000000`; replace them with real IDs.
- Validation rules are defined by request DTO annotations and are also visible in Swagger.
- For exact live schemas, use `/v3/api-docs`.
