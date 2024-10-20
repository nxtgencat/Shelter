package net.typeblog.shelter.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import net.typeblog.shelter.services.PaymentStubService;
import net.typeblog.shelter.ui.DummyActivity;

public class SettingsManager {
    private static SettingsManager sInstance = null;
    private LocalStorageManager mStorage = LocalStorageManager.getInstance();
    private Context mContext;

    private SettingsManager(Context context) {
        mContext = context;
    }

    public static void initialize(Context context) {
        sInstance = new SettingsManager(context);
    }

    public static SettingsManager getInstance() {
        return sInstance;
    }

    private void syncSettingsToProfileBool(String name, boolean value) {
        Intent intent = new Intent(DummyActivity.SYNCHRONIZE_PREFERENCE);
        intent.putExtra("name", name);
        intent.putExtra("boolean", value);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Utility.transferIntentToProfile(mContext, intent);
        mContext.startActivity(intent);
    }

    private void syncSettingsToProfileInt(String name, int value) {
        Intent intent = new Intent(DummyActivity.SYNCHRONIZE_PREFERENCE);
        intent.putExtra("name", name);
        intent.putExtra("int", value);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Utility.transferIntentToProfile(mContext, intent);
        mContext.startActivity(intent);
    }

    // Enforce all settings
    public void applyAll() {
        applyCrossProfileFileChooser();
        applyPaymentStub();
    }

    // Read and apply the enabled state of the cross profile file chooser
    public void applyCrossProfileFileChooser() {
        boolean enabled = mStorage.getBoolean(LocalStorageManager.PREF_CROSS_PROFILE_FILE_CHOOSER);
        mContext.getPackageManager().setComponentEnabledSetting(
                new ComponentName(mContext, CrossProfileDocumentsProvider.class),
                enabled ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED : PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }

    // Get the enabled state of the cross profile file chooser
    public boolean getCrossProfileFileChooserEnabled() {
        return mStorage.getBoolean(LocalStorageManager.PREF_CROSS_PROFILE_FILE_CHOOSER);
    }

    // Set the enabled state of the cross profile file chooser
    public void setCrossProfileFileChooserEnabled(boolean enabled) {
        mStorage.setBoolean(LocalStorageManager.PREF_CROSS_PROFILE_FILE_CHOOSER, enabled);
        applyCrossProfileFileChooser();
        syncSettingsToProfileBool(LocalStorageManager.PREF_CROSS_PROFILE_FILE_CHOOSER, enabled);
    }

    // Get the blocked state of cross-profile contacts searching
    public boolean getBlockContactsSearchingEnabled() {
        return mStorage.getBoolean(LocalStorageManager.PREF_BLOCK_CONTACTS_SEARCHING);
    }

    // Set the blocked state of cross-profile contacts searching
    public void setBlockContactsSearchingEnabled(boolean enabled) {
        mStorage.setBoolean(LocalStorageManager.PREF_BLOCK_CONTACTS_SEARCHING, enabled);
        syncSettingsToProfileBool(LocalStorageManager.PREF_BLOCK_CONTACTS_SEARCHING, enabled);
    }

    // Get the enabled state of the auto freeze service
    public boolean getAutoFreezeServiceEnabled() {
        return mStorage.getBoolean(LocalStorageManager.PREF_AUTO_FREEZE_SERVICE);
    }

    // Set the enabled state of the auto freeze service
    // This does NOT need to be synchronized nor applied across profile
    public void setAutoFreezeServiceEnabled(boolean enabled) {
        mStorage.setBoolean(LocalStorageManager.PREF_AUTO_FREEZE_SERVICE, enabled);
    }

    // Get the delay for auto freeze service
    public int getAutoFreezeDelay() {
        int ret = mStorage.getInt(LocalStorageManager.PREF_AUTO_FREEZE_DELAY);
        if (ret == Integer.MIN_VALUE) {
            // Default delay is 0 seconds
            ret = 0;
        }
        return ret;
    }

    // Set the delay for auto freeze service (in seconds)
    public void setAutoFreezeDelay(int seconds) {
        mStorage.setInt(LocalStorageManager.PREF_AUTO_FREEZE_DELAY, seconds);
        syncSettingsToProfileInt(LocalStorageManager.PREF_AUTO_FREEZE_DELAY, seconds);
    }

    // Get the enabled state of "skip foreground"
    public boolean getSkipForegroundEnabled() {
        return mStorage.getBoolean(LocalStorageManager.PREF_DONT_FREEZE_FOREGROUND);
    }

    // Set the enabled state of "skip foreground"
    public void setSkipForegroundEnabled(boolean enabled) {
        mStorage.setBoolean(LocalStorageManager.PREF_DONT_FREEZE_FOREGROUND, enabled);
        syncSettingsToProfileBool(LocalStorageManager.PREF_DONT_FREEZE_FOREGROUND, enabled);
    }

    public boolean getPaymentStubEnabled() {
        return mStorage.getBoolean(LocalStorageManager.PREF_PAYMENT_STUB);
    }

    public void setPaymentStubEnabled(boolean enabled) {
        mStorage.setBoolean(LocalStorageManager.PREF_PAYMENT_STUB, enabled);
        applyPaymentStub();
    }

    // Enable / disable the payment stub component based on settings in local storage
    public void applyPaymentStub() {
        boolean enabled = mStorage.getBoolean(LocalStorageManager.PREF_PAYMENT_STUB);
        mContext.getPackageManager().setComponentEnabledSetting(
                new ComponentName(mContext, PaymentStubService.class),
                enabled ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED : PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }
}
