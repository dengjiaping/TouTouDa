package com.quanliren.quan_two.activity.shop;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.a.dd.CircularProgressButton;
import com.loopj.android.http.RequestParams;
import com.quanliren.quan_two.util.http.JsonHttpResponseHandler;
import com.alipay.Keys;
import com.alipay.Result;
import com.alipay.Rsa;
import com.alipay.android.app.sdk.AliPay;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.quanliren.quan_two.activity.R;
import com.quanliren.quan_two.activity.base.BaseActivity;
import com.quanliren.quan_two.activity.seting.HtmlActivity_;
import com.quanliren.quan_two.adapter.ShopAdapter;
import com.quanliren.quan_two.adapter.ShopAdapter.IBuyListener;
import com.quanliren.quan_two.bean.OrderBean;
import com.quanliren.quan_two.bean.ShopBean;
import com.quanliren.quan_two.bean.User;
import com.quanliren.quan_two.bean.UserTable;
import com.quanliren.quan_two.fragment.SetingMoreFragment;
import com.quanliren.quan_two.util.URL;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@EActivity(R.layout.vip_detail)
public class ShopVipDetail extends BaseActivity implements IBuyListener{
	private static final int RQF_PAY = 1;
	private static final int RQF_LOGIN = 2;

	@ViewById
	ImageView banner;
	@ViewById
	ListView listview;
	ShopAdapter adapter;
	List<ShopBean> list = new ArrayList<ShopBean>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getSupportActionBar().setTitle("商城");
	}

	@AfterViews
	void initView() {
		
		list.add(new ShopBean(-1,0, "会员购买", "￥0", 0));
		list.add(new ShopBean(0,R.drawable.shop_icon_month_vip1, "1个月会员", "￥12.00", 1));
		list.add(new ShopBean(1,R.drawable.shop_icon_month_vip2, "3个月会员", "￥30.00", 2));
		list.add(new ShopBean(2,R.drawable.shop_icon_year_vip, "12个月年费会员", "￥88.00", 3));
		list.add(new ShopBean(-1,0, "靓点购买", "￥0", 0));
		list.add(new ShopBean(3,R.drawable.shop_icon_5, "5个靓点", "￥50.00", 1));
		list.add(new ShopBean(4,R.drawable.shop_icon_10, "10个靓点", "￥98.00", 2));
		list.add(new ShopBean(5,R.drawable.shop_icon_20, "20个靓点", "￥198.00", 2));
		list.add(new ShopBean(6,R.drawable.shop_icon_50, "50个靓点", "￥488.00", 2));
		list.add(new ShopBean(7,R.drawable.shop_icon_100, "100个靓点", "￥998.00", 3));
		
		adapter=new ShopAdapter(this, list,this);
		listview.setAdapter(adapter);
		
		
		try {
			Bitmap loadedImage= ((BitmapDrawable)banner.getDrawable()).getBitmap();
			int swidth=getResources().getDisplayMetrics().widthPixels;
			float widthScale=(float)swidth/(float)loadedImage.getWidth();
			int height=(int)(widthScale*loadedImage.getHeight());
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					swidth, height);
			banner.setLayoutParams(lp);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@Click
	void banner(){
		HtmlActivity_.intent(this).title("会员介绍").url("file:///android_asset/vip.html").start();
	}


	public void startBao(final ShopBean sb) {
        RequestParams ap=getAjaxParams();
        ap.put("gnumber", sb.getId());
		ac.finalHttp.post(URL.GETALIPAY, ap,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(JSONObject jo) {
						try {
							int status = jo.getInt(URL.STATUS);
							switch (status) {
							case 0:
								doSuccess();
								buy(jo.toString(),sb.getTitle());
								break;
							default:
								doFail();
								showFailInfo(jo);
								break;
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onFailure() {
						doFail();
						showIntentErrorToast();
					}
				});
	}
	@UiThread(delay = 500)
	void doSuccess() {
		if(mProgress!=null){
			mProgress.setProgress(0);
			mProgress=null;
		}
	}
	@UiThread(delay = 500)
	void doFail() {
		mProgress.setProgress(-1);
		doRstoref();
	}

	@UiThread(delay = 1500)
	void doRstoref() {
		mProgress.setProgress(0);
	}
	public void buy(String t,String name) {
		try {
			String info = getNewOrderInfo(t,name);
			String sign = Rsa.sign(info, Keys.PRIVATE);
			sign = URLEncoder.encode(sign);
			info += "&sign=\"" + sign + "\"&" + getSignType();

			final String orderInfo = info;
			new Thread() {
				public void run() {
					AliPay alipay = new AliPay(ShopVipDetail.this, mHandler);
					// 设置为沙箱模式，不设置默认为线上环境
					// alipay.setSandBox(true);

					String result = alipay.pay(orderInfo);

					Message msg = new Message();
					msg.what = RQF_PAY;
					msg.obj = result;
					mHandler.sendMessage(msg);
				}
			}.start();

		} catch (Exception ex) {
			ex.printStackTrace();
			Toast.makeText(ShopVipDetail.this, "remote_call_failed",
					Toast.LENGTH_SHORT).show();
		}
	}

	private String getSignType() {
		return "sign_type=\"RSA\"";
	}

	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			Result result = new Result((String) msg.obj);
			switch (msg.what) {
			case RQF_PAY:
				if (result.getResultStatus().equals("9000")) {
					showCustomToast("购买成功");
					User user = getHelper().getUserInfo();
					user.setIsvip(1);
					UserTable ut = new UserTable(user);
					try {
						userTableDao.delete(ut);
						userTableDao.create(ut);
					} catch (SQLException e) {
						e.printStackTrace();
					}

					Intent i = new Intent(SetingMoreFragment.UPDATE_USERINFO);
					sendBroadcast(i);

				} else if (result.getResultStatus().equals("6001")) {
					// showCustomToast("取消购买");
				} else {
					showCustomToast("购买失败");
				}
				break;
			case RQF_LOGIN: {
				Toast.makeText(ShopVipDetail.this, result.getResult(),
						Toast.LENGTH_SHORT).show();
			}
				break;
			default:
				break;
			}
		};
	};

	OrderBean ob=null;
	
	private String getNewOrderInfo(String t,String name) {
		String url = "";
		try {
			JSONObject jo = new JSONObject(t);
			ob=new Gson().fromJson(jo.getString(URL.RESPONSE), new TypeToken<OrderBean>(){}.getType());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		StringBuilder sb = new StringBuilder();
		sb.append("partner=\"");
		sb.append(Keys.DEFAULT_PARTNER);
		sb.append("\"&out_trade_no=\"");

		sb.append(ob.getOrder_no());

		sb.append("\"&subject=\"");
		sb.append(name);
		sb.append("\"&body=\"");
		sb.append(name);
		sb.append("\"&total_fee=\"");
		sb.append(ob.getPrice());
		sb.append("\"&notify_url=\"");

		// 网址需要做URL编码
		sb.append(URLEncoder.encode(ob.getNotify_url()));

		sb.append("\"&service=\"mobile.securitypay.pay");
		sb.append("\"&_input_charset=\"UTF-8");
		sb.append("\"&return_url=\"");
		sb.append(URLEncoder.encode("http://m.alipay.com"));
		sb.append("\"&payment_type=\"1");
		sb.append("\"&seller_id=\"");
		sb.append(Keys.DEFAULT_SELLER);

		// 如果show_url值为空，可不传
		// sb.append("\"&show_url=\"");
		sb.append("\"&it_b_pay=\"1m");
		sb.append("\"");

		return new String(sb);
	}

	CircularProgressButton mProgress;
	
	@Override
	public void buyClick(final CircularProgressButton progress) {
		if(getHelper().getUser()==null){
			startLogin();
			return;
		}
		if((this.mProgress!=null&&this.mProgress.getProgress()!=0)){
			return;
		}
		AlertDialog dialog=new AlertDialog.Builder(this).setItems(new String[] { "支付宝安全支付", "储蓄卡支付",
				 "信用卡支付" }, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						ShopBean sb=(ShopBean) progress.getTag();
						switch (which) {
						case 0:
							mProgress=progress;
							if(progress.getProgress()==0){
								progress.setProgress(50);
								startBao(sb);
							}
							break;
						case 1:
							VipCardActivity_.intent(ShopVipDetail.this).sb(sb).channelType("DEBIT_EXPRESS").start();
							break;
						case 2:
							VipCardActivity_.intent(ShopVipDetail.this).sb(sb).channelType("OPTIMIZED_MOTO").start();
							break;
						}
					}
				}).create();
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}

}
