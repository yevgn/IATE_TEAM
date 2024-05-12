import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BookInfo extends JDialog {

    private JPanel panel1;
    private JTextArea taDescription;
    private JButton btnPlanSession;
    private JLabel lbName;
    private JLabel lbAuthor;
    private JLabel lbYear;
    private JLabel lbGenre;
    private JLabel lbRating;

    public BookInfo(JFrame parent, Book book, int userId){
        super(parent);
        setContentPane(panel1);
        setLocation(500, 250);
        setModal(true);
        setSize(new Dimension(900, 600));
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);


        lbName.setText(book.getName());
        lbAuthor.setText("Автор : " + book.getAuthor());
        lbYear.setText("Год издания : " + String.valueOf(book.getYearOfPublishing()));
        lbRating.setText("Оценка (ЛитРес) : " + String.valueOf(book.getRating()));
        String[] genres = book.getGenre().split("\n");
        String genre = "Жанры : ";
        for(int i = 0; i < genres.length; i++){
            if(i == genres.length - 1)
                genre = genre.concat(genres[i]);
            else
                genre = genre.concat(genres[i] + ", ");
        }
        lbGenre.setText(genre);

        final int numOfLettersInOneString = 90;
        StringBuilder description = new StringBuilder(book.getDescription());
        divideByLines(description, numOfLettersInOneString);
        taDescription.setText(description.toString());



        btnPlanSession.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                    btnPlanSession.setOpaque(true);
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    btnPlanSession.setOpaque(false);
                }
        });


        btnPlanSession.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                createPlanSessionForm(userId);
            }
        });


        setVisible(true);
    }

    private void divideByLines(StringBuilder line, final int lettersNumInOneLine){
        int i = 0;
        int pos = 0;
        char ch;
        while(pos !=  line.length() - 1){
            if( line.charAt(pos) == '\n'){
                i = 0;
                pos++;
                continue;
            }

            if( (i + 1) == 90) {
                ch =  line.charAt(pos);
                if(ch == ' ') {
                    line.insert(pos + 1, "\n");
                    i = 0;
                    pos++;
                    continue;
                }  else{
                    int j = pos;
                    while( line.charAt(j) != ' '){
                        if(pos ==  line.length() - 1 )
                            break;
                        pos++;
                        j++;
                    }
                    line.insert(j + 1, "\n");
                    pos++;
                    i = 0;
                    continue;
                }
            }
            pos++;
            i++;
        }
    }

    public String getBookName(){
        return lbName.getText();
    }

    private PlanSessionForm createPlanSessionForm(int userId){
        return new PlanSessionForm(null, this, userId);
    }

    public String getBookAuthor(){
        return lbAuthor.getText();
    }
}
