package se.cloudcharge.consul;

import se.cloudcharge.consul.cache.CacheConfig;
import se.cloudcharge.consul.exception.ConsulException;
import se.cloudcharge.consul.model.Value;
import se.cloudcharge.consul.monitoring.ClientEventCallback;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.*;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

import static se.cloudcharge.consul.util.Util.trimLeadingSlash;

public class KeyValueClient extends BaseClient {

    private static String CLIENT_NAME = "keyvalue";
    public static final int NOT_FOUND_404 = 404;

    private final Api api;

    KeyValueClient(Retrofit retrofit, CacheConfig config, ClientEventCallback eventCallback) {
        super(CLIENT_NAME, config, eventCallback);
        this.api = retrofit.create(Api.class);
    }

    public Optional<Value> getValue(String key) {
        try {
            return getSingleValue(http.extract(api.getValue(trimLeadingSlash(key)), NOT_FOUND_404));
        } catch (ConsulException ignored) {
            if(ignored.getCode() != NOT_FOUND_404) {
                throw ignored;
            }
        }

        return Optional.empty();
    }

    private Optional<Value> getSingleValue(List<Value> values) {
        if (values != null && !values.isEmpty()) {
            Value value = values.get(0);
            byte[] decodedBytes = Base64.getDecoder().decode(value.getValue());
            String decodedString = new String(decodedBytes);
            value.setValue(decodedString);
            return Optional.of(value);
        } else {
            return Optional.empty();
        }
    }

    public boolean putValue(String key, String value) {
        return putValue(key, value.getBytes());
    }

    public boolean putValue(String key, byte[] value) {

        return http.extract(api.putValue(trimLeadingSlash(key),
                    RequestBody.create(MediaType.parse("application/octet-stream"), value)));

    }

    public void deleteKey(String key) {
        http.handle(api.deleteValues(trimLeadingSlash(key)));
    }


    interface Api {

        @GET("kv/{key}")
        Call<List<Value>> getValue(@Path("key") String key);

        @PUT("kv/{key}")
        Call<Boolean> putValue(@Path("key") String key);

        @PUT("kv/{key}")
        Call<Boolean> putValue(@Path("key") String key,
                               @Body RequestBody data);

        @DELETE("kv/{key}")
        Call<Void> deleteValues(@Path("key") String key);

    }

}
