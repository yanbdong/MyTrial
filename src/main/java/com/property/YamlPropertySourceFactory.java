package com.property;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.DefaultPropertySourceFactory;
import org.springframework.core.io.support.EncodedResource;

import java.io.IOException;

/**
 * @author yanbdong@cienet.com.cn
 * @since Nov 19, 2020
 */
//@Import(YamlPropertySourceFactory.class)
class YamlPropertySourceFactory extends DefaultPropertySourceFactory {

    @Autowired
    private YamlPropertySourceLoader yamlPropertySourceLoader = new YamlPropertySourceLoader();

    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource resource) throws IOException {
        String sourceName = name == null ? resource.getResource().getURI().getRawPath() : name;
        for (String extension : yamlPropertySourceLoader.getFileExtensions()) {
            if (sourceName.endsWith(extension)) {
                return yamlPropertySourceLoader.load(resource.toString(), resource.getResource()).get(0);
            }
        }
        return super.createPropertySource(name, resource);
    }
}
