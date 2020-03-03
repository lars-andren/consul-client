import cache.CacheConfig;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.io.BaseEncoding;
import com.google.common.net.HostAndPort;
import lombok.Getter;
import lombok.Setter;
import monitoring.ClientEventCallback;
import okhttp3.Dispatcher;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.internal.Util;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Getter
@Setter
public class ConsulConnector {

    public static final String DEFAULT_HTTP_HOST = "localhost";

    public static final int DEFAULT_HTTP_PORT = 8500;

    private final AgentClient agentClient;
    private final KeyValueClient keyValueClient;
    private final ExecutorService executorService;
    private final OkHttpClient okHttpClient;

    private ConsulConnector(AgentClient agentClient, KeyValueClient keyValueClient, ExecutorService executorService,
                   OkHttpClient okHttpClient) {
        this.agentClient = agentClient;
        this.keyValueClient = keyValueClient;
        this.executorService = executorService;
        this.okHttpClient = okHttpClient;
    }

    public void destroy() {
        this.okHttpClient.dispatcher().cancelAll();
        this.executorService.shutdownNow();
    }

    public static Builder builder() {
        return new Builder();
    }

    @VisibleForTesting
    public static ConsulConnector newClient() {
        return builder().build();
    }

    public static class Builder {
        private URL url;
        private SSLContext sslContext;
        private X509TrustManager trustManager;
        private boolean ping = true;
        private Interceptor basicAuthInterceptor;
        private Long connectTimeoutMillis;
        private Long readTimeoutMillis;
        private Long writeTimeoutMillis;
        private ExecutorService executorService;
        private CacheConfig clientConfig;
        private ClientEventCallback clientEventCallback;

        {
            try {
                url = new URL("http", DEFAULT_HTTP_HOST, DEFAULT_HTTP_PORT, "");
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }

        public Builder withUrl(URL url) {
            this.url = url;

            return this;
        }

        public Builder withPing(boolean ping) {
            this.ping = ping;

            return this;
        }

        public Builder withBasicAuth(String username, String password) {
            String credentials = username + ":" + password;
            final String basic = "Basic " + BaseEncoding.base64().encode(credentials.getBytes());
            basicAuthInterceptor = chain -> {
                Request original = chain.request();

                Request.Builder requestBuilder = original.newBuilder()
                        .header("Authorization", basic)
                        .method(original.method(), original.body());

                Request request = requestBuilder.build();
                return chain.proceed(request);
            };

            return this;
        }

        public Builder withHostAndPort(HostAndPort hostAndPort) {
            try {
                this.url = new URL("http", hostAndPort.getHost(), hostAndPort.getPort(), "");
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }

            return this;
        }

        public Builder withUrl(String url) {
            try {
                this.url = new URL(url);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }

            return this;
        }

        public Builder withSslContext(SSLContext sslContext) {
            this.sslContext = sslContext;
            return this;
        }

        public Builder withTrustManager(X509TrustManager trustManager) {
            this.trustManager = trustManager;
            return this;
        }

        public Builder withConnectTimeoutMillis(long timeoutMillis) {
            Preconditions.checkArgument(timeoutMillis >= 0, "Negative value");
            this.connectTimeoutMillis = timeoutMillis;
            return this;
        }

        public Builder withReadTimeoutMillis(long timeoutMillis) {
            Preconditions.checkArgument(timeoutMillis >= 0, "Negative value");
            this.readTimeoutMillis = timeoutMillis;
            return this;
        }

        public Builder withWriteTimeoutMillis(long timeoutMillis) {
            Preconditions.checkArgument(timeoutMillis >= 0, "Negative value");
            this.writeTimeoutMillis = timeoutMillis;
            return this;
        }

        public Builder withExecutorService(ExecutorService executorService) {
            this.executorService = executorService;
            return this;
        }

        public Builder withClientConfiguration(CacheConfig clientConfig) {
            this.clientConfig = clientConfig;
            return this;
        }

        public Builder withClientEventCallback(ClientEventCallback callback) {
            this.clientEventCallback = callback;
            return this;
        }

        public ConsulConnector build() {
            final Retrofit retrofit;

            ExecutorService executorService = this.executorService;
            if (executorService == null) {
                executorService = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60, TimeUnit.SECONDS,
                        new SynchronousQueue<>(), Util.threadFactory("OkHttp Dispatcher", true));
            }

            CacheConfig config = (clientConfig != null) ? clientConfig : CacheConfig.builder().build();

            OkHttpClient okHttpClient = createOkHttpClient(
                    this.sslContext,
                    this.trustManager,
                    executorService,
                    config);

            try {
                retrofit = createRetrofit(buildUrl(this.url), okHttpClient);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }

            ClientEventCallback eventCallback = clientEventCallback != null ?
                    clientEventCallback :
                    new ClientEventCallback(){};

            AgentClient agentClient = new AgentClient(retrofit, config, eventCallback);
            KeyValueClient keyValueClient = new KeyValueClient(retrofit, config, eventCallback);

            if (ping) {
                agentClient.ping();
            }
            return new ConsulConnector(agentClient, keyValueClient, executorService, okHttpClient);
        }

        private String buildUrl(URL url) {
            return url.toExternalForm().replaceAll("/$", "") + "/v1/";
        }

        private OkHttpClient createOkHttpClient(SSLContext sslContext, X509TrustManager trustManager,
                                                ExecutorService executorService, CacheConfig clientConfig) {

            final OkHttpClient.Builder builder = new OkHttpClient.Builder();

            if (basicAuthInterceptor != null) { builder.addInterceptor(basicAuthInterceptor); }

            if (sslContext != null && trustManager != null) {
                builder.sslSocketFactory(sslContext.getSocketFactory(), trustManager);
            } else if (sslContext != null) {
                builder.sslSocketFactory(sslContext.getSocketFactory());
            }

            if (connectTimeoutMillis != null) { builder.connectTimeout(connectTimeoutMillis, TimeUnit.MILLISECONDS); }

            if (readTimeoutMillis != null) { builder.readTimeout(readTimeoutMillis, TimeUnit.MILLISECONDS); }

            if (writeTimeoutMillis != null) { builder.writeTimeout(writeTimeoutMillis, TimeUnit.MILLISECONDS); }

          //  builder.addInterceptor(new TimeoutInterceptor(clientConfig));

            Dispatcher dispatcher = new Dispatcher(executorService);
            dispatcher.setMaxRequests(Integer.MAX_VALUE);
            dispatcher.setMaxRequestsPerHost(Integer.MAX_VALUE);
            builder.dispatcher(dispatcher);
            return builder.build();
        }

        private Retrofit createRetrofit(String url, OkHttpClient okHttpClient) throws MalformedURLException {

            final URL consulUrl = new URL(url);

            return new Retrofit.Builder()
                    .baseUrl(new URL(consulUrl.getProtocol(), consulUrl.getHost(),
                            consulUrl.getPort(), consulUrl.getFile()).toExternalForm())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();
        }

    }
}
