package app.com.myapplication.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import app.com.myapplication.OnLoadMoreListener;
import app.com.myapplication.R;

/**
 * @author：JTL
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "LoadMore";
    private MyAdapter myAdapter;
    private LinearLayoutManager layoutManager;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private Handler handler;
    private List<Integer> listData = new ArrayList<>();
    private int count = 0;
    private OnLoadMoreListener mOnLoadMoreListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        myAdapter = new MyAdapter();
        handler = new Handler();
        layoutManager = new LinearLayoutManager(this);

        refreshLayout = findViewById(R.id.swiperefreshlayout);
        recyclerView = findViewById(R.id.recyclerview);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(myAdapter);

        //设置下拉时圆圈的颜色（可以尤多种颜色拼成）
        refreshLayout.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_red_light,
                android.R.color.holo_orange_light);
        //设置下拉时圆圈的背景颜色（这里设置成白色）
        refreshLayout.setProgressBackgroundColorSchemeResource(android.R.color.white);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData("refresh");
            }
        });
        mOnLoadMoreListener=new OnLoadMoreListener() {
            @Override
            protected void onLoading(int countItem, int lastItem) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getData("loadMore");
                    }
                }, 3000);
            }
        };
        recyclerView.addOnScrollListener(mOnLoadMoreListener);

        getData("reset");
    }


    private void getData(final String type) {
        if ("reset".equals(type)) {
            listData.clear();
            count = 0;
            for (int i = 0; i < 3; i++) {
                count += 1;
                listData.add(count);
            }
        }
       else if ("refresh".equals(type)) {
            listData.clear();
            count = 0;
            for (int i = 0; i < 13; i++) {
                count += 1;
                listData.add(count);
            }
        } else {
            for (int i = 0; i < 3; i++) {
                count += 1;
                listData.add(count);
            }
        }

        myAdapter.notifyDataSetChanged();
        if (refreshLayout.isRefreshing()) {
            refreshLayout.setRefreshing(false);
        }
        if ("refresh".equals(type)) {
            Toast.makeText(getApplicationContext(), "刷新完毕", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "加载完毕", Toast.LENGTH_SHORT).show();
        }
    }

    private class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private final static int TYPE_CONTENT = 0;//正常内容
        private final static int TYPE_FOOTER = 1;//加载View

        @Override
        public int getItemViewType(int position) {
            if (position == listData.size() && mOnLoadMoreListener.isAllScreen()) {
                return TYPE_FOOTER;
            }
            return TYPE_CONTENT;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_FOOTER) {
                View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.activity_main_foot, parent, false);
                return new FootViewHolder(view);
            } else {
                View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.activity_main_item, parent, false);
                MyViewHolder myViewHolder = new MyViewHolder(view);
                return myViewHolder;
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            if (getItemViewType(position) == TYPE_FOOTER) {
            } else {
                MyViewHolder viewHolder = (MyViewHolder) holder;
                viewHolder.textView.setText("第" + position + "行");
            }
        }


        @Override
        public int getItemCount() {
            return listData.size() + 1;
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;

        public MyViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textItem);
        }
    }

    private class FootViewHolder extends RecyclerView.ViewHolder {
        ContentLoadingProgressBar contentLoadingProgressBar;

        public FootViewHolder(View itemView) {
            super(itemView);
            contentLoadingProgressBar = itemView.findViewById(R.id.pb_progress);
        }
    }
}
