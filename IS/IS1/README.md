\# IS1 — управление объектами Vehicle



Лабораторная работа №1 по дисциплине «Информационные системы».



Веб-приложение для работы с объектами `Vehicle` и связанными с ними `Coordinates`: создание, просмотр, изменение и удаление, таблица с пагинацией, фильтрацией и сортировкой, обновление таблицы у всех клиентов в реальном времени и пять специальных операций.



\## Технологии



\- Jakarta EE 11: JAX-RS, CDI, JPA, Bean Validation, WebSocket

\- EclipseLink (реализация JPA)

\- WildFly 37

\- PostgreSQL

\- Фронтенд: HTML, CSS, JavaScript без фреймворков



\## Структура



```

src/main/java/com/rosgard/

├── entity/       сущности Vehicle, Coordinates и перечисления VehicleType, FuelType

├── repository/   доступ к БД через EntityManager и JPQL

├── service/      бизнес-логика, транзакции, валидация, спецоперации

├── websocket/    рассылка изменений клиентам

└── rest/         REST API и обработка ошибок

src/main/resources/META-INF/persistence.xml

src/main/webapp/  страницы, стили, скрипты и картинки типов транспорта

docs/             отчёт и UML-диаграммы

```



\## REST API (`/api`)



| Метод | Путь | Что делает |

|---|---|---|

| GET | `/vehicles?page\&size\&sort\&asc\&filterField\&filterValue` | страница таблицы и общее число объектов |

| GET | `/vehicles/{id}` | один объект |

| POST | `/vehicles` | создать объект |

| PUT | `/vehicles/{id}` | изменить объект |

| DELETE | `/vehicles/{id}` | удалить объект |

| GET | `/coordinates` | все координаты |

| GET | `/coordinates/usage` | координаты и число объектов, которые на них ссылаются |

| DELETE | `/coordinates/{id}?reassignTo` | удалить координаты, привязав объекты к другим |

| DELETE | `/special/fuel-type/{fuelType}` | удалить все объекты с указанным типом топлива |

| GET | `/special/oldest` | объект с самой ранней датой создания |

| GET | `/special/fuel-type-greater/{fuelType}` | объекты с типом топлива больше заданного |

| GET | `/special/engine-power?min\&max` | объекты с мощностью двигателя в диапазоне |

| POST | `/special/{id}/add-wheels?count` | добавить колёса объекту |



WebSocket `/ws/vehicles` сообщает клиентам об изменениях после успешного коммита транзакции, и таблица перезагружается во всех открытых вкладках.



\## Запуск



1\. Создать в PostgreSQL базу для приложения.

2\. Добавить в WildFly datasource с JNDI-именем `java:jboss/datasources/IS1`, указывающий на эту базу.

3\. Собрать: `mvn clean package`, результат `target/IS1.war`.

4\. Задеплоить `IS1.war` в WildFly и открыть `http://localhost:8080/IS1/`.



Таблицы создаёт EclipseLink при деплое.



\## Как устроена история в репозитории



Код выложен цепочкой pull request'ов по слоям приложения, снизу вверх. Каждая ветка создана от предыдущей, и у каждого PR base — предыдущая ветка, поэтому в PR видны только файлы своего слоя:



| Ветка | Base | Содержимое |

|---|---|---|

| `is1/model` | `main` | Maven-проект, persistence.xml, сущности |

| `is1/repository` | `is1/model` | репозитории |

| `is1/service` | `is1/repository` | сервисный слой |

| `is1/websocket` | `is1/service` | WebSocket |

| `is1/rest` | `is1/websocket` | REST API и обработка ошибок |

| `is1/frontend` | `is1/rest` | веб-интерфейс |

| `is1/report` | `is1/frontend` | отчёт, диаграммы, этот README |

