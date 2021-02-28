// Copyright 2020
// Author: Matei Simtinică

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Task3
 * This being an optimization problem, the solve method's logic has to work differently.
 * You have to search for the minimum number of arrests by successively querying the oracle.
 * Hint: it might be easier to reduce the current task to a previously solved task
 */
public class Task3 extends Task {
    String task2InFilename;

    String task2OutFilename;

    Integer numberOfFamilies;

    Integer numberOfRelations;

    Integer[][] relations;

    Integer[][] task2Relations;

    List<Integer> extendedFamily;

    boolean oracleAnswer;

    int relationsNo = 0;

    @Override
    public void solve() throws IOException, InterruptedException {
        task2InFilename = inFilename + "_t2";
        task2OutFilename = outFilename + "_t2";
        Task2 task2Solver = new Task2();
        task2Solver.addFiles(task2InFilename, oracleInFilename, oracleOutFilename, task2OutFilename);
        readProblemData();
        reduceToTask2();

        int extendedFamilySizeSearched;

        for (int i = 0; i < numberOfFamilies; i++) {
            // compute the input for task2
            FileWriter task2Writer = new FileWriter(task2InFilename);

            extendedFamilySizeSearched = numberOfFamilies - i - 1;

            task2Writer.write(numberOfFamilies + " "
                                 + relationsNo + " " + extendedFamilySizeSearched + "\n");

            for (int j = 0; j < relationsNo; j++) {
                task2Writer.write(task2Relations[j][0] + " " + task2Relations[j][1] + "\n");
            }

            task2Writer.flush();
            task2Writer.close();

            // solve the problem
            task2Solver.solve();

            extractAnswerFromTask2();

            // check if we got an answer for the current i
            if (oracleAnswer) {
                writeAnswer();
                break;
            }
        }
    }

    @Override
    public void readProblemData() throws IOException {
        BufferedReader input = new BufferedReader(new FileReader(inFilename));

        String[] firstLineElems = input.readLine().split("\\s");

        numberOfFamilies = Integer.parseInt(firstLineElems[0]);

        numberOfRelations = Integer.parseInt(firstLineElems[1]);

        relations = new Integer[numberOfFamilies][numberOfFamilies];

        // initialize each family relation with 0 first
        for (int i = 0; i < numberOfFamilies; i++) {
            for (int j = 0; j < numberOfFamilies; j++) {
                relations[i][j] = 0;
            }
        }

        // if 2 families are in relation, we will mark it with a '1' in the relations matrix
        for (int i = 0; i < numberOfRelations; i++) {
            String[] currentLine = input.readLine().split("\\s");

            int firstFamily = Integer.parseInt(currentLine[0]);
            int secondFamily = Integer.parseInt(currentLine[1]);

            relations[firstFamily - 1][secondFamily - 1] = 1;
            relations[secondFamily - 1][firstFamily - 1] = 1;
        }

        input.close();
    }

    public void reduceToTask2() {
        // generate the complement of relations
        Integer[][] complementRelations = new Integer[numberOfFamilies][numberOfFamilies];

        int currentRelation = 0;

        for (int i = 0; i < numberOfFamilies; i++) {
            for (int j = 0; j < numberOfFamilies; j++) {
                if (relations[i][j].equals(0)) {
                    complementRelations[i][j] = 1;

                    currentRelation++;
                } else {
                    complementRelations[i][j] = 0;
                }
            }
        }

        // generate the matrix for the task2 input
        task2Relations = new Integer[currentRelation][2];

        relationsNo = 0;

        for (int i = 0; i < numberOfFamilies; i++) {
            for (int j = 0; j < numberOfFamilies; j++) {
                if (complementRelations[i][j].equals(1) && i != j) {
                    task2Relations[relationsNo][0] = i + 1;
                    task2Relations[relationsNo][1] = j + 1;

                    // to avoid duplicate
                    complementRelations[j][i] = 0;

                    relationsNo++;
                }
            }
        }
    }

    public void extractAnswerFromTask2() throws IOException {
        BufferedReader result = new BufferedReader(new FileReader(task2OutFilename));

        String firstLine = result.readLine().split("\\s")[0];

        oracleAnswer = Boolean.parseBoolean(firstLine);

        if (oracleAnswer) {
            // save the families that form the extended family
            extendedFamily = new ArrayList<Integer>();

            String[] nodes = result.readLine().split("\\s");

            for (int i = 0; i < nodes.length; i++) {
                extendedFamily.add(Integer.parseInt(nodes[i]));
            }
        }

        result.close();
    }

    @Override
    public void writeAnswer() throws IOException {
        FileWriter out = new FileWriter(outFilename);

        for (int i = 1; i <= numberOfFamilies; i++) {
            // write the families that needs to be removed
            if (!extendedFamily.contains(i)) {
                out.write(i + " ");
            }
        }

        out.flush();
        out.close();
    }
}
