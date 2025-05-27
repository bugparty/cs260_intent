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
// Simplified version of Android's Parcel class
// This code is for illustrative purposes and does not represent a functional Parcel.
// It focuses on demonstrating the requested simplifications.

package android.os;

import java.util.ArrayList; // Used to simulate internal data storage
import java.util.HashMap;
import java.util.List;      // Used for the list of integers
import java.util.Map;       // Used for map interfaces

/**
 * Simplified Container for a message (data) that can be sent through an IBinder.
 * This version focuses on basic integer read/write operations for demonstration.
 *
 * <p>Note: This is a highly simplified model and is not intended for actual use.
 * It removes most of the original Parcel's complex features, native interactions,
 * and error handling for clarity as requested.</p>
 */
public final class Parcel {
    private static final int VAL_NULL = -1;
    private static final int VAL_STRING = 0;
    private static final int VAL_INTEGER = 1;
    private static final int VAL_MAP = 2;
    private static final int VAL_BUNDLE = 3;
    private static final int VAL_PARCELABLE = 4;
    private static final int VAL_SHORT = 5;
    private static final int VAL_LONG = 6;
    private static final int VAL_FLOAT = 7;
    private static final int VAL_DOUBLE = 8;
    private static final int VAL_BOOLEAN = 9;
    private static final int VAL_CHARSEQUENCE = 10;
    private static final int VAL_LIST  = 11;
    private static final int VAL_SPARSEARRAY = 12;
    private static final int VAL_BYTEARRAY = 13;
    private static final int VAL_STRINGARRAY = 14;
    private static final int VAL_IBINDER = 15;
    private static final int VAL_PARCELABLEARRAY = 16;
    private static final int VAL_OBJECTARRAY = 17;
    private static final int VAL_INTARRAY = 18;
    private static final int VAL_LONGARRAY = 19;
    private static final int VAL_BYTE = 20;
    private static final int VAL_SERIALIZABLE = 21;
    private static final int VAL_SPARSEBOOLEANARRAY = 22;
    private static final int VAL_BOOLEANARRAY = 23;
    private static final int VAL_CHARSEQUENCEARRAY = 24;
    private static final int VAL_PERSISTABLEBUNDLE = 25;
    private static final int VAL_SIZE = 26;
    private static final int VAL_SIZEF = 27;
    private static final int VAL_DOUBLEARRAY = 28;
    // A list to simulate the internal data buffer where integers are written and read.
    // In a real Parcel, this would be a native memory buffer.
    private List<Integer> mDataBuffer;
    private int mDataPosition; // Simulates the current read/write position

    /**
     * Private constructor to prevent direct instantiation.
     * In the original Parcel, this would handle native memory allocation.
     * For this simplified version, we just initialize our data buffer.
     *
     * @param nativePtr A placeholder for the native pointer in the original Parcel.
     * Not used in this simplified version.
     */
    private Parcel(long nativePtr) {
        // Initialize the data buffer and position
        mDataBuffer = new ArrayList<>();
        mDataPosition = 0;
    }

    /**
     * Retrieve a new Parcel object from the pool.
     * In this simplified version, it just creates a new instance.
     * In a real system, this would involve object pooling for performance.
     *
     * @return A new simplified Parcel instance.
     */
    public static Parcel obtain() {
        // In a real scenario, this would manage a pool of Parcel objects.
        // For simplicity, we just create a new one.
        return new Parcel(0);
    }

    /**
     * Put a Parcel object back into the pool.
     * In this simplified version, it just clears the internal state.
     * In a real system, this would return the object to a pool for reuse.
     */
    public final void recycle() {
        // Clear the internal data to simulate recycling the Parcel.
        if (mDataBuffer != null) {
            mDataBuffer.clear();
        }
        mDataPosition = 0;
    }

    /**
     * Writes an integer value into the parcel.
     * This method simulates adding an integer to the internal data buffer.
     *
     * @param val The integer value to write.
     */
    public final void writeInt(int val) {
        // Simulate writing the integer to the internal buffer.
        // In a real Parcel, this would call a native method to write to memory.
        mDataBuffer.add(val);
    }

    /**
     * Writes a long integer value into the parcel.
     * As per the request, this method is left empty.
     * In a real Parcel, this would write a long value to the internal buffer.
     *
     * @param val The long value to write (ignored in this simplified version).
     */
    public final void writeLong(long val) {
        // This method is intentionally left empty as per the simplification request.
        // In a full implementation, it would write a long value to the parcel's data.
    }

    /**
     * Read an integer value from the parcel at the current dataPosition().
     * This method simulates reading an integer from the internal data buffer.
     *
     * @return The integer value read from the parcel.
     * @throws IndexOutOfBoundsException if attempting to read beyond available data.
     */
    public final int readInt() {
        // Simulate reading the integer from the internal buffer.
        // In a real Parcel, this would call a native method to read from memory.
        if (mDataPosition < mDataBuffer.size()) {
            int value = mDataBuffer.get(mDataPosition);
            mDataPosition++; // Advance the read position
            return value;
        } else {
            // Handle cases where there's no more data to read.
            // In a real Parcel, this might lead to a different kind of error or behavior.
            throw new IndexOutOfBoundsException("No more data to read in Parcel.");
        }
    }

    /**
     * Read a long integer value from the parcel at the current dataPosition().
     * As per the request, this method is left empty and returns a default value.
     * In a real Parcel, this would read a long value from the internal buffer.
     *
     * @return A default long value (0L) as this method is empty.
     */
    public final long readLong() {
        // This method is intentionally left empty and returns a default value
        // as per the simplification request.
        return 0L;
    }

    /**
     * Returns the total amount of data contained in the parcel.
     * In this simplified version, it returns the number of integers stored.
     *
     * @return The number of integers currently stored in the parcel.
     */
    public final int dataSize() {
        return mDataBuffer.size();
    }

    /**
     * Returns the current position in the parcel data.
     *
     * @return The current read/write position.
     */
    public final int dataPosition() {
        return mDataPosition;
    }

    /**
     * Move the current read/write position in the parcel.
     *
     * @param pos New offset in the parcel; must be between 0 and {@link #dataSize}.
     * @throws IllegalArgumentException if the position is out of bounds.
     */
    public final void setDataPosition(int pos) {
        if (pos < 0 || pos > mDataBuffer.size()) {
            throw new IllegalArgumentException("Invalid data position: " + pos);
        }
        mDataPosition = pos;
    }

    // --- Empty/Stubbed methods from the original Parcel for simplification ---

    // Placeholder for native pointer, not used in this simplified Java-only version.
    private long mNativePtr;
    private boolean mOwnsNativeParcelObject; // Placeholder

    // Constants from the original Parcel, kept for context but not actively used.
    private static final int VAL_NULL = -1;
    private static final int VAL_STRING = 0;
    private static final int VAL_INTEGER = 1;

    // Stub for native methods, as they are not implemented in this simplified Java version.
    private static native void nativeMarkSensitive(long nativePtr);
    private static native int nativeDataSize(long nativePtr);
    private static native int nativeDataAvail(long nativePtr);
    private static native int nativeDataPosition(long nativePtr);
    private static native int nativeDataCapacity(long nativePtr);
    private static native void nativeSetDataSize(long nativePtr, int size);
    private static native void nativeSetDataPosition(long nativePtr, int pos);
    private static native void nativeSetDataCapacity(long nativePtr, int size);
    private static native int nativeWriteInt(long nativePtr, int val);
    private static native int nativeWriteLong(long nativePtr, long val);
    private static native int nativeReadInt(long nativePtr);
    private static native long nativeReadLong(long nativePtr);
    private static native long nativeCreate();
    private static native void nativeFreeBuffer(long nativePtr);
    private static native void nativeDestroy(long nativePtr);
    private static native void nativeSignalExceptionForError(int error); // Placeholder

    // Dummy classes/interfaces to avoid compilation errors for removed dependencies.
    // In a real Android environment, these would be actual framework classes.
    public interface Parcelable {
        interface Creator<T> {}
        interface ClassLoaderCreator<T> extends Creator<T> {}
    }
    public interface IBinder {}
    public interface IInterface {}
    public static class Bundle {}
    public static class PersistableBundle {}
    public static class Size {
        public Size(int width, int height) {}
        public int getWidth() { return 0; }
        public int getHeight() { return 0; }
    }
    public static class SizeF {
        public SizeF(float width, float height) {}
        public float getWidth() { return 0f; }
        public float getHeight() { return 0f; }
    }
    public static class FileDescriptor {}
    public static class ParcelFileDescriptor {
        public ParcelFileDescriptor(FileDescriptor fd) {}
    }
    public static class ArrayMap<K, V> {
        public ArrayMap() {}
        public ArrayMap(int capacity) {}
        public ArrayMap(ArrayMap<K, V> other) {}
        public void put(K key, V value) {}
        public V get(K key) { return null; }
        public int size() { return 0; }
        public K keyAt(int index) { return null; }
        public V valueAt(int index) { return null; }
        public void append(K key, V value) {}
        public void putAll(ArrayMap<K, V> other) {}
        public void validate() {}
    }
    public static class ArraySet<T> {
        public ArraySet() {}
        public ArraySet(int capacity) {}
        public void append(T value) {}
        public int size() { return 0; }
        public T valueAt(int index) { return null; }
    }
    public static class SparseArray<T> {
        public SparseArray() {}
        public SparseArray(int capacity) {}
        public void append(int key, T value) {}
        public int size() { return 0; }
        public int keyAt(int index) { return 0; }
        public T valueAt(int index) { return null; }
    }
    public static class SparseBooleanArray {
        public SparseBooleanArray() {}
        public SparseBooleanArray(int capacity) {}
        public void append(int key, boolean value) {}
        public int size() { return 0; }
        public int keyAt(int index) { return 0; }
        public boolean valueAt(int index) { return false; }
    }
    public static class SparseIntArray {
        public SparseIntArray() {}
        public SparseIntArray(int capacity) {}
        public void append(int key, int value) {}
        public int size() { return 0; }
        public int keyAt(int index) { return 0; }
        public int valueAt(int index) { return 0; }
    }
    public static class BadParcelableException extends RuntimeException {
        public BadParcelableException(String msg) { super(msg); }
        public BadParcelableException(String msg, Throwable cause) { super(msg, cause); }
    }
    public static class RemoteException extends Exception {
        public RemoteException(String msg, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
            super(msg, cause, enableSuppression, writableStackTrace);
        }
    }
    public static class ServiceSpecificException extends RuntimeException {
        public int errorCode;
        public ServiceSpecificException(int errorCode, String msg) { super(msg); this.errorCode = errorCode; }
    }
    public static class NetworkOnMainThreadException extends RuntimeException {}

    // Dummy classes for static utility methods
    public static class AppOpsManager {
        public static void prefixParcelWithAppOpsIfNeeded(Parcel p) {}
        public static void readAndLogNotedAppops(Parcel p) {}
    }
    public static class StrictMode {
        public static boolean hasGatheredViolations() { return false; }
        public static void clearGatheredViolations() {}
        public static void writeGatheredViolationsToParcel(Parcel p) {}
        public static void readAndHandleBinderCallViolations(Parcel p) {}
    }
    public static class SystemClock {
        public static long elapsedRealtime() { return 0L; }
    }
    public static class ExceptionUtils {
        public static void appendCause(Throwable target, Throwable cause) {}
    }
    public static class SneakyThrow {
        public static void sneakyThrow(Throwable t) {
            if (t instanceof RuntimeException) {
                throw (RuntimeException) t;
            } else if (t instanceof Error) {
                throw (Error) t;
            } else {
                throw new RuntimeException(t); // Fallback for checked exceptions
            }
        }
    }
    public static class Log {
        public static void d(String tag, String msg) {}
        public static void d(String tag, String msg, Throwable tr) {}
        public static void e(String tag, String msg) {}
        public static void e(String tag, String msg, Throwable tr) {}
        public static void w(String tag, String msg, Throwable tr) {}
        public static void wtfStack(String tag, String msg) {}
    }
    public static class Slog { // Simplified version of Slog
        public static void wtfStack(String tag, String msg) {
            Log.e(tag, msg); // For simplification, just log as error
        }
    }
    public static class TextUtils {
        public static Parcelable.Creator<CharSequence> CHAR_SEQUENCE_CREATOR = new Parcelable.Creator<CharSequence>() {
            @Override
            public CharSequence createFromParcel(Parcel source) { return null; }
            @Override
            public CharSequence[] newArray(int size) { return new CharSequence[size]; }
        };
        public static void writeToParcel(CharSequence cs, Parcel p, int flags) {}
    }
    public static class Build {
        public static final class VERSION_CODES {
            public static final int R = 30; // Example value
            public static final int P = 28; // Example value
        }
    }
    public static class UnsupportedAppUsage {
        public int maxTargetSdk;
        public long trackingBug;
    }
    public static class TestApi {}
    public static class GuardedBy {
        public GuardedBy(String value) {}
    }
    public static class CriticalNative {}
    public static class FastNative {}
    public static class ArrayUtils {
        public static void throwsIfOutOfBounds(int arrayLength, int offset, int len) {
            if (offset < 0 || len < 0 || offset + len > arrayLength) {
                throw new IndexOutOfBoundsException("Offset or length out of bounds");
            }
        }
    }


    // --- Other methods from original Parcel, stubbed out or simplified ---

    // Removed all native method declarations that are not directly related to writeInt/readInt
    // or are not part of the requested simplification (e.g., nativeWriteLong, nativeReadLong).
    // The simplified version doesn't interact with actual native memory.

    // Other fields from the original class, kept as placeholders
    private static final boolean DEBUG_RECYCLE = false;
    private static final boolean DEBUG_ARRAY_MAP = false;
    private static final String TAG = "Parcel";
    private ArrayMap<Class, Object> mClassCookies;
    private RuntimeException mStack;
    private static boolean sParcelExceptionStackTrace;
    private static final Object sPoolSync = new Object();
    private Parcel mPoolNext;
    private static Parcel sOwnedPool;
    private static Parcel sHolderPool;
    private static int sOwnedPoolSize = 0;
    private static int sHolderPoolSize = 0;
    private static final int POOL_SIZE = 32;
    private static final int OK = 0;
    private static volatile long sLastWriteExceptionStackTrace;
    private static final int WRITE_EXCEPTION_STACK_TRACE_THRESHOLD_MS = 1000;
    private ArrayMap<Parcelable, Integer> mWrittenSquashableParcelables;
    private boolean mAllowSquashing = false;
    private SparseArray<Parcelable> mReadSquashableParcelables;
    private static final HashMap<ClassLoader,HashMap<String,Parcelable.Creator<?>>> mCreators = new HashMap<>();


    // Remaining methods from the original Parcel, simplified or stubbed out
    // to meet the "don't need to compile" and "simplified" requirements.

    public final int dataAvail() { return mDataBuffer.size() - mDataPosition; }
    public final int dataCapacity() { return mDataBuffer.size(); } // Simplified
    public final void setDataSize(int size) { /* Empty */ } // Simplified
    public final void setDataCapacity(int size) { /* Empty */ } // Simplified
    public final boolean pushAllowFds(boolean allowFds) { return false; } // Simplified
    public final void restoreAllowFds(boolean lastValue) { /* Empty */ } // Simplified
    public final byte[] marshall() { return new byte[0]; } // Simplified
    public final void unmarshall(@NonNull byte[] data, int offset, int length) { /* Empty */ } // Simplified
    public final void appendFrom(Parcel parcel, int offset, int length) { /* Empty */ } // Simplified
    public final int compareData(Parcel other) { return 0; } // Simplified
    public final void setClassCookie(Class clz, Object cookie) { /* Empty */ } // Simplified
    @Nullable public final Object getClassCookie(Class clz) { return null; } // Simplified
    public final void adoptClassCookies(Parcel from) { /* Empty */ } // Simplified
    public Map<Class, Object> copyClassCookies() { return new ArrayMap<>(); } // Simplified
    public void putClassCookies(Map<Class, Object> cookies) { /* Empty */ } // Simplified
    public final boolean hasFileDescriptors() { return false; } // Simplified
    public final void writeInterfaceToken(@NonNull String interfaceName) { /* Empty */ } // Simplified
    public final void enforceInterface(@NonNull String interfaceName) { /* Empty */ } // Simplified
    public boolean replaceCallingWorkSourceUid(int workSourceUid) { return false; } // Simplified
    public int readCallingWorkSourceUid() { return 0; } // Simplified
    public final void writeByteArray(@Nullable byte[] b) { /* Empty */ } // Simplified
    public final void writeByteArray(@Nullable byte[] b, int offset, int len) { /* Empty */ } // Simplified
    public final void writeBlob(@Nullable byte[] b) { /* Empty */ } // Simplified
    public final void writeBlob(@Nullable byte[] b, int offset, int len) { /* Empty */ } // Simplified
    public final void writeFloat(float val) { /* Empty */ } // Simplified
    public final void writeDouble(double val) { /* Empty */ } // Simplified

    /**
     * Write a string value into the parcel at the current dataPosition(),
     * growing dataCapacity() if needed.
     * This method delegates to {@link #writeString16(String)}.
     *
     * @param val The string value to write.
     */
    public final void writeString(@Nullable String val) {
        writeString16(val);
    }

    public final void writeString8(@Nullable String val) {
        mReadWriteHelper.writeString8(this, val);
    }

    /**
     * Write a string value into the parcel at the current dataPosition(),
     * growing dataCapacity() if needed.
     * This method delegates to the {@link ReadWriteHelper} for its actual implementation.
     *
     * @param val The string value to write.
     */
    public final void writeString16(@Nullable String val) {
        mReadWriteHelper.writeString16(this, val);
    }

    /**
     * Write a string without going through a {@link ReadWriteHelper}.
     * Subclasses of {@link ReadWriteHelper} must use this method instead of
     * {@link #writeString} to avoid infinite recursive calls.
     * This method delegates to {@link #writeString16NoHelper(String)}.
     *
     * @param val The string value to write.
     */
    public void writeStringNoHelper(@Nullable String val) {
        writeString16NoHelper(val);
    }

    /**
     * Write a string (UTF-8 encoded) without going through a {@link ReadWriteHelper}.
     * In this simplified version, it writes the string's length as an integer,
     * followed by the integer representation of each character.
     *
     * @param val The string value to write.
     */
    public void writeString8NoHelper(@Nullable String val) {
        // For simplification, treating String8 and String16 similarly in terms of storage.
        // In a real Parcel, String8 would use a different encoding/native call.
        if (val == null) {
            writeInt(-1);
        } else {
            writeInt(val.length());
            for (int i = 0; i < val.length(); i++) {
                writeInt((int) val.charAt(i)); // Store char as int
            }
        }
    }

    /**
     * Write a string (UTF-16 encoded) without going through a {@link ReadWriteHelper}.
     * In this simplified version, it writes the string's length as an integer,
     * followed by the integer representation of each character.
     *
     * @param val The string value to write.
     */
    public void writeString16NoHelper(@Nullable String val) {
        if (val == null) {
            writeInt(-1);
        } else {
            writeInt(val.length());
            for (int i = 0; i < val.length(); i++) {
                writeInt((int) val.charAt(i)); // Store char as int
            }
        }
    }

    public final void writeBoolean(boolean val) { /* Empty */ } // Simplified
    public final void writeCharSequence(@Nullable CharSequence val) { /* Empty */ } // Simplified
    public final void writeStrongBinder(IBinder val) { /* Empty */ } // Simplified
    public final void writeStrongInterface(IInterface val) { /* Empty */ } // Simplified
    public final void writeFileDescriptor(@NonNull FileDescriptor val) { /* Empty */ } // Simplified
    public final void writeRawFileDescriptor(@NonNull FileDescriptor val) { /* Empty */ } // Simplified
    public final void writeRawFileDescriptorArray(@Nullable FileDescriptor[] value) { /* Empty */ } // Simplified
    public final void writeByte(byte val) { /* Empty */ } // Simplified
    public final void writeMap(@Nullable Map val) { /* Empty */ } // Simplified
    void writeMapInternal(@Nullable Map<String,Object> val) { /* Empty */ } // Simplified
    void writeArrayMapInternal(@Nullable ArrayMap<String, Object> val) { /* Empty */ } // Simplified
    public void writeArrayMap(@Nullable ArrayMap<String, Object> val) { /* Empty */ } // Simplified
    public <T extends Parcelable> void writeTypedArrayMap(@Nullable ArrayMap<String, T> val, int parcelableFlags) { /* Empty */ } // Simplified
    public void writeArraySet(@Nullable ArraySet<? extends Object> val) { /* Empty */ } // Simplified
    public final void writeBundle(@Nullable Bundle val) { /* Empty */ } // Simplified
    public final void writePersistableBundle(@Nullable PersistableBundle val) { /* Empty */ } // Simplified
    public final void writeSize(@NonNull Size val) { /* Empty */ } // Simplified
    public final void writeSizeF(@NonNull SizeF val) { /* Empty */ } // Simplified
    public final void writeList(@Nullable List val) { /* Empty */ } // Simplified
    public final void writeArray(@Nullable Object[] val) { /* Empty */ } // Simplified
    public final <T> void writeSparseArray(@Nullable SparseArray<T> val) { /* Empty */ } // Simplified
    public final void writeSparseBooleanArray(@Nullable SparseBooleanArray val) { /* Empty */ } // Simplified
    public final void writeSparseIntArray(@Nullable SparseIntArray val) { /* Empty */ } // Simplified
    public final void writeBooleanArray(@Nullable boolean[] val) { /* Empty */ } // Simplified
    public final void readBooleanArray(@NonNull boolean[] val) { /* Empty */ } // Simplified
    public final void writeCharArray(@Nullable char[] val) { /* Empty */ } // Simplified
    public final void readCharArray(@NonNull char[] val) { /* Empty */ } // Simplified
    public final void writeIntArray(@Nullable int[] val) { /* Empty */ } // Simplified
    public final void readIntArray(@NonNull int[] val) { /* Empty */ } // Simplified
    public final void writeLongArray(@Nullable long[] val) { /* Empty */ } // Simplified
    public final void readLongArray(@NonNull long[] val) { /* Empty */ } // Simplified
    public final void writeFloatArray(@Nullable float[] val) { /* Empty */ } // Simplified
    public final void readFloatArray(@NonNull float[] val) { /* Empty */ } // Simplified
    public final void writeDoubleArray(@Nullable double[] val) { /* Empty */ } // Simplified
    public final void readDoubleArray(@NonNull double[] val) { /* Empty */ } // Simplified
    public final void writeStringArray(@Nullable String[] val) { /* Empty */ } // Simplified
    public final void readStringArray(@NonNull String[] val) { /* Empty */ } // Simplified
    public final void writeString8Array(@Nullable String[] val) { /* Empty */ } // Simplified
    public final void readString8Array(@NonNull String[] val) { /* Empty */ } // Simplified
    public final void writeString16Array(@Nullable String[] val) { /* Empty */ } // Simplified
    public final void readString16Array(@NonNull String[] val) { /* Empty */ } // Simplified
    public final void writeBinderArray(@Nullable IBinder[] val) { /* Empty */ } // Simplified
    public final void writeCharSequenceArray(@Nullable CharSequence[] val) { /* Empty */ } // Simplified
    public final void writeCharSequenceList(@Nullable ArrayList<CharSequence> val) { /* Empty */ } // Simplified
    public final <T extends Parcelable> void writeTypedList(@Nullable List<T> val) { /* Empty */ } // Simplified
    public final <T extends Parcelable> void writeTypedSparseArray(@Nullable SparseArray<T> val, int parcelableFlags) { /* Empty */ } // Simplified
    public <T extends Parcelable> void writeTypedList(@Nullable List<T> val, int parcelableFlags) { /* Empty */ } // Simplified
    public final void writeStringList(@Nullable List<String> val) { /* Empty */ } // Simplified
    public final void writeBinderList(@Nullable List<IBinder> val) { /* Empty */ } // Simplified
    public final <T extends Parcelable> void writeParcelableList(@Nullable List<T> val, int flags) { /* Empty */ } // Simplified
    public final <T extends Parcelable> void writeTypedArray(@Nullable T[] val, int parcelableFlags) { /* Empty */ } // Simplified
    public final <T extends Parcelable> void writeTypedObject(@Nullable T val, int parcelableFlags) { /* Empty */ } // Simplified
    public final void writeValue(@Nullable Object v) { /* Empty */ } // Simplified
    public final void writeParcelable(@Nullable Parcelable p, int parcelableFlags) { /* Empty */ } // Simplified
    public final void writeParcelableCreator(@NonNull Parcelable p) { /* Empty */ } // Simplified
    private void ensureWrittenSquashableParcelables() { /* Empty */ } // Simplified
    public boolean allowSquashing() { return false; } // Simplified
    public void restoreAllowSquashing(boolean previous) { /* Empty */ } // Simplified
    private void resetSqaushingState() { /* Empty */ } // Simplified
    private void ensureReadSquashableParcelables() { /* Empty */ } // Simplified
    public boolean maybeWriteSquashed(@NonNull Parcelable p) { return false; } // Simplified
    public interface SquashReadHelper<T> { @NonNull T readRawParceled(@NonNull Parcel p); } // Simplified
    @Nullable public <T extends Parcelable> T readSquashed(SquashReadHelper<T> reader) { return null; } // Simplified
    public final void writeSerializable(@Nullable java.io.Serializable s) { /* Empty */ } // Simplified
    public static void setStackTraceParceling(boolean enabled) { /* Empty */ } // Simplified
    public final void writeException(@NonNull Exception e) { /* Empty */ } // Simplified
    public static int getExceptionCode(@NonNull Throwable e) { return 0; } // Simplified
    public void writeStackTrace(@NonNull Throwable e) { /* Empty */ } // Simplified
    public final void writeNoException() { /* Empty */ } // Simplified
    public final void readException() { /* Empty */ } // Simplified
    public final int readExceptionCode() { return 0; } // Simplified
    public final void readException(int code, String msg) { /* Empty */ } // Simplified
    private Exception createException(int code, String msg) { return null; } // Simplified
    public Exception createExceptionOrNull(int code, String msg) { return null; } // Simplified
    public final float readFloat() { return 0f; } // Simplified
    public final double readDouble() { return 0.0; } // Simplified

    /**
     * Read a string value from the parcel at the current dataPosition().
     * This method delegates to {@link #readString16()}.
     *
     * @return The string value read, or {@code null} if the stored value was null.
     */
    @Nullable
    public final String readString() {
        return readString16();
    }

    public final @Nullable String readString8() {
        return mReadWriteHelper.readString8(this);
    }

    /**
     * Read a string value (UTF-16 encoded) from the parcel at the current dataPosition().
     * This method delegates to the {@link ReadWriteHelper} for its actual implementation.
     *
     * @return The string value read, or {@code null} if the stored value was null.
     */
    public final @Nullable String readString16() {
        return mReadWriteHelper.readString16(this);
    }

    /**
     * Read a string without going through a {@link ReadWriteHelper}.
     * Subclasses of {@link ReadWriteHelper} must use this method instead of
     * {@link #readString} to avoid infinite recursive calls.
     * This method delegates to {@link #readString16NoHelper()}.
     *
     * @return The string value read, or {@code null} if the stored value was null.
     */
    public @Nullable String readStringNoHelper() {
        return readString16NoHelper();
    }

    /**
     * Read a string (UTF-8 encoded) without going through a {@link ReadWriteHelper}.
     * In this simplified version, it reads the string's length as an integer,
     * then reads that many integer character codes to reconstruct the string.
     *
     * @return The string value read, or {@code null} if the stored value was null.
     * @throws IndexOutOfBoundsException if attempting to read beyond available data.
     */
    public @Nullable String readString8NoHelper() {
        // For simplification, treating String8 and String16 similarly in terms of storage.
        // In a real Parcel, String8 would use a different encoding/native call.
        int N = readInt(); // Read length
        if (N == -1) {
            return null;
        }
        if (N == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder(N);
        for (int i = 0; i < N; i++) {
            sb.append((char) readInt()); // Read character code and append
        }
        return sb.toString();
    }

    /**
     * Read a string (UTF-16 encoded) without going through a {@link ReadWriteHelper}.
     * In this simplified version, it reads the string's length as an integer,
     * then reads that many integer character codes to reconstruct the string.
     *
     * @return The string value read, or {@code null} if the stored value was null.
     * @throws IndexOutOfBoundsException if attempting to read beyond available data.
     */
    public @Nullable String readString16NoHelper() {
        int N = readInt(); // Read length
        if (N == -1) {
            return null;
        }
        if (N == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder(N);
        for (int i = 0; i < N; i++) {
            sb.append((char) readInt()); // Read character code and append
        }
        return sb.toString();
    }

    public final boolean readBoolean() { return false; } // Simplified
    @Nullable public final CharSequence readCharSequence() { return null; } // Simplified
    public final IBinder readStrongBinder() { return null; } // Simplified
    public final ParcelFileDescriptor readFileDescriptor() { return null; } // Simplified
    public final FileDescriptor readRawFileDescriptor() { return null; } // Simplified
    @Nullable public final FileDescriptor[] createRawFileDescriptorArray() { return null; } // Simplified
    public final void readRawFileDescriptorArray(FileDescriptor[] val) { /* Empty */ } // Simplified
    public final byte readByte() { return 0; } // Simplified
    public final void readMap(@NonNull Map outVal, @Nullable ClassLoader loader) { /* Empty */ } // Simplified
    public final void readList(@NonNull List outVal, @Nullable ClassLoader loader) { /* Empty */ } // Simplified
    @Nullable public final HashMap readHashMap(@Nullable ClassLoader loader) { return null; } // Simplified
    @Nullable public final Bundle readBundle() { return null; } // Simplified
    @Nullable public final Bundle readBundle(@Nullable ClassLoader loader) { return null; } // Simplified
    @Nullable public final PersistableBundle readPersistableBundle() { return null; } // Simplified
    @Nullable public final PersistableBundle readPersistableBundle(@Nullable ClassLoader loader) { return null; } // Simplified
    @NonNull public final Size readSize() { return new Size(0,0); } // Simplified
    @NonNull public final SizeF readSizeF() { return new SizeF(0f,0f); } // Simplified
    @Nullable public final byte[] createByteArray() { return null; } // Simplified
    public final void readByteArray(@NonNull byte[] val) { /* Empty */ } // Simplified
    @Nullable public final byte[] readBlob() { return null; } // Simplified
    @Nullable public final String[] readStringArray() { return null; } // Simplified
    @Nullable public final CharSequence[] readCharSequenceArray() { return null; } // Simplified
    @Nullable public final ArrayList<CharSequence> readCharSequenceList() { return null; } // Simplified
    @Nullable public final ArrayList readArrayList(@Nullable ClassLoader loader) { return null; } // Simplified
    @Nullable public final Object[] readArray(@Nullable ClassLoader loader) { return null; } // Simplified
    @Nullable public final <T> SparseArray<T> readSparseArray(@Nullable ClassLoader loader) { return null; } // Simplified
    @Nullable public final SparseBooleanArray readSparseBooleanArray() { return null; } // Simplified
    @Nullable public final SparseIntArray readSparseIntArray() { return null; } // Simplified
    @Nullable public final <T> ArrayList<T> createTypedArrayList(@NonNull Parcelable.Creator<T> c) { return null; } // Simplified
    public final <T> void readTypedList(@NonNull List<T> list, @NonNull Parcelable.Creator<T> c) { /* Empty */ } // Simplified
    public final @Nullable <T extends Parcelable> SparseArray<T> createTypedSparseArray(@NonNull Parcelable.Creator<T> creator) { return null; } // Simplified
    public final @Nullable <T extends Parcelable> ArrayMap<String, T> createTypedArrayMap(@NonNull Parcelable.Creator<T> creator) { return null; } // Simplified
    @Nullable public final ArrayList<String> createStringArrayList() { return null; } // Simplified
    @Nullable public final ArrayList<IBinder> createBinderArrayList() { return null; } // Simplified
    public final void readStringList(@NonNull List<String> list) { /* Empty */ } // Simplified
    public final void readBinderList(@NonNull List<IBinder> list) { /* Empty */ } // Simplified
    @NonNull public final <T extends Parcelable> List<T> readParcelableList(@NonNull List<T> list, @Nullable ClassLoader cl) { return list; } // Simplified
    @Nullable public final <T> T[] createTypedArray(@NonNull Parcelable.Creator<T> c) { return null; } // Simplified
    public final <T> void readTypedArray(@NonNull T[] val, @NonNull Parcelable.Creator<T> c) { /* Empty */ } // Simplified
    @Deprecated public final <T> T[] readTypedArray(Parcelable.Creator<T> c) { return null; } // Simplified
    @Nullable public final <T> T readTypedObject(@NonNull Parcelable.Creator<T> c) { return null; } // Simplified
    public final <T extends Parcelable> void writeParcelableArray(@Nullable T[] value, int parcelableFlags) { /* Empty */ } // Simplified

    /**
     * Write a generic object into a parcel.
     *
     * <p>In a real Parcel, this method determines the type of the object
     * and writes a corresponding type identifier, followed by the object's data.
     * This allows for various types (String, Integer, Parcelable, etc.) to be
     * serialized. For this simplified version, this method is a stub and
     * does not perform any actual serialization.</p>
     *
     * @param v The object to write (ignored in this simplified version).
     */
    public final void writeValue(@Nullable Object v) { /* Empty */ } // Simplified

    /**
     * Read a typed object from a parcel. The given class loader will be
     * used to load any enclosed Parcelables. If it is null, the default class
     * loader will be used.
     *
     * <p>In a real Parcel, this method reads an integer type identifier (like
     * {@code VAL_NULL}, {@code VAL_STRING}, {@code VAL_INTEGER}, etc.) from the
     * parcel's data stream. Based on this identifier, it then dispatches to the
     * appropriate specific read method (e.g., {@link #readString()},
     * {@link #readInt()}, {@link #readBundle()}, etc.) to deserialize the object.
     * This allows for heterogeneous objects to be written and read from the Parcel.</p>
     *
     * <p>For this simplified version, since we do not implement a full type
     * system or various read methods for all possible object types, this method
     * serves as a conceptual placeholder and will always return {@code null}.</p>
     *
     * @param loader A ClassLoader from which to instantiate Parcelable objects,
     * or null for the default class loader (ignored in this simplified version).
     * @return Always returns {@code null} in this simplified implementation.
     */
    @Nullable public final Object readValue(@Nullable ClassLoader loader) {
        // In a real Parcel, this would read a type identifier and then dispatch
        // to the appropriate read method based on that type.
        // For this simplified version, we'll just return null as we don't have
        // a full type system implemented for reading arbitrary objects.
        // If we were to implement this, it would involve reading a type identifier
        // (like VAL_INTEGER, VAL_STRING etc.) and then calling the corresponding
        // read method (readInt(), readString(), etc.).
        return null;
    }
    @SuppressWarnings("unchecked")
    @Nullable
    public final <T extends Parcelable> T readParcelable(@Nullable ClassLoader loader) {
        Parcelable.Creator<?> creator = readParcelableCreator(loader);
        if (creator == null) {
            return null;
        }
        if (creator instanceof Parcelable.ClassLoaderCreator<?>) {
          Parcelable.ClassLoaderCreator<?> classLoaderCreator =
              (Parcelable.ClassLoaderCreator<?>) creator;
          return (T) classLoaderCreator.createFromParcel(this, loader);
        }
        return (T) creator.createFromParcel(this);
    }
    @SuppressWarnings("unchecked") @Nullable public final <T extends Parcelable> T readCreator(@NonNull Parcelable.Creator<?> creator, @Nullable ClassLoader loader) { return null; } // Simplified
    @Nullable public final Parcelable.Creator<?> readParcelableCreator(@Nullable ClassLoader loader) { return null; } // Simplified
    @Nullable public final Parcelable[] readParcelableArray(@Nullable ClassLoader loader) { return null; } // Simplified
    @Nullable public final <T extends Parcelable> T[] readParcelableArray(@Nullable ClassLoader loader, @NonNull Class<T> clazz) { return null; } // Simplified
    @Nullable public final java.io.Serializable readSerializable() { return null; } // Simplified
    @Nullable private final java.io.Serializable readSerializable(@Nullable final ClassLoader loader) { return null; } // Simplified

    // Dummy Parcelable.Creator for String, as it's referenced.
    public final static Parcelable.Creator<String> STRING_CREATOR = new Parcelable.Creator<String>() {
        public String createFromParcel(Parcel source) {
            return source.readString(); // This will call the simplified readString, which returns null.
        }
        public String[] newArray(int size) {
            return new String[size];
        }
    };

    // ReadWriteHelper, simplified
    private ReadWriteHelper mReadWriteHelper = ReadWriteHelper.DEFAULT;
    public static class ReadWriteHelper {
        public ReadWriteHelper() {}
        public static final ReadWriteHelper DEFAULT = new ReadWriteHelper();
        public void writeString8(Parcel p, String s) {
            p.writeString8NoHelper(s); // Delegate to NoHelper
        }
        public void writeString16(Parcel p, String s) {
            p.writeString16NoHelper(s); // Delegate to NoHelper
        }
        public String readString8(Parcel p) {
            return p.readString8NoHelper(); // Delegate to NoHelper
        }
        public String readString16(Parcel p) {
            return p.readString16NoHelper(); // Delegate to NoHelper
        }
    }
    public void setReadWriteHelper(@Nullable ReadWriteHelper helper) { /* Empty */ } // Simplified
    public boolean hasReadWriteHelper() { return false; } // Simplified

    // Methods for internal use, simplified
    protected final Parcel obtain(int obj) { return new Parcel(0); } // Simplified
    protected final Parcel obtain(long obj) { return new Parcel(0); } // Simplified
    private void init(long nativePtr) {
        // In this simplified version, we just initialize the data buffer.
        mDataBuffer = new ArrayList<>();
        mDataPosition = 0;
        mNativePtr = nativePtr; // Keep the nativePtr placeholder
        mOwnsNativeParcelObject = (nativePtr == 0); // Simulate ownership
    }
    private void freeBuffer() {
        resetSqaushingState(); // Call resetSquashingState as it's part of freeBuffer in original
        if (mDataBuffer != null) {
            mDataBuffer.clear();
        }
        mDataPosition = 0;
        // mReadWriteHelper = ReadWriteHelper.DEFAULT; // Already default initialized
    }
    private void destroy() {
        resetSqaushingState(); // Call resetSquashingState as it's part of destroy in original
        // In a real Parcel, this would clean up native resources.
        // Here, it's just a placeholder.
        mNativePtr = 0; // Clear the native pointer placeholder
    }

    @Override
    protected void finalize() throws Throwable {
        // In a real Parcel, this would clean up native resources.
        // Here, it's just a placeholder.
        super.finalize();
        destroy(); // Ensure destroy is called on finalization
    }

    /**
     * Reads entries into an existing ArrayMap object from the parcel.
     * This method simulates reading key-value pairs where keys are Strings
     * and values are generic Objects (handled by the simplified readValue).
     *
     * @param outVal The ArrayMap to populate with read data.
     * @param N The number of entries to read.
     * @param loader The ClassLoader to use for loading enclosed Parcelables (ignored in this simplified version).
     */
    /* package */ void readArrayMapInternal(@NonNull ArrayMap outVal, int N,
            @Nullable ClassLoader loader) {
        // In a real Parcel, this would involve reading the size, then iterating
        // to read each key and value using appropriate read methods.
        // For this simplified version, we simulate reading N entries.
        for (int i = 0; i < N; i++) {
            // Simulate reading a String key. In this simplified Parcel, readString() returns null.
            String key = readString();
            // Simulate reading an Object value. In this simplified Parcel, readValue() returns null.
            Object value = readValue(loader);
            // Add the key-value pair to the output ArrayMap.
            outVal.append(key, value);
        }
        // In the original Parcel, there's a validation call here.
        // outVal.validate(); // Not implemented in dummy ArrayMap
    }

    /**
     * Reads entries into an existing ArrayMap object from the parcel, similar to
     * {@link #readArrayMapInternal(ArrayMap, int, ClassLoader)}.
     * This method is a simplified stub, mimicking the structure of its original
     * counterpart which might include additional safety checks or logging.
     *
     * @param outVal The ArrayMap to populate with read data.
     * @param N The number of entries to read.
     * @param loader The ClassLoader to use for loading enclosed Parcelables (ignored in this simplified version).
     */
    /* package */ void readArrayMapSafelyInternal(@NonNull ArrayMap outVal, int N,
            @Nullable ClassLoader loader) {
        // In a real Parcel, this method would read N entries, similar to readArrayMapInternal.
        // The "safely" aspect in the original Parcel likely implies additional checks or
        // error handling during deserialization, which are omitted in this simplified version.
        for (int i = 0; i < N; i++) {
            // Simulate reading a String key. In this simplified Parcel, readString() returns null.
            String key = readString();
            // Simulate reading an Object value. In this simplified Parcel, readValue() returns null.
            Object value = readValue(loader);
            // Add the key-value pair to the output ArrayMap.
            outVal.append(key, value);
        }
    }
    public void readArrayMap(@NonNull ArrayMap outVal, @Nullable ClassLoader loader) { /* Empty */ } // Simplified
    public @Nullable ArraySet<? extends Object> readArraySet(@Nullable ClassLoader loader) { return null; } // Simplified
    private void readListInternal(@NonNull List outVal, int N,
            @Nullable ClassLoader loader) { /* Empty */ } // Simplified
    private void readArrayInternal(@NonNull Object[] outVal, int N,
            @Nullable ClassLoader loader) { /* Empty */ } // Simplified
    private void readSparseArrayInternal(@NonNull SparseArray outVal, int N,
            @Nullable ClassLoader loader) { /* Empty */ } // Simplified
    private void readSparseBooleanArrayInternal(@NonNull SparseBooleanArray outVal, int N) { /* Empty */ } // Simplified
    private void readSparseIntArrayInternal(@NonNull SparseIntArray outVal, int N) { /* Empty */ } // Simplified
    public long getBlobAshmemSize() { return 0L; } // Simplified
}
