package transport;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public final class HttpResponse {

    private final int statusCode;
    private final String statusMessage;

    private final String content;

    private final Long consulIndex;
    private final Boolean consulKnownLeader;
    private final Long consulLastContact;
}

