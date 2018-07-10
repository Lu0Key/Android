package cn.edu.sdu.online.isdu.ui.design.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.edu.sdu.online.isdu.R;

public class WeekDialog extends Dialog {
    private Context mContext;
    private int startWeek;
    private int endWeek;

    private RecyclerView weekRV, dayRV;

    public WeekDialog(Context context, int startWeek, int endWeek) {
        this(context);
        this.startWeek = startWeek;
        this.endWeek = endWeek;
    }

    private WeekDialog(@NonNull Context context) {
        super(context, R.style.DialogTheme);
        mContext = context;
    }

    public WeekDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.design_week_dialog);

        weekRV = findViewById(R.id.recycler_view_weeks);
        dayRV = findViewById(R.id.recycler_view_days);

        weekRV.setLayoutManager(new GridLayoutManager(mContext, 5));
        dayRV.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));

        weekRV.setAdapter(new WeekAdapter(startWeek, endWeek));

        List<Day> days = new ArrayList<>();
        days.add(new Day("一"));
        days.add(new Day("二"));
        days.add(new Day("三"));
        days.add(new Day("四"));
        days.add(new Day("五"));
        days.add(new Day("六"));
        days.add(new Day("日"));
        dayRV.setAdapter(new DayAdapter(days));
    }


    class WeekAdapter extends RecyclerView.Adapter<WeekAdapter.ViewHolder> {
        private List<Week> weeks = new ArrayList<>();

        WeekAdapter(int start, int end) {
            for (int i = start; i <= end; i++) {
                weeks.add(new Week(i));
            }
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
            Week week = weeks.get(position);
            holder.textView.setText(week.index + "");
            holder.textView.setBackgroundResource((week.choosen) ?
                    R.drawable.purple_circle : R.drawable.white_circle);
            holder.textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    weeks.get(position).choosen = !weeks.get(position).choosen;
                    notifyDataSetChanged();
                }
            });
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view =
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.week_select_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public int getItemCount() {
            return weeks.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private TextView textView;
            ViewHolder(View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.item_layout);
            }
        }
    }

    class DayAdapter extends RecyclerView.Adapter<DayAdapter.ViewHolder> {
        private List<Day> days;

        DayAdapter(List<Day> days) {
            this.days = days;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
            Day day = days.get(position);
            holder.textView.setText(day.name);
            holder.textView.setBackgroundResource((day.choosen) ?
                    R.drawable.purple_circle : R.drawable.white_circle);
            holder.textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    days.get(position).choosen = !days.get(position).choosen;
                    notifyDataSetChanged();
                }
            });
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view =
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.week_select_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public int getItemCount() {
            return days.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private TextView textView;
            ViewHolder(View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.item_layout);
            }
        }
    }

    class Week {
        int index;
        boolean choosen = false;
        Week() {}
        Week(int index) {this.index = index;}
    }

    class Day {
        String name;
        boolean choosen = false;
        Day() {}
        Day(String name) {this.name = name;}
    }
}
