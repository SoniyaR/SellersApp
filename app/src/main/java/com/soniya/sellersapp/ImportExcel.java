package com.soniya.sellersapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.android.gms.common.api.Api;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ImportExcel extends AppCompatActivity {

    //private static final int REQUEST_CODE_DOC = 1;
    private static final int READ_REQUEST_CODE = 1;
    List<HashMap<String, Object>> hmList = new ArrayList<>();
    ListView carsList;
    SimpleAdapter adapter;
    ArrayList<String> activeOrders = new ArrayList<>();
    Button importButton;

    FirebaseDataFactory database;

   /* @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

        }
    }
*/

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.fileimport_menu, menu);
        menu.setHeaderTitle("Select Option");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.excelImport)    {
            Log.i("soni-importpage", "selected Excel");
            browseClicked();
            return true;
        }else if(item.getItemId() == R.id.something)    {
            Log.i("soni-importpage", "selected Something");
            return true;
        }else {
            return super.onContextItemSelected(item);
        }
    }

    public void onImportTextClicked(View view)  {
        this.openContextMenu(view);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_excel);

        setTitle("Import Excel File");

        importButton = (Button) findViewById(R.id.importExcelButton);
        importButton.setVisibility(View.VISIBLE);

        TextView importTextView = findViewById(R.id.importFileTextView);
        registerForContextMenu(importTextView);

        database = new FirebaseDataFactory();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        /*String[] from = {"model_name", "sellingprice", "location", "carImage", "availability"};
        int[] to = {R.id.modelName, R.id.sellingprice, R.id.location, R.id.carImageView, R.id.availabilityText};


        carsList = findViewById(R.id.importedDataView);
        carsList.setVisibility(View.INVISIBLE);
        activeOrders.clear();
        adapter = new SimpleAdapter(this, hmList, R.layout.carslist_layout, from, to);
        adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) {
                if(view instanceof ImageView && view.getId() == R.id.carImageView && data instanceof Bitmap){
                    //R.id.userImageView

                    ImageView imgV = (ImageView) view;
                    imgV.setImageBitmap((Bitmap)data);

                }else if(view instanceof TextView && data instanceof String){
                    TextView textV = (TextView) view;
                    textV.setText((String) data);
                }
                return true;
            }
        });

        carsList.setAdapter(adapter);
*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == READ_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            //carsList.setVisibility(View.VISIBLE);
            //importButton.setVisibility(View.INVISIBLE);

            Uri uri = data.getData();
            //read excel file and assign retrieved hmlist to current hmlist
            Log.i("soni-", "here to read this file " + uri.getPath().split(":")[1]);

           // List<HashMap<String, String>> hmlist = new ArrayList();
            String filePath = uri.getPath().split(":")[1].trim();
            FileInputStream inputStream = null;
            try {
                inputStream = new FileInputStream(filePath);
                //OPCPackage opcPackage = OPCPackage.open(filePath);

                Iterator<Row> rowIterator = null;
                if(filePath.endsWith("xlsx")) {
                    XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
                    XSSFSheet sheet = workbook.getSheetAt(0);
                    rowIterator = sheet.iterator();
                }else if(filePath.endsWith("xls")){
                    HSSFWorkbook workbook = new HSSFWorkbook(inputStream);
                    HSSFSheet sheet = workbook.getSheetAt(0);
                    rowIterator = sheet.iterator();
                }

                ArrayList<String> labels = new ArrayList();
                hmList.clear();

                if(rowIterator !=null) {
                    while (rowIterator.hasNext()) {

                        HashMap<String, Object> hm = new HashMap();

                        Row row = rowIterator.next();

                        //System.out.println(row.getRowNum());
                        //if(row.getRowNum() == 1) {
                        //	continue;
                        //}

                        Iterator<Cell> cellIterator = row.cellIterator();

                        String val = "";
                        int count = 0;

                        while (cellIterator.hasNext()) {

                            Cell cell = cellIterator.next();
                            switch (cell.getCellType()) {

                                case NUMERIC:
                                    System.out.print(cell.getNumericCellValue() + "\t");
                                    val = String.valueOf(cell.getNumericCellValue());
                                    break;

                                case STRING:
                                    System.out.print(cell.getStringCellValue() + "\t");
                                    val = cell.getStringCellValue();
                                    break;

                            }
                            if (row.getRowNum() == 1) {
                                labels.add(val);
                            } else {
                                if (labels.size() > 0) {
                                    hm.put(labels.get(count), val);
                                }
                            }
                            count += 1;
                        }

                        if (row.getRowNum() > 1) {
                            hmList.add(hm);
                        }

                        System.out.println("");

                    }
                }

                HashMap<String, Object> hashMap = hmList.get(0);
                Set<String> keys = hashMap.keySet();
                Iterator<String> it = keys.iterator();
                while(it.hasNext()){
                    Log.i("soni--->", it.next().toString());

                }

                inputStream.close();

                //call upload data method of Firebasedatafactory
                //database.uploadImportData(hmList);

            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            finally {
                if(inputStream !=null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        Log.i("soni-", "failed closing resources");
                        e.printStackTrace();
                    }
                }
            }

           // adapter.notifyDataSetChanged();

            //if all is good, go to homepage with new content uploaded
            //else show error

        }
    }

    public void browseClicked(){

        Log.i("soni-", "BROWSE clicked");

        /*
        Uri selectedUri = Uri.parse(Environment.getExternalStorageDirectory() + "/myfolder/");


        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(selectedUri, "resource/folder");
        if(i.resolveActivityInfo(getPackageManager(), 0) !=null )   {
            startActivity(i);
        }else{
            //file explorer not found
            Log.i("soni-", "file explorer not found");
        }

        */

//        String[] mimeTypes =
//                {"application/msword","application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
//                        "application/vnd.ms-powerpoint","application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
//                        "application/vnd.ms-excel","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
//                        "text/plain",
//                        "application/pdf",
//                        "application/zip"};
//
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
//            if (mimeTypes.length > 0) {
//                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
//            }
//        } else {
//            String mimeTypesStr = "";
//            for (String mimeType : mimeTypes) {
//                mimeTypesStr += mimeType + "|";
//            }
//            intent.setType(mimeTypesStr.substring(0,mimeTypesStr.length() - 1));
//        }
//
//        startActivityForResult(Intent.createChooser(intent,"ChooseFile"), REQUEST_CODE_DOC);

        //startActivityForResult(i, Intent.ACTION_PICK_ACTIVITY);



        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        intent.setType("*/*");

        startActivityForResult(intent, READ_REQUEST_CODE);

    }
}
