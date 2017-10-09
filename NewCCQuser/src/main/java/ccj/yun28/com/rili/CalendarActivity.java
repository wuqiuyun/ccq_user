package ccj.yun28.com.rili;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import ccj.yun28.com.R;

public class CalendarActivity extends Activity implements AdapterView.OnItemClickListener{

    private GridView mGridView;
    private TextView tv_title;

    private CalendarAdapter adapter;

    private Calendar mCalendar;
    private String[] weeks = {"周日","周一","周二","周三","周四","周五","周六"};
    private int curDay;//当前日子
    private int kong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        tv_title = (TextView)findViewById(R.id.tv_title);
        mGridView = (GridView)findViewById(R.id.calendar);

        mCalendar = Calendar.getInstance();
        tv_title.setText((mCalendar.get(Calendar.MONTH)+1)+"月签到");

        adapter = new CalendarAdapter(getDateList());
        mGridView.setAdapter(adapter);
        mGridView.setOnItemClickListener(this);
    }


    private List<CalendarEntity> getDateList(){

        int year = mCalendar.get(Calendar.YEAR);
        int month = mCalendar.get(Calendar.MONTH);
        curDay = mCalendar.get(Calendar.DATE);

        List<CalendarEntity> list = new ArrayList<CalendarEntity>();

        //------星期-----
        for (int i = 0; i < weeks.length; i++) {
            CalendarEntity entity = new CalendarEntity();
            entity.setName(weeks[i]);
            entity.setHasSignIn(false);
            list.add(entity);
        }

        //-------获取当前月1号是星期几,意味着前面要空几格
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR,year);
        c.set(Calendar.MONTH,month);
        c.set(Calendar.DAY_OF_MONTH,1);
        int kong = c.get(Calendar.DAY_OF_WEEK)-1;

        for (int i = 0; i < kong; i++) {
            CalendarEntity entity = new CalendarEntity();
            entity.setName("");
            entity.setHasSignIn(false);
            list.add(entity);
        }
        //-------

        //-------获取当前月有多少天
        int monthOfDay = mCalendar.getActualMaximum(Calendar.DATE);
        for (int i = 0; i < monthOfDay; i++) {
            CalendarEntity entity = new CalendarEntity();
            entity.setName((i+1)+"");
            entity.setHasSignIn(false);
            list.add(entity);
        }
        return list;
    }

    private class CalendarAdapter extends BaseAdapter{

        private List<CalendarEntity> list;

        public CalendarAdapter(List<CalendarEntity> list){
            this.list = list;
        }

        public List<CalendarEntity> getList(){
            return list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;
            if (convertView == null){
                holder = new ViewHolder();
                convertView = LayoutInflater.from(CalendarActivity.this).inflate(R.layout.item_grid_calendar,null);
                holder.week = (TextView) convertView.findViewById(R.id.item_calendar_week);
                holder.date = (TextView)convertView.findViewById(R.id.item_calendar_date);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }

            CalendarEntity entity = list.get(position);
            if (position < 7){//星期
                holder.week.setVisibility(View.VISIBLE);
                holder.date.setVisibility(View.GONE);
                holder.week.setText(entity.getName());
            }else {
                if (TextUtils.isEmpty(entity.getName())) {//前面空的几格
                    holder.week.setVisibility(View.GONE);
                    holder.date.setVisibility(View.GONE);
                }else{//日期
                    holder.week.setVisibility(View.GONE);
                    holder.date.setVisibility(View.VISIBLE);
                    holder.date.setText(entity.getName());

                    int nowDate = Integer.parseInt(entity.getName());
                    if (!entity.isHasSignIn()){//未签到
                        if (curDay < nowDate){//以后的日期
                            holder.date.setBackgroundResource(R.drawable.kong);
                        }else if(curDay == nowDate){//当前日期
                            holder.date.setBackgroundResource(R.drawable.qian);
                        }else{//前面的日期
                            holder.date.setBackgroundResource(R.drawable.bu);
                        }
                    }

                }


            }



            return convertView;
        }
    }

    private class ViewHolder{
        TextView week;
        TextView date;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int nowDate = Integer.parseInt(adapter.getList().get(position).getName());//获取点击的日期
        if (curDay == nowDate){//当天的日期才可以点击
            Log.e("ee","click");
        }
    }
}
