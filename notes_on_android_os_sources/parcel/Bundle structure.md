In Android, `Bundle` is a subclass of `android.os.BaseBundle`, essentially a **key-value mapping table (Map)** wrapper, whose data is transmitted via `Parcel` serialization. Below is the **original serialization structure of `Bundle`** (i.e., the byte structure after `Bundle.writeToParcel()`), which is key to understanding vulnerabilities (e.g., CVE-2023-20963) and custom Parcel construction.

---

## 🧱 Original Bundle Serialization Format (in Parcel)

```plaintext
[4 bytes] length                      // Total data length (excluding this field itself)
[4 bytes] 0x4C444E42 ('BNDL')         // Magic number, marks this as a Bundle
[...   ] Map<String, Object> serialized data (internally handled by Bundle.mMap write process)
```

The key content is **`mMap`** (an `ArrayMap<String, Object>`) serialization.

---

## 📦 mMap Serialization Structure

mMap (actually processed by `Parcel.writeMapInternal()`) uses the following format:

```plaintext
[4 bytes] N                          // Number of key-value pairs (int)
Repeat N times:
    [type marker]                   // Type marker, identifies the type of the value (e.g., TYPE_STRING = 0x03)
    [key]                           // String type key
    [value]                         // Value written according to type marker, e.g., writeString, writeParcelable, writeInt...
```

But inside `Bundle`, this is actually completed by `Parcel.writeBundle()` and `Parcel.writeValue()` together, supporting types including:

| Type         | Marker (type marker)   |
| ---------- | ------------------ |
| null       | -1                 |
| String     | 0x03               |
| IBinder    | 0x0D               |
| Intent     | 0x0C (i.e., Parcelable) |
| Bundle     | 0x0A               |
| Boolean    | 0x12               |
| Integer    | 0x0F               |
| ArrayList  | 0x0E               |
| Parcelable | 0x0C               |

---

## 🎯 Specific Construction Process (Example: Contains an Intent)

Fake Bundle:

```java
Bundle bundle = new Bundle();
bundle.putParcelable("intent", maliciousIntent);
```

Its Parcel data will be approximately as follows (hexadecimal structure):

```plaintext
00 00 00 xx     // Length (size of subsequent data)
4C 44 4E 42     // Magic number 'BNDL'
00 00 00 01     // 1 key-value pair
00 00 00 03     // Type: String (key)
...             // UTF-16 encoded string "intent"
00 00 00 0C     // Type: Parcelable (value)
...             // Serialized content of Parcelable object (Intent)
```

The Parcelable part will call `Intent.writeToParcel()`, including content like `ComponentName`, `Action`, `Extras`, `Flags`, `Data`, `Categories`, `ClipData`, etc.

---

## 📌 Reference: `Bundle.readFromParcel()` Process (Deserialization)

1. Read total length `length`
2. Verify magic is `'BNDL'`
3. Call `Parcel.readHashMap()` -> `Parcel.readValue()` to restore each value
4. Reflectively construct actual objects (e.g., `Intent`) using classloader

⚠️ The problem lies here: **Attackers can control the written content, and if system components blindly call `readValue()` + classloader, it may reflectively construct instances of attacker-specified classes.**

---

## 🧪 Advanced: Key Techniques for Constructing Payload

1. Strings in Parcel are UTF-16 encoded with a length prefix.
2. All structures need to be aligned (4-byte alignment).
3. When nesting `Parcelable` objects, correctly write their class name `writeString("android.content.Intent")`.
4. For `writeMap()`, `writeBundle()` will insert additional metadata (like magic number, length).

---

Do you need me to detail the original structure of constructing a Bundle with code or hexadecimal? I can print the entire chain from Java serialization -> Parcel -> Hexadecimal.
