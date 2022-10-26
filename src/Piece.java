public class Piece {
    String type;
    double value = 0;
    boolean isPromoted;
    boolean isSente;
    boolean isDed;
    int xPos, yPos;

    public Piece(String type, boolean isSente, int yPos, int xPos) {
        this.type = type;
        this.isSente = isSente;
        this.xPos = xPos;
        this.yPos = yPos;
        updateValue();
    }

    public void promote() {
        isPromoted = true;
    }

    public void kill() {
        isDed = true;
        isSente = !this.isSente;
    }

    public void move(int xPos, int yPos) {

    }

    public void updateValue() {
        if (isPromoted) {
            if (type.equals("pawn")) {
                value = 1;
            } else if (type.equals("lance")) {
                value = 4.3;
            } else if (type.equals("knight")) {
                value = 4.5;
            } else if (type.equals("silver")) {
                value = 6.4;
            } else if (type.equals("gold")) {
                value = 6.9;
            } else if (type.equals("bishop")) {
                value = 8.9;
            } else if (type.equals("rook")) {
                value = 10.4;
            }

        } else {
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

}
