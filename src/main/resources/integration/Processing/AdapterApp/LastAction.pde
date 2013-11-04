class LastAction {
  private String lastAction;
  private long lastActionTs;
    
  public void updateLastAction(String s) {
    this.lastAction = s;
    lastActionTs=System.currentTimeMillis();
  }
  
  String getLastAction() {
    return lastAction;
  }
  
  long getLastActionTs() {
    return System.currentTimeMillis()-lastActionTs;
  }
}
