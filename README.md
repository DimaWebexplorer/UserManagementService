# UserManagementService

Сервис пользователей. Java 21, Spring Boot 3.5, PostgreSQL.

## запуск

```bash
docker compose up -d
./gradlew bootRun
```

Порт 9000. Ручки можно проверить в Postman.

## part1

| метод | путь | что делает |
|---|---|---|
| GET | /api/v1/users | все юзеры с ролями |
| GET | /api/v1/users/{id} | юзер по id |
| GET | /api/v1/users/{id}/roles | роли юзера |
| POST | /api/v1/users/register | регистрация, роль subscriber |
| GET | /api/v1/roles | справочник ролей |

## part2

| метод | путь | что добавилось |
|---|---|---|
| POST | /api/v1/users/login | вход, выдача JWT |
| PATCH | /api/v1/users/{id} | изменение своего профиля |
| GET | /api/v1/users?ids= | имена по списку id |
| GET | /api/v1/users?role=&page=&limit= | список для админа с фильтром и пагинацией |
| POST | /api/v1/users/{id}/roles | назначить роль |
| DELETE | /api/v1/users/{id}/roles/{role_id} | снять роль |

Добавлены JWT и проверка прав через Spring Security. Без токена доступны регистрация и вход. Список юзеров и управление ролями — только для админа. GET /users теперь возвращает users, total, page и limit.

Поля и параметры проверяются валидатором до вызова сервиса. Ошибки возвращаются в формате code, error, message, data.
