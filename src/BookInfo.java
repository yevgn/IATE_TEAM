import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
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
    private JPanel pnlDescr;
    private JPanel pnlCenter;
    private JPanel southPanel;
    private JLabel lbDescr;
    private final String GENRES_LABEL = "Жанры: ";
    private final String NAME_LABEL  = "Название: ";
    private final String AUTHOR_LABEL = "Автор: ";
    private final String RATING_LABEL = "Оценка (Литрес): ";
    private String YEAR_LABEL = "Год издания: ";
    final int numOfLettersInOneString = 120;
    Book book;

    public BookInfo(JFrame parent, Book book, int userId){
        super(parent);
        setTitle("Информация о книге");
        setContentPane(panel1);
        setLocation(500, 250);
        setModal(true);
        setSize(new Dimension(900, 600));
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        pnlDescr.setBorder(new RoundedBorder(10));
        pnlCenter.setBorder(new EmptyBorder(0, 20, 0, 20));
        taDescription.setEditable(false);

        this.book = book;
        lbName.setText(NAME_LABEL + book.getName());
        lbAuthor.setText(AUTHOR_LABEL + book.getAuthorName() + " " + book.getAuthorSurname());
        lbYear.setText(YEAR_LABEL + String.valueOf(book.getYearOfPublishing()));
        lbRating.setText(RATING_LABEL + String.valueOf(book.getRating()));

        String[] genres = book.getGenres();
        String genre = "";
        for(int i = 0; i < genres.length; i++){
            if(i == genres.length - 1)
                genre = genre.concat(genres[i]);
            else
                genre = genre.concat(genres[i] + ", ");
        }

        lbGenre.setText(GENRES_LABEL + genre);

        StringBuilder description = new StringBuilder(book.getDescription());
        divideByLines(description);
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

    private void divideByLines(StringBuilder line){
        int i = 0;
        int pos = 0;
        char ch;
        while(pos !=  line.length() - 1){
            if( line.charAt(pos) == '\n'){
                i = 0;
                pos++;
                continue;
            }

            if( (i + 1) == numOfLettersInOneString) {
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


    private PlanSessionForm createPlanSessionForm(int userId){
        return new PlanSessionForm(null, this, userId);
    }

    public Book getBook(){
        return book;
    }
}
