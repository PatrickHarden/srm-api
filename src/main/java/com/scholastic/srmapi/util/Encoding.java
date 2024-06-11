package com.scholastic.srmapi.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public final class Encoding {
    // prevent instantiation
    private Encoding() {
    }

    /**
     * URL encodes value
     * 
     * @param value {@code String} to url encode
     * @return the url encoded {@code String} of value
     * @throws UnsupportedEncodingException
     * @see {@link #percentEncode(String)}
     */
    public static String urlEncode(String value) throws UnsupportedEncodingException {
        return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
    }

    /**
     * Percent encodes value
     * <p>
     * Compared to {@link #urlEncode}, it also percent encodes the following
     * characters: {@code +*~}
     * </p>
     * 
     * @param value {@code String} to percent encode
     * @return the percent encoded {@code String} of value
     * @throws UnsupportedEncodingException
     * @see {@link #urlEncode(String)}
     */
    public static String percentEncode(String value) throws UnsupportedEncodingException {
        return urlEncode(value).replace("+", "%20").replace("*", "%2A").replace("~", "%7E");
    }
}
