package cm.android.common.cache.disk.cache;

import com.jakewharton.disklrucache.DiskLruCache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

import cm.android.common.cache.core.ICache;
import cm.java.util.IoUtil;

public abstract class DiskCache<V> implements ICache<String, V> {

    private static final Logger logger = LoggerFactory.getLogger(DiskCache.class);

    // HttpResponseCache的使用 缓存 cache,Caches HTTP and HTTPS responses to the
    // filesystem so they may be reused, saving time and bandwidth. This class
    // supports HttpURLConnection and HttpsURLConnection; there is no
    // platform-provided cache for DefaultHttpClient or AndroidHttpClient.

    private static final int VERSION = 201308;

    private static final int ENTRY_COUNT = 1;

    protected final DiskLruCache cache;

    public DiskCache(File directory, long maxSize) throws IOException {
        cache = DiskLruCache.open(directory, VERSION, ENTRY_COUNT, maxSize);
    }

    public void release() {
        try {
            cache.flush();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        IoUtil.closeQuietly(cache);
    }

    @Override
    public void put(String key, V value) {
        DiskLruCache.Editor editor = null;
        try {
            editor = cache.edit(key);
            if (editor == null) {
                return;
            }
            writeTo(value, editor);
            editor.commit();
        } catch (IOException e) {
            abortQuietly(editor);
        }
    }

    private void abortQuietly(DiskLruCache.Editor editor) {
        try {
            if (editor != null) {
                editor.abort();
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void delete(String key) {
        try {
            cache.remove(key);
        } catch (IOException e) {
            logger.error("key = " + key, e);
        }
    }

    @Override
    public V get(String key) {
        DiskLruCache.Snapshot snapshot;
        try {
            snapshot = cache.get(key);

            if (snapshot == null) {
                return null;
            }
            return readFrom(snapshot);
        } catch (IOException e) {
            // Give up because the cache cannot be read.
            logger.error("key = " + key, e);
            return null;
        }
    }

    @Override
    public void clear() {
        try {
            cache.delete();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public long size() {
        return cache.size();
    }

    @Override
    public long getMaxSize() {
        return cache.getMaxSize();
    }

    public abstract void writeTo(V value, DiskLruCache.Editor editor)
            throws IOException;

    public abstract V readFrom(DiskLruCache.Snapshot snapshot)
            throws IOException;
}
