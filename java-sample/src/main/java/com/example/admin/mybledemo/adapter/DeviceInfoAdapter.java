package com.example.admin.mybledemo.adapter;

import android.bluetooth.BluetoothGattService;
import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.admin.mybledemo.R;
import com.example.admin.mybledemo.Utils;

import java.util.List;

public class DeviceInfoAdapter extends RecyclerView.Adapter<DeviceInfoAdapter.ViewHolder> {

    private Context context;
    private List<BluetoothGattService> gattServices;
    private int opened = -1;
    private ChildAdapter.FileSelect fileSelect;

    public DeviceInfoAdapter(Context context, List<BluetoothGattService> gattServices, ChildAdapter.FileSelect fileSelect){
        this.context = context;
        this.gattServices = gattServices;
        this.fileSelect = fileSelect;
    }


    @Override
    public ViewHolder onCreateViewHolder( ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_device_info, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder( ViewHolder viewHolder, int position) {
        BluetoothGattService gattService = gattServices.get(position);
        String uuid = gattService.getUuid().toString();
        viewHolder.tvServiceUuid.setText(Utils.getUuid(uuid));
        viewHolder.tvServiceType.setText(gattService.getType()==BluetoothGattService.SERVICE_TYPE_PRIMARY?"PRIMARY SERVICE":"UNKNOWN SERVICE");

        if (position == opened){
            viewHolder.recyclerView.setVisibility(View.VISIBLE);
        } else {
            viewHolder.recyclerView.setVisibility(View.GONE);
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(opened == viewHolder.getAdapterPosition()) {
                    //当点击的item已经被展开了, 就关闭.
                    opened = -1;
                    notifyItemChanged(viewHolder.getAdapterPosition());
                }else {
                    int oldOpened = opened;
                    opened = viewHolder.getAdapterPosition();
                    notifyItemChanged(oldOpened);
                    notifyItemChanged(opened);
                }
            }
        });

        ChildAdapter adapter = new ChildAdapter(context, gattService.getCharacteristics(),fileSelect);
        viewHolder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        viewHolder.recyclerView.setAdapter(adapter);

    }

    @Override
    public int getItemCount() {
        return gattServices.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvServiceUuid;
        TextView tvServiceType;
        RecyclerView recyclerView;


        public ViewHolder( View itemView) {
            super(itemView);
            tvServiceUuid = itemView.findViewById(R.id.tv_service_uuid);
            tvServiceType = itemView.findViewById(R.id.tv_type);
            recyclerView = itemView.findViewById(R.id.recyclerView);
        }

    }

}
