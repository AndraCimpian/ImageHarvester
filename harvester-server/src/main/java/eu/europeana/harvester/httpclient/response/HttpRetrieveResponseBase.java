package eu.europeana.harvester.httpclient.response;

import eu.europeana.harvester.domain.ResponseHeader;
import net.logstash.logback.marker.MapEntriesAppendingMarker;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

abstract class HttpRetrieveResponseBase implements HttpRetrieveResponse {

    /**
     * The exception that caused the {@enum ResponseState.ERROR} state.
     */
    private Throwable exception;

    /**
     * The current state of the response.
     */
    private RetrievingState state = RetrievingState.PREPARING;

    /**
     * The url from where the data was retrieved.
     */
    private URL url;

    /**
     * The response headers.
     */
    private Map<String, String> responseHeaders = new HashMap<String, String>();

    /**
     * The size of the content in bytes. Should always be used instead of getting the content size as, depending
     * on the storage strategy, loading the content might be very expensive.
     */
    protected Long contentSizeInBytes = 0l;

    /**
     * The HTTP response code.
     */
    private Integer httpResponseCode = -1;

    /**
     * The HTTP response content type.
     */
    private String httpResponseContentType = "";

    /**
     * The duration in milliseconds between the socket connection and the first content bytes coming in.
     * This is relevant as it indicates the amount of time the server spends to simply make the resource
     * available. For example a resource coming from a CDN will have this very low and one coming from a
     * slow database will be rather large. Zero if the source is not retrieved.
     */
    private Long socketConnectToDownloadStartDurationInMilliSecs = 0l;

    /**
     * The retrieval duration in milliseconds. Zero if the source is not retrieved.
     */
    private Long retrievalDurationInMilliSecs = 0l;

    /**
     * The checking duration in milliseconds. The same as the retrieval if the source is retrieved.
     */
    private Long checkingDurationInMilliSecs = 0l;

    /**
     * The IP of the source. Useful for debugging when working with DNS load balanced sources that have a pool of real
     * IP's for the same domain name.
     */
    private String sourceIp = "";

    /**
     * List of redirect links.
     */
    private List<String> redirectionPath = new ArrayList<String>();

    /**
     * Stores error messages.
     */
    private String log = "";

    protected MapEntriesAppendingMarker loggingMarker;

    @Override
    synchronized public RetrievingState getState() {
        return state;
    }

    @Override
    synchronized public void setState(RetrievingState state) {
        this.state = state;
    }

    @Override
    synchronized public URL getUrl() {
        return url;
    }

    @Override
    synchronized public void setUrl(URL url) {
        this.url = url;
    }

    @Override
    synchronized public Map<String, String> getResponseHeaders() {
        return (responseHeaders==null ? new HashMap<String, String>() : responseHeaders);
    }

    @Override
    synchronized public void addHeader(String type, String value) {
        type = type.replace("%", "");
        type = type.replace("$", "");
        type = type.replace(".", "");
        value = value.replace("%", "");
        value = value.replace("$", "");
        value = value.replace(".", "");

        responseHeaders.put(type, value);
    }

    @Override
    synchronized public Long getContentSizeInBytes() {
        return contentSizeInBytes;
    }

    @Override
    synchronized public Throwable getException() {
        return exception;
    }

    @Override
    synchronized public void setException(Throwable exception) {
        this.exception = exception;
    }

    @Override
    synchronized public void close() throws IOException {
        // NO IMPLEMENTATION as by default most responses don't need to be closed.
    }

    @Override
    public Integer getHttpResponseCode() {
        return (httpResponseCode == null ? -1 : httpResponseCode);
    }

    @Override
    public void setHttpResponseCode(Integer httpResponseCode) {
        this.httpResponseCode = httpResponseCode;
    }

    @Override
    public String getHttpResponseContentType() {
        return (httpResponseContentType==null ? "" : httpResponseContentType);
    }

    @Override
    public void setHttpResponseContentType(String httpResponseContentType) {
        this.httpResponseContentType = httpResponseContentType;
    }

    @Override
    public Long getSocketConnectToDownloadStartDurationInMilliSecs() {
        return socketConnectToDownloadStartDurationInMilliSecs;
    }

    @Override
    public void setSocketConnectToDownloadStartDurationInMilliSecs(Long socketConnectToDownloadStartDurationInMilliSecs) {
        this.socketConnectToDownloadStartDurationInMilliSecs = socketConnectToDownloadStartDurationInMilliSecs;
    }

    @Override
    public Long getRetrievalDurationInMilliSecs() {
        return retrievalDurationInMilliSecs;
    }

    @Override
    public void setRetrievalDurationInMilliSecs(Long retrievalDurationInMilliSecs) {
        this.retrievalDurationInMilliSecs = retrievalDurationInMilliSecs;
    }

    @Override
    public Long getCheckingDurationInMilliSecs() {
        return checkingDurationInMilliSecs;
    }

    @Override
    public void setCheckingDurationInMilliSecs(Long checkingDurationInMilliSecs) {
        this.checkingDurationInMilliSecs = checkingDurationInMilliSecs;
    }

    @Override
    public String getSourceIp() {
        return sourceIp;
    }

    @Override
    public void setSourceIp(String sourceIp) {
        this.sourceIp = sourceIp;
    }

    @Override
    public List<String> getRedirectionPath() {
        return (redirectionPath == null ? new ArrayList<String>() : redirectionPath);
    }

    @Override
    public void setRedirectionPath(List<String> redirectionPath) {
        this.redirectionPath = redirectionPath;
    }

    @Override
    public void addRedirectionPath(String redirectionPath) {
        this.redirectionPath.add(redirectionPath);
    }

    @Override
    public String getLog() {
        return log;
    }

    @Override
    public void setLog(String log) {
        this.log = log;
    }

    @Override
    public void setLoggingAppFields (MapEntriesAppendingMarker mapEntriesAppendingMarker) {
        this.loggingMarker = mapEntriesAppendingMarker;
    }
}
