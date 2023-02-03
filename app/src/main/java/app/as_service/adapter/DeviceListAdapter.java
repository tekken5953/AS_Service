package app.as_service.adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aslib.AsTextView;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import app.as_service.R;
import app.as_service.dao.AdapterModel;
import app.as_service.view.DeviceDetailActivity;

public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.ViewHolder> {
    private final ArrayList<AdapterModel.GetDeviceList> mData;
    Context context;

    // 생성자에서 데이터 리스트 객체를 전달받음.
    public DeviceListAdapter(ArrayList<AdapterModel.GetDeviceList> list) {
        mData = list;
    }

    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.device_list_item, parent, false);

        return new ViewHolder(view);
    }

    private OnItemClickListener mListener = null;

    private OnItemLongClickListener longClickListener = null;

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(View v, int position);
    }

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.longClickListener = listener;
    }

    // onBindViewHolder() - position 에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            holder.name.setText(mData.get(position).getDevice_name());
        } catch(NullPointerException e) {
            holder.name.setText("없음");
        }

        holder.sn.setText(mData.get(position).getDevice());
        try {
            if (mData.get(position).getBusiness_type().contains(">")) {
                String[] lastBusiness = mData.get(position).getBusiness_type().split(">");
                holder.business.setText(lastBusiness[lastBusiness.length - 1]);
            } else {
                holder.business.setText(mData.get(position).getBusiness_type());
            }
        } catch (NullPointerException e) {
            holder.business.setText("없음");
        }

        try {
            holder.cqi.setVisibility(View.VISIBLE);
            holder.cqi.setIndexTextAsInt(Integer.parseInt(mData.get(position).getCai_val()));
        } catch (NumberFormatException e) {
            holder.cqi.setVisibility(View.GONE);
        }
        try {
            holder.virus.setVisibility(View.VISIBLE);
            holder.virus.setIndexTextAsInt(Integer.parseInt(mData.get(position).getVirus_val()));
        } catch (NumberFormatException e) {
            holder.virus.setVisibility(View.GONE);
        }

        if (mData.get(position).getDevice().startsWith("SI")) {
            Glide.with(context)
                    .load(R.drawable.as100)
                    .placeholder(R.drawable.img_placeholder)
                    .into(holder.type);
        } else if (mData.get(position).getDevice().startsWith("TI")) {
            Glide.with(context)
                    .load(R.drawable.as_m)
                    .placeholder(R.drawable.img_placeholder)
                    .into(holder.type);
        } else {
            Glide.with(context)
                    .load(R.drawable.no_img)
                    .placeholder(R.drawable.img_placeholder)
                    .into(holder.type);
        }


    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, sn, business;
        AsTextView cqi, virus;
        ImageView type;

        ViewHolder(final View itemView) {
            super(itemView);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Intent intent = new Intent(context, DeviceDetailActivity.class);
                    intent.putExtra("deviceName", name.getText().toString());
                    intent.putExtra("serialNumber", sn.getText().toString());
                    intent.putExtra("businessType", business.getText().toString());
                    if (sn.getText().toString().startsWith("SI"))
                        intent.putExtra("deviceType", "as_100");
                    else if (sn.getText().toString().startsWith("TI"))
                        intent.putExtra("deviceType", "as_m");
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
                            (Activity) context,
                            Pair.create(name, "deviceNameTran"),
                            Pair.create(sn, "serialNumberTran"),
                            Pair.create(business, "businessTypeTran"),
                            Pair.create(type, "imageTran")
                    );
                    context.startActivity(intent, options.toBundle());
                }
            });

            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();

                if (position != RecyclerView.NO_POSITION) {
                    if (longClickListener != null) {
                        longClickListener.onItemLongClick(v, position);
                    }
                }
                return false;
            });

            // 뷰 객체에 대한 참조. (hold strong reference)
            name = itemView.findViewById(R.id.deviceListNameTv);
            sn = itemView.findViewById(R.id.deviceListSnTv);
            cqi = itemView.findViewById(R.id.deviceListCqiIndex);
            virus = itemView.findViewById(R.id.deviceListVirusIndex);
            type = itemView.findViewById(R.id.deviceListTypeIv);
            business = itemView.findViewById(R.id.deviceListBusiness);
        }
    }
}