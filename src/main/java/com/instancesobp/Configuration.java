/*
 * Copyright (c) 2025 Sergio Gil Borrás
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to use
 * the Software for non-commercial research purposes only, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.instancesobp;

/**
 * This class contains the configuration options for this project.
 *
 * @author Sergio Gil Borrás
 * @version 1.0
 * 
 */
public class Configuration {

    /**
     * Private constructor to prevent instantiation.
     */
    private Configuration() {
        throw new UnsupportedOperationException("This is a configuration class and cannot be instantiated");
    }

    /**
     * This is the seed used to generate the random numbers for the algorithm.
     * It is used to ensure that the results are reproducible.
     * The value is set to 50, but it can be changed to any other integer value.
     */
    public static final int SEED = 50;
}
