package com.gabrielgatu.haladin.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.gabrielgatu.haladin.R;
import com.gabrielgatu.haladin.beans.DataFlow;
import com.gabrielgatu.haladin.beans.InfoBox;

import java.io.Serializable;
import java.util.ArrayList;

public class DataFlowInfoBoxFragment extends Fragment {

    public static final String KEY_INFO_BOX = "com.gabrielgatu.haladin.INFO_BOX";

    /**
     * A wrapper class used to contain a key-value pair
     * used for the info boxes.
     */
    private InfoBox[] mInfos;

    /**
     * The grid used to contain the 4 info boxes.
     */
    private GridView mGrid;


    public DataFlowInfoBoxFragment() {}

    public static DataFlowInfoBoxFragment newInstance(InfoBox[] infos) {
        Bundle options = new Bundle();
        options.putSerializable(KEY_INFO_BOX, infos);

        DataFlowInfoBoxFragment instance = new DataFlowInfoBoxFragment();
        instance.setArguments(options);
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mInfos = (InfoBox[]) getArguments().getSerializable(KEY_INFO_BOX);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_data_flow_info_box, container, false);

        // Initialize UI components
        mGrid = (GridView) root.findViewById(R.id.grid);

        InfoBoxListAdapter adapter = new InfoBoxListAdapter(mInfos);
        mGrid.setAdapter(adapter);

        return root;
    }

    private class InfoBoxListAdapter extends BaseAdapter {

        private InfoBox[] infos;

        private InfoBoxListAdapter(InfoBox[] infos) {
            this.infos = infos;
        }

        @Override
        public int getCount() {
            return infos.length;
        }

        @Override
        public Object getItem(int position) {
            return infos[position];
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
                LayoutInflater inflater = getActivity().getLayoutInflater();
                view = inflater.inflate(R.layout.partial_list_info_box, parent, false);

                holder = new ViewHolder(view);
                view.setTag(holder);
            }
            else {
                holder = (ViewHolder) view.getTag();
            }

            InfoBox info = infos[position];

            holder.title.setText( info.getTitle() );
            holder.value.setText( info.getValue() );

            switch(position % 4) {
                case 0:
                    view.setBackgroundColor(getResources().getColor(R.color.turquoise));
                    break;
                case 1:
                    view.setBackgroundColor(getResources().getColor(R.color.wet_asphalt));
                    break;
                case 2:
                    view.setBackgroundColor(getResources().getColor(R.color.material_deep_teal_500));
                    break;
                case 3:
                    view.setBackgroundColor(getResources().getColor(R.color.dark_tail));
                    break;
            }

            return view;
        }

        class ViewHolder {
            TextView title;
            TextView value;

            ViewHolder(View root) {
                this.title = (TextView) root.findViewById(R.id.title);
                this.value = (TextView) root.findViewById(R.id.value);
            }
        }
    }

}
