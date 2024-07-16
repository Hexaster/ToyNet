cmake_minimum_required(VERSION 3.28)
project(TensorObj)

set(CMAKE_CXX_STANDARD 17)
set(JAVA_HOME "/Users/u7630167/Library/Java/JavaVirtualMachines/openjdk-22.0.1/Contents/Home")

include_directories(${JAVA_HOME}/include)
include_directories(${JAVA_HOME}/include/darwin)



add_library(TensorObj SHARED TensorObj.cpp)
