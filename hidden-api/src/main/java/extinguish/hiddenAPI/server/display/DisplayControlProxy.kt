package extinguish.hiddenAPI.server.display

import android.annotation.SuppressLint
import android.os.IBinder
import java.lang.reflect.Method

@SuppressLint("BlockedPrivateApi", "PrivateApi")
class DisplayControlProxy {

    companion object {
        private val CLASS: Class<*> by lazy {
            val classLoaderFactoryClass =
                Class.forName("com.android.internal.os.ClassLoaderFactory")
            val createClassLoaderMethod = classLoaderFactoryClass.getDeclaredMethod(
                "createClassLoader",
                String::class.java,
                String::class.java,
                String::class.java,
                ClassLoader::class.java,
                Int::class.javaPrimitiveType,
                Boolean::class.javaPrimitiveType,
                String::class.java
            )
            val classLoader = createClassLoaderMethod.invoke(
                null, "/system/framework/services.jar", null, null,
                ClassLoader.getSystemClassLoader(), 0, true, null
            ) as ClassLoader

            classLoader.loadClass("com.android.server.display.DisplayControl").also {
                val loadMethod = Runtime::class.java.getDeclaredMethod(
                    "loadLibrary0",
                    Class::class.java,
                    String::class.java
                )
                loadMethod.isAccessible = true
                loadMethod.invoke(Runtime.getRuntime(), it, "android_servers")
            }
        }

        private val METHOD_getPhysicalDisplayIdsMethod: Method by lazy {
            CLASS.getMethod("getPhysicalDisplayIds")
        }

        private val METHOD_getPhysicalDisplayTokenMethod: Method by lazy {
            CLASS.getMethod("getPhysicalDisplayToken", Long::class.javaPrimitiveType)
        }

        fun getPhysicalDisplayIds(): LongArray {
            return METHOD_getPhysicalDisplayIdsMethod.invoke(null) as LongArray
        }

        fun getPhysicalDisplayToken(physicalDisplayId: Long): IBinder {
            return METHOD_getPhysicalDisplayTokenMethod.invoke(null, physicalDisplayId) as IBinder
        }
    }
}