package com.chinamworld.bocmbci.biz.prms.trade;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.chinamworld.bocmbci.R;
import com.chinamworld.bocmbci.bii.BiiResponse;
import com.chinamworld.bocmbci.bii.BiiResponseBody;
import com.chinamworld.bocmbci.bii.constant.Prms;
import com.chinamworld.bocmbci.biz.prms.PrmsBaseActivity;
import com.chinamworld.bocmbci.biz.prms.control.PrmsControl;
import com.chinamworld.bocmbci.constant.ConstantGloble;
import com.chinamworld.bocmbci.constant.LocalData;
import com.chinamworld.bocmbci.http.engine.BaseHttpEngine;
import com.chinamworld.bocmbci.log.LogGloble;
import com.chinamworld.bocmbci.utils.StepTitleUtils;
import com.chinamworld.bocmbci.utils.StringUtil;

/**
 * 贵金属买卖信息确认页面
 * 
 * @author xyl
 * 
 */
public class PrmsBuyConfirmActivity extends PrmsBaseActivity {
	private static final String TAG = "PrmsBuyConfirmActivity";
	/**
	 * 交易账户
	 */
	private TextView accountNumTextView;
	/**
	 * 交易币种
	 */
	private TextView tradecurrencyTextView;
	/**
	 * 交易种类
	 */
	private TextView tradestyleTextView;
	/**
	 * 买入数量
	 */
	private TextView tradenumTextView;
	private TextView tradenumTextView1;
	private TextView tradenumUnitTv;
	/**
	 * 交易方式
	 */
	private TextView trademethodTextView;
	/**
	 * 限价即时
	 */
	private TextView limitPriceTv, losPriceTv, winPriceTv, entrustDateTv;
	private TextView limitPriceUnitTv, losPriceUnitTv, winPriceUnitTv;
	/** 当前买入价 */
	private TextView currentPriceUnitTv;
	private TextView currentPriceTv;
	private TextView currentPriceFlagTv;

	private TextView accBalanceTv;
	private TextView accBalanceUtilTv;
	/**
	 * 上一步按钮
	 */
	private Button lastBtn;
	/**
	 * 确定按钮
	 */
	private Button okBtn;

	/**
	 * 交易账户
	 */
	private String accountNumStr;
	/**
	 * 交易币种
	 */
	private String tradeCurrencyStr;
	/**
	 * 钞汇标志
	 */
	private String cashRemitStr;
	/**
	 * 交易种类 S G ...
	 */
	private String tradeStyleStr;
	/**
	 * 买入数量
	 */
	private String tradeNumStr;
	/**
	 * 交易方式
	 */
	private String tradeMethodStr;
	/**
	 * 确认后交易还是直接交易
	 */
	private String direct = "0";
	/**
	 * 交易数量BigDecimal
	 */
	private BigDecimal tradeNum;
	/**
	 * 限制价格 BigDecimal 格式
	 */
	private BigDecimal limitPrice;
	private View limitPriceView, winPriceView, losPriceView, entrustDateView,prms_trade_confirm_runlostlay;
	/**
	 * 交易序号
	 */
	private String transactionIdStr;
	private String exchangeRateStr;
	private String baseRateStr;
	/**
	 * 交易账户
	 */
	private String tradeAccNumStr;
	/**
	 * 即时交易的价格
	 */
	private String marketPrice;
	/**
	 * 防重机制
	 */
	private String tokenId;
	private String tradePriceStr;
	private String accBalanceStr;
	/** 委托日期,委托时间,止损价格,获利价格 */
	private String endDate, endHour, losPrice, winPrice;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
		initDate();
	}

	@Override
	public void requestCommConversationIdCallBack(Object resultObj) {
		super.requestCommConversationIdCallBack(resultObj);
		requestPSNGetTokenId();
	}

	@Override
	public void requestPSNGetTokenIdCallback(Object resultObj) {
		super.requestPSNGetTokenIdCallback(resultObj);
		BiiResponse biiResponse = (BiiResponse) resultObj;
		List<BiiResponseBody> biiResponseBodys = biiResponse.getResponse();
		BiiResponseBody biiResponseBody = biiResponseBodys.get(0);
		tokenId = (String) biiResponseBody.getResult();
		switch (PrmsControl.tradeMethodSwitch(tradeMethodStr)) {
		case PrmsControl.PRMS_TRADEMETHOD_NOW:// 市价和限价即时
		case PrmsControl.PRMS_TRADEMETHOD_LIMIT:
			prmsTradeConfirm(tradeMethodStr,
					ConstantGloble.PRMS_TRADE_TRANSFLAG_BUY, tradeNum,
					tradeCurrencyStr, cashRemitStr, tradeStyleStr);
			break;
		case PrmsControl.PRMS_TRADEMETHOD_WIN:
		case PrmsControl.PRMS_TRADEMETHOD_LOSE:
		case PrmsControl.PRMS_TRADEMETHOD_ONEINWTO:
			prmsTradeResultEntrust(ConstantGloble.PRMS_TRADE_TRANSFLAG_BUY,
					winPrice, losPrice, String.valueOf(tradeNum),
					tradeCurrencyStr, cashRemitStr, tradeStyleStr, direct,
					tokenId, tradeMethodStr, endDate, endHour);
			break;
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public void prmsTradeConfirmCallBack(Object resultObj) {
		super.prmsTradeConfirmCallBack(resultObj);
		BiiResponse biiResponse = (BiiResponse) resultObj;
		List<BiiResponseBody> biiResponseBodys = biiResponse.getResponse();
		BiiResponseBody biiResponseBody = biiResponseBodys.get(0);
		if (StringUtil.isNullOrEmpty(biiResponseBody.getResult())) {
			BaseHttpEngine.dissMissProgressDialog();
			return;
		}
		Map<String, Object> map = (Map<String, Object>) biiResponseBody
				.getResult();
		marketPrice = String.valueOf(map.get(Prms.PRMS_TTADE_CONFIRM_RATE));

		switch (PrmsControl.tradeMethodSwitch(tradeMethodStr)) {// 市价即时交易
		case PrmsControl.PRMS_TRADEMETHOD_NOW:
			prmsTradeResult08(ConstantGloble.PRMS_TRADE_TRANSFLAG_BUY,
					String.valueOf(tradeNum), tradeCurrencyStr, cashRemitStr,
					tradeStyleStr, direct, marketPrice, tokenId);
			break;
		case PrmsControl.PRMS_TRADEMETHOD_LIMIT:
			prmsTradeResultLimit(ConstantGloble.PRMS_TRADE_TRANSFLAG_BUY,
					tradeNum.toString(), limitPrice.toString(),
					tradeCurrencyStr, cashRemitStr, tradeStyleStr, direct,
					marketPrice, tokenId);
			break;
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public void prmsTradeResultLimitCallBack(Object resultObj) {
		super.prmsTradeResultLimitCallBack(resultObj);
		BiiResponse biiResponse = (BiiResponse) resultObj;
		List<BiiResponseBody> biiResponseBodys = biiResponse.getResponse();
		BiiResponseBody biiResponseBody = biiResponseBodys.get(0);
		Map<String, String> map = (Map<String, String>) biiResponseBody
				.getResult();
		if (StringUtil.isNullOrEmpty(biiResponseBody.getResult())) {
			BaseHttpEngine.dissMissProgressDialog();
			return;
		}
		BaseHttpEngine.dissMissProgressDialog();
		// 委托序号
		transactionIdStr = map.get(Prms.PRMS_TTADE_RESOUT_TRANSACTIONID);
		baseRateStr = map.get(Prms.PRMS_TTADE_RESOUT_BASERATE);
		exchangeRateStr = map.get(Prms.PRMS_TTADE_RESOUT_EXCHANGERATE);
		tradeAccNumStr = map.get(Prms.PRMS_TTADE_RESOUT_ACCOUNTNUM);
		String buyAmount = map.get(Prms.PRMS_QUSH_SUBMIT_BUYAMOUNT);
		String buyAmountUnit = map.get(Prms.PRMS_QUERY_DEAL_BUYCURRENCYCODE);
		String sellAmount = map.get(Prms.PRMS_QUSH_SUBMIT_SELLAMOUNT);
		String sellAmountUnit = map.get(Prms.PRMS_QUERY_DEAL_SELLCURRENCYCODE);
		Intent intent = getIntent();
		intent.putExtra(Prms.PRMS_TTADE_RESOUT_TRANSACTIONID, transactionIdStr);
		intent.putExtra(Prms.PRMS_ACCOUNTNUM, tradeAccNumStr);
		intent.putExtra(Prms.PRMS_TTADE_RESOUT_BASERATE, baseRateStr);
		intent.putExtra(Prms.PRMS_TTADE_RESOUT_EXCHANGERATE, exchangeRateStr);
		intent.putExtra(Prms.PRMS_QUSH_SUBMIT_BUYAMOUNT, buyAmount);//交易金额
		intent.putExtra(Prms.PRMS_QUERY_DEAL_BUYCURRENCYCODE, buyAmountUnit);//交易货币
		intent.putExtra(Prms.PRMS_QUSH_SUBMIT_SELLAMOUNT, sellAmount);
		intent.putExtra(Prms.PRMS_QUERY_DEAL_SELLCURRENCYCODE, sellAmountUnit);
		intent.setClass(PrmsBuyConfirmActivity.this,
				PrmsBuySuccessActivity.class);
		startActivity(intent);

	}

	@Override
	public void prmsTradeResultEntrustCallBack(Object resultObj) {
		super.prmsTradeResultEntrustCallBack(resultObj);
		BiiResponse biiResponse = (BiiResponse) resultObj;
		List<BiiResponseBody> biiResponseBodys = biiResponse.getResponse();
		BiiResponseBody biiResponseBody = biiResponseBodys.get(0);
		Map<String, String> map = (Map<String, String>) biiResponseBody
				.getResult();
		if (StringUtil.isNullOrEmpty(biiResponseBody.getResult())) {
			BaseHttpEngine.dissMissProgressDialog();
			return;
		}
		BaseHttpEngine.dissMissProgressDialog();
		// 交易序号
		transactionIdStr = map.get(Prms.PRMS_TTADE_RESOUT_TRANSACTIONID);
		baseRateStr = map.get(Prms.PRMS_TTADE_RESOUT_BASERATE);
		exchangeRateStr = map.get(Prms.PRMS_TTADE_RESOUT_EXCHANGERATE);
		tradeAccNumStr = map.get(Prms.PRMS_TTADE_RESOUT_ACCOUNTNUM);
		//委托序号
		String consignNumStr = map.get(Prms.PRMS_TTADE_RESOUT_CONSIGNNUM);
		String buyAmount = map.get(Prms.PRMS_QUSH_SUBMIT_BUYAMOUNT);
		String buyAmountUnit = map.get(Prms.PRMS_QUERY_DEAL_BUYCURRENCYCODE);
		String sellAmount = map.get(Prms.PRMS_QUSH_SUBMIT_SELLAMOUNT);
		String sellAmountUnit = map.get(Prms.PRMS_QUERY_DEAL_SELLCURRENCYCODE);
		Intent intent = getIntent();
		intent.putExtra(Prms.PRMS_TTADE_RESOUT_TRANSACTIONID, transactionIdStr);
		intent.putExtra(Prms.PRMS_TTADE_RESOUT_CONSIGNNUM, consignNumStr);
		intent.putExtra(Prms.PRMS_TTADE_RESOUT_BASERATE, baseRateStr);
		intent.putExtra(Prms.PRMS_TTADE_RESOUT_EXCHANGERATE, exchangeRateStr);
		intent.putExtra(Prms.PRMS_TTADE_RESOUT_BASERATE, baseRateStr);
		intent.putExtra(Prms.PRMS_QUSH_SUBMIT_BUYAMOUNT, buyAmount);//交易金额
		intent.putExtra(Prms.PRMS_QUERY_DEAL_BUYCURRENCYCODE, buyAmountUnit);//交易货币
		intent.putExtra(Prms.PRMS_QUSH_SUBMIT_SELLAMOUNT, sellAmount);
		intent.putExtra(Prms.PRMS_QUERY_DEAL_SELLCURRENCYCODE, sellAmountUnit);
		intent.setClass(PrmsBuyConfirmActivity.this,
				PrmsBuySuccessActivity.class);
		startActivity(intent);

	}

	@SuppressWarnings("unchecked")
	@Override
	public void prmsTradeResult08CallBack(Object resultObj) {
		BiiResponse biiResponse = (BiiResponse) resultObj;
		List<BiiResponseBody> biiResponseBodys = biiResponse.getResponse();
		BiiResponseBody biiResponseBody = biiResponseBodys.get(0);
		if (StringUtil.isNullOrEmpty(biiResponseBody.getResult())) {
			BaseHttpEngine.dissMissProgressDialog();
			return;
		}
		BaseHttpEngine.dissMissProgressDialog();
		Map<String, String> map = (Map<String, String>) biiResponseBody
				.getResult();
		transactionIdStr = map.get(Prms.PRMS_TTADE_RESOUT_TRANSACTIONID);
		tradeAccNumStr = map.get(Prms.PRMS_TTADE_RESOUT_ACCOUNTNUM);
		baseRateStr = map.get(Prms.PRMS_TTADE_RESOUT_BASERATE);
		exchangeRateStr = map.get(Prms.PRMS_TTADE_RESOUT_EXCHANGERATE);
		String buyAmount = map.get(Prms.PRMS_QUSH_SUBMIT_BUYAMOUNT);
		String buyAmountUnit = map.get(Prms.PRMS_QUERY_DEAL_BUYCURRENCYCODE);
		String sellAmount = map.get(Prms.PRMS_QUSH_SUBMIT_SELLAMOUNT);
		String sellAmountUnit = map.get(Prms.PRMS_QUERY_DEAL_SELLCURRENCYCODE);
		Intent intent = getIntent();
		intent.putExtra(Prms.PRMS_TTADE_RESOUT_TRANSACTIONID, transactionIdStr);
		intent.putExtra(Prms.PRMS_ACCOUNTNUM, tradeAccNumStr);
		intent.putExtra(Prms.PRMS_TTADE_RESOUT_BASERATE, baseRateStr);
		intent.putExtra(Prms.PRMS_TTADE_RESOUT_EXCHANGERATE, exchangeRateStr);
		intent.putExtra(Prms.PRMS_QUSH_SUBMIT_BUYAMOUNT, buyAmount);//交易金额
		intent.putExtra(Prms.PRMS_QUERY_DEAL_BUYCURRENCYCODE, buyAmountUnit);//交易货币
		intent.putExtra(Prms.PRMS_QUSH_SUBMIT_SELLAMOUNT, sellAmount);
		intent.putExtra(Prms.PRMS_QUERY_DEAL_SELLCURRENCYCODE, sellAmountUnit);
		intent.setClass(PrmsBuyConfirmActivity.this,
				PrmsBuySuccessActivity.class);
		startActivity(intent);
	}

	/**
	 * 初始化布局
	 * 
	 * @Author xyl
	 */
	private void init() {
		View v = mainInflater.inflate(R.layout.prms_trade_confirm, null);
		tabcontent.addView(v);
		accountNumTextView = (TextView) findViewById(R.id.prms_trade_confirm_accountnum);
		tradecurrencyTextView = (TextView) findViewById(R.id.prms_trade_confirm_tradecurrency);
		tradestyleTextView = (TextView) findViewById(R.id.prms_trade_confirm_tradestyle);
		tradenumTextView = (TextView) findViewById(R.id.prms_trade_confirm_tradenum);
		tradenumTextView1 = (TextView) findViewById(R.id.prms_trade_confirm_tradenum1);
		tradenumUnitTv = (TextView) findViewById(R.id.prms_trade_confirm_tradenum_unit);
		trademethodTextView = (TextView) findViewById(R.id.prms_trade_confirm_trademethod);

		limitPriceTv = (TextView) findViewById(R.id.prms_trade_confirm_limitprice);
		losPriceTv = (TextView) findViewById(R.id.prms_trade_confirm_losprice);
		winPriceTv = (TextView) findViewById(R.id.prms_trade_confirm_winprice);
		entrustDateTv = (TextView) findViewById(R.id.prms_trade_confirm_entrustDate_tv);

		winPriceUnitTv = (TextView) findViewById(R.id.prms_trade_confirm_winprice_unit);
		losPriceUnitTv = (TextView) findViewById(R.id.prms_trade_confirm_losprice_unit);
		limitPriceUnitTv = (TextView) findViewById(R.id.prms_trade_confirm_limitprice_unit);

		limitPriceView = findViewById(R.id.prms_trade_confirm_limitlay);
		losPriceView = findViewById(R.id.prms_trade_confirm_entrust_losPrice_view);
		winPriceView = findViewById(R.id.prms_trade_confirm_entrust_winPrice_view);
		entrustDateView = findViewById(R.id.prms_trade_confirm_entrust_date_view);

		currentPriceTv = (TextView) findViewById(R.id.prms_trade_buy_buyRate);
		currentPriceUnitTv = (TextView) findViewById(R.id.prms_trade_buy_buyRate_unit);
		currentPriceFlagTv = (TextView) findViewById(R.id.prms_trade_buy_buyRate1);
		accBalanceTv = (TextView) findViewById(R.id.prms_trade_buy_accbalance);
		accBalanceUtilTv = (TextView) findViewById(R.id.prms_trade_buy_accbalance_unit);
		lastBtn = (Button) findViewById(R.id.prms_trade_confirm_last);
		okBtn = (Button) findViewById(R.id.prms_trade_confirm_ok);
		
		prms_trade_confirm_runlostlay = findViewById(R.id.prms_trade_confirm_runlostlay);
//		prms_trade_confirm_runlostprice = (TextView) findViewById(R.id.prms_trade_confirm_runlostprice);
		setTitle(getResources().getString(R.string.prms_title_trade_buy));
		StepTitleUtils.getInstance().initTitldStep(this,
				prmsControl.getStepsForPrmsTrade());
		StepTitleUtils.getInstance().setTitleStep(2);
		lastBtn.setOnClickListener(this);
		okBtn.setOnClickListener(this);
		initRightBtnForMain();

	}

	/**
	 * 初始化数据
	 * 
	 * @Author xyl
	 */
	private void initDate() {
		Intent intent = getIntent();
		Bundle extra = intent.getExtras();
		tradeMethodStr = (String) extra.get(Prms.PRMS_TRADEMETHOD);
		accountNumStr = prmsControl.accNum;
		cashRemitStr = (String) extra.get(Prms.PRMS_CASHREMIT);
		tradeCurrencyStr = (String) extra.get(Prms.PRMS_CURRENCYCODE);
		tradeStyleStr = (String) extra.get(Prms.PRMS_TRADETYPE);
		tradeNumStr = (String) extra.get(Prms.PRMS_TRADENUM);
		tradePriceStr = (String) extra.get(Prms.PRMS_TRADEPRICE);
		accBalanceStr = (String) extra.get(Prms.PRMS_BALACNE);
		setViewByTransMethod(tradeMethodStr);
		//根据交易方式 
		switch (PrmsControl.tradeMethodSwitch(tradeMethodStr)) {
		case PrmsControl.PRMS_TRADEMETHOD_NOW:

			break;
		case PrmsControl.PRMS_TRADEMETHOD_LIMIT:
			String limitPriceTemp = (String) extra.get(Prms.PRMS_LIMITPRICE);
			limitPrice = new BigDecimal(limitPriceTemp.toCharArray());
			limitPriceTv.setText(PrmsControl.parsePriceByCurrency(limitPriceTemp,
					prmsControl.getCurrencyCode(
							tradeCurrencyStr, tradeStyleStr)));
			limitPriceUnitTv.setText(LocalData.prmsTradePricUnitMaptoChi
					.get(tradeCurrencyStr));
			break;
		case PrmsControl.PRMS_TRADEMETHOD_WIN:
			winPrice = (String) extra.get(Prms.PRMS_WINPRICE);
			endDate = (String) extra.get(Prms.PRMS_ENDDATE);
			endHour = (String) extra.get(Prms.PRMS_ENDHOUR);
			winPriceTv.setText(PrmsControl.parsePriceByCurrency(winPrice,
					prmsControl.getCurrencyCode(
							tradeCurrencyStr, tradeStyleStr)));
			winPriceUnitTv.setText(LocalData.prmsTradePricUnitMaptoChi
					.get(tradeCurrencyStr));
			setEnTrustDate(endDate, endHour);
			break;
		case PrmsControl.PRMS_TRADEMETHOD_LOSE:
			losPrice = (String) extra.get(Prms.PRMS_LOSEPRICE);
			endDate = (String) extra.get(Prms.PRMS_ENDDATE);
			endHour = (String) extra.get(Prms.PRMS_ENDHOUR);
			losPriceTv.setText(PrmsControl.parsePriceByCurrency(losPrice,
					prmsControl.getCurrencyCode(
							tradeCurrencyStr, tradeStyleStr)));
			losPriceUnitTv.setText(LocalData.prmsTradePricUnitMaptoChi
					.get(tradeCurrencyStr));
			setEnTrustDate(endDate, endHour);
			break;
		case PrmsControl.PRMS_TRADEMETHOD_ONEINWTO:
			winPrice = (String) extra.get(Prms.PRMS_WINPRICE);
			losPrice = (String) extra.get(Prms.PRMS_LOSEPRICE);
			endDate = (String) extra.get(Prms.PRMS_ENDDATE);
			endHour = (String) extra.get(Prms.PRMS_ENDHOUR);
			winPriceTv.setText(PrmsControl.parsePriceByCurrency(winPrice,
					prmsControl.getCurrencyCode(
							tradeCurrencyStr, tradeStyleStr)));
			winPriceUnitTv.setText(LocalData.prmsTradePricUnitMaptoChi
					.get(tradeCurrencyStr));
			losPriceTv.setText(PrmsControl.parsePriceByCurrency(losPrice,
					prmsControl.getCurrencyCode(
							tradeCurrencyStr, tradeStyleStr)));
			losPriceUnitTv.setText(LocalData.prmsTradePricUnitMaptoChi
					.get(tradeCurrencyStr));
			setEnTrustDate(endDate, endHour);
			break;

		}
		accountNumTextView
				.setText(StringUtil.getForSixForString(accountNumStr));

		if (cashRemitStr.equals(ConstantGloble.CASHRMIT_RMB)) {
			tradecurrencyTextView.setText(LocalData.prmsTradeStyleMaptoChi
					.get(tradeCurrencyStr));
		} else {
			tradecurrencyTextView.setText(LocalData.prmsTradeStyleMaptoChi
					.get(tradeCurrencyStr)
					+ " "
					+ LocalData.CurrencyCashremit.get(cashRemitStr));
		}

		currentPriceTv.setText(tradePriceStr);
		currentPriceUnitTv.setText(LocalData.prmsAccUnitMaptoChi
				.get(tradeCurrencyStr)
				+ LocalData.prmsTradePricUnitMaptoChi.get(tradeCurrencyStr));
		tradestyleTextView.setText(LocalData.prmsStyleMaptoChi
				.get(tradeStyleStr));
//		tradenumTextView.setText(PrmsControl.parseStringPatternTradeNum(
//				prmsControl.getCurrencyCode(tradeCurrencyStr, tradeStyleStr),
//				tradeNumStr));
		tradenumTextView.setText(tradeNumStr);
		tradenumUnitTv.setText(LocalData.prmsUnitMaptoChi1
				.get(tradeCurrencyStr));
		trademethodTextView.setText(LocalData.prmsTradeMethodMaptoChi
				.get(tradeMethodStr));

		try {
			tradeNum = new BigDecimal(tradeNumStr.toCharArray());
		} catch (Exception e) {
			LogGloble.exceptionPrint(e);
		}
		currentPriceFlagTv.setText(getString(R.string.prms_trade_buy_price));
		tradenumTextView1.setText(getString(R.string.prms_trade_buy_num));
		accBalanceTv.setText(StringUtil.parseStringPattern(accBalanceStr, 2));
	}
	/**
	 * 设置委托时间
	 */
	private void setEnTrustDate(String endDate,String endHour){
		entrustDateTv.setText(endDate + " " + endHour+getString(R.string.forex_rate_currency_hour));
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ib_back:// 返回按钮
			finish();
			break;
		case R.id.prms_trade_confirm_ok:
			requestCommConversationId();
			BaseHttpEngine.showProgressDialog();
			break;
		case R.id.prms_trade_confirm_last:
			finish();
			break;
		default:
			break;
		}
	}

	/**
	 * 
	 * @param transMethod
	 */
	private void setViewByTransMethod(String transMethod) {
		switch (PrmsControl.tradeMethodSwitch(transMethod)) {
		case PrmsControl.PRMS_TRADEMETHOD_LIMIT:
			losPriceView.setVisibility(View.GONE);
			winPriceView.setVisibility(View.GONE);
			limitPriceView.setVisibility(View.VISIBLE);
			entrustDateView.setVisibility(View.GONE);
			prms_trade_confirm_runlostlay.setVisibility(View.GONE);
			break;
		case PrmsControl.PRMS_TRADEMETHOD_LOSE:
			losPriceView.setVisibility(View.VISIBLE);
			winPriceView.setVisibility(View.GONE);
			limitPriceView.setVisibility(View.GONE);
			entrustDateView.setVisibility(View.VISIBLE);
			prms_trade_confirm_runlostlay.setVisibility(View.GONE);
			break;
		case PrmsControl.PRMS_TRADEMETHOD_WIN:
			losPriceView.setVisibility(View.GONE);
			winPriceView.setVisibility(View.VISIBLE);
			limitPriceView.setVisibility(View.GONE);
			entrustDateView.setVisibility(View.VISIBLE);
			prms_trade_confirm_runlostlay.setVisibility(View.GONE);
			break;
		case PrmsControl.PRMS_TRADEMETHOD_ONEINWTO:
			losPriceView.setVisibility(View.VISIBLE);
			winPriceView.setVisibility(View.VISIBLE);
			limitPriceView.setVisibility(View.GONE);
			entrustDateView.setVisibility(View.VISIBLE);
			prms_trade_confirm_runlostlay.setVisibility(View.GONE);
			break;
		case PrmsControl.PRMS_TRADEMETHOD_NOW:
			losPriceView.setVisibility(View.GONE);
			winPriceView.setVisibility(View.GONE);
			limitPriceView.setVisibility(View.GONE);
			entrustDateView.setVisibility(View.GONE);
			prms_trade_confirm_runlostlay.setVisibility(View.GONE);
			break;
		}
	}

}
