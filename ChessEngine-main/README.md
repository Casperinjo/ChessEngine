# Chess Engine

This project is a chess engine written in Java. It includes a simple AI that uses the Minimax algorithm with alpha-beta pruning to determine the best move. The engine plays as black, and the player plays as white.

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Setup](#setup)
- [Usage](#usage)
- [Key Components](#key-components)


## Overview

The Chess Engine project aims to create a functional chess game where a human player can play against an AI opponent. The AI uses a basic evaluation function and the Minimax algorithm to make decisions. The game is played on an 8x8 board, and all standard chess rules are implemented.

## Features

- Playable chess game with a human player vs. AI
- AI opponent using Minimax algorithm with alpha-beta pruning
- Bitboard representation for efficient move generation
- Evaluation function for board state assessment

## Setup

### Installation

1. Clone the repository:
    ```bash
    git clone https://github.com/yourusername/chess-engine.git
    cd chess-engine
    ```

2. Open the project in your preferred Java IDE or compile using command-line tools.

## Usage

To run the chess game, execute the `Main` class. The human player will play as white, and the AI will play as black. 


## Key Components

### `AI.java`
The AI class uses the Minimax algorithm with alpha-beta pruning to determine the best move for the engine. It generates legal moves, simulates game states, and evaluates board positions to make decisions.

### `Game.java`
The Game class manages the state of the chess game, including the board, pieces, and moves. It provides methods to make and undo moves, check game status, and generate legal moves.

### `Move.java`
The Move class represents a chess move, including the starting and ending positions, the piece being moved, and any captured pieces.

### `Piece.java` and Subclasses
The Piece class and its subclasses (Pawn, Rook, Knight, Bishop, Queen, King) represent the different types of chess pieces. They include methods for determining legal moves based on the type of piece.

### `Evaluator.java`
The Evaluator class provides a method to evaluate the board state, giving a score that the AI uses to make decisions.

### `BitBoardUtil.java` and `RuleUtils.java`
Utility classes for handling bitboard operations and game rules.

