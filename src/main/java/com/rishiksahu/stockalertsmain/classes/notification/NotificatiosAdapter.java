package com.rishiksahu.stockalertsmain.classes.notification;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.rishiksahu.stockalertsmain.R;
import java.util.List;

public class NotificatiosAdapter extends RecyclerView.Adapter<NotificatiosAdapter.ViewHolder> {

    private List<NotificationModel> list;

    public NotificatiosAdapter(List<NotificationModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notifications_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        holder.setData(list.get(position).title, list.get(position).body, list.get(position).time);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView bodyTv, titleTv, dateTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            bodyTv = itemView.findViewById(R.id.ntBody);
            titleTv = itemView.findViewById(R.id.ntTitle);
            dateTv = itemView.findViewById(R.id.ntDate);

        }
        private void setData(String title, String body, String date){
            titleTv.setText(title);
            bodyTv.setText(body);
            dateTv.setText(date);
        }
    }
}
