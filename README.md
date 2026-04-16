# University Management Information System (MIS)

A Java-based console application backed by a MySQL database that manages university operations across four user roles: **Student**, **Professor**, **Teaching Assistant**, and **Administrator**.

---

## Table of Contents
1. [Project Overview](#project-overview)
2. [Package Structure](#package-structure)
3. [Features by Role](#features-by-role)
4. [Design Patterns Used](#design-patterns-used)
5. [Database Schema (Inferred)](#database-schema-inferred)
6. [Setup & Configuration](#setup--configuration)
7. [How to Run](#how-to-run)
8. [Critical Analysis & Known Issues](#critical-analysis--known-issues)

---

## Project Overview

The system simulates a university portal where users can sign up or log in as one of four roles. After authentication, each role is routed to its dedicated page with role-specific functionality. The application communicates directly with a local MySQL database (`SVNIT`) using JDBC.

---

## Package Structure

```
├── RunnablePack/
│   ├── Run.java               # Entry point — handles login/signup flow
│   └── JDBCConnection.java    # Singleton DB connection wrapper
│
├── All/
│   ├── Webpages/
│   │   ├── Page.java                  # Interface defining showInfo() and main()
│   │   ├── StudentPage.java           # Student portal logic
│   │   ├── ProfessorPage.java         # Professor portal logic
│   │   ├── AdministratorPage.java     # Admin portal logic
│   │   ├── TeachingAssistantPage.java # TA portal (extends StudentPage)
│   │   └── FeedBack.java              # Generic feedback wrapper <TYPE>
│   │
│   ├── Factory/
│   │   └── PageFactory.java           # Factory that returns the correct Page instance
│   │
│   └── Exceptions/
│       ├── CourseFullException.java
│       ├── DropDeadlinePassedException.java
│       └── InvalidLoginException.java
```

---

## Features by Role

### Student
| # | Feature |
|---|---------|
| 1 | View available courses for their current semester |
| 2 | Enroll in courses (with prerequisite & capacity checks) |
| 3 | View personal schedule |
| 4 | Track academic progress (per-semester SGPA & CGPA) |
| 5 | Drop a course (deadline-enforced) |
| 6 | Submit complaints |
| 7 | Check complaint status |
| 8 | Give feedback on an enrolled course |

### Professor
| # | Feature |
|---|---------|
| 1 | Manage owned courses (syllabus, timings, credits, prerequisites, batch size) |
| 2 | View students enrolled in their courses |
| 3 | View feedback for their courses |

### Administrator
| # | Feature |
|---|---------|
| 1 | Manage course catalog (add, view, delete) |
| 2 | Manage student records (view students & GPA, update semester, update SGPA) |
| 3 | Assign professors to courses |
| 4 | Handle/view student complaints |

### Teaching Assistant
| # | Feature |
|---|---------|
| 1 | View grades by semester |
| 2 | Update student grades |
| 3 | View all courses |
| — | Inherits all Student features |

---

## Design Patterns Used

### 1. Singleton — `JDBCConnection`
Ensures only one database connection is created for the lifetime of the application. `getInstance()` returns the cached instance.

### 2. Factory Method — `PageFactory`
`PageFactory.getPage(String type, String id)` abstracts the instantiation logic for the four page types, returning a `Page` interface reference. This decouples `Run.java` from concrete page classes.

### 3. Template Method (implicit) — `Page` interface
All page classes implement `showInfo()` and `main()`, enforcing a consistent structure across roles.

### 4. Generics — `FeedBack<TYPE>`
A lightweight generic wrapper allowing feedback to be either a numeric rating (`Float`) or a text comment (`String`), resolved at runtime via `instanceof`.

---

## Database Schema (Inferred)

| Table | Key Columns |
|-------|-------------|
| `students` | `student_id`, `password`, `name`, `semester`, `cgpa` |
| `professors` | `professor_id`, `password`, `name` |
| `administrators` | `administrator_id`, `password`, `name` |
| `teachingassistants` | `teachingassistant_id`, `password`, `name` |
| `courses` | `course_id`, `course_name`, `professor`, `professor_id`, `credits`, `prerequisites`, `schedule`, `syllabus`, `BatchSize`, `Current_Size`, `Deadline`, `semester` |
| `enrollments` | `student_id`, `course_id` |
| `grades` | `student_id`, `course_id`, `semester`, `grade` |
| `gpa` | `student_id`, `sem1`–`sem8`, `cgpa` |
| `complaints` | `complaint_id`, `student_id`, `complaint`, `status` |
| `feedback` | `student_id`, `course_id`, `feedback` |

---

## Setup & Configuration

### Prerequisites
- Java 11+
- MySQL 8.x running locally on port `3306`
- MySQL Connector/J on the classpath (`com.mysql.cj.jdbc.Driver`)

### Database
Create a schema named `SVNIT` and populate the tables according to the schema above.

### Credentials *(see security note below)*
In `JDBCConnection.java`, update:
```java
String url      = "jdbc:mysql://localhost:3306/SVNIT";
String username = "root";
String password = "1234pass";   // ← change this
```

---

## How to Run

```bash
# Compile (from project root)
javac -cp .;mysql-connector-j-x.x.x.jar \
      RunnablePack/*.java All/Webpages/*.java \
      All/Factory/*.java All/Exceptions/*.java

# Run
java -cp .;mysql-connector-j-x.x.x.jar RunnablePack.Run
```

> **Note:** `Run.main()` does not have the standard `String[] args` signature. You will need a thin wrapper (see Known Issues #1).

---

## Critical Analysis & Known Issues


### 🟡 Medium Severity

**6. Public Static `Connection` Field (`Run.con`)**
Exposing the database connection as a `public static` field in `Run` is a significant anti-pattern. All page classes access `Run.con` directly, creating tight coupling and making testing impossible. A dependency-injected or singleton-provided connection should be used instead.

**7. Incorrect Inheritance: `TeachingAssistantPage extends StudentPage`**
A Teaching Assistant is not semantically a Student. This violates the Liskov Substitution Principle. A TA being able to "enroll in courses" and "submit complaints" as inherited behaviors is a design flaw. Prefer composition or a shared abstract base class for common utilities (e.g., scanner and connection access).

**8. Administrator ID Ignored**
`AdministratorPage(String id)` receives the admin ID but immediately discards it — it is never stored as an instance field. This means there is no authorization check; any logged-in admin can perform all operations.

**9. Resource Leaks**
`ResultSet`, `PreparedStatement`, and `Statement` objects are never closed. Over a long session this will exhaust JDBC resources. Use `try-with-resources` for all `AutoCloseable` JDBC objects.

**10. `Scanner` + `nextInt()` / `nextLine()` Mixing**
`ProfessorPage.update_credits()` and `update_batchSize()` call `sc.nextInt()` followed by `sc.nextLine()` to consume the leftover newline. This is fragile. Using `Integer.parseInt(sc.nextLine())` consistently (as done elsewhere) is safer.

---

### 🟢 Low Severity / Code Quality

**11. Missing `break` in `AdministratorPage.main()` switch**
`case 4: handleComplains(); break;` — the `break` is present, but `case 5: System.exit(-1)` has no `break` and falls through to `default`, printing "enter Valid Input" before exiting. Not harmful, but untidy.

**12. Stray `sc.nextLine()` in `AdministratorPage.addCourse()`**
The method begins with an unexplained `sc.nextLine()` call to consume a phantom newline left from a prior `parseInt` call. This is a symptom of the mixed scanner usage and should be resolved at the source.

**13. `FeedBack` Class Has Package-Private Access**
The constructor `FeedBack(TYPE d)` and `getFeedBack()` method lack access modifiers, making them package-private. If the class is to be reusable, these should be `public`.

**14. `PageFactory` Returns `null` for Unknown Types**
`PageFactory.getPage()` returns `null` for unrecognized type strings. The calling code in `Run.java` does not null-check before invoking `p.main()`, which will throw a `NullPointerException`. An `IllegalArgumentException` should be thrown instead.

**15. Inconsistent Error Handling**
Some catch blocks print a full stack trace via `e.printStackTrace()`, others call `System.out.println(e)` (which prints only the message), and others embed line numbers manually via `Thread.currentThread().getStackTrace()`. A unified logging strategy (even `java.util.logging`) would improve maintainability.
