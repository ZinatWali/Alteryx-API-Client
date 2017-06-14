package foo;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.RandomStringUtils;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.TreeMap;

// Apache Commons Libraries used for the Nonce &amp; Base64
// Bouncy Castle Libraries used for CMAC encryption

/**
 * Very basic sample code that demonstrates how to make an OAuth 1.0 System-to-System
 * request to the LearningStudio API
 */
public class OAuth1 {
    /**
     * Generates a random nonce
     *
     * @return  A unique identifier for the request
     */
    public String getNonce()
    {
        return RandomStringUtils.randomNumeric(9);
    }

    /**
     * Generates an integer representing the number of seconds since the unix epoch using the
     * date/time the request is issued
     *
     * @return  A timestamp for the request
     */
    public String getTimestamp()
    {
        return Long.toString((System.currentTimeMillis() / 1000));
    }

    /**
     * Generates an OAuth 1.0 signature
     *
     * @param   httpMethod  The HTTP method of the request
     * @param   stringUrl     The request URL
     * @param   oauthParams The associative set of signable oAuth parameters
     * @param   secret    Alphanumeric string used to validate the identity of the education partner (Private Key)
     *
     * @return  A string containing the Base64-encoded signature digest
     *
     * @throws  UnsupportedEncodingException
     */
    public String generateSignature(
            String httpMethod,
            String stringUrl,
            Map<String, String> oauthParams,
            String secret
    ) throws UnsupportedEncodingException, MalformedURLException
    {
        // Ensure the HTTP Method is upper-cased
        httpMethod = httpMethod.toUpperCase();

        // Construct the URL-encoded OAuth parameter portion of the signature base string
        String encodedParams = normalizeParams(oauthParams);

        // URL-encode the relative URL
        String encodedUri = URLEncoder.encode(stringUrl, "UTF-8");

        // Build the signature base string to be signed with the Consumer Secret
        String baseString = String.format("%s&%s&%s", httpMethod, encodedUri, encodedParams);

        return generateHmac(secret+ "&", baseString);
    }

    /**
     * Normalizes all OAuth signable parameters and url query parameters according to OAuth 1.0
     *
     * @param   oauthParams The associative set of signable oAuth parameters
     *
     * @return  A string containing normalized and encoded oAuth parameters
     *
     * @throws  UnsupportedEncodingException
     */
    private String normalizeParams(
            Map<String, String> oauthParams
    ) throws UnsupportedEncodingException
    {
        // Sort the parameters in lexicographical order, 1st by Key then by Value
        Map<String, String> kvpParams = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
        kvpParams.putAll(oauthParams);

        // separate the key and values with a "="
        // separate the kvp with a "&"
        StringBuilder combinedParams = new StringBuilder();
        String delimiter="";
        for(String key : kvpParams.keySet()) {
            combinedParams.append(delimiter);
            combinedParams.append(key);
            combinedParams.append("=");
            combinedParams.append(kvpParams.get(key));
            delimiter="&";
        }

        // url encode the entire string again before returning
        return URLEncoder.encode(combinedParams.toString(), "UTF-8");
    }

    private String generateHmac(String key, String msg)
            throws UnsupportedEncodingException
    {
        HMac macProvider = new HMac(new SHA1Digest());
        byte[] output = new byte[macProvider.getMacSize()];
        byte[] keyBytes = key.getBytes("UTF-8");
        byte[] data = msg.getBytes("UTF-8");

        macProvider.init(new KeyParameter(keyBytes));
        macProvider.update(data, 0, data.length);
        macProvider.doFinal(output, 0);

        // Convert the HMAC to a Base64 string and remove the new line the Base64 library adds
        String hmac = Base64.encodeBase64String(output).replaceAll("\r\n", "");

        return hmac;
    }
}