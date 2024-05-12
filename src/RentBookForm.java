import javax.swing.*;
import javax.xml.crypto.Data;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

public class RentBookForm extends JDialog {
    private JPanel panel1;
    private JTextArea taAgreementTerms;
    private JCheckBox checkBoxAgree;
    private JScrollPane spCenter;
    private JButton btnOK;
    private JComboBox cbDayToRentBook;
    private final int maximumDaysAheadToRentBook = 3;
    final int lettersNumInOneLine = 68;
    private final String address = "Обнинск, пр. Карла Маркса, 73";

    public RentBookForm(JFrame parent,BookInfo bookInfo, String bookName, String bookAuthor, int userId){
        super(parent);
        setContentPane(panel1);
        setLocation(500, 250);
        setModal(true);
        setSize(new Dimension(650, 500));
        setResizable(false);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                e.getWindow().dispose();
                bookInfo.setVisible(true);
            }
        });

        StringBuilder topLine = new StringBuilder("Ознакомьтесь с условиями аренды книг в нашей библиотеке и нажмите \"ОК\"," +
                " если Вы действительно согласны с приведенными ниже условиями и готовы взять книгу в аренду.");

        StringBuilder agreementTerms = new StringBuilder("Нажимая на кнопку \"Я согласен с условиями\", вы берете на себя ответственность" +
                " бережно использовать книгу в течение аренды, а также ВЕРНУТЬ ЕЕ В ТЕЧЕНИЕ 14 ДНЕЙ. В случае неявки по истечении срока аренды вы автоматически" +
                " будете занесены в \"черный список\" нашей платформы. В случае если арендованная Вами книга по возвращении окажется существенно потрепанной," +
                " рванной или же отмеченной сколько угодно малыми пятнами, Вы обязаны будете компенсировать ущерб в материальной форме администратору библиотеки. " +
                "\n\nПамятка : выбранную Вами книгу : \"" + bookAuthor + ". " + bookName + "\" " + "Вам следует забрать в нашей библиотеке по адресу \"" + address +
                "\" в течение 3-х дней. " +
                "В противном случае книга выдана не будет");

        divideByLines(topLine, lettersNumInOneLine);
        divideByLines(agreementTerms, lettersNumInOneLine);

        taAgreementTerms.setText(topLine + "\n\n" + agreementTerms);
        Font font = taAgreementTerms.getFont();
        taAgreementTerms.setFont(new Font(font.getFontName(), font.getStyle(), 15));


        btnOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!checkBoxAgree.isSelected())
                    showErrorMessage("Вы не согласны с условиями!", "Ошибка!");
                else {
                    try(Connection connection = DriverManager.getConnection(Database.URL, Database.USERNAME, Database.PASSWORD);
                            Statement statement = connection.createStatement()){

                        String query = "insert into rents (book, author, userid, rentdate) values ('" + bookName + "', '" + bookAuthor + "', '" +
                                userId + "', '" + createCurrentDate() + "');";
                        statement.executeUpdate(query);
                        showInformationMessage("Успех!", "");
                        dispose();
                        bookInfo.dispose();
                    } catch (SQLException ex){
                        showErrorMessage("Ошибка соединения с базой данных. Попробуйте позжею.", "Ошибка");
                    }
                }
            }
        });

        setVisible(true);
    }

    private String createCurrentDate(){
        LocalDate date = LocalDate.now();
        String day = String.valueOf( date.getDayOfMonth());
        String month = String.valueOf(date.getMonthValue());
        if(day.length() == 1)
            day = "0" + day;
        if(month.length() == 1)
            month = "0" + month;
        return day + "." + month;
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

                if( (i + 1) == lettersNumInOneLine) {
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

    private void showErrorMessage(String message, String title){
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }

    private void showInformationMessage(String message, String title){
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

}
