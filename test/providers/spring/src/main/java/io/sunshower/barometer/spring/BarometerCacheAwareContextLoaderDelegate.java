package io.sunshower.barometer.spring;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.CacheAwareContextLoaderDelegate;
import org.springframework.test.context.ContextLoader;
import org.springframework.test.context.MergedContextConfiguration;
import org.springframework.test.context.SmartContextLoader;
import org.springframework.test.context.cache.ContextCache;
import org.springframework.test.context.cache.DefaultContextCache;
import org.springframework.util.Assert;

/**
 * Created by haswell on 11/1/16.
 */
public class BarometerCacheAwareContextLoaderDelegate implements CacheAwareContextLoaderDelegate {


    private static final Log logger = LogFactory.getLog(BarometerCacheAwareContextLoaderDelegate.class);

    static final ContextCache defaultContextCache = new DefaultContextCache();

    private final ContextCache contextCache;


    public BarometerCacheAwareContextLoaderDelegate() {
        this(defaultContextCache);
    }

    public BarometerCacheAwareContextLoaderDelegate(ContextCache contextCache) {
        Assert.notNull(contextCache, "ContextCache must not be null");
        this.contextCache = contextCache;
    }

    protected ContextCache getContextCache() {
        return this.contextCache;
    }

    protected ApplicationContext loadContextInternal(MergedContextConfiguration mergedContextConfiguration)
            throws Exception {

        ContextLoader contextLoader = mergedContextConfiguration.getContextLoader();
        Assert.notNull(contextLoader, "Cannot load an ApplicationContext with a NULL 'contextLoader'. " +
                "Consider annotating your test class with @ContextConfiguration or @ContextHierarchy.");

        ApplicationContext applicationContext;

        if (contextLoader instanceof SmartContextLoader) {
            SmartContextLoader smartContextLoader = (SmartContextLoader) contextLoader;
            applicationContext = smartContextLoader.loadContext(mergedContextConfiguration);
        }
        else {
            String[] locations = mergedContextConfiguration.getLocations();
            Assert.notNull(locations, "Cannot load an ApplicationContext with a NULL 'locations' array. " +
                    "Consider annotating your test class with @ContextConfiguration or @ContextHierarchy.");
            applicationContext = contextLoader.loadContext(locations);
        }

        return applicationContext;
    }

    @Override
    public ApplicationContext loadContext(MergedContextConfiguration mergedContextConfiguration) {
        synchronized (this.contextCache) {
            ApplicationContext context = this.contextCache.get(mergedContextConfiguration);
            if (context == null) {
                try {
                    context = loadContextInternal(mergedContextConfiguration);
                    if (logger.isDebugEnabled()) {
                        logger.debug(String.format("Storing ApplicationContext in cache under key [%s]",
                                mergedContextConfiguration));
                    }
                    this.contextCache.put(mergedContextConfiguration, context);
                }
                catch (Exception ex) {
                    throw new IllegalStateException("Failed to load ApplicationContext", ex);
                }
            }
            else {
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("Retrieved ApplicationContext from cache with key [%s]",
                            mergedContextConfiguration));
                }
            }

            this.contextCache.logStatistics();

            return context;
        }
    }

    @Override
    public void closeContext(MergedContextConfiguration mergedContextConfiguration, DirtiesContext.HierarchyMode hierarchyMode) {
        synchronized (this.contextCache) {
            this.contextCache.remove(mergedContextConfiguration, hierarchyMode);
        }
    }

}
