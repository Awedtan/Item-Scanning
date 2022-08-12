import java.util.Arrays;
import org.apache.poi.ss.usermodel.Row;

public class Item implements Comparable<Item> {
    String upc;
    int qty;
    String sku;
    String color;
    String size;
    String name;
    int counted;
    boolean finished;

    public Item(String upc, int qty, String[] arr) {
        this.upc = upc;
        this.qty = qty;

        sku = arr[2];
        color = arr[3];
        size = arr[4];
        name = String.join(" ", Arrays.copyOfRange(arr, 5, arr.length - 3));
    }

    public Item(String upc, int qty, Row row) {
        this.upc = upc.trim();
        this.qty = qty;
        counted = 0;

        switch (row.getCell(0).getCellType()) {
            case NUMERIC:
                sku = String.format("%.0f", row.getCell(0).getNumericCellValue()).trim();
                break;
            case STRING:
                sku = row.getCell(0).getStringCellValue().trim();
                break;
            default:
                break;
        }

        color = row.getCell(1).getStringCellValue().trim();

        switch (row.getCell(4).getCellType()) {
            case NUMERIC:
                size = String.format("%.0f", row.getCell(4).getNumericCellValue()).trim();
                break;
            case STRING:
                size = row.getCell(4).getStringCellValue().trim();
                break;
            default:
                break;
        }

        name = row.getCell(7).getStringCellValue().trim();
    }

    public Object[] getData() {
        return new Object[] { sku, color, size, name, upc, qty, counted };
    }

    public void count(int i) {
        counted += i;
        if (qty == counted)
            finished = true;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Item) {
            if (((Item) o).upc == upc)
                return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("%s-%s-%s(%d)", sku, color, size, qty);
    }

    @Override
    public int compareTo(Item i) {

        if (sku.compareTo(i.sku) != 0)
            return sku.compareTo(i.sku);
        else if (color.compareTo(i.color) != 0)
            return color.compareTo(i.color);
        else if (size.compareTo(i.size) != 0)
            return size.compareTo(i.size);
        return upc.compareTo(i.upc);
    }
}