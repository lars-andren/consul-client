package transport;

import model.ConsulResponse;

public interface ConsulResponseCallback<T> {

    void onComplete(ConsulResponse<T> consulResponse);

    void onFailure(Throwable throwable);
}