package com.polygamma.demo;

import android.app.Application;
import android.util.Log;

import org.polygamma.android.origin.Origin;
import org.polygamma.android.origin.ads.AdInstance;
import org.polygamma.android.origin.ads.AdsModule;
import org.polygamma.android.origin.ads.PlacementEvent;
import org.polygamma.android.origin.ads.PlacementRenderer;
import org.polygamma.android.origin.antifraud.AntifraudModule;
import org.polygamma.android.origin.antifraud.AntifraudStatus;
import org.polygamma.android.origin.core.OriginModuleEventCallback;

public class MainApplication extends Application {
    private static final String TAG = MainApplication.class.getSimpleName();

    private OriginModuleEventCallback antifraudStatusListener;

    public MainApplication() {
    }


    private void onAntifraudStatus(AntifraudStatus status) {
        Log.i(TAG, "security-verdict-digest=" + status.digest());
        if (status.isFraudulent() && status.confidence() >= 70) {
            Log.e(TAG, "device or app is fraudulent");
            // 可以不必在这里注销事件回调
            Origin.antifraud()
                .unregisterEventCallback(
                        this.antifraudStatusListener,
                        AntifraudModule.STATUS_UPDATE_EVENT
                );
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i(TAG, "Application started");

        // ==============================
        // Origin SDK 初始化
        // ==============================
        Origin.initialize(
            getApplicationContext(),
            Origin.CAPABILITY_ANTIFRAUD | Origin.CAPABILITY_ADS
        );

        this.antifraudStatusListener =
            (module, name, data, timestamp) -> this.onAntifraudStatus((AntifraudStatus) data);
        // 状态更新事件并非具有持久性, 所以请立即获取当前的初始结果
        this.onAntifraudStatus(Origin.antifraud().status());

        Origin.antifraud().registerEventCallback(
            this.antifraudStatusListener,
            AntifraudModule.STATUS_UPDATE_EVENT
        );

        // 监听所有Placement事件
        Origin.ads().registerEventCallback((module, name, data, timestamp) -> {
            PlacementEvent event = (PlacementEvent) data;
            PlacementRenderer renderer = event.renderer();
            AdInstance ad = event.adInstance();

            switch (event.type()) {
                case PlacementEvent.EVENT_ERROR:
                    Log.w(TAG, String.format(
                            "placement %s encountered error%s",
                            renderer.placementId(),
                            ad == null ? "" :
                                    String.format("while rendering ad %s", ad)
                    ));
                    break;
                case PlacementEvent.EVENT_AD_AVAILABLE:
                    assert ad != null;
                    Log.i(TAG, String.format(
                            "placement %s has ad %s available",
                            renderer.placementId(),
                            ad
                    ));
                    break;
                case PlacementEvent.EVENT_AD_SELECTED:
                    assert ad != null;
                    Log.i(TAG, String.format(
                            "placement %s has selected to render ad %s",
                            renderer.placementId(),
                            ad
                    ));
                    break;
                case PlacementEvent.EVENT_AD_RENDERED:
                    assert ad != null;
                    Log.i(TAG, String.format(
                            "placement %s has rendered ad %s",
                            renderer.placementId(),
                            ad
                    ));
                    break;
                case PlacementEvent.EVENT_AD_IMPRESSION:
                    assert ad != null;
                    Log.i(TAG, String.format(
                            "ad %s has received impression in placement %s",
                            ad,
                            renderer.placementId()
                    ));
                    break;
                case PlacementEvent.EVENT_AD_ACTIVATED:
                    assert ad != null;
                    Log.i(TAG, String.format(
                            "ad %s has been activated in placement %s",
                            ad,
                            renderer.placementId()
                    ));
                    break;
                case PlacementEvent.EVENT_AD_REMOVED:
                    assert ad != null;
                    Log.i(TAG, String.format(
                            "ad %s has been removed from placement %s",
                            ad,
                            renderer.placementId()
                    ));
                    break;
                case PlacementEvent.EVENT_AD_RESIZED:
                    assert ad != null;
                    Log.i(TAG, String.format(
                            "ad %s has been resized in placement %s",
                            ad,
                            renderer.placementId()
                    ));
                    break;
                case PlacementEvent.EVENT_USER_REWARD:
                    assert ad != null;
                    Log.i(TAG, String.format(
                            "ad %s in placement %s",
                            ad,
                            renderer.placementId()
                    ));
                    break;
            }
        }, AdsModule.PLACEMENT_EVENT);

        // 全局异常捕获
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            Log.e(TAG, "Uncaught exception in thread: " + thread.getName(), throwable);
        });
    }

}
