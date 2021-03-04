/*************************************************************************
 *
 * CIeNET CONFIDENTIAL
 * __________________
 *
 *  CIeNET Technologies
 *  All Rights Reserved.
 *
 * NOTICE:  All source codes contained herein are, and remain
 * the property of CIeNET Technologies. The intellectual and technical concepts contained
 * herein are proprietary to CIeNET Technologies
 * and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from CIeNET Technologies.
 *************************************************************************/

package com.eventbus;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class JamonStaticResourcesWebConfiguration implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/html/**").addResourceLocations("classpath:/static/");
    }
}
