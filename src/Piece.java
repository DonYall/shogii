public class Piece {
    String type;
    double value = 0;
    boolean isPromoted;
    boolean isSente;
    boolean isDed;
    int pos;
    int x, y;
    int pieceSize = 64;

    public Piece(String type, boolean isSente, int pos) {
        this.type = type;
        this.isSente = isSente;
        this.pos = pos;
        updatePos();
        updateValue();
    }

    public void promote() {
        isPromoted = true;
    }

    public void unpromote() {
        isPromoted = false;
    }

    public void kill() {
        isDed = true;
        isSente = !isSente;
    }

    public boolean move(int pos) {
        if (isDed && pos < 81) {
            isDed = false;
        }
        this.pos = pos;
        updatePos();
        return true;
    }

    public void fakeMove(int pos) {
        this.pos = pos;
    }

    public void updateValue() {
        if (isPromoted) {
            if (type.equals("pawn")) {
                value = 4.2;
            } else if (type.equals("lance")) {
                value = 6.3;
            } else if (type.equals("knight")) {
                value = 6.4;
            } else if (type.equals("silver")) {
                value = 6.7;
            } else if (type.equals("bishop")) {
                value = 11.5;
            } else if (type.equals("rook")) {
                value = 13;
            }

        } else if (pos >= 0) {
            if (type.equals("pawn")) {
                if (isDed) {
                    value = 1.15;
                } else {
                    value = 1;
                }
            } else if (type.equals("lance")) {
                if (isDed) {
                    value = 4.8;
                } else {
                    value = 4.3;
                }
            } else if (type.equals("knight")) {
                if (isDed) {
                    value = 5.1;
                } else {
                    value = 4.5;
                }
            } else if (type.equals("silver")) {
                if (isDed) {
                    value = 7.2;
                } else {
                    value = 6.4;
                }
            } else if (type.equals("gold")) {
                if (isDed) {
                    value = 7.8;
                } else {
                    value = 6.9;
                }
            } else if (type.equals("bishop")) {
                if (isDed) {
                    value = 11.1;
                } else {
                    value = 8.9;
                }
            } else if (type.equals("rook")) {
                if (isDed) {
                    value = 12.7;
                } else {
                    value = 10.4;
                }
            }
        }
    }
    public void updatePos() {
        if (!isDed) {
            x = (pos % 9) * pieceSize;
            y = pos / 9 * pieceSize;    
        } else {
            if (isSente) {
                x = (pos-72)*pieceSize;
                y = 8*pieceSize;
            } else {
                x = (pos-81)*pieceSize;
                y = 0;
            }
        }
    }

}
