/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TestCase;

/**
 *
 * @author Pham Hung
 */
import Jama.Matrix;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class KNN {

    int k;
    KNN(int k) {
        this.k = k;
    }
    Matrix readFile(String filename){
        String thisLine;
        List<String[]> lines = new ArrayList<>();
        List<double[]> line = new ArrayList<>();
        int count = 0;
        try {
            FileInputStream fis = new FileInputStream(filename);
            DataInputStream dis = new DataInputStream(fis);
            while((thisLine = dis.readLine()) != null){
                if(count != 0){
                    lines.add(thisLine.split(","));
                    line.add(Arrays.asList(thisLine.split(",")).stream().mapToDouble(Double::parseDouble).toArray());
//                    System.out.println(line.get(0)[0]);
                }
                count += 1;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[][] array = new String[lines.size()][0];
        double[][] udp_data = new double[line.size()][0];
        lines.toArray(array);
        line.toArray(udp_data);
        Matrix udp_dataset = new Matrix(udp_data);
        return udp_dataset;
    }
    int Calculate(Matrix matrix, double[] inputData){
        int vote = 0;
        int[] row = IntStream.range(0, matrix.getRowDimension()).toArray();
        int[] col1 = {2};
        int[] col2 = {0, 1};
        int[] col3 = {0};
        int[] col4 = {1};

        double[][] inputStream = new double[matrix.getRowDimension()][2];
        for(int i = 0; i < matrix.getRowDimension(); i++){
            inputStream[i] = inputData;
        }

        Matrix udpTraining = matrix.getMatrix(row, col2);
        Matrix udpLabel = matrix.getMatrix(row, col1);
        Matrix input = new Matrix(inputStream);
        udpTraining.minusEquals(input);
        udpTraining.arrayTimesEquals(udpTraining);
        Matrix sum = udpTraining.getMatrix(row, col3).plus(udpTraining.getMatrix(row, col4));
        double[][] dataToSort = new double[1][sum.getRowDimension()];
        dataToSort = (double[][]) sum.transpose().getArray();
        sort(dataToSort[0], 0, dataToSort[0].length - 1);

        for(int i = 0; i < k; i++){
            double value = dataToSort[0][i];
            for(int j = 0; j < sum.getRowDimension(); j++){
                if(sum.get(j,0) == value){
                    if(udpLabel.get(j, 0) == 1)
                        vote += 1;
                }
            }
        }

        if(vote > k/2){
            return 1;
        }
        else{
            return 0;
        }
    }

    List<String> calculateBatch(Matrix matrix, List<ParameterTCP> list){
        List<String> listIP = new ArrayList<>();
        for(int i = 0; i < list.size(); i++){
            double input[] = new double[2];
            input[0] = list.get(i).getNumber_port();
            input[1] = list.get(i).getEntropy_port_src();
            int result = Calculate(matrix, input);
            if(result == 1){
                listIP.add(list.get(i).getIp());
            }
        }
        return listIP;
    }

    // Merges two subarrays of arr[].
    // First subarray is arr[l..m]
    // Second subarray is arr[m+1..r]
    void merge(double arr[], int l, int m, int r) {
        // Find sizes of two subarrays to be merged
        int n1 = m - l + 1;
        int n2 = r - m;

        /* Create temp arrays */
        double L[] = new double [n1];
        double R[] = new double [n2];

        /*Copy data to temp arrays*/
        for (int i=0; i<n1; ++i)
            L[i] = arr[l + i];
        for (int j=0; j<n2; ++j)
            R[j] = arr[m + 1+ j];


        /* Merge the temp arrays */

        // Initial indexes of first and second subarrays
        int i = 0, j = 0;

        // Initial index of merged subarry array
        int k = l;
        while (i < n1 && j < n2)
        {
            if (L[i] <= R[j])
            {
                arr[k] = L[i];
                i++;
            }
            else
            {
                arr[k] = R[j];
                j++;
            }
            k++;
        }

        /* Copy remaining elements of L[] if any */
        while (i < n1)
        {
            arr[k] = L[i];
            i++;
            k++;
        }

        /* Copy remaining elements of R[] if any */
        while (j < n2)
        {
            arr[k] = R[j];
            j++;
            k++;
        }
    }

    // Main function that sorts arr[l..r] using
    // merge()
    void sort(double arr[], int l, int r) {
        if (l < r)
        {
            // Find the middle point
            int m = (l+r)/2;

            // Sort first and second halves
            sort(arr, l, m);
            sort(arr , m+1, r);

            // Merge the sorted halves
            merge(arr, l, m, r);

        }
    }
}

