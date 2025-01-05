package org.example.fiveletters.poiskslov.service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.DefaultClientTlsStrategy;
import org.apache.hc.client5.http.ssl.HostnameVerificationPolicy;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.net.URIBuilder;
import org.apache.hc.core5.ssl.SSLContexts;

public class Crawler {

    private static final String BASE_URL = "https://поиск-слов.рф/suschestvitelnye/5";

    private final CloseableHttpClient httpClient;

    public Crawler() {
        // Пришлось отказаться от валидации хостов в сертификатах,
        // потому что в Apache HttpClient 5 сломана валидация IDN-хостов
        // https://issues.apache.org/jira/browse/HTTPCLIENT-2353
        httpClient = HttpClients.custom()
            .setConnectionManager(
                PoolingHttpClientConnectionManagerBuilder.create()
                    .setTlsSocketStrategy(
                        new DefaultClientTlsStrategy(
                            SSLContexts.createDefault(),
                            HostnameVerificationPolicy.BOTH,
                            NoopHostnameVerifier.INSTANCE
                        )
                    )
                    .build()
            )
            .build();
    }

    public String getHtml(char letter, int page) throws IOException, URISyntaxException {
        URI uri = new URIBuilder(BASE_URL)
            .appendPath(String.valueOf(letter))
            .addParameter("f", "nar")
            .addParameter("page", String.valueOf(page))
            .build();

        HttpGet request = new HttpGet(uri);

        return httpClient.execute(
            request,
            response -> EntityUtils.toString(response.getEntity())
        );
    }
}
