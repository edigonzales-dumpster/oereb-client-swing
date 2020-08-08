package ch.so.agi.oereb;

import java.awt.Container;
import java.awt.EventQueue;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;

public class Main extends JFrame {
    
    public Main() {
        initUI();
    }
    
    private void initUI() {     
        JButton quitButton = new JButton("Quit");

        quitButton.addActionListener((event) -> System.exit(0));

        createLayout(quitButton);

        setTitle("Quit button");
        setSize(300, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

    }
    
    private void createLayout(JComponent... arg) {

        Container pane = getContentPane();
        GroupLayout gl = new GroupLayout(pane);
        pane.setLayout(gl);

        gl.setAutoCreateContainerGaps(true);

        gl.setHorizontalGroup(gl.createSequentialGroup()
                .addComponent(arg[0])
        );

        gl.setVerticalGroup(gl.createSequentialGroup()
                .addComponent(arg[0])
        );
    }
    

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {

            Main app = new Main();
            app.setVisible(true);
        });
    }
}
