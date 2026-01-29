# TeamMate – Intelligent Team Formation System

##  Project Overview

**TeamMate** is a Java-based intelligent team formation system designed for a **University Gaming Club**. The application automatically creates **balanced and diverse teams** for tournaments, friendly matches, and inter-university events.

The system collects participant data through a **personality and interest survey**, processes it using a matching algorithm, and forms teams that promote **strong collaboration, fairness, and performance**.

---

##  Objectives

* Automatically form balanced teams of size **N**
* Encourage diversity in skills, roles, interests, and personalities
* Reduce manual effort and bias in team selection
* Demonstrate Java concepts such as **OOP, file handling, exception handling, and concurrency**

---

##  Key Features

### 1️. Input & Survey Module

Participants complete a short survey containing:

* **5 Personality Questions** (as provided in starter pack)
* **Interest Selection** (e.g., Valorant, Dota, FIFA, Basketball, Badminton)
* **Preferred Playing Role** (e.g., Attacker, Defender, Strategist, Support)
* **Skill Level** (numeric or categorical)

Survey data is loaded from a **CSV file** provided by the organizer.

---

### 2️. Personality Classification

Personality type is determined based on total personality score:

| Score Range | Personality Type |
| ----------- | ---------------- |
| 90 – 100    | Leader           |
| 70 – 89     | Balanced         |
| 50 – 69     | Thinker          |

Personality classification is handled by a dedicated `PersonalityClassifier` class.

---

### 3️. Intelligent Team Matching Algorithm

The system forms teams of size **N** while ensuring:

*  **Diverse interests** (different games/sports per team)
*  **Role variety** (at least one of each required role where possible)
*  **Mixed personality types** for better team dynamics

For large datasets, team formation is processed **in parallel using threads** to improve performance.

---

### 4️. File Handling

* Load participant data from a sample **CSV file**
* Save formed teams to an output file: `formed_teams.csv`
* Ensures data integrity and proper formatting

---

### 5️. Exception Handling

The system includes robust error handling for:

* Missing or invalid user inputs
* Invalid personality scores or roles
* File read/write errors
* Concurrency-related issues

Custom exceptions and validation checks are used where appropriate.

---

### 6️. Concurrency

Threads are used for:

* Processing survey data
* Forming teams in parallel for large participant sets

This improves scalability and system responsiveness.

---

##  System Design (UML)

### Actors

* **Organizer**

  * Uploads CSV file
  * Defines team size
  * Initiates team formation
  * Reset tournament

* **Participant**

  * Completes survey
  * Provides role, skill, and interest data

### Key Classes

* `Participant`
* `Team`
* `TeamBuilder`
* `PersonalityClassifier`
* `CSVFileHandler`
* `TeamFormationService`

Entity relationships are clearly represented in the **Class Diagram**.

### UML Diagrams Included

* Use Case Diagram & Descriptions
* Activity Diagrams (main & alternate flows)
* Class Diagram (entity relationships only)
* Sequence Diagrams (message flow between objects)

---

##  Technologies Used

| Component     | Technology                |
| ------------- | ------------------------- |
| Language      | Java                      |
| File Handling | CSV                       |
| Concurrency   | Java Threads              |
| UI            | Console-based             |
| Design        | UML Diagrams              |

---

##  Project Timeline

| Week | Activities                                        |
| ---- | ------------------------------------------------- |
| 1–2  | UML Design & Core Structure                       |
| 2–5  | Feature Implementation, File & Exception Handling |
| 5    | Module Integration                                |
| 6–7  | Finalization, Logging, UI Improvements            |
| 7–8  | Testing (Unit, Concurrency, File Integrity, UAT)  |

---

##  Learning Outcomes

* Object-Oriented Design using Java
* UML-based system modeling
* File processing and validation
* Multithreading and concurrency handling
* Error handling and defensive programming
* Team-balancing algorithm design

---

##  Author

Developed as part of an academic assessment for designing an **Intelligent Team Formation System** for a University Gaming Club.
