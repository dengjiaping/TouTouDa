package com.quanliren.quan_two.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.quanliren.quan_two.activity.R;
import com.quanliren.quan_two.activity.user.UserOtherInfoActivity_;
import com.quanliren.quan_two.application.AppClass;
import com.quanliren.quan_two.bean.DfMessage;
import com.quanliren.quan_two.bean.User;
import com.quanliren.quan_two.bean.emoticon.EmoticonActivityListBean.EmoticonZip.EmoticonJsonBean;
import com.quanliren.quan_two.service.SocketManage;
import com.quanliren.quan_two.util.DrawableCache;
import com.quanliren.quan_two.util.ImageUtil;
import com.quanliren.quan_two.util.StaticFactory;
import com.quanliren.quan_two.util.Util;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import pl.droidsonroids.gif.MyGifImageView;

public class MessageAdapter extends ParentsAdapter {
	AppClass ac;
	User friend;
	User user;
	Handler hanlder;

	public void setFriend(User friend) {
		this.friend = friend;
	}

	private int COME_MSG = 0;
	private int TO_MSG = 1;

	public Object getItem(int position) {
		return super.getItem((position - 1));
	}

	public void removeObj(int position) {
		super.removeObj((position - 1));
	}

	public void addNewsItem(Object newsitem) {
		super.addNewsItem(newsitem);
	}

	private int windowWidth;

	public MessageAdapter(Context c, List list, User friend, Handler hanler) {
		super(c, list);
		this.friend = friend;
		this.hanlder = hanler;
		ac = (AppClass) c.getApplicationContext();
		user = getHelper().getUserInfo();
		windowWidth = ImageUtil.px2dip(c,
				c.getResources().getDisplayMetrics().widthPixels) - 130;
	}

	public int getItemViewType(int position) {
		DfMessage entity = (DfMessage) getList().get(position);
		if (entity.getSendUid().equals(friend.getId())) {
			return COME_MSG;
		} else {
			return TO_MSG;
		}
	}

	public int getViewTypeCount() {
		return 2;
	}

	LinearLayout.LayoutParams lpn = new LinearLayout.LayoutParams(
			LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

	public View getView(int position, View convertView, ViewGroup arg2) {
		DfMessage dm = (DfMessage) getList().get(position);
		int isComMsg = getItemViewType(position);
		ViewHolder holder = null;

		if (convertView == null) {
			if (isComMsg == COME_MSG) {
				convertView = View.inflate(c,
						R.layout.chatting_item_msg_text_left, null);
			} else {
				convertView = View.inflate(c,
						R.layout.chatting_item_msg_text_right, null);
			}
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		try {
			holder.error_btn.setVisibility(View.GONE);
			holder.progress.setVisibility(View.GONE);
			if (isComMsg == COME_MSG) {
				ImageLoader.getInstance().displayImage(
						friend.getAvatar() + StaticFactory._160x160,
						holder.user_logo, ac.options_userlogo);
				holder.user_logo.setOnClickListener(logo_click);
			} else {
				ImageLoader.getInstance().displayImage(
						user.getAvatar() + StaticFactory._160x160,
						holder.user_logo, ac.options_userlogo);
			}
			if (dm.getDownload() == SocketManage.D_downloading) {
				holder.progress.setVisibility(View.VISIBLE);
			} else if (dm.getDownload() == SocketManage.D_destroy) {
				holder.error_btn.setVisibility(View.VISIBLE);
				holder.error_btn.setTag(dm);
				holder.error_btn.setOnClickListener(logo_click);
			}
			holder.content.setVisibility(View.GONE);
			holder.img_ll.setVisibility(View.GONE);
			holder.gif_ll.setVisibility(View.GONE);
			holder.voice_ll.setVisibility(View.GONE);
            if (isComMsg == COME_MSG) {
                lpn.leftMargin=ImageUtil.dip2px(c,4);
            }else{    lpn.rightMargin=ImageUtil.dip2px(c,4);}
			holder.voice_ll.setLayoutParams(lpn);
			holder.time.setVisibility(View.GONE);
			holder.timel.setVisibility(View.GONE);
			switch (dm.getMsgtype()) {
			case 0:
				holder.content.setVisibility(View.VISIBLE);
				holder.content.setText(dm.getContent());
				holder.content.setTag(dm);
				holder.content.setOnLongClickListener(long_click);
				holder.content.setOnClickListener(null);
				break;
			case 1:
				holder.img_ll.setVisibility(View.VISIBLE);
				holder.img_ll.setTag(dm);
				holder.img_ll.setOnLongClickListener(long_click);
				holder.img_ll.setOnClickListener(voice_click);
				if (dm.getContent().startsWith("http://")) {
					ImageLoader.getInstance().displayImage(
							dm.getContent() + StaticFactory._160x160,
							holder.img, ac.options_chat);
				} else {
					ImageLoader.getInstance().displayImage(
							Util.FILE + dm.getContent(), holder.img,
							ac.options_chat);
				}
				break;
			case 2:
				holder.voice_ll.setVisibility(View.VISIBLE);
				holder.voice_ll.setTag(dm);
				holder.voice_ll.setOnLongClickListener(long_click);
				holder.voice_ll.setOnClickListener(voice_click);
				holder.timel.setVisibility(View.VISIBLE);
				holder.timel.setText(dm.getTimel() + "''");
				int cha = dm.getTimel() - 5;
				if (cha > 0) {
					int sum = 60 + (cha * 3);
					if (sum > 150) {
						sum = 150;
					}
					if (holder.lp == null) {
						holder.lp = new LinearLayout.LayoutParams(
								ImageUtil.dip2px(c, sum),
								LayoutParams.WRAP_CONTENT);
					} else {
						holder.lp.width = ImageUtil.dip2px(c, sum);
					}
                    if (isComMsg == COME_MSG) {
                        holder.lp.leftMargin=ImageUtil.dip2px(c,4);
                    }else{    holder.lp.rightMargin=ImageUtil.dip2px(c,4);}

					holder.voice_ll.setLayoutParams(holder.lp);
				}
				if (dm.isPlaying()) {
					if (isComMsg == COME_MSG) {
						holder.voice.setImageDrawable(c.getResources()
								.getDrawable(R.drawable.chat_left_animation));
					} else {
						holder.voice.setImageDrawable(c.getResources()
								.getDrawable(R.drawable.chat_right_animation));
					}
					AnimationDrawable animationDrawable = (AnimationDrawable) holder.voice
							.getDrawable();
					animationDrawable.start();
				} else {
					if (isComMsg == COME_MSG) {
						holder.voice.setImageDrawable(c.getResources()
								.getDrawable(R.drawable.chat_left_voice3));
					} else {
						holder.voice.setImageDrawable(c.getResources()
								.getDrawable(R.drawable.chat_right_voice3));
					}
				}
				break;
			case 5:
				holder.gif_ll.setVisibility(View.VISIBLE);
				holder.gif_ll.setTag(dm);
				holder.gif_ll.setOnLongClickListener(long_click);
				holder.gif_ll.setOnClickListener(null);
				EmoticonJsonBean bean = dm.getGifContent();
				if (isComMsg == COME_MSG) {
					ImageLoader.getInstance().loadImage(bean.getGifUrl(),
							new gifImageLoadListener(holder));
				} else {
					ImageLoader.getInstance().loadImage(
							Util.FILE + bean.getGifFile(),
							new gifImageLoadListener(holder));
				}
				break;
			case 4:
				holder.content.setVisibility(View.VISIBLE);
				holder.content.setText(dm.getOtherContent().getText()
						+ "　[点击查看]");
				holder.content.setTag(dm);
				holder.content.setOnLongClickListener(long_click);
				holder.content.setOnClickListener(voice_click);
				break;
			default:
				break;
			}

			if (dm.isShowTime()
					|| (position > 0 && Util.fmtDateTime.parse(dm.getCtime())
							.getTime() - 60 * 1000 > Util.fmtDateTime.parse(
							((DfMessage) getList().get(position - 1))
									.getCtime()).getTime())) {
				holder.time.setVisibility(View.VISIBLE);
				holder.time.setText(Util.getChatTime(dm.getCtime()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return convertView;
	}

	class gifImageLoadListener extends SimpleImageLoadingListener {

		ViewHolder holder;

		public gifImageLoadListener(ViewHolder holder) {
			this.holder = holder;
		}

		@Override
		public void onLoadingStarted(String imageUri, View view) {
			holder.gif_progress.setVisibility(View.VISIBLE);
			holder.gif.setImageDrawable(null);
		}

		@Override
		public void onLoadingComplete(String imageUri, View view,
				Bitmap loadedImage) {
			holder.gif_progress.setVisibility(View.GONE);
			DrawableCache.getInstance().displayDrawable(
					holder.gif,
					ImageLoader.getInstance().getDiskCache().get(imageUri)
							.getPath());

		}

		@Override
		public void onLoadingFailed(String imageUri, View view,
				FailReason failReason) {
			holder.gif_progress.setVisibility(View.GONE);
		}
	}

	static class ViewHolder {
		@InjectView(R.id.chat_user_logo)
		ImageView user_logo;
		@InjectView(R.id.img)
		ImageView img;
		@InjectView(R.id.voice)
		ImageView voice;
		@InjectView(R.id.chat_context_tv)
		TextView content;
		@InjectView(R.id.time)
		TextView time;
		@InjectView(R.id.timel)
		TextView timel;
		@InjectView(R.id.img_ll)
		View img_ll;
		@InjectView(R.id.voice_ll)
		View voice_ll;
		@InjectView(R.id.progress)
		View progress;
		@InjectView(R.id.error_btn)
		View error_btn;
		@InjectView(R.id.gif_ll)
		View gif_ll;
		@InjectView(R.id.gif_progress)
		View gif_progress;
		@InjectView(R.id.gif)
		MyGifImageView gif;
		LinearLayout.LayoutParams lp;

		public ViewHolder(View view) {
			ButterKnife.inject(this, view);
		}
	}

	OnClickListener voice_click = new OnClickListener() {

		@Override
		public void onClick(View v) {
			DfMessage msg = (DfMessage) v.getTag();
			Message ms = hanlder.obtainMessage();
			switch (msg.getMsgtype()) {
			case 1:
			case 2:
				ms.what = msg.getMsgtype();
				ms.obj = msg;
				ms.sendToTarget();
				break;
			case 4:
				ms.what = 0;
				ms.obj = msg;
				ms.sendToTarget();
				break;
			}

		}
	};
	OnClickListener logo_click = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.chat_user_logo:
				Intent i = new Intent(c, UserOtherInfoActivity_.class);
				i.putExtra("userId", friend.getId());
				c.startActivity(i);
				break;
			case R.id.error_btn:
				DfMessage msg = (DfMessage) v.getTag();
				Message ms = hanlder.obtainMessage();
				ms.what = 6;
				ms.obj = msg;
				ms.sendToTarget();
				break;
			default:
				break;
			}

		}
	};

	OnLongClickListener long_click = new OnLongClickListener() {

		@Override
		public boolean onLongClick(View v) {
			DfMessage msg = (DfMessage) v.getTag();
			Message ms = hanlder.obtainMessage();
			switch (v.getId()) {
			case R.id.chat_context_tv:
				if (msg.getMsgtype() == 4) {
					ms.what = 8;
				} else {
					ms.what = 3;
				}
				ms.obj = msg;
				ms.sendToTarget();
				break;
			case R.id.img_ll:
				ms.what = 4;
				ms.obj = msg;
				ms.sendToTarget();
				break;
			case R.id.voice_ll:
				ms.what = 5;
				ms.obj = msg;
				ms.sendToTarget();
				break;
			case R.id.gif_ll:
				ms.what = 7;
				ms.obj = msg;
				ms.sendToTarget();
				break;
			}
			return true;
		}
	};
}
