package baseline.csc2;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Created by yizhouyan on 9/2/18.
 */
public class OccListForDeltaTest {
    @Test
    public void binarySearch_1() throws Exception {
        Integer [] numbers = {1,2,3,5,6,8,9,11,21,23,34};
        ArrayList<Integer> numbersList = new ArrayList<Integer>(Arrays.asList(numbers));
        assertEquals(3,OccListForDelta.binarySearch(4,numbersList));
    }

    @Test
    public void binarySearch_2() throws Exception {
        Integer [] numbers = {1,2,3,5,6,8,9,11,21,23,34};
        ArrayList<Integer> numbersList = new ArrayList<Integer>(Arrays.asList(numbers));
        assertEquals(0,OccListForDelta.binarySearch(0,numbersList));
    }

    @Test
    public void binarySearch_3() throws Exception {
        Integer [] numbers = {1,2,3,5,6,8,9,11,21,23,34};
        ArrayList<Integer> numbersList = new ArrayList<Integer>(Arrays.asList(numbers));
        assertEquals(1,OccListForDelta.binarySearch(1,numbersList));
    }

    @Test
    public void binarySearch_4() throws Exception {
        Integer [] numbers = {1,2,3,5,6,8,9,11,21,23,34};
        ArrayList<Integer> numbersList = new ArrayList<Integer>(Arrays.asList(numbers));
        assertEquals(11,OccListForDelta.binarySearch(34,numbersList));
    }

    @Test
    public void binarySearch_5() throws Exception {
        Integer [] numbers = {1,2,3,5,6,8,9,11,21,23,34};
        ArrayList<Integer> numbersList = new ArrayList<Integer>(Arrays.asList(numbers));
        assertEquals(9,OccListForDelta.binarySearch(21,numbersList));
    }
}