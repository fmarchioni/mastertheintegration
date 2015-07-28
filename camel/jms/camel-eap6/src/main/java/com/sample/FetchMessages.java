package com.sample;

import java.util.Properties;
import javax.jms.ConnectionFactory;
import javax.naming.Context;
import javax.naming.InitialContext;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class FetchMessages {

    private static final String DEFAULT_CONNECTION_FACTORY = "jms/RemoteConnectionFactory";

    private static final String DEFAULT_USERNAME = "jmsuser";
    private static final String DEFAULT_PASSWORD = "Password1!";
    private static final String INITIAL_CONTEXT_FACTORY = "org.jboss.naming.remote.client.InitialContextFactory";
    private static final String PROVIDER_URL = "remote://localhost:4447";

    /**
     * A main() so we can easily run these routing rules in our IDE
     */
    public static void main(String... args) throws Exception {
        CamelContext context = new DefaultCamelContext();

        ConnectionFactory connectionFactory = null;
        final Properties env = new Properties();
        env.put(Context.INITIAL_CONTEXT_FACTORY, INITIAL_CONTEXT_FACTORY);
        env.put(Context.PROVIDER_URL, System.getProperty(Context.PROVIDER_URL, PROVIDER_URL));
        env.put(Context.SECURITY_PRINCIPAL, DEFAULT_USERNAME);
        env.put(Context.SECURITY_CREDENTIALS, DEFAULT_PASSWORD);
        Context ctx = new InitialContext(env);

        connectionFactory = (ConnectionFactory) ctx.lookup(DEFAULT_CONNECTION_FACTORY);

        org.apache.camel.component.jms.JmsComponent jms = new org.apache.camel.component.jms.JmsComponent();

        jms.setConnectionFactory(connectionFactory);

        context.addComponent("test-jms", jms);

        context.addRoutes(new RouteBuilder() {
            public void configure() {
                from("test-jms:queue:testQueue").to("stream:out");
            }
        });

        context.start();

        Thread.sleep(2000);
        context.stop();
    }

}
