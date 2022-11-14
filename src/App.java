import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

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
    private boolean[][] files = {{true, true, true, true, true, true, true, true, true}, 
                                 {true, true, true, true, true, true, true, true, true}};
    private Map<String, Integer> capturedPieces = new HashMap<>();
    private Piece selectedPiece;
    private URL moveURL = App.class.getResource("move.wav");
    private URL captureURL = App.class.getResource("capture.wav");
    private JPanel gamePanel;
    private Piece trueGold = new Piece("gold", true, -9);
    private Piece falseGold = new Piece("gold", false, -9);
    private boolean promotion = false;
    private int positionsEvaluated = 0;

    public App(int numPlayers, int pieceSize, boolean start) throws IOException {
        this.numPlayers = numPlayers;
        this.pieceSize = pieceSize;
        this.sente = start;

        // Pawns
        for (int i = 0; i < 9; i++) {
            pieces[i] = new Piece("pawn", true, i+54);
        }
        for (int i = 0; i < 9; i++) {
            pieces[9 + i] = new Piece("pawn", false, i+18);
        }
        // Lances
        pieces[18] = new Piece("lance", true, 80);
        pieces[19] = new Piece("lance", true, 72);
        pieces[20] = new Piece("lance", false, 0);
        pieces[21] = new Piece("lance", false, 8);
        // Knights
        pieces[22] = new Piece("knight", true, 73);
        pieces[23] = new Piece("knight", true, 79);
        pieces[24] = new Piece("knight", false, 1);
        pieces[25] = new Piece("knight", false, 7);
        // Silvers
        pieces[26] = new Piece("silver", true, 74);
        pieces[27] = new Piece("silver", true, 78);
        pieces[28] = new Piece("silver", false, 2);
        pieces[29] = new Piece("silver", false, 6);
        // Golds
        pieces[30] = new Piece("gold", true, 75);
        pieces[31] = new Piece("gold", true, 77);
        pieces[32] = new Piece("gold", false, 3);
        pieces[33] = new Piece("gold", false, 5);
        // Bishops
        pieces[34] = new Piece("bishop", true, 64);
        pieces[35] = new Piece("bishop", false, 16);
        // Rook
        pieces[36] = new Piece("rook", true, 70);
        pieces[37] = new Piece("rook", false, 10);
        // King
        pieces[38] = new Piece("king", true, 76);
        pieces[39] = new Piece("king", false, 4);

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

        // Promotion image
        BufferedImage bufferedPromo = ImageIO.read(getClass().getResource("promo.png"));
        Image promoImage = (bufferedPromo.getScaledInstance(pieceSize * 9, pieceSize * 9, BufferedImage.SCALE_SMOOTH));

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
                    for (int move : getAvailableSquares(selectedPiece)) {
                        if (getPiece(move) == null) {
                            g.drawImage(moveImage, (move%9) * pieceSize, (move/9) * pieceSize, this);
                        } else {
                            g.drawImage(captureImage, (move%9) * pieceSize, (move/9) * pieceSize, this);
                        }
                    }
                }

                for (int i = 0; i < pieces.length; i++) {
                    g.drawImage(pieceImages.get(
                            String.valueOf(pieces[i].isSente) + String.valueOf(pieces[i].isPromoted) + pieces[i].type),
                            pieces[i].x, pieces[i].y, this);
                }
                
                if (promotion) {
                    g.drawImage(promoImage, 0, 0, null);
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
                    selectedPiece.y = e.getY() - pieceSize / 2;
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
                    if (e.getX()/pieceSize < 9 && !promotion && getAvailableSquares(selectedPiece).contains((int) e.getX() / pieceSize + ((int) e.getY() / pieceSize)*9)) {
                        // Drop piece
                        Piece p = getPieceFromLocation(e.getX(), e.getY());
                        sente = !sente;
                        if (p != null) {
                            playSound(captureURL);
                            if (p.type.equals("pawn")) {
                                int i = 1;
                                if (p.isSente) i = 0;
                                files[i][p.pos%9] = false;    
                            }
                            p.kill();
                            p.unpromote();
                            capture(p);
                            capturedPieces.put(String.valueOf(p.isSente) + p.type, capturedPieces.get(String.valueOf(p.isSente) + p.type) + 1);
                        } else {
                            playSound(moveURL);
                        }
                        boolean ded = selectedPiece.isDed;
                        selectedPiece.move((int) e.getX() / pieceSize + ((int) e.getY() / pieceSize)*9);
                        boolean promo = false;
                        if (ded) {
                            selectedPiece = null;
                        } else {
                            promo = promote(selectedPiece);
                        }
                        repaint();
                        if (!promo && numPlayers == 1) {
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    //System.out.println(search(2, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, sente));
                                    long startTime = System.nanoTime();
                                    aiMove();
                                    System.out.println((System.nanoTime() - startTime)/1000000 + "ms");
                                }
                            });    
                        }
                    } else {
                        // Return piece
                        selectedPiece.updatePos();
                        if (!promotion) selectedPiece = null;
                        repaint();
                    }
                }
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
                    if (numPlayers == 1) {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                long startTime = System.nanoTime();
                                aiMove();
                                System.out.println((System.nanoTime() - startTime)/1000000 + "ms");
                            }
                        });     
                    }
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
        if (!sente) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    //aiMove();
                    //pieces[15].move(6, 3);
                    sente = !sente;
                }
            });    
        }
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
            if (pieces[i].pos == (int) x / pieceSize + ((int) y / pieceSize)*9) {
                return pieces[i];
            }
        }
        return null;
    }

    public Piece getPiece(int pos) {
        for (int i = 0; i < pieces.length; i++) {
            if (pieces[i].pos == pos) {
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

    public int[] getRawMoves(Piece p) {
        if (p.type.equals("knight")) {
            if (p.isSente) {
                if (p.isPromoted) return getRawMoves(trueGold);
                if (p.pos%9 == 8) return (new int[] { -19 });
                else if (p.pos%9 == 0) return (new int[] { -17 });
                return (new int[] { -17, -19 });
            } else {
                if (p.isPromoted) return getRawMoves(falseGold);
                if (p.pos%9 == 8) return (new int[] { +17 });
                else if (p.pos%9 == 0) return (new int[] { +19 });
                return (new int[] { +17, +19 });
            }
        } else if (p.type.equals("pawn")) {
            int mod = 1;
            if (p.isSente) {
                if (p.isPromoted) return getRawMoves(trueGold);
                mod = -1;
            } else {
                if (p.isPromoted) return getRawMoves(falseGold);
            }
            return (new int[] { 9*mod });
        } else if (p.type.equals("lance")) {
            int mod = 1;
            if (p.isSente) {
                if (p.isPromoted) return getRawMoves(trueGold);
                mod = -1;
            } else {
                if (p.isPromoted) return getRawMoves(falseGold);
            }
            int pos = p.pos + 9*mod;
            ArrayList<Integer> moveList = new ArrayList<Integer>();
            while (inBounds(pos)) {
                if (getPiece(pos) == null) {
                    moveList.add(pos - p.pos);
                    pos += 9*mod;
                    continue;
                } else if (getPiece(pos).isSente != p.isSente) {
                    moveList.add(pos - p.pos);
                    break;
                } else {
                    break;
                }
            }
            return (moveList.stream().mapToInt(i -> i).toArray());
        } else if (p.type.equals("king")) {
            if (p.pos%9 == 8) return (new int[] { -10, -9, -1, +8, +9 });
            else if (p.pos%9 == 0) return (new int[] { -9, -8, +1, +9, +10 });
            return (new int[] { -10, -9, -8, -1, +1, +8, +9, +10 });
        } else if (p.type.equals("silver")) {
            if (p.isSente) {
                if (p.isPromoted) return getRawMoves(trueGold);
                if (p.pos%9 == 8) return (new int[] { -10, -9, 8 });
                else if (p.pos%9 == 0) return (new int[] {  -9, -8, 10 });
                return (new int[] { -10, -9, -8, 8, 10 });
            } else {
                if (p.isPromoted) return getRawMoves(falseGold);
                if (p.pos%9 == 8) return (new int[] { 9, 8, -10 });
                else if (p.pos%9 == 0) return (new int[] {  10, 9, -8 });
                return (new int[] { 10, 9, 8, -8, -10 });
            }
        } else if (p.type.equals("gold")) {
            if (p.isSente) {
                if (p.pos%9 == 8) return (new int[] { -10, -9, -1, +9 });
                else if (p.pos%9 == 0) return (new int[] { -9, -8, +1, +9 });
                return (new int[] { -10, -9, -8, -1, +1, +9 });
            } else {
                if (p.pos%9 == 8) return (new int[] { 9, 8, -1, -9 });
                else if (p.pos%9 == 0) return (new int[] {  10, 9, +1, -9 });
                return (new int[] { 10, 9, 8, -1, +1, -9 });
            }

        } else if (p.type.equals("rook")) {
            int pos;
            ArrayList<Integer> moveList = new ArrayList<Integer>();
            if (p.isPromoted) {
                // Add diagonal moves
                if (p.pos%9 < 8) {
                    moveList.add(-8);
                    moveList.add(+10);
                }
                if (p.pos%9 > 0) {
                    moveList.add(-10);
                    moveList.add(+8);    
                }
            }
            pos = p.pos+9;
            while (pos < 81) {
                if (getPiece(pos) == null) {
                    moveList.add(pos - p.pos);
                    pos += 9;
                }
                else if (getPiece(pos).isSente != p.isSente) {
                    moveList.add(pos - p.pos);
                    break;
                } else {
                    break;
                }
            }
            pos = p.pos-9;
            while (pos >= 0) {
                if (getPiece(pos) == null) {
                    moveList.add(pos - p.pos);
                    pos -= 9;
                }
                else if (getPiece(pos).isSente != p.isSente) {
                    moveList.add(pos - p.pos);
                    break;
                } else {
                    break;
                }
            }
            int rowStart = ((int)(p.pos/9))*9;
            for (int i = p.pos+1; i < rowStart+9; i++) {
                if (getPiece(i) == null) {
                    moveList.add(i - p.pos);
                }
                else if (getPiece(i).isSente != p.isSente) {
                    moveList.add(i - p.pos);
                    break;
                } else {
                    break;
                }
            }
            for (int i = p.pos-1; i >= rowStart; i--) {
                if (getPiece(i) == null) {
                    moveList.add(i - p.pos);
                }
                else if (getPiece(i).isSente != p.isSente) {
                    moveList.add(i - p.pos);
                    break;
                } else {
                    break;
                }
            }
            return (moveList.stream().mapToInt(i -> i).toArray());
        } else { // Bishop
            int pos;
            ArrayList<Integer> moveList = new ArrayList<Integer>();
            if (p.isPromoted) {
                // Add horizontal moves
                if (p.pos%9 < 8) {
                    moveList.add(+1);
                }
                if (p.pos%9 > 0) {
                    moveList.add(-1);
                }
                moveList.add(-9);
                moveList.add(+9);
            }
            int rowStart = ((int)(p.pos/9))*9;
            pos = p.pos + 10;
            for (int i = p.pos+1; i < rowStart+9; i++) {
                if (getPiece(pos) == null) {
                    moveList.add(pos - p.pos);
                }
                else if (getPiece(pos).isSente != p.isSente) {
                    moveList.add(pos - p.pos);
                    break;
                } else {
                    break;
                }
                pos += 10;
            }
            pos = p.pos + 8;
            for (int i = p.pos-1; i >= rowStart; i--) {
                if (getPiece(pos) == null) {
                    moveList.add(pos - p.pos);
                }
                else if (getPiece(pos).isSente != p.isSente) {
                    moveList.add(pos - p.pos);
                    break;
                } else {
                    break;
                }
                pos += 8;
            }
            pos = p.pos - 10;
            for (int i = p.pos-1; i >= rowStart; i--) {
                if (getPiece(pos) == null) {
                    moveList.add(pos - p.pos);
                }
                else if (getPiece(pos).isSente != p.isSente) {
                    moveList.add(pos - p.pos);
                    break;
                } else {
                    break;
                }
                pos -= 10;
            }
            pos = p.pos - 8;
            for (int i = p.pos+1; i < rowStart+9; i++) {
                if (getPiece(pos) == null) {
                    moveList.add(pos - p.pos);
                }
                else if (getPiece(pos).isSente != p.isSente) {
                    moveList.add(pos - p.pos);
                    break;
                } else {
                    break;
                }
                pos -= 8;
            }
            return (moveList.stream().mapToInt(i -> i).toArray());

        }
    }

    public ArrayList<Integer> getAvailableSquares(Piece p) {
        ArrayList<Integer> squares = getSemiAvailableSquares(p);
        // Check if move leaves you in check
        if (!(p.isDed && !isChecked(p.isSente))) {
            int origin = p.pos;
            for (int i = 0; i < squares.size(); i++) {
                int move = squares.get(i);
                Piece killedPiece = getPiece(move);
                boolean killedPromoted = false;
                p.fakeMove(move);
                if (killedPiece != null) {
                    killedPromoted = killedPiece.isPromoted;
                    killedPiece.fakeMove(-18);
                }
                if (isChecked(p.isSente)) {
                    squares.remove(i);
                    i--;
                }
                p.fakeMove(origin);
                if (killedPiece != null) {
                    killedPiece.fakeMove(move);
                    killedPiece.isPromoted = killedPromoted;
                }
            }
            squares.trimToSize();
        }

        return squares;
    }

    public ArrayList<Integer> getSemiAvailableSquares(Piece p) {
        ArrayList<Integer> squares = new ArrayList<Integer>();
        if (p.pos < 0) return squares;
        if (p.isDed && p.pos >= 81) {
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
            if (p.type.equals("pawn")) {
                int i = 1;
                if (p.isSente) i = 0;
                for (int index = yStart*9; index < yLimit*9; index++) {
                    if (getPiece(index) == null && !files[i][index%9]) { // Double pawn drop
                        squares.add(index);
                    }
                } 
            } else {
                for (int index = yStart*9; index < yLimit*9; index++) {
                    if (getPiece(index) == null) {
                        squares.add(index);
                    }
                } 

            }
        } else {
            int[] moves = getRawMoves(p);
            for (int i = 0; i < moves.length; i++) {
                int move = p.pos + moves[i];
                if (inBounds(move)) { // << FIX - GOTTA CHECK IF PIECE FALLS OFF MAP
                    if (getPiece(move) == null) {
                        squares.add(move);
                    } else if (getPiece(move).isSente != p.isSente) {
                        squares.add(move);
                    }
                }
            }
        }
        return squares;
    }

    public boolean inBounds(int pos) {
        if (pos >= 0 && pos < 81) {
            return true;
        }
        return false;
    }

    public void capture(Piece p) {
        int pos = -9;
        int yMod = 0;
        if (!p.isSente) {
            yMod = 9;
        }
        if (p.type.equals("pawn")) {
            pos = 81+yMod;
        } else if (p.type.equals("lance")) {
            pos = 82+yMod;
        } else if (p.type.equals("knight")) {
            pos = 83+yMod;
        } else if (p.type.equals("silver")) {
            pos = 84+yMod;
        } else if (p.type.equals("gold")) {
            pos = 85+yMod;
        } else if (p.type.equals("bishop")) {
            pos = 86+yMod;
        } else if (p.type.equals("rook")) {
            pos = 87+yMod;
        }
        p.move(pos);
    }

    public boolean promote(Piece p) {
        int floor;
        if (p.isPromoted) {
            selectedPiece = null;
            return false;
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
        if (p.pos/9 == floor && Arrays.asList("pawn", "lance", "knight").contains(p.type)) {
            p.promote();
            selectedPiece = null;
            return false;
        } else if (p.pos/9 == floor+1*mod && p.type.equals("knight")) {
            p.promote();
            selectedPiece = null;
            return false;
        }

        // Promotion choices
        if (Arrays.asList(floor, floor+1*mod, floor+2*mod).contains(p.pos/9)) {
            if (numPlayers == 1 && !p.isSente) {
                p.promote();
                return false;
            }
            promotion = true;
            return true;
        } else {
            selectedPiece = null;
        }
        return false;
    }

    public boolean isChecked(boolean sente) {
        int yMod;
        if (sente) yMod = -1;
        else yMod = 1;
        // Find the king on the board
        Piece p;
        if (sente) p = pieces[38];
        else p = pieces[39];

        // Knight checks
        if (getPiece(p.pos+8*yMod) != null && getPiece(p.pos+8).isSente != p.isSente && getPiece(p.pos+8*yMod).type.equals("knight")) {
            return true;
        }
        if (getPiece(p.pos+8*yMod) != null && getPiece(p.pos+8).isSente != p.isSente && getPiece(p.pos+8*yMod).type.equals("knight")) {
            return true;
        }

        // Look through every direction
        int rowStart = (p.pos/9) * 9;


        // // If the king is checked by a lance
        // for(int i = 18; i <= 21; i++) {
        //     if (pieces[i].isSente != p.isSente && !p.isPromoted) {
        //         if (getPiece(getSemiAvailableSquares(pieces[i]).size()) == p) {
        //             return true;
        //         }
        //     }
        // }

        // // If the king is checked by a pawn
        // if (getPiece(p.pos+9*yMod) != null) {
        //     if (getPiece(p.pos+9*yMod).type.equals("pawn") && !getPiece(p.pos+9*yMod).isPromoted && getPiece(p.pos+9*yMod).isSente != p.isSente) return true;
        // }

        // // Pretty much any other piece
        // for (int i = 22; i <= 39; i++) {
        //     if (pieces[i].isSente != p.isSente) {
        //         for (int move : getSemiAvailableSquares(pieces[i])) {
        //             if (getPiece(move) == p) {
        //                 return true;
        //             }
        //         }
        //     }
        // }

        return(false);
    }

    public boolean isCheckmated(boolean sente) {
        if (!isChecked(sente)) {
            return false;
        } else {
            for (int i = 0; i < pieces.length; i++) {
                if (pieces[i].isSente == sente && getAvailableSquares(pieces[i]).size() > 0) return false;
            }
            return true;
        }
    }

    public float evaluate() {
        float evaluation = 0;
        if (isCheckmated(true)) return Float.NEGATIVE_INFINITY;
        for(Piece p : pieces) {
            if (p.isSente) {
                if (p.pos /9 <= 2 && !p.isPromoted) {
                    p.isPromoted = true;
                    p.updateValue();
                    p.isPromoted = false;
                } else {
                    p.updateValue();
                }
                evaluation += p.value;
                if (!p.isDed) {
                    evaluation += ((float) getSemiAvailableSquares(p).size())/10;
                }
            } else {
                if (p.pos /9 >= 6 && !p.isPromoted) {
                    p.isPromoted = true;
                    p.updateValue();
                    p.isPromoted = false;
                } else {
                    p.updateValue();
                }
                evaluation -= p.value;
                if (!p.isDed) {
                    evaluation -= ((float) getSemiAvailableSquares(p).size())/10;
                }
            }
        }
        return (float) Math.round(evaluation*100.0)/100;
    }

    public List<Integer> orderMoves(Piece p, ArrayList<Integer> squares) {
        // Order captures, safer moves, forward moves, backward moves
        Map<Integer, Double> orderedMoves = new HashMap<>();
        for (int move : squares) {
            double guess = 0;
            Piece killedPiece = getPiece(move);

            // Captures are usually good
            if (killedPiece != null) {
                guess = 10 * killedPiece.value - p.value;
            }

            int yMod = 1;
            if (p.isSente) {
                yMod = -1;
            }

            // Moving in front of a pawn is usually bad
            Piece pawn = getPiece(move+9*yMod);
            if (pawn != null && pawn.type.equals("pawn")) {
                guess -= p.value;
            }

            // Forward moves are usually good
            int direction = yMod * (move/9 - p.pos/9);
            if (direction > 0) {
                guess += 0.5;
            }
            // Backward moves are usually bad
            else if (direction < 0) {
                guess -= 0.5;
            }

            orderedMoves.put(move, guess);
        }
        return orderedMoves.entrySet().stream().sorted(Comparator.comparing(Map.Entry::getValue, Comparator.reverseOrder())).map(Map.Entry::getKey).collect(Collectors.toList());
    }

    public float quiscenceSearch(float alpha, float beta, boolean isSente) {
        float stand = evaluate();
        if (stand >= beta) {
            return beta;
        }
        if (alpha < stand) {
            alpha = stand;
        }

        for (Piece p : pieces) {
            if (!p.isDed && p.isSente == isSente) {
                //for (int[] move : orderMoves(p, getSemiAvailableSquares(p))) {
                for (int move : getSemiAvailableSquares(p)) {
                    int origin = p.pos;
                    Piece killedPiece = getPiece(move);
                    boolean killedPromoted = false;
                    p.fakeMove(move);
                    if (killedPiece != null) {
                        killedPromoted = killedPiece.isPromoted;
                        killedPiece.fakeMove(81);
                        killedPiece.isSente = !killedPiece.isSente;
                        killedPiece.isDed = true;
                        if (!isChecked(p.isSente)) {
                            float evaluation = -quiscenceSearch(-beta, -alpha, !isSente);
                            p.fakeMove(origin);
                            if (killedPiece != null) {
                                killedPiece.fakeMove(move);
                                killedPiece.isDed = false;
                                killedPiece.isSente = !killedPiece.isSente;
                                killedPiece.isPromoted = killedPromoted;
                            }
                            if (evaluation >= beta) {
                                return beta;
                            }
                            if (evaluation > alpha) {
                                alpha = evaluation;
                            }
                        } else {
                            p.fakeMove(origin);
                            if (killedPiece != null) {
                                killedPiece.fakeMove(move);
                                killedPiece.isDed = false;
                                killedPiece.isSente = !killedPiece.isSente;
                                killedPiece.isPromoted = killedPromoted;
                            }    
                        }
                    } else {
                        p.fakeMove(origin);
                    }
                }    
            }
        }

        return alpha;
    }

    public float search(int depth, float alpha, float beta, boolean isSente) {
        if (depth == 0) {
            positionsEvaluated++;
            //return evaluate();
            return quiscenceSearch(alpha, beta, isSente);
        }
        if (isCheckmated(sente)) {
            return Float.NEGATIVE_INFINITY;
        }
        ArrayList<String> dead = new ArrayList<>();
        for (Piece p : pieces) {
            if (p.isDed) {
                if (dead.contains(p.type)) {
                    continue;
                } else {
                    dead.add(p.type);
                }    
            }
            if (p.isSente == isSente) {
                for (int move : orderMoves(p, getSemiAvailableSquares(p))) {
                //for (int[] move : getSemiAvailableSquares(p)) {
                    int origin = p.pos;
                    Piece killedPiece = getPiece(move);
                    boolean killedPromoted = false;
                    p.fakeMove(move);
                    if (killedPiece != null) {
                        killedPromoted = killedPiece.isPromoted;
                        killedPiece.fakeMove(81);
                        killedPiece.isSente = !killedPiece.isSente;
                        killedPiece.isDed = true;
                    }

                    if (!isChecked(p.isSente)) {
                        float evaluation = -search(depth-1, -beta, -alpha, !isSente);
                        p.fakeMove(origin);
                        if (killedPiece != null) {
                            killedPiece.fakeMove(move);
                            killedPiece.isDed = false;
                            killedPiece.isSente = !killedPiece.isSente;
                            killedPiece.isPromoted = killedPromoted;
                        }
                        if (evaluation >= beta) {
                            return beta;
                        }
                        if (evaluation > alpha) {
                            alpha = evaluation;
                        }
                    } else {
                        p.fakeMove(origin);
                        if (killedPiece != null) {
                            killedPiece.fakeMove(move);
                            killedPiece.isDed = false;
                            killedPiece.isSente = !killedPiece.isSente;
                            killedPiece.isPromoted = killedPromoted;
                        }
                    }
                }    
            }
        }
        return alpha;
    }

    public void aiMove() {
        float bestEvaluation = Float.POSITIVE_INFINITY;
        Piece bestPiece = null;
        int bestMove = -9;
        positionsEvaluated = 0;
        ArrayList<String> dead = new ArrayList<>();
        for (Piece p : pieces) {
            if (p.isDed) {
                if (dead.contains(p.type)) {
                    continue;
                } else {
                    dead.add(p.type);
                }    
            }
            if (!p.isSente) {
                //for (int[] move : getSemiAvailableSquares(p)) {
                for (int move : getSemiAvailableSquares(p)) {
                    int origin = p.pos;
                    Piece killedPiece = getPiece(move);
                    boolean killedPromoted = false;
                    p.fakeMove(move);
                    if (killedPiece != null) {
                        killedPromoted = killedPiece.isPromoted;
                        killedPiece.fakeMove(-18);
                        killedPiece.isSente = !killedPiece.isSente;
                        killedPiece.isDed = true;
                    }

                    if (!isChecked(false)) {
                        float evaluation;// = -search(3, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, true);
                        if (killedPiece != null) {
                            evaluation = -quiscenceSearch(Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, true);
                        } else {
                            evaluation = -search(3, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, true);
                        }
                        if (evaluation < bestEvaluation) {
                            bestEvaluation = evaluation;
                            bestPiece = p;
                            bestMove = move;
                        }    
                    }

                    p.fakeMove(origin);
                    if (killedPiece != null) {
                        killedPiece.fakeMove(move);
                        killedPiece.isDed = false;
                        killedPiece.isSente = !killedPiece.isSente;
                        killedPiece.isPromoted = killedPromoted;
                    }
                }
            }
        }
        System.out.println(bestEvaluation);
        sente = !sente;
        Piece p = getPiece(bestMove);
        if (p != null) {
            playSound(captureURL);
            if (p.type.equals("pawn")) {
                int i = 1;
                if (p.isSente) i = 0;
                files[i][p.pos%9] = false;    
            }            
            p.kill();
            p.unpromote();
            capture(p);
            capturedPieces.put(String.valueOf(p.isSente) + p.type, capturedPieces.get(String.valueOf(p.isSente) + p.type) + 1);
        } else {
            playSound(moveURL);
        }
        bestPiece.move(bestMove);
        promote(bestPiece);
        sente = true;
        repaint();
        System.out.println(positionsEvaluated + " positions evaluated");
    }
    
    public static void main(String[] args) throws Exception {
        new App(1, 64, true);
    }
}