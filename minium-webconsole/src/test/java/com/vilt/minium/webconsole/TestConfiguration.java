/*
 * Copyright (C) 2013 The Minium Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vilt.minium.webconsole;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.os.CommandLine;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestConfiguration implements DisposableBean {

    private PhantomJSDriverService service;
    
    @Value("#{systemProperties['minium.remote.url']}")
    private String remoteUrl;
    
    @SuppressWarnings("deprecation")
    @Bean(name = "remoteWebDriverUrl")
    public URL remoteWebDriverUrl() throws IOException {
        if (StringUtils.isEmpty(remoteUrl)) {
            service = new PhantomJSDriverService.Builder()
                .usingPhantomJSExecutable(new File(CommandLine.find("phantomjs")))
                .usingCommandLineArguments(new String[] { "--webdriver-loglevel=ERROR" })
                .withLogFile(new File(System.getProperty("java.io.tmpdir"), "phantomjsdriver.log"))
                .build();
            service.start();
            return service.getUrl();
        }
        else {
            return new URL(remoteUrl);
        }
    }
    
    @Override
    public void destroy() throws Exception {
        if (service != null) {
            service.stop();
        }
    }
    
}
