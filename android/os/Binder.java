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
 * 精简 Binder 实现：核心功能封装
 * 适用于理解 Binder IPC Java 层处理机制
 */
public class Binder implements IBinder {

    private final long mObject;         // native 层 BBinder 对象指针
    private IInterface mOwner;          // 本地 AIDL 接口实现
    private String mDescriptor;         // 接口描述符（如 "android.os.IMyService"）

    public Binder() {
        this(null);
    }

    public Binder(String descriptor) {
        mObject = getNativeBBinderHolder();   // native 创建 Binder 实例
        mDescriptor = descriptor;
        // 通常还会注册 native 分配追踪，这里略去
    }

    // 绑定服务端实际实现对象和接口名
    public void attachInterface(IInterface owner, String descriptor) {
        mOwner = owner;
        mDescriptor = descriptor;
    }

    // 返回接口描述符
    public String getInterfaceDescriptor() {
        return mDescriptor;
    }

    // 本地优化：如果是同进程调用可直接获取接口实现（省 IPC）
    public IInterface queryLocalInterface(String descriptor) {
        if (mDescriptor != null && mDescriptor.equals(descriptor)) {
            return mOwner;
        }
        return null;
    }

    // 返回绑定状态（这里默认活着）
    public boolean isBinderAlive() {
        return true;
    }

    // 客户端 transact 调用（proxy 调用此方法）
    public final boolean transact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        if (data != null) data.setDataPosition(0);
        boolean r = onTransact(code, data, reply, flags);  // 实际处理逻辑
        if (reply != null) reply.setDataPosition(0);
        return r;
    }

    // 服务端处理 IPC 请求的入口，通常由 AIDL Stub 重写
    protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        if (code == INTERFACE_TRANSACTION) {
            reply.writeString(getInterfaceDescriptor());
            return true;
        }
        return false;
    }

    // 获取当前正在处理的调用的 UID（由 Binder 驱动提供）
    public static final native int getCallingUid();

    // 清除并恢复调用者身份（权限隔离常用）
    public static final native long clearCallingIdentity();
    public static final native void restoreCallingIdentity(long token);

    // native 层进入 Java 层的入口方法
    private boolean execTransact(int code, long dataObj, long replyObj, int flags) {
        final int callingUid = Binder.getCallingUid();

        // 设置当前线程的 UID 归属为调用者，用于 WorkSource 追踪
        final long origWorkSource = ThreadLocalWorkSource.setUid(callingUid);
        try {
            return execTransactInternal(code, dataObj, replyObj, flags, callingUid);
        } finally {
            ThreadLocalWorkSource.restore(origWorkSource);  // 恢复原始 UID
        }
    }

    // 真正处理 IPC 的核心方法
    private boolean execTransactInternal(int code, long dataObj, long replyObj, int flags, int callingUid) {
        Parcel data = Parcel.obtain(dataObj);      // native 数据 → Java Parcel
        Parcel reply = Parcel.obtain(replyObj);    // 返回值 Parcel

        boolean res;
        try {
            res = onTransact(code, data, reply, flags);  // 分发给服务端逻辑
        } catch (RemoteException | RuntimeException e) {
            if ((flags & IBinder.FLAG_ONEWAY) == 0) {
                reply.setDataSize(0);
                reply.setDataPosition(0);
                reply.writeException(e);   // 把异常写回客户端
            }
            res = true;
        } finally {
            reply.recycle();
            data.recycle();
        }

        return res;
    }

    // native 创建 BBinder 对象（C++ 层对应 JavaBBinderHolder）
    private static native long getNativeBBinderHolder();
}
