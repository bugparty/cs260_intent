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

/**
 * Simplified Binder implementation: core functionality encapsulation
 * Suitable for understanding Binder IPC Java layer processing mechanism
 */
public class Binder implements IBinder {

    private final long mObject;         // Pointer to native BBinder object
    private IInterface mOwner;          // Local AIDL interface implementation
    private String mDescriptor;         // Interface descriptor (e.g., "android.os.IMyService")

    public Binder() {
        this(null);
    }

    public Binder(String descriptor) {
        mObject = getNativeBBinderHolder();   // Native creates Binder instance
        mDescriptor = descriptor;
        // Usually, native allocation tracking is also registered, omitted here
    }

    // Bind the actual implementation object and interface name on the server side
    public void attachInterface(IInterface owner, String descriptor) {
        mOwner = owner;
        mDescriptor = descriptor;
    }

    // Return the interface descriptor
    public String getInterfaceDescriptor() {
        return mDescriptor;
    }

    // Local optimization: if it's a same-process call, the interface implementation can be directly obtained (saving IPC)
    public IInterface queryLocalInterface(String descriptor) {
        if (mDescriptor != null && mDescriptor.equals(descriptor)) {
            return mOwner;
        }
        return null;
    }

    // Return binding status (default alive here)
    public boolean isBinderAlive() {
        return true;
    }

    // Client transact call (proxy calls this method)
    public final boolean transact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        if (data != null) data.setDataPosition(0);
        boolean r = onTransact(code, data, reply, flags);  // Actual processing logic
        if (reply != null) reply.setDataPosition(0);
        return r;
    }

    // Entry point for server-side processing of IPC requests, usually overridden by AIDL Stub
    protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        if (code == INTERFACE_TRANSACTION) {
            reply.writeString(getInterfaceDescriptor());
            return true;
        }
        return false;
    }

    // Get the UID of the current call being processed (provided by Binder driver)
    public static final native int getCallingUid();

    // Clear and restore caller identity (commonly used for permission isolation)
    public static final native long clearCallingIdentity();
    public static final native void restoreCallingIdentity(long token);

    // Entry method for native layer to enter Java layer
    private boolean execTransact(int code, long dataObj, long replyObj, int flags) {
        final int callingUid = Binder.getCallingUid();

        // Set the UID ownership of the current thread to the caller, used for WorkSource tracking
        final long origWorkSource = ThreadLocalWorkSource.setUid(callingUid);
        try {
            return execTransactInternal(code, dataObj, replyObj, flags, callingUid);
        } finally {
            ThreadLocalWorkSource.restore(origWorkSource);  // Restore original UID
        }
    }

    // Core method for actual IPC processing
    private boolean execTransactInternal(int code, long dataObj, long replyObj, int flags, int callingUid) {
        Parcel data = Parcel.obtain(dataObj);      // Native data → Java Parcel
        Parcel reply = Parcel.obtain(replyObj);    // Return value Parcel

        boolean res;
        try {
            res = onTransact(code, data, reply, flags);  // Dispatch to server-side logic
        } catch (RemoteException | RuntimeException e) {
            if ((flags & IBinder.FLAG_ONEWAY) == 0) {
                reply.setDataSize(0);
                reply.setDataPosition(0);
                reply.writeException(e);   // Write exception back to client
            }
            res = true;
        } finally {
            reply.recycle();
            data.recycle();
        }

        return res;
    }

    // Native creates BBinder object (C++ layer corresponds to JavaBBinderHolder)
    private static native long getNativeBBinderHolder();
}
