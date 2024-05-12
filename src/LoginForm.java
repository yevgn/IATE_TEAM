import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class LoginForm extends JDialog{
    private JPanel panel1;
    private JTextField tfEmail;
    private JPasswordField pfPassword;
    private JButton btnLogIn;
    private JButton btnSignUp;
    private JButton btnForgotPassword;
    private User user;

    public LoginForm(JFrame parent) {
        super(parent);
        setContentPane(panel1);
        setLocation(500, 250);
        setModal(true);
        setSize(new Dimension(900, 600));
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JButton[] buttons = {btnSignUp, btnForgotPassword};
        for(JButton button : buttons){
            Font font = button.getFont();

            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    String fontName = font.getFontName();
                    int fontSize = font.getSize();
                    button.setFont(new Font(fontName, Font.ITALIC, fontSize));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                   button.setFont(font);
                }
            });
        }

        btnLogIn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try(Connection connection = DriverManager.getConnection(Database.URL, Database.USERNAME, Database.PASSWORD);
                    Statement statement = connection.createStatement()){
                    String email = tfEmail.getText();
                    String password = String.valueOf(pfPassword.getPassword());
                    String query = "select * from users where email = '" + email + "' and password = '" + password + "';";
                    ResultSet res = statement.executeQuery(query);

                    if(!res.next()){ // неправильные данные или не зарегистрирован
                        showErrorMessage("Ошибка", "Неправильные данные");
                    } else{ // такой человек зарегистрирован
                        // должны проверить, если ли пользователь в черном списке
                        if(res.getInt("isblocked") == 1){ // в черном списке
                            showInformationMessage("Внимание!", "Вы в черном списке библиотеки!");

                        } else { // не в черном списке
                            res = statement.executeQuery(query);
                           if(res.next()) {
                                int id = res.getInt("userid");
                                String name = res.getString("name");
                                String surname = res.getString("surname");
                                String patronymic = res.getString("patronymic");
                                String phone = res.getString("phone");
                                int age = res.getInt("age");
                                user = new User(id, name, surname, patronymic, phone, email, password, age, "", "", "", "");
                                dispose();
                                createMainFrame();
                            }
                            // Здесь идет запуск нового окна, старое закрывается
                        }
                    }

                } catch (SQLException ex){
                    ex.printStackTrace();
                    showErrorMessage("Ошибка", "Ошибка соединения с базой данных. Попробуйте позже.");
                }

            }
        });


        btnSignUp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                createRegistrationForm();
            }
        });

        btnForgotPassword.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setInvisible();
                createPasswordRecoveryForm();
            }
        });

        setVisible(true);
    }

    private void showErrorMessage(String title, String message){
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }

    private void showInformationMessage(String title, String message){
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    private MainFrame createMainFrame(){
        return new MainFrame(null, user);
    }

   //public String getEmail(){
    //    return tfEmail.getText();
   // }

    private void setInvisible(){
        setVisible(false);
    }

    private PasswordRecoveryForm createPasswordRecoveryForm(){
        return new PasswordRecoveryForm(null, this);
    }

    private RegForm createRegistrationForm(){
        return new RegForm(null);
    }

}

