package zy.ustc.edu.cn.xmgh;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;


public class RecycleViewActivity extends AppCompatActivity {

    WrapRecyclerView mRecyclerView;
    ArrayList<String> mDatas;

    ArrayList<View> mHeaderViews, mFootViews;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycle_view);

        mRecyclerView = (WrapRecyclerView)findViewById(R.id.recycleView_main);
        initDatas();
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(manager);
        HomeAdapter homeAdapter = new HomeAdapter(this);

        mHeaderViews = new ArrayList<>();
        mHeaderViews.add(View.inflate(this, R.layout.layout_kps_header, null));
        mFootViews = new ArrayList<>();
        mFootViews.add(View.inflate(this, R.layout.layout_kps_header,null));
        RecyclerWrapAdapter adapter = new RecyclerWrapAdapter(mHeaderViews, mFootViews,homeAdapter);
        mRecyclerView.setAdapter(adapter);
    }

    private void initDatas() {
        mDatas = new ArrayList<>(10);
        for(int i = 0; i< 10 ; i++){
            mDatas.add("Item "+i);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recycle_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder>{

        Context context;
        public HomeAdapter(Context context){
            this.context = context;
        }
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder holder = new MyViewHolder(View.inflate(context,R.layout.layout_kps_item, null));
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.tv.setText(mDatas.get(position));
        }



        @Override
        public int getItemCount() {
            return mDatas.size();
        }

        @Override
        public int getItemViewType(int position){
            if(position == 0){
                return -1;
            }else{
                return 0;
            }
        }

        class MyViewHolder extends RecyclerView.ViewHolder{

            TextView tv;
            public MyViewHolder(View itemView) {
                super(itemView);
                tv = (TextView) itemView.findViewById(R.id.textView);
            }
        }
    }
}
