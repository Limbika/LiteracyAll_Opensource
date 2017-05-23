package com.literacyall.app.customui;


import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.literacyall.app.R;

public class TopDrawerCustomView extends RelativeLayout {

    Context context;
    ImageView ivAvatar;
    TextView txt;
    ImageView ivTop;

    int id = 0;

    int text_id = 0;
    int color = 0;

    public TopDrawerCustomView(Context context) {
        super(context);
        this.context = context;
        init(context);

    }

    public TopDrawerCustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(context);
    }

    public TopDrawerCustomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init(context);
    }

    public void setIds(int id) {
        this.id = id;
    }

    public int getIds() {
        return id;
    }


    public void setTextId(int id) {
        this.text_id = id;
    }

    public int getTextIds() {
        return text_id;
    }

    public int giveMeColorPic(int position) {
        int drawable = 0;
        switch (position) {
            case 0:
                drawable = R.drawable.ic_grey_1;
                break;
            case 1:
                drawable = R.drawable.ic_grey_2;
                break;
            case 2:
                drawable = R.drawable.ic_grey_3;
                break;
            case 3:
                drawable = R.drawable.ic_grey_4;
                break;
            case 4:
                drawable = R.drawable.ic_grey_5;
                break;

            case 5:
                drawable = R.drawable.ic_black_1;
                break;
            case 6:
                drawable = R.drawable.ic_black_2;
                break;
            case 7:
                drawable = R.drawable.ic_black_3;

                break;
            case 8:
                drawable = R.drawable.ic_black_4;

                break;
            case 9:
                drawable = R.drawable.ic_black_5;
                break;

            case 10:
                drawable = R.drawable.ic_yellow_1;
                break;
            case 11:
                drawable = R.drawable.ic_yellow_2;
                break;

            case 12:
                drawable = R.drawable.ic_yellow_3;

                break;
            case 13:
                drawable = R.drawable.ic_yellow_4;
                break;
            case 14:
                drawable = R.drawable.ic_yellow_5;
                break;

            case 15:
                drawable = R.drawable.ic_orange_1;

                break;
            case 16:
                drawable = R.drawable.ic_orange_2;

                break;
            case 17:
                drawable = R.drawable.ic_orange_3;

                break;
            case 18:
                drawable = R.drawable.ic_orange_4;

                break;
            case 19:
                drawable = R.drawable.ic_orange_5;
                break;
            case 20:
                drawable = R.drawable.ic_turquesa_1;
                break;
            case 21:
                drawable = R.drawable.ic_turquesa_2;
                break;
            case 22:
                drawable = R.drawable.ic_turquesa_3;
                break;

            case 23:
                drawable = R.drawable.ic_turquesa_4;
                break;
            case 24:
                drawable = R.drawable.ic_turquesa_5;
                break;
            case 25:
                drawable = R.drawable.ic_green_1;
                break;
            case 26:
                drawable = R.drawable.ic_green_2;
                break;
            case 27:
                drawable = R.drawable.ic_green_3;

                break;
            case 28:
                drawable = R.drawable.ic_green_4;
                break;
            case 29:
                drawable = R.drawable.ic_green_5;
                break;

            case 30:
                drawable = R.drawable.ic_marine_1;
                break;
            case 31:
                drawable = R.drawable.ic_marine_2;

                break;
            case 32:
                drawable = R.drawable.ic_marine_3;

                break;
            case 33:
                drawable = R.drawable.ic_marine_4;

                break;
            case 34:
                drawable = R.drawable.ic_marine_5;

                break;

            case 35:
                drawable = R.drawable.ic_blue_1;

                break;
            case 36:
                drawable = R.drawable.ic_blue_2;

                break;
            case 37:
                drawable = R.drawable.ic_blue_3;

                break;
            case 38:
                drawable = R.drawable.ic_blue_4;
                break;
            case 39:
                drawable = R.drawable.ic_blue_5;
                break;
            case 40:
                drawable = R.drawable.ic_purple_1;
                break;
            case 41:
                drawable = R.drawable.ic_purple_2;

                break;
            case 42:
                drawable = R.drawable.ic_purple_3;
                break;
            case 43:
                drawable = R.drawable.ic_purple_4;
                break;
            case 44:
                drawable = R.drawable.ic_purple_5;
                break;
            case 45:
                drawable = R.drawable.ic_pink_1;

                break;
            case 46:
                drawable = R.drawable.ic_pink_2;
                break;
            case 47:
                drawable = R.drawable.ic_pink_3;
                break;
            case 48:
                drawable = R.drawable.ic_pink_4;

                break;
            case 49:
                drawable = R.drawable.ic_pink_5;
                break;

            case 50:
                drawable = R.drawable.ic_red_1;
                break;
            case 51:
                drawable = R.drawable.ic_red_2;
                break;

            case 52:
                drawable = R.drawable.ic_red_3;

                break;
            case 53:
                drawable = R.drawable.ic_red_4;
                break;
            case 54:
                drawable = R.drawable.ic_red_5;
                break;


        }
        return drawable;
    }

    public void setTopColorPic(int pos) {
        if (ivTop != null)
            ivTop.setImageResource(giveMeColorPic(pos));
    }


    public void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.top_drawer, null);
        txt = (TextView) view.findViewById(R.id.txt);
        ivTop = (ImageView) view.findViewById(R.id.ivTop);
        addView(view);
    }

    public void setText() {

        txt.setText(String.valueOf(this.text_id));


    }

    public void setBackground(int color) {
        this.color = color;
        ShapeDrawable biggerCircle = new ShapeDrawable(new OvalShape());
        biggerCircle.setIntrinsicHeight(60);
        biggerCircle.setIntrinsicWidth(60);
        biggerCircle.setBounds(new Rect(0, 0, 60, 60));
        biggerCircle.getPaint().setColor(color);

        ShapeDrawable smallerCircle = new ShapeDrawable(new OvalShape());
        smallerCircle.setIntrinsicHeight(10);
        smallerCircle.setIntrinsicWidth(10);
        smallerCircle.setBounds(new Rect(0, 0, 10, 10));
        smallerCircle.getPaint().setColor(Color.WHITE);
        smallerCircle.setPadding(2, 2, 2, 2);
        Drawable[] d = {smallerCircle, biggerCircle};

        LayerDrawable layerDrawable = new LayerDrawable(d);

        txt.setBackground(layerDrawable);
    }

    public int getColor() {
        return this.color;
    }

}
