package app.as_service.adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
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
import java.util.Timer;

import app.as_service.R;
import app.as_service.dao.AdapterModel;
import app.as_service.dao.StaticDataObject;
import app.as_service.view.DeviceDetailActivity;

public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.ViewHolder> {
    private final ArrayList<AdapterModel.GetDeviceList> mData;
    Context context;
    Timer timer;

    // 생성자에서 데이터 리스트 객체를 전달받음.
    public DeviceListAdapter(ArrayList<AdapterModel.GetDeviceList> list) {
        mData = list;
    }

    public Timer getTimer() {
        timer = new Timer();
        return timer;
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

        holder.sn.setText(mData.get(position).getDevice());     // 시리얼 넘버

        // 장치 별명 불러오기
        try {
            holder.name.setVisibility(View.VISIBLE);
            holder.name.setText(mData.get(position).getDeviceName());
        } catch(NullPointerException e) {
            holder.name.setVisibility(View.GONE);
        }

        // 비즈니스 타입 불러오기
        try {
            holder.arrow.setVisibility(View.VISIBLE);
            holder.business.setVisibility(View.VISIBLE);
            // > 가 존재하면 마지막 타입만 불러옴
            if (mData.get(position).getBusinessType().contains(">")) {
                String[] lastBusiness = mData.get(position).getBusinessType().split(">");
                holder.business.setText(lastBusiness[lastBusiness.length - 1]);
            } else {
                // 아니면 다 불러옴
                holder.business.setText(mData.get(position).getBusinessType());
            }
        }
        // 통신실패면 없앰
        catch (NullPointerException e) {
            holder.arrow.setVisibility(View.GONE);
            holder.business.setVisibility(View.GONE);
        }

        // 공기질 통합지수 & 바이러스 위험지수 불러옴
        try {
            holder.cqi.setVisibility(View.VISIBLE);
            holder.cqi.setIndexTextAsInt(Integer.parseInt(mData.get(position).getCaiVal()));
        } catch (NumberFormatException | NullPointerException e) {
            holder.cqi.setVisibility(View.GONE);
        }
        try {
            holder.virus.setVisibility(View.VISIBLE);
            holder.virus.setIndexTextAsInt(Integer.parseInt(mData.get(position).getVirusVal()));
        } catch (NumberFormatException | NullPointerException e) {
            holder.virus.setVisibility(View.GONE);
        }

        // 모델명에 따라 아이콘 다르게 불러옴
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
        ImageView type, arrow;

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
                    if (timer != null) {
                        timer.cancel();
                        Log.w(StaticDataObject.TAG,"리스트 타이머 테스크 종료");
                    }

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
            arrow = itemView.findViewById(R.id.deviceListRightArrow2);
        }
    }
}