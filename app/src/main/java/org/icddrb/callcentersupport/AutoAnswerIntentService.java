/*
 * AutoAnswer
 * Copyright (C) 2010 EverySoft
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * This file incorporates work covered by the following copyright and
 * permission notice:
 *
 *   Copyright (C) 2010 Tedd Scofield
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.icddrb.callcentersupport;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;

import java.io.IOException;

public class AutoAnswerIntentService extends IntentService {

    public AutoAnswerIntentService() {
        super("AutoAnswerIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Context context = getBaseContext();

        // Load preferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);


        // Let the phone ring for a set delay
        try {
            Thread.sleep(Integer.parseInt(prefs.getString("delay", "2")) * 1000);
        } catch (InterruptedException e) {
            // We don't really care
        }

        // Make sure the phone is still ringing
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm.getCallState() != TelephonyManager.CALL_STATE_RINGING) {
            return;
        }

        // Answer the phone
        try {
            answerPhoneHeadsethook(context);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("AutoAnswer", "Error trying to answer using headset.");

        }

        // Enable the speakerphone
        if (prefs.getBoolean("use_speakerphone", false)) {
            enableSpeakerPhone(context);
        }
        return;
    }

    private void enableSpeakerPhone(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setSpeakerphoneOn(true);
    }

    private void answerPhoneHeadsethook(Context context) {

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;

        try {
            if (currentapiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                // Do something for lollipop and above versions
                Log.d("Lollipop>=: ", "execute input keycode headset hook");
                Runtime.getRuntime().exec("input keyevent " +
                        Integer.toString(KeyEvent.KEYCODE_HEADSETHOOK));
            } else {
                // do something for phones running an SDK before lollipop
                Log.d("Kitkat <=: ", "execute input keycode headset hook");
                // Simulate a press of the headset button to pick up the call
                Intent buttonDown = new Intent(Intent.ACTION_MEDIA_BUTTON);
                buttonDown.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN,
                        KeyEvent.KEYCODE_HEADSETHOOK));
                context.sendOrderedBroadcast(buttonDown, "android.permission.CALL_PRIVILEGED");
                // froyo and beyond trigger on buttonUp instead of buttonDown
                Intent buttonUp = new Intent(Intent.ACTION_MEDIA_BUTTON);
                buttonUp.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP,
                        KeyEvent.KEYCODE_HEADSETHOOK));
                context.sendOrderedBroadcast(buttonUp, "android.permission.CALL_PRIVILEGED");
            }
        } catch (IOException e) {

        }
    }

}
