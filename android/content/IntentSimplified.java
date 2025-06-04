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
import android.os.UserHandle; // Only for mContentUserHint
import android.graphics.Rect; // For mSourceBounds
import android.util.ArraySet; // For mCategories

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Set;

/**
 * Intent is an abstract object that describes an operation to be performed.
 * It is used primarily for starting Activities, sending broadcasts, and communicating with Services.
 * This simplified version focuses on its serialization and deserialization mechanism.
 */
public class Intent implements Parcelable, Cloneable {
    // Core attributes of Intent
    private String mAction; // Action name
    private Uri mData; // Data URI associated with the action
    private String mType; // MIME type of the data
    private String mIdentifier; // Unique identifier for the Intent (not involved in filter matching)
    private String mPackage; // Target package name
    private ComponentName mComponent; // Target component name
    private int mFlags; // Flags
    private ArraySet<String> mCategories; // Set of categories
    private Bundle mExtras; // Additional data
    private Rect mSourceBounds; // Sender's bounds (optional)
    private Intent mSelector; // Selector Intent (optional)
    private ClipData mClipData; // Clip data (optional)
    private int mContentUserHint = UserHandle.USER_CURRENT; // Content user hint

    // --- Constructors ---

    /**
     * Default constructor.
     */
    public Intent() {
    }

    /**
     * Copy constructor.
     * @param o Intent object to be copied.
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
     * Constructor that deserializes Intent object from Parcel.
     * @param in Parcel containing serialized data.
     */
    protected Intent(Parcel in) {
        readFromParcel(in);
    }

    // --- Parcelable interface implementation ---

    /**
     * Creator for creating Intent objects from Parcel.
     */
    public static final @android.annotation.NonNull Parcelable.Creator<Intent> CREATOR
            = new Parcelable.Creator<Intent>() {
        public Intent createFromParcel(Parcel in) {
            return new Intent(in); // Call Intent(Parcel) constructor
        }
        public Intent[] newArray(int size) {
            return new Intent[size];
        }
    };

    /**
     * Describe the kinds of special objects contained in this Parcelable instance.
     * @return A bitmask indicating the set of special object types marshaled by this Parcelable object instance.
     */
    @Override
    public int describeContents() {
        // Return CONTENTS_FILE_DESCRIPTOR if mExtras contains FileDescriptor
        return (mExtras != null && mExtras.hasFileDescriptors()) ? CONTENTS_FILE_DESCRIPTOR : 0;
    }

    /**
     * Write the state of this Intent object to Parcel.
     * @param out Parcel to write data to.
     * @param flags Additional flags about how the object should be written.
     */
    @Override
    public void writeToParcel(Parcel out, int flags) {
        // Write Action
        out.writeString(mAction); // writeString8 before Android R (API 30)
        // Write Data (Uri)
        Uri.writeToParcel(out, mData);
        // Write Type
        out.writeString(mType); // writeString8 before Android R (API 30)
        // Write Identifier
        out.writeString(mIdentifier); // writeString8 before Android R (API 30)
        // Write Flags
        out.writeInt(mFlags);
        // Write Package
        out.writeString(mPackage); // writeString8 before Android R (API 30)
        // Write ComponentName
        ComponentName.writeToParcel(mComponent, out);

        // Write SourceBounds
        if (mSourceBounds != null) {
            out.writeInt(1);
            mSourceBounds.writeToParcel(out, flags);
        } else {
            out.writeInt(0);
        }

        // Write Categories
        if (mCategories != null) {
            final int N = mCategories.size();
            out.writeInt(N);
            for (int i=0; i<N; i++) {
                out.writeString(mCategories.valueAt(i)); // writeString8 before Android R (API 30)
            }
        } else {
            out.writeInt(0);
        }

        // Write Selector
        if (mSelector != null) {
            out.writeInt(1);
            mSelector.writeToParcel(out, flags);
        } else {
            out.writeInt(0);
        }

        // Write ClipData
        if (mClipData != null) {
            out.writeInt(1);
            mClipData.writeToParcel(out, flags);
        } else {
            out.writeInt(0);
        }
        // Write ContentUserHint
        out.writeInt(mContentUserHint);
        // Write Extras (Bundle)
        out.writeBundle(mExtras);
        // Note: mLaunchToken is local and not serialized
    }

    /**
     * Read the state of Intent object from Parcel.
     * @param in Parcel containing serialized data.
     */
    public void readFromParcel(Parcel in) {
        // Read Action
        setAction(in.readString()); // readString8 before Android R (API 30)
        // Read Data (Uri)
        mData = Uri.CREATOR.createFromParcel(in);
        // Read Type
        mType = in.readString(); // readString8 before Android R (API 30)
        // Read Identifier
        mIdentifier = in.readString(); // readString8 before Android R (API 30)
        // Read Flags
        mFlags = in.readInt();
        // Read Package
        mPackage = in.readString(); // readString8 before Android R (API 30)
        // Read ComponentName
        mComponent = ComponentName.readFromParcel(in);

        // Read SourceBounds
        if (in.readInt() != 0) {
            mSourceBounds = Rect.CREATOR.createFromParcel(in);
        }

        // Read Categories
        int N = in.readInt();
        if (N > 0) {
            mCategories = new ArraySet<String>();
            for (int i=0; i<N; i++) {
                mCategories.add(in.readString().intern()); // readString8 before Android R (API 30)
            }
        } else {
            mCategories = null;
        }

        // Read Selector
        if (in.readInt() != 0) {
            mSelector = new Intent(in); // Recursive read
        }

        // Read ClipData
        if (in.readInt() != 0) {
            mClipData = new ClipData(in); // Recursive read
        }
        // Read ContentUserHint
        mContentUserHint = in.readInt();
        // Read Extras (Bundle)
        // Ensure ClassLoader is correctly set, usually handled internally in Bundle or by the caller
        mExtras = in.readBundle();
    }

    // --- Getter and Setter methods (partially retained for understanding object state) ---

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
        mType = null; // setData clears type
        return this;
    }

    public String getType() {
        return mType;
    }

    public Intent setType(String type) {
        mData = null; // setType clears data
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
                ? new Bundle(mExtras) // Return a copy to prevent external modification
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

    // Other methods like toUri, parseUri, fillIn, filterEquals, toString, etc. are omitted in this simplified version,
    // as they primarily serve the purpose of Intent matching and representation, rather than core serialization logic.
    // Cloneable interface's clone() method
    @Override
    public Object clone() {
        return new Intent(this);
    }
}
