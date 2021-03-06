package oauth2.auth;

import config.DropwizardConfiguration;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.client.JerseyClientConfiguration;
import io.dropwizard.jetty.ConnectorFactory;
import io.dropwizard.jetty.HttpsConnectorFactory;
import io.dropwizard.setup.Environment;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.glassfish.jersey.SslConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.File;
import java.io.FileInputStream;
import java.net.Socket;
import java.security.KeyStore;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Created by FRAMGIA\dinh.thanh on 24/05/2018.
 */
public class RestClientBuilder extends JerseyClientBuilder {
    private final static Logger log = LoggerFactory.getLogger(RestClientBuilder.class);

    private String keyStore = null;
    private String keyStorePassword = null;
    private String trustStore = null;
    private String trustStorePassword = null;

    public RestClientBuilder(Environment env, DropwizardConfiguration cfg) {
        super(env);
        JerseyClientConfiguration conf = cfg.getJerseyClientConfiguration();

        // force gzip to false
        conf.setGzipEnabled(false);

        using(conf);
        setupSSL(cfg);
    }

    public RestClientBuilder setupSSL(DropwizardConfiguration cfg) {
        SSLContext sslContext;
        ConnectorFactory factory = cfg.getClientConfig();

        if (factory == null || !(factory instanceof HttpsConnectorFactory))
            return this;

        HttpsConnectorFactory hcf = (HttpsConnectorFactory) factory;

        if (hcf.getKeyStorePath() != null) {
            keyStore = hcf.getKeyStorePath();
            keyStorePassword = hcf.getKeyStorePassword();
            trustStore = hcf.getTrustStorePath();
            trustStorePassword = hcf.getTrustStorePassword();

            sslContext = getSSLContext();
        } else {
            SslConfigurator sslConfig = SslConfigurator.newInstance();
            sslContext = sslConfig.createSSLContext();
        }

        SSLConnectionSocketFactory sslConnectionSocketFactory = hcf.isValidateCerts() ?
                new SSLConnectionSocketFactory(sslContext) :
                new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("https", sslConnectionSocketFactory).build();
        using(registry);

        return this;
    }

    private SSLContext getSSLContext() {
        TrustManager trustManagers[] = null;
        KeyManager keyManagers[] = null;

        try {
            if (trustStore != null)
                trustManagers = new TrustManager[] {
                        new MyX509TrustManager(trustStore, trustStorePassword.toCharArray()) };
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            if (keyStore != null)
                keyManagers = new KeyManager[] { new MyX509KeyManager(keyStore, keyStorePassword.toCharArray()) };
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        SSLContext ctx = null;
        try {
            ctx = SSLContext.getInstance("SSL");
            ctx.init(keyManagers, trustManagers, null);
        } catch (java.security.GeneralSecurityException ex) {
            log.error("Error setting SSL configuration", ex);
        }
        return ctx;
    }

    /**
     * Taken from http://java.sun.com/javase/6/docs/technotes/guides/security/jsse/JSSERefGuide.html
     */
    static class MyX509TrustManager implements X509TrustManager {

        /*
         * The default PKIX X509TrustManager9.  We'll delegate
         * decisions to it, and fall back to the logic in this class if the
         * default X509TrustManager doesn't trust it.
         */ X509TrustManager pkixTrustManager;

        MyX509TrustManager(String trustStore, char[] password) throws Exception {
            this(new File(trustStore), password);
        }

        MyX509TrustManager(File trustStore, char[] password) throws Exception {
            // create a "default" JSSE X509TrustManager.

            KeyStore ks = KeyStore.getInstance("JKS");

            ks.load(new FileInputStream(trustStore), password);

            TrustManagerFactory tmf = TrustManagerFactory.getInstance("PKIX");
            tmf.init(ks);

            TrustManager tms[] = tmf.getTrustManagers();

            /*
             * Iterate over the returned trustmanagers, look
             * for an instance of X509TrustManager.  If found,
             * use that as our "default" trust manager.
             */
            for (int i = 0; i < tms.length; i++) {
                if (tms[i] instanceof X509TrustManager) {
                    pkixTrustManager = (X509TrustManager) tms[i];
                    return;
                }
            }

            /*
             * Find some other way to initialize, or else we have to fail the
             * constructor.
             */
            throw new Exception("Couldn't initialize");
        }

        /*
         * Delegate to the default trust manager.
         */
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            try {
                pkixTrustManager.checkClientTrusted(chain, authType);
            } catch (CertificateException ex) {
                // do any special handling here, or rethrow exception.
                log.debug("Error setting SSL configuration", ex);
            }
        }

        /*
         * Delegate to the default trust manager.
         */
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            try {
                pkixTrustManager.checkServerTrusted(chain, authType);
            } catch (CertificateException ex) {
                /*
                 * Possibly pop up a dialog box asking whether to trust the
                 * cert chain.
                 */
                log.debug("Error setting SSL configuration", ex);
            }
        }

        /*
         * Merely pass this through.
         */
        public X509Certificate[] getAcceptedIssuers() {
            return pkixTrustManager.getAcceptedIssuers();
        }
    }

    /**
     * Inspired from http://java.sun.com/javase/6/docs/technotes/guides/security/jsse/JSSERefGuide.html
     */
    static class MyX509KeyManager implements X509KeyManager {

        /*
         * The default PKIX X509KeyManager.  We'll delegate
         * decisions to it, and fall back to the logic in this class if the
         * default X509KeyManager doesn't trust it.
         */ X509KeyManager pkixKeyManager;

        MyX509KeyManager(String keyStore, char[] password) throws Exception {
            this(new File(keyStore), password);
        }

        MyX509KeyManager(File keyStore, char[] password) throws Exception {
            // create a "default" JSSE X509KeyManager.

            KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(new FileInputStream(keyStore), password);

            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509", "SunJSSE");
            kmf.init(ks, password);

            KeyManager kms[] = kmf.getKeyManagers();

            /*
             * Iterate over the returned keymanagers, look
             * for an instance of X509KeyManager.  If found,
             * use that as our "default" key manager.
             */
            for (int i = 0; i < kms.length; i++) {
                if (kms[i] instanceof X509KeyManager) {
                    pkixKeyManager = (X509KeyManager) kms[i];
                    return;
                }
            }

            /*
             * Find some other way to initialize, or else we have to fail the
             * constructor.
             */
            throw new Exception("Couldn't initialize");
        }

        public PrivateKey getPrivateKey(String arg0) {
            return pkixKeyManager.getPrivateKey(arg0);
        }

        public X509Certificate[] getCertificateChain(String arg0) {
            return pkixKeyManager.getCertificateChain(arg0);
        }

        public String[] getClientAliases(String arg0, Principal[] arg1) {
            return pkixKeyManager.getClientAliases(arg0, arg1);
        }

        public String chooseClientAlias(String[] arg0, Principal[] arg1, Socket arg2) {
            return pkixKeyManager.chooseClientAlias(arg0, arg1, arg2);
        }

        public String[] getServerAliases(String arg0, Principal[] arg1) {
            return pkixKeyManager.getServerAliases(arg0, arg1);
        }

        public String chooseServerAlias(String arg0, Principal[] arg1, Socket arg2) {
            return pkixKeyManager.chooseServerAlias(arg0, arg1, arg2);
        }
    }
}