package com.doloop.www.myappmgr.material.unused;


import com.doloop.www.myappmgr.material.unused.TickPlusDrawable.PlusAnimDoneListener;
import com.doloop.www.myappmgr.material.unused.TickPlusDrawable.TickAnimDoneListener;
import com.doloop.www.myappmgrmaterial.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class TickPlusImageView extends ImageView implements TickAnimDoneListener, PlusAnimDoneListener {

    private Bitmap tickBitmap;
    private Bitmap plusBitmap;
    private TickPlusDrawable tickPlusDrawable;

    public TickPlusImageView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        init();
    }

    public TickPlusImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        init();
    }

    public TickPlusImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // TODO Auto-generated constructor stub
        init();
    }

    private void init() {
         tickPlusDrawable =
                new TickPlusDrawable(getResources().getDimensionPixelSize(R.dimen.stroke_width), Color.WHITE,
                        Color.BLACK, this, this);
        setImageDrawable(tickPlusDrawable);
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        // TODO Auto-generated method stub
        super.setImageDrawable(drawable);
        //plusBitmap = drawableToBitmap(getDrawable());
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        // TODO Auto-generated method stub
        super.setImageBitmap(bm);
    }

    @Override
    public void onPlusAnimStart() {
        //this.setEnabled(false);
        // TODO Auto-generated method stub
//        if (plusBitmap == null) {
//            plusBitmap = drawableToBitmap(getDrawable());
//        }
    }
    @Override
    public void onPlusAnimEnd() {
        // TODO Auto-generated method stub
        if (plusBitmap == null) {
            plusBitmap = drawableToBitmap(getDrawable());
        }
        //this.setEnabled(true);
    }
    @Override
    public void onTickAnimStart() {
        // TODO Auto-generated method stub
//        if (tickBitmap == null) {
//            tickBitmap = drawableToBitmap(getDrawable());
//        }
        //this.setEnabled(false);
    }
    @Override
    public void onTickAnimEnd() {
        // TODO Auto-generated method stub
        if (tickBitmap == null) {
            tickBitmap = drawableToBitmap(getDrawable());
        }
        //this.setEnabled(true);
    }
   

    public void showTick(){
        if(tickBitmap == null){
            tickPlusDrawable.tick();
        }
        else{
            this.setImageBitmap(tickBitmap);
            tickPlusDrawable.setTickMode(true);
        }
        
    }
    
    public void showPlus(){
        if(plusBitmap == null){
            tickPlusDrawable.plus();
        }
        else{
            this.setImageBitmap(plusBitmap);
            tickPlusDrawable.setTickMode(false);
        }
    }
    
    public void toggle(){
        this.setImageDrawable(tickPlusDrawable);
        tickPlusDrawable.toggle();
    }
    
    
    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        // We ask for the bounds if they have been set as they would be most
        // correct, then we check we are > 0
        final int width = !drawable.getBounds().isEmpty() ? drawable.getBounds().width() : drawable.getIntrinsicWidth();

        final int height =
                !drawable.getBounds().isEmpty() ? drawable.getBounds().height() : drawable.getIntrinsicHeight();

        // Now we check we are > 0
        final Bitmap bitmap =
                Bitmap.createBitmap(width <= 0 ? 1 : width, height <= 0 ? 1 : height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
    
}
