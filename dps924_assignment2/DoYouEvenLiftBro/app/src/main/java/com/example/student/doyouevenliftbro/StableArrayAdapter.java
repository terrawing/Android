package com.example.student.doyouevenliftbro;

/**
 * Created by student on 3/31/16.
 */
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//Using youtube tutorial from https://www.youtube.com/watch?v=_BZIvjMgH-Q
//Change from an array adapter to simple adapter to allow me to add subtext and a button
public class StableArrayAdapter extends SimpleAdapter implements ListAdapter{

    final int INVALID_ID = -1;

    HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();
    String name = "Nothing";
    Context _context;

    public StableArrayAdapter(Context context, List<Map<String, String>> objects, int textViewResourceId, String[] names, int[] ids) {
        super(context, objects, textViewResourceId, names, ids);

        _context = context;

        for (int i = 0; i < objects.size(); ++i) {
            mIdMap.put(String.valueOf(objects.get(i)), i);
        }
    }

    @Override
    public long getItemId(int position) {
        if (position < 0 || position >= mIdMap.size()) {
            return INVALID_ID;
        }
        String item = getItem(position).toString();
        name = item;
        return mIdMap.get(item);
    }

    /*
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.content_custom__list_view, null);
        }

        Button btn = (Button) view.findViewById(R.id.removeExercise);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Click2", "Clicked2!!");
            }
        });

        return view;
    }*/

    //http://stackoverflow.com/questions/26648991/listviewdragginganimation-broken-on-android-5-lollipop/27153768#27153768
    @Override
    public boolean hasStableIds() {
        return Build.VERSION.SDK_INT < 20;
        //return true;
    }
}
