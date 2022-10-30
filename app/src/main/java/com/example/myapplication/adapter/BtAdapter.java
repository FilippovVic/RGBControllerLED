package com.example.myapplication.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.myapplication.R;
import com.example.myapplication.bluetooth.BtConnection;
import com.example.myapplication.bluetooth.ConnectThread;
import java.util.ArrayList;
import java.util.List;

public class BtAdapter extends ArrayAdapter<ListItem> {
    private List<ListItem> mainList;
    private List<ViewHolder> listViewHolders;
    private SharedPreferences pref;

    public BtAdapter(@NonNull Context context, int resource, List<ListItem> btList ) {
        super(context, resource, btList);
        mainList = btList;
        listViewHolders = new ArrayList<>();
        pref = context.getSharedPreferences(BluetoothConstants.MY_PREF, Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = defaultItem(convertView, position, parent);
        return convertView;
    }

    private void savePref(int pos){
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(BluetoothConstants.MAC_KEY, mainList.get(pos).getBtDevice().getAddress());
        editor.apply();
    }

    static class ViewHolder{
        TextView tvBtName;
        CheckBox chBtSelected;
    }

    private View defaultItem(View convertView, int position, ViewGroup parent){
        ViewHolder viewHolder;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.bt_list_item, null, false);
            viewHolder.tvBtName = convertView.findViewById(R.id.tvBtName);
            viewHolder.chBtSelected = convertView.findViewById(R.id.checkBox);
            convertView.setTag(viewHolder);
            listViewHolders.add(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.chBtSelected.setChecked(false);
        }
        viewHolder.tvBtName.setText(mainList.get(position).getBtDevice().getName());
        viewHolder.chBtSelected.setOnClickListener(v -> {
            for(ViewHolder holder : listViewHolders){
                holder.chBtSelected.setChecked(false);
            }
            if (viewHolder.tvBtName.getText().toString().equals(BtConnection.getName())) {
                System.out.println("УСЛОВИЕ");
            } else {
                try {
                    ConnectThread.closeConnection();
                } catch (Exception ignored) {

                }
            }
            savePref(position);
            viewHolder.chBtSelected.setChecked(true);
        });
        if(pref.getString(BluetoothConstants.MAC_KEY,"no bt selected").equals(mainList.get(position)
                .getBtDevice().getAddress()))viewHolder.chBtSelected.setChecked(true);
        return convertView;
    }
}