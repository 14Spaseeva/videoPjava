import javax.swing.*;

public class main {
    public static final String PATH = "C:\\Users\\ASpaseeva\\Downloads\\";
    public static final String NAME = "king.mp4";

    public static void main(String[] args) {

        Player player = new Player(PATH, NAME);
        Application app = new Application(player);
app.run();
      //  app.setVisible(true);
    }
}
