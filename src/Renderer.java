import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class Renderer extends DefaultTableCellRenderer {
    public Component getTableCellRendererComponent(JTable table,
                                                   Object value,
                                                   boolean isSelected,
                                                   boolean hasFocus,
                                                   int row,
                                                   int column) {
        Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        String cellvalue = table.getValueAt(row, table.getColumnModel().getColumnIndex("")).toString();
        if(cellvalue.equals("завершена")){cell.setEnabled(false);}
        return cell;
    }}