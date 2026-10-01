# Tetris Game 2006ICT

## Object-Oriented Software Development — Milestone 1

This is a JavaFX implementation of the classic Tetris game for the 2006ICT
Object-Oriented Software Development course. The repository follows the
team's existing Maven layout and separates application startup, game logic,
reusable interfaces, and screen presentation into packages under `tetris`.

The code in this repository was reorganized and adapted from the supplied
starter project. Keep any attribution required by the course and describe
team contributions accurately; package and file names are for organization
and do not change the origin or authorship of source code.

## Team

| Contributor | Role |
|---|---|
| **baggaanmol** / Anmol | Repository owner and team lead; project coordination, integration, and repository administration |
| **adityapamar** / Aditya | Interface development; home menu, settings, credits, top scores, and JavaFX screen flow |
| **jigyashu29k** / Jigyashu | Game logic; board, tetromino behaviour, scoring, AI play, and technical documentation |

## Technology

- Java 25
- JavaFX 25
- Maven
- Gson for local settings and score data
- JavaFX Media for music and sound effects
- IntelliJ IDEA or another Maven-compatible IDE

## Repository layout

The Tetris project repository structure

```text
Tetris_game_2006ICT/
├── README.md
├── pom.xml
└── src/
    └── main/
        ├── java/
        │   └── tetris/
        │       ├── Main.java
        │       ├── application/
        │       │   └── TetrisApplication.java
        │       ├── game/
        │       │   ├── AI.java
        │       │   ├── AudioManager.java
        │       │   ├── boardeval.java
        │       │   ├── controller.java
        │       │   ├── ExternalPlayer.java
        │       │   ├── form.java
        │       │   ├── HighScoreEntry.java
        │       │   ├── HighScoreManager.java
        │       │   ├── OpMove.java
        │       │   ├── PureGame.java
        │       │   ├── Settings.java
        │       │   └── Tetris.java
        │       ├── interfaces/
        │       │   └── Movable.java
        │       └── screens/
        │           └── ScreenLayout.java
        └── resources/
            ├── assets/
            │   └── start-image.png
            ├── audio/
            │   ├── clear.mp3
            │   ├── move.mp3
            │   └── music.mp3
            ├── fonts/
            │   └── SpaceGrotesk.ttf
            └── styles/
                └── game.css
```

### Package responsibilities

- `tetris` — stable application entry point.
- `tetris.application` — JavaFX application lifecycle and navigation between
  the main screens.
- `tetris.game` — board loop, pieces, controls, AI, audio, settings, external
  player communication, and local score persistence.
- `tetris.interfaces` — shared contracts, including the movement interface
  implemented by game pieces.
- `tetris.screens` — reusable layout components for menu and information
  screens.

## Features

- Splash screen with replaceable artwork.
- Centered **HOME MENU** with Start Game, Top Scores, Settings, Developers, and
  Exit options.
- Seven tetromino types with distinct colours.
- Collision-aware horizontal movement and rotation.
- Soft drop, hard drop, pause, line clearing, score, and line counters.
- Game-over card with restart and return-to-menu options.
- Settings for board dimensions, speed, difficulty, music, sound effects,
  AI play, and external-player mode.
- Local Top Scores list.
- Deterministic AI placement search that evaluates line clears, height, holes,
  and board bumpiness.

## Controls

| Key | Action |
|---|---|
| `A` or `LEFT` | Move left |
| `D` or `RIGHT` | Move right |
| `S` or `DOWN` | Soft drop while held |
| `W` or `UP` | Rotate |
| `SPACE` | Hard drop |
| `P` | Pause or resume |

## Assets

Asset imagesz are placed at: src/main/resources/assets

```text
src/main/resources/assets/home.png
```

Optional replacement WAV audio can be placed at:

```text
src/main/resources/audio/music.wav
src/main/resources/audio/move.wav
src/main/resources/audio/clear.wav
```

The UI font is in `src/main/resources/fonts/SpaceGrotesk.ttf`; styles
are in `src/main/resources/styles/game.css`.

## Prerequisites

- JDK 25
- Apache Maven
- A desktop environment that supports JavaFX

JavaFX dependencies and the application entry point are configured in the
root `pom.xml`.

## Build and run

Clone the existing repository and enter its root:

```bash
git clone https://github.com/baggaanmol/Tetris_game_2006ICT.git
cd Tetris_game_2006ICT
```

Compile:

```bash
mvn clean compile
```

Run the game:

```bash
mvn javafx:run
```

Package:

```bash
mvn clean package
```

The main class is `tetris.Main`. In IntelliJ IDEA, open the repository root
as a Maven project and run `tetris.Main`.

## Local data

The game creates its data files in the process working directory:

- `settings.json` — saved game settings.
- `highscores.json` — local leaderboard entries.


## Git collaboration

All team members showed commitment to commit their work to the project repository for completion 

### Credit: 
https://pixabay.com/ acted as the main source of the project files for audio 🎶 


CHEERS TO OUR TEAM!

## Repository

https://github.com/baggaanmol/Tetris_game_2006ICT
