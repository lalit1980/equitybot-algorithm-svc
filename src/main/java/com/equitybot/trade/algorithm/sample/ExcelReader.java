package com.equitybot.trade.algorithm.sample;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.ta4j.core.Bar;
import org.ta4j.core.BaseBar;
import org.ta4j.core.BaseTimeSeries;
import org.ta4j.core.Decimal;
import org.ta4j.core.TimeSeries;
import org.ta4j.core.indicators.ATRIndicator;

public class ExcelReader {
	public static final String SAMPLE_XLSX_FILE_PATH = "/Users/lalitsingh/Documents/TestData.xlsx";

	public static void main(String[] args) throws IOException, InvalidFormatException, InterruptedException {

		Workbook workbook = WorkbookFactory.create(new File(SAMPLE_XLSX_FILE_PATH));

		Sheet sheet = workbook.getSheetAt(0);

		DataFormatter dataFormatter = new DataFormatter();

		System.out.println("\n\nIterating over Rows and Columns using for-each loop\n "+sheet.getLastRowNum());
		Duration barDuration = Duration.ofSeconds(60);
		TimeSeries timeSeries=new BaseTimeSeries("TestTimeSeries");
		List<Bar> barList=new ArrayList<Bar>();
		for (int i = 0; i < sheet.getLastRowNum(); i++) {
			Thread.sleep(100);
			Row row = sheet.getRow(i);
			ZonedDateTime beginTime = ZonedDateTime.ofInstant(new Date().toInstant().plusSeconds(60), ZoneId.systemDefault());
			Decimal open = Decimal.valueOf(dataFormatter.formatCellValue(row.getCell(0)));
			Decimal high = Decimal.valueOf(dataFormatter.formatCellValue(row.getCell(1)));
			Decimal low = Decimal.valueOf(dataFormatter.formatCellValue(row.getCell(2)));
			Decimal close = Decimal.valueOf(dataFormatter.formatCellValue(row.getCell(3)));
			Bar bar=new BaseBar( barDuration,
					beginTime,  
					open, 
					high, 
					low, 
					close, 
					Decimal.valueOf(30+i)
					);
			barList.add(bar);
			System.out.println(" Open: "+open+" High: "+high+" Low: "+low+" Close: "+close);
			System.out.println();
			System.out.println(bar.toString());
			timeSeries.addBar(bar);
		}
		ATRIndicator atr=new ATRIndicator(timeSeries, 10);
		final int nbBars = timeSeries.getBarCount();
		for (int i = 0; i < nbBars; i++) {
        	System.out.println(i+": ATR: "+atr.getValue(i));
        	
        }
	}
}