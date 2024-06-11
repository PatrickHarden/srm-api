package com.scholastic.srmapi.service.support;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;

import com.scholastic.srmapi.model.User;
import com.scholastic.srmapi.model.UserLoginSession;
import com.scholastic.srmapi.repository.UserLoginSessionRepository;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import com.scholastic.srmapi.service.LtiService;
import com.scholastic.srmapi.util.Constants;
import com.scholastic.srmapi.util.Encoding;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class LtiServiceImpl implements LtiService {
    private static final String OAUTH_SIGNATURE = "oauth_signature";
    private static final String OAUTH_CONSUMER_KEY = "oauth_consumer_key";
    private static final String HMAC_SHA1 = "HmacSHA1";
    private static final String HTTP_POST_METHOD = HttpMethod.POST.toString();
    private static final String AMPERSAND = "&";

    private final Environment env;

    @Value("${serverRootUrl}")
    private String srmWebRootUrl;

    private final UserLoginSessionRepository userSessionRepository;

    @Override
    public boolean validateLtiRequest(HttpServletRequest request) {
        var signature = request.getParameter(OAUTH_SIGNATURE);
        var requestParametersMap = getSortedRequestParametersMap(request);
        var ltiLaunchUrl = srmWebRootUrl + (srmWebRootUrl.endsWith(Constants.ROOT) ? "lti" : Constants.LTI);
        try {
            return validateOAuthSignature(requestParametersMap, ltiLaunchUrl, signature);
        } catch (InvalidKeyException | NoSuchAlgorithmException | UnsupportedEncodingException e) {
            log.error("Error validating oauth signature: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Creates a sorted key map and flattened version of a request's parameters
     * 
     * @param request the {@code HttpServletRequest} request
     * @return a {@code SortedMap<String, String>} with keys sorted
     */
    private SortedMap<String, String> getSortedRequestParametersMap(HttpServletRequest request) {
        var flatSortedRequestParametersMap = new TreeMap<String, String>(); // using TreeMap to ensure keys are sorted

        for (var entry : request.getParameterMap().entrySet()) {
            var key = entry.getKey();
            var values = entry.getValue();

            // ignore oauth signature key
            if (!key.equals(OAUTH_SIGNATURE)) {
                var value = values[values.length - 1]; // flatten map by only using last value of array
                if (value.indexOf('\r') > -1) {
                    // remove carriage return characters
                    value = value.replaceAll("(?:\\r)", "");
                }
                flatSortedRequestParametersMap.put(key, value);
            }
        }

        return flatSortedRequestParametersMap;
    }

    /**
     * OAuth validation using parameters, launchUrl and signature
     * 
     * @param parametersMap the {@code SortedMap<String, String>} request parameters
     *                      excluding oauth signature
     * @param launchUrl     the url used for the LTI POST request
     * @param signature     the signature to validate against
     * @return {@code true} if successful oauth validation {@code false} otherwise
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws UnsupportedEncodingException
     */
    private boolean validateOAuthSignature(SortedMap<String, String> parametersMap, String launchUrl,
            String signature) throws InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {
        var consumerKey = parametersMap.get(OAUTH_CONSUMER_KEY);
        var secret = Optional.ofNullable(env.getProperty(consumerKey)).orElse("");
        var signedSignature = sign(secret, launchUrl, parametersMap);
        var isValid = signature.equals(signedSignature);

        if (!isValid) {
            var signatureBaseString = getSignatureBaseString(HTTP_POST_METHOD, launchUrl, parametersMap);
            var errorResponse = new StringBuilder().append("**** Oauth Failed on lti launch\n")
                    .append("**** OauthConsumerKey They Sent: " + consumerKey + "\n")
                    .append("**** Local Secret configured for OauthConsumerKey: " + secret + "\n")
                    .append("**** Signature locally calculated: " + signedSignature + "\n")
                    .append("**** Signature SDM calculated: " + signature + "\n")
                    .append("**** URL used : " + launchUrl + "\n")
                    .append("**** Signature Base String used: " + signatureBaseString + "\n");
            log.error(errorResponse.toString());
        }

        return isValid;
    }

    /**
     * Authentication using HMAC and signing of passed parameters using SHA1 hash
     * function
     * 
     * @param secret     secret key to use
     * @param url        url used to launch LTI POST request
     * @param parameters {@code SortedMap<String, String>} of LTI request parameters
     * @return signature {@code String}
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws UnsupportedEncodingException
     */
    private String sign(String secret, String url, SortedMap<String, String> parameters)
            throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
        // build secret key
        var secretKey = secret.concat(AMPERSAND);
        var secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), HMAC_SHA1);
        var mac = Mac.getInstance(secretKeySpec.getAlgorithm());
        mac.init(secretKeySpec);

        // build message to hash
        var signatureBaseString = getSignatureBaseString(HTTP_POST_METHOD, url, parameters);
        var hash = mac.doFinal(signatureBaseString.getBytes(StandardCharsets.UTF_8));
        // base64 encode hash
        var encodedHash = Base64.encodeBase64(hash);

        return new String(encodedHash, StandardCharsets.UTF_8);
    }

    /**
     * Builds the LTI launch message to hash
     * 
     * @param method     HTTP method {@code String}
     * @param url        the launch url
     * @param parameters {@code SortedMap<String, String>} of the request parameters
     * @return the message string to hash
     * @throws UnsupportedEncodingException
     */
    private String getSignatureBaseString(String method, String url, SortedMap<String, String> parameters)
            throws UnsupportedEncodingException {
        var signatureBase = new StringBuilder().append(Encoding.percentEncode(method)).append(AMPERSAND)
                .append(Encoding.percentEncode(url)).append(AMPERSAND);
        var count = 0;

        for (var entry : parameters.entrySet()) {
            signatureBase.append(Encoding.percentEncode(Encoding.percentEncode(entry.getKey())))
                    .append(Encoding.urlEncode("="))
                    .append(Encoding.percentEncode(Encoding.percentEncode(entry.getValue())));
            if (++count < parameters.size()) {
                signatureBase.append(Encoding.urlEncode(AMPERSAND));
            }
        }

        return signatureBase.toString();
    }

    @Override
    public Long createUserSession(String sourceId) {
        var session = new UserLoginSession();
        session.setSourceId(Long.parseLong(sourceId));
        return userSessionRepository.save(session).getId();
    }
}
