package com.gemseeker.pmma;

import com.gemseeker.pmma.ui.components.animations.EasingMode;
import com.gemseeker.pmma.ui.components.animations.QuarticInterpolator;
import javafx.animation.Interpolator;
import javafx.util.Duration;

/**
 * Contains constants used in the entire application.
 *
 * @author Gem Seeker
 */
public final class AppConstants {

    public static final Interpolator IN_ANIM_INTERPOLATOR = new QuarticInterpolator(EasingMode.EASE_OUT);
    
    public static final Interpolator OUT_ANIM_INTERPOLATOR = new QuarticInterpolator(EasingMode.EASE_IN);
    
    public static final Duration ANIM_DURATION_SHORT = new Duration(250);
    
    public static final Duration ANIM_DURATION_LONG = new Duration(400);
    
}
