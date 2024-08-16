package com.malindu.alarm15.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.malindu.alarm15.R;

import java.util.List;

public class TimerDigitAdapter extends RecyclerView.Adapter<TimerDigitAdapter.TimerDigitViewHolder> {
    private static final String TAG = "TimerDigitAdapter";
    private List<Integer> digitList;
    public interface OnDigitClickListener { void onDigitClick(int digit); }
    private OnDigitClickListener digitClickListener;
    private RecyclerView recyclerView;

    public TimerDigitAdapter(List<Integer> digitList, OnDigitClickListener digitClickListener) {
        this.digitList = digitList;
        this.digitClickListener = digitClickListener;
        //this.recyclerView = recyclerView;
    }

    @NonNull
    @Override
    public TimerDigitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_timer_digit, parent, false);
        return new TimerDigitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimerDigitViewHolder holder, int position) {
        int actualPosition = position % digitList.size();
        //Log.d(TAG, "onBindViewHolder: " + position +","+ actualPosition);
        int digit = digitList.get(actualPosition);
        if (digit < 10) {
            String s = "0" + digit;
            holder.digitTextView.setText(s);
        } else {
            holder.digitTextView.setText(String.valueOf(digit));
        }
        holder.itemView.setOnClickListener(v -> {
            int clickedPosition = holder.getAdapterPosition();
            if (clickedPosition != RecyclerView.NO_POSITION) {
                Log.d(TAG, "onBindViewHolder: " + clickedPosition);
                int clickedDigit = digitList.get(clickedPosition % digitList.size());
                digitClickListener.onDigitClick(clickedDigit);
//                if (Integer.parseInt(holder.digitTextView.getText().toString()) > actualPosition) {
//                    recyclerView.smoothScrollToPosition();
//                }
            }
        });
//        holder.itemView.setOnClickListener(v -> {
//            int clickedPosition = holder.getAdapterPosition();
//            Log.d(TAG, "onBindViewHolder: " + clickedPosition +"," +position);
//            if (clickedPosition != RecyclerView.NO_POSITION) {
//                //recyclerView.scrollToPosition(clickedPosition);
//            } else {
//                Log.d(TAG, "onBindViewHolder: ");
//            }
//        });
//        holder.digitTextView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(TAG, "onClick: position: " + position + ", " + holder.getAdapterPosition());
//                if (holder.getAdapterPosition() > position) {
//                    recyclerView.scrollToPosition(holder.getAdapterPosition() + 1);
//                } else if (holder.getAdapterPosition() < position) {
//                    recyclerView.scrollToPosition(holder.getAdapterPosition() - 1);
//                }
//                //recyclerView.scrollToPosition(holder.getAdapterPosition() + 1);
//                //Log.d(TAG, "onClick: 1");
//            }
//        });
//        holder.digitTextView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                recyclerView.scrollToPosition(holder.getAdapterPosition() - 1);
//                Log.d(TAG, "onClick: 2");
//            }
//        });
//        holder.itemView.setOnClickListener(v -> {
//            int targetPosition = holder.getAdapterPosition();
//            // Scroll to the target position
//            recyclerView.smoothScrollToPosition(targetPosition+1);
//        });
    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }

    public class TimerDigitViewHolder extends RecyclerView.ViewHolder {
        TextView digitTextView;

        public TimerDigitViewHolder(@NonNull View itemView) {
            super(itemView);
            digitTextView = itemView.findViewById(R.id.digitTextView);
        }
    }
}
