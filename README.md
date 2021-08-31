# FileUploader

Проект по теме «Разработка клиент-серверного приложения средствами Java Enterprise Edition»

Указания к выполнению задания:

Все классы должны удовлетворять Code Conventions for the Java Programming Language и принципам SOLID

В каждом классе должны быть описаны конструкторы, инициализирующие поля классов, методы получения и установки значений по логике программы, метод toString, методы hashCode и equals

Используемые средства и технологии:

•	Java EE / Spring Boot / Spring Framework

•	JBoss WildFly / Apache Tomcat

•	Servlet API

•	MySQL DB

•	JDBC / Hibernate

•	Java FX / Swing

•	Gson

Техническое задание:

Разработать клиент-серверное приложение, используя вышеперечисленные технологии, для осуществления: 

•	Загрузки файлов на сервер и дальнейшего их хранения на нем. Клиентская часть приложения позволяет произвести выбор папки для просмотра всех папок и файлов в ней. По нажатию на кнопку выгрузить осуществляется перебор всех папок и файлов, а так же подпапок, и с учетом иерархии сохраняются данные файлы и папки на сервере. Если какая-то папка уже есть на сервере в текущей иерархии, то ее заново создавать не нужно. Если в конкретной папке уже имеется файл с таким же названием, то проверяется его хеш-сумма. Если хеш-суммы файлов различны, то производить сохранение данного файла с учетом версии, если одинаковы, то не делать ничего. Если в иерархии папок присутствует архив zip, то необходимо его распаковать в ту же директорию, где он находится, название распакованной директории такое же, как название архива. Все файлы из архива обрабатываются по вышеописанным правилам. 

•	Просмотр иерархии папок сервера

•	Скачивания выбранных файлов с сервера

Серверная часть приложения
Разрабатывается REST API, генерирующее ответы в формате JSON. 
Веб интерфейс должен поддерживать следующий функционал:
1.	Проверка существования директории 
2.	Создание новой директории 
3.	Загрузку файла 
4.	Получение файла/файлов
5.	Получение информации о файле/файлах (имя, хеш-сумма, версия и тд.)


Клиентская часть приложения
Представляет собой набор форм графического интерфейса, которые взаимодействуют друг с другом. Из форм происходит обращение к API серверного приложения через http соединение, посылка запроса на сервер и получение от него ответа в формате JSON, далее его парсинг и отображение результатов в виде элементов пользовательского интерфейса. 
1.	Клиентская часть поддерживает просмотр директорий клиента и сервера
2.	Загрузку данных на сервер и скачивание их
3.	Удаление файлов 


![1](https://user-images.githubusercontent.com/79397536/131583089-18d2c2a8-2456-4752-acf5-12ea4c3c2e80.PNG)

![2](https://user-images.githubusercontent.com/79397536/131583086-33477707-277d-4882-a306-bd5de491c136.PNG)

![3](https://user-images.githubusercontent.com/79397536/131583088-7cb081e6-483b-46f5-a301-c75c4e8d85ad.PNG)
