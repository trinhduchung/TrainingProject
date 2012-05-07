/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Users/trinhduchung/Downloads/SDPlayer/src/gnt/sd/IPlayerService.aidl
 */
package gnt.sd;
public interface IPlayerService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements gnt.sd.IPlayerService
{
private static final java.lang.String DESCRIPTOR = "gnt.sd.IPlayerService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an gnt.sd.IPlayerService interface,
 * generating a proxy if needed.
 */
public static gnt.sd.IPlayerService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof gnt.sd.IPlayerService))) {
return ((gnt.sd.IPlayerService)iin);
}
return new gnt.sd.IPlayerService.Stub.Proxy(obj);
}
public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_getState:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getState();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_isPlaying:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.isPlaying();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_setLooping:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
this.setLooping(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getLooping:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.getLooping();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_play:
{
data.enforceInterface(DESCRIPTOR);
this.play();
reply.writeNoException();
return true;
}
case TRANSACTION_replay:
{
data.enforceInterface(DESCRIPTOR);
this.replay();
reply.writeNoException();
return true;
}
case TRANSACTION_pause:
{
data.enforceInterface(DESCRIPTOR);
this.pause();
reply.writeNoException();
return true;
}
case TRANSACTION_prev:
{
data.enforceInterface(DESCRIPTOR);
this.prev();
reply.writeNoException();
return true;
}
case TRANSACTION_next:
{
data.enforceInterface(DESCRIPTOR);
this.next();
reply.writeNoException();
return true;
}
case TRANSACTION_setVolume:
{
data.enforceInterface(DESCRIPTOR);
float _arg0;
_arg0 = data.readFloat();
this.setVolume(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_seekTo:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.seekTo(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getDuration:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getDuration();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getPosition:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getPosition();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_stop:
{
data.enforceInterface(DESCRIPTOR);
this.stop();
reply.writeNoException();
return true;
}
case TRANSACTION_getCurrentAudioId:
{
data.enforceInterface(DESCRIPTOR);
long _result = this.getCurrentAudioId();
reply.writeNoException();
reply.writeLong(_result);
return true;
}
case TRANSACTION_isShuffle:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.isShuffle();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_setShuffle:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
this.setShuffle(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getLoopMode:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getLoopMode();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_setLoopMode:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setLoopMode(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getSequenceIndex:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getSequenceIndex();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getIdsSequence:
{
data.enforceInterface(DESCRIPTOR);
long[] _result = this.getIdsSequence();
reply.writeNoException();
reply.writeLongArray(_result);
return true;
}
case TRANSACTION_setIdsSequence:
{
data.enforceInterface(DESCRIPTOR);
long[] _arg0;
_arg0 = data.createLongArray();
int _arg1;
_arg1 = data.readInt();
this.setIdsSequence(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_setSequenceIndex:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setSequenceIndex(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getIdsList:
{
data.enforceInterface(DESCRIPTOR);
long[] _result = this.getIdsList();
reply.writeNoException();
reply.writeLongArray(_result);
return true;
}
case TRANSACTION_getCurrentInfo:
{
data.enforceInterface(DESCRIPTOR);
gnt.sd.model.SDMusic _result = this.getCurrentInfo();
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_canNext:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.canNext();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_canPrev:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.canPrev();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements gnt.sd.IPlayerService
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
public int getState() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getState, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean isPlaying() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_isPlaying, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void setLooping(boolean isLooping) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((isLooping)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setLooping, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public boolean getLooping() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getLooping, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void play() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_play, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void replay() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_replay, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void pause() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_pause, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void prev() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_prev, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void next() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_next, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setVolume(float vol) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeFloat(vol);
mRemote.transact(Stub.TRANSACTION_setVolume, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void seekTo(int position) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(position);
mRemote.transact(Stub.TRANSACTION_seekTo, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public int getDuration() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getDuration, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getPosition() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getPosition, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void stop() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_stop, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public long getCurrentAudioId() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
long _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getCurrentAudioId, _data, _reply, 0);
_reply.readException();
_result = _reply.readLong();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean isShuffle() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_isShuffle, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void setShuffle(boolean isEnable) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((isEnable)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setShuffle, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public int getLoopMode() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getLoopMode, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void setLoopMode(int mode) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(mode);
mRemote.transact(Stub.TRANSACTION_setLoopMode, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public int getSequenceIndex() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getSequenceIndex, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public long[] getIdsSequence() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
long[] _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getIdsSequence, _data, _reply, 0);
_reply.readException();
_result = _reply.createLongArray();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void setIdsSequence(long[] ids, int startIdx) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeLongArray(ids);
_data.writeInt(startIdx);
mRemote.transact(Stub.TRANSACTION_setIdsSequence, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setSequenceIndex(int startIdx) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(startIdx);
mRemote.transact(Stub.TRANSACTION_setSequenceIndex, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public long[] getIdsList() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
long[] _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getIdsList, _data, _reply, 0);
_reply.readException();
_result = _reply.createLongArray();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public gnt.sd.model.SDMusic getCurrentInfo() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
gnt.sd.model.SDMusic _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getCurrentInfo, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = gnt.sd.model.SDMusic.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean canNext() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_canNext, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean canPrev() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_canPrev, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_getState = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_isPlaying = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_setLooping = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_getLooping = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_play = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_replay = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_pause = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_prev = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
static final int TRANSACTION_next = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
static final int TRANSACTION_setVolume = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
static final int TRANSACTION_seekTo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 10);
static final int TRANSACTION_getDuration = (android.os.IBinder.FIRST_CALL_TRANSACTION + 11);
static final int TRANSACTION_getPosition = (android.os.IBinder.FIRST_CALL_TRANSACTION + 12);
static final int TRANSACTION_stop = (android.os.IBinder.FIRST_CALL_TRANSACTION + 13);
static final int TRANSACTION_getCurrentAudioId = (android.os.IBinder.FIRST_CALL_TRANSACTION + 14);
static final int TRANSACTION_isShuffle = (android.os.IBinder.FIRST_CALL_TRANSACTION + 15);
static final int TRANSACTION_setShuffle = (android.os.IBinder.FIRST_CALL_TRANSACTION + 16);
static final int TRANSACTION_getLoopMode = (android.os.IBinder.FIRST_CALL_TRANSACTION + 17);
static final int TRANSACTION_setLoopMode = (android.os.IBinder.FIRST_CALL_TRANSACTION + 18);
static final int TRANSACTION_getSequenceIndex = (android.os.IBinder.FIRST_CALL_TRANSACTION + 19);
static final int TRANSACTION_getIdsSequence = (android.os.IBinder.FIRST_CALL_TRANSACTION + 20);
static final int TRANSACTION_setIdsSequence = (android.os.IBinder.FIRST_CALL_TRANSACTION + 21);
static final int TRANSACTION_setSequenceIndex = (android.os.IBinder.FIRST_CALL_TRANSACTION + 22);
static final int TRANSACTION_getIdsList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 23);
static final int TRANSACTION_getCurrentInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 24);
static final int TRANSACTION_canNext = (android.os.IBinder.FIRST_CALL_TRANSACTION + 25);
static final int TRANSACTION_canPrev = (android.os.IBinder.FIRST_CALL_TRANSACTION + 26);
}
public int getState() throws android.os.RemoteException;
public boolean isPlaying() throws android.os.RemoteException;
public void setLooping(boolean isLooping) throws android.os.RemoteException;
public boolean getLooping() throws android.os.RemoteException;
public void play() throws android.os.RemoteException;
public void replay() throws android.os.RemoteException;
public void pause() throws android.os.RemoteException;
public void prev() throws android.os.RemoteException;
public void next() throws android.os.RemoteException;
public void setVolume(float vol) throws android.os.RemoteException;
public void seekTo(int position) throws android.os.RemoteException;
public int getDuration() throws android.os.RemoteException;
public int getPosition() throws android.os.RemoteException;
public void stop() throws android.os.RemoteException;
public long getCurrentAudioId() throws android.os.RemoteException;
public boolean isShuffle() throws android.os.RemoteException;
public void setShuffle(boolean isEnable) throws android.os.RemoteException;
public int getLoopMode() throws android.os.RemoteException;
public void setLoopMode(int mode) throws android.os.RemoteException;
public int getSequenceIndex() throws android.os.RemoteException;
public long[] getIdsSequence() throws android.os.RemoteException;
public void setIdsSequence(long[] ids, int startIdx) throws android.os.RemoteException;
public void setSequenceIndex(int startIdx) throws android.os.RemoteException;
public long[] getIdsList() throws android.os.RemoteException;
public gnt.sd.model.SDMusic getCurrentInfo() throws android.os.RemoteException;
public boolean canNext() throws android.os.RemoteException;
public boolean canPrev() throws android.os.RemoteException;
}
