package me.chilieu.app.tapchallenging;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.format.Time;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherInterstitialAd;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;


public class MyActivity extends Activity  implements GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener, Animation.AnimationListener {

    private GestureDetector detector;
    private GridView gridView;
    private ArrayList<circle> cirlceGrid = new ArrayList<circle>();
    private View v1;
    private int countGreen = 0;
    private int countRed = 0;
    private int position = -1;
    //set animation for touch on grid cell
    private Animation animFadein;
    //private MediaPlayer mPlayer2;
    private Uri Pickup_Coin;
    private Uri Explosion;
    private Uri Powerup;
    private TextView total;
    private static final String AD_UNIT_ID = "ca-app-pub-0760902944328301/2536023620";
    /** The interstitial ad. */
    private InterstitialAd interstitial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        //set grid view item
        Bitmap black_circle = BitmapFactory.decodeResource(this.getResources(), R.drawable.blk_circle);
        Bitmap blue_circle = BitmapFactory.decodeResource(this.getResources(), R.drawable.grn_circle);
        Bitmap red_circle = BitmapFactory.decodeResource(this.getResources(), R.drawable.red_circle);

        detector = new GestureDetector(this, this);
        /*
        //setup sound
        mPlayer2 = new MediaPlayer();
        //mPlayer2.create(this, R.raw.explosion);
        try {
            mPlayer2.setDataSource(getAssets().openFd("raw/explosion.mp3").getFileDescriptor());
        } catch (IOException e) {
            e.printStackTrace();
        }
        mPlayer2.setVolume(0.5f,0.5f);*/

        total = (TextView) findViewById(R.id.total);


        cirlceGrid.add(new circle(0,0));
        cirlceGrid.add(new circle(1,0));
        cirlceGrid.add(new circle(2,0));
        cirlceGrid.add(new circle(3,0));
        cirlceGrid.add(new circle(4,0));
        cirlceGrid.add(new circle(5,0));
        cirlceGrid.add(new circle(6,0));
        cirlceGrid.add(new circle(7,0));
        cirlceGrid.add(new circle(8,0));
        cirlceGrid.add(new circle(9,0));

        gridView = (GridView) findViewById(R.id.matrixLines);
        CustomGridViewAdapter CustomGridViewAdapter = new CustomGridViewAdapter(this, R.layout.row_grid, cirlceGrid);
        gridView.setAdapter(CustomGridViewAdapter);

        //Start button
        final Button startBtn = (Button) findViewById(R.id.startBtn);
        //reset button
        final Button resetBtn = (Button) findViewById(R.id.resetBtn);

        //Start button
        startBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        return true;
                    case MotionEvent.ACTION_UP:
                        resetBtn.setVisibility(View.VISIBLE);
                        startBtn.setVisibility(View.INVISIBLE);
                        //set listener for gridview
                        gridView.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View view, MotionEvent motionEvent) {
                                int action = motionEvent.getActionMasked();  // MotionEvent types such as ACTION_UP, ACTION_DOWN
                                float currentXPosition = motionEvent.getX();
                                float currentYPosition = motionEvent.getY();
                                position = gridView.pointToPosition((int) currentXPosition, (int) currentYPosition);
                                Log.d("onTouchGridview", motionEvent.toString());
                                return detector.onTouchEvent(motionEvent);
                            }
                        });

                        //random circle
                        CountDownTimer countDown = new CountDownTimer(60000, 1000) {
                            @Override
                            public void onTick(long l) {
                                //Log.d("Timer", Long.toString(l));
                                TextView countDownTxt = (TextView) findViewById(R.id.countDownTextView);
                                Time time = new Time();
                                time.set(l);
                                countDownTxt.setText(time.minute + ":" + time.second);
                            }
                            @Override
                            public void onFinish() {
                                TextView countDownTxt = (TextView) findViewById(R.id.countDownTextView);
                                countDownTxt.setText("Game Over! \n Total");
                                gridView.setVisibility(View.INVISIBLE);

                                int totalPoint = countGreen + (countRed * 2);
                                total.setText(Integer.toString(totalPoint));
                                total.setVisibility(View.VISIBLE);
                            }
                        }.start();
                        randCircle();
                        return true;
                    default:
                        return false;
                }
            }
        });

        //resert button
        resetBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        showInterstitialAds();
                        return true;
                    case MotionEvent.ACTION_UP:
                        resetBtn.setVisibility(View.INVISIBLE);
                        startBtn.setVisibility(View.VISIBLE);
                        total.setVisibility(View.VISIBLE);
                        reload();
                        return true;
                    default:
                        return false;
                }
            }
        });

    }

    public void showInterstitialAds(){

        // Create the interstitial.
        interstitial = new InterstitialAd(this);
        interstitial.setAdUnitId(AD_UNIT_ID);

        // Create ad request.
        AdRequest adRequest = new AdRequest.Builder().build();
        // Begin loading your interstitial.
        interstitial.loadAd(adRequest);
        interstitial.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // TODO Auto-generated method stub
                super.onAdLoaded();
                if(interstitial.isLoaded()) {
                    interstitial.show();
                }
            }
        });

    }

    //START TOUCHING LISTENER
    @Override
    public boolean onTouchEvent(MotionEvent me){
        this.detector.onTouchEvent(me);
        return super.onTouchEvent(me);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        Log.d("---onDown----",e.toString());
        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
        Log.d("---onFling---",e1.toString()+e2.toString());
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        Log.d("---onLongPress---",e.toString());
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
        Log.d("---onScroll---",e1.toString()+e2.toString());
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        Log.d("---onShowPress---",e.toString());
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Log.d("---onSingleTapUp---",e.toString());
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        Log.d("---onDoubleTap---",e.toString());
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        //if tag green, countGreen ++
        v1 = (View) gridView.getChildAt(position);
        if (v1 != null) {//if v1 is exists
            ImageView img = (ImageView) v1.findViewById(R.id.image);
            Log.d("---onDoubleTapEvent---", "###### Background:" + v1.findViewById(R.id.image).toString());
            // change background color
            if(img.getTag().toString() == "red") {
                setCountRed();
                resetCircle(v1);
                randCircle();
            }
        }
        Log.d("---onDoubleTapEvent---",e.toString());
        return false;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {

        //if tag green, countGreen ++
        v1 = (View) gridView.getChildAt(position);
        if (v1 != null) {//if v1 is exists
            ImageView img = (ImageView) v1.findViewById(R.id.image);
            Log.d("---onSingleTapConfirmed---", "###### Background: " + img.getTag().toString());
            // change background color
            if(img.getTag().toString() == "green") {
                setCountGreen();
                resetCircle(v1);
                randCircle();
            }
        }
        Log.d("---onSingleTapConfirmed---",e.toString());
        return false;
    }
    //END TOUCHING LISTENER

    //START ANIMATION LISTENER
    @Override
    public void onAnimationEnd(Animation animation) {
        // Take any action after completing the animation
        // check for fade in animation
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onAnimationStart(Animation animation) {
        // TODO Auto-generated method stub

    }
    //END ANIMATION LISTENER

    public void setCountGreen(){
        countGreen++;
        TextView greenTxt = (TextView) findViewById(R.id.greenTxt);
        greenTxt.setText(Integer.toString(countGreen));
    }

    public void setCountRed(){
        countRed++;
        TextView greenTxt = (TextView) findViewById(R.id.redTxt);
        greenTxt.setText(Integer.toString(countRed));
    }

    public void reload() {
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();

        overridePendingTransition(0, 0);
        startActivity(intent);
    }

    public void resetCircle(View v1){
        ImageView img = (ImageView) v1.findViewById(R.id.image);
        img.setBackgroundResource(R.drawable.blk_circle);
        img.setTag("black");
    }

    //random green and red circle
    public void randCircle(){
        int minRed = 1;
        int maxRed = 5;
        Random redCircle = new Random();
        int randRed = redCircle.nextInt(maxRed - minRed + 1) + minRed;

        int min = 0;
        int max = 8;
        Random r = new Random();
        int randNum = r.nextInt(max - min + 1) + min;
        View vRand = (View) gridView.getChildAt(randNum);
        ImageView img = (ImageView) vRand.findViewById(R.id.image);
        if(randRed == 1){
            img.setBackgroundResource(R.drawable.red_circle);
            img.setTag("red");
        } else {
            img.setBackgroundResource(R.drawable.grn_circle);
            img.setTag("green");
        }
        playAnim(img);
        //playSound();
    }

    public void playAnim(ImageView img){
        // load the animation
        animFadein = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.bounce);
        // set animation listener
        animFadein.setAnimationListener(this);
        if (animFadein == null) {
            animFadein.cancel();
        }
        img.startAnimation(animFadein);

    }
/*
    private void playSound() {
        try {
            if(mPlayer2.isPlaying()) {
                mPlayer2.stop();
            }
            mPlayer2.start();
        } catch (Exception e) {
            // don't care
        }
    }
*/
}
