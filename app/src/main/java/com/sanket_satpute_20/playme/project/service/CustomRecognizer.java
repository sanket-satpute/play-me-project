package com.sanket_satpute_20.playme.project.service;

import static com.sanket_satpute_20.playme.MainActivity.musicFiles;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class CustomRecognizer /*extends Service*/ implements TextToSpeech.OnInitListener, RecognitionListener {
//    static final variables
    public static final int TIME_LISTENING = 400;
    public static final int COMPLETE_SILENCE = 100;
    public static final int POSSIBLY_COMPLETE_SILENCE = 100;
    /*  String Variables    */
    public final String APP_NAME = "PLAY ME";
    public String Words, speaker_text = "Hay . How may i help you";

    /*  Extra Variables */
    private SpeechRecognizer _speech;
    private Intent _speechIntent;
    TextToSpeech text_to_speech;
    Context _context;


//    /*  Service Related Stuff   */
//    public final IBinder c_r_binder = new CRBinder();
//
//    public class CRBinder extends Binder {
//        public CustomRecognizer getService() {
//            return CustomRecognizer.this;
//        }
//    }
//
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return c_r_binder;
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        started_Service();
//        return onStartCommand(intent,flags,startId);
//    }

    public CustomRecognizer (Context _context) {
        this._context = _context;
        started_Service();
    }

    public SpeechRecognizer getRecognizer() {
        return _speech;
    }

    private void started_Service() {
        //        initialise text to speak
//        text_to_speech = new TextToSpeech(_context, this, "com.google.android.tts");
//        Set<String> voices = new HashSet<>();
//        voices.add("male");
//        Voice voice = new Voice("en-us-x-sfg#male_2-local", new Locale("en", "US"), 400, 200, true, voices);
//        text_to_speech.setVoice(voice);

//        AudioManager a_manager=(AudioManager)_context.getSystemService(Context.AUDIO_SERVICE);
//        a_manager.setStreamMute(AudioManager.STREAM_NOTIFICATION, true);

        Words = "";
        _speech = SpeechRecognizer.createSpeechRecognizer(this._context);
        _speech.setRecognitionListener(this);
        _speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        _speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            _speechIntent.putExtra(RecognizerIntent.ACTION_RECOGNIZE_SPEECH, RecognizerIntent.EXTRA_PREFER_OFFLINE);
        }
        _speechIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, COMPLETE_SILENCE);
        _speechIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, POSSIBLY_COMPLETE_SILENCE);
        _speechIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, TIME_LISTENING);
    }

    private void startOver()
    {
        _speech.destroy();
        _speech = SpeechRecognizer.createSpeechRecognizer(this._context);
        _speech.setRecognitionListener(this);
        _speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        _speechIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, COMPLETE_SILENCE);
        _speechIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, POSSIBLY_COMPLETE_SILENCE);
        _speechIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, TIME_LISTENING);
        StartListening();
    }

    /*  start & stop listening  */
    public void StartListening()
    {
        _speech.startListening(_speechIntent);
    }

    public void StopListening()
    {
        _speech.stopListening();
        if (text_to_speech != null) {
            text_to_speech.stop();
            text_to_speech.shutdown();
        }
    }

    /*  Voice Recognition Listener Overridden   */
    @Override
    public void onReadyForSpeech(Bundle bundle) {

    }

    @Override
    public void onBeginningOfSpeech() {

    }

    public void onRmsChanged(float rmsdB)
    {

    }

    @Override
    public void onBufferReceived(byte[] bytes) {

    }

    @Override
    public void onEndOfSpeech() {

    }

    @Override
    public void onError(int i) {
        Log.d("ero", "Error " + i);
        startOver();
    }

    @Override
    public void onResults(Bundle bundle) {
        ArrayList<String> matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if (matches == null)
            Words = "Null";
        else
        if (matches.size() != 0)
            Words = matches.get(0);
        else
            Words = "";
        Log.d("llb", Words);
        //do anything you want for the result
        actions(Words);

        startOver();
    }

    @Override
    public void onPartialResults(Bundle bundle) {

    }

    @Override
    public void onEvent(int i, Bundle bundle) {

    }

    /*  Text to Speech Overridden   */
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = text_to_speech.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(_context, "Language Not Supported", Toast.LENGTH_SHORT).show();
            } else {
                speakOut();
            }
        } else {
            Toast.makeText(_context, "Function Not Supported in your Device", Toast.LENGTH_SHORT).show();
        }
    }

//    our methods

    private void speakOut() {
        Toast.makeText(_context, "in", Toast.LENGTH_SHORT).show();
        if (speaker_text != null) {
            String utteranceID = this.hashCode() + "";
            text_to_speech.speak(speaker_text, TextToSpeech.QUEUE_FLUSH, null, utteranceID);
        }
    }

    private void actions(String words) {
        if (words.equalsIgnoreCase("Stop") || words.equalsIgnoreCase("Stop Listening")) {
            Toast.makeText(_context, "inside", Toast.LENGTH_SHORT).show();
            StopListening();
        }
        if (words.equalsIgnoreCase("play") || words.equalsIgnoreCase("play all") ||
                words.equalsIgnoreCase("play songs") || words.equalsIgnoreCase("play song")) {
            if (musicFiles != null) {
                if (musicFiles.size() > 0) {
                    BackService service = new BackService();
                    service.setSongSource(musicFiles);

                    Intent intent = new Intent(_context, BackService.class);
                    intent.putExtra("position", 0);
                    _context.startService(intent);
                }
            }
        }

        if (words.equalsIgnoreCase("playme") || words.equalsIgnoreCase(APP_NAME)) {
            speakOut();
        }
    }
}
