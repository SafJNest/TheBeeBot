/*    */ package com.safjnest.Utilities.tts;
/*    */ 
/*    */ import java.util.EventObject;
/*    */ 
/*    */ 
/*    */ public class SpeechDataEvent<T>
/*    */   extends EventObject
/*    */ {
/*    */   private T _data;
/*    */   
/*    */   public SpeechDataEvent(T arg) {
/* 12 */     super(arg);
/*    */     
/* 14 */     this._data = arg;
/*    */   }
/*    */   
/*    */   public T getData() {
/* 18 */     return this._data;
/*    */   }
/*    */ }


/* Location:              C:\Users\leona\Desktop\voicerss_tts.jar!\com\voicerss\tts\SpeechDataEvent.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */