package com.quanliren.quan_two.activity.wxapi;


import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

import com.quanliren.quan_two.activity.seting.InviteFriendActivity;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler{
	private IWXAPI api;  
	  
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        api = WXAPIFactory.createWXAPI(this, InviteFriendActivity.WEIXIN_ID, true);  
        api.handleIntent(getIntent(), this);  
  
    }  
  
    @Override  
    public void onReq(BaseReq arg0) {  
  
    }  
  
    @Override  
    public void onResp(BaseResp resp) {  
        String result = "";  
  
        switch (resp.errCode) {  
        case BaseResp.ErrCode.ERR_OK:  
            result = "分享成功";  
            break;  
        case BaseResp.ErrCode.ERR_USER_CANCEL:  
            result = "取消分享";  
            break;  
        case BaseResp.ErrCode.ERR_AUTH_DENIED:  
            result = "分享失败";  
            break;  
        default:  
            result = "未知错误";  
            break;  
        }  
  
        Toast.makeText(this, result, Toast.LENGTH_SHORT).show();  
        
        this.finish();  
    }  

}
