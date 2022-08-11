import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;

public class MFrame extends JFrame {
    static HashMap<String, Item> itemMap;
    static ArrayList<Item> itemList;
    static DefaultTableModel tableModel;
    static JTextField[] fieldArr;

    static boolean listenTable = false;

    public MFrame(HashMap<String, Item> inMap) {
        itemMap = inMap;

        JPanel topPanel;
        JLabel mainLabel;
        JTextField mainField;
        JLabel countLabel;
        JTextField countField;

        JScrollPane scrollPanel;
        JTable table;

        JPanel bottomPanel;
        JTextField skuField;
        JTextField colField;
        JTextField sizField;
        JTextField namField;
        JTextField upcField;
        JButton clearButton;

        String[] columnTitles = new String[] { "SKU", "Color", "Size", "Name", "UPC", "Qty", "Counted" };

        itemList = new ArrayList<>();
        itemList.addAll(itemMap.values());
        Collections.sort(itemList);

        // Middle scrolling table
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                if (column == 6)
                    return true;
                return false;
            }
        };

        tableModel.addTableModelListener(
                new TableModelListener() {
                    public void tableChanged(TableModelEvent e) {
                        if (!listenTable)
                            return;

                        int row = e.getFirstRow();
                        int column = e.getColumn();
                        TableModel model = (TableModel) e.getSource();
                        Object value = model.getValueAt(row, column);
                        itemList.get(row).counted = Integer.parseInt(value.toString());
                    }
                });

        for (String s : columnTitles)
            tableModel.addColumn(s);
        for (Item i : itemList)
            tableModel.insertRow(tableModel.getRowCount(), i.getData());

        table = new JTable(tableModel);
        table.setFont(new Font("SansSerif", Font.BOLD, 14));
        table.setRowHeight(20);

        table.getColumnModel().getColumn(0).setPreferredWidth(75);
        table.getColumnModel().getColumn(1).setPreferredWidth(50);
        table.getColumnModel().getColumn(2).setPreferredWidth(50);
        table.getColumnModel().getColumn(3).setPreferredWidth(350);
        table.getColumnModel().getColumn(4).setPreferredWidth(75);
        table.getColumnModel().getColumn(5).setPreferredWidth(50);
        table.getColumnModel().getColumn(6).setPreferredWidth(50);

        scrollPanel = new JScrollPane();
        scrollPanel.setViewportView(table);
        scrollPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Top barcode text fields
        topPanel = new JPanel();
        topPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        mainLabel = new JLabel("Scan barcode here:");
        mainField = new JTextField();
        countLabel = new JLabel("Count qty:");
        countField = new JTextField("1");

        mainField.setPreferredSize(new Dimension(500, 40));
        mainField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Utils.scanBarcode(table, mainField, countField);
            }
        });

        countField.setPreferredSize(new Dimension(40, 40));
        countField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Utils.scanBarcode(table, mainField, countField);
            }
        });

        topPanel.add(mainLabel);
        topPanel.add(mainField);
        topPanel.add(countLabel);
        topPanel.add(countField);

        // Bottom search text fields
        bottomPanel = new JPanel();
        bottomPanel.setBorder(new EmptyBorder(10, 10, 10, 25));
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.LINE_AXIS));

        skuField = new JTextField();
        colField = new JTextField();
        sizField = new JTextField();
        namField = new JTextField();
        upcField = new JTextField();

        fieldArr = new JTextField[] { skuField, colField, sizField, namField, upcField };

        clearButton = new JButton();
        clearButton.setText("Clear selection");

        for (JTextField jtf : fieldArr)
            jtf.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Utils.filterTableModel(fieldArr);
                }
            });

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Utils.resetTableModel();
            }
        });

        skuField.setPreferredSize(new Dimension(75, 30));
        colField.setPreferredSize(new Dimension(50, 30));
        sizField.setPreferredSize(new Dimension(50, 30));
        namField.setPreferredSize(new Dimension(350, 30));
        upcField.setPreferredSize(new Dimension(75, 30));
        clearButton.setPreferredSize(new Dimension(225, 30));

        for (JTextField jtf : fieldArr)
            bottomPanel.add(jtf);
        bottomPanel.add(clearButton);

        // Frame
        add(topPanel, BorderLayout.NORTH);
        add(scrollPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        setSize(1200, 700);
        setResizable(false);
        setVisible(true);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                int resp = JOptionPane.showConfirmDialog(null, "Are you sure you want to exit?",
                        "Exit?", JOptionPane.YES_NO_OPTION);
                if (resp == JOptionPane.YES_OPTION) {
                    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                } else {
                    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                }
            }
        });

        listenTable = true;
    }
}