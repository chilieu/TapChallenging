package me.chilieu.app.tapchallenging;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Thy on 9/28/2014.
 */
public class CustomGridViewAdapter extends ArrayAdapter<circle> {
    Context context;
    int layoutResourceId;
    ArrayList<circle> data = new ArrayList<circle>();


    public CustomGridViewAdapter(Context context, int layoutResourceId, ArrayList<circle> data){
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View row = convertView;
        RecordHolder holder = null;
        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new RecordHolder();
            holder.txtTitle = (TextView) row.findViewById(R.id.textID);
            //holder.image = (ImageView) row.findViewById(R.id.image);
            row.setTag(holder);
        } else {
            holder = (RecordHolder) row.getTag();
        }
        circle item = data.get(position);
        holder.txtTitle.setText("" + item.getTitle());
        return row;

    }

    static class RecordHolder { TextView txtTitle;}

}
