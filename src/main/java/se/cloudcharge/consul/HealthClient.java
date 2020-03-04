package se.cloudcharge.consul;

import se.cloudcharge.consul.cache.CacheConfig;
import se.cloudcharge.consul.model.Check;
import se.cloudcharge.consul.model.ConsulResponse;
import se.cloudcharge.consul.model.Service;
import se.cloudcharge.consul.model.State;
import se.cloudcharge.consul.monitoring.ClientEventCallback;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.util.List;

public class HealthClient extends BaseClient {

    private static String CLIENT_NAME = "health";

    private final Api api;

    HealthClient(Retrofit retrofit, CacheConfig config, ClientEventCallback eventCallback) {
        super(CLIENT_NAME, config, eventCallback);
        this.api = retrofit.create(Api.class);
    }

    public ConsulResponse<List<Check>> getServiceChecks(String service) {
        return http.extractConsulResponse(api.getServiceChecks(service));
    }

    public ConsulResponse<List<Check>> getChecksByState(State state) {
        return http.extractConsulResponse(api.getChecksByState(state.getName()));
    }

    public ConsulResponse<List<Service>> getAllServiceInstances(String service) {
        return http.extractConsulResponse(api.getServiceInstances(service));
    }

    interface Api {

        @GET("health/checks/{service}")
        Call<List<Check>> getServiceChecks(@Path("service") String service);

        @GET("health/state/{state}")
        Call<List<Check>> getChecksByState(@Path("state") String state);

        @GET("health/service/{service}")
        Call<List<Service>> getServiceInstances(@Path("service") String service);
    }
}
