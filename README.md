#Дипломная работа : блоговый движок

Реализованный блоговый движок.Приложение для введения блога; есть возможность
зарегистрировать аккаунт и делать публикации,а также писать комментарии и оценивать
публикации других пользователей.Возможен поиск нужных публикаций по разным критериям.

##Используемые технологии

* [Spring Framework](https://spring.io/)
* Сборщик проекта - [Maven](https://maven.apache.org/)
* Способ реализации аутентификации [Spring Security](https://spring.io/projects/spring-security)
* Для тестирования возможных проблем - [log4j2](https://logging.apache.org/log4j/2.x/)
* Используемая БД - [Postgresql](https://www.postgresql.org/)
* Программа для миграции [Flyway](https://flywaydb.org/)

##Для локального запуска приложения требуется

* [Java Development Kit 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
* [Postgresql 14.4](https://www.enterprisedb.com/downloads/postgres-postgresql-downloads)

###Настройка переменных сред

Нужно прописать следующее:

* system variables -> создать новую переменную JAVA_HOME и в ней также указать C:\Program Files\jdk(или собственный путь до папки jdk)
* system variables -> Path -> новая строка C:\Program Files\jdk\bin(или собственный путь до папки %JAVA_HOME%/bin)
* user variables -> Path -> новая строка C:\Program Files\Postgresql\bin(или собственный путь до папки bin PSQL)
* user variables -> Path -> новая строка C:\Program Files\Postgresql\lib(или собственный путь до папки lib PSQL)

###Подготовка базы данных
      
* откройте pgAdmin в папке C:\Program Files\Postgresql\pgAdmin 4\bin
* создайте аккаунт(superuser) c именем _postgres_ и паролем _postgretest_
* скачайте [кластер](https://disk.yandex.ru/d/lNG5Pm98ERrLuw)
* откройте cmd в папке со скаченным файлом(cd **путь до папки с файлом**)
* пропишите 
```
 psql -f dumpall.sql postgres
```

###запуск

* скачайте само [приложение](https://disk.yandex.ru/d/-Klcox1o3QCAWA)
* откройте cmd и перейдите в папку со скаченным файлом
* запустите через команду 
```
 java -jar DiplomaRepository-0.0.1-SNAPSHOT.jar
```

##ссылкa на рабочий проект (на деплой heroku)

[ссылка](https://diploma-project-baumans.herokuapp.com)


