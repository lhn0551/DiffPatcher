# Diff Update SDK

独立差分更新库，封装 HDiffPatch JNI、C++ 编译和 Kotlin 上层 API。

## 远程依赖

通过 JitPack 引用，在 `settings.gradle.kts` 中添加仓库：

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}
```

在模块 `build.gradle.kts` 中添加依赖：

```kotlin
implementation("com.github.lhn0551:DiffPatcher:v1.0.0")
```

## 使用方式

业务侧优先调用 `DiffPatcher.patchAtomic(...)`，它会先写同目录临时文件，成功后再替换目标 APK，避免失败时留下半成品。

```kotlin
val result = DiffPatcher.patchAtomic(oldApk, diffFile, newApk)
if (result == 0) {
    // 合成成功
}
```

`DiffPatcher.patch(...)` 仍保留给需要自行管理临时文件的调用方。默认 cache memory 为 8 MiB；当前 native 编译未启用多线程，`threadCount` 会限制为 1。`patchAtomic(...)` 会拒绝输出路径与旧文件或差分包相同的调用，返回 `PATCH_OUTPUT_PATH_ERROR`。

## 本地验证

```bash
./gradlew assembleRelease
./gradlew publishReleasePublicationToMavenLocal
```
