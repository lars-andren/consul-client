package se.sparklemuffin.consul.transport;

import se.sparklemuffin.consul.model.ConsulResponse;

public interface ConsulResponseCallback<T> {

    void onComplete(ConsulResponse<T> consulResponse);

    void onFailure(Throwable throwable);
}