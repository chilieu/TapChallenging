package me.chilieu.app.tapchallenging;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

/**
 * Created by Thy on 9/28/2014.
 */
public class circle {

    private int id;
    private int status;
    private ImageView image;

    public circle(){}

    public circle(int id, int status){
        super();
        this.id = id;
        this.status = status;
        //this.image = image;
    }

    @Override
    public String toString(){
        return "Circle : " + this.id + " - Status: " + this.status;
    }

    public int getId(){return this.id;}
    public String getTitle(){return "" + (this.id+1);}
    public int getStatus(){return this.status;}

    //public ImageView getImage(){return this.image;}
    public void setImage(int status){
        if(status == 0){
            this.image.setBackgroundResource(R.drawable.blk_circle);
        } else {
            this.image.setBackgroundResource(R.drawable.grn_circle);
        }
    }
    //public void setImage(ImageView image){this.image = image;}
    public void setStatus(int status){this.status = status;}

}
