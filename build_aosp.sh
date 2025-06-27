#!/bin/bash
# Script to build AOSP for Android SDK emulator
AOSP_DIR=/home/bowman/mnt/aosp
pushd $AOSP_DIR
source ./build/envsetup.sh
export BUILD_HOST_cross_windows=False
lunch sdk_phone_x86_64-eng
make -j$(nproc)
make -j$(nproc) sdk sdk_repo
popd
