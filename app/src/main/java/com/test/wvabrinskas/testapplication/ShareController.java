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

    public void share(ShareType type, final MainActivity app) {  //should possibly pass in post object once created

        Toast newToast = Toast.makeText(app.getApplicationContext(), "", Toast.LENGTH_LONG);
        String shareText = app._currentPost.title + "\n" + app._currentPost.short_url;
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");

        if (type == ShareType.SMS) {
            String shareBody = shareText;
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            app.startActivity(Intent.createChooser(sharingIntent, "Share via"));
        }

        if (type == ShareType.Facebook) {
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentTitle("Checkout this Elite Daily article!")
                    .setContentDescription(
                            app._currentPost.title)
                    .setContentUrl(Uri.parse(app._currentPost.short_url))
                    .build();

            ShareDialog.show(app, linkContent);
        }

        if (type == ShareType.Twitter) {
            shareViaURL("https://twitter.com/intent/tweet?url=" + urlEncode(app._currentPost.short_url) + "&via=EliteDaily", app);
        }

        if (type == ShareType.Whatsapp) {

            Intent sendIntent = new Intent();
            sendIntent.setPackage("com.whatsapp");

            if (doesPackageExist(sendIntent,app)) {
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, shareText);
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
            i.putExtra(Intent.EXTRA_TEXT   , shareText);
            try {
                app.startActivity(Intent.createChooser(i, "Send mail..."));
            } catch (android.content.ActivityNotFoundException ex) {
                newToast.setText("There are no email clients installed.");
                newToast.show();
            }
        }

        if (type == ShareType.Pinterest) {
            String shareUrl = app._currentPost.short_url;
            String mediaUrl = app._currentPost.thumbnailURL;
            String description = app._currentPost.title;
            String url = String.format(
                    "https://www.pinterest.com/pin/create/button/?url=%s&media=%s&description=%s",
                    urlEncode(shareUrl), urlEncode(mediaUrl), description);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            shareViaURL(url,app);
        }

        if (type == ShareType.Copy) {
            ClipboardManager clipboard = (ClipboardManager) app.getSystemService(app.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("label", "Text to copy");
            clipboard.setPrimaryClip(clip);
            newToast.setText("Copied article url");
            newToast.show();
        }

        if (type == ShareType.Browser) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(app);
            alertDialogBuilder.setTitle("Elite Daily");
            alertDialogBuilder
                    .setMessage("Open article in browser?")
                    .setCancelable(false)
                    .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            shareViaURL(app._currentPost.url,app);
                            app.finish();
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
                shareViaURL("fb-messenger://share?link=" + urlEncode(app._currentPost.short_url), app);
            } else {
                newToast.setText("Facebook messenger not installed");
                newToast.show();
            }
        }
    }
}
