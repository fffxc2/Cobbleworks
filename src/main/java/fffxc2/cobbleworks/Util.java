package fffxc2.cobbleworks;

import com.google.gson.JsonObject;

public class Util {
    // Laziness helpers
    public static int getAsInt(JsonObject object, String fieldName) {
        return  object.get(fieldName).getAsInt();
    };

    public static float getAsFloat(JsonObject object, String fieldName) {
        return  object.get(fieldName).getAsFloat();
    };

    public static String getAsString(JsonObject object, String fieldName) {
        return  object.get(fieldName).getAsString();
    };
}
