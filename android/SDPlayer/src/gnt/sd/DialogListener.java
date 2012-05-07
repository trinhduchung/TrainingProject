package gnt.sd;

public interface DialogListener {
	public void onDialogShow();
	public void onDialogSendErrorMessage(final String title, final String message);
	public void onDialogSetFileSize(final String filename, final long Size);
    public void onDialogProgressUpdate(final int value);
    public void onDialogDismiss();
}
