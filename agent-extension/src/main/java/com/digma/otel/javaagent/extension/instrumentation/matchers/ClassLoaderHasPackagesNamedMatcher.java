
package com.digma.otel.javaagent.extension.instrumentation.matchers;

import io.opentelemetry.instrumentation.api.internal.cache.Cache;
import io.opentelemetry.javaagent.bootstrap.internal.ClassLoaderMatcherCacheHolder;
import io.opentelemetry.javaagent.bootstrap.internal.InClassLoaderMatcher;
import net.bytebuddy.matcher.ElementMatcher;

import java.util.BitSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;

public class ClassLoaderHasPackagesNamedMatcher extends ElementMatcher.Junction.AbstractBase<ClassLoader> {
    //todo: see comment bellow, check if we need to disable for build time muzzle checks
    //caching is disabled for build time muzzle checks
    //this field is set via reflection from ClassLoaderMatcher
    private static boolean useCache = true;
    private static final AtomicInteger counter = new AtomicInteger();
    // each matcher gets a unique index that is used for caching the matching status
    private final int index = counter.getAndIncrement();

    private final String[] resources;

    public ClassLoaderHasPackagesNamedMatcher(List<String> packageNames) {
        resources = packageNames.stream().distinct().map(new Function<String, String>() {
            @Override
            public String apply(String s) {
                return s.replace(".", "/");
            }
        }).toArray(String[]::new);
    }

    @Override
    public boolean matches(ClassLoader cl) {
        if (cl == null) {
            // Can't match the bootstrap class loader.
            return false;
        }
        if (useCache) {
            return MyCacheManager.INSTANCE.match(this, cl);
        } else {
            return hasAnyResources(cl, resources);
        }
    }

    private static boolean hasAnyResources(ClassLoader cl, String[] resources) {
        debug("in hasAnyResources for %s", cl);
        boolean priorValue = InClassLoaderMatcher.getAndSet(true);
        boolean hasAnyResource = false;
        try {
            for (String resource : resources) {
                if (cl.getResource(resource) != null) {
                    hasAnyResource = true;
                }
            }
        } finally {
            InClassLoaderMatcher.set(priorValue);
        }
        debug("hasAnyResources for %s is %s", cl, hasAnyResource);
        return hasAnyResource;
    }

    private static class MyCacheManager {
        static final MyCacheManager INSTANCE = new MyCacheManager();
        // each matcher gets a two bits in BitSet, that first bit indicates whether current matcher has
        // been run for given class loader and the second whether it matched or not
        private final Cache<ClassLoader, BitSet> enabled = Cache.weak();
        private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        private final Lock readLock = lock.readLock();
        private final Lock writeLock = lock.writeLock();

        private MyCacheManager() {
            debug("in MyCacheManager constructor");
            ClassLoaderMatcherCacheHolder.addCache(enabled);
        }

        boolean match(ClassLoaderHasPackagesNamedMatcher matcher, ClassLoader cl) {
            BitSet set = enabled.computeIfAbsent(cl, (unused) -> new BitSet(counter.get() * 2));
            int matcherRunBit = 2 * matcher.index;
            int matchedBit = matcherRunBit + 1;
            readLock.lock();
            try {
                if (!set.get(matcherRunBit)) {
                    // read lock needs to be released before upgrading to write lock
                    readLock.unlock();
                    // we do the resource presence check outside the lock to keep the time we need to hold
                    // the write lock minimal
                    boolean matches = hasAnyResources(cl, matcher.resources);
                    writeLock.lock();
                    try {
                        if (!set.get(matcherRunBit)) {
                            if (matches) {
                                set.set(matchedBit);
                            }
                            set.set(matcherRunBit);
                        }
                    } finally {
                        // downgrading the write lock to the read lock
                        readLock.lock();
                        writeLock.unlock();
                    }
                }

                return set.get(matchedBit);
            } finally {
                readLock.unlock();
            }
        }
    }

    private static void debug(String format, Object... args) {
        if (Boolean.getBoolean("otel.javaagent.debug")) {
            System.out.println("ClassLoaderHasPackagesNamedMatcher: " + String.format(format, args));
        }
    }
}
