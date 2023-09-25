# Transactional Limit Service

RESTful приложение. Сервис прототип микросервиса, который будет интегрирован в существующую банковскую систему.

* Transaction:
  * для приема транзакций (условно интеграция с банковскими сервисами)
  * получение списка транзакций, превысивших лимит,  
* Limit - клиентский, для внешних запросов от клиента: 
  * установление нового лимита, 
  * получение всех лимитов.

# Использованные технологии

* [Spring Boot](https://spring.io/projects/spring-boot) – как основной фрэймворк
* [PostgreSQL](https://www.postgresql.org/) – как основная реляционная база данных
* [Сassandra](https://cassandra.apache.org/_/index.html) – как NO SQL база данных для хранения данных о курсе.
* [testcontainers](https://testcontainers.com/) – для изолированного тестирования с базой данных
* [Liquibase](https://www.liquibase.org/) – для ведения миграций схемы БД
* [Gradle](https://gradle.org/) – как система сборки приложения

# База данных

* Liquibase сам накатывает нужные миграции на голый PostgreSql при старте приложения
* В тестах используется [testcontainers](https://testcontainers.com/), в котором тоже запускается отдельный инстанс
  postgres
* В коде продемонстрирована работа с JPA (Hibernate)

# Как запустить локально?

Сначала нужно развернуть окурежение
```shell
# Нужно запустить из корневой директории, где лежит docker-compose.yaml
docker-compose build
docker-compose up -d
```

Затем нужно создать keyspace для Cassandra

```shell
docker exec -it solva_techtask-master_cassandra-1 bash -c "cqlsh -u cassandra -p cassandra"
```

```shell
CREATE KEYSPACE spring_cassandra WITH replication = {'class' : 'SimpleStrategy', 'replication_factor' : 1};
```

Далее собрать gradle проект

```shell
# Нужно запустить из корневой директории, где лежит build.gradle.kts
gradle build
```

Запустить jar'ник

```shell
java -jar build/libs/service.jar
```

Но легче всё это делать через IDE

# Код

* Обычная трёхслойная
  архитектура – Controller, Service, Repository
* Слой Repository реализован на JPA (Hibernate)

## Actions
### Transaction

    URL                                           HTTP Method        Operation
    
    /api/v1/transactions/{accountNumber}/exceeded GET                Get all transactions by account number
    /api/v1/transactions                          POST               Recieve transaction
    
### Limit
    URL                                        HTTP Method        Operation

    /api/v1/limits/{accountNumber}/all         GET                Get all limits by account number
    /api/v1/limits                             POST               Create a new limit

# Тесты

* SpringBootTest
* MockMvc
* Testcontainers
* AssertJ
* JUnit5
* Parameterized tests
