package com.test.wvabrinskas.testapplication;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by wvabrinskas on 6/23/16.
 */
public class Author {
    public String id;
    public String slug;
    public String name;
    public String description;
    public String avatar;

    public Author(JSONObject jsonObject) throws JSONException {
        id = jsonObject.getString("id");
        slug = jsonObject.getString("slug");
        name = jsonObject.getString("name");
        description = jsonObject.getString("description");
        avatar = jsonObject.getJSONObject("avatar").getString("url");
    }
}
