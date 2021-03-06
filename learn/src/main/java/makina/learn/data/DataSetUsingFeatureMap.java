package makina.learn.data;

import makina.math.matrix.Vector;
import makina.math.StatisticsUtilities;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @param   <T>
 * @param   <D>
 *
 * @author Emmanouil Antonios Platanios
 */
public class DataSetUsingFeatureMap<T extends Vector, D extends DataInstance<T>> implements DataSet<D> {
    protected final FeatureMap<T> featureMap;
    protected final int featureMapView;

    private List dataInstances;

    public DataSetUsingFeatureMap(FeatureMap<T> featureMap, int featureMapView) {
        this.featureMap = featureMap;
        this.featureMapView = featureMapView;
        this.dataInstances = new ArrayList<>();
    }

    public DataSetUsingFeatureMap(FeatureMap<T> featureMap, int featureMapView, List<D> dataInstances) {
        this.featureMap = featureMap;
        this.featureMapView = featureMapView;
        this.dataInstances = dataInstances.stream()
                .map(DataInstance::toDataInstanceBase)
                .collect(Collectors.toList());
    }

    @Override
    public int size() {
        return dataInstances.size();
    }

    @Override
    public <S extends DataInstance> DataSet<S> newDataSet() {
        return new DataSetUsingFeatureMap<>(featureMap, featureMapView);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void add(D dataInstance) {
        dataInstances.add(dataInstance.toDataInstanceBase());
    }

    @Override
    @SuppressWarnings("unchecked")
    public void add(List<D> dataInstances) {
        dataInstances.addAll(
                (List) dataInstances.stream().map(DataInstance::toDataInstanceBase).collect(Collectors.toList())
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    public D remove(int index) {
        DataInstanceBase<T> dataInstance = (DataInstanceBase<T>) dataInstances.remove(index);
        return (D) dataInstance.toDataInstance(featureMap.getFeatureVector(dataInstance.name(), featureMapView));
    }

    @Override
    @SuppressWarnings("unchecked")
    public D remove(D dataInstance) {
        dataInstances.remove(dataInstance.toDataInstanceBase());
        return dataInstance;
    }

    @Override
    @SuppressWarnings("unchecked")
    public D get(int index) {
        DataInstanceBase<T> dataInstance = (DataInstanceBase<T>) dataInstances.get(index);
        return (D) dataInstance.toDataInstance(featureMap.getFeatureVector(dataInstance.name(), featureMapView));
    }

    @Override
    @SuppressWarnings("unchecked")
    public void set(int index, D dataInstance) {
        dataInstances.set(index, dataInstance.toDataInstanceBase());
    }

    @Override
    @SuppressWarnings("unchecked")
    public DataSetUsingFeatureMap<T, D> subSet(int fromIndex, int toIndex) {
        DataSetUsingFeatureMap<T, D> subSet = new DataSetUsingFeatureMap<>(featureMap, featureMapView);
        subSet.dataInstances = new ArrayList<>(dataInstances.subList(fromIndex, toIndex));
        return subSet;
    }

    @Override
    @SuppressWarnings("unchecked")
    public DataSetUsingFeatureMap<T, D> subSetComplement(int fromIndex, int toIndex) {
        DataSetUsingFeatureMap<T, D> subSet = new DataSetUsingFeatureMap<>(featureMap, featureMapView);
        subSet.dataInstances = new ArrayList<>(dataInstances.subList(0, fromIndex));
        subSet.dataInstances.addAll(dataInstances.subList(toIndex, dataInstances.size()));
        return subSet;
    }

    // TODO: Note that this method is very slow because it gets the feature vector for each data instance base.
    @Override
    @SuppressWarnings("unchecked")
    public DataSetUsingFeatureMap<T, D> sort(Comparator<? super D> comparator) {
        dataInstances.sort((i1, i2) -> comparator.compare(
                (D) ((DataInstanceBase<T>) i1)
                        .toDataInstance(featureMap.getFeatureVector(((DataInstanceBase<T>) i1).name(), featureMapView)),
                (D) ((DataInstanceBase<T>) i2)
                        .toDataInstance(featureMap.getFeatureVector(((DataInstanceBase<T>) i2).name(), featureMapView))
        ));
        return this;
    }

    @Override
    public DataSetUsingFeatureMap<T, D> shuffle() {
        Collections.shuffle(dataInstances);
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
                return (D) dataInstance.toDataInstance(featureMap.getFeatureVector(dataInstance.name(),
                                                                                   featureMapView));
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
                currentIndex = Math.min(currentIndex + batchSize, dataInstances.size());
                List<DataInstanceBase<T>> dataInstancesSubList = dataInstances.subList(fromIndex, currentIndex);
                return dataInstancesSubList.stream()
                        .map(dataInstance ->
                                     (D) dataInstance.toDataInstance(
                                             featureMap.getFeatureVector(dataInstance.name(), featureMapView)
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
        return continuousRandomBatchIterator(batchSize, sampleWithReplacement, null);
    }

    @Override
    public Iterator<List<D>> continuousRandomBatchIterator(int batchSize,
                                                           boolean sampleWithReplacement,
                                                           Random random) {
        final List<Integer> dataInstancesIndexes = new ArrayList<>(dataInstances.size());
        for (int i = 0; i < dataInstances.size(); i++)
            dataInstancesIndexes.add(i);

        return new Iterator<List<D>>() {
            private int currentIndex = 0;
            private List<Integer> indexes = dataInstancesIndexes;

            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            @SuppressWarnings("unchecked")
            public List<D> next() {
                if (sampleWithReplacement || currentIndex + batchSize >= dataInstances.size()) {
                    if (random == null)
                        StatisticsUtilities.shuffle(indexes);
                    else
                        StatisticsUtilities.shuffle(indexes, random);
                    currentIndex = 0;
                }
                int fromIndex = currentIndex;
                currentIndex = Math.min(currentIndex + batchSize, dataInstances.size());
                List<D> dataInstancesSubList = new ArrayList<>(batchSize);
                for (int i = fromIndex; i < currentIndex; i++) {
                    DataInstanceBase<T> dataInstanceBase = (DataInstanceBase<T>) dataInstances.get(indexes.get(i));
                    dataInstancesSubList.add((D) dataInstanceBase.toDataInstance(
                            featureMap.getFeatureVector(dataInstanceBase.name(), featureMapView)
                    ));
                }
                return dataInstancesSubList;
            }

            @Override
            public void remove() {
                currentIndex--;
                int indexLowerBound = currentIndex - batchSize;
                while (currentIndex > indexLowerBound) {
                    dataInstancesIndexes.remove(indexes.get(currentIndex));
                    dataInstances.remove((int) indexes.remove(currentIndex--));
                }
            }
        };
    }
}
