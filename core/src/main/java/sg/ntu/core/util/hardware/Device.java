package sg.ntu.core.util.hardware;

public class Device {

    public int mWidth;
    public int mHeight;
    public int mOrientation;
    public boolean mIsTablet;

    public Device() {
        super();
    }

    public Device(int mWidth, int mHeight, int mOrientation) {
        super();
        this.mWidth = mWidth;
        this.mHeight = mHeight;
        this.mOrientation = mOrientation;
    }

    public Device(int mWidth, int mHeight, int mOrientation,
                  boolean mIsTablet) {
        super();
        this.mWidth = mWidth;
        this.mHeight = mHeight;
        this.mOrientation = mOrientation;
        this.mIsTablet = mIsTablet;
    }

    @Override
    public String toString() {
        return "Device [mWidth=" + mWidth + ", mHeight=" + mHeight
                + ", mOrientation=" + mOrientation + ", mIsTablet=" + mIsTablet
                + "]";
    }

}
