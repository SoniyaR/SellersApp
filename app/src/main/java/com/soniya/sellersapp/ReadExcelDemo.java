package com.soniya.sellersapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ReadExcelDemo {

	static List<HashMap<String, String>> hmlist = new ArrayList<>();
	static String filename ="";

	public ReadExcelDemo(String fileName){
	    this.filename = fileName;
    }

	public List retrieveHMList() {
		
		try {
			FileInputStream inputStream = new FileInputStream(new File(filename));
			
			XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
			XSSFSheet sheet = workbook.getSheetAt(0);

			ArrayList<String> labels = new ArrayList<>();
			hmlist.clear();
			
			Iterator<Row> rowIterator = sheet.iterator();
			while(rowIterator.hasNext())	{

				HashMap<String, String> hm = new HashMap<>();
				
				Row row = rowIterator.next();
				
				//System.out.println(row.getRowNum());
				//if(row.getRowNum() == 1) {
				//	continue;
				//}
				
				Iterator<Cell> cellIterator = row.cellIterator();

				String val="";
				int count = 0;
				
				while(cellIterator.hasNext()) {
					
					Cell cell = cellIterator.next();
					switch(cell.getCellType())	{
					
					case NUMERIC:
						System.out.print(cell.getNumericCellValue()+"\t");
						val = String.valueOf(cell.getNumericCellValue());
						break;
						
					case STRING:
						System.out.print(cell.getStringCellValue()+"\t");
						val = cell.getStringCellValue();
						break;
					
					}
					if(row.getRowNum() == 1) {
						labels.add(val);
					}else {
						if(labels.size() > 0) {
							hm.put(labels.get(count), val);
						}
					}
					count += 1;
				}

				if(row.getRowNum() > 1) {
					hmlist.add(hm);
				}
				
				System.out.println("");
				
			}

			inputStream.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

        return hmlist;

	}

}
