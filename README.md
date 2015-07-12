# quasar-firebase
Firebase bindings for Quasar e.g. transforms of the asynchronous Firebase Java APIs to fiber-blocking operations.  Example to fetch a single value event:

``` java
DataSnapshot snap = QuasarUtil.INSTANCE.run(new ValueEventListenerAsync() {
  @Override
  protected void requestAsync() {
    ref.child(id).addListenerForSingleValueEvent(this);
  }
});
```
