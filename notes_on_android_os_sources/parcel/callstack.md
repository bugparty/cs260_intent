![[Pasted image 20250525210005.png]]\

callstack

```
Binder:589_9"@28,708 in group "main": RUNNING
<init>:131, WorkSource (android.os)
createFromParcel:1250, WorkSource$1 (android.os)
createFromParcel:1248, WorkSource$1 (android.os)
readParcelable:3333, Parcel (android.os)
readValue:3226, Parcel (android.os)
readArrayMapInternal:3623, Parcel (android.os)
initializeFromParcelLocked:292, BaseBundle (android.os)
unparcel:236, BaseBundle (android.os)
getBoolean:935, BaseBundle (android.os)
onResult:5005, AccountManagerService$Session (com.android.server.accounts)
onTransact:106, IAccountAuthenticatorResponse$Stub (android.accounts)
execTransactInternal:1184, Binder (android.os)
execTransact:1143, Binder (android.os)
```

Code from parcel.java trying to load the WorkSource Parcel
![[Pasted image 20250525220337.png]]

