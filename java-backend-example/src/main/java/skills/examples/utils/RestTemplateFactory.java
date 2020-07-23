/**
 * Copyright 2020 SkillTree
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package skills.examples.utils;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
public class RestTemplateFactory {

    @Autowired
    SkillsConfig skillsConfig;


    public RestTemplate getTemplateWithAuth() {
        RestTemplate restTemplate = new RestTemplate();
        if (!skillsConfig.getAuthMode().equalsIgnoreCase("pki")) {
            // must configure HttpComponentsClientHttpRequestFactory as SpringTemplate does
            // not by default keeps track of session
            restTemplate.setRequestFactory(getHttpRequestFactory());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("username", skillsConfig.getUsername());
            params.add("password", skillsConfig.getPassword());

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(skillsConfig.getServiceUrl() + "/performLogin", request, String.class);
            assert response.getStatusCode() == HttpStatus.OK;
        }
        return restTemplate;
    }

    private ClientHttpRequestFactory getHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        HttpClient httpClient = HttpClientBuilder.create().setSSLHostnameVerifier(new AllowAllHostnameVerifier()).build();
        clientHttpRequestFactory.setHttpClient(httpClient);
        return clientHttpRequestFactory;
    }
}
