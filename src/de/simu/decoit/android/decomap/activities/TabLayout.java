/* 
 * TabLayout.java        0.1.6. 12/03/07
 *  
 * Licensed to the Apache Software Foundation (ASF) under one 
 * or more contributor license agreements.  See the NOTICE file 
 * distributed with this work for additional information 
 * regarding copyright ownership.  The ASF licenses this file 
 * to you under the Apache License, Version 2.0 (the 
 * "License"); you may not use this file except in compliance 
 * with the License.  You may obtain a copy of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on an 
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY 
 * KIND, either express or implied.  See the License for the 
 * specific language governing permissions and limitations 
 * under the License. 
 */

package de.simu.decoit.android.decomap.activities;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TabHost;

/**
 * Tab.-Activity for Setting the different Activities to Tab-Widget
 * 
 * @version 0.1.4.2
 * @author Dennis Dunekacke, Decoit GmbH
 * @author Marcel Jahnke, Decoit GmbH
 */
@SuppressWarnings("deprecation")
public class TabLayout extends TabActivity implements OnGestureListener {

    // gesture detection
    private static final int SWIPE_MIN_DISTANCE = 100;
    private static final int SWIPE_MAX_OFF_PATH = 200;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private GestureDetector mGestureScanner;

    private TabHost mTabHost;

    // -------------------------------------------------------------------------
    // ACTIVITY LIFECYCLE HANDLING
    // -------------------------------------------------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // gesture-detection
        mGestureScanner = new GestureDetector(this);
        Resources res = getResources(); // Resource object to get Drawables
        mTabHost = getTabHost(); // The activity TabHost

        // main-tab
        Intent intent = new Intent(this, MainActivity.class);
        mTabHost.addTab(mTabHost
                .newTabSpec(getResources().getString(R.string.tablayout_tabname_mainactivty))
                .setIndicator(getResources().getString(R.string.tablayout_tabname_mainactivty),
                        res.getDrawable(R.drawable.ic_tab_main)).setContent(intent));

        // setup-tab
        Intent intent2 = new Intent(this, SetupActivity.class);
        mTabHost.addTab(mTabHost
                .newTabSpec(getResources().getString(R.string.tablayout_tabname_setupactivity))
                .setIndicator(getResources().getString(R.string.tablayout_tabname_setupactivity),
                        res.getDrawable(R.drawable.ic_tab_setup)).setContent(intent2));

        // device-status-tab
        Intent intent3 = new Intent(this, StatusActivity.class);
        mTabHost.addTab(mTabHost
                .newTabSpec(getResources().getString(R.string.tablayout_tabname_statusactivity))
                .setIndicator(getResources().getString(R.string.tablayout_tabname_statusactivity),
                        res.getDrawable(R.drawable.ic_tab_status)).setContent(intent3));

        // log-messages-tab
        Intent intent4 = new Intent(this, LogActivity.class);
        mTabHost.addTab(mTabHost
                .newTabSpec(getResources().getString(R.string.tablayout_tabname_logactivity))
                .setIndicator(getResources().getString(R.string.tablayout_tabname_logactivity),
                        res.getDrawable(R.drawable.ic_tab_log)).setContent(intent4));
        Intent intent5 = new Intent(this, InfoActivity.class);

        // about/info-tab
        mTabHost.addTab(mTabHost
                .newTabSpec(getResources().getString(R.string.tablayout_tabname_aboutactivity))
                .setIndicator(getResources().getString(R.string.tablayout_tabname_aboutactivity),
                        res.getDrawable(R.drawable.ic_tab_about)).setContent(intent5));

        // set start tab
        mTabHost.setCurrentTab(0);

        // set tabs Colors
        mTabHost.setBackgroundColor(Color.BLACK);
        mTabHost.getTabWidget().setBackgroundColor(Color.BLACK);
    }

    // -------------------------------------------------------------------------
    // BUTTON AND GESTURES HANDLING
    // -------------------------------------------------------------------------

    @Override
    public boolean onTouchEvent(MotionEvent me) {
        return mGestureScanner.onTouchEvent(me);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mGestureScanner != null) {
            if (mGestureScanner.onTouchEvent(ev))
                return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        int currentTab = mTabHost.getCurrentTab();
        // Check movement along the Y-axis. If it exceeds SWIPE_MAX_OFF_PATH,
        // then dismiss the swipe.
        if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH) {
            return false;
        }

        // Swipe from right to left.
        // The swipe needs to exceed a certain distance (SWIPE_MIN_DISTANCE) and
        // a certain velocity (SWIPE_THRESHOLD_VELOCITY).
        if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
            switch (currentTab) {
            case 0:
                // mTabHost.setAnimation(outToLeftAnimation());
                mTabHost.setAnimation(inFromRightAnimation());
                mTabHost.setCurrentTab(1);

                break;
            case 1:
                // mTabHost.setAnimation(outToLeftAnimation());
                mTabHost.setAnimation(inFromRightAnimation());
                mTabHost.setCurrentTab(2);
                break;
            case 2:
                // mTabHost.setAnimation(outToLeftAnimation());
                mTabHost.setAnimation(inFromRightAnimation());
                mTabHost.setCurrentTab(3);
                break;
            case 3:
                // mTabHost.setAnimation(outToLeftAnimation());
                mTabHost.setAnimation(inFromRightAnimation());
                mTabHost.setCurrentTab(4);
                break;
            default:
                break;
            }
            return true;
        }

        // Swipe from left to right.
        // The swipe needs to exceed a certain distance (SWIPE_MIN_DISTANCE) and
        // a certain velocity (SWIPE_THRESHOLD_VELOCITY).
        if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
            switch (currentTab) {
            case 4:
                mTabHost.setAnimation(inFromLeftAnimation());
                mTabHost.setCurrentTab(3);
                break;
            case 3:
                mTabHost.setAnimation(inFromLeftAnimation());
                mTabHost.setCurrentTab(2);
                break;
            case 2:
                mTabHost.setAnimation(inFromLeftAnimation());
                mTabHost.setCurrentTab(1);
                break;
            case 1:
                mTabHost.setAnimation(inFromLeftAnimation());
                mTabHost.setCurrentTab(0);
                break;
            default:
                break;
            }
            return true;
        }

        return false;
    }

    /**
     * get animation object for "tab in from right"-animation
     * 
     * @return Animation
     */
    public Animation inFromRightAnimation() {
        Animation inFromRight = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromRight.setDuration((long) 0);
        inFromRight.setInterpolator(new AccelerateInterpolator());
        return inFromRight;
    }

    /**
     * get animation object for "tab in from left"-animation
     * 
     * @return Animation
     */
    public Animation inFromLeftAnimation() {

        Animation inFromRight = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromRight.setDuration((long) 0);
        inFromRight.setInterpolator(new AccelerateInterpolator());
        return inFromRight;
    }

    /**
     * get animation object for "tab out to left"-animation
     * 
     * @return Animation
     */
    public Animation outToLeftAnimation() {
        Animation outtoLeft = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, -1.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        outtoLeft.setDuration((long) 0);
        outtoLeft.setInterpolator(new AccelerateInterpolator());
        return outtoLeft;
    }

    /**
     * create options menu
     * 
     * @param menu
     *            options-menu to be creates
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * handler for actions performed when options-menu items is selected by the
     * user
     * 
     * @param item
     *            options-item that has been selected
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.opt_quit:
            finish();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * we override the behavior of the back-button so that the application runs
     * in the background (instead of destroying it) when pressing back (similar
     * to the home button)
     */
    @Override
    public void onBackPressed() {
        Intent setIntent = new Intent(Intent.ACTION_MAIN);
        setIntent.addCategory(Intent.CATEGORY_HOME);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(setIntent);
    }
}