import java.awt.Rectangle;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;

public class Utils {
    public static void clearTextFields(JTextField[] arr) {
        for (JTextField jtf : arr)
            jtf.setText((""));
    }

    public static boolean matchItemSearch(Item i, String sku, String col, String siz, String nam, String upc) {
        if (i.sku.toLowerCase().contains(sku.toLowerCase()) || sku.equals(""))
            if (i.color.toLowerCase().contains(col.toLowerCase()) || col.equals(""))
                if (i.size.toLowerCase().contains(siz.toLowerCase()) || siz.equals(""))
                    if (i.name.toLowerCase().contains(nam.toLowerCase()) || nam.equals(""))
                        if (i.upc.toLowerCase().contains(upc.toLowerCase()) || upc.equals(""))
                            return true;
        return false;
    }

    public static void filterTableModel(JTextField[] arr) {
        MFrame.listenTable = false;

        String sku = arr[0].getText();
        String col = arr[1].getText();
        String siz = arr[2].getText();
        String nam = arr[3].getText();
        String upc = arr[4].getText();

        if (!(sku + col + siz + nam + upc).equals("")) {
            MFrame.tableModel.setRowCount(0);

            for (Item i : MFrame.itemList)
                if (Utils.matchItemSearch(i, sku, col, siz, nam, upc))
                    MFrame.tableModel.insertRow(MFrame.tableModel.getRowCount(), i.getData());
        } else {
            resetTableModel();
        }
        MFrame.listenTable = true;
    }

    public static void resetTableModel() {
        MFrame.listenTable = false;

        MFrame.tableModel.setRowCount(0);
        for (Item i : MFrame.itemList)
            MFrame.tableModel.insertRow(MFrame.tableModel.getRowCount(), i.getData());

        MFrame.listenTable = true;

    }

    public static void scanBarcode(JTable table, JTextField mainField, JTextField countField) {
        MFrame.listenTable = false;

        String upc = mainField.getText();
        int count = 0;

        try {
            count = Integer.parseInt(countField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "The entered quantity is not valid.");
            return;
        }

        if (MFrame.itemMap.containsKey(upc)) {
            Item i = MFrame.itemMap.get(upc);
            int index = MFrame.itemList.indexOf(i);

            if (i.finished) {
                JOptionPane.showMessageDialog(null, "The quantity for this item has been reached.");
                return;
            }

            i.count(count);

            Utils.resetTableModel();
            Utils.clearTextFields(MFrame.fieldArr);

            table.getSelectionModel().setSelectionInterval(index, index);
            table.scrollRectToVisible(new Rectangle(table.getCellRect(index, 6, true)));
        } else {
            JOptionPane.showMessageDialog(null, "The entered UPC is not valid.");
            return;
        }

        mainField.setText("");
        countField.setText("1");

        MFrame.listenTable = true;
    }
}