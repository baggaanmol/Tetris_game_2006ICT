# Tetris Game 2006ICT
# Tetris Game - 2006ICT Object Oriented Software Development

## Milestone 1

This repository contains the implementation of a classic Tetris game developed as part of the 2006ICT course assignment. The project is built using Java and JavaFX, following Object-Oriented principles.

## Team Members

- **Anmol** (Repository Owner / Team Lead): Project management, integration, repository administration.
- **Aditya** (UI Developer): Responsible for user interface implementation including movable, scene manager, application.
- **Kajal** (Game Logic Developer): Game engine, board, tetromino logic, game screens, testing, diagrams, requirements documentation, and README updates.


## Project Structure

The project is structured as a Maven project:

- `src/main/java/tetris/` - Contains all the Java source code.
    - `application/` - Entry point and scene management.
    - `screens/` - All UI screens (Splash, Main Menu, Configuration, High Score, Game).
    - `game/` - Core game logic (Board, Tetromino, GameController).
    - `interfaces/` - Java interfaces like `Movable`.
    - `models/` - Data models (to be expanded for Milestone 2).
    - `utils/` - Utility classes (Constants).

## Features (Milestone 1)

- **Splash Screen:** Displays group information on startup.
- **Main Menu:** Navigation to Play, Configuration, High Scores, and Exit.
- **Configuration Screen:** Interactive controls (sliders, checkboxes) for game settings.
- **High Score Screen:** Displays top 10 dummy high scores.
- **Game Screen:** Basic game loop with a tetromino (I-Piece) that moves down, can be controlled, and pauses.
- **Controls:** Arrow keys to move/rotate, 'P' key to pause.

## How to Run

1.  **Prerequisites:** Java Development Kit (JDK) 17 or later, Maven.
2.  **Clone the Repository:**
    ```bash
    git clone https://github.com/baggaanmol/Tetris_game_2006ICT.git


## Development
Built using Java and JavaFX

                │   └── Position.java
                ├── interfaces/
                │   ├── Movable.java
                │   └── Configurable.java
                └── utils/
                    └── Constants.java
