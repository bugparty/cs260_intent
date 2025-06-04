/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package android.os;

import android.annotation.NonNull;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Slog;

import java.util.Set;

public class BaseBundle {
    private static final String TAG = "Bundle";
    private static final boolean DEBUG = false;

    // Mark whether unpacking is allowed while the Bundle is in transit (for system protection)
    private static final int FLAG_DEFUSABLE = 1 << 0;
    private static final boolean LOG_DEFUSABLE = false;
    private static volatile boolean sShouldDefuse = false;

    // The real data container, stores key-value pairs after unpacking at the Java layer
    ArrayMap<String, Object> mMap = null;

    // If the Bundle is not unpacked, the original Parcel data will be retained
    Parcel mParcelledData = null;

    // Mark whether the parcel is from the native layer
    private boolean mParcelledByNative;

    // Class loader used during unpacking to restore custom Parcelable types
    private ClassLoader mClassLoader;

    // Flags, such as DEFUSABLE
    public int mFlags;

    public BaseBundle() {
        this(null);
    }

    public BaseBundle(ClassLoader loader) {
        mMap = new ArrayMap<>();
        mClassLoader = loader == null ? getClass().getClassLoader() : loader;
    }

    // Set classloader
    public void setClassLoader(ClassLoader loader) {
        mClassLoader = loader;
    }

    public ClassLoader getClassLoader() {
        return mClassLoader;
    }

    // Trigger the unpacking process (restore mMap from mParcelledData)
    void unparcel() {
        synchronized (this) {
            if (mParcelledData != null) {
                initializeFromParcelLocked(mParcelledData, true, mParcelledByNative);
            }
        }
    }

    // Unpack parcel data into map
    private void initializeFromParcelLocked(@NonNull Parcel parcelledData, boolean recycleParcel, boolean parcelledByNative) {
        if (LOG_DEFUSABLE && sShouldDefuse && (mFlags & FLAG_DEFUSABLE) == 0) {
            Slog.wtf(TAG, "Unparceling while in transit may clobber data", new Throwable());
        }

        // If it's an empty parcel, directly initialize an empty map
        if (parcelledData == null || parcelledData == NoImagePreloadHolder.EMPTY_PARCEL) {
            if (mMap == null) {
                mMap = new ArrayMap<>(1);
            } else {
                mMap.clear();
            }
            mParcelledData = null;
            mParcelledByNative = false;
            return;
        }

        // First, read the number of key-value pairs
        final int count = parcelledData.readInt();
        if (count < 0) return;

        // Initialize ArrayMap
        ArrayMap<String, Object> map = mMap;
        if (map == null) {
            map = new ArrayMap<>(count);
        } else {
            map.clear();
            map.ensureCapacity(count);
        }

        try {
            if (parcelledByNative) {
                // Native writing is not sorted, Java reading needs to be safe
                parcelledData.readArrayMapSafelyInternal(map, count, mClassLoader);
            } else {
                // Java writing is sorted, can use efficient append()
                parcelledData.readArrayMapInternal(map, count, mClassLoader);
            }
        } catch (BadParcelableException e) {
            if (sShouldDefuse) {
                Log.w(TAG, "Failed to parse Bundle, defusing", e);
                map.clear();
            } else {
                throw e;
            }
        } finally {
            mMap = map;
            if (recycleParcel) recycleParcel(parcelledData);
            mParcelledData = null;
            mParcelledByNative = false;
        }
    }

    // Recycle Parcel object to save memory
    private static void recycleParcel(Parcel p) {
        if (p != null && p != NoImagePreloadHolder.EMPTY_PARCEL) {
            p.recycle();
        }
    }

    public int size() {
        unparcel();
        return mMap.size();
    }

    public boolean isEmpty() {
        unparcel();
        return mMap.isEmpty();
    }

    public boolean containsKey(String key) {
        unparcel();
        return mMap.containsKey(key);
    }

    public Object get(String key) {
        unparcel();
        return mMap.get(key);
    }

    public void putString(String key, String value) {
        unparcel();
        mMap.put(key, value);
    }

    public String getString(String key) {
        unparcel();
        Object o = mMap.get(key);
        try {
            return (String) o;
        } catch (ClassCastException e) {
            Log.w(TAG, "Type mismatch", e);
            return null;
        }
    }

    // Placeholder class for identifying empty Parcel
    static final class NoImagePreloadHolder {
        public static final Parcel EMPTY_PARCEL = Parcel.obtain();
    }
}
