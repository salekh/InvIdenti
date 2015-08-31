package preprocessing;

import org.carrot2.core.Document;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunlei on 15/8/20.
 */
public class patParser {

    private JLabel Abstract;
    private JLabel Claims;
    private JTextArea AbstractText;
    private JTextArea ClaimsText;
    private JTextArea DescriptionText;
    private JLabel PatentNoLabel;
    private JTextField PatentNumber;
    private JButton Fetch;
    private JButton preprocessingButton;
    private JPanel patParser;


    public patParser() {

        Fetch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if(e.getSource()==Fetch)
                {
                    USPTOSearch s=new USPTOSearch(PatentNumber.getText());
                    AbstractText.setText(s.getAbs());
                    ClaimsText.setText(s.getClaims());
                    DescriptionText.setText(s.getDescription());
                }

            }
        });
        preprocessingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<Document> docs=new ArrayList<Document>();
                docs.add(new Document(AbstractText.getText()));
                //System.out.println(new preProcessing(docs).removeStopWords());
                AbstractText.setText(new preProcessing(docs).removeStopWords());
                docs.clear();
                docs.add(new Document(ClaimsText.getText()));
                //System.out.println(new preProcessing(docs).removeStopWords());
                ClaimsText.setText(new preProcessing(docs).removeStopWords());
                docs.clear();
                docs.add(new Document(DescriptionText.getText()));
                //System.out.println(new preProcessing(docs).removeStopWords());
                DescriptionText.setText(new preProcessing(docs).removeStopWords());
                docs.clear();

            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("patParser");
        frame.setResizable(false);
        frame.setContentPane(new patParser().patParser);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

}
