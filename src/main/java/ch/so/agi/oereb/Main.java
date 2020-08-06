package ch.so.agi.oereb;

import java.awt.EventQueue;
import javax.swing.JFrame;

public class Main extends JFrame {
    
    public Main() {
        initUI();
    }
    
    private void initUI() {     
        setTitle("Simple example");
        setSize(400, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {

            Main app = new Main();
            app.setVisible(true);
        });
    }
}
