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
    private int pieceSize;
    Piece[] pieces = new Piece[40];
    Piece selectedPiece;
    URL moveURL = App.class.getResource("move.wav");
    URL captureURL = App.class.getResource("capture.wav");
    JPanel gamePanel;

    public App(int numPlayers, int pieceSize) throws IOException {
        this.numPlayers = numPlayers;
        this.pieceSize = pieceSize;

        // Pawns
        for (int i = 0; i < 9; i++) {
            pieces[i] = new Piece("pawn", true, 6, i);
        }
        for (int i = 0; i < 9; i++) {
            pieces[9+i] = new Piece("pawn", false, 2, i);
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

        // Constructing the GUI
        setSize((int) (pieceSize * 9 * 16/9) , (int) (pieceSize * 9));
        setUndecorated(true);

        // Board image
        BufferedImage bufferedBoard = ImageIO.read(getClass().getResource("board.png"));
        Image boardImage = (bufferedBoard.getScaledInstance(pieceSize*9, pieceSize*9, BufferedImage.SCALE_SMOOTH));

        // Piece images
        BufferedImage bufferedPieces = ImageIO.read(getClass().getResource("pieces.png"));
        Map<String, Image> pieceImages = new HashMap<>();
        pieceImages.put("truefalsegold", bufferedPieces.getSubimage(0, 0, 100, 100).getScaledInstance(pieceSize, pieceSize, BufferedImage.SCALE_SMOOTH));
        pieceImages.put("falsefalsegold", bufferedPieces.getSubimage(0, 100, 100, 100).getScaledInstance(pieceSize, pieceSize, BufferedImage.SCALE_SMOOTH));
        pieceImages.put("truefalserook", bufferedPieces.getSubimage(100, 0, 100, 100).getScaledInstance(pieceSize, pieceSize, BufferedImage.SCALE_SMOOTH));
        pieceImages.put("falsefalserook", bufferedPieces.getSubimage(100, 100, 100, 100).getScaledInstance(pieceSize, pieceSize, BufferedImage.SCALE_SMOOTH));
        pieceImages.put("truetruerook", bufferedPieces.getSubimage(200, 0, 100, 100).getScaledInstance(pieceSize, pieceSize, BufferedImage.SCALE_SMOOTH));
        pieceImages.put("falsetruerook", bufferedPieces.getSubimage(200, 100, 100, 100).getScaledInstance(pieceSize, pieceSize, BufferedImage.SCALE_SMOOTH));
        pieceImages.put("truefalsebishop", bufferedPieces.getSubimage(300, 0, 100, 100).getScaledInstance(pieceSize, pieceSize, BufferedImage.SCALE_SMOOTH));
        pieceImages.put("falsefalsebishop", bufferedPieces.getSubimage(300, 100, 100, 100).getScaledInstance(pieceSize, pieceSize, BufferedImage.SCALE_SMOOTH));
        pieceImages.put("truetruebishop", bufferedPieces.getSubimage(400, 0, 100, 100).getScaledInstance(pieceSize, pieceSize, BufferedImage.SCALE_SMOOTH));
        pieceImages.put("falsetruebishop", bufferedPieces.getSubimage(400, 100, 100, 100).getScaledInstance(pieceSize, pieceSize, BufferedImage.SCALE_SMOOTH));
        pieceImages.put("truefalsesilver", bufferedPieces.getSubimage(500, 0, 100, 100).getScaledInstance(pieceSize, pieceSize, BufferedImage.SCALE_SMOOTH));
        pieceImages.put("falsefalsesilver", bufferedPieces.getSubimage(500, 100, 100, 100).getScaledInstance(pieceSize, pieceSize, BufferedImage.SCALE_SMOOTH));
        pieceImages.put("truetruesilver", bufferedPieces.getSubimage(600, 0, 100, 100).getScaledInstance(pieceSize, pieceSize, BufferedImage.SCALE_SMOOTH));
        pieceImages.put("falsetruesilver", bufferedPieces.getSubimage(600, 100, 100, 100).getScaledInstance(pieceSize, pieceSize, BufferedImage.SCALE_SMOOTH));
        pieceImages.put("truefalseknight", bufferedPieces.getSubimage(700, 0, 100, 100).getScaledInstance(pieceSize, pieceSize, BufferedImage.SCALE_SMOOTH));
        pieceImages.put("falsefalseknight", bufferedPieces.getSubimage(700, 100, 100, 100).getScaledInstance(pieceSize, pieceSize, BufferedImage.SCALE_SMOOTH));
        pieceImages.put("truetrueknight", bufferedPieces.getSubimage(800, 0, 100, 100).getScaledInstance(pieceSize, pieceSize, BufferedImage.SCALE_SMOOTH));
        pieceImages.put("falsetrueknight", bufferedPieces.getSubimage(800, 100, 100, 100).getScaledInstance(pieceSize, pieceSize, BufferedImage.SCALE_SMOOTH));
        pieceImages.put("truefalselance", bufferedPieces.getSubimage(900, 0, 100, 100).getScaledInstance(pieceSize, pieceSize, BufferedImage.SCALE_SMOOTH));
        pieceImages.put("falsefalselance", bufferedPieces.getSubimage(900, 100, 100, 100).getScaledInstance(pieceSize, pieceSize, BufferedImage.SCALE_SMOOTH));
        pieceImages.put("truetruelance", bufferedPieces.getSubimage(1000, 0, 100, 100).getScaledInstance(pieceSize, pieceSize, BufferedImage.SCALE_SMOOTH));
        pieceImages.put("falsetruelance", bufferedPieces.getSubimage(1000, 100, 100, 100).getScaledInstance(pieceSize, pieceSize, BufferedImage.SCALE_SMOOTH));
        pieceImages.put("truefalsepawn", bufferedPieces.getSubimage(1100, 0, 100, 100).getScaledInstance(pieceSize, pieceSize, BufferedImage.SCALE_SMOOTH));
        pieceImages.put("falsefalsepawn", bufferedPieces.getSubimage(1100, 100, 100, 100).getScaledInstance(pieceSize, pieceSize, BufferedImage.SCALE_SMOOTH));
        pieceImages.put("truetruepawn", bufferedPieces.getSubimage(1200, 0, 100, 100).getScaledInstance(pieceSize, pieceSize, BufferedImage.SCALE_SMOOTH));
        pieceImages.put("falsetruepawn", bufferedPieces.getSubimage(1200, 100, 100, 100).getScaledInstance(pieceSize, pieceSize, BufferedImage.SCALE_SMOOTH));
        pieceImages.put("truefalseking", bufferedPieces.getSubimage(1300, 0, 100, 100).getScaledInstance(pieceSize, pieceSize, BufferedImage.SCALE_SMOOTH));
        pieceImages.put("falsefalseking", bufferedPieces.getSubimage(1300, 100, 100, 100).getScaledInstance(pieceSize, pieceSize, BufferedImage.SCALE_SMOOTH));

        // Drawing the game panel and starting position
        gamePanel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));

                g.fillRect(0, 0, (int) (pieceSize * 9 * 16/9), (int) (pieceSize * 9));
                g.drawImage(boardImage, 0, 0, null);

                for (int i = 0; i < pieces.length; i++) {
                    g.drawImage(pieceImages.get(String.valueOf(pieces[i].isSente) + String.valueOf(pieces[i].isPromoted) + pieces[i].type), pieces[i].x, pieces[i].y, this);
                }
            }
        };

        setTitle("Shogi");
        setDefaultCloseOperation(3);
        add(gamePanel);
        addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (selectedPiece != null) {
                    // Drag piece
                    selectedPiece.x = e.getX() - pieceSize/2;
                    selectedPiece.y = e.getY() - (int) pieceSize*3/4;
                } else if (getPieceFromLocation(e.getX(), e.getY()) != null) {
                    // Pick up piece
                    selectedPiece = getPieceFromLocation(e.getX(), e.getY());
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
                    selectedPiece.move((int) e.getX()/pieceSize, (int) e.getY()/pieceSize);
                    selectedPiece = null;
                    repaint();
                    playSound(moveURL);
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
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
            if (pieces[i].xPos == (int) (x/pieceSize) && pieces[i].yPos == (int) (y/pieceSize)) {
                return pieces[i];
            }
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        new App(1, 64);
    }
}
