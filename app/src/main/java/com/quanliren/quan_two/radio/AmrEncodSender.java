package com.quanliren.quan_two.radio;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.util.Log;

import com.bambuser.broadcaster.AmrEncoder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class AmrEncodSender extends Thread implements OnCompletionListener{
	
	private static final String TAG="AmrEncodSender";

	AudioRecord mAudioRecorder;
	private String fileName = null;
	private MicRealTimeListener mtl;
	private long milltime;
	MediaPlayer mediaPlayer= null;
	AmrEncoder mAmrEncoder;
	
	final int SampleRateInHz = 8000;
	
	public AmrEncodSender(String fileName,MicRealTimeListener mtl){
		this.fileName = fileName;
		this.mtl = mtl;
	}
	
	public void run(){
		
		mAmrEncoder = new AmrEncoder();
		
		int min = AudioRecord.getMinBufferSize(SampleRateInHz,
				AudioFormat.CHANNEL_IN_DEFAULT,
				AudioFormat.ENCODING_PCM_16BIT);
		
		int frameSize = Math.max(2560,min)*4;
		
		mAudioRecorder = new AudioRecord(
				MediaRecorder.AudioSource.MIC,
				SampleRateInHz,
				AudioFormat.CHANNEL_IN_DEFAULT, 
				AudioFormat.ENCODING_PCM_16BIT, 
				frameSize);
		
		int read = 0;
		byte[] arrayOfByte1 = new byte[min];
	    byte[] arrayOfByte2 = new byte[1024];
	    int encodeLength =0;
	    FileOutputStream file_out = null;
	    milltime = System.currentTimeMillis();
	    try {
	    	file_out = new FileOutputStream (fileName);
	    	byte[] head = new byte[]{0x23, 0x21, 0x41, 0x4d, 0x52, 0x0a};
	    	file_out.write(head);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	    mAudioRecorder.startRecording();
		while(AmrEngine.getSingleEngine().isRecordRunning())
		{
			read= mAudioRecorder.read(arrayOfByte1,0,320);
			if(read<0) continue;
			encodeLength = mAmrEncoder.encode(arrayOfByte1,0,read,arrayOfByte2,AmrEncoder.MR515);
			try {
				file_out.write(arrayOfByte2,0,encodeLength);
				int v = 0;
				for (int i = 0; i < arrayOfByte1.length; i++) {
					v += arrayOfByte1[i] * arrayOfByte1[i];
				}
//				double dB = 10*Math.log10(v/(double)read);
				int value = (int) (Math.abs((int)(v /(double)read)/10) >> 1);
//				Log.d(TAG, "----------------"+value);
				mtl.getMicRealTimeSize(Double.valueOf(value), System.currentTimeMillis() - milltime);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		mAudioRecorder.stop();
		mAudioRecorder.release();
		mAudioRecorder=null;
		try {
			file_out.close();
			if (System.currentTimeMillis() - milltime < 1000) {
				File file = new File(fileName);
				file.delete();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void onCompletion(MediaPlayer player) {
		Log.e("", "onCompletion");
		mediaPlayer.stop();
		mediaPlayer.release();
		mediaPlayer = null;
	}
}
