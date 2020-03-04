package se.cloudcharge.consul.transport;

import se.cloudcharge.consul.model.ConsulResponse;

public interface ConsulResponseCallback<T> {

    void onComplete(ConsulResponse<T> consulResponse);

    void onFailure(Throwable throwable);
}