# Introduction to Coroutines

## Setting up your project to use Coroutines
To start working with Coroutines you have to set up your Kotlin version to '1.3.0-rc-146' and the following line to the repositories:

    maven { url 'https://kotlin.bintray.com/kotlin-eap' }


Also you have to include to following lines in your gradle file:

    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.0.0-RC1'
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.0.0-RC1'
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-rx2:1.0.0-RC1'

## Usage

You can start a Coroutine by calling an async task inside a launch block. First let's try out using the `GlobalScope.launch {}`.
```kotlin
GlobalScope.launch {
    val myTask = async { runLongTask() }
    showText(myTask.await())
}
```
Inside the `launch{}` you start an async task, then in the next line with `await()` you are waiting for the result and when the result is there it will show some text.

If you wan to extract the inside of the `launch{}` to a function you have to use suspended functions otherwise you cannot call `await()` inside the function.

```kotlin
...
GlobalScope.launch {
    runMyTask()
}
...

suspend fun runMyTask(){
    val myTask = async { runLongTask() }
    showText(myTask.await())
}
```

So running this code will work, it will run in the background, not blocking the UI thread since it is run on the `GlobalScope` but because of this if your fragment gets destroyed while the task is still running the task will continue to run. So when it calls the `showText()` your app will throw a runtime exception because the `TextView` is no longer there where you wanted to display the result.

## Using CoroutineScope
The solution to the problem is using CoroutineScope, looking at the documentation of the interface this is how you should implement it:

```kotlin
lateinit var job: Job

override val coroutineContext: CoroutineContext
    get() = Dispatchers.Main + job

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    job = Job()
  }

override fun onDestroy() {
    super.onDestroy()
    job.cancel() // Cancel job on activity destroy. After destroy all children jobs will be cancelled automatically
}
```


So after implementing the CoroutineScope interface, instead of using the `GLobalScope.launch{}` we can simply use `launch{}`. Tying it out we can see that is running on the main thread because of the `Dispatchers.Main` in the `CoroutineContext`.
There are a few fixes for that. Changing it on the CoroutineContext to Dispatchers.IO but this way all of your tasks would run on the background.
Or you could specify it in the launch like this: `launch (Dispatchers.IO){}` or in thy async task: `async (Dispatchers.IO){}`
You can choose which fits your case better, but I would recommend to use the last two options. So now our code looks like this:

```kotlin
...
launch(Dispatchers.IO) {
    val myTask = async { runLongTask() }
    showText(myTask.await())
}
..

private fun runLongTask(): String {
     Thread.sleep(3000)
     Log.d(TAG, System.currentTimeMillis().toString())
     return "successs"
}

private fun showText(text: String) {
    Log.d(TAG, text)
    textView.text = text
}
```

Now our code runs nicely, without error.

### withContext()
An other way to start a task is with the `withContext()` function. The previous example would look like this using `withContext()`:
```kotlin
val myOtherTask=withContext(Dispatchers.IO) {
    runLongTask()
}
showText(myOtherTask)
```

So what is the difference? Let's do some experimenting.

```kotlin
....
launch {
    withContext(Dispatchers.IO) {
        getTimeWithContext()
    }
    withContext(Dispatchers.IO) {
        getTimeWithContext()
    }
    withContext(Dispatchers.IO) {
        getTimeWithContext()
    }
}

launch(Dispatchers.IO) {
    async { getTimeWithAsync() }.await()
    async { getTimeWithAsync() }.await()
    async { getTimeWithAsync() }.await()
}
...


private fun getTimeWithContext() {
    Thread.sleep(3000)
    Log.d(TAG+" withContext", System.currentTimeMillis().toString())
}


private fun getTimeWithAsync() {
    Thread.sleep(3000)
    Log.d(TAG+" async", System.currentTimeMillis().toString())
}
```

This way the output is the following:
```
CoroutinesFragment async: 1540455705771
CoroutinesFragment withContext: 1540455705842
CoroutinesFragment async: 1540455708777
CoroutinesFragment withContext: 1540455708847
CoroutinesFragment async: 1540455711781
CoroutinesFragment withContext: 1540455711849
```
So we can se that the too launches runs at the same time but inside the launch the tasks always wait for the previous task to finish inside the same launch.
Let's change the launch running the async tasks a bit:

```kotlin
launch(Dispatchers.IO) {
    val i1 = async { getTimeWithAsync() }
    val i2 = async { getTimeWithAsync() }
    val i3 = async { getTimeWithAsync() }

    i1.await()
    i2.await()
    i3.await()
}
```

Now the output is the following:
```
CoroutinesFragment async: 1540456242611
CoroutinesFragment async: 1540456242622
CoroutinesFragment async: 1540456242629
CoroutinesFragment withContext: 1540456242665
CoroutinesFragment withContext: 1540456245669
CoroutinesFragment withContext: 1540456248853
```
So what happened? Now the async tasks run at the same time, and didn't wait for the previous one to finish. So this is the difference between `async{}` and `withContext{}`

### RxJava, Retrofit
Using coroutines with RxJava is pretty similar to what we just did. Here is how you can use Retrofit RxJava and Coroutines together.

Your API call should look like this:
```kotlin
@GET("/api/books/1")
fun getBookData(): Single<Book>```

And you can call it simply like this:

```kotlin
launch{
    val book = withContext(Dispatchers.IO) {
        service.getBookData().await()
    }
}
```

And now you can use the data how you like. Also Retrofit will introduce suspended function in Retrofit 3.0 so after there is no need to include RxJava.


### Sources
https://chris.banes.me/talks/2018/android-suspenders/

https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-coroutine-scope/

https://stackoverflow.com/questions/50230466/kotlin-withcontext-vs-async-await
