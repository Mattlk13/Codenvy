/*
 *  [2012] - [2017] Codenvy, S.A.
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.resource.api.free;

import com.codenvy.resource.model.FreeResourcesLimit;
import com.codenvy.resource.shared.dto.FreeResourcesLimitDto;
import com.codenvy.resource.shared.dto.ResourceDto;
import com.codenvy.resource.spi.FreeResourcesLimitDao;
import com.codenvy.resource.spi.impl.FreeResourcesLimitImpl;
import com.codenvy.resource.spi.impl.ResourceImpl;

import org.eclipse.che.api.core.Page;
import org.eclipse.che.dto.server.DtoFactory;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.testng.MockitoTestNGListener;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static java.util.Collections.singletonList;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

/**
 * Tests for {@link FreeResourcesLimitManager}
 *
 * @author Sergii Leschenko
 */
@Listeners(MockitoTestNGListener.class)
public class FreeResourceManagerTest {
    private static final String TEST_RESOURCE_TYPE = "Test";

    @Mock
    private FreeResourcesLimitDao freeResourcesLimitDao;

    @InjectMocks
    private FreeResourcesLimitManager manager;

    @Test
    public void shouldStoreFreeResourcesLimit() throws Exception {
        //given
        ResourceImpl resource = new ResourceImpl(TEST_RESOURCE_TYPE,
                                                 1,
                                                 "unit");
        FreeResourcesLimitImpl resourcesLimitImpl = new FreeResourcesLimitImpl("account123", singletonList(resource));

        ResourceDto resourceDto = DtoFactory.newDto(ResourceDto.class)
                                            .withAmount(1)
                                            .withType(TEST_RESOURCE_TYPE)
                                            .withUnit("unit");
        FreeResourcesLimitDto freeResourcesLimitDto = DtoFactory.newDto(FreeResourcesLimitDto.class)
                                                                .withAccountId("account123")
                                                                .withResources(singletonList(resourceDto));

        //when
        FreeResourcesLimit storedLimit = manager.store(freeResourcesLimitDto);

        //then
        assertEquals(storedLimit, resourcesLimitImpl);
        verify(freeResourcesLimitDao).store(resourcesLimitImpl);
    }

    @Test(expectedExceptions = NullPointerException.class,
          expectedExceptionsMessageRegExp = "Required non-null free resources limit")
    public void shouldThrowNpeOnStoringNullableFreeResourcesLimit() throws Exception {
        //when
        manager.store(null);
    }

    @Test
    public void shouldReturnFreeResourcesLimitForSpecifiedAccount() throws Exception {
        //given
        ResourceImpl resource = new ResourceImpl(TEST_RESOURCE_TYPE,
                                                 1,
                                                 "unit");
        FreeResourcesLimitImpl resourcesLimitImpl = new FreeResourcesLimitImpl("account123", singletonList(resource));

        when(freeResourcesLimitDao.get(any())).thenReturn(resourcesLimitImpl);

        //when
        FreeResourcesLimit fetchedLimit = manager.get("account123");

        //then
        assertEquals(fetchedLimit, resourcesLimitImpl);
        verify(freeResourcesLimitDao).get("account123");
    }

    @Test(expectedExceptions = NullPointerException.class,
          expectedExceptionsMessageRegExp = "Required non-null account id")
    public void shouldThrowNpeOnGettingFreeResourcesLimitByNullableAccountId() throws Exception {
        //when
        manager.get(null);
    }

    @Test
    public void shouldRemoveFreeResourcesLimitForSpecifiedAccount() throws Exception {
        //when
        manager.remove("account123");

        //then
        verify(freeResourcesLimitDao).remove("account123");
    }

    @Test(expectedExceptions = NullPointerException.class,
          expectedExceptionsMessageRegExp = "Required non-null account id")
    public void shouldThrowNpeOnRemovingFreeResourcesLimitByNullableAccountId() throws Exception {
        //when
        manager.remove(null);
    }

    @Test
    public void shouldReturnFreeResourcesLimits() throws Exception {
        //given
        ResourceImpl resource = new ResourceImpl(TEST_RESOURCE_TYPE,
                                                 1,
                                                 "unit");
        FreeResourcesLimitImpl resourcesLimitImpl = new FreeResourcesLimitImpl("account123", singletonList(resource));

        when(freeResourcesLimitDao.getAll(anyInt(), anyInt()))
                .thenReturn(new Page<>(singletonList(resourcesLimitImpl), 5, 1, 9));

        //when
        Page<? extends FreeResourcesLimit> fetchedLimits = manager.getAll(1, 5);

        //then
        assertEquals(fetchedLimits.getTotalItemsCount(), 9);
        assertEquals(fetchedLimits.getSize(), 1);
        assertEquals(fetchedLimits.getItems().get(0), resourcesLimitImpl);
        verify(freeResourcesLimitDao).getAll(1, 5);
    }
}
