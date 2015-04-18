#include <string.h>
#include <jni.h>
#include "MainActivity.h"

jstring Java_com_alk_receiptcap_v03_MainActivity_onCreate(JNIEnv* env, jobject javaThis) {
  return (*env)->NewStringUTF(env, "ndk");
}
