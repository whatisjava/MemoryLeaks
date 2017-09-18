package com.thxjava.memoryleaks;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

/*
 Avoiding memory leaks

 19 January 2009

 Android applications are, at least on the T-Mobile G1, limited to 16 MB of heap. It's both
 a lot of memory for a phone and yet very little for what some developers want to achieve. Even if
 you do not plan on using all of this memory, you should use as little as possible to let other
 applications run without getting them killed. The more applications Android can keep in memory,
 the faster it will be for the user to switch between his apps. As part of my job, I ran into
 memory leaks issues in Android applications and they are most of the time due to the same mistake:
 keeping a long-lived reference to a Context.

 On Android, a Context is used for many operations but mostly to load and access resources.
 This is why all the widgets receive a Context parameter in their constructor. In a regular
 Android application, you usually have two kinds of Context, Activity and Application. It's
 usually the first one that the developer passes to classes and methods that need a Context:

 @Override
 protected void onCreate(Bundle state) {
 super.onCreate(state);

 TextView label = new TextView(this);
 label.setText("Leaks are bad");

 setContentView(label);
 }
 This means that views have a reference to the entire activity and therefore to anything
 your activity is holding onto; usually the entire View hierarchy and all its resources. Therefore,
 if you leak the Context ("leak" meaning you keep a reference to it thus preventing the GC from
 collecting it), you leak a lot of memory. Leaking an entire activity can be really easy if you're
 not careful.

 When the screen orientation changes the system will, by default, destroy the current
 activity and create a new one while preserving its state. In doing so, Android will reload the
 application's UI from the resources. Now imagine you wrote an application with a large bitmap
 that you don't want to load on every rotation. The easiest way to keep it around and not having
 to reload it on every rotation is to keep in a static field:

 private static Drawable sBackground;

 @Override
 protected void onCreate(Bundle state) {
 super.onCreate(state);

 TextView label = new TextView(this);
 label.setText("Leaks are bad");

 if (sBackground == null) {
 sBackground = getDrawable(R.drawable.large_bitmap);
 }
 label.setBackgroundDrawable(sBackground);

 setContentView(label);
 }
 This code is very fast and also very wrong; it leaks the first activity created upon the
 first screen orientation change. When a Drawable is attached to a view, the view is set as a
 callback on the drawable. In the code snippet above, this means the drawable has a reference to
 the TextView which itself has a reference to the activity (the Context) which in turns has
 references to pretty much anything (depending on your code.)

 This example is one of the simplest cases of leaking the Context and you can see how we
 worked around it in the Home screen's source code (look for the unbindDrawables() method) by
 setting the stored drawables' callbacks to null when the activity is destroyed. Interestingly
 enough, there are cases where you can create a chain of leaked contexts, and they are bad. They
 make you run out of memory rather quickly.

 There are two easy ways to avoid context-related memory leaks. The most obvious one is to
 avoid escaping the context outside of its own scope. The example above showed the case of a
 static reference but inner classes and their implicit reference to the outer class can be equally
 dangerous. The second solution is to use the Application context. This context will live as long
 as your application is alive and does not depend on the activities life cycle. If you plan on
 keeping long-lived objects that need a context, remember the application object. You can obtain
 it easily by calling Context.getApplicationContext() or Activity.getApplication().

 In summary, to avoid context-related memory leaks, remember the following:

 Do not keep long-lived references to a context-activity (a reference to an activity should
 have the same life cycle as the activity itself)
 Try using the context-application instead of a context-activity
 Avoid non-static inner classes in an activity if you don't control their life cycle, use
 a static inner class and make a weak reference to the activity inside. The solution to this issue
 is to use a static inner class with a WeakReference to the outer class, as done in ViewRoot and
 its W inner class for instance
 A garbage collector is not an insurance against memory leaks
*/


public class ContextActivity extends AppCompatActivity {

    private static Drawable sBackground;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView label = new TextView(this);
        label.setText("Leaks are bad");

        if (sBackground == null) {
            sBackground = getDrawable(R.drawable.large_bitmap);
        }
        label.setBackgroundDrawable(sBackground);

        setContentView(label);
    }
}

