import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Deque;

public class AdminMainFrame extends JDialog {
    private JPanel panel1;
    private JButton btnTodaySessions;
    private JButton btnAllTimeSessions;
    private JButton btnBookList;
    private JButton btnUsers;
    private JButton btnBlackList;
    private JPanel pnlCards;
    private JPanel pnlTodaySessions;
    private JPanel pnlAllTimeSessions;
    private JComboBox cbDay;
    private JPanel pnlBookList;
    private JPanel pnlUsers;
    private JPanel pnlBlackList;
    CardLayout cardLayout;
    String today;
    boolean allTimeSessionsWasClicked = false;
    JTable users;
    JScrollPane spUsers;
    JTable tableBlackList;

    public AdminMainFrame(JFrame parent){
        super(parent);
        setContentPane(panel1);
        setLocation(500, 250);
        setModal(true);
        setSize(new Dimension(800, 510));
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        cardLayout = (CardLayout) pnlCards.getLayout();

        LocalDate currentDate = LocalDate.now();
        String day = String.valueOf(currentDate.getDayOfMonth());
        String month = String.valueOf(currentDate.getMonthValue());
        if(day.length() == 1)
            day = "0".concat(day);
        if(month.length() == 1)
            month = "0".concat(month);
        today = day + "." + month;

        JButton[] buttons = {btnTodaySessions, btnAllTimeSessions, btnUsers, btnBlackList, btnBookList};
        for(JButton btn : buttons){
            btn.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    btn.setBackground(new Color(153, 167, 242));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    btn.setBackground(new Color(242, 242, 242));
                }
            });
        }

        createTableSessions(today, true);
        initializeCbDay();
        createBookList();
        createTableUsers();
        createTableBlackList();

        btnTodaySessions.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(pnlCards, "CardTodaySessions");
            }
        });

        btnAllTimeSessions.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(pnlCards, "CardAllTimeSessions");
            }
        });

        btnBookList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(pnlCards, "CardBookList");
            }
        });


        btnUsers.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(pnlCards, "CardUsers");
            }
        });

        btnBlackList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(pnlCards, "CardBlackList");
            }
        });


        cbDay.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(allTimeSessionsWasClicked)
                    pnlAllTimeSessions.remove(1);
                String date = (String) cbDay.getSelectedItem();
                createTableSessions(date, false);
                allTimeSessionsWasClicked = true;
            }
        });

        setVisible(true);


    }

    private void createBookList(){
        try(Connection connection = DriverManager.getConnection(Database.URL, Database.USERNAME, Database.PASSWORD);
                Statement statement = connection.createStatement()){

             DefaultTableModel model = new DefaultTableModel();
            JTable bookList = new JTable(model){
                //private static final long serialVersionUID = 1L;
                public boolean isCellEditable(int row, int col)
                {
                    return false;
                }
            };
            JPanel panel = new JPanel(new BorderLayout());
            //panel.setOpaque(false);

            model.addColumn("Название");
            model.addColumn("Автор");
            model.addColumn("Год издетельства");
            model.addColumn("Средння оценка");
            model.addColumn("Жанр");
            model.addColumn("Обложка");

            bookList.getColumnModel().getColumn(0).setPreferredWidth(300);
            bookList.getColumnModel().getColumn(1).setPreferredWidth(300);
            bookList.getColumnModel().getColumn(2).setPreferredWidth(300);
            bookList.getColumnModel().getColumn(3).setPreferredWidth(300);
            bookList.getColumnModel().getColumn(4).setPreferredWidth(400);
            bookList.getColumnModel().getColumn(5).setPreferredWidth(300);

            bookList.setRowHeight(20);
            //bookList.setEnabled(false);


            ResultSet res = statement.executeQuery("select name, author, yearofpublishing, rating, genre, imagepath from books;");
            while(res.next()){
                String[] genres = res.getString("genre").split("\n");
                String genre = "";
                for(int i = 0; i < genres.length; i++){
                    if(i == genres.length - 1)
                        genre += genres[i];
                    else
                        genre += genres[i] + ", ";
                }
                Object[] rowData = {res.getString("name"), res.getString("author"), res.getInt("yearofpublishing"),
                                    res.getFloat("rating"), genre, res.getString("imagepath")};
                model.addRow(rowData);

            }

            JButton btnAddBook = new JButton("Добавить книгу");
            JButton btnDeleteBook = new JButton("Убрать книгу");
            btnAddBook.setFocusPainted(false);
            btnDeleteBook.setFocusPainted(false);
            btnDeleteBook.setEnabled(false);

            JPanel pnlButtons = new JPanel(new GridLayout());
            pnlButtons.add(btnAddBook);
            pnlButtons.add(btnDeleteBook);

            panel.add(bookList, BorderLayout.CENTER);
            JScrollPane sp = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            pnlBookList.add(sp, BorderLayout.CENTER);
            pnlBookList.add(pnlButtons, BorderLayout.SOUTH);

            btnAddBook.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    createAddBookForm(model);
                }
            });


            ListSelectionModel selModel = bookList.getSelectionModel();

            selModel.addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    btnDeleteBook.setEnabled(false);
                    int[] selectedRows = bookList.getSelectedRows();
                    if(selectedRows.length == 1) {
                        btnDeleteBook.setEnabled(true);
                    }
                }
            });

            btnDeleteBook.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int[] selectedRows = bookList.getSelectedRows();
                    DefaultTableModel model = (DefaultTableModel) bookList.getModel();
                    for(int i = 0; i < selectedRows.length; i++){
                        int selIndex = selectedRows[i];
                        deleteBookFromDatabase( (String) model.getValueAt(selIndex, 0));
                        model.removeRow(selIndex);
                    }
                    btnDeleteBook.setEnabled(false);
                }
            });

        } catch (SQLException ex){
            showErrorMessage("Ошибка соединения с базой данных. Попробуйте позже", "Ошибка!");
            ex.printStackTrace();
        }
    }

//    private void updateImagePathInDatabase(String imagepath, String name){
//        try(Connection connection = DriverManager.getConnection(Database.URL, Database.USERNAME, Database.PASSWORD);
//                Statement statement = connection.createStatement()){
//
//            String query = "update books set imagepath = '" + imagepath + "' where name = '" + name + "';";
//            statement.executeUpdate(query);
//        } catch (SQLException ex){
//            showErrorMessage("Ошибка соединения с базой данных. Попробуйте позже", "Ошибка!");
//            ex.printStackTrace();
//        }
//    }

    private void createTableUsers(){
        DefaultTableModel model = new DefaultTableModel(){

            public boolean isCellEditable(int row, int col)
            {
                return false;
            }
        };
        users = new JTable(model);
        JPanel panel = new JPanel(new BorderLayout());

        model.addColumn("ID");
        model.addColumn("Фамилия");
        model.addColumn("Имя");
        model.addColumn("Отчество");
        model.addColumn("Телефон");
        model.addColumn("Email");
        model.addColumn("Возраст");
        model.addColumn("Серия и номер паспорта");
        model.addColumn("Кем выдан");
        model.addColumn("Код подразделения");
        model.addColumn("Дата выдачи");
        model.addColumn("Статус");

        users.getColumnModel().getColumn(0).setPreferredWidth(250);
        users.getColumnModel().getColumn(1).setPreferredWidth(250);
        users.getColumnModel().getColumn(2).setPreferredWidth(250);
        users.getColumnModel().getColumn(3).setPreferredWidth(250);
        users.getColumnModel().getColumn(4).setPreferredWidth(250);
        users.getColumnModel().getColumn(5).setPreferredWidth(250);
        users.getColumnModel().getColumn(6).setPreferredWidth(250);
        users.getColumnModel().getColumn(7).setPreferredWidth(250);
        users.getColumnModel().getColumn(8).setPreferredWidth(250);
        users.getColumnModel().getColumn(9).setPreferredWidth(250);
        users.getColumnModel().getColumn(10).setPreferredWidth(250);
        users.getColumnModel().getColumn(11).setPreferredWidth(250);
        users.setRowHeight(20);

        try(Connection connection = DriverManager.getConnection(Database.URL, Database.USERNAME, Database.PASSWORD);
                Statement statement = connection.createStatement()){
            String query = "select * from users order by userid asc;";
            ResultSet res = statement.executeQuery(query);

            while(res.next()){
                Object[] rowData = {res.getInt("userid"), res.getString("surname"), res.getString("name"),
                res.getString("patronymic"), res.getString("phone"), res.getString("email"), res.getInt("age"),
                res.getString("passportid"), res.getString("issuedby"), res.getString("departmentnumber"),
                res.getString("dateofissue"), res.getInt("isblocked") == 1 ? "Заблокирован" : ""};
                model.addRow(rowData);
            }

        } catch (SQLException ex){
            showErrorMessage("Ошибка соединения с базой данных. Попробуйте позже", "Ошибка!");
            ex.printStackTrace();
        }


        panel.add(users, BorderLayout.CENTER);

        spUsers = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        pnlUsers.add(spUsers, BorderLayout.CENTER);

        JButton btnBanUser = new JButton("Заблокировать пользователя");
        JButton btnUnbanUser = new JButton("Разблокировать пользователя");
        btnUnbanUser.setFocusPainted(false);
        btnBanUser.setFocusPainted(false);
        btnBanUser.setEnabled(false);
        btnUnbanUser.setEnabled(false);

        JPanel pnlButtons = new JPanel(new GridLayout());
        pnlButtons.add(btnBanUser);
        pnlButtons.add(btnUnbanUser);

        JPanel pnlSearch = new JPanel(new FlowLayout());
        JTextField tfSearch = new JTextField();
        JLabel labelSearch = new JLabel("Поиск по ID");
        labelSearch.setPreferredSize(new Dimension(100, 20));

        tfSearch.setPreferredSize(new Dimension(100, 20));

        pnlSearch.add(labelSearch);
        pnlSearch.add(tfSearch);
        pnlUsers.add(pnlSearch, BorderLayout.NORTH);

        tfSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    int id = -1;
                    TableModel model = users.getModel();
                    try{
                       id = Integer.parseInt(tfSearch.getText());
                    } catch (NumberFormatException ex) {
                        showErrorMessage("Неправильно введен ID!", "Ошибка!");
                    }

                    int indexToScrollTo = 0;
                    for(int i = 0; i < users.getRowCount(); i++){
                        if(id == (Integer) model.getValueAt(i, 0)) {
                            indexToScrollTo = i;
                            break;
                        }
                    }

                    users.changeSelection(indexToScrollTo, 0, false, false);
                    int position = indexToScrollTo * users.getRowHeight();
                    JScrollBar sb = spUsers.getVerticalScrollBar();
                    sb.setValue(position);

                }
            }
        });

        ListSelectionModel selModel = users.getSelectionModel();

        selModel.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                btnBanUser.setEnabled(false);
                btnUnbanUser.setEnabled(false);
                int[] selectedRows = users.getSelectedRows();
                if(selectedRows.length == 1) {
                    int selIndex = selectedRows[0];
                    DefaultTableModel model = (DefaultTableModel) users.getModel();
                    if (model.getValueAt(selIndex, 11).equals("Заблокирован"))
                        btnUnbanUser.setEnabled(true);
                    else
                        btnBanUser.setEnabled(true);
                } else{
                    btnUnbanUser.setEnabled(false);
                    btnBanUser.setEnabled(false);
                }
            }
        });

        btnBanUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //должен поменять значение isblocked и удалить все сеансы этого пользователя в sessions
                int[] selectedRows = users.getSelectedRows();
                int selIndex = selectedRows[0];
                DefaultTableModel model = (DefaultTableModel) users.getModel();
                int userid =  (Integer) model.getValueAt(selIndex, 0);
                banUser(userid);
                deletePlannedSessions(userid);
                model.setValueAt("Заблокирован", selIndex, 11);
                btnBanUser.setEnabled(false);
                //обновляю черный список
                addToTableBlackList(userid, (String) model.getValueAt(selIndex, 1), (String)model.getValueAt(selIndex, 2),
                        (String) model.getValueAt(selIndex, 3), (String)model.getValueAt(selIndex, 4),
                        (String)model.getValueAt(selIndex, 5), (Integer) model.getValueAt(selIndex, 6),
                        (String)model.getValueAt(selIndex, 7), (String)model.getValueAt(selIndex, 8),
                        (String)model.getValueAt(selIndex, 9), (String)model.getValueAt(selIndex, 10)
                );
            }
        });

        btnUnbanUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[] selectedRows = users.getSelectedRows();
                int selIndex = selectedRows[0];
                DefaultTableModel model = (DefaultTableModel) users.getModel();
                int userid = (Integer) model.getValueAt(selIndex, 0);
                unbanUser(userid);
                model.setValueAt("", selIndex, 11);
                btnUnbanUser.setEnabled(false);
                deleteFromTableBlackList(userid);
            }
        });

        pnlUsers.add(pnlButtons, BorderLayout.SOUTH);

    }

    private void deleteFromTableBlackList(int userId){
        DefaultTableModel model =  (DefaultTableModel) tableBlackList.getModel();
        for(int i = 0; i < tableBlackList.getRowCount(); i++){
            if(userId == (Integer) model.getValueAt(i, 0)) {
                model.removeRow(i);
            }
        }
    }

    private void createTableBlackList(){
        DefaultTableModel model = new DefaultTableModel(){
            public boolean isCellEditable(int row, int col)
            {
                return false;
            }
        };

        tableBlackList = new JTable(model);

        model.addColumn("ID");
        model.addColumn("Фамилия");
        model.addColumn("Имя");
        model.addColumn("Отчество");
        model.addColumn("Телефон");
        model.addColumn("Email");
        model.addColumn("Возраст");
        model.addColumn("Серия и номер паспорта");
        model.addColumn("Кем выдан");
        model.addColumn("Код подразделения");
        model.addColumn("Дата выдачи");

        tableBlackList.getColumnModel().getColumn(0).setPreferredWidth(250);
        tableBlackList.getColumnModel().getColumn(1).setPreferredWidth(250);
        tableBlackList.getColumnModel().getColumn(2).setPreferredWidth(250);
        tableBlackList.getColumnModel().getColumn(3).setPreferredWidth(250);
        tableBlackList.getColumnModel().getColumn(4).setPreferredWidth(250);
        tableBlackList.getColumnModel().getColumn(5).setPreferredWidth(250);
        tableBlackList.getColumnModel().getColumn(6).setPreferredWidth(250);
        tableBlackList.getColumnModel().getColumn(7).setPreferredWidth(250);
        tableBlackList.getColumnModel().getColumn(8).setPreferredWidth(250);
        tableBlackList.getColumnModel().getColumn(9).setPreferredWidth(250);
        tableBlackList.getColumnModel().getColumn(10).setPreferredWidth(250);
        tableBlackList.setRowHeight(20);

        try(Connection connection = DriverManager.getConnection(Database.URL, Database.USERNAME, Database.PASSWORD);
                Statement statement = connection.createStatement()){
            String query = "select * from users where isblocked = 1;";
            ResultSet res = statement.executeQuery(query);

            while(res.next()){
                Object[] rowData = {res.getInt("userid"), res.getString("surname"), res.getString("name"),
                        res.getString("patronymic"), res.getString("phone"), res.getString("email"), res.getInt("age"),
                        res.getString("passportid"), res.getString("issuedby"), res.getString("departmentnumber"),
                        res.getString("dateofissue")};
                model.addRow(rowData);
            }

            JPanel panel = new JPanel(new BorderLayout());
            panel.add(tableBlackList, BorderLayout.CENTER);
            JScrollPane sp = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            pnlBlackList.add(sp, BorderLayout.CENTER);

            JPanel pnlSearch = new JPanel(new FlowLayout());
            JLabel labelSearch = new JLabel("Поиск по ID");
            labelSearch.setPreferredSize(new Dimension(100, 20));
            JTextField tfSearch = new JTextField();
            tfSearch.setPreferredSize(new Dimension(100, 20));

            pnlSearch.add(labelSearch);
            pnlSearch.add(tfSearch);

            pnlBlackList.add(pnlSearch, BorderLayout.NORTH);

            JButton btnUnbanUser = new JButton("Разблокировать");
            btnUnbanUser.setFocusPainted(false);
            btnUnbanUser.setEnabled(false);

            tfSearch.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if(e.getKeyCode() == KeyEvent.VK_ENTER){
                        int id = -1;
                        TableModel model = tableBlackList.getModel();
                        try{
                            id = Integer.parseInt(tfSearch.getText());
                        } catch (NumberFormatException ex) {
                            showErrorMessage("Неправильно введен ID!", "Ошибка!");
                        }

                        int indexToScrollTo = 0;
                        for(int i = 0; i < tableBlackList.getRowCount(); i++){
                            if(id == (Integer) model.getValueAt(i, 0)) {
                                indexToScrollTo = i;
                                break;
                            }
                        }
                        

                        tableBlackList.changeSelection(indexToScrollTo, 0, false, false);
                        int position = indexToScrollTo * users.getRowHeight();
                        JScrollBar sb = spUsers.getVerticalScrollBar();
                        sb.setValue(position);
                    }
                }
            });


           // btnUnbanUser.setPreferredSize(new Dimension(800, 20));
            pnlBlackList.add(btnUnbanUser, BorderLayout.SOUTH);

            ListSelectionModel listSelectionModel = tableBlackList.getSelectionModel();
            listSelectionModel.addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    btnUnbanUser.setEnabled(false);
                    int[] selectedRows = tableBlackList.getSelectedRows();
                    if(selectedRows.length == 1){
                        btnUnbanUser.setEnabled(true);
                    }
                }
            });



            btnUnbanUser.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int[] selectedRows = tableBlackList.getSelectedRows();
                    int selIndex = selectedRows[0];
                    DefaultTableModel defaultTableModel = (DefaultTableModel) tableBlackList.getModel();
                    int userid = (Integer) defaultTableModel.getValueAt(selIndex, 0);
                    unbanUser(userid);
                    defaultTableModel.removeRow(selIndex);
                    btnUnbanUser.setEnabled(false);
                    updateTableUsers(userid);
                }
            });




        } catch (SQLException ex){
            showErrorMessage("Ошибка соединения с базой данных. Попробуйте позже", "Ошибка!");
            ex.printStackTrace();
        }
    }

    private void updateTableUsers(int userid){
        DefaultTableModel defaultTableModel = (DefaultTableModel) users.getModel();
        for(int i = 0; i < users.getRowCount(); i++){
            if( (Integer) defaultTableModel.getValueAt(i, 0) == userid) {
                defaultTableModel.setValueAt("", i,  11);
                break;
            }
        }
    }

    private void addToTableBlackList(int id, String name, String surname, String patronymic, String phone, String email,
                                      int age, String passportId, String issuedBy, String departmentNumber, String dateOfIssue){

        DefaultTableModel defaultTableModel = (DefaultTableModel) tableBlackList.getModel();
        Object[] rowData = {id, surname, name, patronymic, phone, email, age, passportId, issuedBy, departmentNumber, dateOfIssue};
        defaultTableModel.addRow(rowData);
    }

    private void deletePlannedSessions(int userid){ // если пользователь имел сеансы на этот день, то их удаляем и обновляем todaySessions
        try(Connection connection = DriverManager.getConnection(Database.URL, Database.USERNAME, Database.PASSWORD);
                Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)){

            String query = "select id, date, starttime, userarrived from sessions where userid = '" + userid + "';";
            ResultSet res = statement.executeQuery(query);

            while(res.next()){
                String date = res.getString("date");
                int startHour = Integer.parseInt(res.getString("starttime").substring(0, 2));
                int code = isFollowingMoment(date, startHour);
                if(code == 0)
                    continue;
                if(code == 2) { // он пришел
                    res.updateInt("userarrived", 1);
                    res.updateRow();
                }
                else{
                    res.deleteRow();
                }
                pnlTodaySessions.removeAll();
                createTableSessions(today, true);

            }

        } catch (SQLException ex){
            showErrorMessage("Ошибка соединения с базой данных. Попробуйте позже", "Ошибка!");
            ex.printStackTrace();
        }
    }

    private int isFollowingMoment( String date, int startHour){
        LocalDateTime currentMoment = LocalDateTime.now();
        int currentDay = currentMoment.getDayOfMonth();
        int currentMonth = currentMoment.getMonthValue();
        int currentHour = currentMoment.getHour();
        int month = Integer.parseInt( date.substring(3));
        int day =  Integer.parseInt(date.substring(0, 2));

        if(month > currentMonth)
            return 1;
        else if(month == currentMonth){
            if(day > currentDay)
                return 1;
            else if(day == currentDay){
                if(startHour == currentHour)
                    return 2;
                else if(startHour > currentHour)
                    return 1;
            }
        }
        return 0;
    }

    private void unbanUser(int userid){
        try(Connection connection = DriverManager.getConnection(Database.URL, Database.USERNAME, Database.PASSWORD);
                Statement statement = connection.createStatement()){

            String query = "update users set isblocked = 0 where userid = '" + userid + "';";
            statement.executeUpdate(query);
        } catch (SQLException ex){
            showErrorMessage("Ошибка соединения с базой данных. Попробуйте позже", "Ошибка!");
            ex.printStackTrace();
        }
    }

    private void banUser(int userid){
        try(Connection connection = DriverManager.getConnection(Database.URL, Database.USERNAME, Database.PASSWORD);
                Statement statement = connection.createStatement()){

            String query = "update users set isblocked = 1 where userid = '" + userid + "';";
            statement.executeUpdate(query);
        } catch (SQLException ex){
            showErrorMessage("Ошибка соединения с базой данных. Попробуйте позже", "Ошибка!");
            ex.printStackTrace();
        }
    }

    private addBookForm createAddBookForm(DefaultTableModel model){
        return new addBookForm(null,  model);
    }

    private void deleteBookFromDatabase(String name){
        try(Connection connection = DriverManager.getConnection(Database.URL, Database.USERNAME, Database.PASSWORD);
                Statement statement = connection.createStatement()){

            String query = "delete from books where name = '" + name + "';";
            statement.executeUpdate(query);

        } catch (SQLException ex){
            showErrorMessage("Ошибка соединения с базой данных. Попробуйте позже", "Ошибка!");
            ex.printStackTrace();
        }
    }

    private void createTableSessions(String date, boolean isCardTodaySessions){

        DefaultTableModel model = new DefaultTableModel();
        JTable todaySessions = new JTable(model){

            public boolean isCellEditable(int row, int col)
            {
                return false;
            }
        };
        JPanel panel = new JPanel(new BorderLayout());
            //panel.setOpaque(false);

            model.addColumn("Книга");
            model.addColumn("Автор");
            model.addColumn("Время");
            model.addColumn("ФИО");
            model.addColumn("ID пользователя");
            model.addColumn("Номер и серия паспорта");
            model.addColumn("Кем выдан");
            model.addColumn("Код подразделения");
            model.addColumn("Дата выдачи");
            todaySessions.getColumnModel().getColumn(0).setPreferredWidth(250);
            todaySessions.getColumnModel().getColumn(1).setPreferredWidth(250);
            todaySessions.getColumnModel().getColumn(2).setPreferredWidth(250);
            todaySessions.getColumnModel().getColumn(3).setPreferredWidth(250);
            todaySessions.getColumnModel().getColumn(4).setPreferredWidth(250);
            todaySessions.getColumnModel().getColumn(5).setPreferredWidth(250);
            todaySessions.getColumnModel().getColumn(6).setPreferredWidth(250);
            todaySessions.getColumnModel().getColumn(7).setPreferredWidth(250);
            todaySessions.getColumnModel().getColumn(8).setPreferredWidth(250);

            todaySessions.setRowHeight(20);

            //Object[] headers = {"Книга", "Автор", "Время", "ФИО", "ID пользователя", "Номер и серия паспорта", "Кем выдан", "Код подразделения", "Дата выдачи"};
            // model.addRow(headers);

            try (Connection connection = DriverManager.getConnection(Database.URL, Database.USERNAME, Database.PASSWORD);
                 Statement statement = connection.createStatement()) {

                    String query  = "select users.name, users.surname, users.patronymic, users.passportid, users.issuedby, users.departmentnumber, " +
                        "users.dateofissue, " + "sessions.book, sessions.author, sessions.date, sessions.starttime, sessions.endtime, sessions.userid," +
                            " sessions.userarrived from users join sessions" +
                            " on users.userid = sessions.userid where date = '" + date + "'";
                    if(isCardTodaySessions)
                            query = query.concat(" and userarrived = 0;");
                        else
                            query = query.concat(";");

                ResultSet res = statement.executeQuery(query);
                ArrayList<Session> sessions = new ArrayList<>();

                while (res.next()) {
                    User user = new User(res.getInt("userid"), res.getString("name"), res.getString("surname"),
                            res.getString("patronymic"), "", "", "", 0, res.getString("passportid"),
                            res.getString("issuedby"), res.getString("departmentnumber"), res.getString("dateofissue"));
                    sessions.add(new Session(res.getString("book"), res.getString("author"), res.getString("date"),
                            res.getString("starttime") + " - " + res.getString("endtime"), user));
                }
                //сортируем массив по starttime
                sortSessions(sessions);
                for (int i = 0; i < sessions.size(); i++) {
                    Session session = sessions.get(i);
                    User user = session.getUser();

                    Object[] rowData = {session.getBook(), session.getAuthor(), session.getTime(), user.getSurname() + " " + user.getName() + " " +
                            user.getPatronymic(), user.getId(), user.getPassportId(), user.getIssuedBy(), user.getDepartmentNum(), user.getDateOfIssue()};
                    model.addRow(rowData);
                }

            } catch (SQLException exception) {
                showErrorMessage("Ошибка соединения с базой данных. Попробуйте позже", "Ошибка!");
                exception.printStackTrace();
            }

            todaySessions.setFillsViewportHeight(true);
            //todaySessions.setEnabled(false);
            panel.add(todaySessions, BorderLayout.CENTER);

            JScrollPane sp = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            sp.setOpaque(false);


        if(!isCardTodaySessions){ // для карточки всех сессий
            pnlAllTimeSessions.add(sp,BorderLayout.CENTER);
            pnlAllTimeSessions.updateUI();
        } else {

            pnlTodaySessions.add(sp, BorderLayout.CENTER);

            JButton btnMarkedUserArrived = new JButton("Отметить выполненными");
            btnMarkedUserArrived.setFocusPainted(false);
            btnMarkedUserArrived.setEnabled(false);

            pnlTodaySessions.add(btnMarkedUserArrived, BorderLayout.SOUTH);

            ListSelectionModel selModel = todaySessions.getSelectionModel();

            selModel.addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    btnMarkedUserArrived.setEnabled(false);
                    int[] selectedRows = todaySessions.getSelectedRows();
                    if(selectedRows.length == 1)
                        btnMarkedUserArrived.setEnabled(true);
                }
            });

            btnMarkedUserArrived.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int[] selectedRows = todaySessions.getSelectedRows();
                    for (int i = 0; i < selectedRows.length; i++) {
                        int selIndex = selectedRows[i];
                        TableModel model1 = todaySessions.getModel();
                        Integer userid = (Integer) model1.getValueAt(selIndex, 4);
                        String startime = ((String) model1.getValueAt(selIndex, 2)).substring(0, 5);
                        setUserArrivedTrueInDatabase(userid, today, startime);
                        model.removeRow(selIndex);
                        btnMarkedUserArrived.setEnabled(false);
                    }
                }
            });
        }
    }

    private void initializeCbDay(){
        //создать элементы комбобокс
        LocalDate startDate = LocalDate.now().minusDays(30);
        LocalDate endDate = LocalDate.now().plusDays(20);
        LocalDate buf = startDate;
        while(!buf.equals(endDate)){
            String day = String.valueOf(buf.getDayOfMonth());
            String month = String.valueOf(buf.getMonthValue());
            if(day.length() == 1)
                day = "0".concat(day);
            if(month.length() == 1)
                month = "0".concat(month);
            String date = day + "." + month;
            cbDay.addItem(date);
            buf = buf.plusDays(1);
        }


    }

    private void sortSessions(ArrayList<Session>  sessions){
            //МЕТОД ПУЗЫРЬКОВОЙ СОРТИРОВКИ
        int leftStartTime = 0;
        int rightStartTime = 0;
        for (int out = sessions.size() - 1; out >= 1; out--){  //Внешний цикл
            for (int in = 0; in < out; in++){
                leftStartTime = Integer.parseInt(sessions.get(in).getTime().substring(0, 2)); //Внутренний цикл
                rightStartTime = Integer.parseInt(sessions.get(in + 1).getTime().substring(0, 2));
                if(leftStartTime > rightStartTime) {               //Если порядок элементов нарушен
                    Session temp = sessions.get(in);
                    sessions.set(in, sessions.get(in + 1));
                    sessions.set(in + 1, temp);
                }//вызвать метод, меняющий местами
            }
        }

    }

    private void setUserArrivedTrueInDatabase(int userid, String date, String startTime){
        try(Connection connection = DriverManager.getConnection(Database.URL, Database.USERNAME, Database.PASSWORD);
            Statement statement = connection.createStatement()){

            statement.executeUpdate("update sessions set userarrived = 1 where userid = '" + userid + "' and date = '" + date + "' and " +
                    "starttime = '" + startTime + "';");

        } catch (SQLException exception){
            showErrorMessage("Ошибка соединения с базой данных. Попробуйте позже", "Ошибка!");
            exception.printStackTrace();
        }
    }

    private void showErrorMessage(String message, String title){
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }

}
