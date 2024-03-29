/*     */ package org.voicerss.tts.Voice;
/*     */ 
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.net.HttpURLConnection;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;

import org.voicerss.tts.Speech.SpeechDataEvent;
import org.voicerss.tts.Speech.SpeechDataEventListener;
import org.voicerss.tts.Speech.SpeechErrorEvent;
import org.voicerss.tts.Speech.SpeechErrorEventListener;
/*     */ 
/*     */ public class VoiceProvider
/*     */ {
/*  14 */   private ArrayList<SpeechErrorEventListener> _speechErrorListeners = new ArrayList<SpeechErrorEventListener>();
/*  15 */   private ArrayList<SpeechDataEventListener> _speechDataListeners = new ArrayList<SpeechDataEventListener>();
/*     */   
/*     */   private String _apiKey;
/*     */   private Boolean _ssl;
/*     */   
/*     */   public VoiceProvider(String apiKey) {
/*  21 */     this._apiKey = apiKey;
/*  22 */     this._ssl = Boolean.valueOf(false);
/*     */   }
/*     */   
/*     */   public VoiceProvider(String apiKey, Boolean ssl) {
/*  26 */     this(apiKey);
/*  27 */     this._ssl = ssl;
/*     */   }
/*     */   
/*     */   public String getApiKey() {
/*  31 */     return this._apiKey;
/*     */   }
/*     */   
/*     */   public void setApiKey(String value) {
/*  35 */     this._apiKey = value;
/*     */   }
/*     */   
/*     */   public Boolean getSSL() {
/*  39 */     return this._ssl;
/*     */   }
/*     */   
/*     */   public void setSSL(Boolean value) {
/*  43 */     this._ssl = value;
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized void addSpeechErrorEventListener(SpeechErrorEventListener listener) {
/*  48 */     this._speechErrorListeners.add(listener);
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized void removeSpeechErrorEventListener(SpeechErrorEventListener listener) {
/*  53 */     this._speechErrorListeners.remove(listener);
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized void addSpeechDataEventListener(SpeechDataEventListener listener) {
/*  58 */     this._speechDataListeners.add(listener);
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized void removeSpeechDataEventListener(SpeechDataEventListener listener) {
/*  63 */     this._speechDataListeners.remove(listener);
/*     */   }
/*     */ 
/*     */   
/*     */   private void handleSpeechError(Exception exception) {
/*  68 */     if (exception != null && this._speechErrorListeners != null)
/*     */     {
/*  70 */       for (SpeechErrorEventListener _listener : this._speechErrorListeners) {
/*  71 */         _listener.handleSpeechErrorEvent(new SpeechErrorEvent(exception));
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   @SuppressWarnings({ "rawtypes", "unchecked" })
private <T> void handleSpeechData(T data) {
/*  77 */     if (data != null && !data.equals("") && this._speechDataListeners != null)
/*     */     {
/*  79 */       for (SpeechDataEventListener _listener : this._speechDataListeners) {
/*  80 */         _listener.handleSpeechDataEvent(new SpeechDataEvent(data));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   @SuppressWarnings("unchecked")
public <T> T speech(VoiceParameters params) throws Exception {
/*  87 */     validate(params);
/*     */     
/*  89 */     URL url = new URL(String.valueOf(this._ssl.booleanValue() ? "https" : "http") + "://api.voicerss.org/");
/*  90 */     HttpURLConnection conn = (HttpURLConnection)url.openConnection();
/*  91 */     conn.setRequestMethod("POST");
/*  92 */     conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
/*  93 */     conn.setConnectTimeout(60000);
/*  94 */     conn.setDoOutput(true);
/*     */     
/*  96 */     DataOutputStream outStream = new DataOutputStream(conn.getOutputStream());
/*  97 */     BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream, "UTF-8"));
/*  98 */     writer.write(buildParameters(params));
/*  99 */     writer.close();
/* 100 */     outStream.close();
/*     */ 
/*     */     
/* 103 */     if (conn.getResponseCode() != 200) {
/* 104 */       throw new Exception(conn.getResponseMessage());
/*     */     }
/* 106 */     ByteArrayOutputStream outArray = new ByteArrayOutputStream();
/* 107 */     InputStream inStream = conn.getInputStream();
/*     */     
/* 109 */     byte[] buffer = new byte[4096];
/* 110 */     int n = -1;
/*     */     
/* 112 */     while ((n = inStream.read(buffer)) > 0) {
/* 113 */       outArray.write(buffer, 0, n);
/*     */     }
/* 115 */     byte[] response = outArray.toByteArray();
/*     */     
/* 117 */     inStream.close();
/*     */     
/* 119 */     String responseString = new String(response, "UTF-8");
/*     */     
/* 121 */     if (responseString.indexOf("ERROR") == 0) {
/* 122 */       throw new Exception(responseString);
/*     */     }
/* 124 */     return params.getBase64().booleanValue() ? (T)responseString : (T)response;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void speechAsync(final VoiceParameters params) {
/*     */     try {
/* 131 */       (new Thread(new Runnable()
/*     */           {
/*     */             
/*     */             public void run()
/*     */             {
/*     */               try {
/* 137 */                 Object response = VoiceProvider.this.speech(params);
/*     */                 
/* 139 */                 if (params.getBase64().booleanValue()) {
/* 140 */                   VoiceProvider.this.handleSpeechData(response);
/*     */                 } else {
/* 142 */                   VoiceProvider.this.handleSpeechData(response);
/*     */                 } 
/* 144 */               } catch (Exception e) {
/*     */                 
/* 146 */                 VoiceProvider.this.handleSpeechError(e);
/*     */               } 
/*     */             }
/* 149 */           })).start();
/*     */     }
/* 151 */     catch (Exception e) {
/*     */       
/* 153 */       handleSpeechError(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void validate(VoiceParameters params) throws Exception {
/* 159 */     if (this._apiKey == null || this._apiKey.trim().equals("")) {
/* 160 */       throw new Exception("The API key is undefined");
/*     */     }
/* 162 */     if (params.getText() == null || params.getText().trim().equals("")) {
/* 163 */       throw new Exception("The text is undefined");
/*     */     }
/* 165 */     if (params.getLanguage() == null || params.getLanguage().trim().equals("")) {
/* 166 */       throw new Exception("The language is undefined");
/*     */     }
/*     */   }
/*     */   
/*     */   private String buildParameters(VoiceParameters params) {
/* 171 */     StringBuilder sb = new StringBuilder();
/*     */     
/* 173 */     sb.append("key=" + ((this._apiKey != null) ? this._apiKey : ""));
/* 174 */     sb.append("&src=" + ((params.getText() != null) ? params.getText() : ""));
/* 175 */     sb.append("&hl=" + ((params.getLanguage() != null) ? params.getLanguage() : ""));
/* 176 */     sb.append("&v=" + ((params.getVoice() != null) ? params.getVoice() : ""));
/* 177 */     sb.append("&r=" + ((params.getRate() != null) ? params.getRate().toString() : ""));
/* 178 */     sb.append("&c=" + ((params.getCodec() != null) ? params.getCodec() : ""));
/* 179 */     sb.append("&f=" + ((params.getFormat() != null) ? params.getFormat() : ""));
/* 180 */     sb.append("&ssml=" + ((params.getSSML() != null) ? params.getSSML().toString() : ""));
/* 181 */     sb.append("&b64=" + ((params.getBase64() != null) ? params.getBase64().toString() : ""));
/*     */     
/* 183 */     return sb.toString();
/*     */   }
/*     */ }


/* Location:              C:\Users\leona\Desktop\voicerss_tts.jar!\com\voicerss\tts\VoiceProvider.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */