#!/bin/bash
DEBUG_PORT=8700                   # Used for remote JDWP attach
PACKAGE_PID=""                    # PID of system_server or other package

adb root
adb remount
adb reboot
sleep 10
adb wait-for-device
adb root
adb remount
adb shell setprop persist.sys.dalvik.vm.lib.2 libart.so
adb shell setprop dalvik.vm.debug.enable-jdwp true

echo "🔍 Searching for system_server PID..."
PACKAGE_PID=$(adb shell pidof system_server | tr -d '\r')
if [ -z "$PACKAGE_PID" ]; then
    echo "❌ system_server PID not found, it may not have started yet"
    exit 1
fi

echo "✅ Found system_server PID: $PACKAGE_PID"

echo "🔌 Setting JDWP forward port..."
adb forward tcp:$DEBUG_PORT jdwp:$PACKAGE_PID
