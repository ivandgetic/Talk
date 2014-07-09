package org.ivandgetic.talk.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

public class SettingsFragment extends PreferenceFragment {
    private Context context;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        addPreferencesFromResource(R.xml.preferences);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        final EditTextPreference editTextPreferenceServerAddress = (EditTextPreference) findPreference("server_address");
        editTextPreferenceServerAddress.setSummary(editTextPreferenceServerAddress.getText());
        final EditTextPreference editTextPreferenceUsername = (EditTextPreference) findPreference("username");
        editTextPreferenceUsername.setSummary(editTextPreferenceUsername.getText());
        preferences.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (key.equals("server_address")) {
                    editTextPreferenceServerAddress.setSummary(editTextPreferenceServerAddress.getText());
                }
                if (key.equals("username")) {
                    editTextPreferenceUsername.setSummary(editTextPreferenceUsername.getText());
                    MainActivity.USERNAME=editTextPreferenceUsername.getText();
                }
            }
        });
    }
}
