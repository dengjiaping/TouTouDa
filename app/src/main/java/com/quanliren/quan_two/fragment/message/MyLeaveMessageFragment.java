package com.quanliren.quan_two.fragment.message;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import com.a.me.maxwin.view.XXListView;
import com.a.me.maxwin.view.XXListView.IXListViewListener;
import com.a.nineoldandroids.animation.Animator;
import com.a.nineoldandroids.animation.AnimatorListenerAdapter;
import com.a.nineoldandroids.animation.ValueAnimator;
import com.a.nineoldandroids.view.ViewHelper;
import com.a.nineoldandroids.view.ViewPropertyAnimator;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.quanliren.quan_two.activity.PropertiesActivity;
import com.quanliren.quan_two.activity.R;
import com.quanliren.quan_two.activity.user.ChatActivity_;
import com.quanliren.quan_two.adapter.LeaveMessageAdapter;
import com.quanliren.quan_two.bean.ChatListBean;
import com.quanliren.quan_two.bean.DfMessage;
import com.quanliren.quan_two.bean.LoginUser;
import com.quanliren.quan_two.bean.User;
import com.quanliren.quan_two.db.DBHelper;
import com.quanliren.quan_two.fragment.base.MenuFragmentBase;
import com.quanliren.quan_two.fragment.impl.LoaderImpl;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OrmLiteDao;
import org.androidannotations.annotations.Receiver;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@EFragment
public class MyLeaveMessageFragment extends MenuFragmentBase implements IXListViewListener,LoaderImpl{

	public static final String TAG="MyLeaveMessageActivity";
	public static final String REFEREMSGCOUNT="com.quanliren.quan_two.MyLeaveMessageActivity.REFEREMSGCOUNT";
	public static final String ADDMSG="com.quanliren.quan_two.MyLeaveMessageActivity.ADDMSG";
	@ViewById XXListView listview;
	@OrmLiteDao(helper=DBHelper.class,model=ChatListBean.class)
	Dao<ChatListBean, Integer> chatListDao;
	@OrmLiteDao(helper=DBHelper.class,model=DfMessage.class)
	Dao<DfMessage, Integer> messageDao;
	LeaveMessageAdapter adapter;
	int p=0;
	LoginUser user;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		user=getHelper().getUser();
		
	}
	
	public void onDestroy() {
		super.onDestroy();
	};
	
	View view;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(view==null){
			view=inflater.inflate(R.layout.my_leavemessage_list, null);
		}else{
			ViewParent parent=view.getParent();
			if(parent!=null&&parent instanceof ViewGroup){
				((ViewGroup)parent).removeView(view);
			}
		}
		return view;
	}

	public void initAdapter() {
		adapter = new LeaveMessageAdapter(getActivity(), new ArrayList<ChatListBean>());
		adapter.handler=handler;
		listview.setAdapter(adapter);
		listview.setXListViewListener(this);
		listview.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				if(position>0){
					ChatListBean mlb=(ChatListBean) adapter.getItem(position-1);
					Message msg = handler.obtainMessage();
					msg.what = 1;
					msg.obj = mlb;
					msg.sendToTarget();
					return true;
				}
				return false;
			}
		});
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				if(position>0){
					ChatListBean bean = (ChatListBean) adapter.getItem(position-1);
					User user = new User(bean.getFriendid(),bean.getUserlogo(),bean.getNickname());
					ChatActivity_.intent(MyLeaveMessageFragment.this).friend(user).start();
				}
			}
		});
	}

	@Override
	public void onRefresh() {
		findList();
	}
	
	@Background
	void findList(){
		try {
			final List<ChatListBean> list=chatListDao.query(chatListDao.queryBuilder().orderBy("id", false).where().eq("userid", user.getId()).prepare());
			if(list!=null&&list.size()>0){
				for (ChatListBean c : list) {
					QueryBuilder<DfMessage, Integer> qb = messageDao.queryBuilder();
					Where where =qb.where();
					where.and(where.eq("userid", user.getId()), where.eq("receiverUid", user.getId()), where.eq("sendUid", c.getFriendid()),where.eq("isRead", 0));
					c.setMsgCount((int)qb.countOf());
				}
			}
			notifyData(list);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@UiThread
	void notifyData(List<ChatListBean> list){
		if(list!=null&&list.size()>0){
			adapter.setList(list);
		}
		adapter.notifyDataSetChanged();
		listview.stop();
	}

	@Override
	public void onLoadMore() {
	}
	
	
	Handler handler=new Handler(){
		public void dispatchMessage(Message msg) {
			final ChatListBean bean=(ChatListBean) msg.obj;
			switch (msg.what) {
			case 1:
				AlertDialog dialog=new AlertDialog.Builder(getActivity()).setItems(new String[]{"删除"}, new OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							new AlertDialog.Builder(getActivity()).setTitle("提示").setMessage("你确定要删除这条记录吗？").setPositiveButton("确定", new OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									try {
										chatListDao.delete(bean);
										
										DeleteBuilder<DfMessage, Integer> db = messageDao.deleteBuilder();
										final Where<DfMessage, Integer> where= db.where();
										where.eq("userid", user.getId()).and().or(where.eq("sendUid", bean.getFriendid()),where.eq("receiverUid", bean.getFriendid()));
										messageDao.delete(db.prepare());
										
										final int position=adapter.getList().indexOf(bean);
										if(position>-1){
											final View view = listview.getChildAt((position+1)-listview.getFirstVisiblePosition());
											if(view != null){
												ViewPropertyAnimator.animate(view)  
							                    .alpha(0)  
							                    .setDuration(200)  
							                    .setListener(new AnimatorListenerAdapter() {  
							                        @Override  
							                        public void onAnimationEnd(Animator animation) {  
							                        	performDismiss(view,position);  
							                        }  
							                    });  
												
											}else{
												adapter.removeObj(position);
												adapter.notifyDataSetChanged();
											}
										}
										Intent intent = new Intent(PropertiesActivity.PROPERTIESACTIVITY_NEWMESSAGE);
										getActivity().sendBroadcast(intent);
									} catch( Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							}).setNegativeButton("取消", new OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0, int arg1) {
								}
							}).create().show();
							break;
						}
					}
				}).create();
				dialog.setCanceledOnTouchOutside(true);
				dialog.show();
				break;
			}
			super.dispatchMessage(msg);
		};
	};
	
	private void performDismiss(final View dismissView,final int position) {  
        final ViewGroup.LayoutParams lp = dismissView.getLayoutParams();//获取item的布局参数  
        final int originalHeight = dismissView.getHeight();//item的高度  
  
        ValueAnimator animator = ValueAnimator.ofInt(originalHeight, 0).setDuration(200);  
        animator.start();  
  
        animator.addListener(new AnimatorListenerAdapter() {  
            @Override  
            public void onAnimationEnd(Animator animation) {  
            	adapter.removeObj(position);   
                ViewHelper.setAlpha(dismissView, 1f);  
                ViewGroup.LayoutParams lp = dismissView.getLayoutParams();  
                lp.height =  ViewGroup.LayoutParams.WRAP_CONTENT;  
                dismissView.setLayoutParams(lp);  
  
                adapter.notifyDataSetChanged();
            }  
        });  
  
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {  
            @Override  
            public void onAnimationUpdate(ValueAnimator valueAnimator) {  
                //这段代码的效果是ListView删除某item之后，其他的item向上滑动的效果  
                lp.height = (Integer) valueAnimator.getAnimatedValue();  
                dismissView.setLayoutParams(lp);  
            }  
        });  
  
    } 
	

	@Override
	public void refresh() {
		if(getActivity()!=null&&init.compareAndSet(false,true)){
			initAdapter();
		}
	}
	
	@Receiver(actions={REFEREMSGCOUNT,ADDMSG})
	public void receiver(Intent i ){
		try {
			String action=i.getAction();
			if (action.equals(REFEREMSGCOUNT)) {
				List<ChatListBean> list=adapter.getList();
				for (ChatListBean messageListBean : list) {
					if(messageListBean.getFriendid().equals(i.getStringExtra("id"))){
						QueryBuilder<DfMessage, Integer> qb= messageDao.queryBuilder();
						Where where =qb.where();
						where.and(where.eq("userid", user.getId()), where.eq("receiverUid", user.getId()), where.eq("sendUid", messageListBean.getFriendid()),where.eq("isRead", 0));
						messageListBean.setMsgCount((int)qb.countOf());
					}
				}
				adapter.notifyDataSetChanged();
			}else if(action.equals(ADDMSG)){
				ChatListBean bean=(ChatListBean) i.getExtras().getSerializable("bean");
				
				QueryBuilder<DfMessage, Integer> qb= messageDao.queryBuilder();
				Where where =qb.where();
				where.and(where.eq("userid", user.getId()), where.eq("receiverUid", user.getId()), where.eq("sendUid", bean.getFriendid()),where.eq("isRead", 0));
				bean.setMsgCount((int)qb.countOf());
				
				ChatListBean temp=null;
				List<ChatListBean> list=adapter.getList();
				for (ChatListBean messageListBean : list) {
					if(messageListBean.getFriendid().equals(bean.getFriendid())){
						temp=messageListBean;
					}
				}
				if(temp!=null){
					adapter.removeObj(temp);
				}
				adapter.addFirstItem(bean);
				adapter.notifyDataSetChanged();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
