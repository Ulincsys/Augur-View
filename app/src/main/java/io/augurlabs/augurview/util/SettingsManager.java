package io.augurlabs.augurview.util;

import android.util.JsonReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Scanner;
import java.util.Set;

import io.augurlabs.augurview.MainActivity;

public class SettingsManager {
    private static SettingsManager This;

    String settingsFileName = "settings.json";
    File settingsFile;

    JSONObject contents;

    private SettingsManager() {
        settingsFile = new File(MainActivity.This.getFilesDir(), settingsFileName);
        try {
            if(settingsFile.createNewFile()) {
                contents = new JSONObject();
                contents.put("servers", new JSONArray());
                // contents.put("");
                synchronize();
            } else {
                readSettings();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(contents.toString());
    }

    private void readSettings() {
        try {
            Scanner scanner = new Scanner(settingsFile);
            contents = new JSONObject(scanner.nextLine());
            scanner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static SettingsManager getManager() {
        if(This == null) {
            This = new SettingsManager();
        }
        return This;
    }

    public static void restart() {
        This.format();
        This = new SettingsManager();
    }

    public JSONArray getServers() {
        return contents.optJSONArray("servers");
    }

    public void addServer(String server) {
        JSONArray servers = getServers();
        for(int i = 0; i < servers.length(); ++i) {
            if(servers.optString(i).compareToIgnoreCase(server) == 0) {
                return;
            }
        }
        servers.put(server);
        try {
            contents.putOpt("servers", servers);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        synchronize();
    }

    public void synchronize() {
        try {
            FileWriter writer = new FileWriter(settingsFile);
            writer.write(contents.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void format() {
        settingsFile.delete();
    }
}
