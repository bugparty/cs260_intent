//see also https://android.googlesource.com/platform/frameworks/native/+/android12-dev/libs/binder/Parcel.cpp
status_t Parcel::readString16(std::optional<String16>* pArg) const { return readData(pArg); }
status_t Parcel::readString16(std::unique_ptr<String16>* pArg) const { return readData(pArg); }
template <typename T>
binder_status_t ReadArray(const AParcel* parcel, void* arrayData, ArrayAllocator<T> allocator,
                          ArraySetter<T> setter, status_t (Parcel::*read)(T*) const) {
    const Parcel* rawParcel = parcel->get();

    int32_t length;
    status_t status = rawParcel->readInt32(&length);
    //if length is -1,just return
    // This collapses the statuses into the declared range.
    //binder_status_t PruneStatusT(android::status_t status);
    if (status != STATUS_OK) return PruneStatusT(status);
    if (length < -1) return STATUS_BAD_VALUE;

    if (!allocator(arrayData, length)) return STATUS_NO_MEMORY;

    if (length <= 0) return STATUS_OK;

    for (int32_t i = 0; i < length; i++) {
        T readTarget;
        status = (rawParcel->*read)(&readTarget);
        if (status != STATUS_OK) return PruneStatusT(status);

        setter(arrayData, i, readTarget);
    }

    return STATUS_OK;
}

status_t Parcel::writeBool(bool val)
{
    return writeInt32(int32_t(val));
}
status_t Parcel::writeChar(char16_t val)
{
    return writeInt32(int32_t(val));
}
status_t Parcel::writeByte(int8_t val)
{
    return writeInt32(int32_t(val));
}
status_t Parcel::writeInt64(int64_t val)
{
    return writeAligned(val);
}
status_t Parcel::writeUint64(uint64_t val)
{
    return writeAligned(val);
}
status_t Parcel::writePointer(uintptr_t val)
{
    return writeAligned<binder_uintptr_t>(val);
}
status_t Parcel::writeFloat(float val)
{
    return writeAligned(val);
}
#if defined(__mips__) && defined(__mips_hard_float)
status_t Parcel::writeDouble(double val)
{
    union {
        double d;
        unsigned long long ll;
    } u;
    u.d = val;
    return writeAligned(u.ll);
}
#else
status_t Parcel::writeDouble(double val)
{
    return writeAligned(val);
}
#endif
status_t Parcel::writeCString(const char* str)
{
    return write(str, strlen(str)+1);
}
status_t Parcel::writeString8(const String8& str)
{
    return writeString8(str.string(), str.size());
}
status_t Parcel::writeString8(const char* str, size_t len)
{
    if (str == nullptr) return writeInt32(-1);
    // NOTE: Keep this logic in sync with android_os_Parcel.cpp
    status_t err = writeInt32(len);
    if (err == NO_ERROR) {
        uint8_t* data = (uint8_t*)writeInplace(len+sizeof(char));
        if (data) {
            memcpy(data, str, len);
            *reinterpret_cast<char*>(data+len) = 0;
            return NO_ERROR;
        }
        err = mError;
    }
    return err;
}
status_t Parcel::writeString16(const String16& str)
{
    return writeString16(str.string(), str.size());
}
status_t Parcel::writeString16(const char16_t* str, size_t len)
{
    if (str == nullptr) return writeInt32(-1);
    // NOTE: Keep this logic in sync with android_os_Parcel.cpp
    status_t err = writeInt32(len);
    if (err == NO_ERROR) {
        len *= sizeof(char16_t);
        uint8_t* data = (uint8_t*)writeInplace(len+sizeof(char16_t));
        if (data) {
            memcpy(data, str, len);
            *reinterpret_cast<char16_t*>(data+len) = 0;
            return NO_ERROR;
        }
        err = mError;
    }
    return err;
}
