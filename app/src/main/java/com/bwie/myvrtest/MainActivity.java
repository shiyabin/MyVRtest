package com.bwie.myvrtest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.vr.sdk.widgets.common.VrWidgetView;
import com.google.vr.sdk.widgets.pano.VrPanoramaEventListener;
import com.google.vr.sdk.widgets.pano.VrPanoramaView;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private VrPanoramaView vr_view;
    private MyTask mytask;

    /*0.0 在项目里新建一个资产目录assets,把图片放入资产目录下,
                1.0 在清单文件下Application节点中加入android:largeHeap="true"的属下节点.
                2.0 导入VR需要依赖的library库,以导model的方式去导入:Common,Commonwidge,Panowidget
                3.0 在Module的build.gradle文件里dependencies,添加:compile 'com.google.protobuf.nano:protobuf-javanano:3.0.0-alpha-7'
                4.0 完成项目XML布局,VrPanoramaView
                5.0 由于VR资源数据量大,获取需要时间,故把加载图片放到子线程中进行,主线程来显示图片,可以使用一个异步线程AsyncTask或EventBus技术完成
                6.0 因为VR很占用内存,所以当界面进入onPause状态,暂停VR视图显示,进入onResume状态,继续VR视图显示,进入onDestroy状态,杀死VR,关闭异步任务
                7.0 设置对VR运行状态的监听,如果VR运行出现错误,可以及时的处理.
                8.0 播放VR效果,只需执行异步任务即可.

             */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        vr_view = (VrPanoramaView) findViewById(R.id.vr_view);
        vr_view.setInfoButtonEnabled(false);
        vr_view.setFullscreenButtonEnabled(false);
        vr_view.setDisplayMode(VrPanoramaView.DisplayMode.FULLSCREEN_STEREO);
        vr_view.setEventListener(new MyListener());
        mytask = new MyTask();
        mytask.execute();

    }
    //自定义一个类继承AsyncTask,只使用我们需要的方法.
    // 由于VR资源数据量大,获取需要时间,故把加载图片放到子线程中进行,主线程来显示图片,故可以使用一个异步线程AsyncTask或EventBus来处理.
    class MyTask extends AsyncTask<View,View,Bitmap>{

        @Override
        protected Bitmap doInBackground(View... params) {
            try {
                InputStream open = getAssets().open("mn.jpg");
                Bitmap bitmap = BitmapFactory.decodeStream(open);
                return  bitmap;
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
        //该方法在主线程运行
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            VrPanoramaView.Options options=new VrPanoramaView.Options();
            options.inputType = VrPanoramaView.Options.TYPE_MONO;
            vr_view.loadImageFromBitmap(bitmap,options);

            super.onPostExecute(bitmap);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        vr_view.pauseRendering();
    }

    @Override
    protected void onResume() {
        super.onResume();
        vr_view.resumeRendering();
    }

    @Override
    protected void onDestroy() {

        vr_view.shutdown();
        if (mytask!=null){
           if (!mytask.isCancelled()){
               mytask.cancel(true);
           }
        }
        super.onDestroy();
    }

    class  MyListener extends VrPanoramaEventListener{
        @Override
        public void onLoadSuccess() {
            super.onLoadSuccess();
              Toast.makeText(MainActivity.this, " 加载成功", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onLoadError(String errorMessage) {
            super.onLoadError(errorMessage);
            Toast.makeText(MainActivity.this, " 加载失败", Toast.LENGTH_SHORT).show();
        }
    }
}
