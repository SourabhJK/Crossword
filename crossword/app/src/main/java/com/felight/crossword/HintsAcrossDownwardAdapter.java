package com.felight.crossword;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Sourabh on 02-05-2017.
 */

public class HintsAcrossDownwardAdapter extends BaseAdapter {

    private ArrayList<HashMap<String,String>> hintAcross;
    private ArrayList<HashMap<String,String>> hintDownwards;
    private LayoutInflater layoutInflater;

    public HintsAcrossDownwardAdapter(Activity activity, ArrayList hintAcross, ArrayList hintDownwards){
        this.hintAcross = hintAcross;
        this.hintDownwards = hintDownwards;
        layoutInflater = activity.getLayoutInflater();
    }

    @Override
    public int getCount() {
        if(hintAcross.size()>hintDownwards.size())
        return hintAcross.size();

        else
        return hintDownwards.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = layoutInflater.inflate(R.layout.hint_across_downward_layout,viewGroup,false);
        TextView tvAcrossMarking = (TextView) view.findViewById(R.id.tvAcrossMarking);
        TextView tvAcrossHint = (TextView) view.findViewById(R.id.tvAcrossHint);
        TextView tvDownwardMarking = (TextView) view.findViewById(R.id.tvDownwardMarking);
        TextView tvDownwardHint = (TextView) view.findViewById(R.id.tvDownwardHint);

        if(hintAcross.size()>i){
            HashMap<String, String> across = hintAcross.get(i);
            tvAcrossMarking.setText(across.get("Marking")+") ");
            tvAcrossHint.setText(across.get("Hint"));
        }
        else {
            tvAcrossHint.setText("");
            tvAcrossMarking.setText("");
        }

        if (hintDownwards.size()>i){
            HashMap<String, String> downward = hintDownwards.get(i);
            tvDownwardMarking.setText(downward.get("Marking")+") ");
            tvDownwardHint.setText(downward.get("Hint"));
        }
        else{
            tvDownwardMarking.setText("");
            tvDownwardHint.setText("");
        }

        return view;
    }
}
