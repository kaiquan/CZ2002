package sg.ntu.core.util.text;

import android.util.Patterns;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author khanhnv
 */
public class ValidateUtil {

    @SuppressWarnings("unused")
    private static final String PASSWORD_PATTERN_STRONG = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,20})";
    private static final String PASSWORD_PATTERN = "[0-9a-zA-z!@#$%^&*]{6,20}";
    private static ValidateUtil mInstance;
    private Pattern mPattern;
    private Matcher mMatcher;

    private ValidateUtil() {
        mPattern = Pattern.compile(PASSWORD_PATTERN);
    }

    public static ValidateUtil getInstance() {
        if (mInstance == null) {
            mInstance = new ValidateUtil();
        }
        return mInstance;
    }

    /**
     * Validate password with regular expression
     *
     * @param password password for validation
     * @return true valid is password, false invalid password
     */
    public boolean validatePassword(final String password) {
        mMatcher = mPattern.matcher(password);
        return mMatcher.matches();
    }

    /**
     * validate email address
     *
     * @param email the email uses to validate
     * @return true if email is correct, else return false
     */
    public boolean validateEmail(final String email) {
        mMatcher = Patterns.EMAIL_ADDRESS.matcher(email);
        return mMatcher.matches();
    }

    /**
     * validate phone number
     *
     * @param phone the phone number uses to validate
     * @return true if phone correct, else return false
     */
    public boolean validatePhone(final String phone) {
        mMatcher = Patterns.PHONE.matcher(phone);
        return mMatcher.matches();
    }

    /**
     * validate web URL
     *
     * @param url the URL uses to validate
     * @return true if web URL is correct, else return false
     */
    public boolean validateUrl(final String url) {
        mMatcher = Patterns.WEB_URL.matcher(url);
        return mMatcher.matches();
    }
}
