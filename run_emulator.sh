#!/bin/bash
AOSP_DIR=/home/bowman/mnt/aosp
ANDROID_SDK_DIR=/home/bowman/Android/Sdk
pushd $AOSP_DIR
source build/envsetup.sh
lunch sdk_phone_x86_64-eng
${ANDROID_SDK_DIR}/emulator/emulator -writable-system
popd