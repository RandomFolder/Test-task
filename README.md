# Консольное приложение для работы с БД
## Что нужно установить для запуска
1. Java JDK
2. Maven
3. SQLite

## Запуск исходного кода
1. Зайдите в корневую папку проекта
2. Введите команду:
``` bash
mvn exec:java -D"exec.mainClass"="com.example.Main" -D"exec.args"="<аргументы>"
```

## Создание .jar файла
1. Зайдите в корневую папку
2. Введите команду:
``` bash
mvn clean compile assembly:single
```