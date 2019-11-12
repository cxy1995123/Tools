@file:Suppress("LABEL_NAME_CLASH")

package chen.com.kotlin_test

import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

//<editor-fold desc="launch">
/**
 *  launch - 创建协程
 *  async - 创建带返回值的协程，返回的是 Deferred 类
 *  withContext - 不创建新的协程，在指定协程上运行代码块
 *  runBlocking - 不是 GlobalScope 的 API，可以独立使用，区别是 runBlocking 里面的 delay 会阻塞线程，
 *  而 launch 创建的不会
 * */

/**
 *  CoroutineContext - 可以理解为协程的上下文，
 *  在这里我们可以设置 CoroutineDispatcher
 *  协程运行的线程调度器，有 4种线程模式：
 *  ---------------------------------------------------------
 *  Dispatchers.Default
 *  Dispatchers.IO -
 *  Dispatchers.Main - 主线程
 *  Dispatchers.Unconfined - 没指定，就是在当前线程
 *  -------------------------------------------------------
 *  newSingleThreadContext    单线程
 *  newFixedThreadPoolContext 线程池
 * */

/**
 *  CoroutineStart - 启动模式，默认是DEAFAULT，也就是创建就启动；
 *  ---------------------------------------------------------
 *  DEAFAULT - 模式模式，不写就是默认
 *  ATOMIC -
 *  UNDISPATCHED
 *  LAZY - 懒加载模式，你需要它的时候，再调用启动， (需要手动跳动 job.start())
 * */


fun launch(block: suspend CoroutineScope.() -> Unit): Job {
    val job = Job()
    GlobalScope.launch(job + Dispatchers.Default, CoroutineStart.DEFAULT, block)
    return job
}

fun launch(context: CoroutineContext = EmptyCoroutineContext,
           start: CoroutineStart = CoroutineStart.DEFAULT,
           block: suspend CoroutineScope.() -> Unit): Job {
    return GlobalScope.launch(context, start, block)
}

suspend fun <T> widthMainThread(block: suspend CoroutineScope.() -> T): T {
    return withContext(Dispatchers.Main, block)
}

suspend fun <T> widthBackGroundThread(block: suspend CoroutineScope.() -> T): T {
    return withContext(Dispatchers.Default, block)
}

suspend fun <T> widthIoThread(block: suspend CoroutineScope.() -> T): T {
    return withContext(Dispatchers.IO, block)
}

fun <T> async(context: CoroutineContext = EmptyCoroutineContext,
              start: CoroutineStart = CoroutineStart.DEFAULT,
              block: suspend CoroutineScope.() -> T): Deferred<T> {
    return GlobalScope.async(context, start, block)
}

//</editor-fold>


//<editor-fold desc="LifecycleOwner 异步协程 感知生命周期">

fun <T> LifecycleOwner.async(loader: () -> T): Deferred<T> {
    val deferred = async(context = Dispatchers.Default, start = CoroutineStart.LAZY) {
        return@async loader()
    }
    lifecycle.addObserver(CoroutineLifecycleListener(deferred))
    return deferred
}

infix fun <T> Deferred<T>.then(block: (T) -> Unit): Job {
    
    return launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
        if (this@then.isActive) {
            block(this@then.await())
        }
    }
}


class CoroutineLifecycleListener(private val deferred: Deferred<*>) : LifecycleObserver {
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun cancelCoroutine() {
        if (!deferred.isCancelled) {
            deferred.cancel()
        }
    }
}


//</editor-fold>

//<editor-fold desc="扩展">

@Suppress("UNCHECKED_CAST")
class Coroutinehelper(var parentJob: Job) {
    var deferred: Deferred<Any>? = null
    fun <T> async(loader: () -> T): Coroutinehelper {
        deferred = async(context = Dispatchers.Default, start = CoroutineStart.LAZY) {
        
        }
        return this@Coroutinehelper
    }
    
    suspend fun <T> mainThread(block: (T) -> Unit) {
        withContext(Dispatchers.Main) {
            if (parentJob.isActive) {
                val await = deferred!!.await()
                block(await as T)
            }
        }
    }
}


//</editor-fold>




