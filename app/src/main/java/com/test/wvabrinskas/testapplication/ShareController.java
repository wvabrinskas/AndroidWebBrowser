package com.test.wvabrinskas.testapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.widget.Toast;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

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

    private boolean isPackageInstalled(String packagename, PackageManager packageManager) {
        PackageManager pm = packageManager;
        try {
            pm.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void shareViaURL(String url, Activity app) {
        String shareURL = url;
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(shareURL));
        app.startActivity(browserIntent);
    }

    private boolean doesPackageExist(Intent intent, Activity app) {
        List<ResolveInfo> ri = app.getPackageManager().queryIntentServices(intent, 0);
        if (ri != null && !ri.isEmpty()) {
            return true;
        }
        return false;
    }

    private String urlEncode(String string) {
        try {
            return URLEncoder.encode(string,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    public void share(ShareType type, Activity app) {  //should possibly pass in post object once created

        Toast newToast = Toast.makeText(app.getApplicationContext(), "", Toast.LENGTH_LONG);

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

            ShareDialog.show(app, linkContent);
        }

        if (type == ShareType.Twitter) {
            shareViaURL("https://twitter.com/intent/tweet?url=http://elitedai.ly/1rfj1zI&text=These%20Quotes%20From%20The%20Police%20Report%20Prove%20Brock%20Turner%E2%80%99s%20Story%20Is%20Even%20Crazier%20Than%20You%20Thought&via=EliteDaily", app);
        }

        if (type == ShareType.Whatsapp) {

            Intent sendIntent = new Intent();
            sendIntent.setPackage("com.whatsapp");

            if (doesPackageExist(sendIntent,app)) {
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
                sendIntent.setType("text/plain");
                app.startActivity(sendIntent);
            } else {
                newToast.setText("Whatsapp not installed");
                newToast.show();
            }
        }

        if (type == ShareType.Email) {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("message/rfc822");
            i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"recipient@example.com"});
            i.putExtra(Intent.EXTRA_SUBJECT, "subject of email");
            i.putExtra(Intent.EXTRA_TEXT   , "body of email");
            try {
                app.startActivity(Intent.createChooser(i, "Send mail..."));
            } catch (android.content.ActivityNotFoundException ex) {
                newToast.setText("There are no email clients installed.");
                newToast.show();
            }
        }

        if (type == ShareType.Pinterest) {
            String shareUrl = "http://stackoverflow.com/questions/27388056/";
            String mediaUrl = "http://cdn.sstatic.net/stackexchange/img/logos/so/so-logo.png";
            String description = "Pinterest sharing using Android intents";
            String url = String.format(
                    "https://www.pinterest.com/pin/create/button/?url=%s&media=%s&description=%s",
                    urlEncode(shareUrl), urlEncode(mediaUrl), description);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            shareViaURL(url,app);
        }

        if (type == ShareType.Copy) {
            ClipboardManager clipboard = (ClipboardManager) app.getSystemService(app.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("label", "Text to copy"); //this is where the share controller will access the post object and get its url.
            clipboard.setPrimaryClip(clip);
            newToast.setText("Copied article url");
            newToast.show();
        }

        if (type == ShareType.Browser) {
            final Activity shareApp = app;
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(app);
            alertDialogBuilder.setTitle("Elite Daily");
            alertDialogBuilder
                    .setMessage("Open article in browser?")
                    .setCancelable(false)
                    .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            shareViaURL("http://elitedaily.com",shareApp); //TODO: get article url
                            shareApp.finish();
                        }
                    })
                    .setNegativeButton("No",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

        if (type == ShareType.FBMessenger) {
            Intent sendIntent = new Intent();
            sendIntent.setPackage("com.facebook");
            if (doesPackageExist(sendIntent,app)){
                shareViaURL("fb-messenger://share?link=http://elitedaily.com/humor/jean-shorts-are-not-ok/1442249/%23attachment_1442957", app);
            } else {
                newToast.setText("Facebook messenger not installed");
                newToast.show();
            }
        }
    }
}
