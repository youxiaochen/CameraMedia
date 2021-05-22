# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

#include头文件目录
include_directories(${PROJECT_SOURCE_DIR}/include)

#设置所有.cpp文件路径
file(GLOB SRC_CPP_LIST ${PROJECT_SOURCE_DIR}/*.cpp)

# 指定链接库文件目录
link_directories(${PROJECT_SOURCE_DIR}/jniLibs/${ANDROID_ABI})

add_library(
        yuv-utils
        SHARED
        ${SRC_CPP_LIST})

target_link_libraries(
        yuv-utils
        #       libyuv.so
        yuv
        log)