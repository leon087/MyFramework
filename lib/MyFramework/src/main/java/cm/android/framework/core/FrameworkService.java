package cm.android.framework.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Intent;

import java.util.concurrent.atomic.AtomicBoolean;

@Deprecated
public abstract class FrameworkService extends Service {

    private final AtomicBoolean create = new AtomicBoolean(false);

    private static final Logger logger = LoggerFactory.getLogger("FrameworkService");

    @Override
    public final void onCreate() {
        super.onCreate();
        boolean start = ServiceManager.isStarted();
        logger.info("ServiceManager.isStarted() = {},this = {}", start,
                this.getClass().getSimpleName());
        if (!start) {
            stopSelf();
            return;
        }

        create.set(true);
        onServiceCreate();
    }

    @Override
    public final void onDestroy() {
        logger.info("create.get() = {},this = {}", create.get(), this.getClass().getSimpleName());
        if (create.get()) {
            onServiceDestroy();
            create.set(false);
        }

        super.onDestroy();
    }

    @Override
    @TargetApi(5)
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (null == intent) {
            if (startId % 3 == 0) {
                startService(new Intent(this, this.getClass()));
                return START_NOT_STICKY;
            }
        }

        if (create.get()) {
            onServiceStart(intent, flags, startId);
        }

        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    protected abstract void onServiceCreate();

    protected abstract void onServiceDestroy();

    protected abstract void onServiceStart(Intent intent, int flags, int startId);
}
