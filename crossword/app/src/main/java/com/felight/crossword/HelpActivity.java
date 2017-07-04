package com.felight.crossword;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class HelpActivity extends AppCompatActivity {

    private TextView tvHelp1;
    private TextView tvHelp2;
    private TextView tvHelp3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        tvHelp1 = (TextView) findViewById(R.id.tvHelp1);
        tvHelp2 = (TextView) findViewById(R.id.tvHelp2);
        tvHelp3 = (TextView) findViewById(R.id.tvHelp3);

        tvHelp1.setText("1. Click on start button to start the crossword");
        tvHelp2.setText("2. To display the hint of particular marking, double tap on the position of the marking");
        tvHelp3.setText("3. To get the answer at the particular position, double tap on the position and then click on Hint button");
    }
}
