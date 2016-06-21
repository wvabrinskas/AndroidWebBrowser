package com.test.wvabrinskas.testapplication;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

/**
 * Created by wvabrinskas on 6/21/16.
 */
public class ShareController {
    private static ShareController ourInstance = new ShareController();

    public static ShareController getInstance() {
        return ourInstance;
    }

    private ShareController() {
    }

    public enum ShareType {
        Facebook, Twitter, Whatsapp, Pinterest, FBMessenger, SMS, Email, Copy, Browser
    }

    public void share(ShareType type, Activity app) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");

        if (type == ShareType.SMS) {
            String shareBody = "Here is the share content body";
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            app.startActivity(Intent.createChooser(sharingIntent, "Share via"));
        }

        if (type == ShareType.Facebook) {
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentTitle("Hello Facebook")
                    .setContentDescription(
                            "The 'Hello Facebook' sample  showcases simple Facebook integration")
                    .setContentUrl(Uri.parse("http://developers.facebook.com/android"))
                    .build();

            ShareDialog.show(app,linkContent);
        }

        if (type == ShareType.Twitter) {
           String shareURL = "https://twitter.com/intent/tweet?url=http://elitedai.ly/1rfj1zI&text=These%20Quotes%20From%20The%20Police%20Report%20Prove%20Brock%20Turner%E2%80%99s%20Story%20Is%20Even%20Crazier%20Than%20You%20Thought&via=EliteDaily";
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(shareURL));
            app.startActivity(browserIntent);
        }
    }
}
