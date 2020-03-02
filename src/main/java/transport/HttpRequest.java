package transport;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Builder
@Getter
@Setter
public class HttpRequest {

    private final String url;
    private final Map<String, String> headers;

    private final String content;
    private final byte[] binaryContent;
}
