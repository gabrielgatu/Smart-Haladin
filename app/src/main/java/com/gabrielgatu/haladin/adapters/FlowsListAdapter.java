package com.gabrielgatu.haladin.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gabrielgatu.haladin.R;
import com.gabrielgatu.haladin.beans.DataFlow;

import java.util.ArrayList;

/**
 * Created by gabrielgatu on 19/01/15.
 */
public class FlowsListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<DataFlow> flows;

    public FlowsListAdapter(Context context, ArrayList<DataFlow> flows) {
        this.context = context;
        this.flows = flows;
    }

    @Override
    public int getCount() {
        return flows.size();
    }

    @Override
    public Object getItem(int position) {
        return flows.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.partial_list_data_flow, parent, false);

            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        else {
            holder = (ViewHolder) view.getTag();
        }

        DataFlow flow = flows.get(position);

        holder.title.setText( flow.getPlace() );
        holder.subtitle.setText( flow.getName() );

        String firstLetter = flow.getPlace().split(" ")[1].charAt(0) + "";
        holder.icon.setText( firstLetter );

        // Setting the background color
        switch(position % 4) {
            case 0:
                view.setBackgroundColor(context.getResources().getColor(R.color.turquoise));
                break;
            case 1:
                view.setBackgroundColor(context.getResources().getColor(R.color.material_deep_teal_500));
                break;
            case 2:
                view.setBackgroundColor(context.getResources().getColor(R.color.dark_tail));
                break;
            case 3:
                view.setBackgroundColor(context.getResources().getColor(R.color.wet_asphalt));
                break;
        }

        return view;
    }

    class ViewHolder {
        TextView icon;
        TextView title;
        TextView subtitle;

        ViewHolder(View root) {
            this.icon = (TextView) root.findViewById(R.id.icon);
            this.title = (TextView) root.findViewById(R.id.title);
            this.subtitle = (TextView) root.findViewById(R.id.subtitle);
        }
    }

}
