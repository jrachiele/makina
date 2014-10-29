package org.platanios.learn.classification.reflect;

import org.platanios.learn.classification.*;
import org.platanios.learn.math.matrix.Vector;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author Emmanouil Antonios Platanios
 */
public class Integrator<T extends Vector, S> {
    private final List<TrainableClassifier<T, S>> classifiers;
    private final DataSelectionMethod dataSelectionMethod;
    private final double dataSelectionParameter;
    private final ExecutorService taskExecutor;
    private final String workingDirectory;
    private final boolean saveModelsOnEveryIteration;
    private final boolean useDifferentFilePerIteration;

    private List<MultiViewDataInstance<T, S>> labeledDataInstances;
    private List<MultiViewDataInstance<T, S>> unlabeledDataInstances;
    private int iterationNumber = 1;

    public static class Builder<T extends Vector, S> {
        private List<TrainableClassifier<T, S>> classifiers;
        private List<MultiViewDataInstance<T, S>> dataInstances;

        private DataSelectionMethod dataSelectionMethod = DataSelectionMethod.FIXED_PROPORTION;
        private double dataSelectionParameter = 0.1;
        private int numberOfThreads = Runtime.getRuntime().availableProcessors();
        private String workingDirectory = "Integrator Directory";
        private boolean saveModelsOnEveryIteration = true;
        private boolean useDifferentFilePerIteration = true;

        public Builder() {
            classifiers = new ArrayList<>();
            dataInstances = new ArrayList<>();
        }

        public Builder(String modelsFileAbsolutePath, boolean resumeTraining) {
            classifiers = new ArrayList<>();
            dataInstances = new ArrayList<>();
            if (resumeTraining) {
                File inputFile = new File(modelsFileAbsolutePath);
                workingDirectory = inputFile.getParentFile().getAbsolutePath();
                try {
                    ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(inputFile));
                    dataSelectionMethod = DataSelectionMethod.values()[objectInputStream.readInt()];
                    dataSelectionParameter = objectInputStream.readDouble();
                    int numberOfClassifiers = objectInputStream.readInt();
                    for (int i = 0; i < numberOfClassifiers; i++) {
                        ClassifierType classifierType = ClassifierType.values()[objectInputStream.readInt()];
                        Classifier<T, S> classifier = Classifiers.build(objectInputStream, classifierType);
                        if (!(classifier instanceof TrainableClassifier)) {
                            throw new RuntimeException("The stored classifier is not a trainable classifier and " +
                                                               "so cannot be used with the integrator!");
                        }
                        classifiers.add((TrainableClassifier<T, S>) classifier);
                    }
                } catch (IOException e) {
                    System.out.println("Could not open the file \""
                                               + inputFile.getAbsolutePath() + "\" to load the models from!");
                }
            }
        }

        public Builder addClassifier(TrainableClassifier<T, S> classifier) {
            classifiers.add(classifier);
            return this;
        }

        public Builder addDataInstance(MultiViewDataInstance<T, S> dataInstance) {
            dataInstances.add(dataInstance);
            return this;
        }

        public Builder addDataInstances(List<MultiViewDataInstance<T, S>> dataInstances) {
            this.dataInstances.addAll(dataInstances);
            return this;
        }

        public Builder dataSelectionMethod(DataSelectionMethod dataSelectionMethod) {
            this.dataSelectionMethod = dataSelectionMethod;
            return this;
        }

        public Builder dataSelectionParameter(double dataSelectionParameter) {
            this.dataSelectionParameter = dataSelectionParameter;
            return this;
        }

        public Builder numberOfThreads(int numberOfThreads) {
            this.numberOfThreads = numberOfThreads;
            return this;
        }

        public Builder workingDirectory(String workingDirectory) {
            this.workingDirectory = workingDirectory;
            return this;
        }

        public Builder saveModelsOnEveryIteration(boolean saveModelsOnEveryIteration) {
            this.saveModelsOnEveryIteration = saveModelsOnEveryIteration;
            return this;
        }

        public Builder useDifferentFilePerIteration(boolean useDifferentFilePerIteration) {
            this.useDifferentFilePerIteration = useDifferentFilePerIteration;
            return this;
        }

        public Integrator<T, S> build() {
            return new Integrator<T, S>(this);
        }
    }

    private Integrator(Builder<T, S> builder) {
        classifiers = builder.classifiers;
        dataSelectionMethod = builder.dataSelectionMethod;
        dataSelectionParameter = builder.dataSelectionParameter;
        taskExecutor = Executors.newFixedThreadPool(builder.numberOfThreads);
        workingDirectory = builder.workingDirectory;
        saveModelsOnEveryIteration = builder.saveModelsOnEveryIteration;
        useDifferentFilePerIteration = builder.useDifferentFilePerIteration;
        initializeWorkingDirectory();
        labeledDataInstances = new ArrayList<>();
        unlabeledDataInstances = new ArrayList<>();
        for (MultiViewDataInstance<T, S> dataInstance : builder.dataInstances) {
            if (dataInstance.label() != null) {
                labeledDataInstances.add(dataInstance);
            } else {
                unlabeledDataInstances.add(dataInstance);
            }
        }
    }

    private void initializeWorkingDirectory() {
        File directory = new File(workingDirectory);
        if (!directory.exists() && !directory.mkdirs()) {
            System.out.println("Unable to create directory " + directory.getAbsolutePath());
        }
    }

    public void trainClassifiers() {
        List<Callable<Boolean>> classifierTrainingTasks = new ArrayList<>();
        for (int i = 0; i < classifiers.size(); i++) {
            TrainableClassifier<T, S> classifier = classifiers.get(i);
            List<DataInstance<T, S>> trainingData = DataInstances.getSingleViewDataInstances(labeledDataInstances, i);
            classifierTrainingTasks.add(() -> classifier.train(trainingData));
        }
        try {
            taskExecutor.invokeAll(classifierTrainingTasks);
        } catch (InterruptedException e) {
            System.out.println("Execution was interrupted while training the classifiers.");
        }
    }

    public void makePredictions() {
        List<Callable<List<DataInstance<T, S>>>> classifierPredictionTasks = new ArrayList<>();
        for (int i = 0; i < classifiers.size(); i++) {
            TrainableClassifier<T, S> classifier = classifiers.get(i);
            List<DataInstance<T, S>> testingData = DataInstances.getSingleViewDataInstances(unlabeledDataInstances, i);
            classifierPredictionTasks.add(() -> classifier.predictInPlace(testingData));
        }
        try {
            List<Future<List<DataInstance<T, S>>>> predictionResults =
                    taskExecutor.invokeAll(classifierPredictionTasks);
            for (int i = 0; i < classifiers.size(); i++) {
                List<DataInstance<T, S>> dataInstances = predictionResults.get(i).get();
                for (int j = 0; j < dataInstances.size(); j++) {
                    if (dataInstances.get(j).probability() > unlabeledDataInstances.get(j).probability()) {
                        unlabeledDataInstances.set(i,
                                                   new MultiViewDataInstance.Builder<>(unlabeledDataInstances.get(j))
                                                           .label(dataInstances.get(j).label())
                                                           .probability(dataInstances.get(j).probability()).build());
                    }
                }
            }
        } catch (InterruptedException e) {
            System.out.println("Execution was interrupted while making predictions with the classifiers.");
        } catch (ExecutionException e) {
            System.out.println("Something went wrong while making predictions with the classifiers.");
        }
    }

    public void transferData() {
        dataSelectionMethod.transferData(labeledDataInstances, unlabeledDataInstances, dataSelectionParameter);
    }

    private void saveModels(boolean useDifferentFilePerIteration) {
        File outputFile;
        if (useDifferentFilePerIteration) {
            outputFile = new File(workingDirectory + File.separator + "Iteration_" + iterationNumber + ".integrator");
        } else {
            outputFile = new File(workingDirectory + File.separator + "Models.integrator");
        }
        try {
            if (!outputFile.exists() && outputFile.createNewFile()) {
                System.out.println("Could not create the file \""
                                           + outputFile.getAbsolutePath() + "\" to store the models!");
            }
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(outputFile, false));
            objectOutputStream.writeInt(dataSelectionMethod.ordinal());
            objectOutputStream.writeDouble(dataSelectionParameter);
            objectOutputStream.writeInt(classifiers.size());
            for (TrainableClassifier classifier : classifiers) {
                objectOutputStream.writeInt(classifier.type().ordinal());
                classifier.writeModelToStream(objectOutputStream);
            }
        } catch (IOException e) {
            System.out.println("Could not create or open the file \""
                                       + outputFile.getAbsolutePath() + "\" to store the models!");
        }
    }

    public void performSingleIteration() {
        trainClassifiers();
        makePredictions();
        transferData();
        if (saveModelsOnEveryIteration) {
            saveModels(useDifferentFilePerIteration);
        }
        iterationNumber++;
    }

    public enum DataSelectionMethod {
        FIXED_PROPORTION {
            @Override
            public <T extends Vector, S> void transferData(List<MultiViewDataInstance<T, S>> labeledDataInstances,
                                                           List<MultiViewDataInstance<T, S>> unlabeledDataInstances,
                                                           double proportionToTransfer) {
                unlabeledDataInstances
                        .parallelStream()
                        .sorted((i1, i2) -> -Double.compare(i1.probability(), i2.probability()));
                int numberOfPredictionsToTransfer =
                        (int) Math.floor(proportionToTransfer * unlabeledDataInstances.size());
                for (int i = 0; i < numberOfPredictionsToTransfer; i++) {
                    labeledDataInstances.add(unlabeledDataInstances.get(0));
                    unlabeledDataInstances.remove(0);
                }
            }
        },
        PROBABILITY_THRESHOLD {
            @Override
            public <T extends Vector, S> void transferData(List<MultiViewDataInstance<T, S>> labeledDataInstances,
                                                           List<MultiViewDataInstance<T, S>> unlabeledDataInstances,
                                                           double probabilityThreshold) {
                unlabeledDataInstances
                        .parallelStream()
                        .sorted((i1, i2) -> -Double.compare(i1.probability(), i2.probability()));
                for (int i = 0; i < unlabeledDataInstances.size(); i++) {
                    if (unlabeledDataInstances.get(0).probability() >= probabilityThreshold) {
                        labeledDataInstances.add(unlabeledDataInstances.get(0));
                        unlabeledDataInstances.remove(0);
                    } else {
                        break;
                    }
                }
            }
        };

        public abstract <T extends Vector, S> void transferData(
                List<MultiViewDataInstance<T, S>> labeledDataInstances,
                List<MultiViewDataInstance<T, S>> unlabeledDataInstances,
                double parameter
        );
    }
}
