Создание статьи:

`curl -X POST http://localhost:1111/api/articles \
-H "Content-Type: application/json" \
-d '{"name": "Заголовок статьи", "tags": ["тег1", "тег2"]}'`

Получение всех статей:

`curl -X GET http://localhost:1111/api/articles`

Получение статьи по id:

`curl -X GET http://localhost:1111/api/articles/1`

Редактирование статьи по id:

`curl -X PUT http://localhost:1111/api/articles/1 \
-H "Content-Type: application/json" \
-d '{"name": "Обновленный заголовок статьи", "tags": ["новый тег1", "новый тег2"]}'`

Создание комментария:

`curl -X POST http://localhost:1111/api/articles/1/comments -H "Content-Type: application/json" -d '{"text": "Это новый комментарий"}'`

Получение статьи по id:

`curl -X GET http://localhost:1111/api/articles/1/comments`

Получение комментария по id:

`curl -X GET http://localhost:1111/api/articles/1/comments/1`

Получение всех комментариев по id статьи:

`curl -X GET http://localhost:1111/api/articles/1/comments`

Обновление комментария по id:

`curl -X PUT http://localhost:1111/api/articles/1/comments/1 \
-H "Content-Type: application/json" \
-d '{"text": "Обновленный текст комментария"}'`

Удаление статьи:

`curl -X DELETE http://localhost:1111/api/articles/1`

Удаление комментария:

`curl -X DELETE http://localhost:1111/api/articles/1/comments/1`

