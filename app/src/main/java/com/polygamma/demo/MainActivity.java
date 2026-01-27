package com.polygamma.demo;

import android.os.Bundle;
import android.util.Log;
import android.util.Pair;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.polygamma.demo.utils.StaticDeviceId;

import org.polygamma.android.origin.Origin;
import org.polygamma.android.origin.OriginOptions;
import org.polygamma.android.origin.ads.AdSize;
import org.polygamma.android.origin.ads.DisplayPlacementView;
import org.polygamma.android.origin.ads.PlacementEvent;

public class MainActivity extends AppCompatActivity {

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_main);

        Origin.initialize(super.getApplication(), Origin.CAPABILITY_ADS);
        /** 自定义标识符 */
        // Origin.initializeWithOptions(
        //    this,
        //    (new OriginOptions())
        //        .addCapability(Origin.CAPABILITY_ADS)
        //        .addDynamicDeviceId("GAID", (ctxt) -> {
        //            String gaid = "UNKNOWN";
        //            try {
        //                AdvertisingIdClient.Info info =
        //                        AdvertisingIdClient.getAdvertisingIdInfo(ctxt);
        //                gaid = info.getId();
        //
        //                boolean limit = info.isLimitAdTrackingEnabled();
        //
        //                Log.d("OriginDemo", "GAID = " + gaid);
        //                Log.d("OriginDemo", "LimitTracking = " + limit);
        //
        //                return new Pair<>(gaid, limit);
        //            } catch (Exception e) {
        //                Log.e("OriginDemo", "GAID error", e);
        //                return new Pair<>("ERROR", true);
        //            }
        //        })
        //        .addStaticDeviceId("CUSTOM", StaticDeviceId.get(this), false)
        // );

        /** 手动请求广告 */
        DisplayPlacementView plcmt =
                DisplayPlacementView.ofPlacementId(this, "test");

        // plcmt.setPlaybackAdMediaVolume(0.1f, 0.5f);
        // plcmt.setPlacementEventListener((event) -> {
        //     if (event.type() != PlacementEvent.EVENT_AD_SELECTED)
        //         return;
        //
        //     if (event.adInstance().playbackDurationSeconds() < 5) {
        //         plcmt.setPlaybackAdMediaVolume(1, 1);
        //     } else {
        //         plcmt.setPlaybackAdMediaVolume(0.1, 0.5);
        //     }
        // });

        plcmt.setSupportedAdMediaSize(AdSize.ofExactWidth(320, 50));
        plcmt.setPlacementEventListener((event) -> {
            // 如果广告可用, 就将添加到视图层次结构中, 以展示我们的广告。
            if (event.type() == PlacementEvent.EVENT_AD_AVAILABLE && plcmt.getParent() == null)
                this.setContentView(plcmt);
        });

        /** 全屏广告 */
        // DisplayPlacementView rewardPlcmt = DisplayPlacementView.ofBuilder(this, "test-rewarded")
        //     .modality(DisplayPlacementView.MODALITY_EXPANDABLE)
        //     .build();
        // super.setContentView(rewardPlcmt);

        /** 插页广告 */
        DisplayPlacementView interstitialPlacement = DisplayPlacementView.ofBuilder(this, "test-rewarded")
            .modality(DisplayPlacementView.MODALITY_INTERSTITIAL)
            .build();
        super.setContentView(interstitialPlacement);

    }
}