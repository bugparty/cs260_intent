Now let's talk about the **format structure of `Bundle` during serialization (writeToParcel)**, which is key to understanding Binder testing and Parcel content analysis.

---

## 🧱 `Bundle` Serialization Format (via `Parcel.writeToParcel()`)

### Suppose we have a simple Bundle:

```java
Bundle b = new Bundle();
b.putString("name", "Bowen");
b.putInt("age", 18);
```

### Its write format is as follows (order matters):

```
[0] int: number of entries (key-value pairs) → 2

for each entry:
  [1] String: key ("name")
  [2] int:    type marker (VAL_STRING)
  [3] String: value ("Bowen")

  [4] String: key ("age")
  [5] int:    type marker (VAL_INTEGER)
  [6] int:    value (18)
```

---

## 📦 Actual Structure (according to Parcel write order)

| Order  | Type       | Meaning                |
| --- | -------- | ----------------- |
| 1   | `int`    | Number of entries `count`      |
| 2   | `String` | key               |
| 3   | `int`    | Type marker (type marker) |
| 4   | (varies) | value content, depending on type    |
| ... | Repeat N times     | Each key-value pair    |

---

## 🎯 Type Markers (type marker)

These are the numeric constants used by `Parcel` to mark "what type to read next":

| Type           | Constant Name              | Integer Value |
| ------------ | ---------------- | --- |
| `null`       | `VAL_NULL`       | -1  |
| `String`     | `VAL_STRING`     | 0   |
| `Integer`    | `VAL_INTEGER`    | 1   |
| `Map`        | `VAL_MAP`        | 2   |
| `Bundle`     | `VAL_BUNDLE`     | 3   |
| `Parcelable` | `VAL_PARCELABLE` | 4   |
| `Short`      | `VAL_SHORT`      | 8   |
| `Float`      | `VAL_FLOAT`      | 9   |
| `Double`     | `VAL_DOUBLE`     | 10  |
| ...          | ...              | ... |

These values are controlled by AOSP's `Parcel.readValue()` / `writeValue()`.

---

## 🔄 Where Does Writing Happen?

Calling `Bundle.writeToParcel(Parcel dest, int flags)` eventually calls:

```java
Parcel.writeInt(count);
for each (entry in mMap):
    writeString(key);
    writeValue(value); // Writes type + value
```

---

## 🧪 Example Serialization Result (from a binary perspective)

```text
02 00 00 00                     # int: 2 entries
06 00 00 00  'n' 'a' 'm' 'e'    # key: "name"
00 00 00 00                     # type: VAL_STRING
05 00 00 00  'B' 'o' 'w' 'e' 'n'# value: "Bowen"

03 00 00 00  'a' 'g' 'e'        # key: "age"
01 00 00 00                     # type: VAL_INTEGER
12 00 00 00                     # value: 18
```

> Note: Strings are actually encoded in UTF-16 variable-length; this is just for intuitive understanding.

---

## 🧠 Summary

| Step       | Content                          |
| -------- | --------------------------- |
| First int  | Number of key-value pairs         |
| Each entry | key → type marker → value   |
| value type | Determines the actual written structure |
| All data     | Serialized as `Parcel`, can be sent via Binder |

---

