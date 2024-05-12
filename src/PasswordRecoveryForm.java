import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;

public class PasswordRecoveryForm extends JDialog {
    private JPanel panel1;
    private JTextField tfPassportId;
    private JTextField tfIssuedBy;
    private JTextField tfDepartmentNum;
    private JTextField tfDateOfIssue;
    private JPasswordField pfPassword;
    private JPasswordField pfPassword1;
    private JButton btnOK;
    private JTextField tfEmail;
    private String oldPassword;

    public PasswordRecoveryForm(JFrame parent, LoginForm loginForm){
        super(parent);
        setContentPane(panel1);
        setLocation(500, 250);
        setModal(true);
        setSize(new Dimension(500, 600));
        setResizable(false);
      setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                e.getWindow().dispose();
               loginForm.setVisible(true);
            }
        });

        btnOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String passportId = tfPassportId.getText();
                String issuedBy = tfIssuedBy.getText();
                String departmentNum = tfDepartmentNum.getText();
                String dateOfIssue = tfDateOfIssue.getText();
                String newPassword = String.valueOf(pfPassword.getPassword());
                String newPassword1 = String.valueOf(pfPassword1.getPassword());

                if(passportId.isEmpty() || issuedBy.isEmpty() || departmentNum.isEmpty() || dateOfIssue.isEmpty() ||
                        newPassword.isEmpty() || newPassword1.isEmpty()){
                    showErrorMessage("Ошибка!", "Некоторые поля остались незаполненными!");
                } else if(!isPassportInfoCorrect(passportId, issuedBy, departmentNum, dateOfIssue)){
                    showErrorMessage("Ошибка!", "Неправильные паспортные данные!");
                } else{
                    if(!newPassword.equals(newPassword1))
                        showErrorMessage("Ошибка!", "Пароли не совпадают!");
                    else{
                        if(oldPassword.equals(newPassword))
                            showErrorMessage("Ошибка!", "Вы ввели старый пароль. Придумайте новый");
                        else{
                            try(Connection connection = DriverManager.getConnection(Database.URL, Database.USERNAME, Database.PASSWORD);
                                    Statement statement = connection.createStatement()){

                                String query = "update users set password = '" + newPassword + "' where email = '" + tfEmail.getText() + "';";
                                statement.executeUpdate(query);
                                showInformationMessage("", "Пароль был изменен");
                                dispose();
                                loginForm.setVisible(true);
                            }catch (SQLException ex){
                                showErrorMessage("Ошибка!", "Ошибка соединения с базой данных. Попробуйте позже.");
                                ex.printStackTrace();
                            }
                        }
                    }
                }
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

    private boolean isPassportInfoCorrect(String id, String issuedBy, String departmentNum, String dateOfIssue) {
        try (Connection connection = DriverManager.getConnection(Database.URL, Database.USERNAME, Database.PASSWORD);
             Statement statement = connection.createStatement()) {

            String query = "select password, passportid, issuedby, departmentnumber, dateofissue from users where email = '" + tfEmail.getText() + "';";
            ResultSet res = statement.executeQuery(query);
            if (!res.next()) {
                showErrorMessage("Ошибка!", "Пользователя с таким адресом электронной почты не существует!");
            } else {
                oldPassword = res.getString("password");
                if (id.equals(res.getString("passportid")) && issuedBy.equals(res.getString("issuedby"))
                        && departmentNum.equals(res.getString("departmentnumber")) && dateOfIssue.equals(res.getString("dateofissue")))
                    return true;
                else return false;
            }
        } catch (SQLException ex) {
            showErrorMessage("Ошибка!", "Ошибка соединения с базой данных. Попробуйте позже.");
            ex.printStackTrace();
        }
        return false;
    }
}
