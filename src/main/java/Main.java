import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

public class Main
{
    public static void main(String[] args) throws IOException,ParseException
    {
        XSSFWorkbook originWorkbook = new XSSFWorkbook("origin.xlsx");
        XSSFWorkbook statisticsWorkbook = new XSSFWorkbook("statistics.xlsx");

        Iterator<Sheet> originIterator = originWorkbook.sheetIterator();
        Iterator<Sheet> statisticsIterator = statisticsWorkbook.sheetIterator();
        int timeCount = 0;

        int originSheetCount = originWorkbook.getNumberOfSheets();
        int statisticsSheetCount = statisticsWorkbook.getNumberOfSheets();
        if(originSheetCount != statisticsSheetCount)
            throw new RuntimeException("num is not equal");

        for(int k = 0;k < originSheetCount;k ++)
        {
            System.out.println(timeCount ++);
            /*XSSFSheet statisticsSheet = (XSSFSheet) statisticsIterator.next();
            XSSFSheet originSheet = (XSSFSheet) originIterator.next();*/
        XSSFSheet statisticsSheet = statisticsWorkbook.getSheetAt(k);
        XSSFSheet originSheet = originWorkbook.getSheetAt(k);
            int statisticsRowNum = statisticsSheet.getPhysicalNumberOfRows();
            for(int i = 2;i < statisticsRowNum;i ++)
            {
                XSSFRow statisticsRow = statisticsSheet.getRow(i);
                Cell timeCell = statisticsRow.getCell(1);

                if(timeCell == null)
                    break;

                ValidateTime validateTime = new ValidateTime(timeCell.getStringCellValue());
                int originRowNum = originSheet.getPhysicalNumberOfRows();
                double CODSum = 0;
                int count = 0;

                double NH3N2Sum = 0;
                for(int j = 1;j < originRowNum;j ++)
                {
                    XSSFRow originRow = originSheet.getRow(j);
                    Cell cell = originRow.getCell(0);
                    if(validateTime.isTimeInRange(cell.getStringCellValue()))
                    {
                        Cell CODCell = originRow.getCell(2);
                        Cell NH2N2Cell = originRow.getCell(3);
                        CODSum += CODCell.getNumericCellValue();
                        NH3N2Sum += NH2N2Cell.getNumericCellValue();
                        count ++;
                    }
                }
                statisticsRow.createCell(7).setCellValue(CODSum / count);
                statisticsRow.createCell(8).setCellValue(NH3N2Sum / count);

            }


        }
        FileOutputStream fileOutputStream = new FileOutputStream("1.xlsx");
        statisticsWorkbook.write(fileOutputStream);
        fileOutputStream.flush();
        fileOutputStream.close();
        statisticsWorkbook.close();
        originWorkbook.close();

    }
}
