import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCell;
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
        XSSFWorkbook originWorkbook = new XSSFWorkbook("origin_data_2022.xlsx");
        //XSSFWorkbook statisticsWorkbook = new XSSFWorkbook("statistics.xlsx");

        Iterator<Sheet> originIterator = originWorkbook.sheetIterator();
        //Iterator<Sheet> statisticsIterator = statisticsWorkbook.sheetIterator();
        int timeCount = 0;

        int originSheetCount = originWorkbook.getNumberOfSheets();
        //int statisticsSheetCount = statisticsWorkbook.getNumberOfSheets();
        //*if(originSheetCount != statisticsSheetCount)
            //throw new RuntimeException("num is not equal");*//*

        for(int k = 0;k < originSheetCount;k ++)
        {
            System.out.println(timeCount ++);

            XSSFSheet originSheet = originWorkbook.getSheetAt(k);

            int originRowNum = originSheet.getPhysicalNumberOfRows();
            Calendar preCalendar = timeStr2Calendar(originSheet.getRow(1).getCell(0).getStringCellValue());

            int CODRowColumn = 2;
           /* if(k == 0 || k == 7)
                CODRowColumn = 1;*/

            double firstCODValue = originSheet.getRow(1).getCell(CODRowColumn).getNumericCellValue();
            Calendar firstCODCalendar = timeStr2Calendar(originSheet.getRow(1).getCell(0).getStringCellValue());


            for(int i = 2;i < (originRowNum - 1);i ++)
            {
                XSSFRow originSheetRow = originSheet.getRow(i);
                Cell timeCell = originSheetRow.getCell(0);

                Cell CODCell = originSheetRow.getCell(CODRowColumn);

                if(timeCell == null || CODCell == null)
                    break;

                double currentCODValue = CODCell.getNumericCellValue();


                String currentCellTime = timeCell.getStringCellValue();
                Calendar currentCalendar = timeStr2Calendar(currentCellTime);
                if(isContinuous(preCalendar,currentCalendar))
                {
                    preCalendar = currentCalendar;
                }
                else
                {
                    Cell discontinuousMark = originSheetRow.createCell(7);
                    discontinuousMark.setCellValue("时间非连续点");
                    i ++;
                    preCalendar = timeStr2Calendar(originSheet.getRow(i).getCell(0).getStringCellValue());
                }

                if(currentCODValue != firstCODValue)
                {
                    firstCODValue = currentCODValue;
                    firstCODCalendar = currentCalendar;
                }
                else
                {
                    firstCODCalendar.add(Calendar.HOUR,6);
                    if(firstCODCalendar.equals(currentCalendar) || firstCODCalendar.before(currentCalendar))
                    {
                        firstCODValue = currentCODValue;
                        firstCODCalendar = currentCalendar;
                        originSheetRow.createCell(6).setCellValue("六小时未变化");
                    }
                    else
                    {
                        firstCODCalendar.add(Calendar.HOUR,-6);
                    }
                }


            }


        }
        FileOutputStream fileOutputStream = new FileOutputStream("2022原始数据检测.xlsx");
        originWorkbook.write(fileOutputStream);
        fileOutputStream.flush();
        fileOutputStream.close();
        originWorkbook.close();
        originWorkbook.close();

    }


    public static Calendar timeStr2Calendar(String timeStr) throws ParseException
    {
        String trimStr = timeStr.trim();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date  = dateFormat.parse(trimStr);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.SECOND,0);
        return cal;
    }

    public static boolean isContinuous(Calendar preCal,Calendar currentCal)
    {
        preCal.add(Calendar.MINUTE,5);
        if(preCal.equals(currentCal))
            return true;
        else
            return false;
       /* SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        String preCalStr = dateFormat.format(preCal.getTime());
        String currentCalStr = dateFormat.format(currentCal.getTime());

        if(preCalStr.equals(currentCalStr))
            return true;
        else
            return false;*/


    }
}
