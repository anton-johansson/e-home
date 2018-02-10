/**
 * Copyright 2017 Anton Johansson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.anton.ehome.common;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.OngoingStubbing;

/**
 * Abstract skeleton for unit tests.
 */
public abstract class AbstractTest extends Assert
{
    @Before
    public void setUp() throws Exception
    {
        MockitoAnnotations.initMocks(this);
        initMocks();
    }

    @After
    public void tearDown() throws Exception
    {
    }

    /**
     * Initializes mocks.
     */
    protected void initMocks() throws Exception
    {
    }

    /**
     * @see Mockito#mock(Class)
     */
    protected final <T> T mock(Class<T> classToMock)
    {
        return Mockito.mock(classToMock);
    }

    /**
     * @see ArgumentMatchers#any()
     */
    protected final <T> T any()
    {
        return ArgumentMatchers.any();
    }

    /**
     * @see ArgumentMatchers#eq(Object)
     */
    protected final <T> T eq(T value)
    {
        return ArgumentMatchers.eq(value);
    }

    /**
     * @see Mockito#when(Object)
     */
    protected final <T> OngoingStubbing<T> when(T methodCall)
    {
        return Mockito.when(methodCall);
    }
}
