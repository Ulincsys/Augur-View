package io.augurlabs.augurview;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.fragment.NavHostFragment;

import org.json.JSONArray;
import org.json.JSONException;

import io.augurlabs.augurview.util.SettingsManager;

public class FirstFragment extends Fragment {

    public static TextView display;
    public static LinearLayout body;

    private static FirstFragment This;

    private SettingsManager settings;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_first, container, false);
        display = (TextView)view.findViewById(R.id.textview_first);
        body = (LinearLayout) view.findViewById(R.id.scroll_layout);
        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        This = this;
        FirstFragment.body.setDividerPadding(20);
        settings = SettingsManager.getManager();

        JSONArray servers = settings.getServers();

        if(servers.length() != 0) {
            display.setText("Servers");
        }

        for(int i = 0; i < servers.length(); ++i) {
            TextView nview = new TextView(MainActivity.This);
            try {
                nview.setText(servers.getString(i));
                addView(nview);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static void addView(View view) {
        if(This != null) {
            FirstFragment.body.addView(view);
        }
    }

    public static void refresh() {
        FragmentTransaction ft = This.getFragmentManager().beginTransaction();
        if (Build.VERSION.SDK_INT >= 26) {
            ft.setReorderingAllowed(false);
        }
        ft.detach(This).attach(This).commit();
    }
}