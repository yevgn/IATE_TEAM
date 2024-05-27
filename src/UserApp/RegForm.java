package UserApp;

import mainClasses.Phone;
import mainClasses.User;

import database.Database;

import exceptions.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    private User user;
    LoginForm loginForm;

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
        this.loginForm = loginForm;

        btnOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = tfName.getText();
                String surname = tfSurname.getText();
                String patronymic = tfPatronymic.getText();
                String age = tfAge.getText();
                String email = tfEmail.getText();
                String phone = tfPhone.getText();
                String password = String.valueOf(pfPassword.getPassword());
                String password1 = String.valueOf(pfPassword1.getPassword());

                try {
                    if (name.isEmpty() || surname.isEmpty() || age.isEmpty() || email.isEmpty() || password.isEmpty() || password1.isEmpty() ||
                            phone.isEmpty()) {
                        showMessage("Ошибка!", "Некоторые поля остались незаполненными!", JOptionPane.ERROR_MESSAGE);
                    } else if (Integer.parseInt(age) <= 0) {
                        throw new IncorrectAgeException();
                    } else if (!password.equals(password1)) {
                        showMessage("Ошибка!", "Пароли не совпадают!", JOptionPane.ERROR_MESSAGE);
                    } else if(!Phone.isCorrectNumber(phone)){ // проверка номера телефона
                        throw new IncorrectPhoneNumberException();
                    }  else{
                        if(isUserExistInDatabase(email, phone)){ // если в базе уже есть такой человек
                            showMessage("Ошибка!", "Пользователь с таким номером телефона или адресом " +
                                    "электронной почты уже зарегистрирован", JOptionPane.ERROR_MESSAGE);
                        } else {
                            user = new User();
                            user.setName(name);
                            user.setSurname(surname);
                            user.setPatronymic(patronymic.isEmpty() ? null : patronymic);
                            user.setAge(Integer.parseInt(age));
                            user.setEmail(email);
                            user.setPhone(phone);
                            user.setPassword(password);
                            addUserToDatabase(user);
                            showMessage("", "Вы были успешно зарегистированы!", JOptionPane.INFORMATION_MESSAGE);
                            dispose();
                            setLoginFormVisible();
                        }
                    }
                } catch (IncorrectAgeException | NumberFormatException ex ){
                    showMessage("Ошибка!", "Неправильно указан возраст!", JOptionPane.ERROR_MESSAGE);
                } catch (IncorrectPhoneNumberException ex){
                    showMessage("Ошибка!", "Неправильно указан номер телефона!", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        setVisible(true);
    }

    private void showMessage(String title, String message, int type){
        JOptionPane.showMessageDialog(this, message, title, type);
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
            showMessage("Ошибка!", "Ошибка соединения с базой данных. Попробуйте позже.", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void setLoginFormVisible(){
        loginForm.setVisible(true);
    }


    public User getUser(){
        return user;
    }

    private boolean isUserExistInDatabase(String email, String phone){
        try(Connection connection = DriverManager.getConnection(Database.URL, Database.USERNAME, Database.PASSWORD);
            Statement statement = connection.createStatement()){

            final String query = "select id from users where email = '" + email + "' or phone = '" + "+7".concat(phone) + "';";
            ResultSet res = statement.executeQuery(query);
            if(res.next())
                return true;
        } catch (SQLException ex){
            showMessage("Ошибка!", "Ошибка соединения с базой данных. Попробуйте позже.", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
        return false;
    }
}
