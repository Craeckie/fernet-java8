/**
   Copyright 2018 Carlos Macasaet

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       https://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package com.macasaet.fernet.jersey;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.function.Function;

import javax.ws.rs.NotAuthorizedException;

import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.model.Parameter;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import com.macasaet.fernet.Key;
import com.macasaet.fernet.Token;
import com.macasaet.fernet.jaxrs.FernetToken;


public class FernetTokenValueParamProviderTest {

    @Spy
    private TokenHeaderUtility tokenHeaderUtility = new TokenHeaderUtility();

    @InjectMocks
    private FernetTokenValueParamProvider provider;

    @Mock
    private Parameter parameter;
    @Mock
    private Key key;
    @Mock
    private Token token;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private Function<ContainerRequest, Token> function;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        doReturn(Token.class).when(parameter).getRawType();
        given(parameter.isAnnotationPresent(FernetToken.class)).willReturn(true);
        function = provider.getValueProvider(parameter);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public final void verifyApplyReturnsTokenFromBearerToken() {
        // given
        final ContainerRequest request = mock(ContainerRequest.class);
        doReturn(token).when(tokenHeaderUtility).getAuthorizationToken(request);
        doReturn(null).when(tokenHeaderUtility).getXAuthorizationToken(request);

        // when
        final Token result = function.apply(request);

        // then
        assertEquals(token.serialise(), result.serialise());
    }

    @Test
    public final void verifyApplyReturnsTokenFromXToken() {
        // given
        final ContainerRequest request = mock(ContainerRequest.class);
        doReturn(null).when(tokenHeaderUtility).getAuthorizationToken(request);
        doReturn(token).when(tokenHeaderUtility).getXAuthorizationToken(request);

        // when
        final Token result = function.apply(request);

        // then
        assertEquals(token.serialise(), result.serialise());
    }

    @Test
    public final void verifyApplyThrowsNotAuthorized() {
        // given
        final ContainerRequest request = mock(ContainerRequest.class);

        // when / then
        thrown.expect(NotAuthorizedException.class);
        function.apply(request);
    }

}