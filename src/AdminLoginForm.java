import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class AdminLoginForm extends JDialog {
    private JPanel panel1;
    private JTextField tfLogin;
    private JPasswordField pfPassword;
    private JButton btnOK;

    public AdminLoginForm(JFrame parent){
        super(parent);
        setContentPane(panel1);
        setLocation(500, 250);
        setModal(true);
        setSize(new Dimension(350, 250));
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        btnOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String login = tfLogin.getText();
                String password = String.valueOf(pfPassword.getPassword());
                if(login.isEmpty() || password.isEmpty())
                    showErrorMessage("Остались пустые поля!", "Ошибка!");
                else if(!checkLoginAndPasswordInDatabase(login, password)){
                    showErrorMessage("Неправильные данные!", "Ошибка!");
                } else{
                    dispose();
                    createAdminMainFrame();
                }
            }
        });

        setVisible(true);
    }

    private boolean checkLoginAndPasswordInDatabase(String login, String password){
        try(Connection connection = DriverManager.getConnection(Database.URL, Database.USERNAME, Database.PASSWORD);
            Statement statement = connection.createStatement()){

           String query = "select * from admins where login = '" + login + "' and password = '" + password + "';";
            ResultSet res = statement.executeQuery(query);
            return res.next();

        } catch (SQLException ex){
            showErrorMessage("Ошибка соединения с базой данных. Попробуйте позже", "Ошибка!");
            ex.printStackTrace();
        }
        return false;
    }

    private AdminMainFrame createAdminMainFrame(){
        return new AdminMainFrame(null);
    }

    private void showErrorMessage(String message, String title){
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }


}
