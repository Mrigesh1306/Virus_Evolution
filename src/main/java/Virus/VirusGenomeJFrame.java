package Virus;

import javax.swing.*;
import java.io.IOException;
import java.io.OutputStream;

public class VirusGenomeJFrame extends OutputStream {
    private JTextArea textArea;
    private JPanel panel1;

    private final StringBuilder sb = new StringBuilder();
    private String title;

    public VirusGenomeJFrame(final JTextArea textArea, String title) {
        this.textArea = textArea;
        this.title = title;
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() {
    }

    @Override
    public void write(int b) throws IOException {

        if (b == '\r')
            return;

        if (b == '\n') {
            final String text = sb.toString() + "\n";
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    textArea.append(text);
                }
            });
            sb.setLength(0);
            return;
        }

        sb.append((char) b);
    }
}
