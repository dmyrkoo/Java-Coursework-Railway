# 🚂 Railway Management System

**Курсова робота з дисципліни "Об'єктно-орієнтоване програмування"**

![Java](https://img.shields.io/badge/Language-Java_17-ED8B00?logo=java&logoColor=white)
![JavaFX](https://img.shields.io/badge/GUI-JavaFX_AtlantaFX-007AFF?logo=java)
![SQLite](https://img.shields.io/badge/Database-SQLite_JDBC-003B57?logo=sqlite&logoColor=white)
![Testing](https://img.shields.io/badge/Testing-JUnit_5-25A162?logo=junit5&logoColor=white)
![Status](https://img.shields.io/badge/Status-Release_v1.0-success)

**Railway Management System** — сучасна настільна система для управління рухомим складом потягу. Проєкт демонструє застосування принципів ООП, N-Tier архітектури та патерну CQS для створення швидкого, масштабованого та зручного програмного продукту. Система підтримує динамічне додавання вагонів, розширений пошук за пасажиромісткістю, сортування та збереження стану в базу даних SQLite.

---

## 📸 Інтерфейс

| Головне вікно | Підсвітка пошуку та вагонів |
|:---:|:---:|
| <img src="screenshots/main_screen.png" width="400" /> | <img src="screenshots/search_highlight.png" width="400" /> |

| Сортування списку | Додавання вагону |
|:---:|:---:|
| <img src="screenshots/sorted_list.png" width="400" /> | <img src="screenshots/add_wagon.png" width="400" /> |

---

## 🚀 Функціональні можливості

* **🔍 Гнучкий пошук:** Знаходження вагонів за діапазоном місткості пасажирів із динамічною підсвіткою у візуальній схемі.
* **💾 База даних SQLite:** Надійне збереження стану складу потяга (вагони, класи комфорту, багаж, персонал) локально через JDBC.
* **➕ Динамічне поповнення:** Можливість додавати нові пасажирські та службові вагони, які миттєво зберігаються в БД та відображаються у схемі потяга.
* **📜 Сортування:** Інтелектуальне сортування вагонів за рівнем комфортності та класом.
* **✨ Modern UI:** Адаптивний графічний інтерфейс на базі `JavaFX` із застосуванням преміальної теми `AtlantaFX` (PrimerLight) та динамічною колірною підсвіткою.
* **🛠 Архітектура:** Суворе дотримання N-Tier архітектури (Presentation -> Service -> Data Access) з використанням CQS та Command Pattern. Повна відсутність протікання логіки між шарами.

---

## 🛠️ Технічний стек

### Core & Backend
* **Мова:** Java 17
* **Архітектурні патерни:** N-Tier, Command/Query Separation, Command Pattern, Constructor Dependency Injection.
* **База даних:** SQLite (через JDBC) — файл `potiag.db`
* **Логування:** Logback + SLF4J
* **Тестування:** JUnit 5

### Frontend (UI)
* **Фреймворк:** JavaFX
* **Тема / Стилі:** AtlantaFX (PrimerLight theme)

---

## 📂 Структура проєкту

```text
Railway-Management-System/
├── src/main/java/            # Вихідний код
│   ├── commands/             # Реалізація CQS та паттерну Command
│   ├── main/                 # Точка входу
│   ├── model/                # Доменні моделі (Вагони, потяг, Enum)
│   ├── repository/           # Рівень доступу до даних (SQLite JDBC)
│   ├── services/             # Бізнес-логіка (SkladService, PotiagService)
│   ├── ui/                   # Графічний інтерфейс (JavaFX + AtlantaFX)
│   └── utils/                # Допоміжні утиліти (File Manager)
├── src/main/resources/       # Ресурси
│   └── logback.xml           # Конфігурація логування
├── src/test/                 # Unit тести (JUnit 5)
├── screenshots/              # Зображення для README
├── pom.xml                   # Maven конфігурація та залежності
└── potiag.db                 # Файл локальної БД SQLite (генерується автоматично)
```

## ⚙️ Інструкція із запуску

### Запуск додатку
Для запуску проекту використовується Maven та плагін JavaFX. Відкрийте термінал у кореневій папці проекту і виконайте команду:

```bash
mvn clean javafx:run
```

Система автоматично:
1. Скомпілює проект та завантажить залежності.
2. Створить файл `potiag.db` (якщо його не існує) та заповнить його стартовими даними для демонстрації.
3. Запустить графічний інтерфейс програми.

---

## 👤 Автор

**Студент групи ОІ-22 Петрунів Дмитро**  
Національний університет "Львівська політехніка"

## 📅 Release

**Версія:** v1.0  
**Рік:** 2026
