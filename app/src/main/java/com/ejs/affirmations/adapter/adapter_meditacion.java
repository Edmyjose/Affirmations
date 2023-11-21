package com.ejs.affirmations.adapter;

import static com.ejs.affirmations.utils.msg.*;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ejs.affirmations.R;
import com.ejs.affirmations.model.categoriaMeditacionClass;
import com.ejs.affirmations.utils.ProgressAlert;

import java.util.List;

public class adapter_meditacion extends RecyclerView.Adapter<adapter_meditacion.ViewHolder> {
    private final String TAG = this.getClass().getSimpleName();
    private final AppCompatActivity appCompatActivity;
    private final List<categoriaMeditacionClass> items;
    private final ProgressAlert progressAlert;

    public adapter_meditacion(AppCompatActivity appCompatActivity, List<categoriaMeditacionClass> items, ProgressAlert progressAlert) {
        this.appCompatActivity = appCompatActivity;
        this.items = items;
        this.progressAlert = progressAlert;
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView main_card_cat;
        ImageView iv_cat_picture;
        TextView tv_cat_title;

        ViewHolder(View v) {
            super(v);
            tv_cat_title = v.findViewById(R.id.tv_cat_title);
            iv_cat_picture = v.findViewById(R.id.iv_cat_picture);
            main_card_cat = v.findViewById(R.id.main_card_cat);
        }
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cat_meditacion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tv_cat_title.setText(items.get(holder.getAdapterPosition()).getstring());
        holder.tv_cat_title.setHint(String.valueOf(items.get(holder.getAdapterPosition()).getid()));
        holder.main_card_cat.setOnClickListener(v -> {
            log("e", TAG,"clicked in: " + holder.tv_cat_title.getHint() + " " +  holder.tv_cat_title.getText());
            toast(appCompatActivity, "i", "clicked in: " + "clicked in: " + holder.tv_cat_title.getHint() + " " +  holder.tv_cat_title.getText() );
        });
        if (holder.getAdapterPosition() == items.size() -1){
            progressAlert.dismiss();
        }
    }

    @Override
    public int getItemCount() {
        int size;
        if (items != null){
            size= items.size();
        } else {
            size= 0;
        }
        return size;
    }

}
