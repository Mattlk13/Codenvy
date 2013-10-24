/*
 *
 * CODENVY CONFIDENTIAL
 * ________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 * NOTICE: All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any. The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */

package com.codenvy.analytics.metrics;

import com.codenvy.analytics.metrics.value.LongValueData;
import com.codenvy.analytics.metrics.value.ValueData;
import com.codenvy.analytics.metrics.value.ValueDataFactory;

import java.io.IOException;
import java.text.ParseException;
import java.util.Map;
import java.util.Set;

/**
 * The value of the metric will be calculated as: previous value + added value - removed value.
 *
 * @author <a href="mailto:abazko@codenvy.com">Anatoliy Bazko</a>
 */
public abstract class CumulativeMetric extends AbstractMetric {

    private final InitialValueContainer iValueContainer;
    private final Metric                addedMetric;
    private final Metric                removedMetric;

    CumulativeMetric(MetricType metricType, Metric addedMetric, Metric removedMetric) {
        super(metricType);

        this.iValueContainer = InitialValueContainer.getInstance();
        this.addedMetric = addedMetric;
        this.removedMetric = removedMetric;
    }

    /** {@inheritDoc} */
    @Override
    public Set<Parameters> getParams() {
        Set<Parameters> params = addedMetric.getParams();
        params.addAll(removedMetric.getParams());
        params.remove(Parameters.FROM_DATE);

        return params;
    }

    /** {@inheritDoc} */
    @Override
    public ValueData getValue(Map<String, String> context) throws InitialValueNotFoundException, IOException {
        try {

            context = Utils.clone(context);
            Parameters.FROM_DATE.put(context, Parameters.TO_DATE.get(context));
            Parameters.TIME_UNIT.put(context, TimeUnit.DAY.name());

            validateExistenceInitialValueBefore(context);

            try {
                ValueData initalValue = iValueContainer.getInitalValue(metricType, makeUUID(context).toString());

                if (!Utils.getAvailableFilters(context).isEmpty()) {
                    return ValueDataFactory.createDefaultValue(getValueDataClass());
                } else {
                    return initalValue;
                }
            } catch (InitialValueNotFoundException e) {
                // ignoring, may be next time lucky
            }

            LongValueData addedEntities = (LongValueData)addedMetric.getValue(context);
            LongValueData removedEntities = (LongValueData)removedMetric.getValue(context);

            Map<String, String> prevDayContext = Utils.prevDateInterval(context);

            LongValueData previousEntities = (LongValueData)getValue(prevDayContext);

            return new LongValueData(
                    previousEntities.getAsLong() + addedEntities.getAsLong() - removedEntities.getAsLong());
        } catch (ParseException e) {
            throw new IOException(e);
        }

    }

    protected void validateExistenceInitialValueBefore(Map<String, String> context)
            throws InitialValueNotFoundException, ParseException {
        iValueContainer.validateExistenceInitialValueBefore(metricType, context);
    }

    /** {@inheritDoc} */
    @Override
    public Class<? extends ValueData> getValueDataClass() {
        return LongValueData.class;
    }
}
