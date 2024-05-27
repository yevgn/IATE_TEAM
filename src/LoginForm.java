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
    private User user;

    public LoginForm(JFrame parent) {
        super(parent);
        setContentPane(panel1);
        setLocation(500, 250);
        setModal(true);
        setSize(new Dimension(900, 600));
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JButton[] buttons = {btnSignUp};
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
                        showMessage("Ошибка", "Неправильные данные", JOptionPane.ERROR_MESSAGE);
                    } else{ // такой человек зарегистрирован
                        // должны проверить, если ли пользователь в черном списке
                        if(res.getBoolean("is_blocked")){ // в черном списке
                            showMessage("Внимание!", "Вы в черном списке библиотеки!",
                                    JOptionPane.INFORMATION_MESSAGE);

                        } else { // не в черном списке
                                int id = res.getInt("id");
                                String name = res.getString("name");
                                String surname = res.getString("surname");
                                String patronymic = res.getString("patronymic");
                                patronymic = patronymic.equals("null") ? null : patronymic;
                                String phone = res.getString("phone");
                                int age = res.getInt("age");
                                user = new User(id, name, surname, patronymic, phone, email, password, age);
                                dispose();
                                createMainFrame();
                            // Здесь идет запуск нового окна, старое закрывается
                        }
                    }

                } catch (SQLException ex){
                    ex.printStackTrace();
                    showMessage("Ошибка", "Ошибка соединения с базой данных. Попробуйте позже.",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        btnSignUp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setFrameInvisible();
                createRegistrationForm();
            }
        });

        setVisible(true);
    }


    private void showMessage(String title, String message, int type){
        JOptionPane.showMessageDialog(this, message, title, type);
    }

    private MainFrame createMainFrame(){
        return new MainFrame(null, user);
    }

    private void setFrameInvisible(){
        setVisible(false);
    }

    private RegForm createRegistrationForm(){
        return new RegForm(null, this);
    }

}

