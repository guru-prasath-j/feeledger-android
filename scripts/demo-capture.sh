#!/usr/bin/env bash
# Installs the debug build on a running emulator, runs the scripted walkthrough,
# records the screen, and copies screenshots + video into docs/.
# Always collects outputs (instrumentation result, logcat) so a failure can be diagnosed.
set -uo pipefail
PKG=dev.guruprasath.feeledger

adb wait-for-device
adb shell 'while [[ -z $(getprop sys.boot_completed) ]]; do sleep 1; done'
# No soft keyboard in the captures: text is entered through Compose semantics, not the IME.
for ime in $(adb shell ime list -s | tr -d '\r'); do adb shell ime disable "$ime" || true; done

adb install -r app/build/outputs/apk/debug/app-debug.apk
adb install -r app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk
adb shell pm grant $PKG android.permission.POST_NOTIFICATIONS || true
adb logcat -c || true

adb shell screenrecord --bit-rate 6000000 --time-limit 170 /sdcard/demo.mp4 &
sleep 2
adb shell am instrument -w -e class $PKG.demo.DemoWalkthroughTest $PKG.test/androidx.test.runner.AndroidJUnitRunner > instrument.txt 2>&1
cat instrument.txt
sleep 3
adb shell pkill -INT screenrecord || true
sleep 4

adb logcat -d -t 4000 > logcat.txt || true
mkdir -p docs/screenshots /tmp/shots
adb exec-out run-as $PKG tar cf - files/screenshots | tar xf - -C /tmp/shots || true
cp /tmp/shots/files/screenshots/*.png docs/screenshots/ || true
adb pull /sdcard/demo.mp4 docs/demo.mp4 || true
ls -la docs docs/screenshots || true

grep -q "^OK (" instrument.txt
