package test;

import javax.inject.Inject;

/**
 * Created by haswell on 10/25/16.
 */
public class DefaultTestService implements TestService {
    
    @Inject
    private Holder holder;

    @Override
    public String sayHelloWithInjection(String name) {
        return holder.name + name;
    }

    @Override
    public String sayHello(String name) {
        if("die".equals(name)) {
            throw new RuntimeException("Ded");
        }
        return "Hello " + name;
    }

    @Override
    public String sayHelloXML(String name) {
        if("die".equals(name)) {
            throw new RuntimeException("Ded");
        }
        return "Hello " + name;
    }
}
