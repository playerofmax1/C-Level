package com.clevel.kudu.model;

import java.util.EventListener;

public abstract class KuduEventListener implements EventListener {

    public abstract void eventPerformed(Object... params);

}
