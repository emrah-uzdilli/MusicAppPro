package myt.music.app_pro;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;


public class FacebookAds {


    public static AdView adView;
    public static InterstitialAd interstitialAd;
    public static RelativeLayout adViewContainer,adViewContainer2;
    public static Context cnt;
    public static Bundle bundle;





    public static void facebookLoadBanner(final Context context, final View view)
    {



        adViewContainer = view.findViewById(R.id.adViewContainer);

        adView = new AdView(context, context.getResources().getString(
                R.string.facebook_banner), AdSize.BANNER_HEIGHT_50);
        adViewContainer.addView(adView);


        adView.loadAd();
        cnt=context;



        adView.setAdListener(new com.facebook.ads.AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {


                //Toast.makeText(context, "error : " + adError.getErrorMessage(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onAdLoaded(Ad ad) {

            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        });




        AdSettings.addTestDevice("d14ff7849b3269f6b6626e2a19467cb2");


    }




    public static void facebookInterstitialAd(final Activity context)
    {


        interstitialAd = new InterstitialAd(context, context.getResources().getString(
                R.string.facebook_Intersitial));

        interstitialAd.setAdListener(new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {

            }

            @Override
            public void onInterstitialDismissed(Ad ad) {

            }

            @Override
            public void onError(Ad ad, AdError adError) {

               // Toast.makeText(context, "intersitial error : " + adError.getErrorMessage(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onAdLoaded(Ad ad) {
                interstitialAd.show();
            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        });
        interstitialAd.loadAd();




        cnt=context;



      AdSettings.addTestDevice("d14ff7849b3269f6b6626e2a19467cb2");

    }

}
