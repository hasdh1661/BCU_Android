package com.mandarin.bcu.androidutil.asynchs;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.mandarin.bcu.R;
import com.mandarin.bcu.androidutil.StaticStore;
import com.mandarin.bcu.androidutil.adapters.DynamicEmExplanation;
import com.mandarin.bcu.androidutil.adapters.EnemyRecycle;

import java.lang.ref.WeakReference;

import common.system.MultiLangCont;

public class EInfoLoader extends AsyncTask<Void,Integer,Void> {
    private final WeakReference<Activity> weakReference;
    private final int id;

    public EInfoLoader(Activity activity,int id) {
        this.weakReference = new WeakReference<>(activity);
        this.id = id;
    }

    @Override
    protected void onPreExecute() {
        Activity activity = weakReference.get();

        if(MultiLangCont.EEXP.getCont(StaticStore.enemies.get(id)) == null) {
            View view1 = activity.findViewById(R.id.enemviewtop);
            View view2 = activity.findViewById(R.id.enemviewbot);
            ViewPager viewPager = activity.findViewById(R.id.eneminfexp);
            TextView exptext = activity.findViewById(R.id.eneminfexptx);

            view1.setVisibility(View.GONE);
            view2.setVisibility(View.GONE);
            viewPager.setVisibility(View.GONE);
            exptext.setVisibility(View.GONE);
        }
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Activity activity = weakReference.get();

        RecyclerView recyclerView = activity.findViewById(R.id.eneminftable);

        EnemyRecycle enemyRecycle = new EnemyRecycle(activity,id);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setAdapter(enemyRecycle);
        ViewCompat.setNestedScrollingEnabled(recyclerView,false);

        DynamicEmExplanation explain = new DynamicEmExplanation(activity,id);

        ViewPager viewPager = activity.findViewById(R.id.eneminfexp);
        viewPager.setAdapter(explain);
        viewPager.setOffscreenPageLimit(1);

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        Activity activity = weakReference.get();

        ImageButton back = activity.findViewById(R.id.eneminfbck);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });

        ScrollView scrollView = activity.findViewById(R.id.eneminfscroll);
        ProgressBar prog = activity.findViewById(R.id.eneminfprog);

        scrollView.setVisibility(View.VISIBLE);
        prog.setVisibility(View.GONE);
    }
}