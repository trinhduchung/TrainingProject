package gnt.sd.model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

public class SDPlaylist extends BaseObject implements Parcelable {

    public SDPlaylist() {
        this._dateAdded = System.currentTimeMillis();
        this._dateModified = System.currentTimeMillis();
    }

    public SDPlaylist(Parcel in) {
        Bundle bundle = in.readBundle();
        setName(bundle.getString(NAME));
        setDateAdded(bundle.getLong(DATE_ADDED));
        setDateModified(bundle.getLong(DATE_MODIFIED));
    }

    public SDPlaylist(String name, long dateAdded, long dateModified) {
        this._name = name;
        this._dateAdded = dateAdded;
        this._dateModified = dateModified;
    }

    public String getName() {    	
    	return _name;
    }

    public void setName(String name) {
        this._name = name;
    }

    public long getDateAdded() {
        return _dateAdded;
    }

    public void setDateAdded(long dateAdded) {
        this._dateAdded = dateAdded;
    }

    public long getDateModified() {
        return _dateModified;
    }

    public void setDateModified(long dateModified) {
        this._dateModified = dateModified;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<SDPlaylist> CREATOR = new Parcelable.Creator<SDPlaylist>() {
        @Override
		public SDPlaylist createFromParcel(Parcel in) {
            return new SDPlaylist(in);
        }

        @Override
		public SDPlaylist[] newArray(int size) {
            return new SDPlaylist[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Bundle bundle = new Bundle();
        bundle.putString(NAME, getName());
        bundle.putLong(DATE_ADDED, getDateAdded());
        bundle.putLong(DATE_MODIFIED, getDateModified());
        dest.writeBundle(bundle);
    }

    @Override
    public boolean equals(Object o) {
    	return (this.getName().equals(((SDPlaylist) o).getName()));
    }
    
    
    @Override
    public int hashCode() {
    	return super.hashCode();
    }
    
    private static final String NAME = "name";
    private static final String DATE_ADDED = "date_added";
    private static final String DATE_MODIFIED = "date_modified";
    
    protected String 	_name; 			// The name of the playlist
    protected long 	_dateAdded; 	// The time the file was added to the media provider
                                    // Units are seconds since 1970.
    protected long 	_dateModified; 	// The time the file was last modified Units are
                                    // seconds since 1970.
}
