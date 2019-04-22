import org.opencv.videoio.Videoio;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Application {
    private static final String TITLE = "Video Player openCV";
    private final        Player player;

    private JButton    playButton;
    private JSlider    zoom;
    private JSlider    gaussianSlider;
    private JPanel     rootPanel;
    private JPanel     videoPanel;
    private JToolBar   toolBarZoom;
    private JToolBar   upperToolBar;
    private JLabel     videoLabel;
    private JSlider    smoothSlider;
    private JScrollBar scrollBar1;
    private JSlider    slider1;
    private JButton    bNext;
    private JButton    backButton;
    private JSplitPane zoomButton;

    private boolean  isPlaying = true;
    private MyThread thread;
    private double   scaling   = 1;
    private int      debug     = 1;

    public Application(Player player) {
        this.player = player;
        thread = new MyThread();
        bNext.addActionListener(e ->
        {
            synchronized (thread) {
                boolean temp = isPlaying;
                isPlaying = false;
                player.jumpUp();
                isPlaying = temp;
                if (temp) {
                    thread.notifyAll();
                }
            }
        });
        backButton.addActionListener(e ->
        {
            synchronized (thread) {
                boolean temp = isPlaying;
                isPlaying = false;
                player.jumpDown();
                isPlaying = temp;
                if (temp) {
                    thread.notifyAll();
                }
            }
        });

        playButton.addActionListener(e ->
        {
            synchronized (thread) {
                isPlaying = !isPlaying;
                if (isPlaying) {
                    thread.notifyAll();
                }
            }
        });

        videoLabel.addComponentListener(new ComponentAdapter() {
        });

        zoom.addComponentListener(new ComponentAdapter() {
        });
        zoom.addChangeListener(e -> {player.setScaling(zoom.getValue());
        System.out.println(zoom.getValue());});

        smoothSlider.addChangeListener(e -> player.setSmooth(smoothSlider.getValue()));
        gaussianSlider.addChangeListener(e -> player.setGSmooth(gaussianSlider.getValue()));

    }

    public void run() {
        JFrame rootFrame = new JFrame(TITLE);
        rootFrame.setSize(1500, 1000);
        rootFrame.setContentPane(rootPanel);
        rootFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        rootFrame.setVisible(true);
        rootFrame.setLayout(new BorderLayout());

        zoom.setPaintLabels(true);
        zoom.setMaximum(12);
        zoom.setMinimum(1);
        zoom.setValue(7);
        slider1.setMinimum(0);
        slider1.setMaximum((int) player.length);
        slider1.setValue(0);
        thread.start();
    }

    public void play() {
        double fps = player.camera.get(Videoio.CAP_PROP_FPS);

        System.out.println(debug);
        if (player.camera.read(player.frame)) {
            videoLabel.repaint();
            BufferedImage imgBuf = player.getMat2BufferedImage();
            ImageIcon image = new ImageIcon(imgBuf);
            videoLabel.setIcon(image);
            slider1.setValue(player.getFrameindex());
            try {
                long speed = 1;
                Thread.currentThread().sleep((long) fps * speed);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }

    class MyThread extends Thread {

        public void run() {
            while (true) {

                synchronized (this) {
                    while (!isPlaying) {
                        try {
                            wait();
                        } catch (Exception e) {
                        }
                    }

                }
                play();
            }
        }
    }
}
