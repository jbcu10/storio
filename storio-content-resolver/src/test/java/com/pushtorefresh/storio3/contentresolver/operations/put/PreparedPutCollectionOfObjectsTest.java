package com.pushtorefresh.storio3.contentresolver.operations.put;

import android.content.ContentValues;

import com.pushtorefresh.storio3.StorIOException;
import com.pushtorefresh.storio3.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio3.contentresolver.operations.SchedulerChecker;
import com.pushtorefresh.storio3.contentresolver.queries.InsertQuery;
import com.pushtorefresh.storio3.contentresolver.queries.UpdateQuery;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.Collection;
import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import io.reactivex.subscribers.TestSubscriber;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(Enclosed.class)
public class PreparedPutCollectionOfObjectsTest {

    public static class WithoutTypeMapping {

        @Test
        public void shouldPutObjectsWithoutTypeMappingBlocking() {
            final PutObjectsStub putStub = PutObjectsStub.newPutStubForMultipleObjectsWithoutTypeMapping();

            final PutResults<TestItem> putResults = putStub.storIOContentResolver
                    .put()
                    .objects(putStub.items)
                    .withPutResolver(putStub.putResolver)
                    .prepare()
                    .executeAsBlocking();

            putStub.verifyBehaviorForMultipleObjects(putResults);
        }

        @Test
        public void shouldPutObjectsWithoutTypeMappingAsFlowable() {
            final PutObjectsStub putStub = PutObjectsStub.newPutStubForMultipleObjectsWithoutTypeMapping();

            final Flowable<PutResults<TestItem>> flowable = putStub.storIOContentResolver
                    .put()
                    .objects(putStub.items)
                    .withPutResolver(putStub.putResolver)
                    .prepare()
                    .asRxFlowable(BackpressureStrategy.MISSING);

            putStub.verifyBehaviorForMultipleObjects(flowable);
        }

        @Test
        public void shouldPutObjectsWithoutTypeMappingAsSingle() {
            final PutObjectsStub putStub = PutObjectsStub.newPutStubForMultipleObjectsWithoutTypeMapping();

            final Single<PutResults<TestItem>> single = putStub.storIOContentResolver
                    .put()
                    .objects(putStub.items)
                    .withPutResolver(putStub.putResolver)
                    .prepare()
                    .asRxSingle();

            putStub.verifyBehaviorForMultipleObjects(single);
        }

        @Test
        public void shouldPutObjectsWithoutTypeMappingAsCompletable() {
            final PutObjectsStub putStub = PutObjectsStub.newPutStubForMultipleObjectsWithoutTypeMapping();

            final Completable completable = putStub.storIOContentResolver
                    .put()
                    .objects(putStub.items)
                    .withPutResolver(putStub.putResolver)
                    .prepare()
                    .asRxCompletable();

            putStub.verifyBehaviorForMultipleObjects(completable);
        }
    }

    public static class WithTypeMapping {

        @Test
        public void shouldPutObjectsWithTypeMappingBlocking() {
            final PutObjectsStub putStub = PutObjectsStub.newPutStubForMultipleObjectsWithTypeMapping();

            final PutResults<TestItem> putResults = putStub.storIOContentResolver
                    .put()
                    .objects(putStub.items)
                    .prepare()
                    .executeAsBlocking();

            putStub.verifyBehaviorForMultipleObjects(putResults);
        }

        @Test
        public void shouldPutObjectsWithTypeMappingAsFlowable() {
            final PutObjectsStub putStub = PutObjectsStub.newPutStubForMultipleObjectsWithTypeMapping();

            final Flowable<PutResults<TestItem>> flowable = putStub.storIOContentResolver
                    .put()
                    .objects(putStub.items)
                    .prepare()
                    .asRxFlowable(BackpressureStrategy.MISSING);

            putStub.verifyBehaviorForMultipleObjects(flowable);
        }

        @Test
        public void shouldPutObjectsWithTypeMappingAsSingle() {
            final PutObjectsStub putStub = PutObjectsStub.newPutStubForMultipleObjectsWithTypeMapping();

            final Single<PutResults<TestItem>> single = putStub.storIOContentResolver
                    .put()
                    .objects(putStub.items)
                    .prepare()
                    .asRxSingle();

            putStub.verifyBehaviorForMultipleObjects(single);
        }

        @Test
        public void shouldPutObjectsWithTypeMappingAsCompletable() {
            final PutObjectsStub putStub = PutObjectsStub.newPutStubForMultipleObjectsWithTypeMapping();

            final Completable completable = putStub.storIOContentResolver
                    .put()
                    .objects(putStub.items)
                    .prepare()
                    .asRxCompletable();

            putStub.verifyBehaviorForMultipleObjects(completable);
        }
    }

    public static class NoTypeMappingError {

        @Test
        public void shouldReturnItemsInGetData() {
            final StorIOContentResolver storIOContentResolver = mock(StorIOContentResolver.class);

            //noinspection unchecked
            final PutResolver<TestItem> putResolver = mock(PutResolver.class);

            final List<TestItem> items = asList(TestItem.newInstance(), TestItem.newInstance());

            final PreparedPutCollectionOfObjects<TestItem> operation =
                    new PreparedPutCollectionOfObjects.Builder<TestItem>(storIOContentResolver, items)
                            .withPutResolver(putResolver)
                            .prepare();

            assertThat(operation.getData()).isEqualTo(items);
        }

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithoutAffectingContentProviderBlocking() {
            final StorIOContentResolver storIOContentResolver = mock(StorIOContentResolver.class);
            final StorIOContentResolver.LowLevel lowLevel = mock(StorIOContentResolver.LowLevel.class);

            when(storIOContentResolver.lowLevel()).thenReturn(lowLevel);

            when(storIOContentResolver.put()).thenReturn(new PreparedPut.Builder(storIOContentResolver));

            final List<TestItem> items = asList(TestItem.newInstance(), TestItem.newInstance());

            final PreparedPut<PutResults<TestItem>, Collection<TestItem>> preparedPut = storIOContentResolver
                    .put()
                    .objects(items)
                    .prepare();

            try {
                preparedPut.executeAsBlocking();
                failBecauseExceptionWasNotThrown(StorIOException.class);
            } catch (StorIOException expected) {
                // it's okay, no type mapping was found
                assertThat(expected).hasCauseInstanceOf(IllegalStateException.class);
            }

            verify(storIOContentResolver).put();
            verify(storIOContentResolver).lowLevel();
            verify(storIOContentResolver).interceptors();
            verify(lowLevel).typeMapping(TestItem.class);
            verify(lowLevel, never()).insert(any(InsertQuery.class), any(ContentValues.class));
            verify(lowLevel, never()).update(any(UpdateQuery.class), any(ContentValues.class));
            verifyNoMoreInteractions(storIOContentResolver, lowLevel);
        }

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithoutAffectingContentProviderAsFlowable() {
            final StorIOContentResolver storIOContentResolver = mock(StorIOContentResolver.class);
            final StorIOContentResolver.LowLevel lowLevel = mock(StorIOContentResolver.LowLevel.class);

            when(storIOContentResolver.lowLevel()).thenReturn(lowLevel);

            when(storIOContentResolver.put()).thenReturn(new PreparedPut.Builder(storIOContentResolver));

            final List<TestItem> items = asList(TestItem.newInstance(), TestItem.newInstance());

            final TestSubscriber<PutResults<TestItem>> testSubscriber = new TestSubscriber<PutResults<TestItem>>();

            storIOContentResolver
                    .put()
                    .objects(items)
                    .prepare()
                    .asRxFlowable(BackpressureStrategy.MISSING)
                    .subscribe(testSubscriber);

            testSubscriber.awaitTerminalEvent();
            testSubscriber.assertNoValues();
            assertThat(testSubscriber.errors().get(0))
                    .isInstanceOf(StorIOException.class)
                    .hasCauseInstanceOf(IllegalStateException.class);

            verify(storIOContentResolver).put();
            verify(storIOContentResolver).lowLevel();
            verify(storIOContentResolver).interceptors();
            verify(storIOContentResolver).defaultRxScheduler();
            verify(lowLevel).typeMapping(TestItem.class);
            verify(lowLevel, never()).insert(any(InsertQuery.class), any(ContentValues.class));
            verify(lowLevel, never()).update(any(UpdateQuery.class), any(ContentValues.class));
            verifyNoMoreInteractions(storIOContentResolver, lowLevel);
        }

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithoutAffectingContentProviderAsSingle() {
            final StorIOContentResolver storIOContentResolver = mock(StorIOContentResolver.class);
            final StorIOContentResolver.LowLevel lowLevel = mock(StorIOContentResolver.LowLevel.class);

            when(storIOContentResolver.lowLevel()).thenReturn(lowLevel);

            when(storIOContentResolver.put()).thenReturn(new PreparedPut.Builder(storIOContentResolver));

            final List<TestItem> items = asList(TestItem.newInstance(), TestItem.newInstance());

            final TestObserver<PutResults<TestItem>> testObserver = new TestObserver<PutResults<TestItem>>();

            storIOContentResolver
                    .put()
                    .objects(items)
                    .prepare()
                    .asRxSingle()
                    .subscribe(testObserver);

            testObserver.awaitTerminalEvent();
            testObserver.assertNoValues();
            assertThat(testObserver.errors().get(0))
                    .isInstanceOf(StorIOException.class)
                    .hasCauseInstanceOf(IllegalStateException.class);

            verify(storIOContentResolver).put();
            verify(storIOContentResolver).lowLevel();
            verify(storIOContentResolver).interceptors();
            verify(storIOContentResolver).defaultRxScheduler();
            verify(lowLevel).typeMapping(TestItem.class);
            verify(lowLevel, never()).insert(any(InsertQuery.class), any(ContentValues.class));
            verify(lowLevel, never()).update(any(UpdateQuery.class), any(ContentValues.class));
            verifyNoMoreInteractions(storIOContentResolver, lowLevel);
        }

        @Test
        public void shouldThrowExceptionIfNoTypeMappingWasFoundWithoutAffectingContentProviderAsCompletable() {
            final StorIOContentResolver storIOContentResolver = mock(StorIOContentResolver.class);
            final StorIOContentResolver.LowLevel lowLevel = mock(StorIOContentResolver.LowLevel.class);

            when(storIOContentResolver.lowLevel()).thenReturn(lowLevel);

            when(storIOContentResolver.put()).thenReturn(new PreparedPut.Builder(storIOContentResolver));

            final List<TestItem> items = asList(TestItem.newInstance(), TestItem.newInstance());

            final TestObserver<PutResults<TestItem>> testObserver = new TestObserver<PutResults<TestItem>>();

            storIOContentResolver
                    .put()
                    .objects(items)
                    .prepare()
                    .asRxCompletable()
                    .subscribe(testObserver);

            testObserver.awaitTerminalEvent();
            testObserver.assertNoValues();
            assertThat(testObserver.errors().get(0))
                    .isInstanceOf(StorIOException.class)
                    .hasCauseInstanceOf(IllegalStateException.class);

            verify(storIOContentResolver).put();
            verify(storIOContentResolver).lowLevel();
            verify(storIOContentResolver).interceptors();
            verify(storIOContentResolver).defaultRxScheduler();
            verify(lowLevel).typeMapping(TestItem.class);
            verify(lowLevel, never()).insert(any(InsertQuery.class), any(ContentValues.class));
            verify(lowLevel, never()).update(any(UpdateQuery.class), any(ContentValues.class));
            verifyNoMoreInteractions(storIOContentResolver, lowLevel);
        }
    }

    public static class OtherTests {

        @Test
        public void putCollectionOfObjectsFlowableExecutesOnSpecifiedScheduler() {
            final PutObjectsStub putStub = PutObjectsStub.newPutStubForMultipleObjectsWithoutTypeMapping();
            final SchedulerChecker schedulerChecker = SchedulerChecker.create(putStub.storIOContentResolver);

            final PreparedPutCollectionOfObjects<TestItem> operation = putStub.storIOContentResolver
                    .put()
                    .objects(putStub.items)
                    .withPutResolver(putStub.putResolver)
                    .prepare();

            schedulerChecker.checkAsFlowable(operation);
        }

        @Test
        public void putCollectionOfObjectsSingleExecutesOnSpecifiedScheduler() {
            final PutObjectsStub putStub = PutObjectsStub.newPutStubForMultipleObjectsWithoutTypeMapping();
            final SchedulerChecker schedulerChecker = SchedulerChecker.create(putStub.storIOContentResolver);

            final PreparedPutCollectionOfObjects<TestItem> operation = putStub.storIOContentResolver
                    .put()
                    .objects(putStub.items)
                    .withPutResolver(putStub.putResolver)
                    .prepare();

            schedulerChecker.checkAsSingle(operation);
        }

        @Test
        public void putCollectionOfObjectsCompletableExecutesOnSpecifiedScheduler() {
            final PutObjectsStub putStub = PutObjectsStub.newPutStubForMultipleObjectsWithoutTypeMapping();
            final SchedulerChecker schedulerChecker = SchedulerChecker.create(putStub.storIOContentResolver);

            final PreparedPutCollectionOfObjects<TestItem> operation = putStub.storIOContentResolver
                    .put()
                    .objects(putStub.items)
                    .withPutResolver(putStub.putResolver)
                    .prepare();

            schedulerChecker.checkAsCompletable(operation);
        }
    }
}
