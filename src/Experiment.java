import javax.swing.*;

public class Experiment {

    public static void main(String[] args) {
        new Experiment();

    }
    Experiment(){
      JFrame f = new JFrame("hello");
      f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
      JButton jButton = new JButton("helllo");
      jButton.addActionListener(e -> System.out.println("action 1"));
        jButton.addActionListener(e -> System.out.println("action 2"));
        f.add(jButton);
      f.pack();
      f.setVisible(true);
    }


}