package makina.learn.data;

import makina.math.StatisticsUtilities;

import java.util.*;

/**
 * @author Emmanouil Antonios Platanios
 */
public class DataSetInMemory<D extends DataInstance> implements DataSet<D> {
    private List<D> dataInstances;

    public DataSetInMemory() {
        this.dataInstances = new ArrayList<>();
    }

    public DataSetInMemory(List<D> dataInstances) {
        this.dataInstances = dataInstances;
    }

    @Override
    public int size() {
        return dataInstances.size();
    }

    @Override
    public <S extends DataInstance> DataSet<S> newDataSet() {
        return new DataSetInMemory<>();
    }

    @Override
    public void add(D dataInstance) {
        dataInstances.add(dataInstance);
    }

    @Override
    public void add(List<D> dataInstances) {
        dataInstances.addAll(dataInstances);
    }

    @Override
    public D remove(int index) {
        return dataInstances.remove(index);
    }

    @Override
    public D remove(D dataInstance) {
        dataInstances.remove(dataInstance);
        return dataInstance;
    }

    @Override
    public D get(int index) {
        return dataInstances.get(index);
    }

    @Override
    public void set(int index, D dataInstance) {
        dataInstances.set(index, dataInstance);
    }

    @Override
    public DataSetInMemory<D> subSet(int fromIndex, int toIndex) {
        return new DataSetInMemory<>(new ArrayList<>(dataInstances.subList(fromIndex, toIndex)));
    }

    @Override
    public DataSetInMemory<D> subSetComplement(int fromIndex, int toIndex) {
        List<D> dataInstancesList = new ArrayList<>(dataInstances.subList(0, fromIndex));
        dataInstancesList.addAll(dataInstances.subList(toIndex, dataInstances.size()));
        return new DataSetInMemory<>(dataInstancesList);
    }

    @Override
    public DataSetInMemory<D> sort(Comparator<? super D> comparator) {
        dataInstances.sort(comparator);
        return this;
    }

    @Override
    public DataSetInMemory<D> shuffle() {
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
            public D next() {
                return dataInstances.get(currentIndex++);
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
            public List<D> next() {
                int fromIndex = currentIndex;
                currentIndex = Math.min(currentIndex + batchSize, dataInstances.size());
                return dataInstances.subList(fromIndex, currentIndex);
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
                for (int i = fromIndex; i < currentIndex; i++)
                	dataInstancesSubList.add(dataInstances.get(indexes.get(i)));
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
