package cache;


import com.google.common.annotations.VisibleForTesting;
import lombok.Builder;
import lombok.extern.java.Log;

import java.time.Duration;

@Log
@Builder
public class CacheConfig {

    @VisibleForTesting
    static final Duration DEFAULT_WATCH_DURATION = Duration.ofSeconds(10);
    @VisibleForTesting
    static final Duration DEFAULT_BACKOFF_DELAY = Duration.ofSeconds(10);
    @VisibleForTesting
    static final Duration DEFAULT_MIN_DELAY_BETWEEN_REQUESTS = Duration.ZERO;
    @VisibleForTesting
    static final boolean DEFAULT_TIMEOUT_AUTO_ADJUSTMENT_ENABLED = true;
    @VisibleForTesting
    static final Duration DEFAULT_TIMEOUT_AUTO_ADJUSTMENT_MARGIN = Duration.ofSeconds(2);

    private final Duration minBackOffDelay;
    private final Duration maxBackOffDelay;
    private final Duration minDelayBetweenRequests;
    private final Duration timeoutAutoAdjustmentMargin;
    private final boolean timeoutAutoAdjustmentEnabled;

    private CacheConfig(Duration minBackOffDelay, Duration maxBackOffDelay, Duration minDelayBetweenRequests,
                        boolean timeoutAutoAdjustmentEnabled, Duration timeoutAutoAdjustmentMargin) {
        this.minBackOffDelay = minBackOffDelay;
        this.maxBackOffDelay = maxBackOffDelay;
        this.minDelayBetweenRequests = minDelayBetweenRequests;
        this.timeoutAutoAdjustmentEnabled = timeoutAutoAdjustmentEnabled;
        this.timeoutAutoAdjustmentMargin = timeoutAutoAdjustmentMargin;
    }

    public Duration getWatchDuration() {
        return DEFAULT_WATCH_DURATION;
    }
}
