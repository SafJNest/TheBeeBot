/*    */ package com.safjnest.Utilities.tts;
/*    */ 
/*    */ import java.util.EventObject;
/*    */ 
/*    */ 
/*    */ public class SpeechErrorEvent
/*    */   extends EventObject
/*    */ {
/*    */   private Exception _exception;
/*    */   
/*    */   public SpeechErrorEvent(Exception arg) {
/* 12 */     super(arg);
/*    */     
/* 14 */     this._exception = arg;
/*    */   }
/*    */   
/*    */   public Exception getException() {
/* 18 */     return this._exception;
/*    */   }
/*    */ }


/* Location:              C:\Users\leona\Desktop\voicerss_tts.jar!\com\voicerss\tts\SpeechErrorEvent.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */