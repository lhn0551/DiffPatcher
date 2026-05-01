# diff-update-sdk

独立差分更新库，封装 HDiffPatch JNI、C++ 编译和 Kotlin 上层 API。

## 打包

```bash
./gradlew assembleRelease
```

生成产物：

```text
build/outputs/aar/diff-update-sdk-release.aar
```

替换到 Deka：

```bash
cp build/outputs/aar/diff-update-sdk-release.aar ../DeKa/app/libs/diff-update-sdk-release.aar
```

## 接入

Deka 依赖打出的 AAR：

```kotlin
implementation(files("libs/diff-update-sdk-release.aar"))
```

业务侧优先调用 `DiffPatcher.patchAtomic(...)`，它会先写同目录临时文件，成功后再替换目标 APK，避免失败时留下半成品。

```kotlin
val result = DiffPatcher.patchAtomic(oldApk, diffFile, newApk)
if (result == 0) {
    // 合成成功
}
```

`DiffPatcher.patch(...)` 仍保留给需要自行管理临时文件的调用方。默认 cache memory 为 8 MiB；当前 native 编译未启用多线程，`threadCount` 会限制为 1。更新 SDK 代码后，重新打包并替换 Deka 的 `app/libs/diff-update-sdk-release.aar`。

## 校验

```bash
./gradlew test lintRelease assembleRelease
```
