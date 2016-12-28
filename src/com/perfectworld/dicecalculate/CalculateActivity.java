package com.perfectworld.dicecalculate;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

public class CalculateActivity extends Activity {
    private String LOG_TAG = "DICE_CALUCLATE";

    private Spinner mSpinnerCurrentPoint;
    private FlowRadioGroup mFlowRadioGroupNeedPoint;
    private Spinner mSpinnerTopPoint;
    private RadioGroup mRadioGroupShowOrientation;
    private TextView mTextMessage;
    private TextView mTextResult;

    private RadioButton mRadioButton1;
    private RadioButton mRadioButton2;
    private RadioButton mRadioButton3;
    private RadioButton mRadioButton4;
    private RadioButton mRadioButton5;
    private RadioButton mRadioButton6;

    private int MAX = 4;
    private int MIN = 3;

    private int mCurrentPoint = 1;
    private int mNeedPoint;
    private int mOrientation;
    private int mTopPoint = 1;
    private Integer[] mTopPointArray = { 1, 2, 3, 4, 5, 6 };

    private int mMainKey = 0;

    private Integer[] destTopPointArray = new Integer[MAX];
    private Integer[] destBelowPointArray = new Integer[MAX];
    private Integer[] destFrontPointArray = new Integer[MAX];
    private Integer[] destBackPointArray = new Integer[MAX];
    private Integer[] destLeftPointArray = new Integer[MAX];
    private Integer[] destRightPointArray = new Integer[MAX];

    private Integer[] minCombOne = new Integer[MIN];
    private Integer[] minCombTwo = new Integer[MIN];

    private int mFrontPoint;
    private int mBackPoint;
    private int mLeftPoint;
    private int mRightPoint;
    private int mUpPoint;
    private int mBelowPoint;

    private static final int POINT_SUM = 7;

    private ArrayAdapter<Integer> adapterTopPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate);
        initView();
        mTextMessage = (TextView) findViewById(R.id.TextMessage);
        mTextResult = (TextView) findViewById(R.id.TextResult);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.calculate, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        mSpinnerCurrentPoint = (Spinner) findViewById(R.id.SpinnerCurrentPoint);
        // mCurrentPoint = mSpinnerCurrentPoint.getSelectedItem();
        setSpinnerListener();

        mFlowRadioGroupNeedPoint = (FlowRadioGroup) findViewById(R.id.radioGroupNeedPoint);
        RadioButton radioButton = (RadioButton) findViewById(mFlowRadioGroupNeedPoint.getCheckedRadioButtonId());
        mNeedPoint = Integer.parseInt(radioButton.getText().toString().substring(0, 1));
        setFlowRadioGroupListener();

        mRadioGroupShowOrientation = (RadioGroup) findViewById(R.id.radioGroupShowOrientation);
        setGroupShowOrientation();

        initTopPointValues();

        mRadioButton1 = (RadioButton) findViewById(R.id.radio_1);
        mRadioButton2 = (RadioButton) findViewById(R.id.radio_2);
        mRadioButton3 = (RadioButton) findViewById(R.id.radio_3);
        mRadioButton4 = (RadioButton) findViewById(R.id.radio_4);
        mRadioButton5 = (RadioButton) findViewById(R.id.radio_5);
        mRadioButton6 = (RadioButton) findViewById(R.id.radio_6);
        releaseRadioButton();
    }

    private void setSpinnerListener() {
        mSpinnerCurrentPoint.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                mCurrentPoint = pos + 1;
                initTopPointValues();
                calculateDiceOrder();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });
    }

    private void setFlowRadioGroupListener() {
        mFlowRadioGroupNeedPoint.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
                // TODO Auto-generated method stub
                switch (arg1) {
                case R.id.radio_1:
                    mNeedPoint = 1;
                    break;
                case R.id.radio_2:
                    mNeedPoint = 2;
                    break;
                case R.id.radio_3:
                    mNeedPoint = 3;
                    break;
                case R.id.radio_4:
                    mNeedPoint = 4;
                    break;
                case R.id.radio_5:
                    mNeedPoint = 5;
                    break;
                case R.id.radio_6:
                    mNeedPoint = 6;
                    break;
                default:
                    break;
                }
                calculateDiceOrder();
            }
        });
    }

    private void setGroupShowOrientation() {

        mRadioGroupShowOrientation.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
                // TODO Auto-generated method stub
                switch (arg1) {
                case R.id.radio_SingleRight:
                    mOrientation = 1;
                    break;
                case R.id.radio_SingleUp:
                    mOrientation = 2;
                    break;
                default:
                    break;
                }
                calculateDiceOrder();
            }
        });
    }

    private void calculateSixSideArray() {
        // init back point array values
        for (int i = 0; i < MAX; i++) {
            destBackPointArray[i] = POINT_SUM - mCurrentPoint;
            Log.d(LOG_TAG, "Back Point Array " + "" + i + ":" + destBackPointArray[i]);
        }

        // init below point array values
        for (int i = 0; i < MAX; i++) {
            destBelowPointArray[i] = POINT_SUM - destTopPointArray[i];
            Log.d(LOG_TAG, "Below Point Array " + "" + i + ":" + destBelowPointArray[i]);
        }

        // init Left point array values
        // 1-->2 2-->4 3-->1 4-->3
        // EVEN 3 1 4 2
        Integer[] mTempEvenNumberOrder = { 3, 1, 4, 2 };
        // ODD 2 4 1 3
        Integer[] mTempODDNumberOrder = { 2, 4, 1, 3 };
        for (int i = 0; i < MAX; i++) {
            if (mCurrentPoint % 2 == 0) {
                destLeftPointArray[i] = destTopPointArray[mTempEvenNumberOrder[i] - 1];

                destRightPointArray[i] = POINT_SUM - destLeftPointArray[i];
                Log.d(LOG_TAG, "Even Number,destLeftPointArray = " + destLeftPointArray[i] + ";destRightPointArray = "
                        + destRightPointArray[i]);
            } else {
                destLeftPointArray[i] = destTopPointArray[mTempODDNumberOrder[i] - 1];

                destRightPointArray[i] = POINT_SUM - destLeftPointArray[i];
                Log.d(LOG_TAG, "Odd Number,destLeftPointArray = " + destLeftPointArray[i] + ";destRightPointArray = "
                        + destRightPointArray[i]);
            }
        }
    }

    private void initTopPointValues() {
        mSpinnerTopPoint = (Spinner) findViewById(R.id.SpinnerTopPoint);
        // mTopPoint

        int j = 0;
        for (int i = 0; i < mTopPointArray.length; i++) {
            if (mTopPointArray[i] == mCurrentPoint || mTopPointArray[i] == POINT_SUM - mCurrentPoint) {
                /// TODO nothing to do it.
            } else {
                destTopPointArray[j] = mTopPointArray[i];
                j++;
            }

        }
        // mTopPointArray = destPointArray;
        adapterTopPoint = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, destTopPointArray);
        adapterTopPoint.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerTopPoint.setAdapter(adapterTopPoint);
        mSpinnerTopPoint.setOnItemSelectedListener(new SpinnerSelectedListener());
    }

    private class SpinnerSelectedListener implements OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long arg3) {
            mTopPoint = destTopPointArray[pos];
            calculateDiceOrder();
        }

        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }

    private void calculateDiceOrder() {
        calculateSixSideArray();
        releaseRadioButton();
        mUpPoint = mTopPoint;
        for (int i = 0; i < MAX; i++) {
            if (destTopPointArray[i] == mUpPoint) {
                mMainKey = i;
            }
        }
        mFrontPoint = mCurrentPoint;
        mBackPoint = destBackPointArray[mMainKey];
        mLeftPoint = destLeftPointArray[mMainKey];
        mRightPoint = destRightPointArray[mMainKey];
        mBelowPoint = destBelowPointArray[mMainKey];

        if (mOrientation == 1) {
            for (int i = 1; i <= 6; i++) {

                if (i == mLeftPoint || i == mRightPoint || i == mFrontPoint) {
                    switch (i) {
                    case 1:
                        mRadioButton1.setEnabled(false);
                        mRadioButton1.setTextColor(Color.BLACK);
                        break;
                    case 2:
                        mRadioButton2.setEnabled(false);
                        mRadioButton2.setTextColor(Color.BLACK);
                        break;
                    case 3:
                        mRadioButton3.setEnabled(false);
                        mRadioButton3.setTextColor(Color.BLACK);
                        break;
                    case 4:
                        mRadioButton4.setEnabled(false);
                        mRadioButton4.setTextColor(Color.BLACK);
                        break;
                    case 5:
                        mRadioButton5.setEnabled(false);
                        mRadioButton5.setTextColor(Color.BLACK);
                        break;
                    case 6:
                        mRadioButton6.setEnabled(false);
                        mRadioButton6.setTextColor(Color.BLACK);
                        break;
                    default:
                        break;
                    }
                }
            }
            minCombOne[0] = mUpPoint;
            minCombOne[1] = mBelowPoint;
            minCombOne[2] = mBackPoint;
            calculateFinalyResult(minCombOne, 0);
        } else if (mOrientation == 2) {
            for (int i = 1; i <= 6; i++) {
                if (i == mUpPoint || i == mBelowPoint || i == mFrontPoint) {
                    switch (i) {
                    case 1:
                        mRadioButton1.setEnabled(false);
                        mRadioButton1.setTextColor(Color.BLACK);
                        break;
                    case 2:
                        mRadioButton2.setEnabled(false);
                        mRadioButton2.setTextColor(Color.BLACK);
                        break;
                    case 3:
                        mRadioButton3.setEnabled(false);
                        mRadioButton3.setTextColor(Color.BLACK);
                        break;
                    case 4:
                        mRadioButton4.setEnabled(false);
                        mRadioButton4.setTextColor(Color.BLACK);
                        break;
                    case 5:
                        mRadioButton5.setEnabled(false);
                        mRadioButton5.setTextColor(Color.BLACK);
                        break;
                    case 6:
                        mRadioButton6.setEnabled(false);
                        mRadioButton6.setTextColor(Color.BLACK);
                        break;
                    default:
                        break;
                    }
                }
            }
            minCombTwo[0] = mLeftPoint;
            minCombTwo[1] = mRightPoint;
            minCombTwo[2] = mBackPoint;
            calculateFinalyResult(minCombTwo, 1);
        }
        Log.d(LOG_TAG,
                "End dice point FrontPoint : " + mFrontPoint + ";mBackPoint = " + mBackPoint + ";mLeftPoint = "
                        + mLeftPoint + ";mRightPoint = " + mRightPoint + ";mBelowPoint = " + mBelowPoint
                        + ";mUpPoint = " + mUpPoint + "; mMainKey = " + mMainKey);

    }

    private void releaseRadioButton() {
        mRadioButton1.setEnabled(true);
        mRadioButton1.setTextColor(Color.RED);

        mRadioButton2.setEnabled(true);
        mRadioButton2.setTextColor(Color.RED);

        mRadioButton3.setEnabled(true);
        mRadioButton3.setTextColor(Color.RED);

        mRadioButton4.setEnabled(true);
        mRadioButton4.setTextColor(Color.RED);

        mRadioButton5.setEnabled(true);
        mRadioButton5.setTextColor(Color.RED);

        mRadioButton6.setEnabled(true);
        mRadioButton6.setTextColor(Color.RED);
    }

    private void calculateFinalyResult(Integer[] comb, int tag) {
        String mResult = "NULL";
        if (tag == 0) {
            Log.d(LOG_TAG, "minCombOne[0] = " + minCombOne[0] + "; minCombOne[1] = " + minCombOne[1] +"; minCombOne[2] = " + minCombOne[2] +"; mCurrentPoint = " + mCurrentPoint);
            for (int i = 0; i < MIN; i++) {
                if (minCombOne[i] == mNeedPoint) {
                    Log.d(LOG_TAG, "test = " + minCombOne[i] + "; i = " + i +"; mCurrentPoint = " + mCurrentPoint);
                    switch (i) {
                    case 0:
                        mResult = getResources().getString(R.string.result_TRR);
                        break;
                    case 1:
                        mResult = getResources().getString(R.string.result_RRT);
                        break;
                    case 2:
                        mResult = getResources().getString(R.string.result_RTR);
                        break;
                    default:
                        break;
                    }
                }
            }
        } else if (tag == 1) {
            for (int i = 0; i < MIN; i++) {
                if (minCombTwo[i] == mNeedPoint) {
                    switch (i) {
                    case 0:
                        mResult = getResources().getString(R.string.result_TTR);
                        break;
                    case 1:
                        mResult = getResources().getString(R.string.result_RTT);
                        break;
                    case 2:
                        mResult = getResources().getString(R.string.result_TRT);
                        break;
                    default:
                        break;
                    }
                }
            }
        }
        Log.d(LOG_TAG, "Result Message = " + mResult);
        mTextResult.setText(mResult);
        mTextResult.setTextSize(16);
        mTextResult.setTextColor(Color.RED);
    }
}
