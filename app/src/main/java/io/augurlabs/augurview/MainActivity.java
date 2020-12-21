package io.augurlabs.augurview;

import android.graphics.Color;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;

import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import io.augurlabs.augurview.util.RequestManager;
import io.augurlabs.augurview.util.SettingsManager;

public class MainActivity extends AppCompatActivity {

    public static String defaultURL = "http://augur.osshealth.io:5055/api/unstable";

    private static String reposLink = "/repos";
    private static String groupsLink = "/repo-groups";
    private static String repoGroupLink = "/repo-groups/$ID/repos";

    public static MainActivity This;
    private RequestManager manager;
    private SettingsManager settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        This = this;
        manager = RequestManager.getInstance(this);
        settings = SettingsManager.getManager();

//        SettingsManager.restart();
//
//        settings = SettingsManager.getManager();

//        settings.format();
//        settings.synchronize();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            addServer();
            //FirstFragment.refresh();
            //FirstFragment.addView(initiateInstance(URL));
        });
    }

    @Override
    public void onBackPressed() {
        if(FirstFragment.display.getText().toString().equalsIgnoreCase("servers")) {
            super.onBackPressed();
        } else {
            FirstFragment.refresh();
        }

    }

    public void addServer() {
        DialogFragment dialog = new AddServerDialog();
        dialog.show(getSupportFragmentManager(), "url_dialog");
    }

    public LinearLayout initiateInstance(String URL) {
        LinearLayout layout = new LinearLayout(This);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setDividerPadding(20);
        layout.setPadding(20, 10, 20, 10);

        JsonObjectRequest APIRequest = new JsonObjectRequest
                (Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        setDisplay(URL);
                        if(response.toString().contains("OK")) {
                            JsonArrayRequest RepoRequest = new JsonArrayRequest
                                    (Request.Method.GET, URL + "/repos", null, new Response.Listener<JSONArray>() {

                                        @Override
                                        public void onResponse(JSONArray response) {
                                            try {
                                                for(int i = 0; i < response.length(); ++i) {
                                                    JSONObject object = response.getJSONObject(i);
                                                    CardView view = new CardView(This);
                                                    TextView title = new TextView(This);
                                                    TextView description = new TextView(This);
                                                    title.setText(object.remove("repo_name").toString());
                                                    String body = object.toString(5);
                                                    body = body.replaceFirst("\\{", "");
                                                    body = "\n" + body.substring(0, body.length() - 2);
                                                    description.setText(body);
                                                    view.addView(title);
                                                    description.setVisibility(View.GONE);

                                                    view.setOnClickListener(event -> {
                                                        if(description.getVisibility() == View.GONE) {
                                                            description.setVisibility(View.VISIBLE);
                                                        } else {
                                                            description.setVisibility(View.GONE);
                                                        }
                                                    });

                                                    view.setContentPadding(20, 45, 20, 45);
                                                    view.setCardBackgroundColor(Color.DKGRAY);
                                                    view.addView(description);

                                                    Space space = new Space(This);
                                                    space.setMinimumHeight(10);

                                                    layout.addView(view);
                                                    layout.addView(space);
                                                }

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            fireHint("Error fetching response: " + error.getMessage());
                                        }
                                    });
                            manager.addToRequestQueue(RepoRequest);
                        } else {
                            fireHint("The API responded !OK");
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        FirstFragment.display.setText("Error fetching response: " + error.getMessage());

                    }
                });
        manager.addToRequestQueue(APIRequest);

        return layout;
    }

    public void setDisplay(String display) {
        FirstFragment.display.setText(display);
    }

    public void fireHint(String message) {
        Snackbar.make(findViewById(R.id.fab), message, Snackbar.LENGTH_LONG)
        .setAction("Action", null).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        settings.synchronize();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}