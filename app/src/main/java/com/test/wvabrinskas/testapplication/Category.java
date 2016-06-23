package com.test.wvabrinskas.testapplication;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by wvabrinskas on 6/23/16.
 */
public class Category {
    public String id;
    public String slug;
    public String title;

    public Category(JSONObject jsonObject) throws JSONException {
        id = jsonObject.getString("id");
        slug = jsonObject.getString("slug");
        title = jsonObject.getString("title");
    }
}
