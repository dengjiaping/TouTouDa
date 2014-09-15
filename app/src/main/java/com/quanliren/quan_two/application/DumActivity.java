package com.quanliren.quan_two.application; /**
 * Licensed under Creative Commons Attribution 3.0 Unported license.
 * http://creativecommons.org/licenses/by/3.0/
 * You are free to copy, distribute and transmit the work, and 
 * to adapt the work.  You must attribute android-plist-parser 
 * to Free Beachler (http://www.freebeachler.com).
 * 
 * The Android PList parser (android-plist-parser) is distributed in 
 * the hope that it will be useful, but WITHOUT ANY WARRANTY; without 
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE.
 *//*
package com.longevitysoft.android;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;

import com.longevitysoft.android.xml.plist.PListXMLHandler;
import com.longevitysoft.android.xml.plist.PListXMLParser;
import com.longevitysoft.android.xml.plist.domain.Array;
import com.longevitysoft.android.xml.plist.domain.Dict;
import com.longevitysoft.android.xml.plist.domain.PList;
import com.longevitysoft.android.xml.plist.domain.PListObject;

*//**
 * 
 *//*

*//**
 * Here for the purpose of instrumentation. For example of how to use the PList
 * parser, refer to the android-plist-parser-test instrumentation project.
 *//*
public class DumActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		new Thread(new Runnable() {
			@Override
			public void run() {
				PListXMLParser parser = new PListXMLParser();
				PListXMLHandler handler = new PListXMLHandler();
				parser.setHandler(handler);

				try {
					parser.parse(getAssets().open("area.plist"));
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				PList actualPList = ((PListXMLHandler) parser.getHandler())
						.getPlist();
				Dict root = (Dict) actualPList.getRootElement();

				Map<String, PListObject> provinceCities = root.getConfigMap();

				for (String key : provinceCities.keySet()) {
					PListObject o = provinceCities.get(key);
					try {
						System.out.println(new String(((com.longevitysoft.android.xml.plist.domain.String)o).getValue().getBytes("utf-8")));
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
		;
	}

}
*/