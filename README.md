# Gomoku Game

A JavaFX-based implementation of the classic Gomoku board game, featuring both human and AI gameplay.

## Overview

Gomoku is a strategy board game where two players take turns placing stones on a grid. The objective is to create an unbroken line of five stones horizontally, vertically, or diagonally before the opponent.

This project implements:

- Interactive graphical user interface using JavaFX
- Human vs Human gameplay
- Human vs AI gameplay
- Configurable board sizes
- Turn timer system
- Minimax-based AI opponent

## Features

### Game Modes

Players can choose between:

- Player vs Player
- Player vs AI

The AI opponent uses the **Minimax algorithm** with limited recursion depth to evaluate possible moves and make decisions.

### Timer System

Each player has a configurable time limit for their turn.

If a player runs out of time:
- Their turn is skipped
- The opponent continues playing

### AI Implementation

The AI uses:

- Minimax search
- Depth-limited recursion
- Board evaluation heuristics

The search depth is adjusted depending on board size to balance difficulty and performance.

## Technologies

- Java
- JavaFX
- Maven

## Running the Project

### Requirements

- Java 17+
- Maven (optional, Maven Wrapper included)

### Using Maven

Linux / Mac:

```bash
./mvnw clean javafx:run
