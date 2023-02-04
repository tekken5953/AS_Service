package app.as_service.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aslib.AsTextView;

import java.util.ArrayList;

import app.as_service.R;
import app.as_service.dao.AdapterModel;

public class AirConditionAdapter extends RecyclerView.Adapter<AirConditionAdapter.ViewHolder> {
    private final ArrayList<AdapterModel.AirCondData> mData;
    Context context;

    // 생성자에서 데이터 리스트 객체를 전달받음.
    public AirConditionAdapter(ArrayList<AdapterModel.AirCondData> list) {
        mData = list;
    }

    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.detail_air_item, parent, false);

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
        holder.title.setText(mData.get(position).getTitle());   // 공기질 데이터 종류 불러오기
        // 데이터 불러오기
        try {
            holder.data.setSort(mData.get(position).getSort());
        } catch (NullPointerException e) {
            holder.data.setSort("grade");
        }
        // 데이터가 온도나 습도이면 소숫점까지 노출
        try {
            if (mData.get(position).getSort().equals("temp") || mData.get(position).getSort().equals("humid"))
                holder.data.setIndexTextAsFloat(Float.parseFloat(mData.get(position).getData()));
            else
                holder.data.setIndexTextAsInt(Float.parseFloat(mData.get(position).getData()));
        } catch (NumberFormatException e) {
            holder.data.setVisibility(View.GONE);
        }
    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        AsTextView data;

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
            title = itemView.findViewById(R.id.airItemTitle);
            data = itemView.findViewById(R.id.airItemData);
        }
    }
}
