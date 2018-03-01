package eu.fiskaljdoo.stocktaking.utils;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.widget.Toast;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import eu.fiskaljdoo.stocktaking.R;
import eu.fiskaljdoo.stocktaking.adapters.AdapterStocktaking;
import eu.fiskaljdoo.stocktaking.dialogs.DialogEditResult;
import eu.fiskaljdoo.stocktaking.dialogs.DialogStocktakingPicker;
import eu.fiskaljdoo.stocktaking.models.Result;
import jxl.Cell;
import jxl.CellView;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

/**
 * Created by Valentin on 27.2.2018..
 */

public class Tools {

    public static boolean needRequestPermission() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);
    }

    public static void startEditResultDialog(FragmentManager fm, Result result, DialogEditResult.OnDismissListener onDismissListener) {
        DialogEditResult dialogEditResult = DialogEditResult.newInstance(result);
        dialogEditResult.show(fm, "dialogEditResult");
        dialogEditResult.setOnDismissListener(onDismissListener);
    }

    public static void startInventurePickerDialog(FragmentManager fm, AdapterStocktaking.StocktakingClickListener listener){
        DialogStocktakingPicker dialogStocktakingPicker = DialogStocktakingPicker.newInstance(listener);
        dialogStocktakingPicker.show(fm, "dialogStocktakingPicker");
    }

    public static void exportToExcel(Context context, List<Result> resultList){

        File sd = Environment.getExternalStorageDirectory();
        String csvFile = "results.xls";

        File directory = new File(sd.getAbsolutePath());
        //create directory if not exist
        if (!directory.isDirectory()) {
            directory.mkdirs();
        }
        try {
            //file path
            File file = new File(directory, csvFile);
            WorkbookSettings wbSettings = new WorkbookSettings();
            wbSettings.setLocale(new Locale("hr", "HR"));
            WritableWorkbook workbook;
            workbook = jxl.Workbook.createWorkbook(file, wbSettings);

            int red = 0;
            int kolona = 0;

            //Excel sheet name. 0 represents first sheet
            WritableSheet sheet = workbook.createSheet("resultList", 0);
            // column and row
            sheet.addCell(new Label(kolona++, red, "ID"));
            sheet.addCell(new Label(kolona++, red, "Broj inventure"));
            sheet.addCell(new Label(kolona++, red, "Bar code"));
            sheet.addCell(new Label(kolona++, red, "Artikal"));
            sheet.addCell(new Label(kolona++, red, "Količina"));
            sheet.addCell(new Label(kolona++, red, "Cijena"));
            sheet.addCell(new Label(kolona++, red, "Datum"));
            sheet.addCell(new Label(kolona, red, "Korisnik"));

            for (Result r: resultList){
                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat(context.getString(R.string.ISO_8601_PATTERN_1), Locale.getDefault());
                SimpleDateFormat vdf = new SimpleDateFormat(context.getString(R.string.VIEW_DATE_PATTERN), Locale.getDefault());
                c.setTime(sdf.parse(r.getDate()));

                kolona = 0;
                red ++;
                sheet.addCell(new Label(kolona++, red, Integer.toString(r.getId())));
                sheet.addCell(new Label(kolona++, red, Integer.toString(r.getStocktakingNumber())));
                sheet.addCell(new Label(kolona++, red, r.getArticle().getCode()));
                sheet.addCell(new Label(kolona++, red, r.getArticle().getName()));
                sheet.addCell(new Label(kolona++, red, String.format(Locale.getDefault(), "%s", new DecimalFormat("###,##0.##").format(r.getAmount()))));
                sheet.addCell(new Label(kolona++, red, String.format(Locale.getDefault(), "%s", new DecimalFormat("###,##0.##").format(r.getArticle().getPrice()))));
                sheet.addCell(new Label(kolona++, red, vdf.format(c.getTime())));
                sheet.addCell(new Label(kolona, red, r.getUser()));
            }

            sheetAutoFitColumns(sheet);

            workbook.write();
            workbook.close();
            Toast.makeText(context,
                    "Podaci uspješno exportani", Toast.LENGTH_SHORT).show();
        } catch(Exception e){
            e.printStackTrace();
        }

    }

    private static void sheetAutoFitColumns(WritableSheet sheet) {
        for (int i = 0; i < sheet.getColumns(); i++) {
            Cell[] cells = sheet.getColumn(i);
            int longestStrLen = -1;

            if (cells.length == 0)
                continue;

        /* Find the widest cell in the column. */
            for (int j = 0; j < cells.length; j++) {
                if ( cells[j].getContents().length() > longestStrLen ) {
                    String str = cells[j].getContents();
                    if (str == null || str.isEmpty())
                        continue;
                    longestStrLen = str.trim().length();
                }
            }

        /* If not found, skip the column. */
            if (longestStrLen == -1)
                continue;

        /* If wider than the max width, crop width */
            if (longestStrLen > 255)
                longestStrLen = 255;

            CellView cv = sheet.getColumnView(i);
            cv.setSize(longestStrLen * 256 + 100); /* Every character is 256 units wide, so scale it. */
            sheet.setColumnView(i, cv);
        }
    }

}
