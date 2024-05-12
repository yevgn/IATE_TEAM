import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class addBookForm extends JDialog {
    private JTextField tfName;
    private JTextField tfAuthor;
    private JTextField tfYearOfPublishing;
    private JTextField tfGenre;
    private JTextField tfRating;
    private JTextField tfImagePath;
    private JTextArea taDescription;
    private JButton btnAdd;
    private JPanel panel1;

    public addBookForm(JFrame parent, DefaultTableModel model){
        super(parent);
        setContentPane(panel1);
        setLocation(500, 250);
        setModal(true);
        setSize(new Dimension(500, 600));
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);


        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = tfName.getText();
                String author = tfAuthor.getText();
                String yearOfPublishing = tfYearOfPublishing.getText();

                String[] genres = tfGenre.getText().split(", ");
                String genre = "";
                if(genres.length != 1){
                    for(int i = 0; i < genres.length; i++){
                        if(i == genres.length - 1){
                            genre += genres[i];
                        } else{
                            genre += genres[i] + "\n";
                        }
                    }
                } else
                    genre = genres[0];
                String rating = tfRating.getText();
                String imagePath = tfImagePath.getText();
                String description = taDescription.getText();

                if(name.isEmpty() || author.isEmpty() || genre.isEmpty() || rating.isEmpty() || yearOfPublishing.isEmpty() ||
                    imagePath.isEmpty() || description.isEmpty())
                    showErrorMessage("Поля остались незаполненными!", "Ошибка!");
                else if(!checkIfYearIsCorrect(yearOfPublishing))
                    showErrorMessage("Неправильно введен год издания!", "Ошибка!");
                else if(!checkIfRatingIsCorrect(rating))
                    showErrorMessage("Неправильно введена средняя оценка!", "Ошибка!");
                else if(checkIfBookExistsInDatabase(name))
                    showErrorMessage("Книга с таким названием уже существует!", "Ошибка!");
                else { // добавляем книгу в базу и таблицу
                    addBookToDatabase(name, author,Integer.parseInt(yearOfPublishing), genre, Float.parseFloat(rating), imagePath, description);
                    Object[] newRow = {name, author, yearOfPublishing, rating, tfGenre.getText(), imagePath};
                    model.addRow(newRow);
                    showInformationMessage("Успех!", "");
                    dispose();
                }

            }
        });

        setVisible(true);
    }

    private boolean checkIfYearIsCorrect(String year){
        try{
            Integer.parseInt(year);
            return true;
        } catch (NumberFormatException ex){
           return false;
        }
    }

    private boolean checkIfRatingIsCorrect(String rating){
        try{
           Float.parseFloat(rating);
           return true;
        } catch (NumberFormatException ex){
            return false;
        }
    }

    private void addBookToDatabase(String name, String author, int yearOfPublishing, String genre, float rating, String imagePath, String description){
        try(Connection connection = DriverManager.getConnection(Database.URL, Database.USERNAME, Database.PASSWORD);
            Statement statement =connection.createStatement()){

            String query = "insert into books (name, author, yearofpublishing, genre, rating, imagepath, description) values ('" + name + "', '" + author +
                    "', '" + yearOfPublishing + "', '" + genre + "', '" + rating + "', '" + imagePath + "', '" + description + "');";
            statement.executeUpdate(query);
        } catch (SQLException ex){
            showErrorMessage("Ошибка соединения с базой данных. Попробуйте позже", "Ошибка!");
            ex.printStackTrace();
        }
    }

    private boolean checkIfBookExistsInDatabase(String name){
        try(Connection connection = DriverManager.getConnection(Database.URL, Database.USERNAME, Database.PASSWORD);
            Statement statement =connection.createStatement()){

            String query = "select name from books where name = '" + name + "';";
            ResultSet res = statement.executeQuery(query);
            return res.next();
        } catch (SQLException ex){
            showErrorMessage("Ошибка соединения с базой данных. Попробуйте позже", "Ошибка!");
            ex.printStackTrace();
        }
        return false;
    }

    private void showInformationMessage(String message, String title){
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }
    private void showErrorMessage(String message, String title){
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }

}
