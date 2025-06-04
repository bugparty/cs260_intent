I have extracted the core concepts and key methods defined in `Intent.java` and simplified the explanation of the `Intent` class to help you understand it.

**Core Concepts:**

- **Action:** Describes the general operation to be performed, such as viewing, editing, dialing, etc.
- **Data:** The data to be operated on, usually represented as a `Uri`, such as a contact URI or file URI.
- **Component:** The name of the specific component (Activity, Service, or BroadcastReceiver) to be explicitly started. If a Component is specified, the system will directly start that component, ignoring other Intent properties.
- **Category:** Provides additional context information for the Action, such as indicating that the app should appear in the launcher or as an optional operation for certain data.
- **Type:** The explicit MIME type of the data. The system usually infers the type based on the Data, but you can explicitly set it to enforce a specific type.
- **Extras:** A `Bundle` object used to carry additional key-value pair information for the target component.
- **Flags:** Various flags that control how the Intent is processed, such as starting an Activity in a new task or clearing the Activity stack.

**Key Methods (for operating core concepts):**

- **`setAction(String action)` / `getAction()`:** Set or get the Action of the Intent.
- **`setData(Uri data)` / `getData()`:** Set or get the Data (Uri) of the Intent.
- **`setType(String type)` / `getType()`:** Set or get the Type (MIME type) of the Intent.
- **`setDataAndType(Uri data, String type)`:** Set both Data and Type simultaneously.
- **`setComponent(ComponentName component)` / `getComponent()`:** Set or get the Component to be explicitly started.
- **`setClass(Context packageContext, Class<?> cls)`:** Set the Component using Context and Class objects.
- **`setClassName(String packageName, String className)` / `setClassName(Context packageContext, String className)`:** Set the Component using package name and class name.
- **`addCategory(String category)` / `getCategories()` / `hasCategory(String category)` / `removeCategory(String category)`:** Add, get, check, or remove the Category of the Intent.
- **`putExtra(String name, ...)` / `get...Extra(String name, ...)` / `getExtras()` / `hasExtra(String name)` / `removeExtra(String name)` / `replaceExtras(Bundle extras)`:** Add, get, check, remove, or replace Extras (data in the Bundle) of the Intent.
- **`setFlags(int flags)` / `getFlags()` / `addFlags(int flags)` / `removeFlags(int flags)`:** Set, get, add, or remove Flags of the Intent.
- **`setSelector(Intent selector)` / `getSelector()`:** Set or get the Selector of the Intent (used for more precise component matching).
- **`setClipData(ClipData clip)` / `getClipData()`:** Set or get the ClipData associated with the Intent (used to carry complex data, such as multiple URIs).

**Intent Types and Resolution:**

- **Explicit Intent:** Specifies the exact component to be started using `setComponent()` or `setClass()`. The system directly starts the component.
- **Implicit Intent:** Does not specify a specific component to be started but describes the operation to be performed and the type of data to be operated on using properties such as Action, Data, Type, and Category. The system resolves the Intent through the **Intent Resolution** process to find components that can handle the Intent.

**Implicit Intent Resolution Process:**

The system queries the `<intent-filter>` tags in the `AndroidManifest.xml` files of all installed applications. For a component to handle an implicit Intent, its `<intent-filter>` must meet the following conditions:

- **Action Match:** The `<action>` tag must include the Action of the Intent.
- **Data Match:** If the Intent contains Data, the `<data>` tag must match the scheme, host, path, and port of the Intent's Data (if specified).
- **Type Match:** If the Intent contains Type, the `android:mimeType` attribute of the `<data>` tag must match the Type of the Intent.
- **Category Match:** The `<category>` tag must include **all** Categories of the Intent. Typically, an Activity needs to support `android.intent.category.DEFAULT` to be resolved by `startActivity()`.

**Summary:**

The `Intent` class is key to communication between components in Android. It is an object that contains the operation to be performed, the data to be operated on, and other related information. By explicitly specifying the target component or relying on the system for implicit resolution, `Intent` achieves loose coupling and flexible interaction between applications. Understanding the core properties of `Intent` and how to use them is crucial for developing Android applications.

```java
package android.content;

import android.net.Uri;
import android.os.Bundle;
import android.content.ComponentName;
import java.util.Set;
import android.content.ClipData;

public class Intent {

    // Core properties
    private String action;
    private Uri data;
    private String type;
    private ComponentName component;
    private Set<String> categories;
    private Bundle extras;
    private int flags;
    private Intent selector;
    private ClipData clipData;

    // Constructors
    public Intent() {
    }

    public Intent(String action) {
        this.action = action;
    }

    public Intent(String action, Uri data) {
        this.action = action;
        this.data = data;
    }

    public Intent(Context packageContext, Class<?> cls) {
        this.component = new ComponentName(packageContext, cls);
    }

    // Simplified core methods
    public String getAction() {
        return action;
    }

    public Intent setAction(String action) {
        this.action = action;
        return this;
    }

    public Uri getData() {
        return data;
    }

    public Intent setData(Uri data) {
        this.data = data;
        this.type = null;
        return this;
    }

    public String getType() {
        return type;
    }

    public Intent setType(String type) {
        this.data = null;
        this.type = type;
        return this;
    }

    public Intent setDataAndType(Uri data, String type) {
        this.data = data;
        this.type = type;
        return this;
    }

    public ComponentName getComponent() {
        return component;
    }

    public Intent setComponent(ComponentName component) {
        this.component = component;
        return this;
    }

    public Intent setClass(Context packageContext, Class<?> cls) {
        this.component = new ComponentName(packageContext, cls);
        return this;
    }

    public Set<String> getCategories() {
        return categories;
    }

    public Intent addCategory(String category) {
        if (categories == null) {
            categories = new java.util.HashSet<>();
        }
        categories.add(category);
        return this;
    }

    public Bundle getExtras() {
        return extras;
    }

    public Intent putExtras(Bundle extras) {
        if (this.extras == null) {
            this.extras = new Bundle(extras);
        } else {
            this.extras.putAll(extras);
        }
        return this;
    }

    public <T> T getParcelableExtra(String name) {
        return extras == null ? null : extras.getParcelable(name);
    }

    public Intent putExtra(String name, android.os.Parcelable value) {
        if (extras == null) {
            extras = new Bundle();
        }
        extras.putParcelable(name, value);
        return this;
    }

    public int getFlags() {
        return flags;
    }

    public Intent setFlags(int flags) {
        this.flags = flags;
        return this;
    }

    public Intent addFlags(int flags) {
        this.flags |= flags;
        return this;
    }

    public Intent getSelector() {
        return selector;
    }

    public void setSelector(Intent selector) {
        this.selector = selector;
    }

    public ClipData getClipData() {
        return clipData;
    }

    public void setClipData(ClipData clipData) {
        this.clipData = clipData;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Intent { ");
        if (action != null) sb.append("act=").append(action).append(" ");
        if (data != null) sb.append("dat=").append(data).append(" ");
        if (type != null) sb.append("typ=").append(type).append(" ");
        if (component != null) sb.append("cmp=").append(component.flattenToShortString()).append(" ");
        if (categories != null && !categories.isEmpty()) sb.append("cat=").append(categories).append(" ");
        if (extras != null && !extras.isEmpty()) sb.append("(has extras) ");
        sb.append("}");
        return sb.toString();
    }
}
```