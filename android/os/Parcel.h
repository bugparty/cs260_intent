//https://android.googlesource.com/platform/frameworks/native/+/refs/heads/android12-dev/libs/binder/include/binder/Parcel.h
//-----------------------------------------------------------------------------
    // Generic type read and write methods for Parcel:
    //
    // readData(T *value) will read a value from the Parcel.
    // writeData(const T& value) will write a value to the Parcel.
    //
    // Our approach to parceling is based on two overloaded functions
    // readData() and writeData() that generate parceling code for an
    // object automatically based on its type. The code from templates are generated at
    // compile time (if constexpr), and decomposes an object through a call graph matching
    // recursive descent of the template typename.
    //
    // This approach unifies handling of complex objects,
    // resulting in fewer lines of code, greater consistency,
    // extensibility to nested types, efficiency (decisions made at compile time),
    // and better code maintainability and optimization.
    //
    // Design decision: Incorporate the read and write code into Parcel rather than
    // as a non-intrusive serializer that emits a byte stream, as we have
    // active objects, alignment, legacy code, and historical idiosyncrasies.
    //
    // --- Overview
    //
    // Parceling is a way of serializing objects into a sequence of bytes for communication
    // between processes, as part of marshaling data for remote procedure calls.
    //
    // The Parcel instance contains objects serialized as bytes, such as the following:
    //
    // 1) Ordinary primitive data such as int, float.
    // 2) Established structured data such as String16, std::string.
    // 3) Parcelables, which are C++ objects that derive from Parcelable (and thus have a
    //    readFromParcel and writeToParcel method).  (Similar for Java)
    // 4) A std::vector<> of such data.
    // 5) Nullable objects contained in std::optional, std::unique_ptr, or std::shared_ptr.
    //
    // And active objects from the Android ecosystem such as:
    // 6) File descriptors, base::unique_fd (kernel object handles)
    // 7) Binder objects, sp<IBinder> (active Android RPC handles)
    //
    // Objects from (1) through (5) serialize into the mData buffer.
    // Active objects (6) and (7) serialize into both mData and mObjects buffers.
    //
    // --- Data layout details
    //
    // Data is read or written to the parcel by recursively decomposing the type of the parameter
    // type T through readData() and writeData() methods.
    //
    // We focus on writeData() here in our explanation of the data layout.
    //
    // 1) Alignment
    // Implementation detail: Regardless of the parameter type, writeData() calls are designed
    // to finish at a multiple of 4 bytes, the default alignment of the Parcel.
    //
    // Writes of single uint8_t, int8_t, enums based on types of size 1, char16_t, etc
    // will result in 4 bytes being written.  The data is widened to int32 and then written;
    // hence the position of the nonzero bytes depend on the native endianness of the CPU.
    //
    // Writes of primitive values with 8 byte size, double, int64_t, uint64_t,
    // are stored with 4 byte alignment.  The ARM and x86/x64 permit unaligned reads
    // and writes (albeit with potential latency/throughput penalty) which may or may
    // not be observable unless the process is IO bound.
    //
    // 2) Parcelables
    // Parcelables are detected by the type's base class, and implemented through calling
    // into the Parcelable type's readFromParcel() or writeToParcel() methods.
    // Historically, due to null object detection, a (int32_t) 1 is prepended to the data written.
    // Parcelables must have a default constructor (i.e. one that takes no arguments).
    //
    // 3) Arrays
    // Arrays of uint8_t and int8_t, and enums based on size 1 are written as
    // a contiguous packed byte stream.  Hidden zero padding is applied at the end of the byte
    // stream to make a multiple of 4 bytes (and prevent info leakage when writing).
    //
    // All other array writes can be conceptually thought of as recursively calling
    // writeData on the individual elements (though may be implemented differently for speed).
    // As discussed in (1), alignment rules are therefore applied for each element
    // write (not as an aggregate whole), so the wire representation of data can be
    // substantially larger.
    //
    // Historical Note:
    // Because of element-wise alignment, CharVector and BoolVector are expanded
    // element-wise into integers even though they could have been optimized to be packed
    // just like uint8_t, int8_t (size 1 data).
    //
    // 3.1) Arrays accessed by the std::vector type.  This is the default for AIDL.
    //
    // 4) Nullables
    // std::optional, std::unique_ptr, std::shared_ptr are all parceled identically
    // (i.e. result in identical byte layout).
    // The target of the std::optional, std::unique_ptr, or std::shared_ptr
    // can either be a std::vector, String16, std::string, or a Parcelable.
    //
    // Detection of null relies on peeking the first int32 data and checking if the
    // the peeked value is considered invalid for the object:
    // (-1 for vectors, String16, std::string) (0 for Parcelables).  If the peeked value
    // is invalid, then a null is returned.
    //
    // Application Note: When to use each nullable type:
    //
    // std::optional: Embeds the object T by value rather than creating a new instance
    // by managed pointer as std::unique_ptr or std::shared_ptr.  This will save a malloc
    // when creating an optional instance.
    //
    // Use of std::optionals by value can result in copies of the underlying value stored in it,
    // so a std::move may be used to move in and move out (for example) a vector value into
    // the std::optional or for the std::optional itself.
    //
    // std::unique_ptr, std::shared_ptr: These are preferred when the lifetime of the object is
    // already managed by the application.  This reduces unnecessary copying of data
    // especially when the calls are local in-proc (rather than via binder rpc).
    //
    // 5) StrongBinder (sp<IBinder>)
    // StrongBinder objects are written regardless of null. When read, null StrongBinder values
    // will be interpreted as UNKNOWN_ERROR if the type is a single argument <sp<T>>
    // or in a vector argument <std::vector<sp<T>>. However, they will be read without an error
    // if present in a std::optional, std::unique_ptr, or std::shared_ptr vector, e.g.
    // <std::optional<std::vector<sp<T>>>.
    //
    // See AIDL annotation @Nullable, readStrongBinder(), and readNullableStrongBinder().
    //
    // Historical Note: writing a vector of StrongBinder objects <std::vector<sp<T>>
    // containing a null will not cause an error. However reading such a vector will cause
    // an error _and_ early termination of the read.
    //  --- Examples
    //
    // Using recursive parceling, we can parcel complex data types so long
    // as they obey the rules described above.
    //
    // Example #1
    // Parceling of a 3D vector
    //
    // std::vector<std::vector<std::vector<int32_t>>> v1 {
    //     { {1}, {2, 3}, {4} },
    //     {},
    //     { {10}, {20}, {30, 40} },
    // };
    // Parcel p1;
    // p1.writeData(v1);
    // decltype(v1) v2;
    // p1.setDataPosition(0);
    // p1.readData(&v2);
    // ASSERT_EQ(v1, v2);
    //
    // Example #2
    // Parceling of mixed shared pointers
    //
    // Parcel p1;
    // auto sp1 = std::make_shared<std::vector<std::shared_ptr<std::vector<int>>>>(3);
    // (*sp1)[2] = std::make_shared<std::vector<int>>(3);
    // (*(*sp1)[2])[2] = 2;
    // p1.writeData(sp1);
    // decltype(sp1) sp2;
    // p1.setDataPosition(0);
    // p1.readData(&sp2);
    // ASSERT_EQ((*sp1)[0], (*sp2)[0]); // nullptr
    // ASSERT_EQ((*sp1)[1], (*sp2)[1]); // nullptr
    // ASSERT_EQ(*(*sp1)[2], *(*sp2)[2]); // { 0, 0, 2}
    //  --- Helper Methods
    // TODO: move this to a utils header.
    //
    // Determine if a type is a specialization of a templated type
    // Example: is_specialization_v<T, std::vector>
    template <typename Test, template <typename...> class Ref>
    struct is_specialization : std::false_type {};
    template <template <typename...> class Ref, typename... Args>
    struct is_specialization<Ref<Args...>, Ref>: std::true_type {};
    template <typename Test, template <typename...> class Ref>
    static inline constexpr bool is_specialization_v = is_specialization<Test, Ref>::value;
    // Get the first template type from a container, the T from MyClass<T, ...>.
    template<typename T> struct first_template_type;
    template <template <typename ...> class V, typename T, typename... Args>
    struct first_template_type<V<T, Args...>> {
        using type_t = T;
    };
    template <typename T>
    using first_template_type_t = typename first_template_type<T>::type_t;
    // For static assert(false) we need a template version to avoid early failure.
    template <typename T>
    static inline constexpr bool dependent_false_v = false;
    // primitive types that we consider packed and trivially copyable as an array
    template <typename T>
    static inline constexpr bool is_pointer_equivalent_array_v =
            std::is_same_v<T, int8_t>
            || std::is_same_v<T, uint8_t>
            // We could support int16_t and uint16_t, but those aren't currently AIDL types.
            || std::is_same_v<T, int32_t>
            || std::is_same_v<T, uint32_t>
            || std::is_same_v<T, float>
            // are unaligned reads and write support is assumed.
            || std::is_same_v<T, uint64_t>
            || std::is_same_v<T, int64_t>
            || std::is_same_v<T, double>
            || (std::is_enum_v<T> && (sizeof(T) == 1 || sizeof(T) == 4)); // size check not type
    // allowed "nullable" types
    // These are nonintrusive containers std::optional, std::unique_ptr, std::shared_ptr.
    template <typename T>
    static inline constexpr bool is_parcel_nullable_type_v =
            is_specialization_v<T, std::optional>
            || is_specialization_v<T, std::unique_ptr>
            || is_specialization_v<T, std::shared_ptr>;
    // special int32 value to indicate NonNull or Null parcelables
    // This is fixed to be only 0 or 1 by contract, do not change.
    static constexpr int32_t kNonNullParcelableFlag = 1;
    static constexpr int32_t kNullParcelableFlag = 0;
    // special int32 size representing a null vector, when applicable in Nullable data.
    // This fixed as -1 by contract, do not change.
    static constexpr int32_t kNullVectorSize = -1;
    // --- readData and writeData methods.
    // We choose a mixture of function and template overloads to improve code readability.
    // TODO: Consider C++20 concepts when they become available.
    // writeData function overloads.
    // Implementation detail: Function overloading improves code readability over
    // template overloading, but prevents writeData<T> from being used for those types.
    status_t writeData(bool t) {
        return writeBool(t);  // this writes as int32_t
    }
    status_t writeData(int8_t t) {
        return writeByte(t);  // this writes as int32_t
    }
    status_t writeData(uint8_t t) {
        return writeByte(static_cast<int8_t>(t));  // this writes as int32_t
    }
    status_t writeData(char16_t t) {
        return writeChar(t);  // this writes as int32_t
    }
    status_t writeData(int32_t t) {
        return writeInt32(t);
    }
    status_t writeData(uint32_t t) {
        return writeUint32(t);
    }
    status_t writeData(int64_t t) {
        return writeInt64(t);
    }
    status_t writeData(uint64_t t) {
        return writeUint64(t);
    }
    status_t writeData(float t) {
        return writeFloat(t);
    }
    status_t writeData(double t) {
        return writeDouble(t);
    }
    status_t writeData(const String16& t) {
        return writeString16(t);
    }
    status_t writeData(const std::string& t) {
        return writeUtf8AsUtf16(t);
    }
    status_t writeData(const base::unique_fd& t) {
        return writeUniqueFileDescriptor(t);
    }
    status_t writeData(const Parcelable& t) {  // std::is_base_of_v<Parcelable, T>
        // implemented here. writeParcelable() calls this.
        status_t status = writeData(static_cast<int32_t>(kNonNullParcelableFlag));
        if (status != OK) return status;
        return t.writeToParcel(this);
    }
    // writeData<T> template overloads.
    // Written such that the first template type parameter is the complete type
    // of the first function parameter.
    template <typename T,
            typename std::enable_if_t<std::is_enum_v<T>, bool> = true>
    status_t writeData(const T& t) {
        // implemented here. writeEnum() calls this.
        using UT = std::underlying_type_t<T>;
        return writeData(static_cast<UT>(t)); // recurse
    }
    template <typename T,
            typename std::enable_if_t<is_specialization_v<T, sp>, bool> = true>
    status_t writeData(const T& t) {
        return writeStrongBinder(t);
    }