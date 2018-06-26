#include <MyNative.h>


/**
* �������� ������ � native.
*/
JNIEXPORT void Java_ru_suvitruf_androidndk_tutorial1_AndroidNDK_SetString(JNIEnv * env, jobject obj, jstring str){

	jboolean isCopy;
	const char * Str;

	Str = env->GetStringUTFChars(str, &isCopy);
	strcpy(MyStr,Str);
	LOGI("string = \"%s\"",MyStr);

}

void ChangeStr(){

	strcat(MyStr," and bb.");
}

/**
 * ��������� ������.
 */
JNIEXPORT void Java_ru_suvitruf_androidndk_tutorial1_AndroidNDK_ChangeString(JNIEnv * env, jobject obj){
	ChangeStr();
	LOGI("string after change = \"%s\"",MyStr);
}

/**
 * ��������� ������ �� native � Java.
 */
JNIEXPORT jstring Java_ru_suvitruf_androidndk_tutorial1_AndroidNDK_GetString(JNIEnv * env, jobject obj){

	LOGI("returned string = \"%s\"",MyStr);
	return env->NewStringUTF(MyStr);
}
