package com.vcinsidedigital.webcore.validation;

import com.vcinsidedigital.webcore.WebServerApplication;
import com.vcinsidedigital.webcore.annotations.Plugin;
import com.vcinsidedigital.webcore.extensibility.AnnotationHandlerRegistry;
import com.vcinsidedigital.webcore.plugin.AbstractPlugin;
import com.vcinsidedigital.webcore.validation.handlers.ValidHandler;

@Plugin
public class ValidationPlugin extends AbstractPlugin
{
    @Override
    public void onLoad(WebServerApplication application) {
        AnnotationHandlerRegistry registry = AnnotationHandlerRegistry.getInstance();
        registry.registerParameterHandler(new ValidHandler());
    }

    @Override
    public void onStart(WebServerApplication application) {

    }

    @Override
    public String getId() {
        return "com.vcinsidedigital.webcore.validation";
    }

    @Override
    public String getName() {
        return "Validation";
    }

    @Override
    public String getVersion() {
        return "1.0.1";
    }
}
