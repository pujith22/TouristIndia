package com.example.TouristIndia;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FaqAdapter extends RecyclerView.Adapter<FaqAdapter.faqHolder> {
    Context context;
    List<faqitem> itemList = new ArrayList<faqitem>();

    public FaqAdapter(Context con, List<faqitem> items) {
        this.context = con;
        itemList = items;
    }

    @NonNull
    @Override
    public faqHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.faqitem, parent, false);
        return new faqHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull faqHolder holder, int position) {

        holder.question.setText(itemList.get(position).getQuestion());
        holder.answer.setText(itemList.get(position).getAnswer());

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }


    public class faqHolder extends RecyclerView.ViewHolder {

        ImageView add;
        TextView question, answer;
        LinearLayout drop;

        boolean open = false;

        public faqHolder(@NonNull View itemView) {
            super(itemView);

            add = itemView.findViewById(R.id.open);
            question = itemView.findViewById(R.id.question);
            answer = itemView.findViewById(R.id.ans);
            drop = itemView.findViewById(R.id.drop);

            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (!open) {
                        drop.setVisibility(View.VISIBLE);
                        add.setImageResource(R.drawable.remove);
                        Animation animSlideDown = AnimationUtils.loadAnimation(context, R.anim.slide_down);
                        drop.startAnimation(animSlideDown);
                        open = true;
                    } else {
                        add.setImageResource(R.drawable.add);
                        Animation animSlideUp = AnimationUtils.loadAnimation(context, R.anim.slide_up);
                        drop.startAnimation(animSlideUp);
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                drop.setVisibility(View.GONE);
                            }
                        }, 500);
                        open = false;
                    }
                }
            });
        }
    }
}