package com.inf311.paineldoestudante;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class RecentProfilesManager {
    private static final String FILE_NAME = "recent_profiles.txt";
    private static final int MAX_PROFILES = 5;
    private static final String SEPARATOR = "|||";

    public static void saveProfile(Context context, String userId, String userName, String imageUrl) {
        List<ProfileEntry> existingProfiles = readAllProfiles(context);

        for (int i = 0; i < existingProfiles.size(); i++) {
            if (existingProfiles.get(i).userId.equals(userId)) {
                existingProfiles.remove(i);
                break;
            }
        }

        existingProfiles.add(0, new ProfileEntry(userId, userName, imageUrl));

        while (existingProfiles.size() > MAX_PROFILES) {
            existingProfiles.remove(existingProfiles.size() - 1);
        }

        try {
            FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            for (ProfileEntry profile : existingProfiles) {
                String line = profile.userId + SEPARATOR + profile.userName + SEPARATOR + profile.imageUrl + "\n";
                fos.write(line.getBytes());
            }
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<ProfileEntry> readAllProfiles(Context context) {
        List<ProfileEntry> profiles = new ArrayList<>();

        try {
            FileInputStream fis = context.openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);

            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|\\|\\|");
                if (parts.length == 3) {
                    profiles.add(new ProfileEntry(parts[0], parts[1], parts[2]));
                }
            }

            br.close();
            isr.close();
            fis.close();
        } catch (IOException e) { }

        return profiles;
    }

    public static class ProfileEntry {
        public String userId;
        public String userName;
        public String imageUrl;

        public ProfileEntry(String userId, String userName, String imageUrl) {
            this.userId = userId;
            this.userName = userName;
            this.imageUrl = imageUrl;
        }
    }
}