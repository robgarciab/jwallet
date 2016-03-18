package com.katari.jwallet.web.rest.util;

import org.springframework.http.HttpHeaders;

/**
 * Utility class for http header creation.
 *
 */
public class HeaderUtil {

    public static HttpHeaders createAlert(String message, String param) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-jwalletApp-alert", message);
        headers.add("X-jwalletApp-params", param);
        return headers;
    }

    public static HttpHeaders createEntityActionAlert(String entityName, EntityAction action, String param) {
        return createAlert("jwalletApp." + entityName + "." + action.toString().toLowerCase(), param);
    }

    public static HttpHeaders createFailureAlert(String param, String errorKey, String defaultMessage) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-jwalletApp-error", "error." + errorKey);
        headers.add("X-jwalletApp-params", param);
        return headers;
    }
}
