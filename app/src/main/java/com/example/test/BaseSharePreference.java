package com.example.test;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;

import java.util.Map;
import java.util.Set;

/**
 * Created by zhangbo on 2019/4/12.
 */

public abstract class BaseSharePreference implements SharedPreferences.OnSharedPreferenceChangeListener {
    private SharedPreferences mSharedPreferences;

    protected BaseSharePreference(String name, Context context) {
        mSharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    public String getValue(String key) {
        SharedPreferences sharedPreferences = mSharedPreferences;
        Map<String, ?> map = sharedPreferences.getAll();
        Object value = map.get(key);
        if (value != null) {
            return value.toString();
        }
        return "";
    }

    public long getLong(String key, long defValue) {
        return mSharedPreferences.getLong(key, defValue);
    }

    public boolean getBoolean(String key, boolean defValue) {
        return mSharedPreferences.getBoolean(key, defValue);
    }

    public int getInt(String key, int defValue) {
        return mSharedPreferences.getInt(key, defValue);
    }

    public String getString(String key, String defValue) {
        return mSharedPreferences.getString(key, defValue);
    }

    public float getFloat(String key, float defValue) {
        return mSharedPreferences.getFloat(key, defValue);
    }

    public Set<String> getStringSet(String key, Set<String> values) {
        return mSharedPreferences.getStringSet(key, values);
    }

    public void putBoolean(String key, boolean value) {
        mSharedPreferences.edit().putBoolean(key, value).apply();
    }

    public void putLong(String key, long value) {
        mSharedPreferences.edit().putLong(key, value).apply();
    }

    public void putInt(String key, int value) {
        mSharedPreferences.edit().putInt(key, value).apply();
    }

    @SuppressLint("ApplySharedPref")
    public void putIntForce(String key, int value) {
        mSharedPreferences.edit().putInt(key, value).commit();
    }

    public void putString(String key, String value) {
        mSharedPreferences.edit().putString(key, value).apply();
    }

    public void putFloat(String key, float value) {
        mSharedPreferences.edit().putFloat(key, value).apply();
    }

    public void putStringSet(String key, @Nullable Set<String> values) {
        mSharedPreferences.edit().putStringSet(key, values).apply();
    }

    public void remove(String key) {
        mSharedPreferences.edit().remove(key).apply();
    }

    public void clear() {
        mSharedPreferences.edit().clear().apply();
    }

    public boolean contains(String key) {
        return mSharedPreferences.contains(key);
    }

    protected void registerOnSharedPreferenceChangeListener() {
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    protected void unregisterOnSharedPreferenceChangeListener() {
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }
}
