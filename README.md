# UserManagementService

Сервис пользователей. Java 21, Spring Boot 3.5, PostgreSQL.

## Запуск

```
docker compose up -d
./gradlew bootRun
```

Порт 9000.

## Ручки

```
GET  /api/v1/users
GET  /api/v1/users/{id}
GET  /api/v1/users/{id}/roles
POST /api/v1/users/register
GET  /api/v1/roles
```

## Пока нет

```
POST   /api/v1/users/login
PATCH  /api/v1/users/{id}
GET    /api/v1/users?ids=
GET    /api/v1/users?role=&page=&limit=
POST   /api/v1/users/{id}/roles
DELETE /api/v1/users/{id}/roles/{role_id}
```

Авторизация по JWT (Spring Security).
