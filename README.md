# 🎮 Tetris — Java Swing Game

A complete implementation of the classic **Tetris** game written in **Java** using **Swing**, low-level rendering, collections, and serialization.  

This project includes:

- Fully functional Tetris gameplay  
- Low-level rendering with `Graphics2D`  
- 7-bag tetromino randomizer  
- Game loop with Swing `Timer`  
- Save/Load game state  
- High score table with persistence  
- Unit tests (JUnit 5)  

---

## Features

### ✔ Classic Tetris Mechanics
- Falling tetrominoes  
- Movement and rotation  
- Line clearing (1–4 lines)  
- Score system:
  - 1 line → 100 points  
  - 2 lines → 300 points  
  - 3 lines → 500 points  
  - 4 lines → 800 points  

### ✔ Low-Level Rendering  
Rendered manually using Java `Graphics2D`.

### ✔ 7-Bag Randomizer  
Provides fair and predictable distribution of tetrominoes, like modern official Tetris.

### ✔ Save & Load  
Saves:
- board  
- current piece  
- next piece  
- score  

Restores everything exactly as it was.

### ✔ High Scores  
Stores top-10 results in `scores.ser`.

### ✔ Pause / Resume  
Press **P** anytime.

---

## 🕹 Controls

| Key | Action |
|-----|--------|
| ⬅️ | Move left |
| ➡️ | Move right |
| ⬇️ | Soft drop |
| ⬆️ / X | Rotate clockwise |
| Z | Rotate counter-clockwise |
| Space | Hard drop |
| P | Pause / Resume |
