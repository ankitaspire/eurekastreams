/*
 * Copyright (c) 2010 Lockheed Martin Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.eurekastreams.server.service.actions.strategies.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

import junit.framework.Assert;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.junit.Test;

/**
 * A collection of tests that test the Interpolation List Collider against the Intersect method in Apache Commons.
 */
public class IterpolationListColliderTest
{
    /**
     * System under test.
     */
    private static ListCollider collider = new InterpolationListCollider();

    /**
     * 1000 item sorted list location.
     */
    private static final String ITEMS_1000_SORTED_FILE = 
        "src/test/resources/List.1280321312726.sorted.1000.step0.iterations1.index1-2000";

    /**
     * 1000 item unsorted list location.
     */
    private static final String ITEMS_1000_UNSORTED_FILE = 
        "src/test/resources/List.1280321446257.unsorted.1000.step0.iterations1.index1-2000";

    /**
     * 100 item sorted list location.
     */
    private static final String ITEMS_100_SORTED_FILE = 
        "src/test/resources/List.1280320787414.sorted.100.step0.iterations1.index1-1000";

    /**
     * 100 item unsorted list location.
     */
    private static final String ITEMS_100_UNSORTED_FILE = 
        "src/test/resources/List.1280320908654.unsorted.100.step0.iterations1.index1-1000";

    /**
     * Test collision miss where item is too high.
     */
    @Test
    public final void testCollisionMissHigh()
    {
        Long[] sorted = { 5L, 4L, 2L, 1L };
        Long[] unsorted = { 6L };

        collideTest(sorted, unsorted);
    }

    /**
     * Test collision miss where item is too low.
     */
    @Test
    public final void testCollisionMissLow()
    {
        Long[] sorted = { 5L, 4L, 2L, 1L };
        Long[] unsorted = { 0L };

        collideTest(sorted, unsorted);
    }

    /**
     * Simple Test collision.
     */
    @Test
    public final void testCollision()
    {
        Long[] sorted = { 5L, 4L, 3L, 2L, 1L };
        Long[] unsorted = { 8L, 0L, 3L, 5L };

        collideTest(sorted, unsorted);
    }

    /**
     * Test colliding, empty lists.
     */
    @Test
    public final void testCollisionEmpty()
    {
        Long[] sorted = {};
        Long[] unsorted = {};

        collideTest(sorted, unsorted);
    }

    /**
     * Test colliding, one sorted item, none unsorted.
     */
    @Test
    public final void testCollisionOneItemSortedEmptyUnsorted()
    {
        Long[] sorted = { 1L };
        Long[] unsorted = {};

        collideTest(sorted, unsorted);
    }

    /**
     * Test colliding, one unsorted item, none sorted.
     */
    @Test
    public final void testCollisionOneItemUnsortedEmptySorted()
    {
        Long[] sorted = {};
        Long[] unsorted = { 1L };

        collideTest(sorted, unsorted);
    }

    /**
     * Test colliding same lists, one item.
     */
    @Test
    public final void testCollisionSameList1()
    {
        Long[] sorted = { 1L };
        Long[] unsorted = sorted;

        collideTest(sorted, unsorted);
    }

    /**
     * Test colliding same lists.
     */
    @Test
    public final void testCollisionSameList()
    {
        Long[] sorted = { 5L, 4L, 3L, 2L, 1L };
        Long[] unsorted = sorted;

        collideTest(sorted, unsorted);
    }

    /**
     * Test collision where the item misses in the middle of the list.
     */
    @Test
    public final void testCollisionSingleMiss()
    {
        Long[] sorted = { 5L, 4L, 2L, 1L };
        Long[] unsorted = { 3L };

        collideTest(sorted, unsorted);
    }

    /**
     * Test collision more complex.
     */
    @Test
    public final void testCollisionSparseList()
    {
        final Long[] sorted = { 1000L, 900L, 899L, 898L, 897L, 896L, 895L, 799L, 501L, 500L, 499L, 5L, 4L, 3L, 2L, 1L };
        final Long[] unsorted = { 1L, 1000L, 600L, 502L, 3L, 2L, 2500L, 999L, 899L, 895L, 894L, 900L, 901L };

        collideTest(sorted, unsorted);
    }

    /**
     * Test collision collding 100 sorted items with 10 unsorted items..
     * 
     * @throws FileNotFoundException
     *             if the file is not found.
     */
    @Test
    public final void testCollision100x10() throws FileNotFoundException
    {
        final Long[] sorted = fileToList(ITEMS_100_SORTED_FILE, 100);

        // Method that generates array does it the ascending, switch to descending.
        ArrayUtils.reverse(sorted);

        // Contains some known items in the list.
        final Long[] unsorted = { 508L, 25L, 251L, 413L, 500L, 795L, 1L, 990L, 2L };

        collideTest(sorted, unsorted);
    }

    /**
     * Test collision collding 100 sorted items with 100 unsorted items..
     * 
     * @throws FileNotFoundException
     *             if the file is not found.
     */
    @Test
    public final void testCollision100x100() throws FileNotFoundException
    {
        final Long[] sorted = fileToList(ITEMS_100_SORTED_FILE, 100);

        // Method that generates array does it the ascending, switch to descending.
        ArrayUtils.reverse(sorted);

        final Long[] unsorted = fileToList(ITEMS_100_UNSORTED_FILE, 100);

        collideTest(sorted, unsorted);
    }

    /**
     * Test collision collding 1000 sorted items with 1000 unsorted items..
     * 
     * @throws FileNotFoundException
     *             if list file is not found.
     */
    @Test
    public final void testCollision1000x1000() throws FileNotFoundException
    {
        final Long[] sorted = fileToList(ITEMS_1000_SORTED_FILE, 1000);

        // Method that generates array does it the ascending, switch to descending.
        ArrayUtils.reverse(sorted);

        final Long[] unsorted = fileToList(ITEMS_1000_UNSORTED_FILE, 1000);

        collideTest(sorted, unsorted);
    }

    /**
     * Test collision collding 1000 sorted items with 1000 unsorted items, both are the same list.
     * 
     * @throws FileNotFoundException
     *             if list file is not found.
     */
    @Test
    public final void testCollision1000x1000SameList() throws FileNotFoundException
    {
        final Long[] sorted = fileToList(ITEMS_1000_SORTED_FILE, 1000);

        // Method that generates array does it the ascending, switch to descending.
        ArrayUtils.reverse(sorted);

        Long[] unsorted = sorted;

        collideTest(sorted, unsorted);

    }

    /**
     * Read an array of longs from a file.
     * 
     * @param file
     *            the path to the file.
     * @param expectedSize
     *            the expected size of the list.
     * @return the array of longs.
     * @throws FileNotFoundException
     *             thrown if file can't be found.
     */
    private static Long[] fileToList(final String file, final int expectedSize) throws FileNotFoundException
    {
        Scanner scanFile = new Scanner(new File(file));

        List<Long> list = new ArrayList<Long>();
        Scanner s = new Scanner(scanFile.nextLine());

        while (s.hasNextLong())
        {
            list.add(s.nextLong());
        }

        Long[] arr = new Long[list.size()];

        list.toArray(arr);

        Assert.assertEquals(expectedSize, arr.length);

        return arr;
    }

    /**
     * Helpder method, tests against apache commons intersection.
     * 
     * @param sorted
     *            the sorted list.
     * @param unsorted
     *            the unsorted list.
     */
    @SuppressWarnings("unchecked")
    private static void collideTest(final Long[] sorted, final Long[] unsorted)
    {
        List<Long> sortedList = Arrays.asList(sorted);
        List<Long> unsortedList = Arrays.asList(unsorted);

        Collection<Long> expected = CollectionUtils.intersection(sortedList, unsortedList);

        List<Long> actual = collider.collide(sortedList, unsortedList);

        Assert.assertEquals(expected.size(), actual.size());

        for (Long expectedItem : expected)
        {
            Assert.assertTrue(actual.contains(expectedItem));
        }
    }
}