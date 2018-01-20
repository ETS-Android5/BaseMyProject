package preferences;

import android.content.Context;
import android.content.SharedPreferences;

import base.MyApp;

public class PreferencesData {

    private static SharedPreferences getPreferences(final Context context) {
        return context.getSharedPreferences("preference_1", Context.MODE_PRIVATE);
    }

    public static void clear() {
        getPreferences(MyApp.getApp()).edit().clear().commit();
    }

    /**
     * 最后选择的相册ID
     */
    private static final String LAST_ALBUM_ID = "LAST_ALBUM_ID";

    public static void setLastAlbumId(String value) {
        SharedPreferences prefs = getPreferences(MyApp.getApp());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(LAST_ALBUM_ID, value);
        editor.commit();
    }

    public static String getLastAlbumId() {
        return getPreferences(MyApp.getApp()).getString(LAST_ALBUM_ID, "");
    }

}
