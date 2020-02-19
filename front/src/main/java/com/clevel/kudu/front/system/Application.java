package com.clevel.kudu.front.system;

import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

@ApplicationScoped
@Named
public class Application {
    @Inject
    private Logger log;

    @Inject
    public Application() {
    }

    @PostConstruct
    public void onCreation() {
        log.debug("onCreation.");
    }

}
