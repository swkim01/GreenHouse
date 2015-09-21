package deu.sw.greenhouse.graph;

import android.os.Parcel;
import android.os.Parcelable;

public class TimeValue implements Parcelable {
	private int time;
	private float value;
	
	public TimeValue(int time, float value) {
		this.time = time;
		this.value = value;
	}
	
	public TimeValue(Parcel in) {
		// TODO Auto-generated constructor stub
		readFromParcel(in);
	}

	public int getTime() {
		return this.time;
	}
	
	public float getValue() {
		return this.value;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeInt(this.time);
		dest.writeFloat(this.value);
	}
	
	private void readFromParcel(Parcel in) {
		time = in.readInt();
		value = in.readFloat();
	}
	
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public TimeValue createFromParcel(Parcel in) {
			return new TimeValue(in);
		}

		public TimeValue[] newArray(int size) {
			return new TimeValue[size];
		}
	};	
}
