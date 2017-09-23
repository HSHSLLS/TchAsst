package com.xc.www.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Administrator on 2016/11/9.
 */
public class UserIcon extends ImageView {

    private Bitmap mBitmap;
    private Paint mPaint;
    private BitmapShader mBitmapShader;

    private int src_resource;

    public UserIcon(Context context) {
        this(context, null);
    }

    public UserIcon(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UserIcon(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        src_resource = attrs.getAttributeResourceValue("http://schemas.android.com/apk/res/android", "src", 0);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        mBitmap = BitmapFactory.decodeResource(getResources(), src_resource);
        mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        mPaint = new Paint();
        mPaint.setShader(mBitmapShader);
        canvas.drawCircle(500, 250, 200, mPaint);
        super.onDraw(canvas);
    }

}
