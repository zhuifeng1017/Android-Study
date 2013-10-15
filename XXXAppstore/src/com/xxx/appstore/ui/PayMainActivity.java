package com.xxx.appstore.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager.BadTokenException;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.ViewAnimator;

import com.xxx.appstore.Session;
import com.xxx.appstore.common.ApiAsyncTask;
import com.xxx.appstore.common.MarketAPI;
import com.xxx.appstore.common.util.DBUtils;
import com.xxx.appstore.common.util.DialogUtil;
import com.xxx.appstore.common.util.MobileSecurePayHelper;
import com.xxx.appstore.common.util.MobileSecurePayer;
import com.xxx.appstore.common.util.TopBar;
import com.xxx.appstore.common.util.Utils;
import com.xxx.appstore.common.util.DialogUtil.ProgressDialogListener;
import com.xxx.appstore.common.util.DialogUtil.WarningDialogListener;
import com.xxx.appstore.common.vo.CardInfo;
import com.xxx.appstore.common.vo.CardsVerification;
import com.xxx.appstore.common.vo.CardsVerifications;
import com.xxx.appstore.common.widget.BaseActivity;
import com.xxx.appstore.common.widget.TitleSpinner;

import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

public class PayMainActivity extends BaseActivity implements
		View.OnClickListener, ApiAsyncTask.ApiRequestListener,
		DialogUtil.ProgressDialogListener, DialogUtil.WarningDialogListener,
		CompoundButton.OnCheckedChangeListener {
	private static final int DIALOG_ACCOUNT_NUM_WRONG = 10;
	private static final int DIALOG_CARD_IS_EMPTY = 2;
	private static final int DIALOG_CHARGE_CARD_ERROR = 17;
	private static final int DIALOG_CHARGE_CARD_NO_ENOUGH_BALANCE_ERROR = 18;
	private static final int DIALOG_CHARGE_CARD_OR_PWD_FAILED = 20;
	private static final int DIALOG_CHARGE_CONNECT_FAILED = 15;
	private static final int DIALOG_CHARGE_FAILED = 14;
	private static final int DIALOG_CHARGE_INFO = 22;
	private static final int DIALOG_CHARGE_NETWORK_ERROR = 19;
	private static final int DIALOG_CHARGE_SUCCESS = 9;
	private static final int DIALOG_CHECKBOX_IS_EMPTY = 4;
	private static final int DIALOG_CONFIRM = 5;
	private static final int DIALOG_ERROR_1 = 6;
	private static final int DIALOG_ERROR_2 = 7;
	private static final int DIALOG_ERROR_3 = 8;
	private static final int DIALOG_NO_CARD_CHOOSE = 21;
	private static final int DIALOG_OUT_TIME = 13;
	private static final int DIALOG_PASSWORD_IS_EMPTY = 3;
	private static final int DIALOG_PROGRESS_BAR = 0;
	private static final int DIALOG_PSD_NUM_WRONG = 11;
	private static final int DIALOG_QUERY_CREDIT = 1;
	private static final int DIALOG_START_ERROR = 16;
	private static final int DIALOG_UNKNOWN_ERROR = 12;
	private int[] cardMoney;
	private int checkedId = -1;
	private long lastTime;
	private CardInfo mCard;
	private EditText mCardNumberEditText;
	private EditText mCardPasswordEditText;
	private CardsVerification mCardVerification;
	private CardsVerifications mCardVerifications;
	private TitleSpinner mCardsSpinner;
	private ViewAnimator mCenterArea;
	private int mCredit;
	private TitleSpinner mDenominationSpinner;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message paramAnonymousMessage) {
			// try
			// {
			// String str = (String)paramAnonymousMessage.obj;
			// switch (paramAnonymousMessage.what)
			// {
			// default:
			// super.handleMessage(paramAnonymousMessage);
			// break;
			// case 1:
			// {
			// int i =
			// Integer.valueOf(str.split(";")[0].split("=")[1].replace("{",
			// "").replace("}", "")).intValue();
			// if ((6001 == i) || (4000 == i))
			// {
			// try
			// {
			// PayMainActivity.this.removeDialog(0);
			// }
			// catch (WindowManager.BadTokenException localBadTokenException2)
			// {
			// }
			// }
			// else
			// {
			// PayMainActivity.access$902(PayMainActivity.this,
			// System.currentTimeMillis());
			// MarketAPI.queryAliPayResult(PayMainActivity.this,
			// PayMainActivity.this, PayMainActivity.this.mOrderID);
			// }
			// break;
			// }
			// case 2:
			// {
			// try
			// {
			// PayMainActivity.this.removeDialog(0);
			// PayMainActivity.this.showDialog(14);
			// }
			// catch (WindowManager.BadTokenException localBadTokenException1)
			// {
			// }
			// }
			// break;
			//
			// }
			// }
			// catch (Exception localException1)
			// {
			// localException1.printStackTrace();
			// }
		}
	};
	private TextView mHintView;
	private EditText mInputEditText;
	private boolean mIsOnPause;
	private String mOrderID;
	private ProgressBar mProgressBar;
	private Button mRetryButton;
	private String mType;

	private void alipay(String paramString) {
		if (new MobileSecurePayHelper(this).detectMobile_sp()) {
			try {
				showDialog(0);
				MarketAPI.getAliPayOrder(this, this,
						Integer.parseInt(paramString), getString(2131296528),
						getString(2131296529));
			} catch (Exception localException) {
				while (true)
					Utils.W("alipay", localException);
			}
		}
	}

	private void initAlipayView() {
		initTopBar(2130903106, 2131296523);
		TextView localTextView1 = (TextView) findViewById(2131492994);
		Object[] arrayOfObject = new Object[2];
		arrayOfObject[0] = this.mSession.getUserName();
		arrayOfObject[1] = Integer.valueOf(getIntent()
				.getIntExtra("balance", 0));
		localTextView1.setText(getString(2131296525, arrayOfObject));
		if (!getIntent().hasExtra("balance")) {
			showDialog(0);
			MarketAPI.getBalance(this, this);
		}
		final Button localButton = (Button) findViewById(2131492995);
		localButton.setOnClickListener(this);
		final TextView localTextView2 = (TextView) findViewById(2131492866);
		this.mInputEditText = ((EditText) findViewById(2131492939));
		this.mInputEditText.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable paramAnonymousEditable) {
				if (paramAnonymousEditable.length() > 0) {
					localButton.setEnabled(true);
					localTextView2.setText(PayMainActivity.this
							.getString(2131296526)
							+ "  价值"
							+ 10
							* Integer
									.valueOf(paramAnonymousEditable.toString())
									.intValue() + "机锋券");
				} else {
					localButton.setEnabled(false);
					localTextView2.setText(2131296526);
				}
			}

			public void beforeTextChanged(
					CharSequence paramAnonymousCharSequence,
					int paramAnonymousInt1, int paramAnonymousInt2,
					int paramAnonymousInt3) {
			}

			public void onTextChanged(CharSequence paramAnonymousCharSequence,
					int paramAnonymousInt1, int paramAnonymousInt2,
					int paramAnonymousInt3) {
			}
		});
		if (getIntent().hasExtra("payment")) {
			int i = Math.min((int) Math.ceil(getIntent().getIntExtra("payment",
					100) / 10.0D), 999);
			this.mInputEditText.setText(i + "");
		} else
			this.mInputEditText.setText("10");

		Selection.setSelection(this.mInputEditText.getText(),
				this.mInputEditText.length());
	}

	private void initData() {
		requestData();
	}

	private void initPhoneCardView() {
		initTopBar(2130903105, 2131296532);
		this.mCenterArea = ((ViewAnimator) findViewById(2131492983));
		this.mHintView = ((TextView) findViewById(2131492937));
		this.mRetryButton = ((Button) findViewById(2131492986));
		this.mRetryButton.setOnClickListener(this);
		this.mProgressBar = ((ProgressBar) findViewById(2131492985));
		this.mCardsSpinner = ((TitleSpinner) findViewById(2131492987));
		this.mDenominationSpinner = ((TitleSpinner) findViewById(2131492988));
		this.mCardNumberEditText = ((EditText) findViewById(2131492989));
		this.mCardPasswordEditText = ((EditText) findViewById(2131492990));
		((TextView) findViewById(2131492991)).setOnClickListener(this);
		((Button) findViewById(2131492993)).setOnClickListener(this);
	}

	private void initTopBar(int paramInt1, int paramInt2) {
		setContentView(paramInt1);
		Session localSession = this.mSession;
		View[] arrayOfView = new View[1];
		arrayOfView[0] = findViewById(2131493035);
		int[] arrayOfInt = new int[1];
		arrayOfInt[0] = 0;
		TopBar.createTopBar(localSession, this, arrayOfView, arrayOfInt,
				getString(paramInt2));
		TextView localTextView = new TextView(this);
		localTextView.setId(100);
		localTextView.setTextColor(-1);
		localTextView.setFocusable(true);
		localTextView.setClickable(true);
		localTextView.setOnClickListener(this);
		localTextView.setText(Html.fromHtml(getString(2131296524)));
		RelativeLayout.LayoutParams localLayoutParams = new RelativeLayout.LayoutParams(
				-2, -2);
		localLayoutParams.addRule(11, -1);
		localLayoutParams.addRule(15, -1);
		localLayoutParams.rightMargin = 10;
		((RelativeLayout) findViewById(2131492884)).addView(localTextView,
				localLayoutParams);
	}

	private boolean isOutTime() {
		if (System.currentTimeMillis() - this.lastTime > 60000L)
			;
		for (boolean bool = true;; bool = false)
			return bool;
	}

	private void onClickOk() {
		String s;
		String s1;
		s = mCardNumberEditText.getText().toString();
		s1 = mCardPasswordEditText.getText().toString();
		if (mCard == null) {
			if (!isFinishing())
				showDialog(21);
			return;
		}
		if (checkedId == -1) {
			if (!isFinishing())
				showDialog(4);
		} else if (TextUtils.isEmpty(s)) {
			if (!isFinishing())
				showDialog(2);
		} else if (TextUtils.isEmpty(s1)) {
			if (!isFinishing())
				showDialog(3);
		} else if (s.length() != mCardVerification.accountNum) {
			if (!isFinishing())
				showDialog(10);
		} else if (s1.length() != mCardVerification.passwordNum) {
			if (!isFinishing())
				showDialog(11);
		} else {
			mCard.cardAccount = mCardNumberEditText.getText().toString();
			mCard.cardPassword = mCardPasswordEditText.getText().toString();
			mCard.cardCredit = 100 * cardMoney[checkedId];
			if (!isFinishing())
				showDialog(5);
		}
	}

	private void onClickRetry() {
		showLoadingHint();
		requestData();
	}

	private void requestCharge() {
		MarketAPI.charge(this, this, null, "GFanClient", this.mCard);
	}

	private void requestData() {
		MarketAPI.syncCardInfo(this, this);
	}

	private void requestQuery() {
		MarketAPI.queryChargeResult(this, this, this.mOrderID);
	}

	private void showHint(String paramString, boolean paramBoolean) {
		int i = 8;
		this.mHintView.setText(paramString);
		this.mProgressBar.setVisibility(i);
		Button localButton = this.mRetryButton;
		if (paramBoolean)
			i = 0;
		localButton.setVisibility(i);
		this.mCenterArea.setDisplayedChild(0);
	}

	private void showListView() {
		this.mProgressBar.setVisibility(8);
		this.mCenterArea.setDisplayedChild(1);
		ArrayAdapter localArrayAdapter = new ArrayAdapter(this, 17367048,
				this.mCardVerifications.getCardNames());
		localArrayAdapter.setDropDownViewResource(17367049);
		this.mCardsSpinner.setAdapter(localArrayAdapter);
		this.mCardsSpinner.setOnClickListener(new OnClickListener() {
			public void onClick(DialogInterface paramAnonymousDialogInterface,
					int paramAnonymousInt) {
				// PayMainActivity.access$002(PayMainActivity.this, new
				// CardInfo());
				// PayMainActivity.access$102(PayMainActivity.this,
				// (CardsVerification)PayMainActivity.this.mCardVerifications.cards.get(paramAnonymousInt));
				// String[] arrayOfString1 =
				// PayMainActivity.this.mCardVerification.credit.split(",");
				// int i = arrayOfString1.length;
				// PayMainActivity.access$302(PayMainActivity.this, new int[i]);
				// String[] arrayOfString2 = new String[i];
				// for (int j = 0; j < i; j++)
				// {
				// PayMainActivity.this.cardMoney[j] =
				// Integer.parseInt(arrayOfString1[j]);
				// PayMainActivity localPayMainActivity3 = PayMainActivity.this;
				// Object[] arrayOfObject3 = new Object[1];
				// arrayOfObject3[0] =
				// Integer.valueOf(PayMainActivity.this.cardMoney[j]);
				// arrayOfString2[j] =
				// localPayMainActivity3.getString(2131296516, arrayOfObject3);
				// }
				// PayMainActivity.this.mCard.payType =
				// PayMainActivity.this.mCardVerification.pay_type;
				// ArrayAdapter localArrayAdapter = new
				// ArrayAdapter(PayMainActivity.this, 17367048, arrayOfString2);
				// localArrayAdapter.setDropDownViewResource(17367049);
				// PayMainActivity.this.mDenominationSpinner.setAdapter(localArrayAdapter);
				// PayMainActivity.this.mDenominationSpinner.setOnClickListener(new
				// PayMainActivity.2.1(this));
				// String str1 = "";
				// if (PayMainActivity.this.mCardVerification != null)
				// {
				// PayMainActivity localPayMainActivity2 = PayMainActivity.this;
				// Object[] arrayOfObject2 = new Object[1];
				// arrayOfObject2[0] =
				// Integer.valueOf(PayMainActivity.this.mCardVerification.accountNum);
				// str1 = localPayMainActivity2.getString(2131296500,
				// arrayOfObject2);
				// }
				// PayMainActivity.this.mCardNumberEditText.setHint(PayMainActivity.this.getString(2131296490)
				// + str1);
				// String str2 = "";
				// if (PayMainActivity.this.mCardVerification != null)
				// {
				// PayMainActivity localPayMainActivity1 = PayMainActivity.this;
				// Object[] arrayOfObject1 = new Object[1];
				// arrayOfObject1[0] =
				// Integer.valueOf(PayMainActivity.this.mCardVerification.passwordNum);
				// str2 = localPayMainActivity1.getString(2131296500,
				// arrayOfObject1);
				// }
				// PayMainActivity.this.mCardPasswordEditText.setHint(PayMainActivity.this.getString(2131296491)
				// + str2);
			}
		});
	}

	private void showLoadingHint() {
		this.mHintView.setText(2131296381);
		this.mProgressBar.setVisibility(0);
		this.mRetryButton.setVisibility(8);
		this.mCenterArea.setDisplayedChild(0);
	}

	private void startChargeTypeListActivity(boolean paramBoolean) {
		finish();
		Intent localIntent = new Intent(this, ChargeTypeListActivity.class);
		localIntent.putExtras(getIntent());
		if (paramBoolean)
			localIntent.putExtra("error", this.mType);
		startActivity(localIntent);
	}

	public void onCheckedChanged(CompoundButton paramCompoundButton,
			boolean paramBoolean) {
		if (paramBoolean)
			this.mSession.setDefaultChargeType(this.mType);
		else
			this.mSession.setDefaultChargeType(null);
	}

	public void onClick(View paramView) {
		switch (paramView.getId()) {
		default:
			break;
		case 2131493033:
			onSearchRequested();
			break;
		case 2131492986:
			onClickRetry();
			break;
		case 2131492993:
			onClickOk();
			break;
		case 2131492991:
			if (!isFinishing()) {
				showDialog(22);
			}
			break;
		case 2131492995:
			alipay(this.mInputEditText.getText().toString());
			break;
		case 100:
			startChargeTypeListActivity(false);
			break;
		}
	}

	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setContentView(2130903105);
		this.mType = getIntent().getStringExtra("type");
		if (this.mType == null) {
			finish();
			startActivity(new Intent(getApplicationContext(),
					ChargeTypeListActivity.class));
			return;
		}
		if ("phonecard".equals(this.mType)) {
			initPhoneCardView();
			initData();
		} else if ("alipay".equals(this.mType))
			initAlipayView();

		CheckBox localCheckBox = (CheckBox) findViewById(2131492992);
		localCheckBox.setChecked(this.mType.equals(this.mSession
				.getDefaultChargeType()));
		localCheckBox.setOnCheckedChangeListener(this);
	}

	protected Dialog onCreateDialog(int paramInt) {
		Dialog localObject = super.onCreateDialog(paramInt);
		switch (paramInt) {
		default:

			break;
		case 0:
			localObject = DialogUtil
					.createIndeterminateProgressWhiteTextDialog(this, paramInt,
							getString(2131296501), false, this);
			break;
		case 1:
			String str2 = getString(2131296536);
			Object[] arrayOfObject6 = new Object[1];
			arrayOfObject6[0] = Integer.valueOf(this.mCredit);
			localObject = DialogUtil.createShowHintOKDialog(this, paramInt,
					str2, getString(2131296502, arrayOfObject6));
			break;
		case 2:
			localObject = DialogUtil.createOKWarningDialog(this, paramInt,
					getString(2131296503), this);
			break;
		case 3:
			localObject = DialogUtil.createOKWarningDialog(this, paramInt,
					getString(2131296405), this);
			break;
		case 4:
			localObject = DialogUtil.createOKWarningDialog(this, paramInt,
					getString(2131296504), this);
			break;
		case 5:
			String str1 = getString(2131296505);
			Object[] arrayOfObject5 = new Object[2];
			arrayOfObject5[0] = Integer.valueOf(this.cardMoney[this.checkedId]);
			arrayOfObject5[1] = this.mCardVerification.name;
			localObject = DialogUtil.createYesNo2TVDialog(this, paramInt,
					String.format(str1, arrayOfObject5), getString(2131296506),
					this);
			break;
		case 6:
			localObject = DialogUtil.createOKWarningDialog(this, paramInt,
					getString(2131296507), this);
			break;
		case 7:
			localObject = DialogUtil.createOKWarningDialog(this, paramInt,
					getString(2131296508), this);
			break;
		case 8:
			localObject = DialogUtil.createOKWarningDialog(this, paramInt,
					getString(2131296509), this);
			break;
		case 10:
			Object[] arrayOfObject4 = new Object[2];
			arrayOfObject4[0] = this.mCardVerification.name;
			arrayOfObject4[1] = Integer
					.valueOf(this.mCardVerification.accountNum);
			localObject = DialogUtil.createOKWarningDialog(this, paramInt,
					getString(2131296510, arrayOfObject4), this);
			break;
		case 11:
			Object[] arrayOfObject3 = new Object[2];
			arrayOfObject3[0] = this.mCardVerification.name;
			arrayOfObject3[1] = Integer
					.valueOf(this.mCardVerification.passwordNum);
			localObject = DialogUtil.createOKWarningDialog(this, paramInt,
					getString(2131296511, arrayOfObject3), this);
			break;
		case 12:
			localObject = DialogUtil.createOKWarningDialog(this, paramInt,
					getString(2131296512), this);
			break;
		case 13:
			localObject = DialogUtil.createOKWarningDialog(this, paramInt,
					getString(2131296495), this);
			break;
		case 9:
			if ("phonecard".equals(this.mType)) {
				Object[] arrayOfObject2 = new Object[2];
				arrayOfObject2[0] = Integer
						.valueOf(this.cardMoney[this.checkedId]);
				arrayOfObject2[1] = Integer
						.valueOf(10 * this.cardMoney[this.checkedId]);
				localObject = DialogUtil.createOKWarningDialog(this, paramInt,
						getString(2131296496, arrayOfObject2), this);
			} else {
				int i = Integer.parseInt(this.mInputEditText.getText()
						.toString());
				Object[] arrayOfObject1 = new Object[2];
				arrayOfObject1[0] = Integer.valueOf(i);
				arrayOfObject1[1] = Integer.valueOf(i * 10);
				localObject = DialogUtil.createOKWarningDialog(this, paramInt,
						getString(2131296496, arrayOfObject1), this);
			}
			break;
		case 14:
			localObject = DialogUtil.createOKWarningDialog(this, paramInt,
					getString(2131296512), null);
			break;
		case 15:
			localObject = DialogUtil.createOKWarningDialog(this, paramInt,
					getString(2131296509), null);
			break;
		case 19:
			localObject = DialogUtil.createOKWarningDialog(this, paramInt,
					getString(2131296497), null);
			break;
		case 16:
			localObject = DialogUtil.createOKWarningDialog(this, paramInt,
					getString(2131296507), null);
			break;
		case 17:
			localObject = DialogUtil.createOKWarningDialog(this, paramInt,
					getString(2131296498), null);
			break;
		case 20:
			localObject = DialogUtil.createOKWarningDialog(this, paramInt,
					getString(2131296499), null);
			break;
		case 18:
			localObject = new AlertDialog.Builder(this).setMessage(2131296515)
					.setPositiveButton(2131296376, null).create();
			break;
		case 21:
			break;
		case 22:
			break;
		}
		return localObject;
	}

	public void onError(int i, int j) {
		if (j == 204) {
			mCardVerifications = DBUtils.getAllCardsVerification(this);
			if (mCardVerifications.getCardNames() == null) {
				if (mSession.getCreditCardVersion() > -1) {
					mSession.setCreditCardVersion(-1);
					requestData();
				} else {
					showHint(getString(0x7f090105), true);
				}
			} else {
				showListView();
			}

			return;
		}

		switch (i) {
		case 21: // '\025'
			removeDialog(0);
			showDialog(14);
			break;

		case 23: // '\027'
			removeDialog(0);
			if (!isFinishing())
				showDialog(15);
			break;

		case 25: // '\031'
			if (j == 224) {
				if (!isOutTime()) {
					requestQuery();
				} else {
					removeDialog(0);
					if (!isFinishing() && !mIsOnPause)
						showDialog(13);
				}
			} else if (j == 221) {
				removeDialog(0);
				if (!isFinishing() && !mIsOnPause)
					showDialog(14);
			} else if (j == 223) {
				removeDialog(0);
				if (!isFinishing() && !mIsOnPause)
					showDialog(20);
			} else if (j == 220) {
				removeDialog(0);
				if (!isFinishing() && !mIsOnPause)
					showDialog(18);
			} else {
				removeDialog(0);
				if (!isFinishing() && !mIsOnPause)
					showDialog(12);
			}
			break;

		case 24: // '\030'
			showHint(getString(0x7f090105), true);
			break;

		case 32: // ' '
			removeDialog(0);
			showDialog(14);
			break;

		case 31: // '\037'
			removeDialog(0);
			showDialog(14);
			break;
		}
	}

	protected void onPause() {
		super.onPause();
		this.mIsOnPause = true;
	}

	protected void onPrepareDialog(int paramInt, Dialog paramDialog) {
		super.onPrepareDialog(paramInt, paramDialog);
		if (paramDialog.isShowing())
			paramDialog.dismiss();
	}

	public void onProgressDialogCancel(int paramInt) {
	}

	protected void onResume() {
		super.onResume();
		this.mIsOnPause = false;
	}

	public void onSuccess(int paramInt, Object paramObject) {
		// switch (paramInt)
		// {
		// case 22:
		// case 26:
		// case 27:
		// case 28:
		// case 29:
		// case 30:
		// default:
		// case 23:
		// case 25:
		// case 24:
		// case 21:
		// case 31:
		// case 32:
		// }
		// while (true)
		// {
		// return;
		// this.mOrderID = ((String)paramObject);
		// this.lastTime = System.currentTimeMillis();
		// requestQuery();
		// continue;
		// removeDialog(0);
		// if (!isFinishing())
		// {
		// showDialog(9);
		// continue;
		// this.mCardVerifications = ((CardsVerifications)paramObject);
		// if (this.mCardVerifications == null)
		// {
		// showHint(getString(2131296517), true);
		// }
		// else
		// {
		// this.mSession.setCreditCardVersion(this.mCardVerifications.version);
		// DBUtils.updataCardsVerification(this, this.mCardVerifications.cards);
		// showListView();
		// continue;
		// removeDialog(0);
		// int j = Integer.parseInt((String)paramObject);
		// getIntent().putExtra("balance", j);
		// TextView localTextView = (TextView)findViewById(2131492994);
		// Object[] arrayOfObject = new Object[2];
		// arrayOfObject[0] = this.mSession.getUserName();
		// arrayOfObject[1] = Integer.valueOf(j);
		// localTextView.setText(getString(2131296525, arrayOfObject));
		// continue;
		// try
		// {
		// JSONObject localJSONObject2 = (JSONObject)paramObject;
		// if (1 == localJSONObject2.getInt("resultCode"))
		// {
		// String str = localJSONObject2.getString("alipayParam");
		// this.mOrderID = localJSONObject2.getString("orderNo");
		// if (new MobileSecurePayer().pay(str, this.mHandler, 1, this))
		// continue;
		// removeDialog(0);
		// showDialog(14);
		// }
		// }
		// catch (JSONException localJSONException2)
		// {
		// try
		// {
		// removeDialog(0);
		// showDialog(14);
		// }
		// catch (WindowManager.BadTokenException localBadTokenException4)
		// {
		// }
		// continue;
		// removeDialog(0);
		// showDialog(14);
		// }
		// catch (WindowManager.BadTokenException localBadTokenException3)
		// {
		// }
		// continue;
		// JSONObject localJSONObject1 = (JSONObject)paramObject;
		// try
		// {
		// i = localJSONObject1.getInt("resultCode");
		// if (2 == i)
		// if (isOutTime())
		// {
		// removeDialog(0);
		// showDialog(13);
		// }
		// }
		// catch (JSONException localJSONException1)
		// {
		// int i;
		// try
		// {
		// removeDialog(0);
		// showDialog(14);
		// }
		// catch (WindowManager.BadTokenException localBadTokenException2)
		// {
		// }
		// continue;
		// new Thread(new Runnable()
		// {
		// public void run()
		// {
		// try
		// {
		// Thread.sleep(3000L);
		// MarketAPI.queryAliPayResult(PayMainActivity.this,
		// PayMainActivity.this, PayMainActivity.this.mOrderID);
		// return;
		// }
		// catch (InterruptedException localInterruptedException)
		// {
		// while (true)
		// localInterruptedException.printStackTrace();
		// }
		// }
		// }).start();
		// continue;
		// if (1 == i)
		// {
		// removeDialog(0);
		// showDialog(9);
		// }
		// else
		// {
		// removeDialog(0);
		// showDialog(14);
		// }
		// }
		// catch (WindowManager.BadTokenException localBadTokenException1)
		// {
		// }
		// }
		// }
		// }
	}

	public void onWarningDialogCancel(int paramInt) {
	}

	public void onWarningDialogOK(int paramInt) {
		switch (paramInt) {
		case 4:
			break;
		default:
			break;
		case 2:
			this.mCardNumberEditText.requestFocus();
			break;
		case 3:
			this.mCardPasswordEditText.requestFocus();
			break;
		case 5:
			requestCharge();
			if (!isFinishing()) {
				showDialog(0);
			}
			break;
		case 6:
		case 7:
		case 8:
		case 10:
		case 11:
			this.mCardNumberEditText.requestFocus();
			break;
		case 9:
			finish();
			break;
		}
	}

	public static class AlixOnCancelListener implements
			DialogInterface.OnCancelListener {
		Activity mcontext;

		public AlixOnCancelListener(Activity paramActivity) {
			this.mcontext = paramActivity;
		}

		public void onCancel(DialogInterface paramDialogInterface) {
			this.mcontext.onKeyDown(4, null);
		}
	}
}