#!/bin/bash
DEBUG_PORT=8700                   # 用于远程 JDWP attach
PACKAGE_PID=""                    # system_server 或其他包的 PID

adb root
adb remount
adb reboot
sleep 10
adb wait-for-device
adb root
adb remount
adb shell setprop persist.sys.dalvik.vm.lib.2 libart.so
adb shell setprop dalvik.vm.debug.enable-jdwp true

echo "🔍 查找 system_server PID..."
PACKAGE_PID=$(adb shell pidof system_server | tr -d '\r')
if [ -z "$PACKAGE_PID" ]; then
    echo "❌ 未找到 system_server PID，可能还未启动完成"
    exit 1
fi

echo "✅ 找到 system_server PID: $PACKAGE_PID"

echo "🔌 设置 JDWP forward 端口..."
adb forward tcp:$DEBUG_PORT jdwp:$PACKAGE_PID
