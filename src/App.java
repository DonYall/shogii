import java.util.*;
import javax.swing.*;
import java.io.*;
import java.net.URL;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;

public class App extends JFrame {
    private int posX = 0, posY = 0;
    private int numPlayers;
    private boolean sente = true;
    private int pieceSize;
    private Piece[] pieces = new Piece[40];
    private Map<String, Integer> capturedPieces = new HashMap<>();
    private Piece selectedPiece;
    private URL moveURL = App.class.getResource("move.wav");
    private URL captureURL = App.class.getResource("capture.wav");
    private JPanel gamePanel;
    private Piece trueGold = new Piece("gold", true, -1, -1);
    private Piece falseGold = new Piece("gold", false, -1, -1);
    private boolean promotion = false;

    public App(int numPlayers, int pieceSize) throws IOException {
        this.numPlayers = numPlayers;
        this.pieceSize = pieceSize;

        // Pawns
        for (int i = 0; i < 9; i++) {
            pieces[i] = new Piece("pawn", true, 6, i);
        }
        for (int i = 0; i < 9; i++) {
            pieces[9 + i] = new Piece("pawn", false, 2, i);
        }
        // Lances
        pieces[18] = new Piece("lance", true, 8, 8);
        pieces[19] = new Piece("lance", true, 8, 0);
        pieces[20] = new Piece("lance", false, 0, 8);
        pieces[21] = new Piece("lance", false, 0, 0);
        // Knights
        pieces[22] = new Piece("knight", true, 8, 1);
        pieces[23] = new Piece("knight", true, 8, 7);
        pieces[24] = new Piece("knight", false, 0, 1);
        pieces[25] = new Piece("knight", false, 0, 7);
        // Silvers
        pieces[26] = new Piece("silver", true, 8, 2);
        pieces[27] = new Piece("silver", true, 8, 6);
        pieces[28] = new Piece("silver", false, 0, 2);
        pieces[29] = new Piece("silver", false, 0, 6);
        // Golds
        pieces[30] = new Piece("gold", true, 8, 3);
        pieces[31] = new Piece("gold", true, 8, 5);
        pieces[32] = new Piece("gold", false, 0, 3);
        pieces[33] = new Piece("gold", false, 0, 5);
        // Bishops
        pieces[34] = new Piece("bishop", true, 7, 1);
        pieces[35] = new Piece("bishop", false, 1, 7);
        // Rook
        pieces[36] = new Piece("rook", true, 7, 7);
        pieces[37] = new Piece("rook", false, 1, 1);
        // King
        pieces[38] = new Piece("king", true, 8, 4);
        pieces[39] = new Piece("king", false, 0, 4);

        // Dead pieces
        capturedPieces.put("truepawn", 0);
        capturedPieces.put("truelance", 0);
        capturedPieces.put("trueknight", 0);
        capturedPieces.put("truesilver", 0);
        capturedPieces.put("truegold", 0);
        capturedPieces.put("truebishop", 0);
        capturedPieces.put("truerook", 0);
        capturedPieces.put("falsepawn", 0);
        capturedPieces.put("falselance", 0);
        capturedPieces.put("falseknight", 0);
        capturedPieces.put("falsesilver", 0);
        capturedPieces.put("falsegold", 0);
        capturedPieces.put("falsebishop", 0);
        capturedPieces.put("falserook", 0);

        // Constructing the GUI
        setSize((int) (pieceSize * 9 * 16 / 9), (int) (pieceSize * 9));
        setUndecorated(true);

        // Board image
        BufferedImage bufferedBoard = ImageIO.read(getClass().getResource("board.png"));
        Image boardImage = (bufferedBoard.getScaledInstance(pieceSize * 9, pieceSize * 9, BufferedImage.SCALE_SMOOTH));

        // Piece images
        BufferedImage bufferedPieces = ImageIO.read(getClass().getResource("pieces.png"));
        Map<String, Image> pieceImages = new HashMap<>();
        pieceImages.put("truefalsegold", bufferedPieces.getSubimage(0, 0, 100, 100).getScaledInstance(pieceSize,
                pieceSize, BufferedImage.SCALE_SMOOTH));
        pieceImages.put("falsefalsegold", bufferedPieces.getSubimage(0, 100, 100, 100).getScaledInstance(pieceSize,
                pieceSize, BufferedImage.SCALE_SMOOTH));
        pieceImages.put("truefalserook", bufferedPieces.getSubimage(100, 0, 100, 100).getScaledInstance(pieceSize,
                pieceSize, BufferedImage.SCALE_SMOOTH));
        pieceImages.put("falsefalserook", bufferedPieces.getSubimage(100, 100, 100, 100).getScaledInstance(pieceSize,
                pieceSize, BufferedImage.SCALE_SMOOTH));
        pieceImages.put("truetruerook", bufferedPieces.getSubimage(200, 0, 100, 100).getScaledInstance(pieceSize,
                pieceSize, BufferedImage.SCALE_SMOOTH));
        pieceImages.put("falsetruerook", bufferedPieces.getSubimage(200, 100, 100, 100).getScaledInstance(pieceSize,
                pieceSize, BufferedImage.SCALE_SMOOTH));
        pieceImages.put("truefalsebishop", bufferedPieces.getSubimage(300, 0, 100, 100).getScaledInstance(pieceSize,
                pieceSize, BufferedImage.SCALE_SMOOTH));
        pieceImages.put("falsefalsebishop", bufferedPieces.getSubimage(300, 100, 100, 100).getScaledInstance(pieceSize,
                pieceSize, BufferedImage.SCALE_SMOOTH));
        pieceImages.put("truetruebishop", bufferedPieces.getSubimage(400, 0, 100, 100).getScaledInstance(pieceSize,
                pieceSize, BufferedImage.SCALE_SMOOTH));
        pieceImages.put("falsetruebishop", bufferedPieces.getSubimage(400, 100, 100, 100).getScaledInstance(pieceSize,
                pieceSize, BufferedImage.SCALE_SMOOTH));
        pieceImages.put("truefalsesilver", bufferedPieces.getSubimage(500, 0, 100, 100).getScaledInstance(pieceSize,
                pieceSize, BufferedImage.SCALE_SMOOTH));
        pieceImages.put("falsefalsesilver", bufferedPieces.getSubimage(500, 100, 100, 100).getScaledInstance(pieceSize,
                pieceSize, BufferedImage.SCALE_SMOOTH));
        pieceImages.put("truetruesilver", bufferedPieces.getSubimage(600, 0, 100, 100).getScaledInstance(pieceSize,
                pieceSize, BufferedImage.SCALE_SMOOTH));
        pieceImages.put("falsetruesilver", bufferedPieces.getSubimage(600, 100, 100, 100).getScaledInstance(pieceSize,
                pieceSize, BufferedImage.SCALE_SMOOTH));
        pieceImages.put("truefalseknight", bufferedPieces.getSubimage(700, 0, 100, 100).getScaledInstance(pieceSize,
                pieceSize, BufferedImage.SCALE_SMOOTH));
        pieceImages.put("falsefalseknight", bufferedPieces.getSubimage(700, 100, 100, 100).getScaledInstance(pieceSize,
                pieceSize, BufferedImage.SCALE_SMOOTH));
        pieceImages.put("truetrueknight", bufferedPieces.getSubimage(800, 0, 100, 100).getScaledInstance(pieceSize,
                pieceSize, BufferedImage.SCALE_SMOOTH));
        pieceImages.put("falsetrueknight", bufferedPieces.getSubimage(800, 100, 100, 100).getScaledInstance(pieceSize,
                pieceSize, BufferedImage.SCALE_SMOOTH));
        pieceImages.put("truefalselance", bufferedPieces.getSubimage(900, 0, 100, 100).getScaledInstance(pieceSize,
                pieceSize, BufferedImage.SCALE_SMOOTH));
        pieceImages.put("falsefalselance", bufferedPieces.getSubimage(900, 100, 100, 100).getScaledInstance(pieceSize,
                pieceSize, BufferedImage.SCALE_SMOOTH));
        pieceImages.put("truetruelance", bufferedPieces.getSubimage(1000, 0, 100, 100).getScaledInstance(pieceSize,
                pieceSize, BufferedImage.SCALE_SMOOTH));
        pieceImages.put("falsetruelance", bufferedPieces.getSubimage(1000, 100, 100, 100).getScaledInstance(pieceSize,
                pieceSize, BufferedImage.SCALE_SMOOTH));
        pieceImages.put("truefalsepawn", bufferedPieces.getSubimage(1100, 0, 100, 100).getScaledInstance(pieceSize,
                pieceSize, BufferedImage.SCALE_SMOOTH));
        pieceImages.put("falsefalsepawn", bufferedPieces.getSubimage(1100, 100, 100, 100).getScaledInstance(pieceSize,
                pieceSize, BufferedImage.SCALE_SMOOTH));
        pieceImages.put("truetruepawn", bufferedPieces.getSubimage(1200, 0, 100, 100).getScaledInstance(pieceSize,
                pieceSize, BufferedImage.SCALE_SMOOTH));
        pieceImages.put("falsetruepawn", bufferedPieces.getSubimage(1200, 100, 100, 100).getScaledInstance(pieceSize,
                pieceSize, BufferedImage.SCALE_SMOOTH));
        pieceImages.put("truefalseking", bufferedPieces.getSubimage(1300, 0, 100, 100).getScaledInstance(pieceSize,
                pieceSize, BufferedImage.SCALE_SMOOTH));
        pieceImages.put("falsefalseking", bufferedPieces.getSubimage(1300, 100, 100, 100).getScaledInstance(pieceSize,
                pieceSize, BufferedImage.SCALE_SMOOTH));

        // Misc images
        BufferedImage bufferedMisc = ImageIO.read(getClass().getResource("misc.png"));
        Image moveImage = bufferedMisc.getSubimage(300, 0, 100, 100).getScaledInstance(pieceSize, pieceSize, BufferedImage.SCALE_SMOOTH);
        Image captureImage = bufferedMisc.getSubimage(0, 0, 100, 100).getScaledInstance(pieceSize, pieceSize, BufferedImage.SCALE_SMOOTH);

        // Drawing the game panel and starting position
        gamePanel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHints(
                        new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));

                g.fillRect(0, 0, (int) (pieceSize * 9 * 16 / 9), (int) (pieceSize * 9));
                g.drawImage(boardImage, 0, 0, null);

                if (selectedPiece != null && !promotion) {
                    for (int[] move : getAvailableSquares(selectedPiece)) {
                        if (getPiece(move[0], move[1]) == null) {
                            g.drawImage(moveImage, move[0] * pieceSize, move[1] * pieceSize, this);
                        } else {
                            g.drawImage(captureImage, move[0] * pieceSize, move[1] * pieceSize, this);
                        }
                    }
                }

                for (int i = 0; i < pieces.length; i++) {
                    g.drawImage(pieceImages.get(
                            String.valueOf(pieces[i].isSente) + String.valueOf(pieces[i].isPromoted) + pieces[i].type),
                            pieces[i].x, pieces[i].y, this);
                }
            }
        };

        setTitle("Shogi");
        setDefaultCloseOperation(3);
        add(gamePanel);
        addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (selectedPiece != null && !promotion) {
                    // Drag piece
                    selectedPiece.x = e.getX() - pieceSize / 2;
                    selectedPiece.y = e.getY() - (int) pieceSize * 3 / 4;
                } else if (getPieceFromLocation(e.getX(), e.getY()) != null) {
                    if (getPieceFromLocation(e.getX(), e.getY()).isSente == sente && !promotion) {
                        // Pick up piece
                        selectedPiece = getPieceFromLocation(e.getX(), e.getY());
                    }
                } else {
                    // Drag and drop window movement
                    setLocation(e.getXOnScreen() - posX, e.getYOnScreen() - posY);
                }
                repaint();
            }

            @Override
            public void mouseMoved(MouseEvent e) {
            }
        });
        addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
                posX = e.getX();
                posY = e.getY();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (selectedPiece != null) {
                    if (!promotion && getAvailableSquares(selectedPiece).stream().anyMatch(a -> Arrays.equals(a, new int[] {(int) e.getX() / pieceSize, (int) e.getY() / pieceSize}))) {
                        // Drop piece
                        Piece p = getPiece((int) e.getX() / pieceSize, (int) e.getY() / pieceSize);
                        sente = !sente;
                        if (p != null) {
                            playSound(captureURL);
                            p.kill();
                            p.unpromote();
                            capture(p);
                            capturedPieces.put(String.valueOf(p.isSente) + p.type, capturedPieces.get(String.valueOf(p.isSente) + p.type) + 1);
                        } else {
                            playSound(moveURL);
                        }
                        boolean ded = selectedPiece.isDed;
                        selectedPiece.move((int) e.getX() / pieceSize, (int) e.getY() / pieceSize);
                        if (ded) {
                            selectedPiece = null;
                        } else {
                            promote(selectedPiece);
                        }
                    } else {
                        // Return piece
                        selectedPiece.updatePos();
                        if (!promotion) selectedPiece = null;
                    }
                }
                repaint();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });

        addKeyListener(new KeyListener() {

            @Override
            public void keyPressed(KeyEvent e) {
                if (promotion) {
                    if (e.getKeyChar() == '1') {
                        // Do not promote
                        promotion = false;
                    } else if (e.getKeyChar() == '2') {
                        // Promote
                        selectedPiece.promote();
                        promotion = false;
                    }
                    selectedPiece = null;
                    repaint();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }

            @Override
            public void keyTyped(KeyEvent e) {
            }
        });
        setVisible(true);
    }

    public void playSound(URL url) {
        AudioInputStream InputStream;
        try {
            InputStream = AudioSystem.getAudioInputStream(url);
            Clip clip = AudioSystem.getClip();
            clip.open(InputStream);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public Piece getPieceFromLocation(int x, int y) {
        for (int i = 0; i < pieces.length; i++) {
            if (pieces[i].xPos == (int) (x / pieceSize) && pieces[i].yPos == (int) (y / pieceSize)) {
                return pieces[i];
            }
        }
        return null;
    }

    public Piece getPiece(int x, int y) {
        for (int i = 0; i < pieces.length; i++) {
            if (inBounds(new int[] {pieces[i].xPos, pieces[i].yPos}) && pieces[i].xPos == x && pieces[i].yPos == y) {
                return pieces[i];
            }
        }
        return null;
    }

    public Piece getPieceFromName(String type, boolean isSente) {
        for (int i = 0; i < pieces.length; i++) {
            if (pieces[i].type.equals(type) && pieces[i].isSente == isSente) {
                return pieces[i];
            }
        }
        return null;
    }

    public int[][] getRawMoves(Piece p) {
        if (p.type.equals("knight")) {
            if (p.isSente) {
                if (p.isPromoted) return getRawMoves(trueGold);
                int[] xMoves = { -1, +1 };
                int[] yMoves = { -2, -2 };
                return (new int[][] { xMoves, yMoves });
            } else {
                if (p.isPromoted) return getRawMoves(falseGold);
                int[] xMoves = { -1, +1 };
                int[] yMoves = { +2, +2 };
                return (new int[][] { xMoves, yMoves });
            }
        } else if (p.type.equals("pawn")) {
            int mod = 1;
            if (p.isSente) {
                if (p.isPromoted) return getRawMoves(trueGold);
                mod = -1;
            } else {
                if (p.isPromoted) return getRawMoves(falseGold);
            }
            int[] xMoves = { 0 };
            int[] yMoves = { 1 * mod };
            return (new int[][] { xMoves, yMoves });
        } else if (p.type.equals("lance")) {
            int mod = 1;
            if (p.isSente) {
                if (p.isPromoted) return getRawMoves(trueGold);
                mod = -1;
            } else {
                if (p.isPromoted) return getRawMoves(falseGold);
            }
            int x;
            int y;
            ArrayList<Integer> xMoveList = new ArrayList<Integer>();
            ArrayList<Integer> yMoveList = new ArrayList<Integer>();
            x = p.xPos;
            y = p.yPos + mod;
            while (y < 9 && y >= 0) {
                if (getPiece(x, y) == null) {
                    yMoveList.add(y - p.yPos);
                    xMoveList.add(0);
                    y += mod;
                    continue;
                } else if (getPiece(x, y).isSente != p.isSente) {
                    yMoveList.add(y - p.yPos);
                    xMoveList.add(0);
                    break;
                } else {
                    break;
                }
            }

            int[] xMoves = new int[xMoveList.size()];
            int[] yMoves = new int[yMoveList.size()];
            for (int i = 0; i < xMoveList.size(); i++) {
                xMoves[i] = xMoveList.get(i);
            }
            for (int i = 0; i < yMoveList.size(); i++) {
                yMoves[i] = yMoveList.get(i);
            }
            return (new int[][] { xMoves, yMoves });
        } else if (p.type.equals("king")) {
            int[] xMoves = { -1, -1, -1, 0, 0, +1, +1, +1, +1, +1 };
            int[] yMoves = { -1, 0, +1, +1, -1, -1, 0, +1, 0, 0 };
            return (new int[][] { xMoves, yMoves });
        } else if (p.type.equals("silver")) {
            int[] xMoves = { -1, -1, 0, +1, +1 };
            int[] yMoves = new int[5];
            int mod;
            if (p.isSente) {
                if (p.isPromoted) return getRawMoves(trueGold);
                mod = 1;
            } else {
                if (p.isPromoted) return getRawMoves(falseGold);
                mod = -1;
            }
            // {-1, +1, -1, +1, -1}
            yMoves[0] = -1 * mod;
            yMoves[1] = 1 * mod;
            yMoves[2] = -1 * mod;
            yMoves[3] = 1 * mod;
            yMoves[4] = -1 * mod;
            return (new int[][] { xMoves, yMoves });
        } else if (p.type.equals("gold")) {
            int[] xMoves = { -1, 0, +1, +1, -1, 0 };
            int[] yMoves = new int[6];
            int mod;
            if (p.isSente) {
                mod = 1;
            } else {
                mod = -1;
            }
            // {-1, +1, -1, +1, -1}
            yMoves[0] = -1 * mod;
            yMoves[1] = -1 * mod;
            yMoves[2] = -1 * mod;
            yMoves[3] = 0;
            yMoves[4] = 0;
            yMoves[5] = +1 * mod;
            return (new int[][] { xMoves, yMoves });

        } else if (p.type.equals("rook")) {
            int x;
            int y;
            ArrayList<Integer> xMoveList = new ArrayList<Integer>();
            ArrayList<Integer> yMoveList = new ArrayList<Integer>();
            if (p.isPromoted) {
                xMoveList.add(-1);
                yMoveList.add(-1);

                xMoveList.add(-1);
                yMoveList.add(+1);

                xMoveList.add(+1);
                yMoveList.add(-1);

                xMoveList.add(+1);
                yMoveList.add(+1);
            }
            x = p.xPos;
            y = p.yPos + 1;
            while (x < 9 && x >= 0 && y < 9 && y >= 0) {
                if (getPiece(x, y) == null) {
                    yMoveList.add(y - p.yPos);
                    xMoveList.add(0);
                }
                else if (getPiece(x, y).isSente != p.isSente) {
                    yMoveList.add(y - p.yPos);
                    xMoveList.add(0);
                    break;
                } else {
                    break;
                }
                y++;
            }
            x = p.xPos;
            y = p.yPos - 1;
            while (x < 9 && x >= 0 && y < 9 && y >= 0) {
                if (getPiece(x, y) == null) {
                    yMoveList.add(y - p.yPos);
                    xMoveList.add(0);
                }
                else if (getPiece(x, y).isSente != p.isSente) {
                    yMoveList.add(y - p.yPos);
                    xMoveList.add(0);
                    break;
                } else {
                    break;
                }
                y--;
            }
            x = p.xPos + 1;
            y = p.yPos;
            while (x < 9 && x >= 0 && y < 9 && y >= 0) {
                if (getPiece(x, y) == null) {
                    xMoveList.add(x - p.xPos);
                    yMoveList.add(0);
                }
                else if (getPiece(x, y).isSente != p.isSente) {
                    xMoveList.add(x - p.xPos);
                    yMoveList.add(0);
                    break;
                } else {
                    break;
                }
                x++;
            }
            x = p.xPos - 1;
            y = p.yPos;
            while (x < 9 && x >= 0 && y < 9 && y >= 0) {
                if (getPiece(x, y) == null) {
                    xMoveList.add(x - p.xPos);
                    yMoveList.add(0);
                }
                else if (getPiece(x, y).isSente != p.isSente) {
                    xMoveList.add(x - p.xPos);
                    yMoveList.add(0);
                    break;
                } else {
                    break;
                }
                x--;
            }
            int[] xMoves = new int[xMoveList.size()];
            int[] yMoves = new int[yMoveList.size()];
            for (int i = 0; i < xMoveList.size(); i++) {
                xMoves[i] = xMoveList.get(i);
                yMoves[i] = yMoveList.get(i);
            }
            return (new int[][] { xMoves, yMoves });
        } else { // Bishop
            int x;
            int y;
            ArrayList<Integer> xMoveList = new ArrayList<Integer>();
            ArrayList<Integer> yMoveList = new ArrayList<Integer>();
            if (p.isPromoted) {
                xMoveList.add(-1);
                yMoveList.add(0);

                xMoveList.add(+1);
                yMoveList.add(0);

                xMoveList.add(0);
                yMoveList.add(-1);

                xMoveList.add(0);
                yMoveList.add(+1);
            }
            x = p.xPos + 1;
            y = p.yPos + 1;
            while (x < 9 && x >= 0 && y < 9 && y >= 0) {
                if (getPiece(x, y) == null) {
                    yMoveList.add(y - p.yPos);
                    xMoveList.add(x - p.xPos);
                } else if (getPiece(x, y).isSente != p.isSente) {
                    yMoveList.add(y - p.yPos);
                    xMoveList.add(x - p.xPos);
                    break;
                } else {
                    break;
                }
                y++;
                x++;
            }
            x = p.xPos + 1;
            y = p.yPos - 1;
            while (x < 9 && x >= 0 && y < 9 && y >= 0) {
                if (getPiece(x, y) == null) {
                    yMoveList.add(y - p.yPos);
                    xMoveList.add(x - p.xPos);
                } else if (getPiece(x, y).isSente != p.isSente) {
                    yMoveList.add(y - p.yPos);
                    xMoveList.add(x - p.xPos);
                    break;
                } else {
                    break;
                }
                y--;
                x++;
            }
            x = p.xPos - 1;
            y = p.yPos + 1;
            while (x < 9 && x >= 0 && y < 9 && y >= 0) {
                if (getPiece(x, y) == null) {
                    yMoveList.add(y - p.yPos);
                    xMoveList.add(x - p.xPos);
                } else if (getPiece(x, y).isSente != p.isSente) {
                    yMoveList.add(y - p.yPos);
                    xMoveList.add(x - p.xPos);
                    break;
                } else {
                    break;
                }
                y++;
                x--;
            }
            x = p.xPos - 1;
            y = p.yPos - 1;
            while (x < 9 && x >= 0 && y < 9 && y >= 0) {
                if (getPiece(x, y) == null) {
                    yMoveList.add(y - p.yPos);
                    xMoveList.add(x - p.xPos);
                } else if (getPiece(x, y).isSente != p.isSente) {
                    yMoveList.add(y - p.yPos);
                    xMoveList.add(x - p.xPos);
                    break;
                } else {
                    break;
                }
                y--;
                x--;
            }
            int[] xMoves = new int[xMoveList.size()];
            int[] yMoves = new int[yMoveList.size()];
            for (int i = 0; i < xMoveList.size(); i++) {
                xMoves[i] = xMoveList.get(i);
                yMoves[i] = yMoveList.get(i);
            }
            /*
             * int[] yMoves = {+1, -1, 0, 0};
             * int[] xMoves = { 0, 0, -1, +1};
             */
            return (new int[][] { xMoves, yMoves });

        }
    }

    public ArrayList<int[]> getAvailableSquares(Piece p) {
        ArrayList<int[]> squares = new ArrayList<int[]>();
        if (p.isDed) {
            // Piece dropping mechanics
            int yStart = 0;
            int yLimit = 9;
            if (p.type.equals("pawn") || p.type.equals("lance")) {
                if (p.isSente) {
                    yStart = 1;
                } else {
                    yLimit = 8;
                }
            } else if (p.type.equals("knight")) {
                if (p.isSente) {
                    yStart = 2;
                } else {
                    yLimit = 7;
                }
            }
            for (int r = yStart; r < yLimit; r++) {
                for (int c = 0; c < 9; c++) {
                    if (getPiece(c, r) == null) {
                        squares.add(new int[] {c, r});
                    }
                }
            }
        } else {
            int[][] moves = getRawMoves(p);
            for (int i = 0; i < moves[0].length; i++) {
                int[] move = new int[] {p.xPos + moves[0][i], p.yPos + moves[1][i]};
                if (inBounds(move)) {
                    if (getPiece(move[0], move[1]) == null) {
                        squares.add(move);
                    } else if (getPiece(move[0], move[1]).isSente != p.isSente) {
                        squares.add(move);
                    }
                }
            }
        }

        // Check if move leaves you in check
        int[] origin = {p.xPos, p.yPos};
        for (int i = 0; i < squares.size(); i++) {
            int[] move = squares.get(i);
            Piece killedPiece = getPiece(move[0], move[1]);
            boolean killedPromoted = false;
            p.fakeMove(move[0], move[1]);
            if (killedPiece != null) {
                killedPromoted = killedPiece.isPromoted;
                killedPiece.fakeMove(-3, -3);
            }
            if (isChecked(p.isSente)) {
                squares.remove(move);
            }
            p.fakeMove(origin[0], origin[1]);
            if (killedPiece != null) {
                killedPiece.fakeMove(move[0], move[1]);
                killedPiece.isPromoted = killedPromoted;
            }
        }

        squares.trimToSize();
        return squares;
    }

    public boolean inBounds(int[] pos) {
        if (pos[0] >= 0 && pos[0] < 9 && pos[1] >= 0 && pos[1] < 9) {
            return true;
        } else {
            return false;
        }
    }

    public void capture(Piece p) {
        int yPos = -1;
        int xPos = -1;
        if (p.isSente) {
            yPos = 8;
        } else {
            yPos = 0;
        }
        if (p.type.equals("pawn")) {
            xPos = 9;
        } else if (p.type.equals("lance")) {
            xPos = 10;
        } else if (p.type.equals("knight")) {
            xPos = 11;
        } else if (p.type.equals("silver")) {
            xPos = 12;
        } else if (p.type.equals("gold")) {
            xPos = 13;
        } else if (p.type.equals("bishop")) {
            xPos = 14;
        } else if (p.type.equals("rook")) {
            xPos = 15;
        }
        p.move(xPos, yPos);
    }

    public void promote(Piece p) {
        int floor;
        if (p.isPromoted) {
            selectedPiece = null;
            return;
        }
        int mod;
        if (p.isSente) {
            floor = 0;
            mod = 1;
        } else {
            floor = 8;
            mod = -1;
        }

        // Forced promotions
        if (p.yPos == floor && Arrays.asList("pawn", "lance", "knight").contains(p.type)) {
            p.promote();
            selectedPiece = null;
            return;
        } else if (p.yPos == floor+1*mod && p.type.equals("knight")) {
            p.promote();
            selectedPiece = null;
            return;
        }

        // Promotion choices
        if (Arrays.asList(floor, floor+1*mod, floor+2*mod).contains(p.yPos)) {
            promotion = true;
        } else {
            selectedPiece = null;
        }
    }

    public boolean isChecked(boolean sente) {
        int yMod;
        if (sente) yMod = -1;
        else yMod = 1;
        // Find the king on the board
        Piece p;
        if (sente) p = pieces[38];
        else p = pieces[39];
        // If the king is checked by a lance
        if (getPiece(p.xPos+1, p.yPos) != null) {
            if (getPiece(p.xPos+1, p.yPos).type.equalsIgnoreCase("lance") && getPiece(p.xPos+1, p.yPos).isSente != sente) return true;
        } else if (getPiece(p.xPos-1, p.yPos) != null) {
            if (getPiece(p.xPos-1, p.yPos).type.equalsIgnoreCase("lance") && getPiece(p.xPos-1, p.yPos).isSente != sente) return true;
        }

        int x = p.xPos;
        int y = p.yPos + yMod;
        while (y < 9 && y >= 0) {
            if (getPiece(x, y) == null) {
                y+=yMod;
                continue;
            }
            else if (getPiece(x, y).isSente != sente && (getPiece(x, y).type.equalsIgnoreCase("lance") || getPiece(x, y).type.equalsIgnoreCase("rook"))) {
                return(true);
            } else {
                break;
            }
        }
        // If the king is checked by a rook
        x = p.xPos;
        y = p.yPos - yMod;
        while ((y < 9 && y >= 0) && (x < 9 && x >= 0)) {
            if (getPiece(x, y) == null) {
                y-=yMod;
                continue;
            }
            else if (getPiece(x, y).isSente != sente && (getPiece(x, y).type.equalsIgnoreCase("rook"))) {
                return(true);
            } else {
                break;
            }
        }
        x = p.xPos+1;
        y = p.yPos;
        while ((y < 9 && y >= 0) && (x < 9 && x >= 0)) {
            if (getPiece(x, y) == null) {
                x++;
                continue;
            }
            else if (getPiece(x, y).isSente != sente && (getPiece(x, y).type.equalsIgnoreCase("rook"))) {
                return(true);
            } else {
                break;
            }
        }
        x = p.xPos-1;
        y = p.yPos;
        while ((y < 9 && y >= 0) && (x < 9 && x >= 0)) {
            if (getPiece(x, y) == null) {
                x--;
                continue;
            }
            else if (getPiece(x, y).isSente != sente && (getPiece(x, y).type.equalsIgnoreCase("rook"))) {
                return(true);
            } else {
                break;
            }
        }
        // If the king is checked by a bishop
        x = p.xPos + 1;
        y = p.yPos + 1;
        while ((y < 9 && y >= 0) && (x < 9 && x >= 0)) {
            if (getPiece(x, y) == null) {
                x++;
                y++;
                continue;
            } else if (getPiece(x, y).isSente != sente && (getPiece(x, y).type.equalsIgnoreCase("bishop"))) {
                return(true);
            } else {
                break;
            }
        }
        x = p.xPos - 1;
        y = p.yPos + 1;
        while ((y < 9 && y >= 0) && (x < 9 && x >= 0)) {
            if (getPiece(x, y) == null) {
                x--;
                y++;
                continue;
            } else if (getPiece(x, y).isSente != sente && (getPiece(x, y).type.equalsIgnoreCase("bishop"))) {
                return(true);
            } else {
                break;
            }
        }
        x = p.xPos + 1;
        y = p.yPos - 1;
        while ((y < 9 && y >= 0) && (x < 9 && x >= 0)) {
            if (getPiece(x, y) == null) {
                x++;
                y--;
                continue;
            } else if (getPiece(x, y).isSente != sente && (getPiece(x, y).type.equalsIgnoreCase("bishop"))) {
                return(true);
            } else {
                break;
            }
        }
        x = p.xPos - 1;
        y = p.yPos - 1;
        while ((y < 9 && y >= 0) && (x < 9 && x >= 0)) {
            if (getPiece(x, y) == null) {
                x--;
                y--;
                continue;
            } else if (getPiece(x, y).isSente != sente && (getPiece(x, y).type.equalsIgnoreCase("bishop"))) {
                return(true);
            } else {
                break;
            }
        }
        // If the king is checked by a knight
        x = p.xPos;
        y = p.yPos;
        int[] knightX = {-1, +1};
        int[] knightY = {+2, +2};
        for (int i = 0; i < knightX.length; i++) {
            if (getPiece((x+knightX[i]), (y+knightY[i])) == null) continue;
            else if (getPiece((x+knightX[i]), (y+knightY[i])).type.equalsIgnoreCase("knight") && getPiece((x+knightX[i]), (y+knightY[i])).isSente != sente && !getPiece((x+knightX[i]), (y+knightY[i])
            ).isPromoted) {
                return(true);
            }
        }

        // If the king is checked by a silver
        x = p.xPos;
        y = p.yPos;
        int[] silverX = {-1, -1,  0, +1, +1};
        int[] silverY = new int[5];
        int mod = -yMod;
        // {-1, +1, -1, +1, -1}
        silverY[0] = -1*mod;
        silverY[1] =  1*mod;
        silverY[2] = -1*mod;
        silverY[3] =  1*mod;
        silverY[4] = -1*mod;

        for (int i = 0; i < silverX.length; i++) {
            if (getPiece((x+silverX[i]), (y+silverY[i])) == null) continue;
            if (getPiece((x+silverX[i]), (y+silverY[i])) != null && (getPiece((x+silverX[i]), (y+silverY[i])).type.equalsIgnoreCase("silver")) && getPiece((x+silverX[i]), (y+silverY[i])).isSente != sente) {
                return(true);
            }
        }

        // If the king is checked by a gold
        x = p.xPos;
        y = p.yPos;
        int[] goldX = {-1, -1,  0, +1, +1};
        int[] goldY = new int[5];
        // {-1, +1, -1, +1, -1}
        goldY[0] = 0;
        goldY[1] = -1*mod;
        goldY[2] = 1*mod;
        goldY[3] = -1*mod;
        goldY[4] = 0;

        for (int i = 0; i < silverX.length; i++) {
            if (getPiece((x+goldX[i]), (y+goldY[i])) == null) continue;
            if (getPiece((x+goldX[i]), (y+goldY[i])) != null && (getPiece((x+goldX[i]), (y+goldY[i])).type.equalsIgnoreCase("gold")) && getPiece((x+goldX[i]), (y+goldY[i])).isSente != sente) {
                return(true);
            }
        }

        // If the king is checked by a king
        x = p.xPos;
        y = p.yPos;
        int[] kingX = {-1, -1, -1,  0,  0, +1, +1, +1};
        int[] kingY = {-1,  0, +1, +1, -1, -1,  0, +1};
        for (int i = 0; i < kingX.length; i++) {
            if (getPiece(x+kingX[i], y+kingY[i]) == null) continue;
            if (getPiece(x+kingX[i], y+kingY[i]) != null && (getPiece(x+kingX[i], y+kingY[i]).type.equalsIgnoreCase("king") || ((getPiece(x+kingX[i], y+kingY[i]).type.equalsIgnoreCase("bishop")) || getPiece(x+kingX[i], y+kingY[i]).type.equalsIgnoreCase("rook")) && (getPiece(x+kingX[i], y+kingY[i]).isPromoted)) && getPiece(x+kingX[i], y+kingY[i]).isSente != sente) {
                return true;
            }
        }
        // If the king is checked by a pawn
        x = p.xPos;
        y = p.yPos;
        if (getPiece(x, y+1*yMod) != null) {
            if (getPiece(x, y+1*yMod).type.equals("pawn")) return true;
        }

        return(false);
    }
    
    public static void main(String[] args) throws Exception {
        new App(1, 64);
    }
}