/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_example_hellojni_HelloJni */

#ifndef _Included_com_example_hellojni_HelloJni
#define _Included_com_example_hellojni_HelloJni
#ifdef __cplusplus
extern "C" {
#endif
#undef com_example_hellojni_HelloJni_MODE_PRIVATE
#define com_example_hellojni_HelloJni_MODE_PRIVATE 0L
#undef com_example_hellojni_HelloJni_MODE_WORLD_READABLE
#define com_example_hellojni_HelloJni_MODE_WORLD_READABLE 1L
#undef com_example_hellojni_HelloJni_MODE_WORLD_WRITEABLE
#define com_example_hellojni_HelloJni_MODE_WORLD_WRITEABLE 2L
#undef com_example_hellojni_HelloJni_MODE_APPEND
#define com_example_hellojni_HelloJni_MODE_APPEND 32768L
#undef com_example_hellojni_HelloJni_MODE_MULTI_PROCESS
#define com_example_hellojni_HelloJni_MODE_MULTI_PROCESS 4L
#undef com_example_hellojni_HelloJni_MODE_ENABLE_WRITE_AHEAD_LOGGING
#define com_example_hellojni_HelloJni_MODE_ENABLE_WRITE_AHEAD_LOGGING 8L
#undef com_example_hellojni_HelloJni_BIND_AUTO_CREATE
#define com_example_hellojni_HelloJni_BIND_AUTO_CREATE 1L
#undef com_example_hellojni_HelloJni_BIND_DEBUG_UNBIND
#define com_example_hellojni_HelloJni_BIND_DEBUG_UNBIND 2L
#undef com_example_hellojni_HelloJni_BIND_NOT_FOREGROUND
#define com_example_hellojni_HelloJni_BIND_NOT_FOREGROUND 4L
#undef com_example_hellojni_HelloJni_BIND_ABOVE_CLIENT
#define com_example_hellojni_HelloJni_BIND_ABOVE_CLIENT 8L
#undef com_example_hellojni_HelloJni_BIND_ALLOW_OOM_MANAGEMENT
#define com_example_hellojni_HelloJni_BIND_ALLOW_OOM_MANAGEMENT 16L
#undef com_example_hellojni_HelloJni_BIND_WAIVE_PRIORITY
#define com_example_hellojni_HelloJni_BIND_WAIVE_PRIORITY 32L
#undef com_example_hellojni_HelloJni_BIND_IMPORTANT
#define com_example_hellojni_HelloJni_BIND_IMPORTANT 64L
#undef com_example_hellojni_HelloJni_BIND_ADJUST_WITH_ACTIVITY
#define com_example_hellojni_HelloJni_BIND_ADJUST_WITH_ACTIVITY 128L
#undef com_example_hellojni_HelloJni_CONTEXT_INCLUDE_CODE
#define com_example_hellojni_HelloJni_CONTEXT_INCLUDE_CODE 1L
#undef com_example_hellojni_HelloJni_CONTEXT_IGNORE_SECURITY
#define com_example_hellojni_HelloJni_CONTEXT_IGNORE_SECURITY 2L
#undef com_example_hellojni_HelloJni_CONTEXT_RESTRICTED
#define com_example_hellojni_HelloJni_CONTEXT_RESTRICTED 4L
#undef com_example_hellojni_HelloJni_RESULT_CANCELED
#define com_example_hellojni_HelloJni_RESULT_CANCELED 0L
#undef com_example_hellojni_HelloJni_RESULT_OK
#define com_example_hellojni_HelloJni_RESULT_OK -1L
#undef com_example_hellojni_HelloJni_RESULT_FIRST_USER
#define com_example_hellojni_HelloJni_RESULT_FIRST_USER 1L
/* Inaccessible static: FOCUSED_STATE_SET */
#undef com_example_hellojni_HelloJni_DEFAULT_KEYS_DISABLE
#define com_example_hellojni_HelloJni_DEFAULT_KEYS_DISABLE 0L
#undef com_example_hellojni_HelloJni_DEFAULT_KEYS_DIALER
#define com_example_hellojni_HelloJni_DEFAULT_KEYS_DIALER 1L
#undef com_example_hellojni_HelloJni_DEFAULT_KEYS_SHORTCUT
#define com_example_hellojni_HelloJni_DEFAULT_KEYS_SHORTCUT 2L
#undef com_example_hellojni_HelloJni_DEFAULT_KEYS_SEARCH_LOCAL
#define com_example_hellojni_HelloJni_DEFAULT_KEYS_SEARCH_LOCAL 3L
#undef com_example_hellojni_HelloJni_DEFAULT_KEYS_SEARCH_GLOBAL
#define com_example_hellojni_HelloJni_DEFAULT_KEYS_SEARCH_GLOBAL 4L
/*
 * Class:     com_example_hellojni_HelloJni
 * Method:    stringFromJNI
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_example_hellojni_HelloJni_stringFromJNI
  (JNIEnv *, jobject);

/*
 * Class:     com_example_hellojni_HelloJni
 * Method:    stringFromJNI2
 * Signature: (C)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_example_hellojni_HelloJni_stringFromJNI2
  (JNIEnv *, jobject, jchar);

/*
 * Class:     com_example_hellojni_HelloJni
 * Method:    stringFromJNI3
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_example_hellojni_HelloJni_stringFromJNI3
  (JNIEnv *, jclass);

/*
 * Class:     com_example_hellojni_HelloJni
 * Method:    unimplementedStringFromJNI
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_example_hellojni_HelloJni_unimplementedStringFromJNI
  (JNIEnv *, jobject);

#ifdef __cplusplus
}
#endif
#endif
