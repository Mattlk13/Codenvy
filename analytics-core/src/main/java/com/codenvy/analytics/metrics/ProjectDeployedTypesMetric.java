/*
 * Copyright (C) 2013 Codenvy.
 */
package com.codenvy.analytics.metrics;

import com.codenvy.analytics.metrics.value.LongValueData;
import com.codenvy.analytics.metrics.value.ValueData;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/** @author <a href="mailto:abazko@codenvy.com">Anatoliy Bazko</a> */
public class ProjectDeployedTypesMetric extends ValueReadBasedMetric {

    public ProjectDeployedTypesMetric() {
        super(MetricType.PROJECT_DEPLOYED_TYPES);
    }

    /** {@inheritDoc} */
    @Override
    public Set<Parameters> getParams() {
        return new LinkedHashSet<>(
                Arrays.asList(new Parameters[]{Parameters.FROM_DATE,
                                                    Parameters.TO_DATE,
                                                    Parameters.PARAM}));
    }

    @Override
    public String getDescription() {
        return "The number of deployed projects on specific PaaS";
    }

    /** {@inheritDoc} */
    @Override
    public Class<? extends ValueData> getValueDataClass() {
        return LongValueData.class;
    }
}
