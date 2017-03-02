package pl.librus.client.api;

public class HttpException extends RuntimeException {
    private final int code;

    public HttpException(Throwable cause, String url) {
        super(String.format("Request to URL %s failed", url), cause);
        this.code = -1;
    }

    public HttpException(int code, String message, String url) {
        super(String.format("Request to URL %s failed with code %d and message: %s", url, code, message));
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
