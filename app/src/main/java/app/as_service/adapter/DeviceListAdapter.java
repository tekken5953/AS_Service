/**
 * 에어시그널 태블릿 대쉬보드 (사용자용)
 * 개발자 LeeJaeYoung (jy5953@airsignal.kr)
 * 개발시작 2022-06-20
 */

package app.as_service.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.aslib.AsTextView;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import app.as_service.R;
import app.as_service.dao.ApiModel;
import app.as_service.util.ViewTouchListener;
import kotlin.random.Random;

public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.ViewHolder> {
    private final ArrayList<ApiModel.GetDeviceList> mData;
    Context context;

    // 생성자에서 데이터 리스트 객체를 전달받음.
    public DeviceListAdapter(ArrayList<ApiModel.GetDeviceList> list) {
        mData = list;
    }

    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.device_list_item, parent, false);

        ViewTouchListener viewTouchListener = new ViewTouchListener();
        viewTouchListener.onPressView(view);

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
        holder.name.setText(mData.get(position).getDeviceName());
        holder.sn.setText(mData.get(position).getDevice());
        holder.business.setText(mData.get(position).getBusinessType());
        holder.cqi.setIndexTextAsInt(Random.Default.nextInt(500));
        holder.virus.setIndexTextAsInt(Random.Default.nextInt(10));

        if (mData.get(position).getDevice().startsWith("SI")) {
            Glide.with(context)
                    .load(R.drawable.as100)
                    .placeholder(R.drawable.img_placeholder)
                    .error(R.drawable.no_img)
                    .into(holder.type);
        } else if (mData.get(position).getDevice().startsWith("TI")) {
            Glide.with(context)
                    .load(R.drawable.as_m)
                    .placeholder(R.drawable.img_placeholder)
                    .error(R.drawable.no_img)
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
        AsTextView cqi,virus;
        ImageView type;

        ViewHolder(final View itemView) {
            super(itemView);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();

                if (position != RecyclerView.NO_POSITION) {
                    if (mListener != null) {
                        mListener.onItemClick(v, position);
                    }
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