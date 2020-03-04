package se.cloudcharge.consul.exception;

import lombok.Getter;
import okhttp3.ResponseBody;
import retrofit2.Response;

import java.io.IOException;

@Getter
public class ConsulException extends RuntimeException {

    private int code;

    public ConsulException(String message) {
        super(message);
    }

    public ConsulException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public ConsulException(int code, Response<?> response) {
        super(String.format("Consul request failed with status [%s]: %s",
                code, message(response)));
        this.code = code;
    }

    public ConsulException(Throwable throwable) {
        super("Consul request failed", throwable);
    }

    static String message(Response response) {
        try {
            ResponseBody responseBody = response.errorBody();
            return responseBody == null ? response.message() : responseBody.string();
        } catch (IOException e) {
            return response.message();
        }
    }
}
