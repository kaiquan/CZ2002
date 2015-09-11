package sg.ntu.core.util;

import java.util.UUID;

public class GoogleUtils {

    /**
     * Use to convert guid of race meeting to product id race meeting of
     * google-play in app billing
     *
     * @param guid
     * @return
     */
    public static String convertToSKU(String guid) {
        if (guid == null) {
            return null;
        }

        // convert to lower case
        guid = guid.toLowerCase();
        // replace - to _
        guid = guid.replaceAll("-", "_");
        return guid;
    }

    /**
     * Convert sku to guid of race meeting
     *
     * @param sku
     * @return
     */
    public static String convertToGuid(String sku) {
        String productId = "";
        sku = sku.toUpperCase();

        String[] arr = sku.split("[_]");
        if (arr.length == 4) {
            productId = arr[0] + "-" + arr[1] + "-" + arr[2] + "_" + arr[3];
        }
        return productId;
    }

    /**
     * Random generate purchase string to verify when purchase
     *
     * @return
     */
    public static String generatePurchaseString() {
        String uuid = UUID.randomUUID().toString();
        return uuid;
    }
}
