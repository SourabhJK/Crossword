package com.felight.crossword;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class MainActivity extends AppCompatActivity {


    private int gridNoOfColumn =0;
    private int gridNoOfRows =0;
    private static int score = 0;
    private int switchForSubmit = 0;

    private long startTime=0;
    private long totalTime=0;

    private String url;
    private String jsonStr;

    private ArrayList<String[]> crosswordLines = new ArrayList();
    private ArrayList<String> crosswordAnswers = new ArrayList<>();
    private ArrayList<String> keysForRows = new ArrayList<>();
    private ArrayList<String> keysForColumns = new ArrayList<>();
    private ArrayList<String> markingDetailsArray = new ArrayList<>();
    private ArrayList<EditText> arrayOfEnteredValues = new ArrayList<>();
    private ArrayList<HashMap<String,String>> hintAcross = new ArrayList<>();
    private ArrayList<HashMap<String,String>> hintDownward = new ArrayList<>();

    private LinearLayout verticalLayout;
    private LinearLayout llMain;
    private LinearLayout llStart;

    private ProgressDialog progressDialog;

    private ListView lvHints;

    private Button btnHint;
    private Button btnStart;
    private Button btnSubmit;

    private TextView tvHintDisplay;

    private hintClickListener mListener;

    private boolean isStartClicked = false;

    private static int startButtonCounter = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.mListener = null;


     //   url="http://cogniscoop.com/jowist/phpadminpanel/crossword/cWrdData.json";
        url="http://jowist.com/phpadminpanel/crossword/LCM_and_HCF_lcmandhcf_grade8.json";
     //   url="http://jowist.com/phpadminpanel/crossword/Square%20Root_squareroot_grade8.json";

        llMain = (LinearLayout) findViewById(R.id.llMain);
        llStart = (LinearLayout) findViewById(R.id.llStart);

        lvHints = (ListView) findViewById(R.id.lvHints);

        btnHint = (Button) findViewById(R.id.btnHint);
        btnStart = (Button) findViewById(R.id.btnStart);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);


        startButtonCounter=0;

        arrayOfEnteredValues = new ArrayList<>();

        tvHintDisplay = (TextView) findViewById(R.id.tvHintDisplay);

        tvHintDisplay.setText("Tap twice for the hint");

        score=0;

        btnSubmit.setEnabled(false);


        llMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isStartClicked)
                    Toast.makeText(getBaseContext(),"Click on Start to start crossword",Toast.LENGTH_SHORT).show();
            }
        });

        btnHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mListener!=null) {
                    mListener.hintClick(true);
                } else {
                    Toast.makeText(MainActivity.this, "Double tap on position where you want the hint", Toast.LENGTH_SHORT).show();
                }
            }
        });


        llMain.setAlpha(0.5f);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(true);
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.show();

//        btnHelp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getBaseContext(),HelpActivity.class);
//                startActivity(intent);
//            }
//        });

        new GetJSON().execute(url);
//        createJSONArray();
//        createCrosswordPuzzle();
//        HintsAcrossDownwardAdapter hintsAcrossDownwardAdapter = new HintsAcrossDownwardAdapter(MainActivity.this,hintAcross,hintDownward);
//        lvHints.setAdapter(hintsAcrossDownwardAdapter);



    }


    //AsyncTask to get the JSON data from the url
    private class GetJSON extends AsyncTask<String, Void, Void>{
        @Override
        protected Void doInBackground(String... urlReq) {
            HttpHandler httpHandler = new HttpHandler();
            jsonStr = httpHandler.makeServiceCall(urlReq[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            createJSONArray();
            if(jsonStr!=null){
                createCrosswordPuzzle();
            }else{
              Toast toast =  Toast.makeText(MainActivity.this,"Please check your internet connection",Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
                Intent intent = new Intent(MainActivity.this,ErrorDisplay.class);
                startActivity(intent);

            }

            HintsAcrossDownwardAdapter hintsAcrossDownwardAdapter = new HintsAcrossDownwardAdapter(MainActivity.this,hintAcross,hintDownward);
            lvHints.setAdapter(hintsAcrossDownwardAdapter);
            progressDialog.dismiss();
        }
    }


    public void createJSONArray(){

//        String jsonStr = "{\n" +
//                "    \"data\": [\n" +
//                "        [\"*\", \"*\", \"*\", \"*\", \"*\", \"*\", \"E\", \"*\", \"*\", \"*\"],\n" +
//                "        [\"*\", \"*\", \"*\", \"*\", \"*\", \"F\", \"I\", \"V\", \"E\", \"*\"],\n" +
//                "        [\"*\", \"*\", \"*\", \"*\", \"*\", \"*\", \"G\", \"*\", \"*\", \"*\"],\n" +
//                "        [\"P\", \"O\", \"I\", \"N\", \"T\", \"*\", \"H\", \"*\", \"*\", \"*\"],\n" +
//                "        [\"*\", \"*\", \"*\", \"*\", \"E\", \"*\", \"T\", \"*\", \"*\", \"*\"],\n" +
//                "        [\"*\", \"*\", \"M\", \"O\", \"N\", \"E\", \"Y\", \"*\", \"P\", \"*\"],\n" +
//                "        [\"*\", \"*\", \"*\", \"N\", \"*\", \"*\", \"*\", \"*\", \"A\", \"*\"],\n" +
//                "        [\"S\", \"E\", \"V\", \"E\", \"N\", \"T\", \"Y\", \"S\", \"I\", \"X\"],\n" +
//                "        [\"I\", \"*\", \"*\", \"*\", \"*\", \"*\", \"*\", \"*\", \"S\", \"*\"],\n" +
//                "        [\"X\", \"*\", \"*\", \"*\", \"R\", \"U\", \"P\", \"E\", \"E\", \"S\"]\n" +
//                "    ],\n" +
//                "    \"Hints\": [{ \"strow\": 0, \"stcol\": 6, \"endrow\": 5, \"endcol\": 6, \"hint\": \"8000 paise is equal to ______ rupees\" }, { \"strow\": 1, \"stcol\": 5, \"endrow\": 1, \"endcol\": 8, \"hint\": \"Maximum notes of Rs 20 that can be used to make Rs 100\" }, { \"strow\": 3, \"stcol\": 0, \"endrow\": 3, \"endcol\": 4, \"hint\": \"While performing the addition and subtraction of money, we have to place the ______ one below other\" }, { \"strow\": 3, \"stcol\": 4, \"endrow\": 5, \"endcol\": 4, \"hint\": \"Number of 50 rupees notes in 500 rupees\" }, { \"strow\": 5, \"stcol\": 2, \"endrow\": 5, \"endcol\": 6, \"hint\": \"In India, rupee is the unit of ______\" }, {\n" +
//                "        \"strow\": 5,\n" +
//                "        \"stcol\": 3,\n" +
//                "        \"endrow\": 7,\n" +
//                "        \"endcol\": 3,\n" +
//                "        \"hint\": \"100 paise is equal to ______ rupee\"\n" +
//                "    }, { \"strow\": 5, \"stcol\": 8, \"endrow\": 9, \"endcol\": 8, \"hint\": \"Point separates rupees from _______\" }, { \"strow\": 7, \"stcol\": 0, \"endrow\": 9, \"endcol\": 0, \"hint\": \"Number of 5 rupees notes in 30 rupees\" }, { \"strow\": 7, \"stcol\": 0, \"endrow\": 7, \"endcol\": 9, \"hint\": \"How many paise in Rs. 104.76\" }, { \"strow\": 9, \"stcol\": 4, \"endrow\": 9, \"endcol\": 9, \"hint\": \"In precise form of writing money, numbers left to dot represents the number of ______\" }],\n" +
//                "    \"Markings\": { \"0\": { \"6\": 1 }, \"1\": { \"5\": 2 }, \"3\": { \"0\": 3, \"4\": 4 }, \"5\": { \"2\": 5, \"3\": 6, \"8\": 7 }, \"7\": { \"0\": 8, \"0\": 8 }, \"9\": { \"4\": 10 } },\n" +
//                "    \"img\": [{\n" +
//                "            \"name\": \"img1\",\n" +
//                "            \"src\": \"headerfiles/money.bmp\",\n" +
//                "            \"top\": 0,\n" +
//                "            \"left\": 0,\n" +
//                "            \"height\": 3,\n" +
//                "            \"width\": 5\n" +
//                "        },\n" +
//                "        {\n" +
//                "            \"name\": \"img2\",\n" +
//                "            \"src\": \"headerfiles/coin.bmp\",\n" +
//                "            \"top\": 3,\n" +
//                "            \"left\": 8,\n" +
//                "            \"height\": 2,\n" +
//                "            \"width\": 2\n" +
//                "        }\n" +
//                "    ],\n" +
//                "    \"recid\": 1\n" +
//                "}";

 //       jsonStr="{\"data\" : [[\"P\",\"E\",\"R\",\"I\",\"M\",\"E\",\"T\",\"E\",\"R\",\"*\"],[\"*\",\"*\",\"*\",\"*\",\"*\",\"*\",\"*\",\"*\",\"U\",\"*\"],[\"F\",\"A\",\"L\",\"S\",\"E\",\"*\",\"*\",\"*\",\"L\",\"*\"],[\"*\",\"D\",\"*\",\"I\",\"*\",\"*\",\"F\",\"*\",\"E\",\"*\"],[\"*\",\"D\",\"*\",\"X\",\"*\",\"F\",\"O\",\"U\",\"R\",\"*\"],[\"*\",\"*\",\"*\",\"T\",\"*\",\"*\",\"U\",\"*\",\"*\",\"*\"],[\"*\",\"*\",\"*\",\"E\",\"*\",\"*\",\"R\",\"*\",\"*\",\"*\"],[\"*\",\"*\",\"L\",\"E\",\"N\",\"G\",\"T\",\"H\",\"*\",\"T\"],[\"*\",\"*\",\"*\",\"N\",\"*\",\"*\",\"E\",\"*\",\"*\",\"R\"],[\"*\",\"*\",\"*\",\"*\",\"*\",\"*\",\"E\",\"*\",\"*\",\"U\"],[\"*\",\"R\",\"E\",\"C\",\"T\",\"A\",\"N\",\"G\",\"L\",\"E\"]], \"Hints\" : [ {\"strow\":0,\"stcol\":0,\"endrow\":0,\"endcol\":8,\"hint\":\"Distance around of a shape.\"},{\"strow\":0,\"stcol\":8,\"endrow\":4,\"endcol\":8,\"hint\":\"We measure length with this tool.\"},{\"strow\":2,\"stcol\":0,\"endrow\":2,\"endcol\":4,\"hint\":\"Perimeter and Area of a rectangle are same.\"},{\"strow\":2,\"stcol\":1,\"endrow\":4,\"endcol\":1,\"hint\":\"To find perimeter of a shape, ______ length of all sides.\"},{\"strow\":2,\"stcol\":3,\"endrow\":8,\"endcol\":3,\"hint\":\"If side of square is 4cm, perimeter is ______ cm.\"},{\"strow\":3,\"stcol\":6,\"endrow\":10,\"endcol\":6,\"hint\":\"If sides of a rectangle are 2 cm and 5 cm, perimeter of rectangle is ______ cm.\"},{\"strow\":4,\"stcol\":5,\"endrow\":4,\"endcol\":8,\"hint\":\"Perimeter of a square is ______ times of its length.\"},{\"strow\":7,\"stcol\":2,\"endrow\":7,\"endcol\":7,\"hint\":\"Perimeter is expressed in units of ______.\"},{\"strow\":7,\"stcol\":9,\"endrow\":10,\"endcol\":9,\"hint\":\"Square is a rectangle.\"},{\"strow\":10,\"stcol\":1,\"endrow\":10,\"endcol\":9,\"hint\":\"Perimeter of ______ is two times the sum of length and width.\"}], \"Markings\": { \"0\": {\"0\":1, \"8\":2}, \"2\": {\"0\":3, \"1\":4, \"3\":5}, \"3\": {\"6\":6}, \"4\": {\"5\":7}, \"7\": {\"2\":8, \"9\":9}, \"10\": {\"1\":10} }}";

 //       jsonStr="{\"data\" : [[\"P\",\"E\",\"R\",\"I\",\"O\",\"D\",\"*\",\"*\",\"*\",\"*\"],[\"*\",\"*\",\"*\",\"*\",\"D\",\"*\",\"*\",\"N\",\"*\",\"R\"],[\"*\",\"M\",\"*\",\"*\",\"D\",\"I\",\"V\",\"I\",\"D\",\"E\"],[\"*\",\"U\",\"*\",\"*\",\"*\",\"*\",\"*\",\"N\",\"*\",\"M\"],[\"*\",\"L\",\"*\",\"*\",\"*\",\"*\",\"*\",\"E\",\"*\",\"A\"],[\"*\",\"T\",\"E\",\"N\",\"*\",\"O\",\"*\",\"*\",\"*\",\"I\"],[\"*\",\"I\",\"*\",\"*\",\"*\",\"N\",\"*\",\"*\",\"*\",\"N\"],[\"*\",\"P\",\"E\",\"R\",\"F\",\"E\",\"C\",\"T\",\"*\",\"D\"],[\"*\",\"L\",\"*\",\"*\",\"A\",\"*\",\"*\",\"W\",\"*\",\"E\"],[\"*\",\"Y\",\"*\",\"*\",\"L\",\"*\",\"F\",\"O\",\"U\",\"R\"],[\"*\",\"*\",\"*\",\"*\",\"S\",\"*\",\"*\",\"*\",\"*\",\"*\"],[\"*\",\"I\",\"N\",\"V\",\"E\",\"R\",\"S\",\"E\",\"*\",\"*\"]], \"Hints\" : [ {\"strow\":0,\"stcol\":0,\"endrow\":0,\"endcol\":5,\"hint\":\"Pair made during long division method is called _______.\"},{\"strow\":0,\"stcol\":4,\"endrow\":2,\"endcol\":4,\"hint\":\"Every square number is sum of first n _____ natural numbers \"},{\"strow\":1,\"stcol\":7,\"endrow\":4,\"endcol\":7,\"hint\":\"______ is square root of greatest perfect square of two-digit number.\"},{\"strow\":1,\"stcol\":9,\"endrow\":9,\"endcol\":9,\"hint\":\"If the final ______ is not zero in long division method, the given number is not a perfect square.\"},{\"strow\":2,\"stcol\":1,\"endrow\":9,\"endcol\":1,\"hint\":\"If we ______ a number by its unpaired prime factor (s), we get a perfect square\"},{\"strow\":2,\"stcol\":4,\"endrow\":2,\"endcol\":9,\"hint\":\"If we ______ a number by its unpaired prime factor (s), we get a perfect square \"},{\"strow\":5,\"stcol\":1,\"endrow\":5,\"endcol\":3,\"hint\":\"Square root of smallest three-digits number is ______.\"},{\"strow\":5,\"stcol\":5,\"endrow\":7,\"endcol\":5,\"hint\":\"Square root of any positive number converges towards _______ .\"},{\"strow\":7,\"stcol\":1,\"endrow\":7,\"endcol\":7,\"hint\":\"If prime factors of a numbers do not occur in pairs, given number is not a ______ square.\"},{\"strow\":7,\"stcol\":4,\"endrow\":11,\"endcol\":4,\"hint\":\"41564083 is perfect square. (True / False)\"},{\"strow\":7,\"stcol\":7,\"endrow\":9,\"endcol\":7,\"hint\":\"Four digits perfect square will have ______ digits in its square root.\"},{\"strow\":9,\"stcol\":6,\"endrow\":9,\"endcol\":9,\"hint\":\"Seven digits perfect square will have ______ digits in its square root.\"},{\"strow\":11,\"stcol\":1,\"endrow\":11,\"endcol\":7,\"hint\":\"Square root is ______ operation of square.\"}], \"Markings\": { \"0\": {\"0\":1, \"4\":2}, \"1\": {\"7\":3, \"9\":4}, \"2\": {\"1\":5, \"4\":6}, \"5\": {\"1\":7, \"5\":8}, \"7\": {\"1\":9, \"4\":10, \"7\":11}, \"9\": {\"6\":12}, \"11\": {\"1\":13} }}";

//        jsonStr="{\"data\" : [[\"P\",\"E\",\"R\",\"I\",\"O\",\"D\",\"*\",\"*\",\"*\",\"*\"],[\"*\",\"*\",\"*\",\"*\",\"D\",\"*\",\"*\",\"N\",\"*\",\"R\"],[\"*\",\"M\",\"*\",\"*\",\"D\",\"I\",\"V\",\"I\",\"D\",\"E\"],[\"*\",\"U\",\"*\",\"*\",\"*\",\"*\",\"*\",\"N\",\"*\",\"M\"],[\"*\",\"L\",\"*\",\"*\",\"*\",\"*\",\"*\",\"E\",\"*\",\"A\"],[\"*\",\"T\",\"E\",\"N\",\"*\",\"O\",\"*\",\"*\",\"*\",\"I\"],[\"*\",\"I\",\"*\",\"*\",\"*\",\"N\",\"*\",\"*\",\"*\",\"N\"],[\"*\",\"P\",\"E\",\"R\",\"F\",\"E\",\"C\",\"T\",\"*\",\"D\"],[\"*\",\"L\",\"*\",\"*\",\"A\",\"*\",\"*\",\"W\",\"*\",\"E\"],[\"*\",\"Y\",\"*\",\"*\",\"L\",\"*\",\"F\",\"O\",\"U\",\"R\"],[\"*\",\"*\",\"*\",\"*\",\"S\",\"*\",\"*\",\"*\",\"*\",\"*\"],[\"*\",\"I\",\"N\",\"V\",\"E\",\"R\",\"S\",\"E\",\"*\",\"*\"]], \"Hints\" : [ {\"strow\":0,\"stcol\":0,\"endrow\":0,\"endcol\":5,\"hint\":\"Pair made during long division method is called _______.\"},{\"strow\":0,\"stcol\":4,\"endrow\":2,\"endcol\":4,\"hint\":\"Every square number is sum of first n _____ natural numbers \"},{\"strow\":1,\"stcol\":7,\"endrow\":4,\"endcol\":7,\"hint\":\"______ is square root of greatest perfect square of two-digit number.\"},{\"strow\":1,\"stcol\":9,\"endrow\":9,\"endcol\":9,\"hint\":\"If the final ______ is not zero in long division method, the given number is not a perfect square.\"},{\"strow\":2,\"stcol\":1,\"endrow\":9,\"endcol\":1,\"hint\":\"If we ______ a number by its unpaired prime factor (s), we get a perfect square\"},{\"strow\":2,\"stcol\":4,\"endrow\":2,\"endcol\":9,\"hint\":\"If we ______ a number by its unpaired prime factor (s), we get a perfect square \"},{\"strow\":5,\"stcol\":1,\"endrow\":5,\"endcol\":3,\"hint\":\"Square root of smallest three-digits number is ______.\"},{\"strow\":5,\"stcol\":5,\"endrow\":7,\"endcol\":5,\"hint\":\"Square root of any positive number converges towards _______ .\"},{\"strow\":7,\"stcol\":1,\"endrow\":7,\"endcol\":7,\"hint\":\"If prime factors of a numbers do not occur in pairs, given number is not a ______ square.\"},{\"strow\":7,\"stcol\":4,\"endrow\":11,\"endcol\":4,\"hint\":\"41564083 is perfect square. (True / False)\"},{\"strow\":7,\"stcol\":7,\"endrow\":9,\"endcol\":7,\"hint\":\"Four digits perfect square will have ______ digits in its square root.\"},{\"strow\":9,\"stcol\":6,\"endrow\":9,\"endcol\":9,\"hint\":\"Seven digits perfect square will have ______ digits in its square root.\"},{\"strow\":11,\"stcol\":1,\"endrow\":11,\"endcol\":7,\"hint\":\"Square root is ______ operation of square.\"}], \"Markings\": { \"0\": {\"0\":1, \"4\":2}, \"1\": {\"7\":3, \"9\":4}, \"2\": {\"1\":5, \"4\":6}, \"5\": {\"1\":7, \"5\":8}, \"7\": {\"1\":9, \"4\":10, \"7\":11}, \"9\": {\"6\":12}, \"11\": {\"1\":13} }}";



        if(jsonStr != null){
            try{
                JSONObject jsonObject = new JSONObject(jsonStr);
                JSONArray jsonArrayData = jsonObject.getJSONArray("data"); // JSONArray of Data
                JSONArray jsonArrayHints = jsonObject.getJSONArray("Hints"); //JSONArray of Hints
                JSONObject jsonObjectsMarkingRows = jsonObject.getJSONObject("Markings"); //JSONArray of Markings
                Log.i(("jsonOject"),""+jsonObjectsMarkingRows);
                Log.i("hints",""+jsonArrayHints);

                gridNoOfRows =jsonArrayData.length();  //No. of rows in the grid

                // To parse the grid details into ArrayList:crosswardLines

                for(int i=0;i<jsonArrayData.length();i++){
                    JSONArray crosswordArray = jsonArrayData.getJSONArray(i);
                    gridNoOfColumn =crosswordArray.length(); // No. of columns in the grid
                    String[] gridDetails = new String[gridNoOfColumn];
                    for(int j=0; j<crosswordArray.length();j++){
                        gridDetails[j]=crosswordArray.getString(j);
                    }
                    crosswordLines.add(gridDetails);
                }


                //To parse the details about markings into ArrayList: markings
                getJSONKeys(jsonObjectsMarkingRows,keysForRows);
                int tempCounter=0;
                TreeMap<Integer,TreeMap<Integer,Integer>> markingRowColumn = new TreeMap<>();
                for(int i=0;i<keysForRows.size();i++) {
                    JSONObject jsonObjectMarkingColumn = jsonObjectsMarkingRows.getJSONObject(""+keysForRows.get(i)); //Row Values
                    getJSONKeys(jsonObjectMarkingColumn, keysForColumns); //Column Values
                    TreeMap<Integer,Integer> markingColumnValue = new TreeMap<>();
                    int x = Integer.parseInt(keysForRows.get(i));
                    for (int j = tempCounter; j < keysForColumns.size(); j++) {

                        int y = Integer.parseInt(keysForColumns.get(tempCounter++));
                        String trialInput = jsonObjectMarkingColumn.getString(""+y);
                        Log.i(">>>MainActivity", "String x:" + x + "\n" + "String y:" + y + "\n" + "MartketValue:" + trialInput);
                        int markingValue = Integer.parseInt(trialInput);
                        markingColumnValue.put(y,markingValue);
                    }
                    markingRowColumn.put(x,markingColumnValue);
                }



                //Trying with treeMap so that it compatible with jelly bean as well
                ArrayList<Integer> rowTreeKeys = new ArrayList<>();
                ArrayList<Integer> columnTreeKeys = new ArrayList<>();
                getJSONKeys(markingRowColumn,rowTreeKeys);
                tempCounter=0;

                for(int i=0;i<rowTreeKeys.size();i++) {
                    TreeMap rowTree = markingRowColumn.get(rowTreeKeys.get(i));
                    getJSONKeys(rowTree, columnTreeKeys); //Column Values



                    for (int j = tempCounter; j < columnTreeKeys.size(); j++) {
                        int x = rowTreeKeys.get(i);
                        int y = columnTreeKeys.get(tempCounter++);
                        String markingValue = ""+rowTree.get(y);
                      //  Log.i(">>>MainActivity", "String x:" + x + "\n" + "String y:" + y + "\n" + "MartketValue:" + markingValue);

                        markingDetailsArray.add(""+x);
                        markingDetailsArray.add(""+y);
                        markingDetailsArray.add(markingValue);
                    }
                }

                    Log.e("checking",markingDetailsArray.toString());
                //To parse the hints into ArrayList: hints
                int counterForMarkingArray=0;
                for(int i=0; i<jsonArrayHints.length();i++){
                    JSONObject crossWordHints = jsonArrayHints.getJSONObject(i);
                    String hint = crossWordHints.getString("hint");
                    String strow = crossWordHints.getString("strow");
                    String stcol = crossWordHints.getString("stcol");
                    String endrow = crossWordHints.getString("endrow");
                    String endcol = crossWordHints.getString("endcol");

                    if(strow.equals(endrow)){ // To add Across hint
                        HashMap<String,String> hintDetailsAcross = new HashMap<>();
                        String row = markingDetailsArray.get(counterForMarkingArray);
                        String column = markingDetailsArray.get(counterForMarkingArray+1);
                        if(strow.equals(row)&&stcol.equals(column)){
                            hintDetailsAcross.put("Marking",markingDetailsArray.get(counterForMarkingArray+2));
                            hintDetailsAcross.put("Hint",hint);
                            counterForMarkingArray=counterForMarkingArray+3;
                            hintAcross.add(hintDetailsAcross);
                        }
                    }

                    else if(stcol.equals(endcol)){ // To add Downward hint
                        HashMap<String,String> hintDetailsDownwards = new HashMap<>();
                        String row = markingDetailsArray.get(counterForMarkingArray);
                        String column = markingDetailsArray.get(counterForMarkingArray+1);
                        if(strow.equals(row)&&stcol.equals(column)){
                            hintDetailsDownwards.put("Marking",markingDetailsArray.get(counterForMarkingArray+2));
                            hintDetailsDownwards.put("Hint",hint);
                            counterForMarkingArray=counterForMarkingArray+3;
                            hintDownward.add(hintDetailsDownwards);
                        }
                    }
                }
                Log.i("Across:  ",hintAcross.toString());
                Log.i("Down:  ",hintDownward.toString());
            }catch (final JSONException e){
                // Toast.makeText(getApplicationContext(),"Json parsing error: " + e.getMessage(),Toast.LENGTH_LONG).show();
            }
        }
    }

    public void createCrosswordPuzzle(){ //To create the puzzle grid

        LinearLayout.LayoutParams layoutParamas = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        verticalLayout = new LinearLayout(this);
        verticalLayout.setLayoutParams(layoutParamas);
        verticalLayout.setOrientation(LinearLayout.VERTICAL);

        final LinearLayout[] horizontalLayout = new LinearLayout[gridNoOfRows];
        String[] rowDetails;
        String trial;

        //To create the grid for crossword
        int temp=0;
        int idCounter=0;
        for(int i = 0; i< gridNoOfRows; i++){
            rowDetails = crosswordLines.get(i);
            horizontalLayout[i] = new LinearLayout(this);
            horizontalLayout[i].setLayoutParams(layoutParamas);
            horizontalLayout[i].setOrientation(LinearLayout.HORIZONTAL);
            horizontalLayout[i].setId(i);

            for(int j = 0; j< gridNoOfColumn; j++){
                trial=rowDetails[j];
                LayoutInflater layoutInflater =  (LayoutInflater) this.getSystemService(this.LAYOUT_INFLATER_SERVICE);
                View editable = layoutInflater.inflate(R.layout.editable_layout_for_crossword,null,true);
                editable.setId(idCounter++);

                TextView tvMarking = (TextView) editable.findViewById(R.id.tvMarking);
                final EditText etInput = (EditText) editable.findViewById(R.id.etInput);
                horizontalLayout[i].addView(editable);
                if(trial!=null){
                    if(trial.equals("*")){
                        etInput.setBackgroundResource(R.drawable.rechantle_black);
                        etInput.setEnabled(false);
                        etInput.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                    }else {

                        etInput.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                        Log.i("editTextId","  "+etInput.getId());
                        if(isStartClicked){
                            etInput.setEnabled(true);
                        }
                        else {
                            etInput.setEnabled(false);
                        }

                        final String finalTrial = trial;
                        final int finalI = i;
                        final int finalJ = j;
//                        etInput.setOnClickListener(new View.OnClickListener() {
//
//                            @Override
//                            public void onClick(View view) {
//                                int etId = etInput.getId();
//
//
//                                for(int k=0;k<markingDetailsArray.size();){
//                                    if(markingDetailsArray.get(k).equals(""+ finalI)){
//                                        if (markingDetailsArray.get(k+1).equals(""+ finalJ)){
//                                            tvHintDisplay.setText(getHint(markingDetailsArray.get(k+2)));
//                                            Toast.makeText(getBaseContext(),"Tap twice to get the hints",Toast.LENGTH_SHORT).show();
//                                            break;
//                                        }
//                                    }
//                                    k=k+3;
//                                }
//                                setmListener(new hintClickListener() {
//
//                                    @Override
//                                    public void hintClick(boolean isclicked) {
//
//                                        if(isclicked) {
//                                            String id=""+etInput.getId();
//                                            if(id.equals("")){
//                                                Toast.makeText(getBaseContext(),"Please click on the position you want the hint",Toast.LENGTH_SHORT).show();
//                                            }
//                                            else{
//                                                if(etInput.getText().toString().equals("")){
//                                                    etInput.setHint(finalTrial);
//                                                    etInput.setEnabled(false);
//                                                }
//                                            }
//                                        }
//                                    }
//                                });
//
//                            }
//                        });


                        etInput.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                int etId = etInput.getId();

                                etInput.addTextChangedListener(new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                    }

                                    @Override
                                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                                    }

                                    @Override
                                    public void afterTextChanged(Editable s) {

                                        etInput.requestFocus();
                                    }
                                });


                                for (int k = 0; k < markingDetailsArray.size(); ) {
                                    if (markingDetailsArray.get(k).equals("" + finalI)) {
                                        if (markingDetailsArray.get(k + 1).equals("" + finalJ)) {
                                            tvHintDisplay.setText(getHint(markingDetailsArray.get(k + 2)));
                                            Toast.makeText(getBaseContext(), "Tap twice to get the hints", Toast.LENGTH_SHORT).show();
                                            break;
                                        }
                                    }
                                    k = k + 3;
                                }
                                setmListener(new hintClickListener() {

                                    @Override
                                    public void hintClick(boolean isclicked) {

                                        if (isclicked) {
                                            String id = "" + etInput.getId();
                                            if (id.equals("")) {
                                                Toast.makeText(getBaseContext(), "Please click on the position you want the hint", Toast.LENGTH_SHORT).show();
                                            } else {
                                                if (etInput.getText().toString().equals("")) {
                                                    etInput.setHint(finalTrial);
                                                    etInput.setEnabled(false);
                                                }
                                            }
                                        }
                                    }
                                });

                            }
                        });

                        // Marking Details

                        if(temp < markingDetailsArray.size()){
                            String row = markingDetailsArray.get(temp);
                            String column = markingDetailsArray.get(temp+1);
                            if((row!=null)&&(column!=null)){
                                if((row.equals(""+i))&&(column.equals(""+j))){
                                    tvMarking.setText(markingDetailsArray.get(temp+2));
                                    temp=temp+3;
                                }
                                else{
                                    tvMarking.setText("");
                                }
                            }
                        }
                    }
                }
                arrayOfEnteredValues.add(etInput);
                crosswordAnswers.add(trial);
            }
            verticalLayout.addView(horizontalLayout[i]);
        }
        llMain.addView(verticalLayout);

    }


    // Listener to check if hint button clicked
    public void setmListener(hintClickListener listener) {
        this.mListener = listener;
    }

    //Interface to show the hints when hint button is clicked
    public interface hintClickListener {
        void hintClick(boolean isclicked);
    }



    //To get the Column and Row value which are keys in the JSON File
    public void getJSONKeys(JSONObject jsonObject, ArrayList arrayList){ // To get the keys of JSONObject Marking
        Iterator<?> keys = jsonObject.keys();
        while (keys.hasNext()){
            arrayList.add(keys.next());
        }
    }

    public void getJSONKeys(TreeMap map, ArrayList arrayList){ // To get the keys of TreeSet
        Set<Integer> keys = map.keySet();

        for(Integer key: keys){
            arrayList.add(key);
        }

        }



    //To calculate the time taken and total score
    public void timeCalculation(View view){

        switch (view.getId()){
            case R.id.btnStart: //To start the timer
                if(startButtonCounter==0){
                    startTime = System.currentTimeMillis();
                    switchForSubmit=1;
                    isStartClicked=true;
                    llMain.removeAllViewsInLayout();
                    createCrosswordPuzzle();
                    llMain.setAlpha(1.0f);
                    btnStart.setText("Restart");
                    startButtonCounter=1;
                    btnSubmit.setEnabled(true);
                }
                else{
                    new AlertDialog.Builder(this).setTitle("Restart").setMessage("Are you sure you want to Restart the puzzle?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    startTime = System.currentTimeMillis();
                                    switchForSubmit=1;
                                    isStartClicked=true;
                                    llMain.removeAllViewsInLayout();
                                    createCrosswordPuzzle();
                                    resetEnteredValues(); // Resets all the entered values
                                    score=0;
                                    btnSubmit.setEnabled(true);
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })
                            .show();
                }

                break;

            case R.id.btnSubmit: //To calculate the score
                totalTime = System.currentTimeMillis()-startTime;
                score=0;
               new  AlertDialog.Builder(this).setTitle("Submit").setMessage("Are you sure you want to submit?")
                       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialogInterface, int i) {
                               if(switchForSubmit==1){
                                   //long totalTime = System.currentTimeMillis()-startTime;
                                   long seconds = (totalTime/1000)%60; //To get the seconds value
                                   totalTime = totalTime/(1000*60); // To get minutes value

                                   //llResult.setVisibility(View.VISIBLE);
                                   getScore(); //Calling the method to Calculate the score
                                   //To display total time taken
                                   String timeTaken;
                                   if(seconds>9){
                                      // tvTimeTaken.setText("Time taken : "+totalTime+ " : "+seconds);
                                       timeTaken=totalTime+" : "+seconds;
                                   }

                                   else{
                                     //  tvTimeTaken.setText("Time taken : "+totalTime+ " : 0"+seconds);
                                       timeTaken=totalTime+" : 0"+seconds;
                                   }

                                   //To display the score
                                   new AlertDialog.Builder(MainActivity.this).setTitle("Congragulations")
                                           .setMessage("Your score is : "+score+"\nTime taken is "+timeTaken)
                                           .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                               @Override
                                               public void onClick(DialogInterface dialogInterface, int i) {
                                                   btnSubmit.setEnabled(false);
                                               }
                                           })
                                           .show();
                                   score=0;
                                   startButtonCounter=1;
                                   btnStart.setText("Restart");
                               }
                               else
                                   Toast.makeText(MainActivity.this,"Start the crossword puzzle to Submit",Toast.LENGTH_SHORT).show();
                           }
                       })
                       .setNegativeButton("No", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialogInterface, int i) {

                           }
                       })
                       .show();
                break;

            case R.id.btnHelp: //To call the help activity
                Intent intent = new Intent(this,HelpActivity.class);
                startActivity(intent);
                break;
        }
    }



    public void getScore(){ //Calculates the score
        score=0;
        for(int i=0;i<crosswordAnswers.size();i++){
            String userInput = arrayOfEnteredValues.get(i).getText().toString();
            String answer = crosswordAnswers.get(i);
            if(userInput.equalsIgnoreCase(answer)){
                score++;

            }

            else{
                arrayOfEnteredValues.get(i).setTextColor(Color.BLUE); //Sets the wrong enter to red colour
                if(answer.equals("*")){

                }
                else
                    arrayOfEnteredValues.get(i).setText(answer);
            }
            arrayOfEnteredValues.get(i).setEnabled(false);
        }
    }



    //To return the value of the required hint
    public String getHint(String marking){
        String hintValue="";
        int counter=0;
        for(int i=0;i<hintAcross.size();i++){
            HashMap<String,String> temp = hintAcross.get(i);
            if(temp.get("Marking").equals(marking)){
                hintValue="Across: "+temp.get("Hint");
                counter=1;
                break;
            }
        }
        if(counter==0){
            for(int i=0;i<hintDownward.size();i++){
                HashMap<String,String> temp = hintDownward.get(i);
                if(temp.get("Marking").equals(marking)){
                    hintValue="Downward: "+temp.get("Hint");
                    break;
                }
            }
        }
        return hintValue;
    }



    //To reset the entered values
    public void resetEnteredValues(){
        for(int i=0;i<arrayOfEnteredValues.size();i++){
            arrayOfEnteredValues.get(i).setText("");
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            new AlertDialog.Builder(this).setTitle("Exit")
                    .setMessage("Are you sure you want to exit")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    }).show();
        }
        return super.onKeyDown(keyCode, event);
    }
}

