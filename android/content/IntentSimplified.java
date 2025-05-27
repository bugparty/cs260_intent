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
package android.content;

import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.UserHandle; // 仅为 mContentUserHint 保留
import android.graphics.Rect; // 为 mSourceBounds 保留
import android.util.ArraySet; // 为 mCategories 保留

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Set;

/**
 * Intent 是一个描述将要执行的操作的抽象对象。
 * 它主要用于 Activity 启动、广播发送和 Service 通信。
 * 此精简版本主要关注其序列化和反序列化机制。
 */
public class Intent implements Parcelable, Cloneable {
    // Intent 的核心属性
    private String mAction; // 操作名称
    private Uri mData; // 操作关联的数据 URI
    private String mType; // 数据的 MIME 类型
    private String mIdentifier; // Intent 的唯一标识符 (不参与 filter 匹配)
    private String mPackage; // 目标包名
    private ComponentName mComponent; // 目标组件名
    private int mFlags; // 标志位
    private ArraySet<String> mCategories; // 类别集合
    private Bundle mExtras; // 附加数据
    private Rect mSourceBounds; // 发送者边界 (可选)
    private Intent mSelector; // 选择器 Intent (可选)
    private ClipData mClipData; // 剪贴板数据 (可选)
    private int mContentUserHint = UserHandle.USER_CURRENT; // 内容用户提示

    // --- 构造函数 ---

    /**
     * 空构造函数。
     */
    public Intent() {
    }

    /**
     * 拷贝构造函数。
     * @param o 要拷贝的 Intent 对象。
     */
    public Intent(Intent o) {
        this.mAction = o.mAction;
        this.mData = o.mData;
        this.mType = o.mType;
        this.mIdentifier = o.mIdentifier;
        this.mPackage = o.mPackage;
        this.mComponent = o.mComponent;
        this.mFlags = o.mFlags;
        this.mContentUserHint = o.mContentUserHint;

        if (o.mCategories != null) {
            this.mCategories = new ArraySet<>(o.mCategories);
        }
        if (o.mExtras != null) {
            this.mExtras = new Bundle(o.mExtras);
        }
        if (o.mSourceBounds != null) {
            this.mSourceBounds = new Rect(o.mSourceBounds);
        }
        if (o.mSelector != null) {
            this.mSelector = new Intent(o.mSelector);
        }
        if (o.mClipData != null) {
            this.mClipData = new ClipData(o.mClipData);
        }
    }

    /**
     * 从 Parcel 中反序列化 Intent 对象的构造函数。
     * @param in 包含序列化数据的 Parcel。
     */
    protected Intent(Parcel in) {
        readFromParcel(in);
    }

    // --- Parcelable 接口实现 ---

    /**
     * 用于从 Parcel 创建 Intent 对象的 Creator。
     */
    public static final @android.annotation.NonNull Parcelable.Creator<Intent> CREATOR
            = new Parcelable.Creator<Intent>() {
        public Intent createFromParcel(Parcel in) {
            return new Intent(in); // 调用 Intent(Parcel) 构造函数
        }
        public Intent[] newArray(int size) {
            return new Intent[size];
        }
    };

    /**
     * 描述此 Parcelable 实例所包含的特殊对象的种类。
     * @return 一个位掩码，指示此 Parcelable 对象实例所编组的特殊对象类型集。
     */
    @Override
    public int describeContents() {
        // 如果 mExtras 包含 FileDescriptor，则返回 CONTENTS_FILE_DESCRIPTOR
        return (mExtras != null && mExtras.hasFileDescriptors()) ? CONTENTS_FILE_DESCRIPTOR : 0;
    }

    /**
     * 将此 Intent 对象的状态写入 Parcel。
     * @param out 将要写入数据的 Parcel。
     * @param flags 关于对象应如何写入的附加标志。
     */
    @Override
    public void writeToParcel(Parcel out, int flags) {
        // 写入 Action
        out.writeString(mAction); // 在 Android R (API 30) 之前是 writeString8
        // 写入 Data (Uri)
        Uri.writeToParcel(out, mData);
        // 写入 Type
        out.writeString(mType); // 在 Android R (API 30) 之前是 writeString8
        // 写入 Identifier
        out.writeString(mIdentifier); // 在 Android R (API 30) 之前是 writeString8
        // 写入 Flags
        out.writeInt(mFlags);
        // 写入 Package
        out.writeString(mPackage); // 在 Android R (API 30) 之前是 writeString8
        // 写入 ComponentName
        ComponentName.writeToParcel(mComponent, out);

        // 写入 SourceBounds
        if (mSourceBounds != null) {
            out.writeInt(1);
            mSourceBounds.writeToParcel(out, flags);
        } else {
            out.writeInt(0);
        }

        // 写入 Categories
        if (mCategories != null) {
            final int N = mCategories.size();
            out.writeInt(N);
            for (int i=0; i<N; i++) {
                out.writeString(mCategories.valueAt(i)); // 在 Android R (API 30) 之前是 writeString8
            }
        } else {
            out.writeInt(0);
        }

        // 写入 Selector
        if (mSelector != null) {
            out.writeInt(1);
            mSelector.writeToParcel(out, flags);
        } else {
            out.writeInt(0);
        }

        // 写入 ClipData
        if (mClipData != null) {
            out.writeInt(1);
            mClipData.writeToParcel(out, flags);
        } else {
            out.writeInt(0);
        }
        // 写入 ContentUserHint
        out.writeInt(mContentUserHint);
        // 写入 Extras (Bundle)
        out.writeBundle(mExtras);
        // 注意: mLaunchToken 是本地的，不参与序列化
    }

    /**
     * 从 Parcel 中读取 Intent 对象的状态。
     * @param in 包含序列化数据的 Parcel。
     */
    public void readFromParcel(Parcel in) {
        // 读取 Action
        setAction(in.readString()); // 在 Android R (API 30) 之前是 readString8
        // 读取 Data (Uri)
        mData = Uri.CREATOR.createFromParcel(in);
        // 读取 Type
        mType = in.readString(); // 在 Android R (API 30) 之前是 readString8
        // 读取 Identifier
        mIdentifier = in.readString(); // 在 Android R (API 30) 之前是 readString8
        // 读取 Flags
        mFlags = in.readInt();
        // 读取 Package
        mPackage = in.readString(); // 在 Android R (API 30) 之前是 readString8
        // 读取 ComponentName
        mComponent = ComponentName.readFromParcel(in);

        // 读取 SourceBounds
        if (in.readInt() != 0) {
            mSourceBounds = Rect.CREATOR.createFromParcel(in);
        }

        // 读取 Categories
        int N = in.readInt();
        if (N > 0) {
            mCategories = new ArraySet<String>();
            for (int i=0; i<N; i++) {
                mCategories.add(in.readString().intern()); // 在 Android R (API 30) 之前是 readString8
            }
        } else {
            mCategories = null;
        }

        // 读取 Selector
        if (in.readInt() != 0) {
            mSelector = new Intent(in); // 递归读取
        }

        // 读取 ClipData
        if (in.readInt() != 0) {
            mClipData = new ClipData(in); // 递归读取
        }
        // 读取 ContentUserHint
        mContentUserHint = in.readInt();
        // 读取 Extras (Bundle)
        // 需要确保 ClassLoader 正确设置，通常在 Bundle 内部处理或由调用者设置
        mExtras = in.readBundle();
    }

    // --- Getter 和 Setter 方法 (部分保留，用于理解对象状态) ---

    public String getAction() {
        return mAction;
    }

    public Intent setAction(String action) {
        mAction = action != null ? action.intern() : null;
        return this;
    }

    public Uri getData() {
        return mData;
    }

    public Intent setData(Uri data) {
        mData = data;
        mType = null; // setData 会清除 type
        return this;
    }

    public String getType() {
        return mType;
    }

    public Intent setType(String type) {
        mData = null; // setType 会清除 data
        mType = type;
        return this;
    }

    public Intent setDataAndType(Uri data, String type) {
        mData = data;
        mType = type;
        return this;
    }

    public String getIdentifier() {
        return mIdentifier;
    }

    public Intent setIdentifier(String identifier) {
        mIdentifier = identifier;
        return this;
    }

    public int getFlags() {
        return mFlags;
    }

    public Intent setFlags(int flags) {
        mFlags = flags;
        return this;
    }

    public Intent addFlags(int flags) {
        mFlags |= flags;
        return this;
    }

    public String getPackage() {
        return mPackage;
    }

    public Intent setPackage(String packageName) {
        if (packageName != null && mSelector != null) {
            throw new IllegalArgumentException(
                    "Can't set package name when selector is already set");
        }
        mPackage = packageName;
        return this;
    }

    public ComponentName getComponent() {
        return mComponent;
    }

    public Intent setComponent(ComponentName component) {
        mComponent = component;
        return this;
    }

    public Set<String> getCategories() {
        return mCategories;
    }

    public Intent addCategory(String category) {
        if (mCategories == null) {
            mCategories = new ArraySet<String>();
        }
        mCategories.add(category.intern());
        return this;
    }

    public Bundle getExtras() {
        return (mExtras != null)
                ? new Bundle(mExtras) // 返回拷贝以防外部修改
                : null;
    }

    public Intent putExtra(String name, String value) {
        if (mExtras == null) {
            mExtras = new Bundle();
        }
        mExtras.putString(name, value);
        return this;
    }

    public Intent putExtra(String name, int value) {
        if (mExtras == null) {
            mExtras = new Bundle();
        }
        mExtras.putInt(name, value);
        return this;
    }

     public Intent putExtra(String name, boolean value) {
        if (mExtras == null) {
            mExtras = new Bundle();
        }
        mExtras.putBoolean(name, value);
        return this;
    }

    public Intent putExtra(String name, Parcelable value) {
        if (mExtras == null) {
            mExtras = new Bundle();
        }
        mExtras.putParcelable(name, value);
        return this;
    }

    public Intent putExtra(String name, Serializable value) {
        if (mExtras == null) {
            mExtras = new Bundle();
        }
        mExtras.putSerializable(name, value);
        return this;
    }

    public Intent putExtras(Bundle extras) {
        if (mExtras == null) {
            mExtras = new Bundle();
        }
        mExtras.putAll(extras);
        return this;
    }

    public Rect getSourceBounds() {
        return mSourceBounds;
    }

    public void setSourceBounds(Rect r) {
        if (r != null) {
            mSourceBounds = new Rect(r);
        } else {
            mSourceBounds = null;
        }
    }

    public Intent getSelector() {
        return mSelector;
    }

    public void setSelector(Intent selector) {
         if (selector == this) {
            throw new IllegalArgumentException(
                    "Intent being set as a selector of itself");
        }
        if (selector != null && mPackage != null) {
            throw new IllegalArgumentException(
                    "Can't set selector when package name is already set");
        }
        mSelector = selector;
    }

    public ClipData getClipData() {
        return mClipData;
    }

    public void setClipData(ClipData clip) {
        mClipData = clip;
    }

    // 其他方法如 toUri, parseUri, fillIn, filterEquals, toString 等在此精简版中省略，
    // 因为它们主要服务于 Intent 的匹配和表示，而非核心序列化逻辑。
    // Cloneable 接口的 clone() 方法
    @Override
    public Object clone() {
        return new Intent(this);
    }
}
