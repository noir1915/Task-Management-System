# Task-Management-System
(Система управления заданиями)
Тестовое задание.

Описание задания:
Разработать простую систему управления задачами (Task Management System) с использованием Java. Система должна обеспечивать создание, редактирование, удаление и просмотр задач. Каждая задача должна содержать заголовок, описание, статус (например, "в ожидании", "в процессе", "завершено") и приоритет (например, "высокий", "средний", "низкий"), а также автора задачи и исполнителя. Реализовать необходимо только API.

Возможности:<br/>1.Сервис поддерживает аутентификацию и авторизацию пользователей по email и паролю.<br/>2.Доступ к API аутентифицирован с помощью JWT токена в заголовке HTTP запроса.
<br/>3. Пользователи могут управлять своими задачами: создавать новые, редактировать существующие, 
просматривать и удалять, менять статус и назначать исполнителей задачи.
4. Пользователи могут просматривать задачи других пользователей, 
а исполнители задачи могут менять только статус своих задач.
5. К задачам можно оставлять комментарии.
6. API позволяет получать задачи конкретного автора или исполнителя, а также все 
комментарии к ним. Обеспечена фильтрация и пагинация вывода.
7. Сервис корректно обрабатывает ошибки и возвращает понятные сообщения, а также 
валидирует входящие данные.
8. API описан с помощью Open API и Swagger. В сервисе настроен Swagger 
UI по адресу: http://localhost:8080/swagger-ui/index.html.
9. Реализовано несколько базовых интеграционных тестов для проверки основных функций 
контроллеров UserController и TaskController.
10. Использован язык Java 17, Spring, Spring Boot. В качестве БД используется PostgreSQL. 
Для реализации аутентификации и авторизации используется Spring Security.
Реализовано кеширование некоторых частых и ресурсоёмких запросов.
Запуск:
С помощью Maven (в системе должна быть установлена PostgreSQL):
./mvnw spring-boot:run
С помощью Docker compose (в системе должен быть установлен Maven, а 
также установлен и запущен Docker):
mvn package -DskipTests
docker-compose up
После запуска приложения переходим на HTTP endpoint http://localhost:8080/swagger-ui/index.html. 
На странице логина вводим данные одного из пользователей, описанных ниже, и получаем в ответе 
JWT токен, который необходимо добавить в форме авторизации.

В системе для демонстрации зарегистрировано 2 пользователя со следующими реквизитами:

email: adm@site.com, password: 123, role: ADMIN, id: 1
email: user@site.com, password: 123, role: USER, id: 2
Только пользователь с ролью ADMIN имеет права на удаление любых других пользователей в системе.

URL, доступные без авторизации: http://localhost:8080/users - демонстрирует список всех 
зарегистрированных пользователей http://localhost:8080/users/login - позволяет пользователю 
авторизоваться и получить JWT токен, который впоследствии должен при каждом запросе отправлен 
в HTTP заголовке "Authorization Bearer "+токен. http://localhost:8080/users/register - позволяет 
зарегистрировать нового пользователя в системе.

Также в системе для демонстрации присутсвует 2 задачи, автором которых является первый 
пользователь с id=1, и 2 комментария к ним.
