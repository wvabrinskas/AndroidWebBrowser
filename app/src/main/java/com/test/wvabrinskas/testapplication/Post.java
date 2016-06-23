package com.test.wvabrinskas.testapplication;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by wvabrinskas on 6/20/16.
 */
public class Post {
    public String id;
    public String title;
    public String slug;
    public String url;
    public String short_url;
    public String app_content;
    public String excerpt;
    public String date;
    public String video_embed;
    public String thumbnailURL;
    public String headerURL;
    public Author author;
    public Category category;

    public Post(JSONObject json) throws JSONException {
        JSONObject post = json.getJSONObject("post");
        id = post.getString("id");
        title = post.getString("title");
        slug = post.getString("slug");
        url = post.getString("url");
        short_url = post.getString("short_url");
        app_content = post.getString("app_content");

        //set up html for excerpt formatting
        String html = "<html><head><style>body { padding-left:7px; padding-right:7px; }</style><link rel='stylesheet' id='foundation-css'  href='style.css' type='text/css' media='all' />\n</head><body><font color='#555' size='3px'>%s</font></body></html>";
        excerpt = String.format(html,post.getString("excerpt"));

        date = post.getString("date");
        video_embed = post.getString("video_embed");
        thumbnailURL = post.getJSONObject("featured_image").getJSONObject("large").getString("url");
        headerURL = post.getJSONObject("featured_image").getJSONObject("wide-full").getString("url");

        author = new Author(post.getJSONObject("author"));

        JSONArray categories = post.getJSONArray("categories");
        JSONObject cat = (JSONObject) categories.getJSONObject(0);
        category = new Category(cat);
    }

}
