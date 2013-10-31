package com.betfair.cougar.transport.impl.protocol;

import com.betfair.cougar.api.export.Protocol;
import com.betfair.cougar.api.security.IdentityTokenResolver;
import com.betfair.cougar.transport.api.protocol.ProtocolBinding;
import com.betfair.cougar.transport.api.protocol.ProtocolBindingRegistry;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 *
 */
public class ProtocolBindingHelperTest {

    @Mock
    private ProtocolBindingRegistry registry;

    private Protocol protocol = Protocol.RESCRIPT;

    private String contextRoot = "/wibble";

    @Mock
    private IdentityTokenResolver<?, ?, ?> identityTokenResolver;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void enabled() throws Exception {

        ProtocolBindingHelper helper = new ProtocolBindingHelper();
        helper.setContextRoot(contextRoot);
        helper.setIdentityTokenResolver(identityTokenResolver);
        helper.setProtocol(protocol);
        helper.setRegistry(registry);

        helper.init();

        verify(registry, times(1)).addProtocolBinding(argThat(new BaseMatcher<ProtocolBinding>() {
            @Override
            public boolean matches(Object o) {
                if (o instanceof ProtocolBinding) {
                    ProtocolBinding pb = (ProtocolBinding) o;
                    if (pb.getIdentityTokenResolver() != identityTokenResolver) {
                        return false;
                    }
                    if (pb.getContextRoot() != contextRoot) {
                        return false;
                    }
                    if (pb.getProtocol() != protocol) {
                        return false;
                    }
                    return true;
                }
                return false;
            }

            @Override
            public void describeTo(Description description) {
            }
        }));
    }

    @Test
    public void disabled() throws Exception {
        ProtocolBindingHelper helper = new ProtocolBindingHelper();
        helper.setContextRoot(contextRoot);
        helper.setIdentityTokenResolver(identityTokenResolver);
        helper.setProtocol(protocol);
        helper.setRegistry(registry);
        helper.setEnabled(false);

        helper.init();

        verify(registry, times(0)).addProtocolBinding(any(ProtocolBinding.class));
    }
}
