/*    */ package org.voicerss.tts.Voice;
/*    */ 
/*    */ public class VoiceParameters {
/*    */   private String _text;
/*    */   private String _language;
/*    */   private String _voice;
/*    */   private Integer _rate;
/*    */   private String _codec;
/*    */   private String _format;
/*    */   private Boolean _ssml;
/*    */   private Boolean _base64;
/*    */   
/*    */   public VoiceParameters(String text, String language) {
/* 14 */     this._text = text;
/* 15 */     this._language = language;
/*    */   }
/*    */   
/*    */   public String getText() {
/* 19 */     return this._text;
/*    */   }
/*    */   
/*    */   public void setText(String value) {
/* 23 */     this._text = value;
/*    */   }
/*    */   
/*    */   public String getLanguage() {
/* 27 */     return this._language;
/*    */   }
/*    */   
/*    */   public void setLanguage(String value) {
/* 31 */     this._language = value;
/*    */   }
/*    */   
/*    */   public String getVoice() {
/* 35 */     return this._voice;
/*    */   }
/*    */   
/*    */   public void setVoice(String value) {
/* 39 */     this._voice = value;
/*    */   }
/*    */   
/*    */   public Integer getRate() {
/* 43 */     return this._rate;
/*    */   }
/*    */   
/*    */   public void setRate(Integer value) {
/* 47 */     this._rate = value;
/*    */   }
/*    */   
/*    */   public String getCodec() {
/* 51 */     return this._codec;
/*    */   }
/*    */   
/*    */   public void setCodec(String value) {
/* 55 */     this._codec = value;
/*    */   }
/*    */   
/*    */   public String getFormat() {
/* 59 */     return this._format;
/*    */   }
/*    */   
/*    */   public void setFormat(String value) {
/* 63 */     this._format = value;
/*    */   }
/*    */   
/*    */   public Boolean getSSML() {
/* 67 */     return this._ssml;
/*    */   }
/*    */   
/*    */   public void setSSML(Boolean value) {
/* 71 */     this._ssml = value;
/*    */   }
/*    */   
/*    */   public Boolean getBase64() {
/* 75 */     return this._base64;
/*    */   }
/*    */   
/*    */   public void setBase64(Boolean value) {
/* 79 */     this._base64 = value;
/*    */   }
/*    */ }


/* Location:              C:\Users\leona\Desktop\voicerss_tts.jar!\com\voicerss\tts\VoiceParameters.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */