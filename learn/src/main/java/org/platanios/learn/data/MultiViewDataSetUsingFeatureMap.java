package org.platanios.learn.data;

import org.platanios.learn.math.matrix.Vector;
import org.platanios.learn.math.statistics.StatisticsUtilities;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @param   <T>
 * @param   <D>
 *
 * @author Emmanouil Antonios Platanios
 */
public class MultiViewDataSetUsingFeatureMap<T extends Vector, D extends MultiViewDataInstance<T>>
        implements MultiViewDataSet<D> {
    private final FeatureMap<T> featureMap;

    private List dataInstances;

    public MultiViewDataSetUsingFeatureMap(FeatureMap<T> featureMap) {
        this.featureMap = featureMap;
        this.dataInstances = new ArrayList<>();
    }

    public MultiViewDataSetUsingFeatureMap(FeatureMap<T> featureMap, List<D> dataInstances) {
        this.featureMap = featureMap;
        this.dataInstances = dataInstances.stream()
                .map(MultiViewDataInstance::toDataInstanceBase)
                .collect(Collectors.toList());
    }

    @Override
    public int size() {
        return dataInstances.size();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void add(D dataInstance) {
        dataInstances.add(dataInstance.toDataInstanceBase());
    }

    @Override
    @SuppressWarnings("unchecked")
    public void add(List<D> dataInstances) {
        dataInstances.addAll((List) dataInstances
                .stream()
                .map(MultiViewDataInstance::toDataInstanceBase)
                .collect(Collectors.toList()));
    }

    @Override
    public void remove(int index) {
        dataInstances.remove(index);
    }

    @Override
    @SuppressWarnings("unchecked")
    public D get(int index) {
        DataInstanceBase<T> dataInstance = (DataInstanceBase<T>) dataInstances.get(index);
        return (D) dataInstance.toMultiViewDataInstance(featureMap.getFeatureVectors(dataInstance.name()));
    }

    @Override
    @SuppressWarnings("unchecked")
    public void set(int index, D dataInstance) {
        dataInstances.set(index, dataInstance.toDataInstanceBase());
    }

    @Override
    public MultiViewDataSetUsingFeatureMap<T, D> subSet(int fromIndex, int toIndex) {
        MultiViewDataSetUsingFeatureMap<T, D> subSet = new MultiViewDataSetUsingFeatureMap<>(featureMap);
        subSet.dataInstances = dataInstances.subList(fromIndex, toIndex);
        return subSet;
    }

    @Override
    @SuppressWarnings("unchecked")
    public DataSetUsingFeatureMap<T, ? extends DataInstance> getSingleViewDataSet(int view) {
        return new DataSetUsingFeatureMap<>(featureMap, view, dataInstances);
    }

    // TODO: Note that this method is very slow because it gets the feature vector for each data instance base.
    @Override
    @SuppressWarnings("unchecked")
    public MultiViewDataSetUsingFeatureMap<T, D> sort(Comparator<? super D> comparator) {
        dataInstances.sort((i1, i2) -> comparator.compare(
                (D) ((DataInstanceBase<T>) i1)
                        .toMultiViewDataInstance(featureMap.getFeatureVectors(((DataInstanceBase<T>) i1).name())),
                (D) ((DataInstanceBase<T>) i2)
                        .toMultiViewDataInstance(featureMap.getFeatureVectors(((DataInstanceBase<T>) i2).name()))
        ));
        return this;
    }

    @Override
    public Iterator<D> iterator() {
        return new Iterator<D>() {
            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex < dataInstances.size();
            }

            @Override
            @SuppressWarnings("unchecked")
            public D next() {
                DataInstanceBase<T> dataInstance = (DataInstanceBase<T>) dataInstances.get(currentIndex++);
                return (D) dataInstance.toMultiViewDataInstance(featureMap.getFeatureVectors(dataInstance.name()));
            }

            @Override
            public void remove() {
                dataInstances.remove(--currentIndex);
            }
        };
    }

    @Override
    public Iterator<List<D>> batchIterator(int batchSize) {
        return new Iterator<List<D>>() {
            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex < dataInstances.size();
            }

            @Override
            @SuppressWarnings("unchecked")
            public List<D> next() {
                int fromIndex = currentIndex;
                currentIndex += batchSize;
                if (currentIndex >= dataInstances.size())
                    currentIndex = dataInstances.size();
                List<DataInstanceBase<T>> dataInstancesSubList = dataInstances.subList(fromIndex, currentIndex);
                return dataInstancesSubList.stream()
                        .map(dataInstance ->
                                     (D) dataInstance.toMultiViewDataInstance(
                                             featureMap.getFeatureVectors(dataInstance.name())
                                     ))
                        .collect(Collectors.toList());
            }

            @Override
            public void remove() {
                currentIndex--;
                int indexLowerBound = currentIndex - batchSize;
                while (currentIndex > indexLowerBound)
                    dataInstances.remove(currentIndex--);
            }
        };
    }

    @Override
    public Iterator<List<D>> continuousRandomBatchIterator(int batchSize, boolean sampleWithReplacement) {
        return new Iterator<List<D>>() {
            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            @SuppressWarnings("unchecked")
            public List<D> next() {
                if (sampleWithReplacement || currentIndex + batchSize >= dataInstances.size()) {
                    StatisticsUtilities.shuffle(dataInstances);
                    currentIndex = 0;
                }
                int fromIndex = currentIndex;
                currentIndex += batchSize;
                if (currentIndex >= dataInstances.size())
                    currentIndex = dataInstances.size();
                List<DataInstanceBase<T>> dataInstancesSubList = dataInstances.subList(fromIndex, currentIndex);
                return dataInstancesSubList.stream()
                        .map(dataInstance ->
                                     (D) dataInstance.toMultiViewDataInstance(
                                             featureMap.getFeatureVectors(dataInstance.name())
                                     ))
                        .collect(Collectors.toList());
            }

            @Override
            public void remove() {
                currentIndex--;
                int indexLowerBound = currentIndex - batchSize;
                while (currentIndex > indexLowerBound)
                    dataInstances.remove(currentIndex--);
            }
        };
    }

    @Override
    public Iterator<List<D>> continuousRandomBatchIterator(int batchSize,
                                                           boolean sampleWithReplacement,
                                                           Random random) {
        return new Iterator<List<D>>() {
            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            @SuppressWarnings("unchecked")
            public List<D> next() {
                if (sampleWithReplacement || currentIndex + batchSize >= dataInstances.size()) {
                    StatisticsUtilities.shuffle(dataInstances, random);
                    currentIndex = 0;
                }
                int fromIndex = currentIndex;
                currentIndex += batchSize;
                if (currentIndex >= dataInstances.size())
                    currentIndex = dataInstances.size();
                List<DataInstanceBase<T>> dataInstancesSubList = dataInstances.subList(fromIndex, currentIndex);
                return dataInstancesSubList.stream()
                        .map(dataInstance ->
                                     (D) dataInstance.toMultiViewDataInstance(
                                             featureMap.getFeatureVectors(dataInstance.name())
                                     ))
                        .collect(Collectors.toList());
            }

            @Override
            public void remove() {
                currentIndex--;
                int indexLowerBound = currentIndex - batchSize;
                while (currentIndex > indexLowerBound)
                    dataInstances.remove(currentIndex--);
            }
        };
    }
}
