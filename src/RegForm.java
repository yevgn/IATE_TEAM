import javax.swing.*;
import javax.xml.crypto.Data;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;

public class RegForm extends JDialog{
    private JPanel panel1;
    private JPanel northPanel;
    private JPanel centerPanel;
    private JPanel southPanel;
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

    public RegForm(JFrame parent)   {
        super(parent);
        setContentPane(panel1);
        setLocation(500, 250);
        setModal(true);
        setSize(440, 550);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        user = new User();

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
                        showErrorMessage("Ошибка!", "Некоторые поля остались незаполненными!");
                    } else if (Integer.parseInt(age) <= 0) {
                        throw new IncorrectAgeException();
                    } else if (!password.equals(password1)) {
                        showErrorMessage("Ошибка!", "Пароли не совпадают!");
                    } else if(!Phone.isCorrectNumber(phone)){ // проверка номера телефона
                        throw new IncorrectPhoneNumberException();
                    } else if (Integer.parseInt(age) < 16){
                        showErrorMessage("Ошибка!", "Ваш возраст меньше 16 лет!");
                    }  else{
                        if(isUserExistInDatabase(email, phone)){ // если в базе уже есть такой человек
                            showErrorMessage("Ошибка!", "Пользователь с таким номером телефона или адресом " +
                                    "электронной почты уже зарегистрирован");
                        } else {
                            user.setName(name);
                            user.setSurname(surname);
                            user.setPatronymic(patronymic);
                            user.setPhone("+7".concat(phone));
                            user.setEmail(email);
                            user.setAge(Integer.parseInt(age));
                            user.setPassword(password);
                            setInvisible();
                            createSecRegForm();
                        }
                    }
                } catch (IncorrectAgeException | NumberFormatException ex ){
                    showErrorMessage("Ошибка!", "Неправильно указан возраст!");
                } catch (IncorrectPhoneNumberException ex){
                    showErrorMessage("Ошибка!", "Неправильно указан номер телефона!");
                }
            }
        });

        setVisible(true);
    }

    private void showErrorMessage(String title, String message){
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }

    private RegFormPassportInfo createSecRegForm(){
        return new RegFormPassportInfo(null, this);
    }

    private void setInvisible(){ setVisible(false);}

    public User getUser(){
        return user;
    }

    private boolean isUserExistInDatabase(String email, String phone){
        try(Connection connection = DriverManager.getConnection(Database.URL, Database.USERNAME, Database.PASSWORD);
            Statement statement = connection.createStatement()){

            String query = "select userid from users where email = '" + email + "' or phone = '" + "+7".concat(phone) + "';";
            ResultSet res = statement.executeQuery(query);
            if(res.next())
                return true;
        } catch (SQLException ex){
            showErrorMessage("Ошибка!", "Ошибка соединения с базой данных. Попробуйте позже.");
            ex.printStackTrace();
        }
        return false;
    }
}
