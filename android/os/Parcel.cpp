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