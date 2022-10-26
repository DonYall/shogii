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
    URL moveURL = App.class.getResource("move.wav");
    URL captureURL = App.class.getResource("capture.wav");
    JPanel gamePanel;

    public App(int numPlayers, int pieceSize) throws IOException {
        this.numPlayers = numPlayers;
        this.pieceSize = pieceSize;

        // Constructing the GUI
        setSize((int) (pieceSize * 9 * 16/9) , (int) (pieceSize * 9));
        setUndecorated(true);

        // Board image
        BufferedImage bufferedBoard = ImageIO.read(getClass().getResource("board.png"));
        Image boardImage = (bufferedBoard.getScaledInstance(pieceSize*9, pieceSize*9, BufferedImage.SCALE_SMOOTH));

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
