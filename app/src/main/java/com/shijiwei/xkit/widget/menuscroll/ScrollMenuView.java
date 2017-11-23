package com.shijiwei.xkit.widget.menuscroll;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shijiwei.xkit.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shijiwei on 2016/10/6.
 */
public class ScrollMenuView<T> extends LinearLayout implements
        ViewPager.OnPageChangeListener,
        AdapterView.OnItemClickListener {

    private int mDefaultColumns = 4;
    private int mDefaultRows = 2;

    private int mMarginOfMenuWithCircle = 10;
    private int mIconWidth = 60;
    private int mLableTextSize = 12;
    private int mCircleRadius = 5;
    private int mItemHeight;

    private ViewPager mViewPager;
    // indicator
    private LinearLayout mLayoutOfCircle;
    private List<ScrollMenuItem<T>> mMenuItemSet;
    private List<GridView> mMenuPageSet;

    private MenuPagerAdapter mMenuPagerAdapter;

    private OnItemClickListener mOnItemClickListener;


    public interface OnItemClickListener {
        void onItemClick(ScrollMenuItem item);
    }

    public ScrollMenuView(Context context) {
        this(context, null);
    }

    public ScrollMenuView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrollMenuView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initialize(context);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {


        int expandSpec = MeasureSpec.makeMeasureSpec(mItemHeight * mDefaultRows + mMarginOfMenuWithCircle + mCircleRadius * 2,
                MeasureSpec.EXACTLY);

        super.onMeasure(widthMeasureSpec, expandSpec);
    }

    private void initialize(Context context) {

        setOrientation(VERTICAL);

        mViewPager = new ViewPager(context);
        LinearLayout.LayoutParams lpOfPager = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT,
                1.0f);
        mViewPager.setLayoutParams(lpOfPager);
        mViewPager.addOnPageChangeListener(this);
        addView(mViewPager);

        mLayoutOfCircle = new LinearLayout(context);
        LinearLayout.LayoutParams lpOfCircleLayout = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT,
                10);
        lpOfCircleLayout.gravity = Gravity.CENTER;
        lpOfCircleLayout.setMargins(0, mMarginOfMenuWithCircle, 0, 0);
        mLayoutOfCircle.setLayoutParams(lpOfCircleLayout);
        mLayoutOfCircle.setGravity(HORIZONTAL);
        addView(mLayoutOfCircle);

        mMenuPageSet = new ArrayList<>();
        mMenuPagerAdapter = new MenuPagerAdapter(mMenuPageSet);
        mViewPager.setAdapter(mMenuPagerAdapter);

    }

    public void setMenuItemSet(List<T> data) throws IllegalAccessException {
        this.setMenuItemSet(data, mDefaultColumns, mDefaultRows);
    }

    public void setMenuItemSet(List<T> data, int columns, int rows) throws IllegalAccessException {

        if (data == null)
            return;

        this.mMenuItemSet = data2ScrollMenuItem(data);
        this.mDefaultRows = rows;
        this.mDefaultColumns = columns;

        int numPagers = mMenuItemSet.size() % (mDefaultRows * mDefaultColumns) == 0
                ? mMenuItemSet.size() / (mDefaultRows * mDefaultColumns)
                : mMenuItemSet.size() / (mDefaultRows * mDefaultColumns) + 1;

        for (int i = 0; i < numPagers; i++) {
            GridView gridView = new MyGridView(getContext());
            gridView.setSelector(R.drawable.shape_list_transparent_selector);
            gridView.setNumColumns(mDefaultColumns);
            LinearLayout.LayoutParams l = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            gridView.setLayoutParams(l);

            List<ScrollMenuItem> menuItems = new ArrayList<>();
            menuItems.addAll(mMenuItemSet.subList(
                    (mDefaultRows * mDefaultColumns) * i,
                    (mDefaultRows * mDefaultColumns) * (i + 1) > mMenuItemSet.size() ?
                            mMenuItemSet.size() : (mDefaultRows * mDefaultColumns) * (i + 1)));

            MenuAdapter menuAdapter = new MenuAdapter(getContext(), menuItems);
            gridView.setAdapter(menuAdapter);
            gridView.setOnItemClickListener(this);

            mMenuPageSet.add(gridView);

            //add circle
            ImageView circle = new ImageView(getContext());
            LinearLayout.LayoutParams lp = new LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 0, 2, 0);
            circle.setLayoutParams(lp);
            circle.setImageResource(R.drawable.shape_bg_circle_selector);
            mLayoutOfCircle.addView(circle);

        }

        mLayoutOfCircle.getChildAt(0).setSelected(true);
        mMenuPagerAdapter.notifyDataSetChanged();
    }

    private List<ScrollMenuItem<T>> data2ScrollMenuItem(List<T> data) throws IllegalAccessException {
        List<ScrollMenuItem<T>> items = new ArrayList<>();
        for (T t : data) {
            String lable = null;
            String iconURL = null;
            int iconResoureceId = -1;
            Class<?> cls = t.getClass();
            Field[] declaredFields = cls.getDeclaredFields();
            for (Field f : declaredFields) {
                ScrollMenuItemField annotation = f.getAnnotation(ScrollMenuItemField.class);
                if (annotation != null) {
                    if (annotation.iconResourceIdFiled().equals("iconResourceId")) {
                        f.setAccessible(true);
                        iconResoureceId = f.getInt(t);
                    }

                    if (annotation.iconURLFiled().equals("iconURL")) {
                        f.setAccessible(true);
                        iconURL = (String) f.get(t);
                    }

                    if (annotation.lableFiled().equals("lable")) {
                        f.setAccessible(true);
                        lable = (String) f.get(t);
                    }
                }
            }

            items.add(new ScrollMenuItem<T>(lable, iconResoureceId, iconURL, t));
        }
        return items;
    }


    public void setIconWidth(int width) {
        this.mIconWidth = width;
    }

    public void setLableTextSize(int size) {
        this.mLableTextSize = size;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }


    public int getCurrentNumPager() {

        for (int i = 0; i < mLayoutOfCircle.getChildCount(); i++) {
            if (mLayoutOfCircle.getChildAt(i).isSelected())
                return i;
        }

        return 0;
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);

    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

        for (int i = 0; i < mLayoutOfCircle.getChildCount(); i++) {
            View child = mLayoutOfCircle.getChildAt(i);
            child.setSelected(false);
        }

        mLayoutOfCircle.getChildAt(position).setSelected(true);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if (mOnItemClickListener != null)
            mOnItemClickListener.onItemClick(mMenuItemSet.get(getCurrentNumPager() * (mDefaultColumns * mDefaultRows) + position));
    }

    class MenuPagerAdapter extends PagerAdapter {

        private List<GridView> mMenuSet;

        public MenuPagerAdapter(List<GridView> mMenuSet) {
            this.mMenuSet = mMenuSet;
        }

        @Override
        public int getCount() {
            return mMenuSet.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mMenuSet.get(position));
            return mMenuSet.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mMenuSet.get(position));
        }
    }

    class MenuAdapter extends BaseAdapter {

        private Context context;
        private List<ScrollMenuItem> mMenuItemSet;

        public MenuAdapter(Context context, List<ScrollMenuItem> mMenuItemSet) {
            this.mMenuItemSet = mMenuItemSet;
            this.context = context;
        }

        @Override
        public int getCount() {
            return mMenuItemSet.size();
        }

        @Override
        public Object getItem(int position) {
            return mMenuItemSet.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {

                LinearLayout viewGroup = new LinearLayout(context);
                viewGroup.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
                viewGroup.setOrientation(VERTICAL);
                AbsListView.LayoutParams lpOfGroup = new AbsListView.LayoutParams(
                        AbsListView.LayoutParams.MATCH_PARENT,
                        AbsListView.LayoutParams.WRAP_CONTENT);
                viewGroup.setPadding(0, 25, 0, 0);
                viewGroup.setLayoutParams(lpOfGroup);

                ImageView icon = new ImageView(context);
                LinearLayout.LayoutParams lpOfIcon = new LinearLayout.LayoutParams(
                        mIconWidth,
                        mIconWidth);
                icon.setLayoutParams(lpOfIcon);
                icon.setScaleType(ImageView.ScaleType.CENTER_CROP);
                viewGroup.addView(icon);

                TextView lable = new TextView(context);
                LinearLayout.LayoutParams lpOfLable = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                lpOfLable.setMargins(0, 5, 0, 0);
                lable.setLayoutParams(lpOfLable);
                lable.setTextSize(mLableTextSize);
                viewGroup.addView(lable);

                convertView = viewGroup;
                mItemHeight = viewGroup.getPaddingTop() +
                        mIconWidth +
                        dip2px(getContext(), mLableTextSize + lpOfLable.topMargin + lpOfLable.bottomMargin + 3 * 2);

                holder = new ViewHolder();

                holder.icon = (ImageView) ((ViewGroup) convertView).getChildAt(0);
                holder.lable = (TextView) ((ViewGroup) convertView).getChildAt(1);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.lable.setText(mMenuItemSet.get(position).getLable());
            holder.icon.setImageResource(mMenuItemSet.get(position).getIconResourceId());
            return convertView;
        }

        class ViewHolder {

            ImageView icon;
            TextView lable;

        }
    }

    class MyGridView extends GridView {

        public MyGridView(Context context) {
            super(context);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

            int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,

                    MeasureSpec.AT_MOST);

            super.onMeasure(widthMeasureSpec, expandSpec);
        }
    }
}

