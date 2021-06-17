package se.sparklemuffin.consul;

import se.sparklemuffin.consul.cache.CacheConfig;
import com.google.common.collect.ImmutableMap;
import se.sparklemuffin.consul.exception.ConsulException;
import se.sparklemuffin.consul.exception.NotRegisteredException;
import se.sparklemuffin.consul.model.Check;
import se.sparklemuffin.consul.model.Service;
import se.sparklemuffin.consul.model.State;
import se.sparklemuffin.consul.monitoring.ClientEventCallback;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.*;

import java.util.Collections;
import java.util.Map;

public class AgentClient extends BaseClient {

    private static String CLIENT_NAME = "agent";

    private final Api api;

    AgentClient(Retrofit retrofit, CacheConfig config, ClientEventCallback eventCallback) {
        super(CLIENT_NAME, config, eventCallback);
        this.api = retrofit.create(Api.class);
    }

    public boolean isRegistered(String serviceId) {
        Map<String, Service> serviceIdToService = getServices();
        return serviceIdToService.containsKey(serviceId);
    }

    public void register(Service service) { http.handle(api.register(service)); }

    public void deregister(String serviceId) { http.handle(api.deregister(serviceId)); }

    public void registerCheck(Check check) { http.handle(api.registerCheck(check)); }

    public void deregisterCheck(String checkId) { http.handle(api.deregisterCheck(checkId)); }

    public Map<String, Check> getChecks() { return http.extract(api.getChecks()); }

    public Map<String, Service> getServices() { return http.extract(api.getServices()); }

    public void check(String checkId, State state, String note) throws NotRegisteredException {
        try {
            Map<String, String> query = note == null ? Collections.emptyMap() : ImmutableMap.of("note", note);

            http.handle(api.check(state.getPath(), checkId, query));
        } catch (Exception ex) {
            throw new NotRegisteredException("Error checking state", ex);
        }
    }

    public void passCheck(String checkId) throws NotRegisteredException { check(checkId, State.PASS, null); }

    public void passCheck(String checkId, String note) throws NotRegisteredException { check(checkId, State.PASS, note); }

    public void warnCheck(String checkId) throws NotRegisteredException { check(checkId, State.WARN, null); }

    public void warnCheck(String checkId, String note) throws NotRegisteredException { check(checkId, State.WARN, note); }

    public void failCheck(String checkId) throws NotRegisteredException { check(checkId, State.FAIL, null); }

    public void failCheck(String checkId, String note) throws NotRegisteredException { check(checkId, State.FAIL, note); }

    public boolean join(String address, boolean wan) {
        Map<String, String> query = wan ? ImmutableMap.of("wan", "1") : Collections.emptyMap();
        boolean result = true;

        try {
            http.handle(api.join(address, query));
        } catch(Exception ex) {
            result = false;
        }

        return result;
    }

    public void toggleMaintenanceMode(String serviceId, boolean enable) {
        http.handle(api.toggleMaintenanceMode(serviceId,
                ImmutableMap.of("enable", Boolean.toString(enable))));
    }

    public void toggleMaintenanceMode(String serviceId,
                                      boolean enable,
                                      String reason) {
        http.handle(api.toggleMaintenanceMode(serviceId,
                ImmutableMap.of("enable", Boolean.toString(enable),
                        "reason", reason)));
    }

    public void ping() {
        try {
            retrofit2.Response<Void> response = api.ping().execute();

            if (!response.isSuccessful()) {
                throw new ConsulException(String.format("Error pinging Consul: %s",
                        response.message()));
            }
        } catch (Exception ex) {
            throw new ConsulException("Error connecting to Consul", ex);
        }
    }

    interface Api {

        @PUT("agent/service/register")
        Call<Void> register(@Body Service service);

        @PUT("agent/service/deregister/{serviceId}")
        Call<Void> deregister(@Path("serviceId") String serviceId);

        @PUT("agent/check/register")
        Call<Void> registerCheck(@Body Check check);

        @PUT("agent/check/deregister/{checkId}")
        Call<Void> deregisterCheck(@Path("checkId") String checkId);

        @GET("agent/self")
        Call<Void> ping();

        @GET("agent/checks")
        Call<Map<String, Check>> getChecks();

        @GET("agent/services")
        Call<Map<String, Service>> getServices();

        @PUT("agent/force-leave")
        Call<Void> forceLeave();

        @PUT("agent/check/{state}/{checkId}")
        Call<Void> check(@Path("state") String state,
                         @Path("checkId") String checkId,
                         @QueryMap Map<String, String> query);

        @PUT("agent/join/{address}")
        Call<Void> join(String address, Map<String, String> query);

        @PUT("agent/service/maintenance/{serviceId}")
        Call<Void> toggleMaintenanceMode(@Path("serviceId") String serviceId,
                                         @QueryMap Map<String, String> query);
    }
}