package com.code.codemercenaries.girdthyswordui.Utilities;

import android.app.Activity;

import com.code.codemercenaries.girdthyswordui.R;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;

/**
 * Created by Joel Kingsley on 29-10-2018.
 */

public class FontHelper {
    public void initialize(Activity activity) {
        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath(activity.getString(R.string.default_font_path))
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());
    }
}
