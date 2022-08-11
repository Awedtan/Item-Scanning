import java.io.*;
import java.util.*;
import org.apache.pdfbox.pdmodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.hssf.usermodel.*;
import technology.tabula.*;
import technology.tabula.Table;
import technology.tabula.extractors.*;

public class Main {
    public static void main(String[] args) throws Exception {

        HashMap<String, Integer> upc_qty = new HashMap<String, Integer>(); // Map of all UPCs and qtys
        // upc_qty.put("716605694611", 3);

        // Get all the pages in the pdf file

        File folder = new File("lists");
        for (File f : folder.listFiles()) {
            PDDocument pd = PDDocument.load(f);
            ObjectExtractor oe = new ObjectExtractor(pd);
            BasicExtractionAlgorithm bea = new BasicExtractionAlgorithm();
            PageIterator pages = oe.extract();

            while (pages.hasNext()) {
                Page page = pages.next();

                // Try to get all tabled information from the page
                List<Table> table = bea.extract(page);

                // Iterate through all tables on pages, should only be one
                for (Table tables : table) {
                    List<List<RectangularTextContainer>> rows = tables.getRows();
                    String upc; // UPC of current item
                    int qty = 0; // Quantity of current item

                    // Row 19 is where actual item info starts
                    for (int i = 19; i < rows.size(); i++) {

                        // Combine then split row text by spaces
                        List<RectangularTextContainer> cells = rows.get(i);
                        List<String> cells_text = new ArrayList<String>();
                        for (int j = 0; j < cells.size(); j++)
                            cells_text.add(cells.get(j).getText());
                        String[] cells_split = String.join(" ", cells_text).split(" ");

                        // Row text format
                        // 1. 1 20402 WHIH 2XL 34" Lab Coat White 29701 716605882865 1.

                        // Get UPC from row text
                        upc = cells_split[cells_split.length - 2];
                        if (upc.equals("Total:")) // End of page
                            break;

                        // Get qty from row text
                        try {
                            qty = Integer.parseInt(cells_split[1]);
                        } catch (NumberFormatException e) {
                        }

                        // If item exists in hashmap, adds current qty
                        // If not, sets current qty
                        if (upc_qty.get(upc) == null)
                            upc_qty.put(upc, qty);
                        else
                            upc_qty.put(upc, upc_qty.get(upc) + qty);
                    }
                }
            }
            oe.close(); // End of packing list extraction
        }

        FileInputStream file = new FileInputStream(new File("spiatonceavailableinventory.xls"));
        HSSFWorkbook workbook = new HSSFWorkbook(file);
        HSSFSheet sheet = workbook.getSheetAt(0);

        HashMap<String, Item> items = new HashMap<String, Item>(); // Map of all items and qtys

        int count = 0;
        for (Row row : sheet) {
            if (count++ < 3)
                continue;

            // Spreadsheet row format
            // 1022 | BLKV | Black | (empty) | 2XL | 716605038156 | 16.00 | Drawstring Pant

            String row_upc = String.format("%.0f", row.getCell(5).getNumericCellValue());
            if (upc_qty.containsKey(row_upc)) {
                items.put(row_upc, new Item(row_upc, upc_qty.get(row_upc), row));
                upc_qty.remove(row_upc);
            }
        }
        if (upc_qty.isEmpty())
            System.out.println("All scanned items successfully imported.");
        else {
            for (String upc : upc_qty.keySet())
                System.out.println("UPC not imported: " + upc);
            System.out.println("Total of " + Integer.toString(upc_qty.keySet().size()) + " items were scanned but failed to import.");
        }

        new MFrame(items);

        workbook.close();
    }
}