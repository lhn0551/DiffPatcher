cmake_minimum_required(VERSION 3.22.1)

project(deka_hpatch)

add_library(
    hpatchz
    SHARED
    HDiffPatch/builds/android_ndk_jni_mk/hpatch.c
    HDiffPatch/builds/android_ndk_jni_mk/hpatch_jni.c
    HDiffPatch/file_for_patch.c
    HDiffPatch/libHDiffPatch/HPatch/patch.c
)

find_library(log-lib log)

target_compile_definitions(
    hpatchz
    PRIVATE
    ANDROID_NDK
    NDEBUG
    _LARGEFILE_SOURCE
    _IS_NEED_DEFAULT_CompressPlugin=0
    _IS_NEED_CACHE_OLD_BY_COVERS=0
    _IS_NEED_CACHE_OLD_ALL=1
    _IS_USED_MULTITHREAD=0
    _IS_NEED_DIR_DIFF_PATCH=0
    _IS_NEED_BSDIFF=0
    _IS_NEED_VCDIFF=0
    _CompressPlugin_zlib
)

target_link_libraries(hpatchz ${log-lib} z)
