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

    // 标记是否允许在 Bundle 正在传输时解包（用于系统防护）
    private static final int FLAG_DEFUSABLE = 1 << 0;
    private static final boolean LOG_DEFUSABLE = false;
    private static volatile boolean sShouldDefuse = false;

    // 真正的数据容器，在 Java 层解包后存储键值对
    ArrayMap<String, Object> mMap = null;

    // 如果 Bundle 还未解包，会保留原始的 Parcel 数据
    Parcel mParcelledData = null;

    // 标记 parcel 是否来自 native 层
    private boolean mParcelledByNative;

    // 解包时用到的 class loader，用于还原自定义 Parcelable 类型
    private ClassLoader mClassLoader;

    // 标记位，比如是否 DEFUSABLE
    public int mFlags;

    public BaseBundle() {
        this(null);
    }

    public BaseBundle(ClassLoader loader) {
        mMap = new ArrayMap<>();
        mClassLoader = loader == null ? getClass().getClassLoader() : loader;
    }

    // 设置 classloader
    public void setClassLoader(ClassLoader loader) {
        mClassLoader = loader;
    }

    public ClassLoader getClassLoader() {
        return mClassLoader;
    }

    // 触发解包过程（从 mParcelledData 还原为 mMap）
    void unparcel() {
        synchronized (this) {
            if (mParcelledData != null) {
                initializeFromParcelLocked(mParcelledData, true, mParcelledByNative);
            }
        }
    }

    // 将 parcel 数据解包成 map
    private void initializeFromParcelLocked(@NonNull Parcel parcelledData, boolean recycleParcel, boolean parcelledByNative) {
        if (LOG_DEFUSABLE && sShouldDefuse && (mFlags & FLAG_DEFUSABLE) == 0) {
            Slog.wtf(TAG, "Unparceling while in transit may clobber data", new Throwable());
        }

        // 如果为空包，直接初始化空的 map
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

        // 首先读取 key-value 的数量
        final int count = parcelledData.readInt();
        if (count < 0) return;

        // 初始化 ArrayMap
        ArrayMap<String, Object> map = mMap;
        if (map == null) {
            map = new ArrayMap<>(count);
        } else {
            map.clear();
            map.ensureCapacity(count);
        }

        try {
            if (parcelledByNative) {
                // native 写入时不排序，Java 读时要用安全方式
                parcelledData.readArrayMapSafelyInternal(map, count, mClassLoader);
            } else {
                // Java 写入是排序过的，可以高效 append()
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

    // 回收 Parcel 对象，节省内存
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

    // 用于识别空 Parcel 的占位类
    static final class NoImagePreloadHolder {
        public static final Parcel EMPTY_PARCEL = Parcel.obtain();
    }
}
