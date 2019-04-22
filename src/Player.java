import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class Player {

    Mat          frame;
    VideoCapture camera;
    private double  scaling = 1;
    private int     smooth  = 1;
    private boolean isColorPencilEffect;
    private double  gsmooth = 1;
    double length;
    private int     frameIndx = 1;
    public  boolean isCustomScrolled;

    public Player(String path, String name) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        frame = new Mat();
        camera = new VideoCapture(path + name);
        length = camera.get(Videoio.CAP_PROP_FRAME_COUNT);

    }

    public void scale() {
        Size size = new Size((double) frame.width() * scaling, (double) frame.height() * scaling);
        Imgproc.resize(frame, frame, size);
    }

    /**
     * Сглаживание путем усреднения
     */
    private void medianSmooth() {
        Imgproc.medianBlur(frame, frame, smooth);
    }

    private void blurSmooth() {
        Imgproc.blur(frame, frame, new Size(Math.round(gsmooth), Math.round(gsmooth)));
    }

    public BufferedImage getMat2BufferedImage() {
        frameIndx++;
        scale();
        medianSmooth();
        blurSmooth();
        Mat m = frame;

        //Method converts a Mat to a Buffered Image
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (m.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = m.channels() * m.cols() * m.rows();
        byte[] b = new byte[bufferSize];
        m.get(0, 0, b); // get all the pixels
        BufferedImage image = new BufferedImage(m.cols(), m.rows(), type);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(b, 0, targetPixels, 0, b.length);
        return image;
    }

    public void setScaling(double scaling) {
        this.scaling = scaling/10;
    }

    public void setSmooth(int smooth) {
        if (smooth % 2 != 1)
            smooth = smooth + 1;
        this.smooth = smooth;
    }

    public void changeColorPencilEffect() {
        isColorPencilEffect = !isColorPencilEffect;
    }

    public void setGSmooth(double smooth) {
        if (smooth % 2 != 1)
            smooth = smooth + 1;
        this.gsmooth = smooth;
    }


    public void jumpUp() {
        // if (isCustomScrolled) {
        int id = Videoio.CAP_PROP_POS_FRAMES;
        int neqIndx = frameIndx + 100;
        frameIndx = frameIndx + 100;
        camera.set(id, neqIndx);
        // }
    }

    int getFrameindex() {
        return frameIndx;
    }

    public void jumpDown() {
        if (frameIndx > 100) {
            int id = Videoio.CAP_PROP_POS_FRAMES;
            int neqIndx = frameIndx - 100;
            frameIndx = frameIndx - 100;
            camera.set(id, neqIndx);
        }
    }
}
