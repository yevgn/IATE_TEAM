import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class RegFormPassportInfo extends JDialog {
    private JPanel panel1;
    private JButton btnInfo;
    private JTextField tfPassportId;
    private JTextField tfIssuedBy;
    private JTextField tfDepatrmentNum;
    private JTextField tfDateOfIssue;
    private JButton btnOK;
    private JButton btnCancel;
    private User user;

    public RegFormPassportInfo(JFrame parent, RegForm regForm) {
        super(parent);
        setContentPane(panel1);
        setLocation(500, 250);
        //setLocation(500, 250);
        setModal(true);
        setSize(400, 500);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                regForm.dispose();
                e.getWindow().dispose();
            }
        });

        user = regForm.getUser();

        btnInfo.addMouseListener(new MouseAdapter() {
            Font font = btnInfo.getFont();
            @Override
            public void mouseEntered(MouseEvent e) {
                String fontName = font.getFontName();
                int fontSize = font.getSize();
                btnInfo.setFont(new Font(fontName, Font.ITALIC, fontSize));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnInfo.setFont(font);
            }
        });


        btnInfo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = "Мы используем ваши паспортные данные для того, чтобы проверяющий\n" +
                                 "в библиотеке мог убедиться, что это действительно Вы.\n\n" +
                                 "Также вы можете использовать ваши паспортные данные для того, чтобы\n"+
                                 "получить доступ к вашему аккаунту, если вдруг забудете пароль.";
                showInformationMessage("Важная информация", message);
            }
        });


        btnOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String passportId = tfPassportId.getText();
                String issuedBy = tfIssuedBy.getText();
                String departmentNum = tfDepatrmentNum.getText();
                String dateOfIssue = tfDateOfIssue.getText();

                if(passportId.isEmpty() || issuedBy.isEmpty() || departmentNum.isEmpty() || dateOfIssue.isEmpty()){
                    showErrorMessage("Ошибка!", "Некоторые поля остались незаполненными!");
                } else if(!Passport.isCorrectPassportId(passportId)){
                    showErrorMessage("Ошибка!", "Неправильно указан номер и серия паспорта!");
                } else if(!Passport.isCorrectDepartmentNumber(departmentNum)){
                    showErrorMessage("Ошибка!", "Неправильно указан код подразделения!");
                } else if(!Passport.isCorrectDateOfIssue(dateOfIssue)){
                    showErrorMessage("Ошибка!", "Неправильно указана дата выдачи!");
                } else{
                    user.setPassportId(passportId);
                    user.setIssuedBy(issuedBy);
                    user.setDepartmentNum(departmentNum);
                    user.setDateOfIssue(dateOfIssue);

                    try(Connection connection = DriverManager.getConnection(Database.URL, Database.USERNAME, Database.PASSWORD);
                        Statement statement = connection.createStatement()){

                        String query = "insert into users (name, surname, patronymic, phone, email, password, age, passportid, issuedby," +
                                " departmentnumber, dateofissue) values ('" + user.getName() + "', '" + user.getSurname() +"', '" + user.getPatronymic() + "'," +
                                " '" + user.getPhone() + "', '" + user.getEmail() + "', '" + user.getPassword() + "', '" + user.getAge() + "', '" +
                                 passportId + "', '" + issuedBy + "', '" + departmentNum + "', '" + dateOfIssue + "');";
                            statement.executeUpdate(query);
                            showInformationMessage("", "Вы были успешно зарегистированы!");
                            ResultSet res = statement.executeQuery("select userid from users order by userid desc limit 1");
                            if(res.next())
                                user.setId(res.getInt("userid"));
                            dispose();
                            regForm.dispose();
                            createLoginForm();

                    } catch (SQLException ex){
                        showErrorMessage("Ошибка!", "Ошибка соединения с базой данных. Попробуйте позже.");
                        ex.printStackTrace();
                    }
                }
            }
        });


        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setInvisible();
                dispose();
                regForm.setVisible(true);
            }
        });

        setVisible(true);
    }

    private void showInformationMessage(String title, String message){
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    private void showErrorMessage(String title, String message){
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }

    private LoginForm createLoginForm(){
        return new LoginForm(null);
    }

    private void setInvisible(){
        setVisible(false);
    }

}

