package com.example.gomokuexample;

import java.util.Scanner;

class GomokuGame {
    private int[][] board;          // 0: empty, 1: player1's stone, 2: player2's stone
    private int[][] curdis;         // closest distance to a stone from player 1, (used for AI player)
    private int currentPlayer;      // 1: player1, 2: player2
    private boolean gameOver;       // true: game over, false: game not over
    private int winner;             // 0: draw, 1: player 1 wins, 2: player 2 wins
    private int winlen = 5;         // amount of same colors in a row required to win
    private int boardSize;          // size of the board
    private int inf = (int)(1e9);   // variable with large value
    private int curmoves = 0;       // total amount of moves done by both players
    private int player1cnt = 0;
    private int player2cnt = 0;
    private int player1mx = 0;
    private int player2mx = 0;

    public GomokuGame(int boardSize, int winlen) {
        if (boardSize < 5 || boardSize > 20) {
            throw new IllegalArgumentException("Board size should be between 5 and 20.");
        }
        this.boardSize = boardSize;
        this.winlen = winlen;
        board = new int[boardSize][boardSize];            // init to be all zeros
        curdis = new int[boardSize][boardSize];

        // no stones present intially, set distance to infinity
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                curdis[i][j] = inf;
            }
        }
        curmoves = 0;
        currentPlayer = 1;
        gameOver = false;
        winner = 0;
    }

    public GomokuGame() {
        this(15, 5);
    }

    public boolean checkWin(int x, int y, int[][] curboard, boolean updatestats) {
        int[][][] directionLines = {{{0, 1}, {0, -1}},                // vertical
                {{1, 0}, {-1, 0}},                // horizontal
                {{1, 1}, {-1, -1}},               // diagonal
                {{1, -1}, {-1, 1}}};              // anti-diagonal
        for (int[][] oppositeDirs : directionLines) {
            int count = 1;
            for (int[] direction: oppositeDirs) {
                int dx = direction[0];
                int dy = direction[1];
                for (int i = 1; i < winlen; i++) {
                    int newX = x + i * dx;
                    int newY = y + i * dy;
                    if (!isValidPosition(newX, newY) || curboard[newX][newY] != curboard[x][y]) {
                        break;
                    }
                    count++;
                }
            }
            if (updatestats) {
                if (curboard[x][y] == 1) player1mx = Math.max(player1mx, count);
                else player2mx = Math.max(player2mx, count);
            }
            if (count >= winlen) return true;
        }


        return false;
    }


    public boolean move(int x, int y) {
        // place a piece at (x, y) for the current player, and then switch to the other player
        if (gameOver) {
            return false;
        }

        if (!isValidPosition(x, y)) {
            return false;
        }

        if (board[x][y] != 0) {
            return false;
        }
        
        board[x][y] = currentPlayer;
        curmoves++;

        // update curdis array
        if (currentPlayer == 1) {
            player1cnt++;
            for (int i = 0; i < boardSize; i++) {
                for (int j = 0; j < boardSize; j++) {
                    int newdis = Math.abs(i - x) + Math.abs(j - y);
                    if (newdis < curdis[i][j]) curdis[i][j] = newdis;
                }
            }
        }
        else player2cnt++;

        if (checkWin(x, y, board, true)) {
            gameOver = true;
            winner = currentPlayer;
        }
        else if (curmoves == boardSize * boardSize) {
            // no available moves left
            gameOver = true;
            winner = 0;
        }
        switchPlayers();     // switch player
        return true;
    }

    public void switchPlayers() {
        currentPlayer = currentPlayer == 1 ? 2 : 1;
    }

    public boolean isValidPosition(int x, int y) {
        return x >= 0 && x < boardSize && y >= 0 && y < boardSize;
    }

    public boolean isGameOver() {return gameOver;}

    public int getWinner() {return winner;}

    public int getCurrentPlayer(){ return currentPlayer; }

    public int[][] getBoard() {return board;}

    public int[] getstats() {
        int[] ret = {player1cnt, player2cnt, player1mx, player2mx};
        return ret;
    }

    public int minimax(int[][] curboard, int depth, boolean maxplayer, int lastx, int lasty) {
        // player 1 wants to maximimze board score (1 player 1 win, 0 draw, -1 player 2 win)
        // player 2 (AI) wants to minimize
        int score = 0;
        if (checkWin(lastx, lasty, curboard, false)){
            if (curboard[lastx][lasty] == 1) score = 1;
            else score = -1;
        }
        // game is already over or recursion depth reached
        if (depth == 0 || score != 0) {
            return score;
        }

        if (maxplayer) {
            // player 1 tries to maximize
            int v = -2;
            for (int i = 0; i < boardSize; i++) {
                for (int j = 0; j < boardSize; j++) {
                    if (curboard[i][j] == 0) {
                        curboard[i][j] = 1;
                        v = Math.max(v, minimax(curboard, depth - 1, false, i, j));
                        curboard[i][j] = 0;
                    }
                }
            }
            return v;
        } else {
            // player 2 tries to minimize
            int v = 2;
            for (int i = 0; i < boardSize; i++) {
                for (int j = 0; j < boardSize; j++) {
                    if (curboard[i][j] == 0) {
                        curboard[i][j] = 2;
                        v = Math.min(v, minimax(curboard, depth - 1, true, i, j));
                        curboard[i][j] = 0;
                    }
                }
            }
            return v;
        }
    }

    public int[] findmove(int lastx, int lasty) {
        int[] bestMove = {-1, -1};
        int v = 2; // current best value found (AI wants to minimize)
        int vdis = inf; // if there are multiple best moves with the same value, pick to one closest to player 1's stones
        int LIM = 2;
        // complexity -> O((boardsize^(2 * LIM)), scale down limit as boardSize grows
        if (boardSize <= 7) LIM = 4;
        else if (boardSize <= 12) LIM = 3;

        // finds best move by looking LIM moves ahead
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (board[i][j] == 0) {
                    board[i][j] = 2;
                    int score = minimax(board, LIM - 1, true, i, j);
                    board[i][j] = 0;
                    if (score < v) {
                        v = score;
                        bestMove[0] = i;
                        bestMove[1] = j;
                    }
                    else if (score == v && curdis[i][j] < vdis) {
                        vdis = curdis[i][j];
                        bestMove[0] = i;
                        bestMove[1] = j;
                    }
                }
            }
        }
        if (v == 1){
            // assuming player 1 plays optimally, ai already lost, so atleast try to cover 1 winning stone of player 1
            int bestdis = inf;
            for (int i = 0; i < boardSize; i++) {
                for (int j = 0; j < boardSize; j++) {
                    if (board[i][j] == 0) {
                        board[i][j] = 1;
                        if (checkWin(i, j, board, false)) {
                            int dis = Math.abs(i - lastx) + Math.abs(j - lasty);
                            if (dis < bestdis) {
                                bestdis = dis;
                                bestMove[0] = i;
                                bestMove[1] = j;
                            }
                        }
                        board[i][j] = 0;
                    }
                }
            }

        }
        return bestMove;
    }
}