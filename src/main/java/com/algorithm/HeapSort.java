package com.algorithm;

import java.util.List;
import java.util.function.Function;

public class HeapSort implements Function<List<Integer>, List<Integer>> {

    @Override
    public List<Integer> apply(List<Integer> integers) {
        for (int actualSize = integers.size(); actualSize > 0; actualSize--) {
            createMaxHeap(integers, actualSize);
            swap(integers, 0, actualSize - 1);
        }
        return integers;
    }

    public void createMaxHeap(List<Integer> integers, int actualSize) {
        // Find the first null leaf node
        final int lastNullLeafIndex = actualSize / 2 - 1;
        // Adjust heap till root
        for (int index = lastNullLeafIndex; index >= 0; index--) {
            recursiveAdjustMaxHeap(integers, actualSize, index);
        }
    }

    public void recursiveAdjustMaxHeap(List<Integer> integers, int actualSize, int rootIndex) {
        int toAdjust = rootIndex;
        while (toAdjust < actualSize) {
            toAdjust = adjustMaxHeap(integers, actualSize, toAdjust);
            if (toAdjust == -1) {
                return;
            }
        }
    }

    public int adjustMaxHeap(List<Integer> integers, int actualSize, int rootIndex) {
        final int leftChildIndex = rootIndex * 2 + 1;
        final int rightChildIndex = rootIndex * 2 + 2;
        if (leftChildIndex >= actualSize) {
            // No need to adjust
            return -1;
        }
        int toSwapIndex;
        if (rightChildIndex >= actualSize) {
            // No right
            if (integers.get(rootIndex) >= integers.get(leftChildIndex)) {
                // Already max
                return -1;
            } else {
                toSwapIndex = leftChildIndex;
            }
        } else {
            toSwapIndex = integers.get(leftChildIndex) > integers.get(rightChildIndex) ? leftChildIndex : rightChildIndex;
            if (integers.get(rootIndex) >= integers.get(toSwapIndex)) {
                return -1;
            }
        }
        swap(integers, rootIndex, toSwapIndex);
        return toSwapIndex;
    }

    private void swap(List<Integer> integers, int s, int w) {
        Integer temp = integers.get(s);
        integers.set(s, integers.get(w));
        integers.set(w, temp);
    }


}
