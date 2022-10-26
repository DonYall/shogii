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
    URL moveURL = App.class.getResource("move.wav");
    URL captureURL = App.class.getResource("capture.wav");
    JPanel gamePanel;

    public App(int numPlayers, int pieceSize) throws IOException {
        this.numPlayers = numPlayers;
        this.pieceSize = pieceSize;

        // Pawns
        for (int i = 0; i < 9; i++) {
            pieces[i] = new Piece("pawn", true, 2, i);
        }
        for (int i = 0; i < 9; i++) {
            pieces[9+i] = new Piece("pawn", false, 6, i);
        }
        // Lances
        pieces[18] = new Piece("lance", true, 8, 0);
        pieces[19] = new Piece("lance", true, 0, 0);
        pieces[20] = new Piece("lance", false, 8, 8);
        pieces[21] = new Piece("lance", false, 0, 8);
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
        pieces[38] = new Piece("king", true, 4, 8);
        pieces[39] = new Piece("king", false, 4, 0);

        // Constructing the GUI
        setSize((int) (pieceSize * 9 * 16/9) , (int) (pieceSize * 9));
        setUndecorated(true);

        // Board image
        BufferedImage bufferedBoard = ImageIO.read(getClass().getResource("board.png"));
        Image boardImage = (bufferedBoard.getScaledInstance(pieceSize*9, pieceSize*9, BufferedImage.SCALE_SMOOTH));

        // Piece images
        

        // Drawing the game panel and starting position
        gamePanel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));

                g.fillRect(0, 0, (int) (pieceSize * 9 * 16/9), (int) (pieceSize * 9));
                g.drawImage(boardImage, 0, 0, null);
            }
        };

        setTitle("Shogi");
        add(gamePanel);
        addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                // Drag and drop window movement
                setLocation(e.getXOnScreen() - posX, e.getYOnScreen() - posY);
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

    public static void main(String[] args) throws Exception {
        new App(1, 64);
    }
}
