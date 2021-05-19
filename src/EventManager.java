import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Project: EventBus
 * -----------------------------------------------------------
 * Copyright © 2020-2021 | Enaium | All rights reserved.
 */
public class EventManager {

    private final HashMap<Class<? extends Listeners>, CopyOnWriteArrayList<MethodBean>> events = new HashMap<>();

    public void register(Object o) {
        Class<?> type = o.getClass();

        for (Method method : type.getDeclaredMethods()) {
            if (method.getParameterTypes().length == 1 && method.isAnnotationPresent(Event.class)) {
                method.setAccessible(true);
                @SuppressWarnings("unchecked")
                Class<? extends Listeners> listener = (Class<? extends Listeners>) method.getParameterTypes()[0];

                MethodBean methodBean = new MethodBean(o, method);

                if (events.containsKey(listener)) {
                    if (!events.get(listener).contains(methodBean)) {
                        events.get(listener).add(methodBean);
                    }
                } else {
                    events.put(listener, new CopyOnWriteArrayList<>(Collections.singletonList(methodBean)));
                }
            }
        }
    }

    public void unregister(Object o) {
        events.values().forEach(methodBeans -> methodBeans.removeIf(methodMethodBean -> methodMethodBean.getObject().equals(o)));
        events.entrySet().removeIf(event -> event.getValue().isEmpty());
    }

    public CopyOnWriteArrayList<MethodBean> getEvent(Class<? extends Listeners> type) {
        return events.get(type);
    }
}
