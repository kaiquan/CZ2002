package sg.ntu.core.util.hardware;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;

public class AccountUtil {

    public static Account queryAccountManager(Context context) {
        Account result = null;
        AccountManager am = AccountManager.get(context);
        Account[] accounts = am.getAccountsByType("com.google");
        if (accounts != null) {
            if (accounts.length > 0) {
                for (Account account : accounts) {
                    if (account.name.contains("@gmail.com")) {
                        result = account;
                        break;
                    }
                }
            }
        }

        return result;
    }
}
