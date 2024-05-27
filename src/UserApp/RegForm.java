package UserApp;

import mainClasses.User;
import database.Database;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;

public class RegForm extends JDialog{
    private JPanel northPanel, centerPanel, southPanel;
    private JPanel panel1;
    private JTextField tfAge;
    private JTextField tfEmail;
    private JTextField tfPhone;
    private JPasswordField pfPassword1;
    private JButton btnOK;
    private JPasswordField pfPassword;
    private JTextField tfName;
    private JTextField tfSurname;
    private JTextField tfPatronymic;

    public RegForm(JFrame parent, LoginForm loginForm)  {
        super(parent);
        setContentPane(panel1);
        setLocation(500, 250);
        setModal(true);
        setSize(440, 480);
        setResizable(false);

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                e.getWindow().dispose();
                loginForm.setVisible(true);
            }
        });

        btnOK.addActionListener(e -> {
            String name = tfName.getText();
            String surname = tfSurname.getText();
            String patronymic = tfPatronymic.getText();
            String age = tfAge.getText();
            String email = tfEmail.getText();
            String phone = tfPhone.getText();
            String password = String.valueOf(pfPassword.getPassword());
            String password1 = String.valueOf(pfPassword1.getPassword());

            if (dataIsCorrect(name, surname, age, email, phone, password, password1)) {
                if (!isUserExistInDatabase(email, phone)) {
                    User user = new User();
                    user.setName(name);
                    user.setSurname(surname);
                    user.setPatronymic(patronymic.isEmpty() ? "" : patronymic);
                    user.setAge(Integer.parseInt(age));
                    user.setEmail(email);
                    user.setPhone(phone);
                    user.setPassword(password);

                    addUserToDatabase(user);

                    showMessage("", "Вы были успешно зарегистированы!", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                    loginForm.setVisible(true);
                } else {
                    showMessage("Ошибка!", "Пользователь с таким номером телефона или адресом " +
                            "электронной почты уже зарегистрирован", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        setVisible(true);
    }


    private void showMessage(String title, String message, int type){
        JOptionPane.showMessageDialog(this, message, title, type);
    }

    private boolean dataIsCorrect(String name, String surname, String age, String email, String phone,
                               String password, String password1){

        try {
            if (name.isEmpty() || surname.isEmpty() || age.isEmpty() || email.isEmpty() || password.isEmpty()
                    || password1.isEmpty() || phone.isEmpty()) {
                showMessage("Ошибка!", "Некоторые поля остались незаполненными!", JOptionPane.ERROR_MESSAGE);
                return false;
            } else if (Integer.parseInt(age) <= 0) {
                showMessage("Ошибка!", "Неправильно указан возраст!", JOptionPane.ERROR_MESSAGE);
            } else if (!password.equals(password1)) {
                showMessage("Ошибка!", "Пароли не совпадают!", JOptionPane.ERROR_MESSAGE);
                return false;
            } else if (!isCorrectPhoneNumber(phone)) { // проверка номера телефона
                showMessage("Ошибка!", "Неправильно указан номер телефона!", JOptionPane.ERROR_MESSAGE);
                return false;
            } else return true;
        } catch (NumberFormatException ex){
            ex.printStackTrace();
            showMessage("Ошибка!", "Неправильно указан возраст!", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return false;
    }

    private boolean isCorrectPhoneNumber(String phone) {
        int length = phone.length();
        if (length != 10)
            return false;

        char ch;
        for (int i = 0; i < length; i++) {
            ch = phone.charAt(i);
            if (ch < 48 || ch > 57)
                return false;
        }
        return true;
    }

    private void addUserToDatabase(User user){
        try(Connection connection = DriverManager.getConnection(Database.URL, Database.USERNAME, Database.PASSWORD);
            Statement statement = connection.createStatement()){

           final String query_1 = "insert into users (name, surname, patronymic, phone, email, password, age) " +
                   "values ('" + user.getName() + "', '" + user.getSurname() + "', '" + user.getPatronymic() + "', '" +
                   user.getPhone() + "', '" + user.getEmail() + "', '" + user.getPassword() + "', '" +
                   user.getAge() + "');";

            statement.executeUpdate(query_1);

            final String query_2 = "select id from users where phone = '" + user.getPhone() + "';" ;
            ResultSet res = statement.executeQuery(query_2);
            if(res.next())
                user.setId(res.getInt("id"));

        } catch (SQLException ex){
            showMessage("Ошибка!", "Ошибка соединения с базой данных. Попробуйте позже.",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }


    private boolean isUserExistInDatabase(String email, String phone){
        try(Connection connection = DriverManager.getConnection(Database.URL, Database.USERNAME, Database.PASSWORD);
            Statement statement = connection.createStatement()){

            final String query = "select id from users where email = '" + email +
                    "' or phone = '" + "+7".concat(phone) + "';";
            ResultSet res = statement.executeQuery(query);
            if(res.next())
                return true;
        } catch (SQLException ex){
            showMessage("Ошибка!", "Ошибка соединения с базой данных. Попробуйте позже.",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
        return false;
    }
}
