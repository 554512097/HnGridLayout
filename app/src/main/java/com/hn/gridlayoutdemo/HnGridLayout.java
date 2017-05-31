package com.hn.gridlayoutdemo;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

/**
 * Copyright (C) 2017,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：JDShop_v1.0
 * 类描述：
 * 创建人：kevinxie
 * 创建时间：2017/3/29 11:32
 * 修改人：
 * 修改时间：2017/3/29 11:32
 * 修改备注：
 * Version:  1.0.0
 */
public class HnGridLayout extends ViewGroup {

    private int columnsSize = 2;
    private float horizontalSpacing = 20;
    private float verticalSpacing = 20;
    private boolean isCollapse;
    private boolean firstCollapse = true;
    private long mDuration = 300;

    public HnGridLayout(Context context) {
        this(context, null);
    }

    public HnGridLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HnGridLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.HnGridLayout);
        columnsSize = typedArray.getInt(R.styleable.HnGridLayout_columnsSize, 2);
        horizontalSpacing = typedArray.getDimension(R.styleable.HnGridLayout_horizontalSpacing, horizontalSpacing);
        verticalSpacing = typedArray.getDimension(R.styleable.HnGridLayout_verticalSpacing, verticalSpacing);
        firstCollapse = typedArray.getBoolean(R.styleable.HnGridLayout_firstCollapse, firstCollapse);
        mDuration = typedArray.getInt(R.styleable.HnGridLayout_collapseDuration, 300);
        isCollapse = firstCollapse;
    }

    public boolean isCollapse() {
        return isCollapse;
    }

    public void setCollapse(boolean collapse) {
        setCollapse(collapse, false);
    }

    public void setCollapse(boolean collapse, boolean isAnimated) {
        firstCollapse = false;
        isCollapse = collapse;
        if (isAnimated) {
            int startHeight;
            int endHeight;
            if (collapse) {
                startHeight = getUnCollapseHeight();
                endHeight = getCollapseHeight();
            } else {
                startHeight = getCollapseHeight();
                endHeight = getUnCollapseHeight();
            }
            ValueAnimator valueAnimator = ValueAnimator.ofInt(startHeight, endHeight);
//            Log.d("ii", "startHeight:-------" + startHeight);
//            Log.d("ii", "endHeight:-------" + endHeight);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
//                    Log.d("ii", "animation.getAnimatedValue():-------" + animation.getAnimatedValue());
                    getLayoutParams().height = (int) animation.getAnimatedValue();
                    requestLayout();
                }
            });
            valueAnimator.setDuration(mDuration);
            valueAnimator.setInterpolator(new DecelerateInterpolator());
            valueAnimator.start();
        } else {
            getLayoutParams().height = collapse ? getCollapseHeight() : getUnCollapseHeight();
            requestLayout();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (columnsSize < 2) {
            throw new IllegalArgumentException("Columns size must bigger than 2");
        }
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        if (getChildCount() == 0) {
            setMeasuredDimension(parentWidth, getPaddingTop() + getPaddingBottom());
        } else {
            for (int i = 0; i < getChildCount(); i++) {
                View childView = getChildAt(i);
                if (childView.getLayoutParams().width == LayoutParams.MATCH_PARENT) {
                    int childWidth = (int) (((parentWidth - paddingLeft - paddingRight - horizontalSpacing * (columnsSize - 1))) / columnsSize);
                    int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY);

                    int childRequestHeight = childView.getLayoutParams().height;
                    int childHeightMeasureSpec;
                    switch (childRequestHeight) {
                        case LayoutParams.MATCH_PARENT:
                            throw new IllegalArgumentException("children's height can not be match parent");
                        case LayoutParams.WRAP_CONTENT:
                            throw new IllegalArgumentException("you must define children's height");
                        default:
                            childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(childRequestHeight, MeasureSpec.EXACTLY);
                            break;
                    }
                    childView.measure(childWidthMeasureSpec, childHeightMeasureSpec);
                } else {
                    throw new IllegalArgumentException("children's width must be match parent");
                }
            }

            //设置自己的高度
            if (firstCollapse) {
                int height = getCollapseHeight();
                setMeasuredDimension(parentWidth, height);
            } else {
                setMeasuredDimension(parentWidth, MeasureSpec.getSize(heightMeasureSpec));
            }
        }
    }

    private int getCollapseHeight() {
        return getChildAt(0).getMeasuredHeight() + getPaddingTop() + getPaddingBottom();
    }

    private int getUnCollapseHeight() {
        int row;
        if (getChildCount() % columnsSize == 0) {
            row = getChildCount() / columnsSize;
        } else {
            row = getChildCount() / columnsSize + 1;
        }
        int childRequestHeight = getChildAt(0).getMeasuredHeight();
        return (int) (childRequestHeight * row + verticalSpacing * (row - 1) + getPaddingTop() + getPaddingBottom());
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        for (int i = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);
            int measuredWidth = childView.getMeasuredWidth();
            int measuredHeight = childView.getMeasuredHeight();
            int left;
            int top;
            if (i == 0) {
                left = paddingLeft;
                top = paddingTop;
            } else {
                if (i % columnsSize == 0) {
                    //换行了
                    left = paddingLeft;
                    top = (int) (getChildAt(i - 1).getBottom() + verticalSpacing);
                } else {
                    //没有换行
                    left = (int) (getChildAt(i - 1).getRight() + horizontalSpacing);
                    top = getChildAt(i - 1).getTop();
                }
            }
            int right = left + measuredWidth;
            int bottom = top + measuredHeight;
            childView.layout(left, top, right, bottom);
        }
    }
}
