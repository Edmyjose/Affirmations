package com.ejs.affirmations;

import static com.ejs.affirmations.utils.Utils.UncaughtException;
import static com.ejs.affirmations.utils.Utils.changeGif;
import static com.ejs.affirmations.utils.Utils.checkCat;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Movie;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.service.wallpaper.WallpaperService;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ejs.affirmations.utils.Utils;

import java.io.IOException;
import java.util.Calendar;

public class MyWallpaperService extends WallpaperService {

    @Override
    public Engine onCreateEngine() {

        try {
            return new MyWallpaperEngine();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public class MyWallpaperEngine extends Engine {
        private final String TAG = this.getClass().getSimpleName();
        private static final long fps = 30L;
        private final Rect rect = new Rect();
        private final Handler handler = new Handler();
        private final SharedPreferences sharedPref;
        private final SharedPreferences sharedOnce;
        private final SharedPreferences sp;
        private Movie movie;
        private String tables = "";
        private String affirm = "";
        private int width;
        private int height;
        private int Duration;
        float mScaleX;
        float mScaleY;
        int mWhen;
        long mStart;
        private final int frameDuration = 20;
        private boolean visible = true;
        private final Runnable drawRunner = new Runnable() {
            @Override
            public void run() {
                draw();
            }
        };



        public MyWallpaperEngine() throws IOException {
            UncaughtException(getApplicationContext());
            mWhen = -1;
            sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            sharedOnce = getSharedPreferences("runonce", MODE_PRIVATE);
            sp = getSharedPreferences("trans_" + Utils.getLocale(), MODE_PRIVATE);

            changeGif(sharedPref);

            Log.e(TAG,"nameGif " + sharedPref.getString("nameGif","image_14.gif"));
            movie  = Movie.decodeStream(getResources().getAssets().open(sharedPref.getString("nameGif","image_14.gif")));

            Log.e(TAG, "Locale "+ Utils.getLocale());
            tables = checkCat(sharedPref);
            affirm = runOnceADay(tables,affirm, sharedOnce);
            handler.post(drawRunner);
        }
        public String runOnceADay(String Table, String affirmations, SharedPreferences shared) throws IOException {
            Calendar c = Calendar.getInstance();
            int day = c.get(Calendar.DAY_OF_WEEK); // Or other Calendar value

            int lastCheckedDay = shared.getInt("DAY_START", -1); // "KEY" you may change yhe value
            String affirmation = affirmations;
            if (lastCheckedDay != day){
                SharedPreferences sp = getSharedPreferences("trans_" + Utils.getLocale(), MODE_PRIVATE);
                Utils.changeQuotte(getApplicationContext(),shared,sp, tables,day);
                changeGif(sharedPref);
            }
            return affirmation;
        }
        @Override
        public void onVisibilityChanged(boolean visible) {
            this.visible = visible;
            if (visible) {
                try {
                    affirm = sp.getString("DAY_AFFIRMATION", sharedOnce.getString("DAY_AFFIRMATION", ""));
                    tables = "";
                    Log.e(TAG,"nameGif " + sharedPref.getString("nameGif","image_14.gif"));
                    movie  = Movie.decodeStream(getResources().getAssets().open(sharedPref.getString("nameGif","image_14.gif")));
                    Duration = movie.duration();
                    mScaleX = width / (1f * movie.width());
                    mScaleY = height / (1f * movie.height());
                    tables = checkCat(sharedPref);
                    affirm = runOnceADay(tables,affirm, sharedOnce);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                handler.post(drawRunner);
            } else {
                handler.removeCallbacks(drawRunner);
            }
        }
        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            this.visible = false;
            handler.removeCallbacks(drawRunner);
        }
        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            this.width = width;
            this.height = height;
            super.onSurfaceChanged(holder, format, width, height);
            mScaleX = width / (1f * movie.width());
            mScaleY = height / (1f * movie.height());
        }

        private void draw() {
            tick();
            SurfaceHolder holder = getSurfaceHolder();
            Canvas canvas = null;
            Bitmap bitmap = null;
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            canvas = new Canvas(bitmap);
            float fontSize;
            double heightText = 4;
            affirm = sp.getString("DAY_AFFIRMATION", sharedOnce.getString("DAY_AFFIRMATION", ""));
            try {
                canvas = holder.lockCanvas();
                if (canvas != null) {
                    canvas.save();
                    canvas.scale(mScaleX,mScaleY);
                    movie.draw(canvas,0,0);
                    canvas.restore();
                    //Canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
                    heightText = heightText + 13;
                    fontSize = getResources().getDimensionPixelSize(R.dimen.myFontSize20);
                    //drawMultilineTextToBitmap(getApplicationContext(),29, affirm);
                    drawTextFill(canvas, affirm, fontSize);
                    drawTextStroke(canvas, affirm, fontSize);
                    //drawText(canvas, runOnceADay(tables,affirm, sharedOnce),width/2, setPosHeight(height, heightText),fontSize);
                }
            } finally {
                if (canvas != null){
                    holder.unlockCanvasAndPost(canvas);
                    movie.setTime((int) (System.currentTimeMillis() % movie.duration() ));
                }
            }
            handler.removeCallbacks(drawRunner);
            if (visible) {
                handler.postDelayed(drawRunner, 1000L/fps);
                //handler.postDelayed(drawRunner, 100);
            }
        }

        void tick() {
            if (mWhen == -1L) {
                mWhen = 0;
                mStart = SystemClock.uptimeMillis();
            } else {
                long mDiff = SystemClock.uptimeMillis() - mStart;
                mWhen = (int) (mDiff % Duration);
            }
        }
        public int setPosHeight(int height, double porcentage){
            return  (int) (( height * porcentage) / 100);
        }


        public void drawTextStroke(Canvas canvas, String text, float fontSize) {

            //canvas.drawColor(Color.TRANSPARENT);
            canvas.drawColor(Color.TRANSPARENT);
            rect.set(160,(height/2)-500,width-160,(height/2)+100);
            //Set your own color, size etc.
            TextPaint textPaint = new TextPaint(Paint.LINEAR_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
            textPaint.setStyle(Paint.Style.STROKE);
            textPaint.setStrokeJoin(Paint.Join.ROUND);
            textPaint.setStrokeCap(Paint.Cap.ROUND);
            textPaint.setStrokeWidth(3);
            textPaint.setStrokeMiter(10.0f);
            textPaint.setColor(Color.GRAY);
            textPaint.setAntiAlias(true);
            textPaint.setDither(true);
            Typeface tf =Typeface.createFromAsset(getAssets(),"fonts/palanquin_dark_bold.ttf");
            textPaint.setTypeface(tf);
            textPaint.setMaskFilter(new BlurMaskFilter(20 , BlurMaskFilter.Blur.SOLID));
            textPaint.setTextSize(fontSize);

            //DynamicLayout sl = new DynamicLayout(text, textPaint, rect.width(),
            StaticLayout sl = new StaticLayout(text, textPaint, rect.width(),
                    Layout.Alignment.ALIGN_CENTER, 1, 1, true);
            canvas.save();

            //calculate X and Y coordinates - In this case we want to draw the text in the
            //center of canvas so we calculate
            //text height and number of lines to move Y coordinate to center.
            float textHeight = getTextHeight(text, textPaint).height();
            int numberOfTextLines = sl.getLineCount();
            float textYCoordinate = rect.exactCenterY() - ((numberOfTextLines * textHeight) / 2);

            //text will be drawn from left
            float textXCoordinate = rect.left;

            canvas.translate(textXCoordinate, textYCoordinate);

            //draws static layout on canvas
            sl.draw(canvas);
            canvas.restore();

        }
        public void drawTextFill(Canvas canvas, String text, float fontSize) {

            //canvas.drawColor(Color.TRANSPARENT);
            canvas.drawColor(Color.TRANSPARENT);
            rect.set(160,(height/2)-500,width-160,(height/2)+100);


            //Set your own color, size etc.
            TextPaint textPaint = new TextPaint(Paint.LINEAR_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
            textPaint.setStyle(Paint.Style.FILL);
            textPaint.setColor(Color.WHITE);
            textPaint.setAntiAlias(true);
            textPaint.setDither(true);
            Typeface tf =Typeface.createFromAsset(getAssets(),"fonts/palanquin_dark_bold.ttf");
            textPaint.setTypeface(tf);
            //textPaint.setMaskFilter(new BlurMaskFilter(20 , BlurMaskFilter.Blur.INNER));
            textPaint.setTextSize(fontSize);
            textPaint.setShadowLayer(20, 0, 0, Color.GRAY);
            /*textPaint.setMaskFilter(new BlurMaskFilter(20, BlurMaskFilter.Blur.OUTER));

            textPaint.setMaskFilter(new BlurMaskFilter(20, BlurMaskFilter.Blur.NORMAL));
            textPaint.setMaskFilter(null);*/
            //Static layout which will be drawn on canvas
            //textOnCanvas - text which will be drawn
            //text paint - paint object
            //bounds.width - width of the layout
            //Layout.Alignment.ALIGN_CENTER - layout alignment
            //1 - text spacing multiply
            //1 - text spacing add
            //true - include padding
            //DynamicLayout sl = new DynamicLayout(text, textPaint, rect.width(),
            StaticLayout sl = new StaticLayout(text, textPaint, rect.width(),
                    Layout.Alignment.ALIGN_CENTER, 1, 1, true);

            canvas.save();

            //calculate X and Y coordinates - In this case we want to draw the text in the
            //center of canvas so we calculate
            //text height and number of lines to move Y coordinate to center.
            float textHeight = getTextHeight(text, textPaint).height();
            int numberOfTextLines = sl.getLineCount();
            float textYCoordinate = rect.exactCenterY() - ((numberOfTextLines * textHeight) / 2);

            //text will be drawn from left
            float textXCoordinate = rect.left;

            canvas.translate(textXCoordinate, textYCoordinate);

            // Draw shadow before drawing object
            //draws static layout on canvas
            Paint myPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            myPaint.setColor(Color.BLACK);
            myPaint.setStyle(Paint.Style.STROKE);
            myPaint.setStrokeWidth(3);
            myPaint.setMaskFilter(new BlurMaskFilter(90 , BlurMaskFilter.Blur.OUTER));
            myPaint.setShadowLayer(10.0f, 0.0f, 0.0f, Color.GRAY);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                canvas.drawRoundRect(0, 0, sl.getWidth(), sl.getHeight(), 40.0f, 40.0f, myPaint);
            }
            myPaint.setColor(Color.GRAY);
            myPaint.setStyle(Paint.Style.FILL);
            myPaint.setShadowLayer(0.0f, 0.0f, 0.0f, 0xFF000000);
            myPaint.setMaskFilter(null);
            myPaint.setAlpha(140);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                canvas.drawRoundRect(0, 0, sl.getWidth(), sl.getHeight(), 40.0f, 40.0f, myPaint);
            }
            sl.draw(canvas);
            canvas.restore();

        }
        /**
         * @return text height
         */
        private Rect getTextHeight(String text, Paint paint) {

            Rect rect = new Rect();
            paint.getTextBounds(text, 0, text.length(), rect);
            return rect;
        }
        public Bitmap drawMultilineTextToBitmap(Context gContext,int gResId,String gText) {

            // prepare canvas
            Resources resources = gContext.getResources();
            float scale = resources.getDisplayMetrics().density;
            Bitmap bitmap = BitmapFactory.decodeResource(resources, gResId);

            Bitmap.Config bitmapConfig = bitmap.getConfig();
            // set default bitmap config if none
            if(bitmapConfig == null) {
                bitmapConfig = Bitmap.Config.ARGB_8888;
            }
            // resource bitmaps are imutable,
            // so we need to convert it to mutable one
            bitmap = bitmap.copy(bitmap.getConfig(), true);

            Canvas canvas = new Canvas(bitmap);

            // new antialiased Paint
            TextPaint paint=new TextPaint(Paint.ANTI_ALIAS_FLAG);
            // text color - #3D3D3D
            paint.setColor(Color.rgb(61, 61, 61));
            // text size in pixels
            paint.setTextSize((int) (14 * scale));
            // text shadow
            paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);

            // set text width to canvas width minus 16dp padding
            int textWidth = canvas.getWidth() - (int) (16 * scale);

            // init StaticLayout for text
            StaticLayout textLayout = new StaticLayout(
                    gText, paint, textWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);

            // get height of multiline text
            int textHeight = textLayout.getHeight();

            // get position of text's top left corner
            float x = (bitmap.getWidth() - textWidth)/2;
            float y = (bitmap.getHeight() - textHeight)/2;

            // draw text to the Canvas center
            canvas.save();
            canvas.translate(x, y);
            textLayout.draw(canvas);
            canvas.restore();

            return bitmap;
        }
        public void drawText(Canvas canvas, String text, int x, int y, float fontSize) {

            canvas.drawColor(Color.TRANSPARENT);

            Paint paint1 = new Paint();
            paint1.setStyle(Paint.Style.STROKE);
            paint1.setAntiAlias(true);
            paint1.setColor(Color.BLACK);
            paint1.setTextSize(fontSize);


            TextView tv = new TextView(getApplicationContext());
            tv.setTextColor(Color.WHITE);

            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            llp.setMargins(20, 2, 20, 0); // llp.setMargins(left, top, right, bottom);
            tv.setLayoutParams(llp);
            tv.setTextSize(20);

            tv.setText(text);
            tv.setDrawingCacheEnabled(true);
            tv.measure(View.MeasureSpec.makeMeasureSpec(canvas.getWidth()-100, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(canvas.getHeight(), View.MeasureSpec.EXACTLY));
            tv.layout(0, 0, tv.getMeasuredWidth(), tv.getMeasuredHeight());
            canvas.drawBitmap(tv.getDrawingCache(), x, y, paint1);
            tv.setDrawingCacheEnabled(false);
            /*

            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setTypeface(Typeface.SERIF);
            //paint.setTextSize(22 * getResources().getDisplayMetrics().density);
            paint.setTextSize(fontSize);
            paint.setShadowLayer(20, 0, 0, Color.GRAY);
            paint.setMaskFilter(new BlurMaskFilter(20  *//*shadowRadius*//* , BlurMaskFilter.Blur.NORMAL));
            int xPos = x - (int)(paint.measureText(text)/2);
            canvas.drawText(text, (xPos)+10, y+10, paint);
            paint.setColor( Color.WHITE);
            paint.setMaskFilter(null);
            Rect result = new Rect();
            paint.getTextBounds(text, 0, text.length(), result);
            canvas.drawText(text, (xPos), y, paint);*/
            //canvas.drawBitmap(makeText(text), x, y, paint);

        }
    }

}
