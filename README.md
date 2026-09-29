# Tetris Game 2006ICT

## Object-Oriented Software Development — Milestone 1

This repository contains a JavaFX implementation of the classic Tetris game
developed for the 2006ICT Object-Oriented Software Development course.

The project is built with Java 25, JavaFX 25, and Maven. It applies
object-oriented design to the game board, tetromino pieces, player controls,
settings, audio, score persistence, and computer-assisted play.

> This README documents the current implementation in this repository. 

## Team Members

| Contributor | Role |
|---|---|
| **baggaanmol** / Anmol | Repository owner and team lead; project coordination, integration, repository administration, and release preparation |
| **adityapamar** / Aditya | User-interface development; home menu, settings, credits, leaderboard, splash screen, styling, and JavaFX scene flow |
| **jigyashu29k** / Jigyashu | Game logic and AI development; board behaviour, tetromino movement, collision handling, scoring, testing support, diagrams, and technical documentation |

## Technology Stack

- **Language:** Java 25
- **User interface:** JavaFX 25
- **Build system:** Maven
- **Persistence:** Gson JSON files
- **Audio:** JavaFX Media
- **IDE:** IntelliJ IDEA or another Maven-compatible Java IDE
- **Font:** Bundled Space Grotesk font

## Project Structure

The repository is a Maven project. The main Maven module is the `Test`
directory.

```text
ObjectO2006ICT-edited/
├── README.md
└── Test/
    ├── pom.xml
    └── src/
        └── main/
            ├── java/
            │   └── org/example/
            │       ├── Main2.java
            │       ├── Tetris.java
            │       ├── AI.java
            │       ├── AudioManager.java
            │       ├── Settings.java
            │       ├── HighScoreEntry.java
            │       ├── HighScoreManager.java
            │       ├── controller.java
            │       ├── form.java
            │       ├── boardeval.java
            │       ├── PureGame.java
            │       ├── ExternalPlayer.java
            │       └── OpMove.java
            └── resources/
                ├── assets/
                │   └── start-image.png
                ├── audio/
                │   ├── music.mp3
                │   ├── move.mp3
                │   └── clear.mp3
                ├── fonts/
                │   └── SpaceGrotesk.ttf
                └── styles/
                    └── game.css
