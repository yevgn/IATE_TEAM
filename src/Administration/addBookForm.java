package Administration;

import database.Database;
import mainClasses.Book;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;

public class addBookForm extends JDialog {
    private JTextField tfName;
    private JTextField tfAuthorName;
    private JTextField tfYearOfPublishing;
    private JTextField tfGenre;
    private JTextField tfRating;
    private JTextArea taDescription;
    private JButton btnAdd;
    private JPanel panel1;
    private JButton btnChooseImage;
    private JTextField tfAuthorSurname;

    private String imagepath = "";

    public addBookForm(JFrame parent, DefaultTableModel model, ArrayList<Book> books){
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
                String authorName = tfAuthorName.getText();
                String authorSurname = tfAuthorSurname.getText();
                String yearOfPublishing = tfYearOfPublishing.getText();
                String rating = tfRating.getText();
                String description = taDescription.getText();

                String[] genres = tfGenre.getText().split(", ");

                if(name.isEmpty() || authorName.isEmpty() ||  authorSurname.isEmpty() || genres.length == 0 ||
                        rating.isEmpty() || yearOfPublishing.isEmpty() ||
                    imagepath.isEmpty() || description.isEmpty())
                    showErrorMessage("Поля остались незаполненными!", "Ошибка!");
                else if(!checkIfYearIsCorrect(yearOfPublishing))
                    showErrorMessage("Неправильно введен год издания!", "Ошибка!");
                else if(!checkIfRatingIsCorrect(rating))
                    showErrorMessage("Неправильно введена средняя оценка!", "Ошибка!");
                else if(checkIfBookExistsInDatabase(name))
                    showErrorMessage("Книга с таким названием уже существует!", "Ошибка!");
                else { // добавляем книгу в базу и таблицу
                    addBookToDatabase(name, authorName, authorSurname, Integer.parseInt(yearOfPublishing), genres,
                            Float.parseFloat(rating), imagepath, description);
                    Object[] newRow = {name, authorName + " " + authorSurname, yearOfPublishing, rating, tfGenre.getText(), imagepath};
                    model.addRow(newRow);
                    books.add(new Book(name, authorName, authorSurname, Integer.parseInt(yearOfPublishing),
                            description, imagepath, Float.parseFloat(rating),
                            genres));
                    showInformationMessage("Книга успешно добавлена!", "Сообщение");
                    dispose();
                }
            }
        });

        btnChooseImage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter(
                        "PNG images", "png");
                chooser.setFileFilter(filter);
                int returnVal = chooser.showOpenDialog(null);
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                    imagepath = chooser.getSelectedFile().getPath();
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

    private void addBookToDatabase(String bookName, String authorName, String authorSurname,
                                   int yearOfPublishing, String[] genres, float rating, String imagePath, String description){
        try(Connection connection = DriverManager.getConnection(Database.URL, Database.USERNAME, Database.PASSWORD);
            Statement statement =connection.createStatement()){

            int authorID = addAuthorToDatabase(authorName, authorSurname);

            final String QUERY = "insert into books (name, author_id, year_publish, rating, imagepath, description) " +
                    "values('" + bookName + "', '" + authorID + "', '" + yearOfPublishing + "', '" + rating + "', '"
                    + imagePath + "', '" + description + "');";
            statement.executeUpdate(QUERY);

            for(String genre : genres) {
                final String QUERY_1 = "insert into book_genre (book_id, genre_id) values ((select id from books where name = '"
                        + bookName + "'), (select id from genres where name = '" + genre + "'));";
                statement.executeUpdate(QUERY_1);
            }
        } catch (SQLException ex){
            showErrorMessage("Ошибка соединения с базой данных. Попробуйте позже", "Ошибка!");
            ex.printStackTrace();
        }
    }

    private Integer addAuthorToDatabase(String authorName, String authorSurname){
        try(Connection connection = DriverManager.getConnection(Database.URL, Database.USERNAME, Database.PASSWORD);
            Statement statement =connection.createStatement()){

            final String QUERY = "insert into authors (name, surname) values ('" + authorName + "', '" + authorSurname + "');";
            statement.executeUpdate(QUERY);

            final String QUERY_1 = "select id from authors where name = '" + authorName + "' and surname = '" + authorSurname + "';" ;
            ResultSet res = statement.executeQuery(  QUERY_1);
            if(res.next())
                return res.getInt("id");

        } catch (SQLException ex){
            showErrorMessage("Ошибка соединения с базой данных. Попробуйте позже", "Ошибка!");
            ex.printStackTrace();
        }
        return null;
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
